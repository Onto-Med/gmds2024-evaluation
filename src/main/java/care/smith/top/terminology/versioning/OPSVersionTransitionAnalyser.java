package care.smith.top.terminology.versioning;

import care.smith.top.terminology.versioning.util.VersionInfoFileNotFoundException;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Ralph Schäfermeier
 */
@SuppressWarnings("unused")
public class OPSVersionTransitionAnalyser extends AbstractTerminologyVersionTransitionAnalyser {
  
  // This class is instantiated via reflection, so ignore the unused warning.
  
  private File fileOld;
  private File fileNew;
  private File fileTransitions;
  
  private final HashMap<String, String> labelsOld = new HashMap<>();
  private final HashMap<String, String> labelsNew = new HashMap<>();
  
  private String undefinedCode; // changes from version to version, we need to detect it
  
  private final HashMap<String, CodeChanges> changesByOldCode = new HashMap<>();
  private final HashMap<String, CodeChanges> changesByNewCode = new HashMap<>();
  
  public OPSVersionTransitionAnalyser(Properties properties) throws IllegalArgumentException, VersionInfoFileNotFoundException, IOException {
    super(properties);
    detectFiles(getProperties().getDirectory());
    readLabels(fileOld, labelsOld);
    readLabels(fileNew, labelsNew);
    
    buildTransitions();
  }
  
  private void buildTransitions() throws IOException {
    final int columnOld = getProperties().getColumnPositions().get(Column.CODE_OLD);
    final int columnNew = getProperties().getColumnPositions().get(Column.CODE_NEW);
    
    new CSVParserBuilder().withSeparator(getProperties().getSeparator()).build();
    new CSVReaderBuilder(new FileReader(fileTransitions, getProperties().getEncoding())).withCSVParser(
            new CSVParserBuilder().withSeparator(getProperties().getSeparator()).build()
    ).build().iterator().forEachRemaining(cells -> {
      
      String oldCode = cells[columnNew];
      String newCode = cells[columnOld];
      
      if (oldCode.equals(undefinedCode)) {
        // this code is new
        // the newCode must be different from undefinedCode
        var changes = getChanges(newCode, changesByNewCode);
        changes.addNewCode(newCode);
      } else if (newCode.equals(undefinedCode)) {
        // the code has been deleted
        var changes = getChanges(oldCode, changesByOldCode);
        changes.addOldCode(oldCode);
      } else {
        // both codes are there
        String oldLabel = labelsOld.get(oldCode);
        String newLabel = labelsNew.get(newCode);
        
        var oldChanges = changesByOldCode.get(oldCode);
        var newChanges = changesByNewCode.get(newCode);
        
        if (oldChanges == null && newChanges == null) {
          // We encounter both for the first time.
          // Might be a simple 1:1 replacement.
          // However, we might encounter one of the codes
          // (old or new) later again.
          // If we encounter the old one again with
          // a different new code, then it's a split.
          // If we encounter the new one again with
          // a different old code, then it's a merge.
          // Other combinations are theoretically
          // possible but would be the result of an
          // error in the change information by the
          // publisher of the code system.
          
          var changes = new CodeChanges(newCode); // if it's a split, we will rename it to the old code later
          changes.addOldCode(oldCode);
          changes.addOldLabel(oldLabel);
          changes.addNewCode(newCode);
          changes.addNewLabel(newLabel);
          
          changesByOldCode.put(oldCode, changes);
          changesByNewCode.put(newCode, changes);
        } else if (oldChanges != null && newChanges == null) {
          // old code has been encountered before. It's a split
          // we keep the old code (source of the split) as the reference
          // as we only want to count the split as one change operation
          
          var newCodes = oldChanges.getNewCodes();
          if (newCodes.size() == 1) {
            // this is the second time we encounter the old code
            // we know now it's not a replacement but a split
            // we can remove the changes from the new codes
            // from then on, we do not have to care about it
            // anymore
            String oc = newCodes.stream().findFirst().get();
            changesByNewCode.remove(oc);
            
            // Also, we rename the change set as we now know
            // it's a split. The reference is the old code.
            oldChanges.setCode(oldCode);
          }
          
          oldChanges.addNewCode(newCode);
          oldChanges.addNewLabel(newLabel);
          
        } else if (oldChanges == null && newChanges != null) {
          // new code has been encountered before. It's a merge
          // we keep the new code (target of the merge) as the reference
          // as we only want to count the merge as one change operation
          
          var oldCodes = newChanges.getOldCodes();
          if (oldCodes.size() == 1) {
            // this is the second time we encounter the new code
            // we know now it's not a replacement but a merge
            // we can remove the changes from the old codes
            // from then on, we do not have to care about it
            // anymore
            String oc = oldCodes.stream().findFirst().get();
            changesByOldCode.remove(oc);
          }
          
          newChanges.addOldCode(oldCode);
          newChanges.addOldLabel(oldLabel);
        } else {
          // this should not happen and would be sign of an error in the
          // change information by the publisher of the code system
          System.err.println("Warning: Weird mapping. " + oldCode + " -> " + newCode);
        }
      }
    });
  }
  
