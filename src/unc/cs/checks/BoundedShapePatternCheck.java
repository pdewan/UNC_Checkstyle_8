package unc.cs.checks;

public abstract class BoundedShapePatternCheck extends LocatableShapePatternCheck {
	public static final String BOUNDS_PROPERTIES = "Width: int" + BASIC_SET_MEMBER_SEPARATOR + "Height: int";

	//@Override
	//public String composeProperties() {
//		return LINE_PATTERN;
	//}
		
		protected String boundsProperties() {
			return BOUNDS_PROPERTIES ;
		}
		
		protected String boundedShapePattern() {
//			return locatablePattern() + "|"  + boundsProperties();
			return locatablePattern() + BASIC_SET_MEMBER_SEPARATOR + boundsProperties();

		}
		@Override
		public String composeProperties() {
			return boundedShapePattern();
		}


//@Override
//public String composePatternName() {
//	return "StructurePatternNames.LINE_PATTERN";
//}	
	
//	public LinePatternCheck() {
//		super.setExpectedPropertiesOfType(LINE_PATTERN);		
//	}
//	@Override
//	protected String msgKey() {
//		// TODO Auto-generated method stub
//		return MSG_KEY;
//	}
//	public Boolean matchesType(String aDescriptor, String aShortClassName) {
//		STNameable aStructurePattern = getPattern(aShortClassName);
//		if (aStructurePattern == null)
//			return true;
//		return aStructurePattern.getName().equals("StructurePatternNames.LINE_PATTERN");
////		if (aDescriptor == null || aDescriptor.length() == 0)
////			return true;
////		if (!aDescriptor.startsWith("@")) {
////			return aShortClassName.equals(aDescriptor);
////		}
////		List<STNameable> aTags = getTags(aShortClassName);
////		if (aTags == null)
////			return null;
////
////		String aTag = aDescriptor.substring(1);
////
////		return contains(aTags, aTag);
//	}
	


}
