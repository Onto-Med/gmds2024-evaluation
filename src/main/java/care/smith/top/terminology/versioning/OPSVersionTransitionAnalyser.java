package care.smith.top.terminology.versioning;

import care.smith.top.terminology.versioning.util.VersionInfoFileNotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ralph Schäfermeier
 */
public class OPSVersionTransitionAnalyser extends AbstractTerminologyVersionTransitionAnalyser {
  
  private File fileOld;
  private File fileNew;
  private File fileTransitions;
  
  private List<String> codesOld = new ArrayList<>();
  private List<String> codesNew = new ArrayList<>();
  
  private List<CodeLabels> codeLabelsOld = new ArrayList<>();
  private List<CodeLabels> codesLabelsNew = new ArrayList<>();
  
  public OPSVersionTransitionAnalyser(Properties properties) throws IllegalArgumentException, VersionInfoFileNotFoundException {
    super(properties);
    
    buildTransitions();
    
  }
  
  private void buildTransitions() throws VersionInfoFileNotFoundException {
    System.out.format("Scanning directory %s for files...%n", getProperties().getDirectory());

    detectFiles(getProperties().getDirectory());
    
    if (fileOld == null || fileNew == null)
      throw new VersionInfoFileNotFoundException("Could not find version info files.");
    if (fileOld.equals(fileNew))
      throw new VersionInfoFileNotFoundException(String.format("Could only find one version info file but expected two: %s.", fileOld.getAbsolutePath()));
    
    System.out.printf("Detected old version: %s%n", fileOld.getName());
    System.out.printf("Detected new version: %s%n", fileNew.getName());
    
    if (fileTransitions == null)
      throw new VersionInfoFileNotFoundException("Could not find transition info file.");
    
    System.out.printf("Detected transition information: %s%n", fileTransitions.getName());
    System.out.println();
    
  }
  
  private void detectFiles(File folder) {
    Arrays.stream(folder.listFiles(file -> !file.getName().toLowerCase().contains("liesmich"))).forEach(file -> {
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
  }
  
  private static final Pattern yearPattern = Pattern.compile("(\\d{4})");
  
  private int getYear(File file) {
    Matcher matcher = yearPattern.matcher(file.getName());
    return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
  }
  
  @Override
  public List getAdditions() {
    return List.of();
  }
  
  @Override
  public List getDeletions() {
    return List.of();
  }
  
  @Override
  public List getMerges() {
    return List.of();
  }
  
  @Override
  public List getSplits() {
    return List.of();
  }
  
  @Override
  public List getRenamings() {
    return List.of();
  }
  
  @Override
  public List getLabelAdditions() {
    return List.of();
  }
  
  @Override
  public List getLabelDeletions() {
    return List.of();
  }
  
  
}
