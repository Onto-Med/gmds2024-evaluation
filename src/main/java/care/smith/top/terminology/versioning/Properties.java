package care.smith.top.terminology.versioning;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Corresponds to one line in a properties file for a terminology that has several revisions.
 * One Properties instance corresponds to one release of the terminology in question.
 * Each release has its own properties because the publishers of a terminology change
 * formats and file structures every now and then.
 * @author Ralph Schäfermeier
 */
public class Properties {
  private final File directory;
  private final char separator;
  private final Charset encoding;
  private final Map<Column, Integer> columnPositions;
  
  public Properties(File directory, char separator, Charset encoding, Map<Column, Integer> columnPositions) {
    this.directory = directory;
    this.separator = separator;
    this.encoding = encoding;
    this.columnPositions = columnPositions;
  }
  
  public File getDirectory() {
    return directory;
  }
  
  public Map<Column, Integer> getColumnPositions() {
    return columnPositions;
  }
  
  public char getSeparator() {
    return separator;
  }
  
  public Charset getEncoding() {
    return encoding;
  }
}
