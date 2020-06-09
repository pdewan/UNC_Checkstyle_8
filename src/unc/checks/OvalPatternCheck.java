package unc.checks;

public class OvalPatternCheck extends BoundedShapePatternCheck {
public static final String OVAL_PATTERN = "StructurePatternNames.OVAL_PATTERN";
public static final String ALTERNATE_OVAL_PATTERN = "Oval Pattern";


@Override
public String composePatternName() {
	return OVAL_PATTERN;
}	
@Override
protected  String composeAlternatePatternName() {
	return ALTERNATE_OVAL_PATTERN  ;
}

}
