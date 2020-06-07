package unc.cs.checks;

public class InvalidClassInstantiationCheck extends ClassInstantiatedCheck {

	public static final String MSG_KEY = "invalidClassInstantiation";

	protected boolean logOnNoMatch() {
		return false;
	}

	
	protected String msgKey() {
		return MSG_KEY;
	}
  

}
