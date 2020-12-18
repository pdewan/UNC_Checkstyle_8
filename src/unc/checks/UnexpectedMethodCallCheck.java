package unc.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.STType;

public class UnexpectedMethodCallCheck extends MissingMethodCallCheck {
  public static final String MSG_KEY_WARNING = "unexpectedMethodCall";
  public static final String MSG_KEY_INFO = "noUnexpectedMethodCall";
  @Override
  protected String msgKeyWarning() {
    return MSG_KEY_WARNING;
  }

  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_INFO;
  }
  protected void maybeLog(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecification,
          boolean found, boolean indirectMethodsNotFullProcessed, String aCallingMethodSignature) {
    if ((!found && !indirectMethodsNotFullProcessed && isInfo()) || !isInfo() && found) {
      // if (aSpecification.contains("run")) {
      // System.out.println ("found specification");
      // }
      // String aCallingMethodSignature = getCallingMethod();

      String aCaller = aCallingMethodSignature;
      if (aCaller == null) {
        aCaller = isInfo() ? "Some" : "Any";
      }

      // log(anAST, aTree, aSpecification, aCallingMethodSignature);
      doMethodCallLog(anAST, aTree, aSpecification, aCaller);

    }
  }

}
