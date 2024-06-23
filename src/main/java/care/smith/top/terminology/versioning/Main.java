package care.smith.top.terminology.versioning;

import com.beust.jcommander.*;
import com.beust.jcommander.converters.FileConverter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @author Ralph Schäfermeier
 */
public class Main {
  
  private static final String PROPERTY_FILE_NAME = "properties.csv";
  
  @Parameter(
          description = "The path to the root folder containing one or several subfolders, each of which, in turn, contains change information about a particular terminology release. The root folder is also expected to contain a properties file in cvs format, providing information about the folder structure and the file format for each release.",
          converter = FileConverter.class,
          validateWith = SourceDirectoryValidator.class,
          required = true,
          help = true)
  private File inputDirectory;
  
  public static class SourceDirectoryValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
      File sourceDirectory = new File(value);
      if (!sourceDirectory.exists())
        throw new ParameterException("Source directory " + sourceDirectory.getAbsolutePath() + " does not exist");
      if (!sourceDirectory.isDirectory())
        throw new ParameterException("Source directory " + sourceDirectory.getAbsolutePath() + " is not a directory");
      if (Objects.requireNonNull(sourceDirectory.listFiles(file -> file.isFile() && file.getName().equals(PROPERTY_FILE_NAME))).length == 0) {
        throw new ParameterException("Source directory " + sourceDirectory.getAbsolutePath() + " must contain a " + PROPERTY_FILE_NAME + " file");
      }
    }
  }
  
  public static void main(String[] args) {
    Main main = new Main();
    try {
      JCommander.newBuilder()
              .addObject(main)
              .build()
              .parse(args);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
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