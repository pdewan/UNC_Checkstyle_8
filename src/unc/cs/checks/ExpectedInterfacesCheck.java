package unc.cs.checks;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.cs.symbolTable.STNameable;
import unc.cs.symbolTable.STType;

public  class ExpectedInterfacesCheck extends ExpectedTypesCheck {
	public static final String MSG_KEY = "expectedInterfaces";
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF,
				};
	}
	
	public void setExpectedInterfaces(String[] aSpecifications) {
//		super.setExpectedStrings(aPatterns);

		super.setExpectedStrings(aSpecifications);

	}
	

	
	protected List<STNameable> getTypes(STType anSTType) {
		return anSTType.getAllInterfaces();
	}
	@Override
	protected
	boolean doCheck(STType anSTType) {
		return !anSTType.isInterface() && !anSTType.isEnum() && !anSTType.isAnnotation();
	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
//	@Override
//	protected Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {
//		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
//
//		if (aPropertyInfos == null)
//			return null;
//
//		Map<String, PropertyInfo> anUmmatchedPropertyInfos = new HashMap(aPropertyInfos);
//		return matchProperties(aStrings, anUmmatchedPropertyInfos, aTree);
//	}
}
