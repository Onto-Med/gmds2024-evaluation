package care.smith.top.terminology.versioning;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Ralph Schäfermeier
 */
public class Main {
  
  @Parameter(converter = FileConverter.class)
  File inputDirectory;
  
  public static void main(String[] args) {
    Main main = new Main();
    JCommander.newBuilder()
            .addObject(main)
            .build()
            .parse(args);
    main.run();
  }
  
  private void run() {
    try {
      
      new AnalysisBatchRunner(inputDirectory);
      
    } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}