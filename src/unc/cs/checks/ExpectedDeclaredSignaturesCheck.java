package unc.cs.checks;

import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;

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
