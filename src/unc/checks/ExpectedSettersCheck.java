package unc.checks;

import unc.symbolTable.PropertyInfo;

public class ExpectedSettersCheck extends BeanTypedPropertiesCheck {
	public static final String MSG_KEY = "expectedSetters";
	
	public static final String MSG_KEY_WARNING = "missingSetter";
  public static final String MSG_KEY_INFO = "expectedSetter";

  @Override
  protected String msgKeyWarning() {
    return MSG_KEY_WARNING;
  }

  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_INFO;
  }
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}



//	public Boolean matchType(String aSpecifiedType, String aProperty,
//			Map<String, PropertyInfo> aPropertyInfos) {
//
//		return matchSetter(aSpecifiedType, aProperty, aPropertyInfos);
//
//	}



	@Override
	public Boolean matchType(String aSpecifiedType, PropertyInfo aProperty) {
		// TODO Auto-generated method stub
		return matchSetter(aSpecifiedType, aProperty);
	}

}
