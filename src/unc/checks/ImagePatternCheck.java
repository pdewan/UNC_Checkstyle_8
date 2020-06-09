package unc.checks;

public class ImagePatternCheck extends LocatableShapePatternCheck {
	public static final String IMAGE_PROPERTIES = "ImageFileName: String";
	public static final String IMAGE_PATTERN = "StructurePatternNames.IMAGE_PATTERN";
	public static final String ALTERNATE_IMAGE_PATTERN = "Image Pattern";


	@Override
	public String composePatternName() {
		return IMAGE_PATTERN;
	}
	@Override
	protected  String composeAlternatePatternName() {
		return ALTERNATE_IMAGE_PATTERN;
	}
	protected String imageShapePattern() {
		return locatablePattern() + BASIC_SET_MEMBER_SEPARATOR + imageProperties();
	}
	@Override
	public String composeProperties() {
		return imageShapePattern();
	}	
	protected String imageProperties() {
		return IMAGE_PROPERTIES ;
	}
}
