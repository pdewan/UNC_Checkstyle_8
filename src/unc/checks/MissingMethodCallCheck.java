package unc.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
public class MissingMethodCallCheck extends MethodCallCheck {
  public static final String MSG_KEY_WARNING = "missingMethodCall";
  public static final String MSG_KEY_INFO = "expectedMethodCall";

  public static final String WRONG_CALLER = "wrongCaller";

  @Override
  public int[] getDefaultTokens() {
    return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,

        // TokenTypes.ANNOTATION,
        // TokenTypes.INTERFACE_DEF,

    };

  }

  protected Boolean check(STType aCallingType, DetailAST ast, String aShortMethodName,
          String aLongMethodName, CallInfo aCallInfo) {
    matchedSignature = "";
    // String[] aSignaturesWithTargets = typeToSignaturesWithTargets.get(specifiedType);
    String[] aSignaturesWithTargets = typeToStrings.get(specifiedType);

    for (String aSignatureWithTarget : aSignaturesWithTargets) {

      // Boolean retVal = matches(toShortTypeName (aCallingType.getName()),aSignatureWithTarget,
      // aShortMethodName, aLongMethodName, aCallInfo);
      Boolean retVal = matches(aCallingType, aSignatureWithTarget, aShortMethodName,
              aLongMethodName, aCallInfo);

      if (retVal == null)
        return null;
      if (retVal) {
        matchedSignature = aSignatureWithTarget;
        return returnValueOnMatch();
      }
    }
    return !returnValueOnMatch();
  }

  public void setExpectedCalls(String[] aPatterns) {
    super.setExpectedStrings(aPatterns);
    // for (String aPattern : aPatterns) {
    // setExpectedSignaturesOfType(aPattern);
    // }

  }
  // "fail" if method matches

  @Override
  protected String msgKeyWarning() {
    return MSG_KEY_WARNING;
  }

  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_INFO;
  }

  @Override
  protected boolean returnValueOnMatch() {
    return true;
  }

  // public void doFinishTree(DetailAST ast) {
  // // STType anSTType =
  // // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
  // // for (STMethod aMethod: anSTType.getMethods()) {
  // // visitMethod(anSTType, aMethod);
  // // }
  // maybeAddToPendingTypeChecks(ast);
  // super.doFinishTree(ast);
  //
  // }
  @Override
  public void leaveType(DetailAST ast) {
    if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
      maybeAddToPendingTypeChecks(ast);
    }
    super.leaveType(ast);
  }

  public void doFinishTree(DetailAST ast) {
    // STType anSTType =
    // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
    // for (STMethod aMethod: anSTType.getMethods()) {
    // visitMethod(anSTType, aMethod);
    // }
    if (!ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {

      maybeAddToPendingTypeChecks(ast);
    }
    super.doFinishTree(ast);

  }

  /**
   * This is ignoring aSpecifiedType It should look for calls in specified type rather than
   * anSTTType. Wonder how it works. With the new scheme, we are ignoring aSpecifiedType anyway.
   */
  protected Boolean processStringsCallInfo(DetailAST anAST, DetailAST aTree, STType anSTType,
          String aSpecifiedType, String[] aStrings) {

    // maybe have a separate check for local calls?
    List<CallInfo> aCalls = anSTType.getAllMethodsCalled();

    // for efficiency, let us remove mapped calls

    if (aCalls == null)
      return null;
    List<CallInfo> aCallsToBeChecked = new ArrayList(aCalls);

    String[] aSpecifications = aStrings;
    boolean returnNull = false;
    // int i = 0;
    for (String aSpecification : aSpecifications) {
      // if (aSpecification.contains("say")) {
      // System.out.println ("found specification:");
      // }
      boolean found = false;
      for (CallInfo aCallInfo : aCallsToBeChecked) {
        String aNormalizedLongName = toLongName(aCallInfo.getNormalizedCall());
        String shortMethodName = toShortTypeOrVariableName(aNormalizedLongName);

        // if (aCallInfo.toString().contains("move")) {
        // System.out.println ("found move");
        // }

        Boolean matches = matches(anSTType, maybeStripComment(aSpecification), shortMethodName,
                aNormalizedLongName, aCallInfo);

        if (matches == null) {
          // commenting out this part, perhaps something else will go wrong
          // if (!aSpecification.contains("!")) { // local call go onto another call, not really
          // what is the difference?
          // continue;
          // }

          returnNull = true;
          found = true; // we will come back to this
          continue;
          // return null;
        }
        if (matches) {
          found = true;
          // same call may be made directly or indirectly, and can cause problems if removed
          // aCallsToBeChecked.remove(aCallInfo);
          break;
        }
      }
      
//      if (!found) {
//        int i = 0;
//        i++;
//      }
      
      if (!found && !isInfo() || found && isInfo()) {
//         if (aSpecification.contains("egister")) {
//         System.out.println ("found specification");
//         }
//        Set<String> anIncludeTypeTags = getIncludeTypeTags();
//        String aTagInformation = anIncludeTypeTags == null || anIncludeTypeTags.isEmpty()?
//                "No Tag":anIncludeTypeTags.toString();
        log(anAST, aTree, aSpecification, toTagInformation());
      }

    }
    if (returnNull)
      return null;
    return true;
  }

//  protected Set<STMethod> aMethodsCalledSet = new HashSet<>();
  
  protected List<STMethod> methodsAndConstructors = new ArrayList();

  /**
   * This is ignoring aSpecifiedType It should look for calls in specified type rather than
   * anSTTType. Wonder how it works. With the new scheme, we are ignoring aSpecifiedType anyway.
   */
  protected Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType,
          String aSpecifiedType, String[] aStrings) {
    if (isProcessCalledMethods() && isFirstPass()) {
      return null;
    }

    // STMethod[] anAllCallingMethods = anSTType.getMethods();
    STMethod[] anAllCallingMethods = anSTType.getMethods();
    STMethod[] aConstructors = anSTType.getDeclaredConstructors();
   


    // maybe have a separate check for local calls?
    // List<CallInfo> aCalls = anSTType.getAllMethodsCalled();

    // for efficiency, let us remove mapped calls

    if (anAllCallingMethods == null && aConstructors == null)
      return null;
    // List<CallInfo> aCallsToBeChecked = new ArrayList(aCalls);
    methodsAndConstructors.clear();
    if (anAllCallingMethods != null) {
    methodsAndConstructors.addAll(Arrays.asList(anAllCallingMethods));
    }
    if (aConstructors != null) {
    methodsAndConstructors.addAll(Arrays.asList(aConstructors));
    }

    String[] aSpecifications = aStrings;
    boolean returnNull = false;
    // int i = 0;
    for (String aSpecification : aSpecifications) {
//       if (aSpecification.contains("legs")) {
//       System.out.println ("found specification:");
//       }
      boolean found = false;
      STMethod aFoundMethod = null;
      STMethod aCallerOfFoundMethod = null;
      boolean indirectMethodsNotFullProcessed = false;
    
      
//      if (aSpecification.contains("fire")){
//        System.err.println("found spec");
//      }
//      for (STMethod aCallingMethod : anAllCallingMethods) {
      for (STMethod aCallingMethod : methodsAndConstructors) {

        if (!checkCallingMethod(aCallingMethod)) {
          continue;
        }

        // Set<STMethod> anAllCalledMethods =
        // aCallingMethod.getAllDirectlyOrIndirectlyCalledMethods();
        // if (aCallingMethod.isIndirectMethodsNotFullProcessed()) {
        // indirectMethodsNotFullProcessed = true;
        // }

        Collection<STMethod> anAllCalledMethods = null;
        if (!isProcessCalledMethods()) {
//          List<STMethod> aList = aCallingMethod.getAllMethodsCalled();
          anAllCalledMethods = aCallingMethod.getAllMethodsCalled();;
        } else {

          anAllCalledMethods = aCallingMethod.getAllDirectlyOrIndirectlyCalledMethods();
          if (aCallingMethod.isIndirectMethodsNotFullProcessed()) {
            indirectMethodsNotFullProcessed = true;
          }
        }
        // if (aCallInfo.toString().contains("move")) {
        // System.out.println ("found move");
        // }
        for (STMethod aCalledMethod : anAllCalledMethods) {
          // if (aCalledMethod.getName().contains("reduce")) {
          // System.err.println("found reduce:");
          // }
//          if (aCalledMethod.getName().contains("ingle")) {
//            int i = 1;
//          }
//          if (aCalledMethod != null && aCalledMethod.getName().contains("fire") && aSpecification.contains("fire")) {
//            System.err.println("Should match");
//          }
          Boolean matches = matches(anSTType, maybeStripComment(aSpecification), aCallingMethod,
                  aCalledMethod);

          if (matches == null) {
            // commenting out this part, perhaps something else will go wrong
            // if (!aSpecification.contains("!")) { // local call go onto another call, not really
            // what is the difference?
            // continue;
            // }

            returnNull = true;
            aCallerOfFoundMethod = aCallingMethod;
            found = true; // we will come back to this
            aFoundMethod = aCalledMethod;
            continue;
            // return null;
          }
          if (matches) {
            found = true;
            aFoundMethod = aCalledMethod;
            aCallerOfFoundMethod = aCallingMethod;


            // same call may be made directly or indirectly, and can cause problems if
            // removed
            // aCallsToBeChecked.remove(aCallInfo);
            break;
          }

        }
        if (found) {
          break;
        }
      }
      processFound(anAST, aTree, anSTType, aSpecification, found, aFoundMethod, returnNull,
              indirectMethodsNotFullProcessed, aCallerOfFoundMethod);
      // boolean aFoundOtherConstraints = found && checkOtherConstraints(aFoundMethod, returnNull);
      // // if ((!found && !indirectMethodsNotFullProcessed && !isInfo()) || isInfo() && found ) {
      // //// if (aSpecification.contains("run")) {
      // //// System.out.println ("found specification");
      // //// }
      // // String aCallingMethodSignature = getCallingMethod();
      // // String aCaller = aCallingMethodSignature == null?"Any":aCallingMethodSignature;
      // // log(anAST, aTree, aSpecification, aCallingMethodSignature);
      // // }
      // maybeLog(anAST, aTree, anSTType, aSpecification, aFoundOtherConstraints,
      // indirectMethodsNotFullProcessed);

    }
    if (returnNull)
      return null;
    return true;
  }

  protected void processFound(DetailAST anAST, DetailAST aTree, STType anSTType,
          String aSpecification, boolean found, STMethod aFoundMethod, boolean returnNull,
          boolean indirectMethodsNotFullProcessed, STMethod aCallingMethod) {
    boolean aFoundOtherConstraints = found && checkOtherConstraints(aFoundMethod, returnNull);
//     if ((!found && !indirectMethodsNotFullProcessed && !isInfo()) || isInfo() && found ) {
//      if (aSpecification.contains("ControllerClass!")) {
//      System.out.println ("found specification");
//      }
    // String aCallingMethodSignature = getCallingMethod();
    // String aCaller = aCallingMethodSignature == null?"Any":aCallingMethodSignature;
    // log(anAST, aTree, aSpecification, aCallingMethodSignature);
    // }
//    maybeLog(anAST, aTree, anSTType, aSpecification, aFoundOtherConstraints,
//            indirectMethodsNotFullProcessed, getCallingMethod());
    
    String aSpecifiedCallingMethodSignature = getCallingMethod();
    String anActualCallingMethodSignature = aCallingMethod == null?null:aCallingMethod.getSimpleChecksSignature();
    String aCallingMethodOutput =  aSpecifiedCallingMethodSignature != null?"Method:" + aSpecifiedCallingMethodSignature:
          anActualCallingMethodSignature != null? "Some method (" + anActualCallingMethodSignature + ")" :  "No method";
    
    
//    maybeLog(anAST, aTree, anSTType, aSpecification, aFoundOtherConstraints,
//            indirectMethodsNotFullProcessed, getCallingMethod());
    maybeLog(anAST, aTree, anSTType, aSpecification, aFoundOtherConstraints,
            indirectMethodsNotFullProcessed, aCallingMethodOutput);
//     }
  }

  protected void maybeLog(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecification,
          boolean found, boolean indirectMethodsNotFullProcessed, String aCallingMethodSignature) {
    if (!found) {
      int i = 0;
      i++;
    }
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

      // log(anAST, aTree, aSpecification, aCallingMethodSignature);
      doMethodCallLog(anSTType, anAST, aTree, aSpecification, aCaller);

    }
  }

  protected void doMethodCallLog(STType anSTType, DetailAST anAST, DetailAST aTree, String aSpecification,
          String aCallingMethodSignature) {
    log(anAST, aTree, aSpecification, aCallingMethodSignature, anSTType.getName() + ":" + toTagInformation());

  }

  protected boolean checkOtherConstraints(STMethod aFoundMethod, boolean aReturnNull) {
    return true;
  }

  public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
    return doStringArrayBasedPendingCheck(anAST, aTree);
  }
  public void doPendingChecks() {
    super.doPendingChecks();
  }
