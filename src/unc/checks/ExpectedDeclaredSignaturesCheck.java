package unc.checks;

import unc.symbolTable.STMethod;
import unc.symbolTable.STType;

public  class ExpectedDeclaredSignaturesCheck extends ExpectedSignaturesCheck {
	public static final String MSG_KEY = "expectedDeclaredSignatures";

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	@Override
	protected STMethod[] getMatchedMethods(STType anSTType) {
//		if (anSTType.getName().contains("Singleton")) {
//			System.out.println("Found Singleton");
//		}
		return anSTType.getDeclaredMethods();
	}
}
