package unc.checks;

import java.util.List;

import unc.symbolTable.STNameable;
import unc.symbolTable.STType;

public  class SuperTypesCheck extends ExpectedTypesCheck {
	public static final String MSG_KEY = "expectedSuperTypes";
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
//				TokenTypes.CLASS_DEF,
//				TokenTypes.INTERFACE_DEF
				};
	}
	public void setExpectedSuperTypes(String[] aSpecifications) {
//		setExpectedTypes(aSpecifications);
		super.setExpectedStrings(aSpecifications);

		

	}
	// this should be in an abstract type
	protected List<STNameable> getTypes(STType anSTType) {
		return anSTType.getAllSuperTypes();
	}

	@Override
	protected
	boolean doCheck(STType anSTType) {
		// TODO Auto-generated method stub
		return !anSTType.isEnum() && !anSTType.isAnnotation();
	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}


}
