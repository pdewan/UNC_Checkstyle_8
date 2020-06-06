package unc.cs.checks;

public class PointPatternCheck extends LocatableShapePatternCheck {
public static final String POINT_PATTERN = "StructurePatternNames.POINT_PATTERN";
public static final String ALTERNATE_POINT_PATTERN = "Point Pattern";


@Override
public String composePatternName() {
	return POINT_PATTERN;
}	
@Override
protected  String composeAlternatePatternName() {
	return ALTERNATE_POINT_PATTERN;
}	

}
