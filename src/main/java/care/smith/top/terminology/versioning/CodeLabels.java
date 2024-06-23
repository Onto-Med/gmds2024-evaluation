package care.smith.top.terminology.versioning;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Ralph Schäfermeier
 */
public class CodeLabels {
  private String code;
  private TreeSet<String> labels;
  
  public CodeLabels(String code) {
    this.code = code;
    this.labels = new TreeSet<>();
  }
  
  public String getCode() {
    return code;
  }
  
  public void addLabel(String label) {
    labels.add(label);
  }
  
  public Set<String> getLabels() {
    return labels;
  }
}
