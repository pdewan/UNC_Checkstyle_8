package unc.tools.checkstyle.commits;


public class TestProjectHistoryCheckerInvoker {

static final String SOURCE = "src/unc/testables";

static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";

static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE};
public static void main (String[] args) {
  ProjectHistoryCheckerSeparateProcessInvoker.main(ARGS);
    
}

}
