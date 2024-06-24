package care.smith.top.terminology.versioning;

import java.util.List;

/**
 * Runs the transition analysis on one release of a terminology.
 * @author Ralph Schäfermeier
 */
public abstract class AbstractTerminologyVersionTransitionAnalyser {
  
  private Properties properties;
  
  public AbstractTerminologyVersionTransitionAnalyser(Properties properties) throws IllegalArgumentException {
    var inputDirectory = properties.getDirectory();
    if (inputDirectory == null || !inputDirectory.exists() || !inputDirectory.isDirectory()) {
      throw new IllegalArgumentException("Input directory does not exist or is not a directory");
    }
    this.properties = properties;
  }
  
  protected Properties getProperties() {
    return properties;
  }
  
  public abstract List<CodeChanges> getAdditions();
  public abstract List<CodeChanges> getDeletions();
  public abstract List<CodeChanges> getMerges();
  public abstract List<CodeChanges> getSplits();
  public abstract List<CodeChanges> getReplacements();
  public abstract List<CodeChanges> getLabelAdditions();
  public abstract List<CodeChanges> getLabelDeletions();
  public abstract List<CodeChanges> getRelabelings();
}
