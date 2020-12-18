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
 * A warning thing, gives message if expected method not called.
 * Expected method call gives info message if expected method called
 *
 */
public  class MinimumCallsCheck extends MethodCallCheck {
	public static final String MSG_KEY = "minimumCallsCheck";
 
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				 TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF,
				
//				TokenTypes.ANNOTATION,
//				 TokenTypes.INTERFACE_DEF,

				};

	}
	 public void setExpectedMinimumCalls(String[] aPatterns) {
	    super.setExpectedStrings(aPatterns);
	  }
	protected Boolean check(STType aCallingType, DetailAST ast,
			String aShortMethodName, String aLongMethodName, CallInfo aCallInfo) {
		matchedSignature = "";
//		String[] aSignaturesWithTargets = typeToSignaturesWithTargets.get(specifiedType);
		String[] aSignaturesWithTargets = typeToStrings.get(specifiedType);

		for (String aSignatureWithTarget:aSignaturesWithTargets) {
			
//			Boolean retVal = matches(toShortTypeName (aCallingType.getName()),aSignatureWithTarget, aShortMethodName, aLongMethodName, aCallInfo);
			Boolean retVal = matches(aCallingType, aSignatureWithTarget, aShortMethodName, aLongMethodName, aCallInfo);

			if (retVal == null)
				return null;
			if (retVal) {
				matchedSignature = aSignatureWithTarget;
				return returnValueOnMatch();
			}
		}
		return !returnValueOnMatch();
	}
	
    // "fail" if method matches
	

	
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}


	@Override
	protected boolean returnValueOnMatch() {
		return true;
	}
