package unc.cs.checks;

public  class VectorPatternCheck extends ExpectedSignaturesCheck {
	
//	public static final String VECTOR_SPECIFICATION = "@VECTOR_PATTERN" + TYPE_SEPARATOR + "elementAt:int->$T | size:->int ";
	public static final String VECTOR_SPECIFICATION = "@VECTOR_PATTERN" + TYPE_SEPARATOR + "elementAt:int->$T " +BASIC_SET_MEMBER_SEPARATOR + " size:->int ";

	/**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
//    public static final String MSG_KEY = "listPattern";
   
    public VectorPatternCheck() {
//		super.setExpectedSignaturesOfType(VECTOR_SPECIFICATION);
		super.setExpectedStringsOfType(VECTOR_SPECIFICATION);

	}
    
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
	
	
}
