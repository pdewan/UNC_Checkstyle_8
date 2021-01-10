package unc.checks;

import unc.symbolTable.PropertyInfo;

public class ExpectedGettersCheck extends BeanTypedPropertiesCheck {
	public static final String MSG_KEY = "expectedGetter";
  public static final String MSG_KEY_WARNING = "missingGetter";
  public static final String MSG_KEY_INFO = "expectedGetter";
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	@Override
  protected String msgKeyWarning() {
    return MSG_KEY_WARNING;
  }

  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_INFO;
  }


//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = getSTType(aTree);
//		if (anSTType.getName().contains("roceed")) {
//			System.out.println("Doing pending check for " + anSTType.getName());
//
//		}
//
//		Boolean retVal = super.doPendingCheck(anAST, aTree);
//		if (retVal == null) {
//			System.out.println ("returning null in pending check");
//		}
//		return retVal;
//	}





	@Override
	public Boolean matchType(String aSpecifiedType, PropertyInfo aProperty) {
		// TODO Auto-generated method stub
		Boolean retVal = matchGetter(aSpecifiedType, aProperty);
		
		return retVal;
//		return matchGetter(aSpecifiedType, aProperty);
	}


}