//  public Boolean doStringArrayBasedPendingCheck(DetailAST anAST, DetailAST aTree) {
//    return super.doStringArrayBasedPendingCheck(anAST, aTree);
//  }

  // public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
  // if (fullTypeName == null)
  // return true;
  // specifiedType = null;
  //// STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
  //// .getSTClassByShortName(
  //// getName(getEnclosingTypeDeclaration(aTree)));
  //// int i = 0;
  // STType anSTType = getSTType(anAST);
  //
  // if (anSTType == null) {
  // System.out.println ("MissingMethodCall:Did not find sttype for " + fullTypeName + " " +
  // toStringList(aTree));
  // return true;
  // }
  //
  // if (anSTType.isEnum())
  // return true;
  //
  //// specifiedType = findMatchingType(typeToSignaturesWithTargets.keySet(),
  //// anSTType);
  // specifiedType = findMatchingType(typeToStrings.keySet(),
  // anSTType);
  // if (specifiedType == null)
  // return true; // the constraint does not apply to us
  //
  // // maybe have a separate check for local calls?
  // List<CallInfo> aCalls = anSTType.getAllMethodsCalled();
  // // for efficiency, let us remove mapped calls
  //
  //
  // if (aCalls == null)
  // return null;
  // List<CallInfo> aCallsToBeChecked = new ArrayList(aCalls);
  //
  // String[] aSpecifications = typeToSignaturesWithTargets.get(specifiedType);
  // boolean returnNull = false;
  //// int i = 0;
  // for (String aSpecification:aSpecifications) {
  //// if (aSpecification.contains("say")) {
  //// System.out.println ("found specification:");
  //// }
  // boolean found = false;
  // for (CallInfo aCallInfo:aCallsToBeChecked ) {
  // String aNormalizedLongName = toLongName(aCallInfo.getNormalizedCall());
  // String shortMethodName = toShortTypeName(aNormalizedLongName);
  //// if (aSpecification.contains("run") && aCallInfo.getCallee().contains("run")) {
  //// System.out.println ("Found specification");
  //// }
  //
  //// if (aCallInfo.toString().contains("move")) {
  //// System.out.println ("found move");
  //// }
  //// if (aCallInfo.toString().contains("sleep")) {
  //// System.out.println ("found sleep");
  //// }
  // Boolean matches = matches(anSTType, maybeStripComment(aSpecification), shortMethodName,
  // aNormalizedLongName, aCallInfo);
  //
  //// Boolean matches = matches(toShortTypeName(anSTType.getName()), aSpecification,
  // shortMethodName, aNormalizedLongName, aCallInfo);
  // if (matches == null) {
  // //commenting out this part, perhaps something else will go wrong
  //// if (!aSpecification.contains("!")) { // local call go onto another call, not really what is
  // the difference?
  //// continue;
  //// }
  //
  // returnNull = true;
  // found = true; // we will come back to this
  // continue;
  //// return null;
  // }
  // if (matches) {
  // found = true;
  // // same call may be made directly or indirectly, and can cause problems if removed
  //// aCallsToBeChecked.remove(aCallInfo);
  // break;
  // }
  // }
  // if (!found) {
  //// if (aSpecification.contains("run")) {
  //// System.out.println ("found specification");
  //// }
  // log(anAST, aTree, aSpecification);
  // }
  //
  // }
  // if (returnNull)
  // return null;
  // return true;
  // }

}
