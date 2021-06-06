package unc.checks;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STMethod;
import unc.symbolTable.STType;

public  class ExpectedConstructorsCheck extends ExpectedSignaturesCheck {
	public static final String MSG_KEY = "expectedConstructors";

	public ExpectedConstructorsCheck() {
	  super();
	}
	@Override
	// get full name
//	public int[] getDefaultTokens() {
//		return new int[] {
////				 TokenTypes.PACKAGE_DEF,
////				TokenTypes.CLASS_DEF
//
//				};
//
//	}
	public int[] getDefaultTokens() {
    return new int[] {
         TokenTypes.PACKAGE_DEF,
        TokenTypes.CLASS_DEF

        };

  }
	@Override
	protected STMethod[] getMatchedMethods(STType anSTType) {
//		if (anSTType.getName().contains("Say")) {
//			System.out.println ("Found Say");
//		}
		return anSTType.getDeclaredConstructors();
		
	}
	@Override
	public Boolean matchMethod(
			STMethod aSpecification, List<STMethod> aMethods) {
//	public Boolean matchMethod(
//			STMethod aSpecification, STMethod[] aMethods) {
//		if (aMethods.length == 0 && aSpecification.getParameterTypes().length == 0)
		if (aMethods.size() == 0 && aSpecification.getParameterTypes().length == 0)

			return true;
		if (
				aMethods.size() == 0 && 
				
				(aSpecification.isConstructor() && 
						aSpecification.getParameterNames().length == 0)) {
			return true; // matching default constructor
		}
		return super.matchMethod(aSpecification, aMethods);
//		for (STMethod aMethod : aMethods) {
//			Boolean hasMatched = matchSignature(aSpecification, aMethod);
//			if (hasMatched == null)
//				return null;
//			if (hasMatched)
//				// return
//				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
//				return true;
//
//			else 
//				continue;
//		}
//		return false;
	}
	// integrate with doCHeck
//	@Override
//	 protected boolean visitType(STType anSTType) {
//		  return !anSTType.isInterface() && !anSTType.isEnum();
//	  }
	
	@Override
	 protected boolean doCheck(STType anSTType) {
		  return !anSTType.isInterface() && !anSTType.isEnum() && !anSTType.isAnnotation();
	  }

	public  STMethod signatureToMethodorOrConstructor(String aSignature) {
		return signatureToConstructor(aSignature);
	}

	public Boolean matchSignature(
			STMethod aSpecification, STMethod aMethod) {
		variablesAdded.clear();
		
		Boolean retVal  = 
				aSpecification.getParameterTypes().length == aMethod.getParameterTypes().length ;
				
		if (!retVal) {
//			backTrackUnification();
			return false;
		}
		return matchParameters(aSpecification, aMethod);
//		// this should be integrated with super class
//		String[] aSpecificationParameterTypes = aSpecification.getParameterTypes();
//		String[] aMethodParameterTypes = aMethod.getParameterTypes();
//		for (int i = 0; i < aSpecificationParameterTypes.length; i++) {
//			String aParameterType = aSpecificationParameterTypes[i];
//
//			STNameable[] parameterTags =null;
//			if (aParameterType.startsWith("@")) {
//				
//				STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aParameterType.substring(1));
//				if (aParameterSTType == null)
//					return null;
//				parameterTags = aParameterSTType.getComputedTags();
//			}
//			
//			if (!matchesNameVariableOrTag(aSpecificationParameterTypes[i], aMethodParameterTypes[i], parameterTags)) {
//				backTrackUnification();
//				return false;
//			}
//		}
//		return true;		
		
	}
	
	 protected void maybeAddToPendingTypeChecks(DetailAST ast) {
	   super.maybeAddToPendingTypeChecks(ast);
	 }
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	



}