//	public void doFinishTree(DetailAST ast) {
//		// STType anSTType =
//		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		// for (STMethod aMethod: anSTType.getMethods()) {
//		// visitMethod(anSTType, aMethod);
//		// }
//		maybeAddToPendingTypeChecks(ast);
//		super.doFinishTree(ast);
//
//	}
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
	 * This is ignoring aSpecifiedType It should look for calls in specified type rather than anSTTType.
	 * Wonder how it works.
	 * With the new scheme, we are ignoring aSpecifiedType anyway.
	 */
	protected Boolean processStringsCallInfo(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {

	
		// maybe have a separate check for local calls?
		List<CallInfo> aCalls = anSTType.getAllMethodsCalled();
		
		// for efficiency, let us remove mapped calls


		if (aCalls == null)
			return null;
		List<CallInfo> aCallsToBeChecked = new ArrayList(aCalls);

		String[] aSpecifications = aStrings;
		boolean returnNull = false; 
//		int i = 0;
		for (String aSpecification:aSpecifications) {
//			if (aSpecification.contains("say")) {
//				System.out.println ("found specification:");
//			}
			boolean found = false;
			for (CallInfo aCallInfo:aCallsToBeChecked ) {
				String aNormalizedLongName = toLongName(aCallInfo.getNormalizedCall());
				String shortMethodName = toShortTypeOrVariableName(aNormalizedLongName);

//				if (aCallInfo.toString().contains("move"))	{
//					System.out.println ("found move");
//				}

				Boolean matches = matches(anSTType, maybeStripComment(aSpecification), shortMethodName, aNormalizedLongName, aCallInfo);

				if (matches == null) {
					//commenting out this part, perhaps something else will go wrong
//					if (!aSpecification.contains("!")) { // local call go onto another call, not really what is the difference?
//						continue;
//					}
						
					returnNull = true;
					found = true; // we will come back to this
					continue;
//					return null;
				}
				if (matches) {
					found = true;
					// same call may be made directly or indirectly, and can cause problems if removed
//					aCallsToBeChecked.remove(aCallInfo);
					break;
				}				
			}
			if (!found) {
//				if (aSpecification.contains("run")) {
//					System.out.println ("found specification");
//				}
				log(anAST, aTree, aSpecification);
			}
			
		}
		if (returnNull)
			return null;
		return true;
	}
	protected  Boolean matches (STType aCallingType, String aSpecifier, STMethod aCallingMethod ) {
//  String aCallingType = toShortTypeOrVariableName(aCallingSTType.getName());
  String[] aCallerAndRest = aSpecifier.split(CALLER_TYPE_SEPARATOR);
  String aCaller = MATCH_ANYTHING_REGULAR_EXPERSSION;
  
  String aNonTargetPart = aSpecifier;
//  List<String> aCalledParameters = null;

  if (aCallerAndRest.length == 2) {
    aCaller = aCallerAndRest[0].trim();
    aNonTargetPart = aCallerAndRest[1];
  } 
  
  if (!aCallingMethod.getName().matches(aCaller)) {
    return false;
  }
  
  String[] aTypeAndSignature = aNonTargetPart.split(TYPE_SIGNATURE_SEPARATOR);
  String aSignature = aNonTargetPart;
  if (aTypeAndSignature.length == 2) {
    aSignature = aTypeAndSignature[1];
  }
    

//  STMethod aCallingMethod = aCallInfo.getCallingMethod();

  STMethod aCalledSpecifiedMethod = signatureToMethod(aSignature);
  Boolean aMatch = false;
  //= matchSignature(aCalledSpecifiedMethod);
  if (aMatch == null) {
    return null;
  }
  return aMatch;
//  if (aMatch) // check if there is a direct call by the specified method
//  // if (retVal)
//    return true;
//
//  Boolean aMatch = matchesCallingMethod(aCallingType, aCalledSpecifiedMethod, aCallingMethod);
//  return aMatch;
//  if (aMatch == null) {
//    return null;
//  }
//  if (!aMatch) {
//
////  if (!matchesCallingMethod(aCallingSTType, aCallingSpecifiedMethod, aCallingMethod)) {
//    return false;
//  }
//  String anEvaluatedNonTargetPart = substituteParameters(aNonTargetPart, aCallingMethod);
//  String[] aNameParts = anEvaluatedNonTargetPart.split(TREE_REGEX_START);
//  String aSignatureWithTarget = aNameParts[0];
//  String aParametersText = null;
//  if (aNameParts.length > 1) {
//    aParametersText = aNameParts[1];
//  }
////  if (aNameParts.length > 1) {
////    aCalledParameters = new ArrayList<>();
////    for (int i = 1; i < aNameParts.length; i++) {
////      aCalledParameters.add(aNameParts[i]);
////    }             
////  }   
//  
////  STMethod  aCallingMatchingMethod = getMatchingMethod(aCallingSTType, aCallingSpecifiedMethod);
//
//  String[] aSignatureAndTarget = aSignatureWithTarget.split(TYPE_SIGNATURE_SEPARATOR);
//  String aSignature ;
//  String aSpecifiedTarget;
////  String aCalledType = aCallInfo.getCalledType();
////  if (aSignatureAndTarget == null ) {
////    
////    System.err.println ("Null signature!");
////    return false;
////  }
////  if (aSignatureAndTarget.length == 0 ) {
////    
////    System.err.println ("signature with no elements");
////    return false;
////  }
////  
////  if (aSignatureAndTarget.length == 1 ) {
////    
//////    System.out.println ("signature with only one element");
////    aSignature = aSignatureAndTarget[0];
////    if (aCalledType.equals("super")) {
////      aSpecifiedTarget = "super"; // so we can match super with either super or the called type
////    } else {
////    String[] dotSplit = aSignature.split("\\.");
////    if (dotSplit.length > 1) {
////      aSpecifiedTarget = dotSplit[0]; // consistent with call info
////    } else {
////    aSpecifiedTarget = aCallingType;
////    }
////    }
//////    aSpecifiedTarget = aCallInfo.getCalledType(); // assuming local call
////    // the following moved to below
//////    if (aSpecifiedTarget.contains("]") || 
//////        aSpecifiedTarget.contains("[") ||
//////        aSpecifiedTarget.contains("(") ||
//////        aSpecifiedTarget.contains(")"))
//////      return false;
//////    return false;
////  }
//////  if (aSignatureAndTarget.length < 2 ||
//////      aSignatureAndTarget[1] == null ) {
//////    
//////    System.out.println ("Null signature");
//////    return false;
//////  }
//////  String aSignature = aSignatureAndTarget[1].trim();
//////  String aSpecifiedTarget = aSignatureAndTarget[0].trim();
////  else {
////  aSignature = aSignatureAndTarget[1].trim();
////  aSpecifiedTarget = aSignatureAndTarget[0].trim();
////  }
//////  if (aCalledType.contains("]") || // array element
//////      aCalledType.contains("[") ||
//////      aCalledType.contains("(") ||// casts
//////      aCalledType.contains(")"))
////////    return false;
//////    return true; // assume the type is right, 
//  STMethod aSpecifiedMethod = signatureToMethod(aSignature);
//  Boolean result = matches(aSpecifiedTarget, aSpecifiedMethod, aCalledMethod);
////  if (result == null) {
////    System.out.println ("Null result, comment ths out");
////  }
////  if (aCalledParameters == null)
////    return result;
//  if (aParametersText == null)
//    return result;
//  
//  if (result != true)
//    return result;
//
////  return incrementalParametersMatch(aCallingMethod, aCallInfo, aCalledParameters);
//
////  return parametersMatch(aCallingMethod, aCallInfo, aParametersText);


  
}
	/**
	 * This is ignoring aSpecifiedType It should look for calls in specified type rather than anSTTType.
	 * Wonder how it works.
	 * With the new scheme, we are ignoring aSpecifiedType anyway.
	 */
	protected Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {

		STMethod[] anAllCallingMethods = anSTType.getMethods();
		// maybe have a separate check for local calls?
//		List<CallInfo> aCalls = anSTType.getAllMethodsCalled();
		
		// for efficiency, let us remove mapped calls


		if (anAllCallingMethods == null)
			return null;
//		List<CallInfo> aCallsToBeChecked = new ArrayList(aCalls);

		String[] aSpecifications = aStrings;
		boolean returnNull = false; 
//		int i = 0;
		for (String aSpecification:aSpecifications) {
//			if (aSpecification.contains("createRegistr")) {
//				System.out.println ("found specification:");
//			}
			boolean found = false;
			boolean indirectMethodsNotFullProcessed = false;
			for (STMethod aCallingMethod:anAllCallingMethods ) {
				
				Set<STMethod> anAllCalledMethods = aCallingMethod.getAllDirectlyOrIndirectlyCalledMethods();
				if (aCallingMethod.isIndirectMethodsNotFullProcessed()) {
					indirectMethodsNotFullProcessed = true;
				}
//				if (aCallInfo.toString().contains("move"))	{
//					System.out.println ("found move");
//				}
				for (STMethod aCalledMethod : anAllCalledMethods) {
//					if (aCalledMethod.getName().contains("reduce")) {
//						System.err.println("found reduce:");
//					}

					Boolean matches = matches(anSTType, maybeStripComment(aSpecification), aCallingMethod,
							aCalledMethod);

					if (matches == null) {
						// commenting out this part, perhaps something else will go wrong
//					if (!aSpecification.contains("!")) { // local call go onto another call, not really what is the difference?
//						continue;
//					}

						returnNull = true;
						found = true; // we will come back to this
						continue;
//					return null;
					}
					if (matches) {
						found = true;
						// same call may be made directly or indirectly, and can cause problems if
						// removed
//					aCallsToBeChecked.remove(aCallInfo);
						break;
					}
					
				}
				if (found) {
					break;
				}
			}
			if (!found && !indirectMethodsNotFullProcessed) {
//				if (aSpecification.contains("run")) {
//					System.out.println ("found specification");
//				}
				log(anAST, aTree, aSpecification);
			}
			
		}
		if (returnNull)
			return null;
		return true;
	}
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		return doStringArrayBasedPendingCheck(anAST, aTree);
	}

