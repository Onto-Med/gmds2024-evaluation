package care.smith.top.terminology.versioning;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderHeaderAware;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Entry point for a batch run on a set of terminology releases.
 * Takes the root directory (the subfolders of which contain release specific
 * change information) and a property file specifying the subfolders and the column
 * mappings for the csv files containing the change information (because folder
 * structure and file structure change from year to year).
 * @author Ralph Schäfermeier
 */
public class AnalysisBatchRunner {
  
  private final File sourceDirectory;
  
  @SuppressWarnings("unchecked")
  public AnalysisBatchRunner(File sourceDirectory) throws Exception {
    this.sourceDirectory = sourceDirectory;
    
    File resultFile = new File(sourceDirectory, String.format("eval_%s.csv", sourceDirectory.getName()));
    FileWriter out = new FileWriter(resultFile);
    
    var terminologyName = sourceDirectory.getName().toUpperCase(Locale.ROOT);
    
    Class<AbstractTerminologyVersionTransitionAnalyser> analyserClass = (Class<AbstractTerminologyVersionTransitionAnalyser>) Class.forName(String.format("%s.%sVersionTransitionAnalyser", AnalysisBatchRunner.class.getPackageName(), terminologyName));
    
    var csvReader = new CSVReaderHeaderAware(new FileReader(new File(sourceDirectory, "properties.csv")));
    int year = 2004;
    out.write("year, additions, deletions, replacements, splits, merges, label additions, label deletions, relabelings\n");
    
    Map<String, String> row;
    while ((row = csvReader.readMap()) != null) {
      try {
        AbstractTerminologyVersionTransitionAnalyser analyser = analyserClass.getDeclaredConstructor(Properties.class).newInstance(buildProperties(row));
        out.write(year++ + ", ");
        out.write(analyser.getAdditions().size() + ", ");
        out.write(analyser.getDeletions().size() + ", ");
        out.write(analyser.getReplacements().size() + ", ");
        out.write(analyser.getSplits().size() + ", ");
        out.write(analyser.getMerges().size() + ", ");
        out.write(analyser.getLabelAdditions().size() + ", ");
        out.write(analyser.getLabelDeletions().size() + ", ");
        out.write(analyser.getRelabelings().size() + "\n");
      } catch (InvocationTargetException e) {
        throw (Exception)e.getCause();
      }
    }
    out.close();
  }
  
  private Properties buildProperties(Map<String, String> row) throws IOException {
    String path = row.get("path");
    char separator = row.get("separator").charAt(0);
    String encoding = row.get("encoding");
    String columnsCsvRaw = row.get("columns"); // this is the value of a CSV cell, but it's CSV itself
    
    List<String> columnList = Arrays.stream(new CSVParserBuilder().withSeparator(separator).build().parseLine(columnsCsvRaw)).toList();
    HashMap<Column, Integer> columns = new HashMap<>();
    columns.put(Column.CODE_OLD, columnList.indexOf(Column.CODE_OLD.getCode()));
    columns.put(Column.CODE_NEW, columnList.indexOf(Column.CODE_NEW.getCode()));
    
    return new Properties(new File(sourceDirectory, path), separator, Charset.forName(encoding), columns);
  }
}
