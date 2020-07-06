package unc.tools.checkstyle.commits;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.beanutils.BeanIntrospector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONML;
import org.json.JSONObject;
import org.json.JSONWriter;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import unc.checks.ComprehensiveVisitCheck;
import unc.checks.STBuilderCheck;
import unc.symbolTable.PropertyInfo;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTable;
import unc.symbolTable.SymbolTableFactory;
import unc.tools.checkstyle.PostProcessingMain;
import unc.tools.checkstyle.ProjectDirectoryHolder;

public class ProjectHistoryChecker {
  public static final String CONFIG_FLAG = "-c";
  public static final String DIRECTORY_FLAG = "-d";
  public static final String NEWEST_PREFIX = "Newest";
  public static final String OLDEST_PREFIX = "Oldest";
  public static final String HISTORY_PREFIX = "history";
  static File oldestVersion;
  static File newestVersion;
  static File history;
  static File[] commits;
  static File[] checkpoints;
  public static final String FILES_CHANGED = "filesChanged.txt";
  public static final String GIT_COMMIT_DATA = "gitCommitData.json";

  public static final String FILE_NAME_SEPARATOR = "\n";
  public static final String OUT_FILE_SUFFIX = "check_results.txt";
  public static final String SYMBOL_TABLE_FILE_SUFFIX = "symbol_table.csv";
  public static final String FILE_CHANGE_DETAILS = "filesInsertedDeletedChnaged.csv";
  public static final String CHECKER_COMMIT_SAVED_STATE = "checkerCommitSavedState.json";

  public static final String CHANGED_FILES_IN_CURRENT_COMMIT = "commit_changes";
  public static final String CHANGED_FILES_IN_PREVIOUS_COMMIT = "prev_version_commit_changes";

  public static final String VERSION_FILE_SEPARATOR = ",";
  public static final String CLASS_FILE_SEPARATOR = ",";
  public static final List<String> filesSeen = new ArrayList();
  // public static final Set<String> filesSeen = new HashSet();

  // protected static Map<String, Set<String>> classNameToFileAndVersion = new HashMap();
  // protected static Map<String, Set<String>> versionToClassAndFilesDeleted = new HashMap();
  // protected static Map<String, Set<String>> versionToClassAndFilesAdded = new HashMap();
  // protected static Map<String, Set<String>> versionToClassAndFilesChanged = new HashMap();

  protected static Map<String, List<String>> classNameToFileAndVersion = new HashMap();
  
//  protected static Map<String, List<String>> versionToClassAndFilesDeleted = new HashMap();
//  protected static Map<String, List<String>> versionToClassAndFilesAdded = new HashMap();
//  protected static Map<String, List<String>> versionToClassAndFilesChanged = new HashMap();
 

//  static List<String> allFilesSeen = new ArrayList();
//  static List<String> allClassesSeen = new ArrayList();
//  static List<String> allFilesModified = new ArrayList();
//
//  static Set<String> filesAddedByThisVersion = new HashSet();
//
//  static Set<String> filesDeletedByThisVersion = new HashSet();
//
//  static Set<String> classesSeenByThisCommit = new HashSet();
//
//  protected static Map<String, String> fileNameToClassName = new HashMap();
//  protected static Map<String, String> ClassNameToFileName = new HashMap();

  public static final String CLASS_NAME_TO_FILE_AND_VERSION = "classNameToFileAndVersion.csv";
  public static final String VERSION_TO_CLASS_AND_FILES_DELETED = "versionToClassAndFilesDeleted.csv";
  public static final String VERSION_TO_CLASS_AND_FILES_ADDED = "versionToClassAndFilesAdded.csv";
  public static final String VERSION_TO_CLASS_AND_FILES_CHANGED = "versionToClassAndFilesChanged.csv";

//  public static void printAggregateFiles() {
//    printClassNameToFileAndVersion();
//    printVersionToClassAndFilesAdded();
//    printVersionToClassAndFilesDeleted();
//    printVersionToClassAndFilesChanged();
//
//  }

//  public static void printClassNameToFileAndVersion() {
//    File aFile = new File(root.getAbsolutePath() + "/" + CLASS_NAME_TO_FILE_AND_VERSION);
//    String aHeader = "Class,Version,File";
//    // printToFile(classNameToFileAndVersion, aFile, aHeader);
//    printJSONToFile(classNameToFileAndVersion, aFile, aHeader);
//
//  }

//  public static void printVersionToClassAndFilesDeleted() {
//    File aFile = new File(root.getAbsolutePath() + "/" + VERSION_TO_CLASS_AND_FILES_DELETED);
//    String aHeader = "Version,Class Deleted,File Deleted";
//
//    // printToFile(versionToClassAndFilesChanged, aFile, aHeader);
//    printJSONToFile(versionToClassAndFilesChanged, aFile, aHeader);
//
//  }

//  public static void printVersionToClassAndFilesAdded() {
//    File aFile = new File(root.getAbsolutePath() + "/" + VERSION_TO_CLASS_AND_FILES_ADDED);
//    String aHeader = "Version,Class Added,File Added";
//
//    // printToFile(versionToClassAndFilesAdded, aFile, aHeader);
//    printJSONToFile(versionToClassAndFilesAdded, aFile, aHeader);
//
//  }

//  public static void printVersionToClassAndFilesChanged() {
//    File aFile = new File(root.getAbsolutePath() + "/" + VERSION_TO_CLASS_AND_FILES_CHANGED);
//    String aHeader = "Version,Class Changed,File Changed";
//
//    // printToFile(versionToClassAndFilesChanged, aFile, aHeader);
//    printJSONToFile(versionToClassAndFilesChanged, aFile, aHeader);
//
//  }

//  public static void saveCheckPoint() {
//    File aFile = new File(root.getAbsolutePath() + "/" + VERSION_TO_CLASS_AND_FILES_CHANGED);
//    String aHeader = "Version,Class Changed,File Changed";
//
//    // printToFile(versionToClassAndFilesChanged, aFile, aHeader);
//    printJSONToFile(versionToClassAndFilesChanged, aFile, aHeader);
//
//  }

