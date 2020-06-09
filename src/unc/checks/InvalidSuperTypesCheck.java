package unc.checks;

public  class InvalidSuperTypesCheck extends SuperTypesCheck {
	public static final String MSG_KEY = "invalidSuperTypes";
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {
//				TokenTypes.CLASS_DEF,
//				TokenTypes.INTERFACE_DEF
//				};
//	}
	public void setInvalidSuperTypes(String[] aSpecifications) {
		setSpecifiedTypes(aSpecifications);

	}
	protected boolean logOnNoMatch() {
		return false;
	}
//	// this should be in an abstract type
//	protected List<STNameable> getTypes(STType anSTType) {
//		return anSTType.getAllSuperTypes();
//	}
//
//	@Override
//	boolean doCheck(STType anSTType) {
//		// TODO Auto-generated method stub
//		return true;
//	}
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}


}
