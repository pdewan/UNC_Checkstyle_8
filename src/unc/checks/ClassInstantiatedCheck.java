package unc.checks;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STMethod;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.tools.checkstyle.ProjectSTBuilderHolder;

public abstract  class ClassInstantiatedCheck extends ComprehensiveVisitCheck {
	// move this to a super class
	public static final String CALLER_TYPE_SEPARATOR = "#";
//	public static final String MSG_KEY = "classInstantiated";
	 public static final String MSG_KEY_INFO = "expectedInstantiation";
	 public static final String MSG_KEY_WARNING = "missingInstantiation";

	public static final String MULTIPLE_INSTANTIATING_CODE = "multipleInstantiatingMethods";
	

  @Override
  protected String msgKeyWarning() {
    return MSG_KEY_WARNING;
  }

  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_INFO;
  }


//	protected Map<String, String[]> typeToSpecifications = new HashMap<>();
//	protected Map<String, List<STMethod>> typeToMethods = new HashMap<>();


//	public static final String SEPARATOR = ">";
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF
				};
	}
//	public void visitClass(DetailAST ast) {
//		visitTypeMinimal(ast); //just get the name
//	}

	// this should be in an abstract class
	public void setSpecificationsOfType(String aPattern) {
		String[] extractTypeAndSpecifications = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndSpecifications[0].trim();
		String[] aSpecifications = extractTypeAndSpecifications[1].split(SET_MEMBER_SEPARATOR);
		
		typeToSpecifications.put(aType, aSpecifications);
//		typeToMethods.put(aType, signaturesToMethods(aSignaturesWithTarget));

	}

	
	public void setInstantiations(String[] aPatterns) {
	  super.setExpectedStrings(aPatterns);
//		for (String aPattern : aPatterns) {
//			setSpecificationsOfType(aPattern);
//		}
	}
	public void setInstantiationsOld(String[] aPatterns) {
    for (String aPattern : aPatterns) {
      setSpecificationsOfType(aPattern);
    }
  }
	
	protected String msgKey() {
		return MSG_KEY;
	}
	
	
	
	String specifiedType; // was global in pending check
	
	protected boolean logOnNoMatch() {
		return true;
	}
	

	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
	    return super.doStringArrayBasedPendingCheck(anAST, aTree);
	}
	
//	protected void logInstantaitedOrNot (STType anSTType, DetailAST aTreeAST, String aMethod, String anInstiatedType)
//	       {
//	      String aMatchedTags = anSTType.getMatchedTags();
//	      String aTypeOrTag = aMatchedTags == null ?anSTType.getName():aMatchedTags;
////	      System.out.println ("Bean:" + aType + " " + aProperty);
//	      log (aTreeAST, aTreeAST, anInstiatedType, aMethod, aTypeOrTag);
////	      String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
////	          .getFilename());
////	      if (aTreeAST == currentTree) {
////	        DetailAST aLoggedAST = aTreeAST;
//	  //
////	        aLoggedAST = aTreeAST;
//	  //
////	        log (aLoggedAST.getLineNo(), msgKey(), aProperty, aType, aSourceName);
////	      } else {
////	        log(0, msgKey(), aProperty, aType, aSourceName);
////	      }
//
//	    }
	
	protected boolean processString(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String aString) {
   List<STMethod> aMethods = anSTType.getInstantiatingMethods(aString);
   boolean found = aMethods != null && aMethods.size() > 0;
   if ((isInfo() && found) || (!isInfo() && !found) ) {
     String aMethodsText = found?aMethods.toString(): "no method";
   

	  log (aTree, aTree, aString, toTypeOrTag(anSTType), aMethodsText );
   }
   return found;

	}

	protected  Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {
	  List<STNameable> anInstantiatedTypes = anSTType.getTypesInstantiated();
	  for (String aString:aStrings) {
	    processString(anAST, aTree, anSTType,  aSpecifiedType, aString);
	  }

	  return true;
	}
	
// move to super type at some points
	public Boolean doPendingCheckOld(DetailAST anAST, DetailAST aTree) {
		specifiedType = null;
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(anAST);

		if (anSTType.isEnum() || anSTType.isInterface() || anSTType.isAnnotation())
			return true;
		
		specifiedType = findMatchingType(typeToSpecifications.keySet(),
				anSTType);
		if (specifiedType == null)
			return true; // the constraint does not apply to us
//		if (specifiedType.contains("ingleton")) {
//			System.out.println ("found test type");
//		}
		String[] aSpecifications = typeToSpecifications.get(specifiedType);
		boolean returnNull = false; 
		boolean returnValue = true;
		for (String aSpecification:aSpecifications) {
			int i = 0;
			String aCaller = null;
			STMethod aCallerSpecifiedMethod = null;
			String aType;
			String[] aCallerAndType = aSpecification.split(CALLER_TYPE_SEPARATOR);
			if (aCallerAndType.length == 2) {
				aCaller = aCallerAndType[0].trim();
				aType = aCallerAndType[1].trim();
				aCallerSpecifiedMethod = signatureToMethod(aCaller);
			} else {
				aCaller = null;
				aType = aSpecification.trim();
			}
			String aTypeWithoutComment = maybeStripComment(aType);
			List<STMethod> anInstantiatingMethods = anSTType.getInstantiatingMethods(aTypeWithoutComment);
			if (anInstantiatingMethods == null || anInstantiatingMethods.contains(null)) {
				returnNull = true;
				
				continue;
//				return null; // should never happen
			}
			if (anInstantiatingMethods.isEmpty() && logOnNoMatch()) {
				returnValue = false;
				log(anAST, aTree, anSTType.getName(), aType);
				continue;
			} else if (anInstantiatingMethods.isEmpty() && !logOnNoMatch()) {
				// no instantiations, so success
				continue;
			}
			// so we know it is not empty now
			if (aCaller == null && logOnNoMatch()) 
				// we know it is not empty and caller is not specified, so success
				continue;
			
			if (aCaller == null && !logOnNoMatch()) {
				// not empty, caller is not specified, so failure
				returnValue = false;
				log(anAST, aTree, anSTType.getName(), aType);
				continue;
			}
			if (anInstantiatingMethods.size() != 1 && logOnNoMatch()) {
				// instantiated by more than one method, so failure if logOnNoMatch
				log(MULTIPLE_INSTANTIATING_CODE, anAST, aTree, anSTType.getName()+"." + aCaller, aType, anInstantiatingMethods.toString() );
				continue;
			}
			
			
			STMethod anInstiatingMethod = anInstantiatingMethods.get(0);
			Boolean aMatch = matchesCallingMethod(anSTType, aCallerSpecifiedMethod, anInstiatingMethod);
			if (aMatch == null) {
				returnNull = true;
				continue;
			}
			if (!aMatch && logOnNoMatch() || aMatch && !logOnNoMatch()) {
				log(anAST, aTree, anSTType.getName() + "." + aCaller, aType );
			} 
		}
		if (returnNull) {
//			flushLog();
			return null;
		}
		return returnValue;
	}
	
//	public void doFinishTree(DetailAST ast) {
//		
////		if (fullTypeName == null) {
////			return; // interface or come other non class
////		}
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

}
