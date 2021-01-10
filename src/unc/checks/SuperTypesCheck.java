package unc.checks;

import java.util.List;

import unc.symbolTable.STNameable;
import unc.symbolTable.STType;

public  class SuperTypesCheck extends ExpectedTypesCheck {
	public static final String MSG_KEY = "expectedSuperTypes";
	 public static final String MSG_KEY_INFO = "expectedSuperType";
   public static final String MSG_KEY_WARNING = "missingSuperType";

   @Override
   protected String msgKey() {
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


}
