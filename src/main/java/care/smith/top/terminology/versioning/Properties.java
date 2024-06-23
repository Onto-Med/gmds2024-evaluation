package care.smith.top.terminology.versioning;

import com.opencsv.CSVIterator;
import com.opencsv.CSVParserBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Corresponds to one line in a properties file for a terminology that has several revisions.
 * One Properties instance corresponds to one release of the terminology in question.
 * Each release has its own properties because the publishers of a terminology change
 * formats and file structures every now and then.
 * @author Ralph Schäfermeier
 */
public class Properties {
  private File directory;
  private Map<Column, Integer> columnPositions;
  
  public Properties(File directory, Map<Column, Integer> columnPositions) {
    this.directory = directory;
    this.columnPositions = columnPositions;
  }
  
  public File getDirectory() {
    return directory;
  }
  
  public Map<Column, Integer> getColumnPositions() {
    return columnPositions;
  }
}
