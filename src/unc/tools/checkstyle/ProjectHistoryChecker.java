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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
  public static final String HISTORY_PREFIX = "history";
  static File oldestVersion;
  static File newestVersion;
  static File history;
  static File[] versions;
  public static final String FILES_CHANGED = "filesChanged.txt";
  public static final String FILE_NAME_SEPARATOR = ":";
  public static final String OUT_FILE_SUFFIX = "check_results.txt";
  public static final String SYMBOL_TABLE_FILE_SUFFIX = "symbol_table.csv";
  public static final String FILE_CHANGE_DETAILS = "filesInsertedDeletedChnaged.csv";
  public static final String VERSION_CONTENTS = "commit_changes";
  public static final String VERSION_FILE_SEPARATOR = ",";
  public static final String CLASS_FILE_SEPARATOR = ",";
  public static final Set<String> filesSeen = new HashSet();
  protected static Map<String, Set<String>> classNameToFileAndVersion = new HashMap();
  protected static Map<String, Set<String>> versionToClassAndFilesDeleted = new HashMap();
  protected static Map<String, Set<String>> versionToClassAndFilesAdded = new HashMap();
  protected static Map<String, Set<String>> versionToClassAndFilesChanged = new HashMap();
  protected static Map<String, String> fileNameToClassName = new HashMap();
  public static final String CLASS_NAME_TO_FILE_AND_VERSION = "classNameToFileAndVersion.csv";
  public static final String VERSION_TO_CLASS_AND_FILES_DELETED= "versionToClassAndFilesDeleted.csv";
  public static final String VERSION_TO_CLASS_AND_FILES_ADDED= "versionToClassAndFilesAdded.csv";
  public static final String VERSION_TO_CLASS_AND_FILES_CHANGED= "versionToClassAndFilesChanged.csv";
  
  public static void printAggregateFiles() {
    printClassNameToFileAndVersion();
    printVersionToClassAndFilesAdded();
    printVersionToClassAndFilesDeleted();
    printVersionToClassAndFilesChanged();
    
    
  }
  
  public static void printClassNameToFileAndVersion() {
    File aFile = new File (root.getAbsolutePath() + "/" + CLASS_NAME_TO_FILE_AND_VERSION  );
    String aHeader = "Class,Version,File";
    printToFile(classNameToFileAndVersion, aFile, aHeader);
  }
  public static void printVersionToClassAndFilesDeleted() {
    File aFile = new File (root.getAbsolutePath() + "/" + VERSION_TO_CLASS_AND_FILES_DELETED  );
    String aHeader = "Version,Class Deleted,File Deleted";

    printToFile(versionToClassAndFilesChanged, aFile, aHeader);
  }
  public static void printVersionToClassAndFilesAdded() {
    File aFile = new File (root.getAbsolutePath() + "/" + VERSION_TO_CLASS_AND_FILES_ADDED  );
    String aHeader = "Version,Class Added,File Added";

    printToFile(versionToClassAndFilesAdded, aFile, aHeader);
  }
  public static void printVersionToClassAndFilesChanged() {
    File aFile = new File (root.getAbsolutePath() + "/" + VERSION_TO_CLASS_AND_FILES_CHANGED  );
    String aHeader = "Version,Class Changed,File Changed";

    printToFile(versionToClassAndFilesChanged, aFile, aHeader);
  }
  public static void printToFile(Map<String, Set<String>> aMap, File aFile, String aHeader) {
    try {
      PrintStream aPrintStream = new PrintStream(aFile);
      aPrintStream.println(aHeader);
      for (String aKey:aMap.keySet()) {
        for (String anElement:aMap.get(aKey)) {
          aPrintStream.println(aKey + "," + anElement);
        }
       
      }
      aPrintStream.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }




  


  
  static File root;
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("No args");
      return;
    }
    String aSource = args[args.length - 1];
