package care.smith.top.terminology.versioning;

import org.apache.commons.collections4.SetUtils;

import java.util.HashSet;

/**
 * Represents a set of atomic code changes from the perspective of a single code in a new
 * code system version.
 *
 * From set of atomic changes one can then calculate a complex change.
 *
 * Furthermore, a code can be subject to lexical and semantic changes, both types are
 * treated separately here.
 *
 * If this code appears in the old version and not in the new version, then the final
 * change type is Deletion.
 *
 * @author Ralph Schäfermeier
 */
public class CodeChanges {
  private String code;
  private HashSet<String> labelsOld = new HashSet<>();
  private HashSet<String> labelsNew = new HashSet<>();
  private HashSet<String> mappingsOld = new HashSet<>();
  private HashSet<String> mappingsNew = new HashSet<>();
  
  public enum LexicalChange {
    labelAddition,
    labelDeletion,
    labelReplacement,
    none
  }
  
  public enum SemanticChange {
    addition,
    deletion,
    replacement,
    split,
    merge,
    none
  }
  
  public CodeChanges(String code) {
    this.code = code;
  }
  
  public SemanticChange getSemanticChange() {
    if (mappingsNew.equals(mappingsOld)) {
      if (mappingsNew.size() != 1)
        throw new RuntimeException("Number of mappings expected to be 1 but was " + mappingsNew.size() + ": " + code);
      return SemanticChange.none;
    }
    
    if (mappingsOld.size() == 1 && mappingsNew.size() == 0) {
      if (mappingsOld.contains(code)) {
        return SemanticChange.deletion;
      }
      throw new RuntimeException("Change set must contain this code but does not. " + code);
    }
    if (mappingsOld.size() == 0 && mappingsNew.size() == 1) {
      if (mappingsNew.contains(code)) {
        return SemanticChange.addition;
      }
      throw new RuntimeException("Change set must contain this code but does not. " + code);
    }
    
    if (mappingsOld.size() == 1 && mappingsNew.size() == 1) {
      if (mappingsNew.contains(code)) {
        return SemanticChange.replacement; // only return replacement for new code, otherwise it would be counted twice
      }
    }
    
    if (mappingsOld.size() == 1 && mappingsNew.size() > 1) {
      return SemanticChange.split;
    }
    
    if (mappingsOld.size() > 1 && mappingsNew.size() == 1) {
      return SemanticChange.merge;
    }
    
    System.out.println(mappingsOld);
    System.out.println(mappingsNew);
    throw new RuntimeException("This should not happen. Look at the above sets and figure out what went wrong.");
  }
  
  public boolean containsOldCode(String code) {
    return mappingsOld.contains(code);
  }
  
  public boolean containsNewCode(String code) {
    return mappingsNew.contains(code);
  }
  
  public LexicalChange getLexicalChange() {
    if (labelsNew.equals(labelsOld)) {
      return LexicalChange.none;
    }
    
    var addedLabels = SetUtils.difference(labelsNew, labelsOld);
    var deletedLabels = SetUtils.difference(labelsOld, labelsNew);
    
    if (addedLabels.isEmpty() && !deletedLabels.isEmpty()) {
      return LexicalChange.labelDeletion;
    }
    if (!addedLabels.isEmpty() && deletedLabels.isEmpty()) {
      return LexicalChange.labelAddition;
    }
    
    return LexicalChange.labelReplacement;
  }
  
  public String getCode() {
    return code;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  
  public void addOldCode(String codeOld) {
    mappingsOld.add(codeOld);
  }
  
  public void addNewCode(String codeNew) {
    mappingsNew.add(codeNew);
  }
  
  public void addOldLabel(String labelOld) {
    labelsOld.add(labelOld);
  }
  
  public void addNewLabel(String labelNew) {
    labelsNew.add(labelNew);
  }
  
  public HashSet<String> getOldCodes() {
    return mappingsOld;
  }
  
  public HashSet<String> getNewCodes() {
    return mappingsNew;
  }
}
