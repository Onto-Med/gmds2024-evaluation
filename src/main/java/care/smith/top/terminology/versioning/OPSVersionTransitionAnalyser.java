package care.smith.top.terminology.versioning;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ralph Schäfermeier
 */
public class OPSVersionTransitionAnalyser extends AbstractTerminologyVersionTransitionAnalyser {
  
  private List<String> codesOld = new ArrayList<>();
  private List<String> codesNew = new ArrayList<>();
  
  private List<CodeLabels> codeLabelsOld = new ArrayList<>();
  private List<CodeLabels> codesLabelsNew = new ArrayList<>();
  
  public OPSVersionTransitionAnalyser(Properties properties) throws IllegalArgumentException {
    super(properties);
    buildTransitions();
  }
  
  private void buildTransitions() {
    System.out.println(getProperties().getColumnPositions());
    Arrays.stream(getProperties().getDirectory().list()).forEach(System.out::println) ;
    System.out.println();
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