//    File aRoot = new File(aSource);
     root = new File(aSource);

    if (!root.exists()) {
      System.err.println("No file:" + aSource);
      return;
    }
    if (!root.isDirectory()) {
      System.err.println("File:" + aSource + " not a directory");
      return;
    }
    findRootSubdirectories(args, root);
    findHistoryVersions();
    runChecks(args);
    printAggregateFiles();
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
    for (int aVersionIndex = 0; aVersionIndex < versions.length; aVersionIndex++) {
      File aVersion = versions[aVersionIndex];
      System.err.println (" Processing version:" + aVersion.getName());
//      if (aVersion.getName().startsWith("1018")) {
//        System.err.println("Found offending version");
//      }

      String anOutFileName = outFileName(aVersion);
      File anOutFile = new File(anOutFileName);
      try {
        anOutFile.createNewFile();

        System.setOut(new PrintStream(anOutFile));
        File aVersionContents = new File (aVersion.getAbsoluteFile() + "/" + VERSION_CONTENTS);
        if (!aVersionContents.exists()) {
          System.err.println(aVersionContents + " does not exist");
          System.exit(0);
          
        }
        

        args[args.length - 1] = aVersionContents.getAbsolutePath();
//        STBuilderCheck.setFirstPass(true);
        ProjectDirectoryHolder.setCurrentProjectDirectory(aVersionContents.getAbsolutePath());
        PostProcessingMain.main(args);
        processVersion(aVersion, aVersionContents);
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
      } else if (history == null && (aFileName.startsWith(HISTORY_PREFIX) )) {
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

//  static Set<String> filesTouchedByThisVersion = new HashSet();
//  static Set<String> filesChagedByThisVersion = new HashSet();
  static Set<String> filesAddedByThisVersion = new HashSet();
  
  static Set<String> filesDeletedByThisVersion = new HashSet();
  static Set<String> filesModifiedByThisVersion = new HashSet();
  static Set<String> classesSeenByThisVersion = new HashSet();


  static Set<String> filesSeenByAllVersions = new HashSet();
  static Set<String> classesSeenByAllVersions = new HashSet();

  static String toString (Set<String> aSet) {
    return aSet.toString();
  }
  static void printFileChangeDetails(File aVersion) {
    
    String[] aFiles = findFilesChanged(aVersion);
//    filesDeletedByThisVersion.addAll(Arrays.asList(aFiles));
//    filesDeletedByThisVersion.removeAll(filesModifiedByThisVersion);
//    filesDeletedByThisVersion.removeAll(filesAddedByThisVersion);
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
  public static Set<String> lookupSet (Map<String, Set<String>> aMap, String aKey) {
    Set<String> aRetVal = aMap.get(aKey);
    if (aRetVal == null) {
      aRetVal = new HashSet();
      aMap.put(aKey, aRetVal);
    }
    return aRetVal;
  }
  public static void addToSet(Map<String, Set<String>> aMap, String aKey, String aValue) {
    Set<String> aSet = lookupSet(aMap, aKey);
    
     aSet.add(aValue);
   
  }
  
  static void processVersion(File aVersion, File aVersionContents) {
    SymbolTable aSymbolTable = SymbolTableFactory.getLastSymbolTable();
    if (aSymbolTable == null) {
      return;
    }
//    filesTouchedByThisVersion.clear();
    filesModifiedByThisVersion.clear();
    filesAddedByThisVersion.clear();
    filesDeletedByThisVersion.clear();
    classesSeenByThisVersion.clear();
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
        String aFileName = toNormalizeFileName(aVersionContents, anSTType.getFileName());
        String aVersionName = aVersion.getName();
        String aVersionAndFileName = aVersionName+ VERSION_FILE_SEPARATOR + aFileName;
        String aClassName = anSTType.getName();
        String aClassAndFileName = aClassName + CLASS_FILE_SEPARATOR + aFileName;
//        maybeAddToSet(classNameToFileAndVersion, aClassName, aFileAndVersionName);
        classesSeenByThisVersion.add(aClassName);

        if (!classesSeenByAllVersions.contains(aClassName)) {
          classesSeenByAllVersions.add(aClassName);
//          addToSet(versionToClassAndFilesAdded, aVersionName, aClassAndFileName);
          addToSet(classNameToFileAndVersion, aClassName, aVersionAndFileName);

//          versionToClassAndFilesAdded.put(aVersion, aClassAndFileName);
        } else {
          addToSet(versionToClassAndFilesChanged, aVersionName, aClassAndFileName);
          if (!aClassName.equals(fileNameToClassName.get(aFileName))) {
            addToSet(classNameToFileAndVersion, aClassName, aVersionAndFileName);
          }
        }
        fileNameToClassName.put(aFileName, aClassName); 
        
        
        
        if (filesSeenByAllVersions.contains(aFileName)) {
          filesModifiedByThisVersion.add(aFileName);
        } else {
          filesAddedByThisVersion.add(aFileName);
          filesSeenByAllVersions.add(aFileName);
          addToSet(versionToClassAndFilesAdded, aVersionName, aClassAndFileName);

        }
//        filesTouchedByThisVersion.add(aFileName);
//        filesSeenByAllVersions.add(aFileName);
        output.println(aFileName + "," + anSTType.getNumberOfDeclaredMethods() + ","
                        + anSTType.getNumberOfDeclaredVariables() + "," + anSTType.getSuperClass()
                        + ","
                        + ComprehensiveVisitCheck.toString(anSTType.getDeclaredInterfaces()));

      }
      String[] aFiles = findFilesChanged(aVersion);

      filesDeletedByThisVersion.addAll(Arrays.asList(aFiles));
      filesDeletedByThisVersion.removeAll(filesModifiedByThisVersion);
      filesDeletedByThisVersion.removeAll(filesAddedByThisVersion);
      String aVersionName = aVersion.getName();
      for (String aFileName:filesDeletedByThisVersion) {
        String aClassName = fileNameToClassName.get(aFileName);
        if (aClassName == null) {
          System.err.println("No class for deleted file" + aFileName);
          continue;
        }
        String aClassAndFileName = aClassName + CLASS_FILE_SEPARATOR + aFileName;
        addToSet(versionToClassAndFilesDeleted, aVersionName, aClassAndFileName);
        if (!classesSeenByThisVersion.contains(aClassName)) {
          addToSet( classNameToFileAndVersion, aClassName, aVersionName + VERSION_FILE_SEPARATOR + "");
        }

      }
      output.close();
//      printFileChangeDetails(aVersion);
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
    File aFilesChangedFile = new File(aVersion.getAbsolutePath() + "/" + aVersion.getName() + "_" + FILES_CHANGED);
    if (!aFilesChangedFile.exists()) {
      System.err.println(aFilesChangedFile + " does not exist");
      System.exit(-1);
    }
    try {
      Scanner aScanner = new Scanner(aFilesChangedFile);
//      aScanner.nextLine();
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
