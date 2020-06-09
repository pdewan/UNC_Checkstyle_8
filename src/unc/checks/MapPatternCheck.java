package unc.checks;

public  class MapPatternCheck extends ExpectedSignaturesCheck {
	
//	public static final String MAP_SPECIFICATION = "@MAP_PATTERN" + TYPE_SEPARATOR + "get:$K->$V | put:$K;$V->* ";
	public static final String MAP_SPECIFICATION = "@MAP_PATTERN" + TYPE_SEPARATOR + "get:$K->$V " + BASIC_SET_MEMBER_SEPARATOR + " put:$K;$V->* ";

	/**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
//    public static final String MSG_KEY = "listPattern";
   
    public MapPatternCheck() {
//		super.setExpectedSignaturesOfType(MAP_SPECIFICATION);
		super.setExpectedStringsOfType(MAP_SPECIFICATION);

	}
    
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
	
	
}
