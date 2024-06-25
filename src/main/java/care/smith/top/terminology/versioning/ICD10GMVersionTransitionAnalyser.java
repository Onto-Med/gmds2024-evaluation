package care.smith.top.terminology.versioning;

import care.smith.top.terminology.versioning.util.VersionInfoFileNotFoundException;

import java.io.IOException;

/**
 * @author Ralph Schäfermeier
 */
public class ICD10GMVersionTransitionAnalyser extends OPSVersionTransitionAnalyser {
  public ICD10GMVersionTransitionAnalyser(Properties properties) throws IllegalArgumentException, VersionInfoFileNotFoundException, IOException {
    super(properties);
  }
}
