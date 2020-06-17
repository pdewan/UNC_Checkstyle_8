package unc.tools.checkstyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import unc.checks.ComprehensiveVisitCheck;
import unc.checks.STBuilderCheck;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTable;
import unc.symbolTable.SymbolTableFactory;

public class ProjectHistoryChecker {
  public static final String CONFIG_FLAG = "-c";
  public static final String DIRECTORY_FLAG = "-d";
  public static final String NEWEST_PREFIX = "Newest";
  public static final String OLDEST_PREFIX = "Oldest";
  public static final String HISTORY_PREFIX = "History";
  static File oldestVersion;
  static File newestVersion;
  static File history;
  static File[] versions;
  public static final String FILES_CHANGED = "filesChanged.txt";
  public static final String FILE_NAME_SEPARATOR = ":";
  public static final String OUT_FILE_SUFFIX = "check_results.txt";
  public static final String SYMBOL_TABLE_FILE_SUFFIX = "symbol_table.csv";
  public static final String FILE_CHANGE_DETAILS = "fileChangeDetails.csv";
  public static final Set<String> filesSeen = new HashSet();

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("No args");
      return;
    }
    String aSource = args[args.length - 1];
    File aRoot = new File(aSource);
    if (!aRoot.exists()) {
      System.err.println("No file:" + aSource);
      return;
    }
    if (!aRoot.isDirectory()) {
      System.err.println("File:" + aSource + " not a directory");
      return;
    }
    findRootSubdirectories(args, aRoot);
    findHistoryVersions();
    runChecks(args);
    // for (File aVersion:versions) {
    // String[] aFiles = findFilesChanged(aVersion);
    //
    // }

  }

  static Date currentDate = new Date(System.currentTimeMillis());
  static long currentTime = System.currentTimeMillis();

  public static void runChecks(String[] args) {
    PrintStream aOoldOut = System.out;
    SymbolTableFactory.setLinkSymbolTables(true);
    STBuilderCheck.setDoAutoPassChange(false);
    for (int aVersionIndex = versions.length - 1; aVersionIndex >= 0; aVersionIndex--) {
      File aVersion = versions[aVersionIndex];
      String anOutFileName = outFileName(aVersion);
      File anOutFile = new File(anOutFileName);
      try {
        anOutFile.createNewFile();

        System.setOut(new PrintStream(anOutFile));
        args[args.length - 1] = aVersion.getAbsolutePath();
        STBuilderCheck.setFirstPass(true);
        PostProcessingMain.main(args);
        processVersion(aVersion);
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    System.setOut(aOoldOut);
  }

  public static void findRootSubdirectories(String[] args, File aRoot) {
    File[] aFiles = aRoot.listFiles();

    for (File aFile : aFiles) {
      String aFileName = aFile.getName();
      if (newestVersion == null && aFileName.startsWith(NEWEST_PREFIX)) {
        newestVersion = aFile;
      } else if (oldestVersion == null && aFileName.startsWith(OLDEST_PREFIX)) {
        oldestVersion = aFile;
      } else if (history == null && aFileName.startsWith(HISTORY_PREFIX)) {
        history = aFile;
      }
    }

  }

  public static String outFileName(File aVersion) {
    // return aVersion.getAbsolutePath() + "/" + (aVersion.getName() + "_"
    // + currentDate + "_" + OUT_FILE_SUFFIX);
    return aVersion.getAbsolutePath() + "/" + 
    aVersion.getName() +
    // "_" + currentTime +
            "_" + OUT_FILE_SUFFIX;

  }

  public static String symbolTableFileName(File aVersion) {
    // return aVersion.getAbsolutePath() + "/" + (aVersion.getName() + "_"
    // + currentDate + "_" + OUT_FILE_SUFFIX);
    return aVersion.getAbsolutePath() + "/" + 
    aVersion.getName() +
    // "_" + currentTime +
            "_" + SYMBOL_TABLE_FILE_SUFFIX;

  }

  public static String fileChangeDetails(File aVersion) {
    // return aVersion.getAbsolutePath() + "/" + (aVersion.getName() + "_"
    // + currentDate + "_" + OUT_FILE_SUFFIX);
    return aVersion.getAbsolutePath() + "/" + aVersion.getName() +
    // "_" + currentTime +
            "_" + FILE_CHANGE_DETAILS;

  }

  static Set<String> filesTouchedByThisVersion = new HashSet();
  static Set<String> filesChagedByThisVersion = new HashSet();
  static Set<String> filesAddedByThisVersion = new HashSet();
  
  static Set<String> filesDeletedByThisVersion = new HashSet();
  static Set<String> filesModifiedByThisVersion = new HashSet();


  static Set<String> filesSeenByAllVersions = new HashSet();
  static String toString (Set<String> aSet) {
    return aSet.toString();
  }
  static void printFileChangeDetails(File aVersion) {
    
    String[] aFiles = findFilesChanged(aVersion);
    filesDeletedByThisVersion.addAll(Arrays.asList(aFiles));
    filesDeletedByThisVersion.removeAll(filesModifiedByThisVersion);
    filesDeletedByThisVersion.removeAll(filesAddedByThisVersion);
    File aFileChangeDetails = new File(fileChangeDetails(aVersion));

  
    try {
      // aSymbolTableFile.createNewFile();
      PrintStream anOutput = new PrintStream(aFileChangeDetails);
      anOutput.println("Files Added:");
      anOutput.println(toString(filesAddedByThisVersion));
      anOutput.println("Files Modified:");
      anOutput.println(toString(filesModifiedByThisVersion));
      anOutput.println("Files Deleted:");
      anOutput.println(toString(filesDeletedByThisVersion));
      anOutput.close();
      
      
    } catch (Exception e) {
      e.printStackTrace();
  }
  }
  static void processVersion(File aVersion) {
    SymbolTable aSymbolTable = SymbolTableFactory.getLastSymbolTable();
    if (aSymbolTable == null) {
      return;
    }
    filesTouchedByThisVersion.clear();
    filesModifiedByThisVersion.clear();
    filesAddedByThisVersion.clear();
    filesDeletedByThisVersion.clear();
    File aSymbolTableFile = new File(symbolTableFileName(aVersion));
    try {
      // aSymbolTableFile.createNewFile();
      PrintStream output = new PrintStream(aSymbolTableFile);
      Collection<STType> anAllTypes = aSymbolTable.getAllSTTypes();
      output.println("File Name" + "," + "# Methods" + "," + "# Variables" + "," + "Super Class"
              + "," + "Interfaces");
      for (STType anSTType : anAllTypes) {
        if (anSTType.isExternal()) {
          continue;
        }
        String aFileName = toNormalizeFileName(aVersion, anSTType.getFileName());
        if (filesSeenByAllVersions.contains(aFileName)) {
          filesModifiedByThisVersion.add(aFileName);
        } else {
          filesAddedByThisVersion.add(aFileName);
          filesSeenByAllVersions.add(aFileName);
        }
        filesTouchedByThisVersion.add(aFileName);
        filesSeenByAllVersions.add(aFileName);
        output.println(aFileName + "," + anSTType.getNumberOfDeclaredMethods() + ","
                        + anSTType.getNumberOfDeclaredVariables() + "," + anSTType.getSuperClass()
                        + ","
                        + ComprehensiveVisitCheck.toString(anSTType.getDeclaredInterfaces()));

      }
      output.close();
      printFileChangeDetails(aVersion);
//      
//      String[] aFiles = findFilesChanged(aVersion);
//      filesDeletedByThisVersion.addAll(Arrays.asList(aFiles));
//      filesDeletedByThisVersion.removeAll(filesModifiedByThisVersion);
//      filesDeletedByThisVersion.removeAll(filesAddedByThisVersion);

    } catch (IOException e) {

    }
  }

  public static void findHistoryVersions() {
    if (!history.isDirectory()) {
      System.err.println(history.getAbsolutePath() + " is not a directory");
      System.exit(-1);
    }
    versions = history.listFiles();
    Arrays.sort(versions);

  }

  public static String[] findFilesChanged(File aVersion) {
    File aFilesChangedFile = new File(aVersion.getAbsolutePath() + "/" + FILES_CHANGED);
    if (!aFilesChangedFile.exists()) {
      System.err.println(aFilesChangedFile + " does not exist");
      System.exit(-1);
    }
    try {
      Scanner aScanner = new Scanner(aFilesChangedFile);
      aScanner.nextLine();
      String aFilesChangedString = aScanner.nextLine();
      String[] aFilesChanged = aFilesChangedString.split(FILE_NAME_SEPARATOR);
      return aFilesChanged;
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;

  }

  public static String toNormalizeFileName(File aVersion, String anAbsoluteFileName) {
    try {
      // File aFile = new File(anAbsoluteFileName);
      // Path aPath = Paths.get(anAbsoluteFileName);

      String aCanonicalFileName = Paths.get(anAbsoluteFileName).toString().replace("\\", "/");

      String aCanonicalVersionName = Paths.get(aVersion.getCanonicalPath()).toString().replace("\\",
              "/");

      String retVal = aCanonicalFileName.substring(aCanonicalVersionName.length() + 1);
      return retVal;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

}
