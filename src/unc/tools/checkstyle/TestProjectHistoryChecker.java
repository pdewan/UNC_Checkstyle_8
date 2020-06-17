package unc.tools.checkstyle;

public class TestProjectHistoryChecker {
  static final String TEST_PROJECT = "Data/TestRepository";
  public static void main (String[] args) {
    String[] myArgs = {"-c", "unc_checks.xml", TEST_PROJECT};
    ProjectHistoryChecker.main(myArgs);
  }

}
