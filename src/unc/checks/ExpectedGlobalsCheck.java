package unc.checks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.STVariable;
import unc.symbolTable.SymbolTableFactory;

public  class ExpectedGlobalsCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "expectedGlobals";

//	protected Map<String, String[]> typeToSpecification = new HashMap<>();
	public int[] getDefaultTokens() {
		return new int[] {
//				TokenTypes.CLASS_DEF, 
//				TokenTypes.INTERFACE_DEF, 
//				TokenTypes.PACKAGE_DEF
				};
	}

//	public void setExpectedSpecificationOfType(String aPattern) {
//		String[] extractTypeAndSpecification = aPattern.split(TYPE_SEPARATOR);
//		String aType = extractTypeAndSpecification[0].trim();
//		String[] aGlobals = extractTypeAndSpecification[1].split("\\|");
//		typeToSpecification.put(aType, aGlobals);
//	}

	
	public void setExpectedGlobals(String[] aPatterns) {
		setExpectedTypesAndSpecifications(aPatterns);
//		for (String aPattern : aPatterns) {
//			setExpectedSpecificationOfType(aPattern);
//		}

	}

	
	protected void logGlobalNotMatched(DetailAST aTypeAST, DetailAST aTreeAST, String aGlobal,
			String aType) {
		log (aTypeAST, aTreeAST, aGlobal, aType);

	}

	public Boolean matchGlobals(String[] aSpecifiedGlobals,
			STType anSTType, DetailAST aTreeAST) {
		boolean retVal = true;
//		Set<String> anUnmatchedGlobals = new HashSet(anSTType.getDeclaredGlobals());
		Set<STVariable> anUnmatchedGlobals = new HashSet(anSTType.getDeclaredSTGlobals());

		for (String aSpecifiedGlobal : aSpecifiedGlobals) {
			String[] aGlobalAndType = aSpecifiedGlobal.split(":");
			String aType = aGlobalAndType[1];
			String aVariableSpecification = aGlobalAndType[0].trim();
//			String[] aPropertiesPath = aPropertySpecification.split(".");
			Boolean matched = matchGlobal(aVariableSpecification, maybeStripComment(aType), anUnmatchedGlobals, anSTType);
			if (matched ==null) {
				return null;
			}
//			if (!matchProperty(aType, aPropertySpecification, aPropertyInfos)) {
			if (!matched) {

				logGlobalNotMatched(anSTType.getAST(), aTreeAST, aVariableSpecification, aType);
				retVal = false;
			}
		}
		return retVal;
	}
	
	protected boolean matchesVariable(STVariable anSTVariable, String aDescriptor) {
//		return anSTVariable.getName().matches(aVariableSpecification);
		return unifyingMatchesNameVariableOrTag(aDescriptor, anSTVariable.getName(), anSTVariable.getTags());
	}
	protected boolean matchesNameable(STNameable anSTVariable, String aDescriptor) {
//		return anSTVariable.getName().matches(aVariableSpecification);
		return unifyingMatchesNameVariableOrTag(aDescriptor, anSTVariable.getName(), null);
	}
	

	public Boolean matchGlobal(String aVariableSpecification,
			String aTypeSpecification, Set<STVariable> anUnmatchedGlobals, STType anSTType) {
//		Set<String> aSet = anSTType.getDeclaredGlobals();
		List<STVariable> aSet = anSTType.getDeclaredSTGlobals();

//		int i = 0;
		for (STVariable aVariable : anUnmatchedGlobals) {
			
			if (matchesVariable(aVariable, aVariableSpecification)) {
//				String anActualType = anSTType.getDeclaredGlobalVariableType(aVariable);
				String anActualType = aVariable.getType();

				
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				Boolean retVal = matchesType(aTypeSpecification, anActualType);
						
				if (retVal == null)
					return null;
				if (retVal) {
					anUnmatchedGlobals.remove(aVariable);
					return true;
				} 

			}
		}
		return false;
	}

	

//	public Boolean matchType(String aSpecifiedType, String aProperty,
//			Map<String, PropertyInfo> aPropertyInfos) {
//
//		return matchGetter(aSpecifiedType, aProperty, aPropertyInfos);
//
//	}
	

	public   Boolean matchType(String aSpecifiedType, String anActualType) {
		return unifyingMatchesNameVariableOrTag(aSpecifiedType, anActualType, null);
	}
		
	

	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);
		if (anSTType == null) {
			System.err.println("ST Type is null!");
			System.err.println("Symboltable names" + SymbolTableFactory.getOrCreateSymbolTable().getAllTypeNames());
//			return true;
		}
		if (anSTType.isEnum() ||
				anSTType.isInterface() || anSTType.isAnnotation()) // why duplicate checking for interfaces
			return true;
		String aSpecifiedType = findMatchingType(typeToSpecifications.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
//		Set<String> aDeclaredGlobals = anSTType.getDeclaredGlobals();
//		
//
//		if (aDeclaredGlobals == null) // should not happen
//			return null;
		String[] aSpecifiedGlobals = typeToSpecifications.get(aSpecifiedType);

		return matchGlobals(aSpecifiedGlobals, anSTType, aTree);
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

	public void doFinishTree(DetailAST ast) {
		// STType anSTType =
		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
		// for (STMethod aMethod: anSTType.getMethods()) {
		// visitMethod(anSTType, aMethod);
		// }
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}
}
