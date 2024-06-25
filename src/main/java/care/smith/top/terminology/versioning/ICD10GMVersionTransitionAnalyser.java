package care.smith.top.terminology.versioning;

import care.smith.top.terminology.versioning.util.VersionInfoFileNotFoundException;

import java.io.IOException;

/**
 * ICD10GM and OPS basically have the same file format.
 * The structure differs, but so does it between versions of the same terminology,
 * which is taken care of by the properties.csv file.
 * @author Ralph Schäfermeier
 */
public class ICD10GMVersionTransitionAnalyser extends OPSVersionTransitionAnalyser {
  public ICD10GMVersionTransitionAnalyser(Properties properties) throws IllegalArgumentException, VersionInfoFileNotFoundException, IOException {
    super(properties);
  }
}
