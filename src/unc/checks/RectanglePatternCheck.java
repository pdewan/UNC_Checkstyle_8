package unc.checks;

public class RectanglePatternCheck extends BoundedShapePatternCheck {
public static final String RECTANGLE_PATTERN = "StructurePatternNames.RECTANGLE_PATTERN";
public static final String ALTERNATE_RECTANGLE_PATTERN = "Rectangle Pattern";

@Override
public String composePatternName() {
	return RECTANGLE_PATTERN;
}	
@Override
protected  String composeAlternatePatternName() {
	return ALTERNATE_RECTANGLE_PATTERN ;
}	

}
