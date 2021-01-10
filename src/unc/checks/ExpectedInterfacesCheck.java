package unc.checks;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STNameable;
import unc.symbolTable.STType;

public  class ExpectedInterfacesCheck extends ExpectedTypesCheck {
	public static final String MSG_KEY = "expectedInterface";
	public static final String MSG_KEY_INFO = "expectedInterface";
  public static final String MSG_KEY_WARNING = "missingInterface";


  
  @Override
  protected String msgKeyWarning() {
    return MSG_KEY_WARNING;
  }

  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_INFO;
  }
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF,
				TokenTypes.PACKAGE_DEF
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
