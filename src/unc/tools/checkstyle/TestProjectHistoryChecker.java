package unc.tools.checkstyle;

public class TestProjectHistoryChecker {
//  static final String TEST_PROJECT = "Data/TestRepository";
//  static final String TEST_PROJECT = "D:/dewan_backup/Java/UNC_Checkstyle_8/Data/TestRepository";

  static final String TEST_PROJECT ="C:/Users/dewan/Downloads/RxJava_java_only/java_only";
  
  public static void main (String[] args) {
//    String[] myArgs = {"-c", "unc_checks.xml", TEST_PROJECT};
    String[] myArgs = {"-c", "D:/dewan_backup/Java/UNC_Checkstyle_8/unc_checks.xml", TEST_PROJECT};

    ProjectHistoryChecker.main(myArgs);
  }

}
