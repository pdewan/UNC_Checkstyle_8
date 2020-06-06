package unc.cs.checks;

public class StringShapePatternCheck extends LocatableShapePatternCheck {
	public static final String STRING_PROPERTIES = "Text: String";
	public static final String STRING_PATTERN = "StructurePatternNames.STRING_PATTERN";
	public static final String ALTERNATE_STRING_PATTERN = "String Pattern";


	@Override
	public String composePatternName() {
		return STRING_PATTERN;
	}
	@Override
	protected  String composeAlternatePatternName() {
		return ALTERNATE_STRING_PATTERN;
	}
	protected String stringShapePattern() {
		return locatablePattern() + BASIC_SET_MEMBER_SEPARATOR + stringProperties();
	}
	@Override
	public String composeProperties() {
		return stringShapePattern();
	}	
	protected String stringProperties() {
		return STRING_PROPERTIES ;
	}
}
