package unc.cs.checks;

/**
 * 
 * An Info thing, gives message if expected method called.
 * Missing method call gives message if expected method not called
 * Should make this a subclass of MissingMethodCallCheck
 *
 */
public  class ExpectedMethodCallCheck extends MethodCallCheck {
	public static final String MSG_KEY = "expectedMethodCall";
	

	public void setExpectedCalls(String[] aPatterns) {
		super.setExpectedStrings(aPatterns);
//		for (String aPattern : aPatterns) {
//			setExpectedSignaturesOfType(aPattern);
//		}

	}
	
	

	@Override
	protected String msgKey() {
		return MSG_KEY;
	}

    // "fail" if method matches

	@Override
	protected boolean returnValueOnMatch() {
		return false;
	}

}
