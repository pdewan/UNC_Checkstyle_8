package unc.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.CallInfo;
import unc.symbolTable.STMethod;
import unc.symbolTable.STType;
import unc.tools.checkstyle.ProjectSTBuilderHolder;

/**
 * 
 * A warning thing, gives message if expected method not called. Expected method call gives info
 * message if expected method called
 *
 */
public class MissingCommonMethodCallCheck extends MissingMethodCallCheck {
  public static final String MSG_KEY_WARNING = "missingCommonMethodCall";
  public static final String MSG_KEY_INFO = "expectedCommonMethodCall";
  protected String otherMethodSignatures;
  protected STMethod[] otherSpecifiedSTMethods;
  protected List<STMethod> otherFoundSTMethods;
  
  public  MissingCommonMethodCallCheck() {
    setIgnoreExternalMethods(true);
  }

  @Override
  protected String msgKeyWarning() {
    return MSG_KEY_WARNING;
  }

  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_INFO;
  }

  public String getOtherMethods() {
    return otherMethodSignatures;
  }

  public void setOtherMethods(String otherMethodSignatures) {
    this.otherMethodSignatures = otherMethodSignatures;
    String[] aSignatures = otherMethodSignatures.split(",");
    otherSpecifiedSTMethods = new STMethod[aSignatures.length];
    for (int anIndex = 0; anIndex < aSignatures.length; anIndex++) {
      String aSignature = aSignatures[anIndex];
      STMethod anSTMethod = signatureToMethod(aSignature);
      otherSpecifiedSTMethods[anIndex] = anSTMethod;

    }
  }
  
  protected List<String> unmatchedStrings = new ArrayList();
  
  protected List<STMethod> getOtherFoundMethods(STType anSTType) {
//    if (otherFoundSTMethods == null) {
      otherFoundSTMethods = new ArrayList();
      for (STMethod anOtherMethod:otherSpecifiedSTMethods) {
        STMethod aFoundMethod = anSTType.getMethod(anOtherMethod.getName(), anOtherMethod.getParameterTypes());
        if (aFoundMethod != null) {
          otherFoundSTMethods.add(aFoundMethod);
        }
      }
//    }
    return otherFoundSTMethods;
  }
 
  protected void processFound(DetailAST anAST, DetailAST aTree, STType anSTType,
          String aSpecification, boolean found, STMethod aFoundMethod, boolean returnNull,
          boolean indirectMethodsNotFullProcessed) {
    // if (!found) {
    //// super.processFound(anAST, aTree, anSTType, aSpecification, found, aFoundMethod, returnNull,
    // indirectMethodsNotFullProcessed);
    // maybeLog
    // return;
    // }
    unmatchedStrings.clear();
    boolean aMatchedAllMethods = found;
    String aCallingMethods = null;
    
    if (!aMatchedAllMethods) {
      aCallingMethods = aSpecification;
      if (isInfo()) { 
        return;
      }
    } else {
//      Set<STMethod> anActualMethods = aFoundMethod.getCallingMethods();
      

      for (STMethod anOtherSpecifiedMethod : getOtherFoundMethods(anSTType)) {
        boolean aSpecifiedMethodFound = false;
        Set<STMethod> aCalledMethods = anOtherSpecifiedMethod.getAllDirectlyOrIndirectlyCalledMethods(); // using side effect
//        Set<STMethod> anActualMethods = aFoundMethod.getCallingMethods();
        if (aCalledMethods.contains(aFoundMethod)) {
          aSpecifiedMethodFound = true;
        }

//        for (STMethod anSTMethod : anActualMethods) {
//          if (matchSignature(anOtherSpecifiedMethod, anSTMethod)) {
//            aSpecifiedMethodFound = true;
//            break;
//          }
//        }
        if (!aSpecifiedMethodFound) {
          unmatchedStrings.add(anOtherSpecifiedMethod.getSignature());
        }
      }
      aMatchedAllMethods = unmatchedStrings.isEmpty();
      
    }
    aCallingMethods = aMatchedAllMethods?
            getCallingMethod() + " and " + otherMethodSignatures:
              unmatchedStrings.toString();
    maybeLog(anAST, aTree, anSTType, aSpecification, aMatchedAllMethods, indirectMethodsNotFullProcessed, aCallingMethods);

  }
  public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
    return super.doPendingCheck(ast, aTreeAST);
  }
  
  protected void maybeLog(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecification,
          boolean found, boolean indirectMethodsNotFullProcessed, String aCallingMethodSignature) {
//    if (!found) {
//      int i = 0;
//      i++;
//    }
    if ((!found && !indirectMethodsNotFullProcessed && !isInfo()) ||
            isInfo() && found) {
//       if (aSpecification.contains("get.*")) {
//       System.out.println ("found specification");
//       }
      // String aCallingMethodSignature = getCallingMethod();

      String aCaller = aCallingMethodSignature;
      if (aCaller == null) {
        aCaller = isInfo() ? "Some" : "Any";
      }
      log(anAST, aTree, aSpecification, aCallingMethodSignature, otherMethodSignatures, anSTType.getName() + ":" + toTagInformation());
      // log(anAST, aTree, aSpecification, aCallingMethodSignature);
//      doMethodCallLog(anSTType, anAST, aTree, aSpecification, aCaller, otherMethodSignatures);

    }
  }
//  public void doFinishTree(DetailAST ast) {
//    
//    super.doFinishTree(ast);
//
//  }

//  protected void maybeLog(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecification,
//          boolean found, boolean indirectMethodsNotFullProcessed, STMethod anOtherMethod) {
//    // indirectMethodNotFullProcessed is redundant
//    if ((!found && !indirectMethodsNotFullProcessed && !isInfo()) || isInfo() && found) {
//      // if (aSpecification.contains("run")) {
//      // System.out.println ("found specification");
//      // }
//      String aCallingMethodSignature = getCallingMethod();
//
//      String aCaller = aCallingMethodSignature;
//      if (aCaller == null) { // this should not happen
//        aCallingMethodSignature = isInfo() ? "Some" : "Any";
//      }
//
//      // log(anAST, aTree, aSpecification, aCallingMethodSignature);
//      doMissingMethodCallLog(anAST, aTree, aSpecification, aCallingMethodSignature,
//              anOtherMethod.getSignature());
//
//    }
//  }
//
//  protected void doMissingMethodCallLog(DetailAST anAST, DetailAST aTree, String aSpecification,
//          String aCallingMethodSignature, String anOtherMethodSignature) {
//    log(anAST, aTree, aSpecification, aCallingMethodSignature, anOtherMethodSignature);
//
//  }

}