  private CodeChanges getChanges(String code, HashMap<String, CodeChanges> changes) {
    var result = changes.get(code);
    if (result == null) {
      result = new CodeChanges(code);
      changes.put(code, result);
    }
    return result;
  }
  
  private void readLabels(File file, HashMap<String, String> map) throws IOException {
    new CSVParserBuilder().withSeparator(getProperties().getSeparator()).build();
    new CSVReaderBuilder(new FileReader(file, getProperties().getEncoding())).withCSVParser(
            new CSVParserBuilder().withSeparator(getProperties().getSeparator()).build()
    ).build().iterator().forEachRemaining(cells -> {
      String code = cells[0];
      String label = cells[1];

      if (code.equalsIgnoreCase("none") || code.equalsIgnoreCase("undef")) {
        undefinedCode = code;
      }
      
      map.put(code, label);
    });
  }
  
  private void detectFiles(File folder) throws VersionInfoFileNotFoundException {
    System.out.format("%nScanning directory %s for files...%n", getProperties().getDirectory());

    Arrays.stream(Objects.requireNonNull(folder.listFiles(file -> !file.getName().toLowerCase().contains("liesmich")))).forEach(file -> {
      if (file.getName().toLowerCase().contains("umsteiger")) {
        fileTransitions = file;
      } else {
        if (fileOld == null && fileNew == null && getYear(file) != -1) {
          fileOld = file;
          fileNew = file;
        } else {
          // either both fileOld and fileNew are null or both have a value, because we set both at the same time
          int year = getYear(file);
          if (year != -1) {
            if (year < getYear(fileOld)) {
              fileOld = file;
            } else {
              fileNew = file;
            }
          }
        }
      }
    });
    
    checkFiles();
  }
  
  private static final Pattern yearPattern = Pattern.compile("(\\d{4})");
  
  private int getYear(File file) {
    Matcher matcher = yearPattern.matcher(file.getName());
    return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
  }
  
  private void checkFiles() throws VersionInfoFileNotFoundException {
    if (fileOld == null || fileNew == null)
      throw new VersionInfoFileNotFoundException("Could not find version info files.");
    if (fileOld.equals(fileNew))
      throw new VersionInfoFileNotFoundException(String.format("Could only find one version info file but expected two: %s.", fileOld.getAbsolutePath()));
    
    System.out.printf("Detected old version: %s%n", fileOld.getName());
    System.out.printf("Detected new version: %s%n", fileNew.getName());
    
    if (fileTransitions == null)
      throw new VersionInfoFileNotFoundException("Could not find transition info file.");
    
    System.out.printf("Detected transition information: %s%n", fileTransitions.getName());
  }
  
  private Collection<CodeChanges> getAllChanges() {
    return CollectionUtils.union(changesByOldCode.values(), changesByNewCode.values());
  }
  
  @Override
  public List<CodeChanges> getAdditions() {
    return getAllChanges().stream().filter(change -> change.getSemanticChange() == CodeChanges.SemanticChange.addition).collect(Collectors.toList());
  }
  
  @Override
  public List<CodeChanges> getDeletions() {
    return getAllChanges().stream().filter(change -> change.getSemanticChange() == CodeChanges.SemanticChange.deletion).collect(Collectors.toList());
  }
  
  @Override
  public List<CodeChanges> getMerges() {
    return getAllChanges().stream().filter(change -> change.getSemanticChange() == CodeChanges.SemanticChange.merge).collect(Collectors.toList());
  }
  
  @Override
  public List<CodeChanges> getSplits() {
    return getAllChanges().stream().filter(change -> change.getSemanticChange() == CodeChanges.SemanticChange.split).collect(Collectors.toList());
  }
  
  @Override
  public List<CodeChanges> getReplacements() {
    return getAllChanges().stream().filter(change -> change.getSemanticChange() == CodeChanges.SemanticChange.replacement).collect(Collectors.toList());
  }
  
  @Override
  public List<CodeChanges> getRelabelings() {
    return getAllChanges().stream().filter(change -> change.getLexicalChange() == CodeChanges.LexicalChange.labelReplacement).collect(Collectors.toList());
  }
  
  @Override
  public List<CodeChanges> getLabelAdditions() {
    return getAllChanges().stream().filter(change -> change.getLexicalChange() == CodeChanges.LexicalChange.labelAddition).collect(Collectors.toList());
  }
  
  @Override
  public List<CodeChanges> getLabelDeletions() {
    return getAllChanges().stream().filter(change -> change.getLexicalChange() == CodeChanges.LexicalChange.labelDeletion).collect(Collectors.toList());
  }
}
