package unc.checks;

import unc.symbolTable.STMethod;
import unc.symbolTable.STType;

public  class ExpectedDeclaredSignaturesCheck extends ExpectedSignaturesCheck {
//	public static final String MSG_KEY = "missingDeclaredSignature";
  public static final String MSG_KEY_WARNING = "missingDeclaredSignature";
  public static final String MSG_KEY_INFO = "expectedDeclaredSignature";
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
	 @Override
	  protected String msgKeyWarning() {
	    return MSG_KEY_WARNING;
	  }

	  @Override
	  protected String msgKeyInfo() {
	    return MSG_KEY_INFO;
	  }
	@Override
	protected STMethod[] getMatchedMethods(STType anSTType) {
//		if (anSTType.getName().contains("Singleton")) {
//			System.out.println("Found Singleton");
//		}
		return anSTType.getDeclaredMethods();
	}
}
