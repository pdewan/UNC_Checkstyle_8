package unc.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.STType;

/**
 * 
 * Like IllegalMethodCall and UnncessaryMethodCall, except they disallow all calls on a class
 * This one, like MissingMethodCallCheck, is tied to a call rather than a class.
 *
 */
public class SpuriousMethodCallCheck extends MissingMethodCallCheck{
  public static final String MSG_KEY_WARNING = "spuriousMethodCall";
  public static final String MSG_KEY_INFO = "noSpuriousMethodCall";
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
  
    if ((found && !indirectMethodsNotFullProcessed && !isInfo()) ||
            isInfo() && !found) {
//       if (aSpecification.contains("get.*")) {
//       System.out.println ("found specification");
//       }
      // String aCallingMethodSignature = getCallingMethod();

      String aCaller = aCallingMethodSignature;
      if (aCaller == null) {
        aCaller = isInfo() ? "No" : "Some";
      }

      // log(anAST, aTree, aSpecification, aCallingMethodSignature);
      doMethodCallLog(anSTType, anAST, aTree, aSpecification, aCaller);

    }
  }
}