//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		if (fullTypeName == null)
//			return true;
//		specifiedType = null;
////		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
////				.getSTClassByShortName(
////						getName(getEnclosingTypeDeclaration(aTree)));
////		int i = 0;
//		STType anSTType = getSTType(anAST);
//		
//		if (anSTType == null) {
//			System.out.println ("MissingMethodCall:Did not find sttype for " + fullTypeName + " " + toStringList(aTree));
//			return true;
//		}
//
//		if (anSTType.isEnum())
//			return true;
//		
////		specifiedType = findMatchingType(typeToSignaturesWithTargets.keySet(),
////				anSTType);
//		specifiedType = findMatchingType(typeToStrings.keySet(),
//				anSTType);
//		if (specifiedType == null)
//			return true; // the constraint does not apply to us
//
//		// maybe have a separate check for local calls?
//		List<CallInfo> aCalls = anSTType.getAllMethodsCalled();
//		// for efficiency, let us remove mapped calls
//
//
//		if (aCalls == null)
//			return null;
//		List<CallInfo> aCallsToBeChecked = new ArrayList(aCalls);
//
//		String[] aSpecifications = typeToSignaturesWithTargets.get(specifiedType);
//		boolean returnNull = false; 
////		int i = 0;
//		for (String aSpecification:aSpecifications) {
////			if (aSpecification.contains("say")) {
////				System.out.println ("found specification:");
////			}
//			boolean found = false;
//			for (CallInfo aCallInfo:aCallsToBeChecked ) {
//				String aNormalizedLongName = toLongName(aCallInfo.getNormalizedCall());
//				String shortMethodName = toShortTypeName(aNormalizedLongName);
////				if (aSpecification.contains("run") && aCallInfo.getCallee().contains("run")) {
////					System.out.println ("Found specification");
////				}
//
////				if (aCallInfo.toString().contains("move"))	{
////					System.out.println ("found move");
////				}
////				if (aCallInfo.toString().contains("sleep"))	{
////					System.out.println ("found sleep");
////				}
//				Boolean matches = matches(anSTType, maybeStripComment(aSpecification), shortMethodName, aNormalizedLongName, aCallInfo);
//
////				Boolean matches = matches(toShortTypeName(anSTType.getName()), aSpecification, shortMethodName, aNormalizedLongName, aCallInfo);
//				if (matches == null) {
//					//commenting out this part, perhaps something else will go wrong
////					if (!aSpecification.contains("!")) { // local call go onto another call, not really what is the difference?
////						continue;
////					}
//						
//					returnNull = true;
//					found = true; // we will come back to this
//					continue;
////					return null;
//				}
//				if (matches) {
//					found = true;
//					// same call may be made directly or indirectly, and can cause problems if removed
////					aCallsToBeChecked.remove(aCallInfo);
//					break;
//				}				
//			}
//			if (!found) {
////				if (aSpecification.contains("run")) {
////					System.out.println ("found specification");
////				}
//				log(anAST, aTree, aSpecification);
//			}
//			
//		}
//		if (returnNull)
//			return null;
//		return true;
//	}

}
