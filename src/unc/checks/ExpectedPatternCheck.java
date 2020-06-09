package unc.checks;

import java.util.HashMap;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.STNameable;
import unc.symbolTable.STType;

public  class ExpectedPatternCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY = "expectedPattern";

	protected Map<String, String> typeToPattern = new HashMap<>();
	public int[] getDefaultTokens() {
		return new int[] {
//				TokenTypes.PACKAGE_DEF,
//				TokenTypes.CLASS_DEF
				};
	}

//	public static final String SEPARATOR = ">";


	// this should be in an abstract class
	public void setExpectedPatternOfType(String aPattern) {
		String[] extractTypeAndPattern = aPattern.split(TYPE_SEPARATOR);
		String aType = extractTypeAndPattern[0].trim();
		String aStructurePatern = extractTypeAndPattern[1].trim();
		typeToPattern.put(aType, aStructurePatern);
	}
	
	public void setExpectedPattern(String[] aPatterns) {
		for (String aPattern : aPatterns) {
			setExpectedPatternOfType(aPattern);
		}

	}
	

	
	protected void logPatternNotMatched(DetailAST aTreeAST, String aPattern) {

		String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
		super.log(aTreeAST, aTreeAST, aPattern, aTypeName);


	}

	
	


	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(
//						getName(getEnclosingTypeDeclaration(aTree)));
		STType anSTType = getSTType(aTree);

		if (anSTType.isEnum() || anSTType.isInterface() || anSTType.isAnnotation())
			return true;
//		int i = 0;
		String aSpecifiedType = findMatchingType(typeToPattern.keySet(),
				anSTType);
		if (aSpecifiedType == null)
			return true; // the constraint does not apply to us
		String anExpectedPattern = typeToPattern.get(aSpecifiedType);

		STNameable anActualPattern = anSTType.getStructurePatternName();
		boolean retVal = true;
		if (anActualPattern == null) {
			retVal = false;
		}
		else {
			String anActualPatternName = maybeStripAt(maybeStripQuotes(anActualPattern.getName()));
			String anAlternativeExpectedPatternName = BeanPatternCheck.getAlternateName(anExpectedPattern);
			retVal = anActualPatternName.endsWith(anExpectedPattern) || 
					(anAlternativeExpectedPatternName != null &&
					anActualPatternName.endsWith(maybeStripQuotes(BeanPatternCheck.getAlternateName(anExpectedPattern))));
		}
		if (!retVal) {
			logPatternNotMatched(anAST, anExpectedPattern);
		}
		return retVal;
	}
//	

	

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	public void doFinishTree(DetailAST ast) {
	
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}


}
