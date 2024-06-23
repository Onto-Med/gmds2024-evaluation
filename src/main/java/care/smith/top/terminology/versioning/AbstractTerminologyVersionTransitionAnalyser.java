package care.smith.top.terminology.versioning;

import java.io.File;
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
  
  public abstract List getAdditions();
  public abstract List getDeletions();
  public abstract List getMerges();
  public abstract List getSplits();
  public abstract List getRenamings();
  public abstract List getLabelAdditions();
  public abstract List getLabelDeletions();
  
}
