package unc.tools.checkstyle.commits;

import unc.tools.checkstyle.PostProcessingMain;

public class TestProjectHistoryChecker {
//  static final String TEST_PROJECT = "Data/TestRepository";
//  static final String TEST_PROJECT = "D:/dewan_backup/Java/UNC_Checkstyle_8/Data/TestRepository";
// Version 2057-b22f24fc086085d66842ffde0a30c6652843d6b0
//  static final String TEST_PROJECT ="C:/Users/dewan/Downloads/RxJava_java_only/java_only";
  static final String TEST_PROJECT ="C:/Users/dewan/Downloads/releases_prev_cp100";
//  static final String TEST_CHECKPOINT = "115-2.0.0-31995f431f227738e8d4e480c8715f7c827ef481";
//  static final String TEST_CHECKPOINT = "0Initial-697fd66aae9beed107e13f49a741455f1d9d8dd9";

//  static final String TEST_CHECKPOINT = "0-0.1.2-933fb40234910dd30d258d393570b599f180080e";
  static final String TEST_CHECKPOINT = "001-0.5.0-369868492739a54f557678955891a8355408e503";

//  static final String TEST_CHECKPOINT = "201-3.0.0-a1693ecc9215027a7a3eae75272979ebb4d79a27";


  public static void main (String[] args) {
//    String[] myArgs = {"-c", "unc_checks.xml", TEST_PROJECT};
    String[] myArgs = {"-c", "D:/dewan_backup/Java/UNC_Checkstyle_8/unc_checks.xml", TEST_PROJECT, TEST_CHECKPOINT};
//    PostProcessingMain.setRedirectFirstPassOutput(false);
//    ProjectHistoryChecker.setRedirecOutput(false);
    ProjectHistoryChecker.main(myArgs);
  }

}
