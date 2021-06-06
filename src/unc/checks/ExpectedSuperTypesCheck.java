package unc.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.STType;

public  class ExpectedSuperTypesCheck extends SuperTypesCheck {
//	public static final String MSG_KEY = "expectedSuperTypes";
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {
//				TokenTypes.CLASS_DEF,
//				TokenTypes.INTERFACE_DEF
//				};
//	}
//	public void setExpectedSuperTypes(String[] aSpecifications) {
//		setExpectedTypes(aSpecifications);
////		setSpecifiedTypes(aSpecifications);
//
//
//	}
	protected boolean logOnNoMatch() {
		return true;
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
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
	@Override
	protected
	boolean doCheck(STType anSTType) {
		// TODO Auto-generated method stub
		return super.doCheck(anSTType) && !anSTType.isInterface();
	}
  protected void maybeAddToPendingTypeChecks(DetailAST ast) {
//System.err.println("Type processed:" + aFullTypeName);
//if (aFullTypeName != null && aFullTypeName.contains("ClassAsType") && !isFirstPass()) {
//  int i = 1;
//}
//    String aFullTypeName = getFullTypeName();

//if (aFullTypeName != null && aFullTypeName.equals("shapes.AShape") ) {
//  if (includeTypeTags.toString().contains("BOUNDED") ) {
//    int i = 0;
//    i++;
//  }
//
//}
//    if (includeTypeTags.toString().contains("BOUNDED") ) {
//      int i = 0;
//      i++;
//    }
  super.maybeAddToPendingTypeChecks(ast);
}

}
