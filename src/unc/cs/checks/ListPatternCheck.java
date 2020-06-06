package unc.cs.checks;

public  class ListPatternCheck extends ExpectedSignaturesCheck {
	
//	public static final String LIST_SPECIFICATION = "@LIST_PATTERN" + TYPE_SEPARATOR + "get:int->$T | size:->int ";
	public static final String LIST_SPECIFICATION = "@LIST_PATTERN" + TYPE_SEPARATOR + "get:int->$T " + BASIC_SET_MEMBER_SEPARATOR + " size:->int ";

	/**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
//    public static final String MSG_KEY = "listPattern";
   
    public ListPatternCheck() {
//		super.setExpectedSignaturesOfType(LIST_SPECIFICATION);
		super.setExpectedStringsOfType(LIST_SPECIFICATION);

	}
    
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
	
	
}
