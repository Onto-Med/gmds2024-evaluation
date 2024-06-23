package care.smith.top.terminology.versioning;

/**
 * @author Ralph Schäfermeier
 */
public enum Column {
  CODE_NEW("code_new"),
  CODE_OLD("code_old");
  
  private final String code;
  
  Column(String code) {
    this.code = code;
  }
  
  public String getCode() {
    return code;
  }
}