  public static void saveCheckeState(File aCommit) {
    CheckerCommitSavedState aCheckerCommitSavedState = new ACheckerCommitSavedState(
            classNameToFileAndVersion
//            , versionToClassAndFilesDeleted, versionToClassAndFilesAdded,
//            versionToClassAndFilesChanged, allFilesSeen, allClassesSeen, allFilesModified
            );
    JSONObject aJSONObject = new JSONObject(aCheckerCommitSavedState);
    String aSavedString = aJSONObject.toString();
    try {
//      File aSavedFile = new File(aCurrentCommit + "/" + CHECKER_COMMIT_SAVED_STATE);
      File aSavedFile = toCheckerStateFile(aCommit);

      PrintStream aPrintStream = new PrintStream(aSavedFile);
      aPrintStream.println(aSavedString);
      aPrintStream.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void toBean(JSONObject jobject, Object object) {
    try {
      BeanInfo aBeanInfo = Introspector.getBeanInfo(object.getClass());

      for (PropertyDescriptor aPropertyDescriptor : aBeanInfo.getPropertyDescriptors()) {
        String aPropertyName = aPropertyDescriptor.getName();
        if (aPropertyName.equals("class")) {
          continue;
        }
        Object aValue = jobject.get(aPropertyName);
        if (aValue instanceof JSONArray) {
          JSONArray aJSOnArray = (JSONArray) aValue;
          aValue = aJSOnArray.toList();
        } else if (aValue instanceof JSONObject) {
          JSONObject aJSONObject = (JSONObject) aValue;
          aValue = aJSONObject.toMap();
        }
        aPropertyDescriptor.getWriteMethod().invoke(object, aValue);

      }
    } catch (IllegalArgumentException | IllegalAccessException | JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IntrospectionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  public static File toCheckerStateFile (File aCommit) {
    String aCurrentCommitName = aCommit.getName();
    return new File(aCommit + "/" + aCurrentCommitName + "_" + CHECKER_COMMIT_SAVED_STATE);
  }
  // Call:
  //
  // User user = (User) toBean(jo, new User());
  public static void loadCheckerState(File aCommit) {

    try {
//      String aCurrentCommitName = aCommit.getName();
//      File aSavedFile = new File(aCommit + "/" + aCurrentCommitName + "_" + CHECKER_COMMIT_SAVED_STATE);
      
      File aSavedFile = toCheckerStateFile(aCommit);
      Scanner aScanner = new Scanner(aSavedFile);
      String aSavedString = aScanner.nextLine();
      JSONObject aJSONObject = new JSONObject(aSavedString);

      PrintStream aPrintStream = new PrintStream(aSavedFile);
      CheckerCommitSavedState aCheckerCommitSavedState = new ACheckerCommitSavedState();
      toBean(aJSONObject, aCheckerCommitSavedState);
      classNameToFileAndVersion = aCheckerCommitSavedState.getClassNameToFileAndCommit();
//      versionToClassAndFilesDeleted = aCheckerCommitSavedState.getCommitToClassAndFilesDeleted();
//      versionToClassAndFilesAdded = aCheckerCommitSavedState.getCommitToClassAndFilesAdded();
//      versionToClassAndFilesChanged = aCheckerCommitSavedState.getCommitToClassAndFilesChanged();
//      allFilesSeen = aCheckerCommitSavedState.getAllFilesSeen();
//      allClassesSeen = aCheckerCommitSavedState.getAllClassesSeen();
//      allFilesModified = aCheckerCommitSavedState.getAllFilesModified();

      aPrintStream.print(aSavedString);
      aPrintStream.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void printToFile(Map<String, Set<String>> aMap, File aFile, String aHeader) {
    try {
      PrintStream aPrintStream = new PrintStream(aFile);
      aPrintStream.println(aHeader);
      for (String aKey : aMap.keySet()) {
        for (String anElement : aMap.get(aKey)) {
          aPrintStream.println(aKey + "," + anElement);
        }

      }
      aPrintStream.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void printJSONToFile(Map<String, List<String>> aMap, File aFile, String aHeader) {
    try {
      PrintStream aPrintStream = new PrintStream(aFile);
      JSONObject aJSONObject = new JSONObject(aMap);
      String aJSONString = aJSONObject.toString();
      aPrintStream.println(aJSONString);
      // aPrintStream.println(aHeader);
      // for (String aKey:aMap.keySet()) {
      // for (String anElement:aMap.get(aKey)) {
      // aPrintStream.println(aKey + "," + anElement);
      // }
      //
      // }
      aPrintStream.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  static StringBuilder stringBuilder = new StringBuilder(10000);

  // public static void readJSONFromFile(Map<String, List<String>> aMap, File aFile, String aHeader)
  // {
  public static void readJSONFromFile(Map aMap, File aFile) {
    try {
      Scanner aScanner = new Scanner(aFile);
      stringBuilder.setLength(0);
      while (aScanner.hasNext()) {
        stringBuilder.append(aScanner.nextLine());
        stringBuilder.append("/n");
      }
      JSONObject aJSONObject = new JSONObject(stringBuilder.toString());
      Set<String> aKeys = aJSONObject.keySet();
      for (String aKey : aKeys) {
        JSONArray aJSONArray = (JSONArray) aJSONObject.get(aKey);
        aMap.put(aKey, aJSONArray.toList());
      }

      aScanner.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  static File root;
  static String currentCheckpointName;
  static String nextCheckpointName;

  static File currentCheckpointFile;
//  static String stoppingCommit;
  static int stoppingCommitIndex;
  static int currentCommitIndex;
  static int currentCheckpointIndex;
  static String currentCheckpointVersion;

  static String previousCheckpointName;
  static File previousCommitDirectory;

  public static File toDirectory(String aString) {
    if (aString.isEmpty()) {
      return null;
    }
    File result = new File(aString);
    if (!result.exists() || !result.isDirectory()) {
      System.err.println("Not a file or directory:" + result);
      return null;

    }
    return result;
  }

  public static int findCommitIndex(String aTargetCommitName) {
    for (int aCommitIndex = 0; aCommitIndex < commits.length; aCommitIndex++) {
      String aCommitName = commits[aCommitIndex].getName();
      if (aCommitName.equals(aTargetCommitName)) {
        return aCommitIndex;
      }
    }
    System.err.println("Could not find commit index");
    System.exit(-1);
    return -1;
  }

  public static void findCurrentVersionAndCommitIndex() {
    String[] aSplit = currentCheckpointName.split("-");
    if (aSplit.length != 3) {
      System.err.println("Malformed checkpoint name");
    }
    currentCheckpointVersion = aSplit[1];
    currentCommitIndex = findCommitIndex(currentCheckpointName);

  }

  public static void findCurrentCheckpointIndex() {
    String[] aSplit = currentCheckpointName.split("-");
    if (aSplit.length != 3) {
      System.err.println("Malformed checkpoint name");
    }

    for (int aCheckpointIndex = 0; aCheckpointIndex < checkpoints.length; aCheckpointIndex++) {
      String aCheckpointName = checkpoints[aCheckpointIndex].getName();
      if (aCheckpointName.equals(currentCheckpointName)) {
        currentCheckpointIndex = aCheckpointIndex;
        break;
      }
    }
  }

  public static void findPreviousCommitDirectory() {
    previousCommitDirectory = null;
    String aMaxPreviousCommitName = "";
    for (int aCommitIndex = currentCommitIndex - 1; aCommitIndex >= 0; aCommitIndex--) {
      String aCommitName = commits[aCommitIndex].getName();
      String aPreviousVersion = getVersion(aCommitName);
      if (aPreviousVersion == null) {
        continue; // some other kind of file;
      }
//      String[] aSplit = aCommitName.split("-");
//      if (aSplit.length != 3) {
//        continue; // some other kind of file;
//      }
//      String aPreviousVersion = aSplit[1];
      int aVersionComparisonWithCurrent = aPreviousVersion.compareTo(currentCheckpointVersion);
      if (aVersionComparisonWithCurrent > 0) {
        continue; // new commit changed a previous version
      } else if (aVersionComparisonWithCurrent == 0) {
        aMaxPreviousCommitName = aCommitName; // same version, so no need to go back
        break;
      }

      if (aCommitName.compareTo(aMaxPreviousCommitName) > 0) {
        aMaxPreviousCommitName = aCommitName; // do not break as there might be a larger version
                                              // later
      }
    }
    if (!aMaxPreviousCommitName.isEmpty()) {
      previousCommitDirectory = toDirectory(
              history.getAbsolutePath() + "/" + aMaxPreviousCommitName);

    }
  }
  public static void findStoppingCommitIndex() {
    stoppingCommitIndex = findStoppingCommitIndex(currentCheckpointIndex);
//    stoppingCommit = "";
//    for (int aCheckpointIndex = currentCheckpointIndex
//            + 1; aCheckpointIndex < checkpoints.length; aCheckpointIndex++) {
//      String aName = checkpoints[aCheckpointIndex].getName();
//      String[] aSplit = aName.split("-");
//      if (aSplit.length != 3) {
//        continue; // some other kind of file;
//      }
//      stoppingCommit = aName;
//      break;
//    }
//    if (stoppingCommit.isEmpty()) {
//      stoppingCommitIndex = commits.length - 1;
//    } else {
//      stoppingCommitIndex = findCommitIndex(stoppingCommit);
//    }
  }

//  public static void findStoppingCommitNameAndCommitIndex() {
//    stoppingCommit = "";
//    for (int aCheckpointIndex = currentCheckpointIndex
//            + 1; aCheckpointIndex < checkpoints.length; aCheckpointIndex++) {
//      String aName = checkpoints[aCheckpointIndex].getName();
//      String[] aSplit = aName.split("-");
//      if (aSplit.length != 3) {
//        continue; // some other kind of file;
//      }
//      stoppingCommit = aName;
//      break;
//    }
//    if (stoppingCommit.isEmpty()) {
//      stoppingCommitIndex = commits.length - 1;
//    } else {
//      stoppingCommitIndex = findCommitIndex(stoppingCommit);
//    }
//  }
  
  public static String getVersion(String aName) {
//    String aName =  aCheckpoint.getName();
    String[] aSplit = aName.split("-");
    if (aSplit.length != 3) {
      return null; // some other kind of file;
    }
    return aSplit[1];
  }
  public static int findStoppingCommitIndex(int aCurrentIndex) {
    String aStoppingCommit = "";
    String aCurrentVersion = getVersion(checkpoints[aCurrentIndex].getName());
    
    int retVal;
    for (int aCheckpointIndex = aCurrentIndex
            + 1; aCheckpointIndex < checkpoints.length; aCheckpointIndex++) {
      String aName = checkpoints[aCheckpointIndex].getName();
      String aVersion = getVersion(aName);
      if (aVersion == null) {
        continue;
      }
//      if (aVersion.compareTo(aCurrentVersion) < 0) {
//        continue; // this should not happen
//      }
//      String[] aSplit = aName.split("-");
//      if (aSplit.length != 3) {
//        continue; // some other kind of file;
//      }
//      if (aName.compareTo(anotherString))
      aStoppingCommit = aName;
      break;
    }
    if (aStoppingCommit.isEmpty()) {
      retVal = commits.length - 1;
    } else {
      retVal = findCommitIndex(aStoppingCommit);
    }
    return retVal;
  }

  public static void processArgs(String[] args) {
    if (args.length < 2) {
      System.err.println("Need at least 2 args");
      return;
    }

    root = toDirectory(args[args.length - 2]);
    currentCheckpointName = args[args.length - 1];

    // previousCheckpoint = toDirectory(root.getAbsolutePath() + "/" + args[args.length - 3]);
    // currentCheckpoint = toDirectory(args[args.length - 2]);
    // nextCheckpoint = toDirectory(args[args.length - 1]);
    // if (currentCheckpoint == null) {
    // System.err.println("Current check point empty");
    // System.exit(-1);
    // }

  }

  public static void main(String[] args) {
    processArgs(args);

    // String aSource = args[args.length - 4 ];
    //// String aSource = args[args.length - 1 ];
    //
    //// File aRoot = new File(aSource);
    // root = new File(aSource);
    //
    // if (!root.exists()) {
    // System.err.println("No file:" + aSource);
    // return;
    // }
    // if (!root.isDirectory()) {
    // System.err.println("File:" + aSource + " not a directory");
    // return;
    // }

    findRootSubdirectories(args, root);
    currentCheckpointFile = toDirectory(root.getAbsolutePath() + "/" + currentCheckpointName);
    findCheckpoints();
    findCommits();
    findCurrentVersionAndCommitIndex();
    findCurrentCheckpointIndex();
    findPreviousCommitDirectory();
    findStoppingCommitIndex();
    runChecks(args);
    // printAggregateFiles();

  }

  static Date currentDate = new Date(System.currentTimeMillis());
  static long currentTime = System.currentTimeMillis();

  protected static boolean redirectOutput = true;

  public static boolean isRedirectOutput() {
    return redirectOutput;
  }

  public static void setRedirecOutput(boolean newVal) {
    redirectOutput = newVal;
  }
  static File changedFilesInCurrentCommit;
  static File changedFilesInPreviousCommit;
  
  public static void runChecks(String[] args) {
    String[] myArgs = new String[args.length - 1];
    for (int i = 0; i < args.length - 1; i++) {
      myArgs[i] = args[i];
    }
    PrintStream anOldOut = System.out;
    SymbolTableFactory.setLinkSymbolTables(true);
    STBuilderCheck.setDoAutoPassChange(false);
    if (previousCommitDirectory != null) {
      loadCheckerState(previousCommitDirectory);
    }
    // let us do stopping commit also so we can compare with previous symbol table
//    for (int aCommitIndex = currentCommitIndex; aCommitIndex < stoppingCommitIndex; aCommitIndex++) {

    for (int aCommitIndex = currentCommitIndex; aCommitIndex <= stoppingCommitIndex; aCommitIndex++) {
      File aCommit = commits[aCommitIndex];
      String aName = aCommit.getName();
      if (getVersion(aName).compareTo(currentCheckpointVersion) < 0) { 
        
        continue;
      }
      System.err.println(" Processing commit:" + aCommit.getName());
      // if (aVersion.getName().startsWith("1018")) {
      // System.err.println("Found offending version");
      // }
      //
      // String anOutFileName = outFileName(aCommit);
      // File anOutFile = new File(anOutFileName);
      try {
        if (isRedirectOutput()) {
          String anOutFileName = outFileName(aCommit);
          File anOutFile = new File(anOutFileName);
          anOutFile.createNewFile();

          System.setOut(new PrintStream(anOutFile));
        }

        changedFilesInCurrentCommit = toDirectory(
                aCommit.getAbsoluteFile() + "/" + CHANGED_FILES_IN_CURRENT_COMMIT);
        changedFilesInPreviousCommit = toDirectory(
                aCommit.getAbsoluteFile() + "/" + CHANGED_FILES_IN_PREVIOUS_COMMIT);

        File aCheckedSource = aCommitIndex == currentCommitIndex ? currentCheckpointFile
                : changedFilesInCurrentCommit;
        myArgs[myArgs.length - 1] = aCheckedSource.getAbsolutePath();
        // STBuilderCheck.setFirstPass(true);
        // ProjectDirectoryHolder.setCurrentProjectDirectory(aChangedFilesInCurrentCommit.getAbsolutePath());
        ProjectDirectoryHolder.setCurrentProjectDirectory(aCheckedSource.getAbsolutePath());

        PostProcessingMain.main(myArgs);
//        processCommit(aCommit, changedFilesInCurrentCommit);
        processCommit(aCommit, aCheckedSource, aCommitIndex);

      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    System.setOut(anOldOut);
  }

  public static void findRootSubdirectories(String[] args, File aRoot) {
    File[] aFiles = aRoot.listFiles();

    for (File aFile : aFiles) {
      String aFileName = aFile.getName();
      if (newestVersion == null && aFileName.startsWith(NEWEST_PREFIX)) {
        newestVersion = aFile;
      } else if (oldestVersion == null && aFileName.startsWith(OLDEST_PREFIX)) {
        oldestVersion = aFile;
      } else if (history == null && (aFileName.startsWith(HISTORY_PREFIX))) {
        history = aFile;
      }
    }

  }

  public static String outFileName(File aVersion) {
    // return aVersion.getAbsolutePath() + "/" + (aVersion.getName() + "_"
    // + currentDate + "_" + OUT_FILE_SUFFIX);
    return aVersion.getAbsolutePath() + "/" + aVersion.getName() +
    // "_" + currentTime +
            "_" + OUT_FILE_SUFFIX;

  }

  public static String symbolTableFileName(File aVersion) {
    // return aVersion.getAbsolutePath() + "/" + (aVersion.getName() + "_"
    // + currentDate + "_" + OUT_FILE_SUFFIX);
    return aVersion.getAbsolutePath() + "/" + aVersion.getName() +
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

  // static Set<String> filesAddedByThisVersion = new HashSet();
  //
  // static Set<String> filesDeletedByThisVersion = new HashSet();
  // static Set<String> filesModifiedByAnyVersion = new HashSet();
  // static Set<String> classesSeenByThisCommit = new HashSet();
  //
  //
  // static Set<String> filesSeenByAllVersions = new HashSet();
  // static Set<String> classesSeenByAnyCommit = new HashSet();

  static String toString(Collection<String> aSet) {
    return aSet.toString();
  }

//  static void printFileChangeDetails(File aVersion) {
//
//    String[] aFiles = findFilesChanged(aVersion);
//    // filesDeletedByThisVersion.addAll(Arrays.asList(aFiles));
//    // filesDeletedByThisVersion.removeAll(filesModifiedByThisVersion);
//    // filesDeletedByThisVersion.removeAll(filesAddedByThisVersion);
//    File aFileChangeDetails = new File(fileChangeDetails(aVersion));
//
//    try {
//      // aSymbolTableFile.createNewFile();
//      PrintStream anOutput = new PrintStream(aFileChangeDetails);
//      anOutput.println("Files Added:");
//      anOutput.println(toString(filesAddedByThisVersion));
//      anOutput.println("Files Modified:");
//      anOutput.println(toString(allFilesModified));
//      anOutput.println("Files Deleted:");
//      anOutput.println(toString(filesDeletedByThisVersion));
//      anOutput.close();
//
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }

  public static Set<String> lookupSet(Map<String, Set<String>> aMap, String aKey) {
    Set<String> aRetVal = aMap.get(aKey);
    if (aRetVal == null) {
      aRetVal = new HashSet();
      aMap.put(aKey, aRetVal);
    }
    return aRetVal;
  }

  public static List<String> lookupList(Map<String, List<String>> aMap, String aKey) {
    List<String> aRetVal = aMap.get(aKey);
    if (aRetVal == null) {
      aRetVal = new ArrayList();
      aMap.put(aKey, aRetVal);
    }
    return aRetVal;
  }

  public static void addToSet(Map<String, Set<String>> aMap, String aKey, String aValue) {
    Set<String> aSet = lookupSet(aMap, aKey);

    aSet.add(aValue);

  }

  public static void addToList(Map<String, List<String>> aMap, String aKey, String aValue) {
    List<String> aSet = lookupList(aMap, aKey);

    if (!aSet.contains(aValue))
      aSet.add(aValue);

  }
  public static String getLastValue(Map<String, List<String>> aMap, String aKey) {
    List<String> aList = aMap.get(aKey);
    if (aList == null || aList.isEmpty()) return null;
    return aList.get(aList.size() - 1); 

  }
  public static String getLastFileName(Map<String, List<String>> aMap, String aKey) {
    String aValue = getLastValue(aMap, aKey);
    return 
            (aValue == null)?null:
             aValue.split(VERSION_FILE_SEPARATOR)[1];
    
  }

  public static void setAdd(Collection aList, Object anElement) {
    if (!aList.contains(anElement)) {
      aList.add(anElement);
    }
  }

//  static void processCommit(File aCommit, File aVersionContents) {
//    SymbolTable aSymbolTable = SymbolTableFactory.getLastSymbolTable();
//    JSONObject aGitCommitData = findGitCommitData(aCommit);
//
//    if (aSymbolTable == null) {
//      return;
//    }
//  
////    filesAddedByThisVersion.clear();
////    filesDeletedByThisVersion.clear();
////    classesSeenByThisCommit.clear();
//    File aSymbolTableFile = new File(symbolTableFileName(aCommit));
//    try {
//      // aSymbolTableFile.createNewFile();
//      PrintStream output = new PrintStream(aSymbolTableFile);
//      Collection<STType> anAllTypes = aSymbolTable.getAllSTTypes();
//      output.println("File Name" + "," + "# Methods" + "," + "# Variables" + "," + "Super Class"
//              + "," + "Interfaces");
//      for (STType anSTType : anAllTypes) {
//        if (anSTType.isExternal()) {
//          continue;
//        }
//        String aFileName = toNormalizeFileName(aVersionContents, anSTType.getFileName());
//        String aCommitName = aCommit.getName();
//        String aVersionAndFileName = aCommitName + VERSION_FILE_SEPARATOR + aFileName;
//        String aClassName = anSTType.getName();
//        String aClassAndFileName = aClassName + CLASS_FILE_SEPARATOR + aFileName;
//        // maybeAddToSet(classNameToFileAndVersion, aClassName, aFileAndVersionName);
//        classesSeenByThisCommit.add(aClassName);
//
//        if (!allClassesSeen.contains(aClassName)) {
//          // allClassesSeen.add(aClassName);
//          setAdd(allClassesSeen, aClassName);
//
//          // addToSet(classNameToFileAndVersion, aClassName, aVersionAndFileName);
//          addToList(classNameToFileAndVersion, aClassName, aVersionAndFileName);
//
//          // versionToClassAndFilesAdded.put(aVersion, aClassAndFileName);
//        } else {
//          // addToSet(versionToClassAndFilesChanged, aVersionName, aClassAndFileName);
//          addToList(versionToClassAndFilesChanged, aCommitName, aClassAndFileName);
//          List<String> aFileAndVersions = classNameToFileAndVersion.get(aClassName); 
//          String aPreviousClassName = fileNameToClassName.get(aFileName);
//          String aPreviousFileName = getLastFileName(classNameToFileAndVersion, aClassName);
////          if (!aClassName.equals(aPreviousClassName)) {
////            if (!aFileName.equals(aPreviousFileName)) {
//
//
////          if (!aClassName.equals(fileNameToClassName.get(aFileName))) {
//            // addToSet(classNameToFileAndVersion, aClassName, aVersionAndFileName);
//            addToList(classNameToFileAndVersion, aClassName, aVersionAndFileName);
//
////          }
//          if (aPreviousFileName != null) { // it should not be
//          String aFullPreviousFileName = changedFilesInPreviousCommit.getAbsolutePath() + "/" + aPreviousFileName;
//          String aFullCurrentFileName = changedFilesInCurrentCommit.getAbsolutePath() + "/" + aFileName;
//          }
//        
//        }
//        fileNameToClassName.put(aFileName, aClassName);
//
//        if (allFilesSeen.contains(aFileName)) {
//          // allFilesModified.add(aFileName);
//          setAdd(allFilesModified, aFileName);
//
//        } else {
//
//          filesAddedByThisVersion.add(aFileName);
//
//          // allFilesSeen.add(aFileName);
//          setAdd(allFilesSeen, aFileName);
//
//          // addToSet(versionToClassAndFilesAdded, aVersionName, aClassAndFileName);
//          addToList(versionToClassAndFilesAdded, aCommitName, aClassAndFileName);
//
//        }
//        // filesTouchedByThisVersion.add(aFileName);
//        // filesSeenByAllVersions.add(aFileName);
//        output.println(aFileName + "," + anSTType.getNumberOfDeclaredMethods() + ","
//                + anSTType.getNumberOfDeclaredVariables() + "," + anSTType.getSuperClass() + ","
//                + ComprehensiveVisitCheck.toString(anSTType.getDeclaredInterfaces()));
//
//      }
//      String[] aFiles = findFilesChanged(aCommit);
//
//      filesDeletedByThisVersion.addAll(Arrays.asList(aFiles));
//      filesDeletedByThisVersion.removeAll(allFilesModified);
//      filesDeletedByThisVersion.removeAll(filesAddedByThisVersion);
//      String aVersionName = aCommit.getName();
//      for (String aFileName : filesDeletedByThisVersion) {
//        if (!isSourceFile(aFileName)) continue;
//        String aClassName = fileNameToClassName.get(aFileName);
//        if (aClassName == null) {
//          System.err.println("No class for deleted file" + aFileName);
//          continue;
//        }
//        String aClassAndFileName = aClassName + CLASS_FILE_SEPARATOR + aFileName;
//        // addToSet(versionToClassAndFilesDeleted, aVersionName, aClassAndFileName);
//        addToList(versionToClassAndFilesDeleted, aVersionName, aClassAndFileName);
//
//        if (!classesSeenByThisCommit.contains(aClassName)) {
//          // addToSet( classNameToFileAndVersion, aClassName, aVersionName + VERSION_FILE_SEPARATOR
//          // + "");
//          addToList(classNameToFileAndVersion, aClassName,
//                  aVersionName + VERSION_FILE_SEPARATOR + ""); // indicate that is was deleted
//
//        }
//
//      }
//      saveCheckeState(aCommit);
//      output.close();
//      // printFileChangeDetails(aVersion);
//      //
//      // String[] aFiles = findFilesChanged(aVersion);
//      // filesDeletedByThisVersion.addAll(Arrays.asList(aFiles));
//      // filesDeletedByThisVersion.removeAll(filesModifiedByThisVersion);
//      // filesDeletedByThisVersion.removeAll(filesAddedByThisVersion);
//
//    } catch (IOException e) {
//
//    }
//  }
  
  static FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);

  static void runChangeDistiller (File aCommit, File aCommitContents, STType anSTType, String aNormalizedFileName) {
    

   
    if (changedFilesInPreviousCommit == null) {
      return;
    }
  
//    String aNormalizedFileName = toNormalizeFileName(aCommitContents, anSTType.getFileName());
    String aPreviousFileFullName =
            changedFilesInPreviousCommit.getAbsolutePath() + "/" + aNormalizedFileName;
    File aPreviousFile = new File(aPreviousFileFullName);
    if (!aPreviousFile.exists()) {
      return;
    }
    String aCurrentFileFullName = 
            changedFilesInCurrentCommit.getAbsolutePath() + "/" + aNormalizedFileName;
    File aCurrentFile = new File(aCurrentFileFullName);
    if (!aCurrentFile.exists()) {
      return;
    }
    System.err.println ("Current file " + aCurrentFileFullName + " Previous file " + aPreviousFileFullName);
      try {
        distiller.extractClassifiedSourceCodeChanges(aPreviousFile, aCurrentFile);
    
  
        List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
        if(changes != null) {
            for(SourceCodeChange change : changes) {
               System.err.println(change.getClass() + " type " + change.getChangedEntity() + " new entity" + change.getChangedEntity());
            }
        }
      } catch(Exception e) {
        /* An exception most likely indicates a bug in ChangeDistiller. Please file a
           bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
           attach the full stack trace along with the two files that you tried to distill. */
        System.err.println("Warning: error while change distilling. " + e.getMessage());
        e.printStackTrace();
    }
    
  }
  static void processCommit(File aCommit, File aCommitContents, int aCommitIndex) {
    if (aCommitIndex == stoppingCommitIndex) {
      return; // for now do nothing with the last one
    }
    SymbolTable aCurrentSymbolTable = SymbolTableFactory.getCurrentSymbolTable();
    SymbolTable aPreviousSymbolTable = aCurrentSymbolTable.getPreviousSymbolTable();
    JSONObject aGitCommitData = findGitCommitData(aCommit);

    if (aCurrentSymbolTable == null) {
      return;
    }
  
//    filesAddedByThisVersion.clear();
//    filesDeletedByThisVersion.clear();
//    classesSeenByThisCommit.clear();
    File aSymbolTableFile = new File(symbolTableFileName(aCommit));
    try {
      // aSymbolTableFile.createNewFile();
      PrintStream output = new PrintStream(aSymbolTableFile);
      Collection<STType> anAllCurrentTypes = aCurrentSymbolTable.getAllSTTypes();
      output.println("File Name" + "," + "# Methods" + "," + "# Variables" + "," + "Super Class"
              + "," + "Interfaces");
      for (STType anSTType : anAllCurrentTypes) {
        if (anSTType.isExternal()) {
          continue;
        }
        String aNormalizedFileName = toNormalizeFileName(aCommitContents, anSTType.getFileName());

        runChangeDistiller(aCommit, aCommitContents, anSTType, aNormalizedFileName);

        
        String aCommitName = aCommit.getName();
        String aVersionAndFileName = aCommitName + VERSION_FILE_SEPARATOR + aNormalizedFileName;
        String aClassName = anSTType.getName();
        String aClassAndFileName = aClassName + CLASS_FILE_SEPARATOR + aNormalizedFileName;
        // maybeAddToSet(classNameToFileAndVersion, aClassName, aFileAndVersionName);
//        classesSeenByThisCommit.add(aClassName);
        
//        String aPreviousFileName = getLastFileName(classNameToFileAndVersion, aFileName);
        addToList(classNameToFileAndVersion, aClassName, aVersionAndFileName);

   
        // filesTouchedByThisVersion.add(aFileName);
        // filesSeenByAllVersions.add(aFileName);
        output.println(aNormalizedFileName + "," + anSTType.getNumberOfDeclaredMethods() + ","
                + anSTType.getNumberOfDeclaredVariables() + "," + anSTType.getSuperClass() + ","
                + ComprehensiveVisitCheck.toString(anSTType.getDeclaredInterfaces()));

      }
      String[] aFiles = findFilesChanged(aCommit); // do not need this, should use git commit data
      
      // process them

     
    
      saveCheckeState(aCommit);
      output.close();
      // printFileChangeDetails(aVersion);
      //
      // String[] aFiles = findFilesChanged(aVersion);
      // filesDeletedByThisVersion.addAll(Arrays.asList(aFiles));
      // filesDeletedByThisVersion.removeAll(filesModifiedByThisVersion);
      // filesDeletedByThisVersion.removeAll(filesAddedByThisVersion);

    } catch (IOException e) {

    }
  }

  public static void findCommits() {
    if (!history.isDirectory()) {
      System.err.println(history.getAbsolutePath() + " is not a directory");
      System.exit(-1);
    }
    commits = history.listFiles();
    Arrays.sort(commits);

  }

  public static void findCheckpoints() {

    checkpoints = root.listFiles();
    Arrays.sort(checkpoints);

  }

  static List<String> filesChanged = new ArrayList();
  static String[] sourceSuffixes = { "java" };
  static Set<String> sourceSuffixesSet = new HashSet(Arrays.asList(sourceSuffixes));
  
  public static boolean isSourceFile(String aFileName) {
    int aDotIndex = aFileName.indexOf(".");
    String aSuffix = aFileName.substring(aDotIndex + 1);
    return sourceSuffixesSet.contains(aSuffix);
    
  }
  static String[] emptyStringArray = {};
  public static String[] findFilesChanged(File aVersion) {
    filesChanged.clear();
    File aFilesChangedFile = new File(
            aVersion.getAbsolutePath() + "/" + aVersion.getName() + "_" + FILES_CHANGED);
    if (!aFilesChangedFile.exists()) {
      System.err.println(aFilesChangedFile + " does not exist");
      return emptyStringArray;
//      System.exit(-1);
    }
    try {
      Scanner aScanner = new Scanner(aFilesChangedFile);
      while (aScanner.hasNext()) {
        String aChangedFile = aScanner.nextLine();
//        int aDotIndex = aChangedFile.indexOf(".");
//        String aSuffix = aChangedFile.substring(aDotIndex + 1);
//        if (sourceSuffixesSet.contains(aSuffix)) {
          filesChanged.add(aChangedFile);
//        }
      }
      // String[] aFilesChanged = aFilesChangedString.split(FILE_NAME_SEPARATOR);
      return filesChanged.toArray(sourceSuffixes);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;

  }

  public static JSONObject findGitCommitData(File aVersion) {

    File aFilesChangedFile = new File(
            aVersion.getAbsolutePath() + "/" + aVersion.getName() + "_" + GIT_COMMIT_DATA);
    if (!aFilesChangedFile.exists()) {
      System.err.println(aFilesChangedFile + " does not exist");
      return null;
//      System.exit(-1);
    }
    try {
      Scanner aScanner = new Scanner(aFilesChangedFile);
      // aScanner.nextLine();
      stringBuilder.setLength(0);
      while (aScanner.hasNext()) {
        String aContents = aScanner.nextLine();
        stringBuilder.append(aContents);
        stringBuilder.append("\n");
      }
      JSONObject aJSONObject = new JSONObject(stringBuilder.toString());
      return aJSONObject;

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
  // public static void jsontest() {
  // JSONObject aJsonObject = new JSONObject(arg0);
  // JSONWriter jsonWriter = new JSONWriter(w);
  // org.json.
  //
  // }
}
