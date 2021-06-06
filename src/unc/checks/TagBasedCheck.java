package unc.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import unc.symbolTable.AnSTNameable;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;
import unc.tools.checkstyle.ProjectSTBuilderHolder;
import unc.tools.checkstyle.UNCAstTreeStringPrinter;

public abstract class TagBasedCheck extends TypeVisitedCheck{
	public static final String COMMENT_START = "//";
	public static final char TAG_CHAR = '@';
	public static final String TAG_STRING = "" + TAG_CHAR;
	public static final String MATCH_ANYTHING = "*";
	public static final String MATCH_ANYTHING_REGULAR_EXPERSSION = ".*";
	public static final String MATCH_SOMETHING_REGULAR_EXPERSSON = "(.*)";



	public static final String TYPE_SEPARATOR = "=";
//	public static final String BASIC_SET_MEMBER_SEPARATOR = "|";
	public static final String BASIC_SET_MEMBER_SEPARATOR = "AND";


//	public static final String SET_MEMBER_SEPARATOR = "\\|";
//	public static final String SET_MEMBER_SEPARATOR = "\\" + BASIC_SET_MEMBER_SEPARATOR;
	public static final String SET_MEMBER_SEPARATOR = BASIC_SET_MEMBER_SEPARATOR;
	public static final String AND_SYMBOL = "+";

	public static final String AND_SYMBOL_REGEXP = "\\" + AND_SYMBOL;


	protected Set<String> excludeTypeTags;
	protected Set<String> includeTypeTags;
	protected Set<String> excludeMethodTags;
	protected Set<String> includeMethodTags;
	protected List<List<String>> includeSets = new ArrayList();
	protected List<List<String>> excludeSets = new ArrayList();
	protected boolean typeTagsInitialized;
	protected List<STNameable> typeTags;
	protected List<STNameable> computedTypeTags;
	protected STNameable pattern;
	protected List<STNameable> currentMethodTags = emptyList;
	protected List<STNameable> currentVariableTags = emptyList;

	protected List<STNameable> currentMethodComputedTags;
	static STNameable[] emptyNameableArray = {};
 	static List<STNameable> emptyNameableList = Arrays.asList( emptyNameableArray);
	protected DetailAST currentTree;
	protected Map<String, Integer> typeToInt = new Hashtable<>();
	protected Map<String, String> specificationVariablesToUnifiedValues = new Hashtable<>();
	protected STNameable structurePattern;
	public static final DetailAST noAST = new DetailAstImpl();
//	protected STType stTypeToBeChecked;


	
	protected List<String> variablesAdded = new ArrayList(); //should be cleared by a matcher
	@Override
	public int[] getDefaultTokens() {
		return new int[] {						
						TokenTypes.CLASS_DEF,  
						TokenTypes.INTERFACE_DEF, 
						};
	}
	
	public static String[] primitiveTypes = {
		"int",
		"double",
		"char",
		"boolean",
		
};
	public static String[] javaLangTypes = {
//		"int",
//		"double",
//		"char",
//		"boolean",
		"Integer",
		"Double",
		"Character",
		"String",
		"StringBuilder",
		"Boolean",
		"Runnable",
		"Thread",
		"Exception",
		"InterruptedException",
		"System",
		"Object",
		"Math",
		"Byte",
		"Class",
		"ClassLoader",
		"ClassValue",
		"Compiler",
		"Enum",
		"Float",
		"InheritableThreadLocal",		
		"Long", 
		"Number", 
		"Package", 
		"ProcessBuilder",
		"ProcessBuilder.Redirect",
		"Runtime",
		"RuntimePermission",		
		"SecurityManager", 
		"Short",
		"StackTraceElement",
		"StrictMath",  
		"StringBuffer", 
		"ThreadGroup",
		"ThreadLocal",  
		"Throwable", 
		"Void",
		"Character.UnicodeScript",
		"ProcessBuilder.Redirect.Type", 
		"Thread.State",
		"ArithmeticException", 
		"ArrayIndexOutOfBoundsException",  
		"ArrayStoreException",
		"ClassCastException",  
		"ClassNotFoundException", 
		"CloneNotSupportedException",  
		"EnumConstantNotPresentException", 
		"IllegalAccessException",  
		"IllegalArgumentException",  
		"IllegalMonitorStateException",  
		"IllegalStateException", 
		"IllegalThreadStateException", 
		"IndexOutOfBoundsException",
		"InstantiationException",  
		"InterruptedException",  
		"NegativeArraySizeException",  
		"NoSuchFieldException",  
		"NoSuchMethodException", 
		"NullPointerException",  
		"NumberFormatException", 
		"ReflectiveOperationException",  
		"RuntimeException",  
		"SecurityException", 
		"StringIndexOutOfBoundsException", 
		"TypeNotPresentException", 
		"UnsupportedOperationException",
		"AbstractMethodError", 
		"AssertionError",  
		"BootstrapMethodError",  
		"ClassCircularityError", 
		"ClassFormatError",  
		"Error", 
		"ExceptionInInitializerError", 
		"IllegalAccessError",  
		"IncompatibleClassChangeError",  
		"InstantiationError",  
		"InternalError", 
		"LinkageError",  
		"NoClassDefFoundError",  
		"NoSuchFieldError",  
		"NoSuchMethodError", 
		"OutOfMemoryError",  
		"StackOverflowError",  
		"ThreadDeath",
		"UnknownError",  
		"UnsatisfiedLinkError",  
		"UnsupportedClassVersionError",  
		"VerifyError", 
		"	VirtualMachineError"
	};

	
	static List<STNameable> emptyList = Arrays.asList(emptyNameableArray);

	protected static Set<String> allProjectExternalImports = new HashSet();
	protected static Set<String> allProjectExternalImportsShortName = new HashSet();

	protected List<STNameable> allImportsOfThisClass = new ArrayList();
//	protected 

	protected static Set<String> javaLangTypesSet;
	protected static Set<String> primitiveTypesSet;

	/*
	 * T1 | @T2 | T3,  T3 | T4  *
	 */
	public void addIncludeSet(String newVal) {
		String[] aTypes = newVal.split(SET_MEMBER_SEPARATOR);
		for (int i = 0; i < aTypes.length; i++) {
			aTypes[i] = aTypes[i].trim();
		}
		includeSets.add(Arrays.asList(aTypes));		
	}
	public void addExcludeSet(String newVal) {
		String[] aTypes = newVal.split(SET_MEMBER_SEPARATOR);
		for (int i = 0; i < aTypes.length; i++) {
			aTypes[i] = aTypes[i].trim();
		}
		excludeSets.add(Arrays.asList(aTypes));		
	}

	public void setExcludeSets(String[] newVal) {
		for (String aString:newVal)	 {
			addExcludeSet(aString);
		}
	}
	public void setIncludeSets(String[] newVal) {
		for (String aString:newVal)	 {
			addIncludeSet(aString);
		}
	}
	public void setIncludeTypeTags(String[] newVal) {
		this.includeTypeTags = new HashSet(Arrays.asList(newVal));		
	}
	
	protected String toTagInformation() {
	  Set<String> anIncludeTypeTags = getIncludeTypeTags();
    String aTagInformation = anIncludeTypeTags == null || anIncludeTypeTags.isEmpty()?
            "No Tag":anIncludeTypeTags.toString();
    return aTagInformation;
	}
	protected Set<String> getIncludeTypeTags() {
	  return includeTypeTags;
	}
	public void setExcludeTypeTags(String[] newVal) {
		this.excludeTypeTags = new HashSet(Arrays.asList(newVal));		
	}
	public void setIncludeMethodTags(String[] newVal) {
		this.includeMethodTags = new HashSet(Arrays.asList(newVal));		
	}
	public void setExcludeMethodTags(String[] newVal) {
		this.excludeMethodTags = new HashSet(Arrays.asList(newVal));		
	}
	
	public boolean hasExcludeTypeTags() {
		return excludeTypeTags != null && excludeTypeTags.size() > 0;
	}
	
	public boolean hasIncludeTypeTags() {
		return includeTypeTags != null && includeTypeTags.size() > 0;
	}
	public boolean hasExcludeMethodTags() {
		return excludeMethodTags != null && excludeMethodTags.size() > 0;
	}
	
	public boolean hasIncludeMethodTags() {
		return includeMethodTags != null && includeMethodTags.size() > 0;
	}
	
	public static  boolean matchesAllAndedSpecificationTag (Collection<STNameable> aStoredTags, String anAndedSpecification) {
		String[] aSpecifications = anAndedSpecification.split(AND_SYMBOL_REGEXP);
	for (String aSpecification:aSpecifications) {
		if (! matchesSomeStoredTag(aStoredTags, aSpecification))
			return false;
	}
	return true;
   }
	public  boolean matchesSomeSpecificationTags (Collection<STNameable> aStoredTags, Collection<String> aSpecifications) {
		for (String aSpecificationTag:aSpecifications) {			
			aSpecificationTag = maybeStripAt(aSpecificationTag);
			if (aSpecificationTag.equals(MATCH_ANYTHING) || 
					matchesAllAndedSpecificationTag(aStoredTags, aSpecificationTag))
				return true;
		}
		return false;
	   }
	
	public static  Boolean matchesSomeStoredTag (Collection<STNameable> aStoredTags, String aDescriptor) {
//		int i = 0;
		if (aStoredTags == null) {
			return true;
		}
		for (STNameable aStoredTag:aStoredTags) {
			if (aStoredTag == null) {
//				System.err.println("Null stored tag!");
//				return null;
				continue; // an interface does this
			}
			if (matchesStoredTag(aStoredTag.getName(), aDescriptor)) {
				return true;
			}
		}
		return false;
	   }
	
	
//	public static boolean containsAndedTags (Collection<String> aSpecificationTags, String aStoredTag) {
//		String[] anAndedTags = aStoredTag.split("&");
//		for (String anAndedTag:anAndedTags) {
//			if (!containsTag(aSpecificationTags, anAndedTag))
//				return false;
//		}
//		return true;
//	}
//	public static Boolean matchesAndedTags(String aDescriptor, String aStoredTag) {
//		String[] anAndedTags = aStoredTag.split("&");
//		for (String anAndedTag:anAndedTags) {
//			if (!matchesStoredTag(aStoredTag, anAndedTag))
//				return false;
//		}
//		return true;	
//    }
//	public static boolean containsAndedTags (Collection<String> aStoredTags, STNameab aSpecificationTag) {
//		String[] anAndedTags = aSpecificationTag.split("&");
//		for (String anAndedTag:anAndedTags) {
//			if (!containsSpecificTag(aStoredTags, anAndedTag))
//				return false;
//		}
//		return true;
//	}
	
	public static boolean containsTag (Collection<String> aSpecificationTags, String aStoredTag) {
		for (String aSpecificationTag:aSpecificationTags) {
			if (matchesStoredTag(aStoredTag, aSpecificationTag))
				return true;		
		}
		return false;
	}
	
//	public boolean checkTagOfCurrentType(String aTag) {
//		if (hasIncludeTags()) {
//			return contains(includeTags, aTag);
//		} else { // we know it has exclude tags
//			return !contains(excludeTags, aTag);
//		}
//	}
	/*
	 * do not have to check hasInclude and hasExcludeTags now
	 */
//	public boolean checkTagOfCurrentType(String aStoredTag) {
//		Boolean retVal = true;
//		if (hasIncludeTags()) {
//			 retVal  = containsTag(includeTags, aStoredTag);
//		}
//		if (retVal && hasExcludeTags()) { 
//			return !containsTag(excludeTags, aStoredTag);
//		}
//		return retVal;
//	}
//	public boolean checkIncludeTagOfCurrentType(String aTag) {
//			return includeTags.contains(aTag);
//		
//	}
//	public boolean checkExcludeTagOfCurrentType(String aTag) {
//		return !excludeTags.contains(aTag);
//	
//   } 
	/*
	 * looks like this is has some stored tag, should use anded stuff here also
	 */
	public static  Boolean hasTag(STNameable[] aStoredTags, String aDescriptor) {
	  if (aStoredTags == null) {
//	    System.err.println("Null stored tags");
	    return false;
	  }
	  List<STNameable> aStoredTagsList = Arrays.asList(aStoredTags);
	  if (aDescriptor.contains(AND_SYMBOL)) {
	    //should we check in a loop if one of the anded tags matches?
	    return matchesAllAndedSpecificationTag(aStoredTagsList, aDescriptor);
	  } else {
	    return matchesSomeStoredTag(aStoredTagsList, aDescriptor);

	  }
	  
	  
//		return matchesSomeStoredTag(Arrays.asList(aStoredTags), aDescriptor);

    }
	
	protected int getInt(String aType) {
		Integer retVal = typeToInt.get(aType);
		if (retVal == null) return typeToInt.get(MATCH_ANYTHING); // should not be exercised
		return retVal;
	}
//public boolean checkIncludeTagsOfCurrentType() {
//	if (!hasIncludeTags() && !hasExcludeTags())
//		return true; // all tags checked in this case
//	if (fullTypeName == null) {
////		System.err.println("Check called without type name being populated");
//		// could be the wrong type, class or interface, so we do nto have to check
//		return false;
//	}
//	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//	STNameable[] aCurrentTags = anSTType.getComputedTags();
//	if (hasIncludeTags())
//		return checkIncludeTagsOfCurrentType(aCurrentTags);
//	else
//		return checkExcludeTagsOfCurrentType(aCurrentTags);		
//}
// if anyone says exclude, exclude
//public boolean checkExcludeTagsOfCurrentType(STNameable[] aCurrentTags) {
//	
//	for (STNameable aCurrentTag:aCurrentTags) {
//		if (checkExcludeTagOfCurrentType(aCurrentTag.getName()))
//				return true;
//	}
//	return false;
//	
//}
// if anyone says include, include
// public boolean checkIncludeTagsOfCurrentType(STNameable[] aCurrentTags) {
//	
//	for (STNameable aCurrentTag:aCurrentTags) {
//		if (checkIncludeTagOfCurrentType(aCurrentTag.getName()))
//				return true;
//	}
//	return false;
//	
//}
 public STNameable getPattern(String aShortClassName)  {
// 	List<STNameable> aTags = emptyList;

 	// these classes have no tags
// 	if ( aShortClassName.endsWith("[]") ||
// 			allKnownImports.contains(aShortClassName) || 
// 			javaLangClassesSet.contains(aShortClassName) ) {
// 		return emptyList;
// 	}
 	if ( isArray(aShortClassName) ||
 			isJavaLangClass(aShortClassName) ) {
 		return null;
 	}
 	if (shortTypeName == null || // guaranteed to not be a pending check
 			(aShortClassName.equals(shortTypeName) || aShortClassName.endsWith("." + shortTypeName))) {
 		return structurePattern;
 	} else {
 		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
 				.getSTClassByShortName(aShortClassName);
 		if (anSTType == null) {
 			if (isExternalImportCacheCheckingShortName(aShortClassName)) // check last as we are not really sure about external
 				return null;			
 			return null;
 		}
 		return anSTType.getStructurePatternName();
 	}
 	
 }
 public  boolean containsEfficient(List<STNameable> aTags, String aTag, String aTypeName) {
	 boolean retVal = matchesAllAndedSpecificationTag(aTags, aTag) ||
			 matchesPatternEfficient(aTag, aTypeName); // should not need this
//	 if (retVal) {
//		 if (aTag.contains("Model") && aTypeName.contains("Model")) {
//			 System.out.println ("Found model");
//		 }
//	 }
	 return retVal;
// 	for (STNameable aNameable:aTags) {
// 		if (matchesStoredTag(aNameable.getName(), aTag)) {
// 			matchedTypeOrTagAST = aNameable.getAST();
//// 		if (aNameable.getName().equals(aTag))
// 			return true;
// 		}
// 	}
// 	return matchesPattern(aTag, aTypeName);
 }
 
 public  boolean matchesPatternEfficient (String aPatternName, String aTypeName) {
//	 STNameable aStructurePattern = getPattern(aTypeName);
	 STNameable aStructurePattern = getPattern(toLongTypeNameNoArray(aTypeName));
		if (aStructurePattern == null)
			return false;
		return aStructurePattern.getName().equals(aPatternName) || 
				aStructurePattern.getName().equals("StructurePatternNames." + aPatternName);
 }
//could return a list also
 public  String findMatchingType (Collection<String> aTypesToBeMatched, STType anSTType) {
	   List<String> aMatchingTypes = findMatchingTypes(aTypesToBeMatched, anSTType);
	   if (aMatchingTypes == null || aMatchingTypes.size() == 0)
		   return null;
	   return aMatchingTypes.get(0);
	   
//		for (String aSpecifiedType:aTypesToBeMatched) {
//			matchedTypeOrTagAST = anSTType.getAST();
//				Boolean matches = matchesAllAndedSpecificationTag(Arrays.asList(anSTType.getComputedTags()), aSpecifiedType);
//
//				if (matches == null) {
//					return null;
//				}
//				if (matches)
//					return aSpecifiedType; 
//		}
//		return null;
	} 

 public  List<String> findMatchingTypes (Collection<String> aTypesToBeMatched, STType anSTType) {
	 if (anSTType == null) {
		 System.err.println("Null anSType in findMatchingTypes");
		 return null;
	 }
	 if (anSTType.getComputedTags() == null) {
     System.err.println("No computed tags for:" + anSTType);
     return null;

	 }
	List<String> retVal = new ArrayList();
	 for (String aSpecifiedType:aTypesToBeMatched) {
			matchedTypeOrTagAST = anSTType.getAST();
//				Boolean matches = matchesTypeUnifying(aSpecifiedType, anSTType.getName());
			
			
				Boolean matches = matchesAllAndedSpecificationTag(Arrays.asList(anSTType.getComputedTags()), aSpecifiedType);

				if (matches == null) {
					return null;
				}
				if (matches) {
					retVal.add(aSpecifiedType);
//				if (matchesType(aSpecifiedType, anSTType.getName()))
//					return aSpecifiedType; 
				}
		}
	 	if (retVal.size() > 1) {
	 		List<String> aFilteredRetVal = new ArrayList();
		 	STNameable[] anActualTags = anSTType.getTags();
		 	for (STNameable aNameable:anActualTags) {
		 		String aTag = "@" + maybeStripQuotes(aNameable.getName());
		 		if (retVal.contains(aTag)) {
		 			aFilteredRetVal.add(aTag);
		 		}
		 	}
		 	if (aFilteredRetVal.size() > 0) {
		 		return aFilteredRetVal;
		 	}

	 	}
		return retVal;
	} 
 
 public static String maybeStripQuotes(String aString) {
 	if (aString.indexOf("\"") != -1) // quote rather than named constant
 		return aString.substring(1, aString.length() -1);
 	return aString;
 }
 public static String maybeStripComment(String aString) {	 
	 	int aCommentStart = aString.indexOf(COMMENT_START);
	 	if (aCommentStart < 0)
	 		return aString.trim();
	 	return aString.substring(0, aCommentStart).trim();
	 }
 public static String maybeStripAt(String aString) {
	 if (aString.startsWith(TAG_STRING)) {
			return aString.substring(1);
	 }
	 return aString;
 }
 public static Boolean matchesStoredTag(String aStoredTag, String aDescriptor) {
 		return 
 				aDescriptor.equals(MATCH_ANYTHING) || 
 				maybeStripAt( // some may add @ to the tag, remove at  the storage point
 						maybeStripQuotes(
 								aStoredTag)).matches(
 											maybeStripAt(
 													maybeStripQuotes(
 															maybeStripComment(
 																	aDescriptor))));
// 						maybeStripQuotes(aStoredTag).matches(
// 									maybeStripAt(
// 											maybeStripQuotes(aDescriptor)));
 	
 }

 
 public static boolean isJavaLangClass(String aShortName) {
	
	 return javaLangTypesSet.contains(aShortName);
 }
 public static boolean isExternalImportCacheChecking(String aFullClassName) {
	 return aFullClassName.startsWith("java.") ||
	  allProjectExternalImports.contains(aFullClassName);
 }
 public static boolean isExternalImportCacheCheckingShortName(String aShortClassName) {
	 return allProjectExternalImportsShortName.contains(aShortClassName);
 }
 
 
 public static boolean isExternalClass(String aFullClassName) {
//	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aFullClassName);
	 STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aFullClassName);

	if (anSTType != null // what if we create existing classes, we may not have the external class in our path
			)
//			&& !STBuilderCheck.getImportsAsExistingClasses()) 
//		return false;
		return anSTType.isExternal();
	String aShortClassName = toShortTypeOrVariableName(aFullClassName);
	return aShortClassName.equals("Object") ||
			isExternalImportCacheCheckingShortName(aShortClassName) || 
//			isExternalImportCacheChecking(aShortClassName) || // not really short name
	     isExternalImportCacheChecking(aFullClassName) || // not really short name

			isJavaLangClass(aShortClassName);
 }
 public static List<STNameable> asListOrNull(STNameable[] anArray) {
	 return anArray == null ?
			 null:
			Arrays.asList(anArray);
	 
 }
 
 public List<STNameable> lookupTags(String aClassName)  {
//	 STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
//				.getSTClassByShortName(aShortClassName);
	 STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
	         .getSTClassByFullName(aClassName);
	 if (anSTType.isExternal()) {
	   return emptyList;
	 }
		if (anSTType == null) {
			if (isExternalImportCacheCheckingShortName(aClassName)) // check last as we are not really sure about external
				return emptyList;			
			return null;
		}		
//		return Arrays.asList(anSTType.getComputedTags());
		return asListOrNull(getAllTagsAsArray(anSTType));

 }
 static List<STNameable> allTags = new ArrayList();
 public List<STNameable> lookupTagsOfCurrentTree()  {
   STType anSTType = null;
   anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(getFullTypeName());
   if (anSTType != null) {
//     return asListOrNull(anSTType.getComputedTags());
//     TagBasedCheck.getAllTags(anSTType); //initialize sttype tags if necessary
     return asListOrNull(TagBasedCheck.getAllTagsAsArray(anSTType));

//     return asListOrNull(anSTType.getConfiguredTags());
   }
   if (!isFirstPass()) {
     anSTType =  getSTType(currentTree);
 }
   if (anSTType == null) {
     
     return computedTypeTags(); // STBuilder
   } else {
//     return Arrays.asList(anSTType.getComputedTags());
     return asListOrNull(anSTType.getComputedTags());

   }
       
   
  }
 
 public List<STNameable> oldLookupTagsOfCurrentTree()  {
	STType anSTType = getSTType(currentTree);
	if (anSTType == null) {
		
		return computedTypeTags(); // STBuilder
	} else {
//		return Arrays.asList(anSTType.getComputedTags());
		return asListOrNull(anSTType.getComputedTags());

	}
			
	
 }
 
 public List<STNameable> getTags(String aLongClassName)  {
	List<STNameable> aTags = emptyList;


	if ( isArray(aLongClassName) ||
			isJavaLangClass(aLongClassName) ) {
		return emptyList;
	}
	aTags = lookupTags(aLongClassName);
	if (aTags == null && (
//			shortTypeName == null ||
//			aLongClassName.equals(shortTypeName))) {
		aLongClassName.equals(getFullTypeName()))) {

		aTags = computedTypeTags();
	}
	/*
	 * why shortcircuit current one, there was a reason, I forget now
	 */
//	if (shortTypeName == null || // guaranteed to not be a pending check
//			aShortClassName.equals(shortTypeName)) {
////		aTags = typeTags();
//		aTags = computedTypeTags();
//	} else {
//		aTags = lookupTags(aShortClassName);
//////		System.out.println ("Checking symbol table");
////		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
////				.getSTClassByShortName(aShortClassName);
////		if (anSTType == null) {
////			if (isExternalImport(aShortClassName)) // check last as we are not really sure about external
////				return emptyList;			
////			return null;
////		}
////		aTags = Arrays.asList(anSTType.getComputedTags());
//////		aTags = Arrays.asList(anSTType.getAllComputedTags());
//
//	}
	return aTags;
	
}
 public Boolean matchISA(String aDescriptor, String aName) {
	 Boolean retVal = null;
	 try {
			STType anActualType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
			if (anActualType != null) {
				retVal = anActualType.isSubtypeOf(aDescriptor);
				if (retVal != null) {
					return retVal;
				}
				
			}
			Class anActualClass = Class.forName(aName) ;
			Class aSpecifiedClass = Class.forName(aDescriptor);
			return aSpecifiedClass.isAssignableFrom(anActualClass);
		 
	 } catch (Throwable t) {
		 return false;
		 
	 }
 }
 public Boolean unifyingMatchesNameVariableOrTag(String aDescriptor, String aName, STNameable[] aTags) {
	 if (aDescriptor == null) {
		 return true;
	 }
	 if (aName == null) {
		 return true;
	 }
	 aDescriptor = aDescriptor.trim();
	 if (aDescriptor.equals(MATCH_ANYTHING)) {
	 	 return true;
	 } else if (aDescriptor.startsWith("$")) {
			String aUnifiedValue = specificationVariablesToUnifiedValues
					.get(aDescriptor);
			if (aUnifiedValue == null) {
				specificationVariablesToUnifiedValues.put(aDescriptor,
						aName);
				variablesAdded.add(aDescriptor);
				return true;
			} else {
				return aName.equals(aUnifiedValue);
			}
		} else if (aDescriptor.startsWith(TAG_STRING)) {
		 
			return hasTag(aTags, aDescriptor);
		}	else {
			String aShortName = toShortTypeOrVariableName(aName);
			boolean aContainsSquareBracket = aName.contains("[") || aDescriptor.contains("[");
//			return aName.matches(aDescriptor) || aName.contains(aDescriptor); // allow regex
			// do not want user scanner to match Scanner class so do not use contains
			// allow regex
			try {
			Boolean retVal = aName.equals(aDescriptor) ||
					aShortName.equals(aDescriptor) || 
					(!aContainsSquareBracket && 
					        (
					aName.matches(aDescriptor) || 
					aShortName.matches(aDescriptor)
					));
//			if (retVal) {
//				return true;
//			}
			return retVal;
//			STType anActualType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
//			if (anActualType != null) {
//				retVal = anActualType.isSubtypeOf(aDescriptor);
//				if (retVal != null) {
//					return retVal;
//				}
//				
//			}
//			Class anActualClass = Class.forName(aName) ;
//			Class aSpecifiedClass = Class.forName(aDescriptor);
//			return aSpecifiedClass.isAssignableFrom(anActualClass);
			} 
			
			catch (Throwable e) {
//				e.printStackTrace();
				return true;
			}

		}
	 
 }
 protected void backTrackUnification() {
	 for (String aVariable:variablesAdded) {
		 specificationVariablesToUnifiedValues.remove(aVariable);
	 }
	 variablesAdded.clear();
 }
 static STNameable[] nameables = {};

//for some reason this is not supposed to call matchedMyType with clas name
	public Boolean matchesMyType(String aDescriptor) {
//		String aClassName = shortTypeName;
		if (aDescriptor == null || aDescriptor.length() == 0 || aDescriptor.equals(MATCH_ANYTHING))
			return true;
		if (aDescriptor.startsWith(TAG_STRING)) {
			STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(getFullTypeName());
			if (anSTType == null) {
				return false;
			}
//			STNameable[] checkTags = anSTType.getAllComputedTags();
//			STNameable[] checkTags = anSTType.getComputedTags();
			STNameable[] checkTags = getAllTags(anSTType).toArray(nameables);

//			String aTag = aDescriptor.substring(1);
//			return contains(typeTags(), aDescriptor, shortTypeName);
			if (checkTags == null) {
				System.err.println("Null check tags:");
				return null;
			}

			return containsEfficient(Arrays.asList(checkTags), aDescriptor, shortTypeName);
		} else {
			return unifyingMatchesNameVariableOrTag(aDescriptor, shortTypeName, null) ||  unifyingMatchesNameVariableOrTag(aDescriptor, getFullTypeName(), null);
		}
			
//		} else if (aDescriptor.startsWith("$")) {
//			String aUnifiedValue = specificationVariablesToUnifiedValues
//					.get(aDescriptor);
//			if (aUnifiedValue == null) {
//				specificationVariablesToUnifiedValues.put(aDescriptor,
//						aUnifiedValue);
//				return true;
//			} else {
//				return aClassName.equals(aUnifiedValue);
//			}
//		} else {
//			return aClassName.equals(aDescriptor);
//		}
	}
 protected DetailAST matchedTypeOrTagAST;
 
public Boolean matchesType(Collection<String> aDescriptors, String aShortClassName) {
	for (String aDescriptor:aDescriptors) {
		Boolean match = matchesTypeUnifying(aDescriptor, aShortClassName);
		if (match == null)
			return null;
		
//		if (matchesType(aDescriptor, aShortClassName))
		if (match)

			return true;
	}
	return false;
}
public  Set<String> setOf(String aType, List<List<String>> aLists) {
	Set<String> result = new HashSet();
	for (List<String> aSet:aLists) {
		Boolean match = matchesType(aSet, aType);
		if (match == null) {
			return null;
		}
//		if (matchesType(aSet, aType)) {
		if (match) {

			result.addAll(aSet);
		}
	}
	return result;
}
public  Set<String> includeSetOf(String aType) {
	return setOf(aType, includeSets);
}
public  Set<String> excludeSetOf(String aType) {
	return setOf(aType, includeSets);
}
protected List<String> filterTypes(List<String> aTypes, String aTypeName) {
	if (aTypes == null)
		return null;
	List<String> result = aTypes;
	if (includeSets.size() > 0) {
		result = filterTypesByIncludeSets(result, aTypeName);
	}
	if (excludeSets.size() > 0) {
		// this should be result
//		aTypes = filterTypesByExcludeSets(result, aTypeName);
		result = filterTypesByExcludeSets(result, aTypeName);

	} 
	return result;
}
protected List<String> filterTypesByIncludeSets(List<String> aTypes, String aTypeName) {
	if (aTypes == null)
		return null;
	Set<String> anIncludeSet = includeSetOf(aTypeName);
	if (anIncludeSet == null)
		return null;
	List<String> result = new ArrayList();
	for (String aType:aTypes) {
		Boolean matches = matchesType(anIncludeSet, aType);
//		if (matchesType(anIncludeSet, aType))
		if (matches == null)
			return null;
		if (matches)
			result.add(aType);
	}
	return result;
		
}
protected List<String> filterTypesByExcludeSets(List<String> aTypes, String aTypeName) {
	if (aTypes == null)
		return null;
	Set<String> anExcludeSet = excludeSetOf(aTypeName);
	if (anExcludeSet == null)
		return null;
	
	List<String> result = new ArrayList();
	for (String aType:aTypes) {
		Boolean match = matchesType(anExcludeSet, aType);
//		if (!matchesType(anExcludeSet, aType))
		if (!match)

			result.add(aType);
	}
	return result;
		
}

public Boolean matchesTypeUnifying(String aDescriptor, String aLongClassName) {
	if (aDescriptor == null || aDescriptor.length() == 0 
			|| aDescriptor.equals(MATCH_ANYTHING)  || 
			aLongClassName.matches(aDescriptor))
		return true;
//	int i = 0;
	if (aLongClassName.contains("]") || // array element
			aLongClassName.contains("[") ||
			aLongClassName.contains("(") ||// casts
			aLongClassName.contains(")"))
//		return false;
		return true; // assume the type is right, 
	aDescriptor = aDescriptor.trim();
	String aShortClassName = toShortTypeName(aLongClassName);
//	if (aDescriptor.equals(MATCH_ANYTHING))
//		return true;
	if (!aDescriptor.startsWith(TAG_STRING)) {
//		return aShortClassName.equals(aDescriptor);
		try {
		return unifyingMatchesNameVariableOrTag(aDescriptor, aLongClassName, null);
		} catch (PatternSyntaxException e) {
			System.out.println("Pattern mismatch Descriptor: " + aDescriptor + "aShortClassName "  + aLongClassName);
			e.printStackTrace();
			return false;
		}
	}
//	int i = 0;
	String aTag = aDescriptor.substring(1);
	if (aShortClassName.matches(aTag) || aShortClassName.matches("A" + aTag))
		return true; // in case the class name is the same as tag or is ATag
//	if (aTag.contains("Model") && aShortClassName.contains("Model")) {
//		System.out.println("found model");
//	}
	List<STNameable> aTags = getTags(aLongClassName);
	if (aTags == null)
		return null;
		// this should be changed back to null at some point
//		return false;

//	String aTag = aDescriptor.substring(1);
//	if (aTag.contains("Model") && aShortClassName.contains("Model")) {
//	System.out.println("found model");
//	}
	boolean aClassContainsTags = containsEfficient(aTags, aTag, aLongClassName);
	if (aClassContainsTags) 
		return true;

		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aLongClassName);
		if (anSTType == null)	
			return null;// this should not happen
		Set<STType> aSubtypes = anSTType.getSubSTTypes();
			if (aSubtypes == null) {
				return null;
			}
			for (STType aSubtype:aSubtypes) {
				List<STNameable>  aSubTags = asListOrNull(aSubtype.getComputedTags());			
				if (aSubTags == null) {
					return null;
				}
				if (containsEfficient(aSubTags, aTag, aSubtype.getName())) {
					return true;
				}
			}	
	
	return false;
}
public static Boolean matchesType(String aDescriptor, String aShortClassName) {
	if (aDescriptor == null || aDescriptor.length() == 0 || aDescriptor.equals(MATCH_ANYTHING ))
		return true;
	if (
//			aShortClassName.contains("]") || // array element
//			aShortClassName.contains("[") ||
			aShortClassName.contains("(") ||// casts
			aShortClassName.contains(")"))
//		return false;
		return true; // assume the type is right, 
	aDescriptor = aDescriptor.trim();
//	if (aDescriptor.equals(MATCH_ANYTHING))
//		return true;
	if (!aDescriptor.startsWith(TAG_STRING)) {
//		return aShortClassName.equals(aDescriptor);
		try {
		return aShortClassName.matches(aDescriptor);
		} catch (PatternSyntaxException e) {
			System.err.println("Pattern mismatch Descriptor: " + aDescriptor + "aShortClassName "  + aShortClassName);
			e.printStackTrace();
			return false;
		}
	}
	String aTag = aDescriptor.substring(1);
	if (aShortClassName.matches(aTag) || aShortClassName.matches("A" + aTag))
		return true; // in case the class name is the same as tag or is ATag
	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aShortClassName);
	if (anSTType == null)
		return null;
//	List<STNameable> aTags = Arrays.asList(anSTType.getComputedTags());
	 List<STNameable> aTags = asListOrNull(anSTType.getAllTags());

	if (aTags == null)
	  return null;
//		return null;
		// this should be changed back to null at some point
//		return false;

//	String aTag = aDescriptor.substring(1);

	return matchesAllAndedSpecificationTag(aTags, aTag);
}

protected boolean inferTag() {
	return false;
}
protected Boolean checkIncludeExcludeTagsOfType(STType anSTType) {
  if (!checkExcludeRegularExpressionsOfCurrentType()) {
    return false;
  }
  if (inferTag())
    return true;
  if (!hasIncludeTypeTags() && !hasExcludeTypeTags())
    return true; // all tags checked in this case
//    return false; // no tags checked in this case
//  
//  if (fullTypeName == null) {
////    System.err.println("Check called without type name being populated");
//    return false;
//  }
  
  return checkIncludeTagsOfCurrentType(anSTType)  && checkExcludeTagsOfCurrentType(anSTType);
//  List<STNameable> aStoredTags = computedTypeTags();
//  for (STNameable aStoredTag:aStoredTags) {
//    if (checkTagOfCurrentType(aStoredTag.getName()))
//        return true;
//  }
//  return false;
  
}
protected boolean checkExcludeRegularExpressionsOfCurrentType() {
	
	if (getFullTypeName() == null) {
		
		return true;
	}
//	if (fullTypeName.contains("AbstractSlave")) {
//		System.err.println("Found abstractslave");
//	}
	for (String anExcludeExpression:STBuilderCheck.getExcludeClassRegularExpressions()) {
		if (getFullTypeName().matches(anExcludeExpression)) {
			return false;
		}
	}
	return true;
}

protected Boolean checkIncludeExcludeTagsOfCurrentType() {
	if (!checkExcludeRegularExpressionsOfCurrentType()) {
		return false;
	}
	if (inferTag())
		return true;
	if (!hasIncludeTypeTags() && !hasExcludeTypeTags())
		return true; // all tags checked in this case
//		return false; // no tags checked in this case
//	
//	if (fullTypeName == null) {
////		System.err.println("Check called without type name being populated");
//		return false;
//	}
	
	return checkIncludeTagsOfCurrentType()	&& checkExcludeTagsOfCurrentType();
//	List<STNameable> aStoredTags = computedTypeTags();
//	for (STNameable aStoredTag:aStoredTags) {
//		if (checkTagOfCurrentType(aStoredTag.getName()))
//				return true;
//	}
//	return false;
	
}
public Boolean checkIncludeExcludeTagsOfCurrentMethod() {
	return checkIncludeExcludeTagsOfMethod(currentMethodComputedTags);
	
}

public Boolean checkIncludeExcludeTagsOfMethod(List<STNameable> aCurrentMethodComputedTags) {
	if (!hasIncludeMethodTags() && !hasExcludeMethodTags())
		return true; // all tags checked in this case
//		return false; // no tags checked in this case
	
	if (aCurrentMethodComputedTags == null) {
//		System.err.println("Check called without type name being populated");
		return false;
	}
	
	return checkIncludeTagsOfMethod(aCurrentMethodComputedTags)	&& checkExcludeTagsOfMethod(aCurrentMethodComputedTags);
//	List<STNameable> aStoredTags = computedTypeTags();
//	for (STNameable aStoredTag:aStoredTags) {
//		if (checkTagOfCurrentType(aStoredTag.getName()))
//				return true;
//	}
//	return false;
	
}
public boolean checkIncludeTagsOfCurrentType() {
	if (!hasIncludeTypeTags())
		return false;
//	return checkTags(includeTags, computedTypeTags());
//	return matchesSomeSpecificationTags(computedTypeTags(), includeTypeTags);
//	if (includeTypeTags.toString().contains("BOUNDED")) {
//	  int i = 0;
//	  i++;
//	}
	return matchesSomeSpecificationTags(lookupTagsOfCurrentTree(), includeTypeTags);
	

	
}
public boolean checkIncludeTagsOfCurrentType(STType anSTType) {
  if (!hasIncludeTypeTags()) {
//    return false;
  return true;
  }

//  return checkTags(includeTags, computedTypeTags());
//  return matchesSomeSpecificationTags(computedTypeTags(), includeTypeTags);
  return matchesSomeSpecificationTags(getAllTags(anSTType), includeTypeTags);
  

  
}
/*
 * return true if type is not to be excluded, that is, checked
 */
public boolean checkExcludeTagsOfCurrentType() {
	if (!hasExcludeTypeTags())
		return true;
//	return !checkTags(excludeTags, computedTypeTags());
	return !matchesSomeSpecificationTags(lookupTagsOfCurrentTree(), excludeTypeTags);	
}
public boolean checkExcludeTagsOfCurrentType(STType anSTType) {
  if (!hasExcludeTypeTags())
    return true;
//  return !checkTags(excludeTags, computedTypeTags());
  return !matchesSomeSpecificationTags(getAllTags(anSTType), excludeTypeTags); 
}

/*
 * return true if method is not to be excluded, that is, checked
 */
public boolean checkExcludeTagsOfMethod(List<STNameable> aCurrentMethodComputedTags) {
	if (!hasExcludeMethodTags())
		return true;
//	return !checkTags(excludeTags, computedTypeTags());
	return !matchesSomeSpecificationTags(aCurrentMethodComputedTags, excludeMethodTags);	
}

public boolean checkIncludeTagsOfMethod(List<STNameable> aCurrentMethodComputedTags) {
	if (!hasIncludeMethodTags())
		return true; // include all
//	return checkTags(includeTags, computedTypeTags());
	return matchesSomeSpecificationTags(aCurrentMethodComputedTags, includeMethodTags);	
}


//public boolean checkTags(Collection<String> aSpecifications, Collection<STNameable> aStoredTags ) {
//	for (STNameable aStoredTag:aStoredTags) {
//		if (checkTagOfCurrentType(aStoredTag.getName()))
//				return true;
//	}
//	return false;
//}
//public boolean checkTags(String aDescription, Collection<STNameable> aStoredTags ) {
//	for (STNameable aStoredTag:aStoredTags) {
//		if (checkTagOfCurrentType(aStoredTag.getName()))
//				return true;
//	}
//	return false;
//}
protected List<STNameable> typeTags( ) {
	if (!typeTagsInitialized) {
		DetailAST aTypeAST = getEnclosingTypeDeclaration(currentTree);
		maybeVisitTypeTags(aTypeAST);
	}
	return typeTags;
}
public static STNameable toShortPatternName(STNameable aLongName) {
	if (aLongName ==null) {
//		System.out.println("Null a long nane:" + aLongName);
		return null;
	}
	String aShortName = toShortTypeOrVariableName(aLongName.getName());

	return  new AnSTNameable(aLongName.getAST(), aShortName);
}

protected List<STNameable> computedTypeTags() {
	typeTags();
	return computedTypeTags;
}

public static List<STNameable> getArrayLiterals (DetailAST parentOfArrayInitializer) {
	List<STNameable> result = new ArrayList<>();
	 DetailAST arrayInit =
			parentOfArrayInitializer.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT);
	 if (arrayInit == null)
		 arrayInit = parentOfArrayInitializer; // single element array
	 DetailAST anArrayElementExpression = arrayInit.findFirstToken(TokenTypes.EXPR);
	 
	 while (anArrayElementExpression != null) {
		 DetailAST anArrayElementAST = anArrayElementExpression.getFirstChild();
		 if (anArrayElementAST == null || anArrayElementAST.getParent() == null){
		   break;
		 }
		 FullIdent aFullIdent = FullIdent.createFullIdentBelow(anArrayElementAST.getParent());

//		 result.add(new AnSTNameable(anArrayElementAST, anArrayElementAST.getText()));
		 result.add(new AnSTNameable(anArrayElementAST, aFullIdent.getText()));

		 if (anArrayElementExpression.getNextSibling() == null)
			 break;
		 anArrayElementExpression = anArrayElementExpression.getNextSibling().getNextSibling();
	 }
	 return result;
}
protected List emptyArrayList = new ArrayList();

public void maybeVisitTypeTags(DetailAST ast) { 
	if (typeTagsInitialized) return;
	typeTagsInitialized = true;
	if (ast == null) {
//		System.err.println("Null ast in maybeVisitTypeTags");
		return;
	}
	DetailAST annotationAST = AnnotationUtil.getAnnotation(ast, "Tags");		
	if (annotationAST == null) {
		typeTags = emptyArrayList;		
//		return;
	} else {
	typeTags = getArrayLiterals(annotationAST);
	}
	if (typeTags.size() > 0) {
	  
	  computedTypeTags = emptyNameableList;
	  return; // do not compute tags if explicit tags provided
	}
	computedTypeTags = new ArrayList(typeTags);
	
	if (typeNameable == null) {
		String aName = getName(ast);
		typeNameable = new AnSTNameable(aName);
	}
		
	computedTypeTags.add(typeNameable);
	computedTypeTags.add(toShortPatternName(typeNameable));
	if (structurePattern != null) {
		computedTypeTags.add(structurePattern);
		computedTypeTags.add(toShortPatternName(structurePattern));
	}
}

//public void maybeVisitPattern(DetailAST ast) { 
//	if (patternInitialized) return;
//	patternInitialized = true;
//	DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "StructurePattern");		
//	if (annotationAST == null) {
//		typeTags = emptyArrayList;
//		return;
//	}
//	typeTags = getArrayLiterals(annotationAST);
//}
static protected List<STNameable> emptyNameables = new ArrayList();
public static List<STNameable> getExplicitTags(DetailAST ast) {
	DetailAST annotationAST = AnnotationUtil.getAnnotation(ast, "Tags");	
	if (annotationAST == null) {
		emptyNameableList.clear(); // it may have been changed by compuyted and derived tags
		return emptyNameableList;
	}
	return getArrayLiterals(annotationAST); // allocating new list and reusing old list in 
}
public void maybeVisitMethodTags(DetailAST ast) {  
	DetailAST annotationAST = AnnotationUtil.getAnnotation(ast, "Tags");		
	if (annotationAST == null) {
		currentMethodTags.clear();;
		return;
	}
	currentMethodTags = getArrayLiterals(annotationAST);
}

private Map<String, String> importShortToLongName = new HashMap();
public static boolean hasVariableNameSyntax(String aName) {
	return !aName.contains(".") && aName.length() > 0 && !Character.isUpperCase(aName.charAt(0));
}

protected String toLongTypeNameNoArray (String aShortOrLongName) {
//	String retVal = aShortName;
	if (aShortOrLongName == null || aShortOrLongName.isEmpty()) {
		return aShortOrLongName;
	}
	if (aShortOrLongName.equals("void") ) {
		return aShortOrLongName;
	}

	if (aShortOrLongName.contains(".") || aShortOrLongName.equals("super") ) {
		return aShortOrLongName;
	}
//	int aSubscriptStart = aShortOrLongName.indexOf("[");
//	String aSubscriptSuffix = "";
//	String aShortOrLongNameWithoutSuffix = aShortOrLongName;
//	if (aSubscriptStart >= -1) {
//		aShortOrLongNameWithoutSuffix = aShortOrLongName.substring(0, aSubscriptStart);
//		aSubscriptSuffix = aShortOrLongName.substring(aSubscriptStart);
//	}
	String aPossibleLongName = importShortToLongName.get(aShortOrLongName);
	if (aPossibleLongName != null) {
		return aPossibleLongName;
	}
	if (isJavaLangClass(aShortOrLongName)) {
		
		return aShortOrLongName;
	}
	if (packageName != null && !packageName.isEmpty() && !hasVariableNameSyntax(aShortOrLongName)) {
		return packageName + "." + aShortOrLongName;
	}
	 
	return  aShortOrLongName;
}
//protected String toLongTypeName (String aShortOrLongName) {
//	String aTypeParameterName = toNormalizedTypeParameterName(aShortOrLongName);
////	String retVal = aShortName;
//	
//	int aSubscriptStart = aShortOrLongName.indexOf("[");
//	String aSubscriptSuffix = "";
//	String aShortOrLongNameWithoutSuffix = aShortOrLongName;
//	if (aSubscriptStart >= 0) {
//		aShortOrLongNameWithoutSuffix = aShortOrLongName.substring(0, aSubscriptStart);
//		aSubscriptSuffix = aShortOrLongName.substring(aSubscriptStart);
//	}
//	String aNameWithoutSuffix = toLongTypeNameNoArray(aShortOrLongNameWithoutSuffix);
//	return aNameWithoutSuffix + aSubscriptSuffix;
//
//}
public static String toElementTypeName(String aShortOrLongName) {
	int aSubscriptStart = aShortOrLongName.indexOf("[");
	if (aSubscriptStart < 0) {
		return aShortOrLongName;
	}
	return aShortOrLongName.substring(0, aSubscriptStart);
}
public static boolean isFieldDeclaredIn (STType aType, String aName) {
	STNameable[] aFields = aType.getDeclaredFields();
	if (aFields == null) {
		return false;
	}
	for (STNameable aField:aFields) {
		if (aField.getName().equals(aName)) {
			return true;
		}
	}
	return false;
}
//public static boolean isCalledMethodDeclaredIn (STType aType, CallInfo aCallInfo) {
//	STMethod[] aMethods = aType.getDeclaredMethods();
//	
//	for (STMethod aMethod:aMethods) {
//		if (aCallInfo.getCallee().equals(aMethod.getName()) &&
//				aCallInfo.getActuals().size() == aMethod.getParameterNames().length) {
//			return true;
//		}
//		if (aCallInfo.getActuals().size() != aMethod.getParameterNames().length) {
//			continue;
//		}
//		if (aField.getName().equals(aName)) {
//			return true;
//		}
//	}
//	return false;
//}
public static String toLongVariableName (STNameable aNameable, String aShortOrLongName) {
	STType aType = SymbolTableFactory.getSymbolTable().getSTClassByFullName(aNameable.getName());
	if (aType == null) {
		return null;
	}
	if (isFieldDeclaredIn(aType, aShortOrLongName)) {
		return aType.getName() + "." + aShortOrLongName;
	}
	return null;
	
}

public static String toLongVariableName (STType aType, String aShortOrLongName) {
//	String retVal = aShortName;
	if (aType == null || aShortOrLongName.contains(".")) {
		return aShortOrLongName;
	}
//	if (isFieldDeclaredIn(aType, aShortOrLongName)) {
//		return aType.getName() + "." + aShortOrLongName;
//	}
	List<STNameable> aTypes = aType.getAllTypes(); 
	if (aTypes == null) {
		return aShortOrLongName;
	}
//	if (aTypes != null) {
	for (STNameable aSuperType:aTypes) {
		String retVal = toLongVariableName(aSuperType, aShortOrLongName) ;
		if (retVal != null)
			return retVal;
	}
//	}
	return aShortOrLongName;	
	
}
//protected String[] toLongTypeNames (String[] aShortNames) {
//	String[] retVal = new String[aShortNames.length];
//	for (int i=0; i < aShortNames.length; i++) {
//		retVal[i] = toLongTypeName(aShortNames[i]);
//	}
//	return retVal;
//	
//}
public void visitImport(DetailAST ast) {
	 FullIdent anImport = FullIdent.createFullIdentBelow(ast);
	 String aLongClassName = anImport.getText();
	 String aShortClassName = findLastDescendentOfFirstChild(ast).getText();
	 if (aShortClassName != null && !aShortClassName.isEmpty()) {
			importShortToLongName.put(aShortClassName, aLongClassName);
			}
//   if (aLongClassName.contains("com.netflix.template.common.Conversation")) {

//   if (aLongClassName.contains("Conversation")) {
//     
//   
//     System.err.println("found offending class");
//   }
	 STNameable anSTNameable = new AnSTNameable(ast, aLongClassName);
	 allImportsOfThisClass.add(anSTNameable);
	 if (!isProjectImport(aLongClassName)) {
//		 allProjectExternalImports.add(aShortClassName);
		
		 allProjectExternalImports.add(aLongClassName);
		 if (!aShortClassName.equals("*"))
		 allProjectExternalImportsShortName.add(aShortClassName);

	 } else {
		 allProjectExternalImports.remove(aLongClassName);
		 if (!aShortClassName.equals("*"))
			 allProjectExternalImportsShortName.remove(aShortClassName);
	 }
}

public static boolean isMaybeProjectImport (String aPackageName, String aClassName) {
  
	String[] aClassNameComponents = aClassName.split("\\.");
	// if it does not begin with com, org or edu should we say we do not care about it?
	// instructor provided pakages may not follow it
	if (aClassNameComponents.length <= 2) { // default package or one package name
		return true;
	}
	String aClassNamePrefix = "";
	for (int i = 0; i < aClassNameComponents.length -1; i++) {
		if (i > 0) {
			aClassNamePrefix += ".";
		}
		aClassNamePrefix += aClassNameComponents[i];
		if (aClassNamePrefix.equals("main") || aClassNamePrefix.equals("com") || aClassNamePrefix.equals("org") || (aClassNamePrefix.equals("edu"))) {
			break;
		}
		if (aPackageName.startsWith(aClassNamePrefix)) {
			return true;
		}
	}
//	if (aPackageNames.l)
	return false;
}

//public static boolean isProjectImportCaching(String aFullName) {
//	boolean retVal = isProjectImport(aFullName);
//	if (retVal) {
//		allProjectExternalImports.add(aFullName);
//	}
//	return retVal;
//}
public static String toShortTypeOrVariableName (String aTypeName) {
	if (aTypeName == null)
		return aTypeName;
	int aDotIndex = aTypeName.lastIndexOf(".");
	String aShortTypeName = aTypeName;
	if (aDotIndex != -1)
		aShortTypeName = aTypeName.substring(aDotIndex + 1);
	if (aShortTypeName.startsWith("*"))  // special kludgy handling of .*
	  aShortTypeName = aTypeName;
	return aShortTypeName;
}

public static  STType fromVariableToSTType(String aVariableName) {
	String aTypeName = fromVariableToTypeName(aVariableName);
	if (aTypeName == null) {
		return null;
	}
//	STType retVal = SymbolTableFactory.getSymbolTable().getSTClassByShortName(aTypeName);
	 STType retVal = SymbolTableFactory.getSymbolTable().getSTClassByFullName(aTypeName);
	return retVal;
}

public static String fromVariableToTypeName(String aVariableName) {
	if (aVariableName == null)
		return null;
	int aDotIndex = aVariableName.lastIndexOf(".");
	String aRetVal = aVariableName;
	if (aDotIndex != -1)
		aRetVal = aRetVal.substring(0, aDotIndex);
	return aRetVal;
}
public static boolean isExternalType(String aFullName) {
	return isExternalTypeElement(toElementTypeName(aFullName));
}
/**
 * Related is isExternalClass,w hich takes short name
 *
 */
public static boolean isExternalTypeElement(String aFullName) {
//	if (TagBasedCheck.isProjectImport(aFullName)) {
//		return false;
//	}
//	if (isExternalClass(toShortTypeOrVariableName(aFullName))) return true;
	 if (isExternalClass(aFullName)) return true;

	
	for (String aString:STBuilderCheck.getExternalTypeRegularExpressions()) {
		if (aFullName.matches(aString)) {
			return true;
		}
	}
	if (TagBasedCheck.isProjectImport(aFullName)) {
		return false;
	}
	return true;
}

public static boolean isExplicitlyTagged (STType anSTType) {
	return (anSTType != null && anSTType.getTags() != null) && (anSTType.getTags().length > 0 || anSTType.getConfiguredTags().length > 0);
}

/**
 * Returns the first one found, perhaps should return a set
 * @param anSTType
 * @return
 */
public static STType findTaggedSubtype (STType anSTType) {
	boolean isSelfTagged = isExplicitlyTagged(anSTType);
	if (isSelfTagged) {
		return anSTType;
	}
	Set<STType> aSubTypes = anSTType.getSubSTTypes();
	STType retVal;
	for (STType aSubType:aSubTypes) {
		retVal = findTaggedSubtype(aSubType);
		if (retVal != null) {
			return retVal;
		}
	}
	return null;
}
public static Boolean isExplicitlyTagged (String aFullName) {
//	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aFullName);
	 STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aFullName);

	if (anSTType == null) {
		return false;
	}
	return isExplicitlyTagged(anSTType);
}
public static String[] toStrings(STNameable[] anSTNameables) {
	String[] retVal = new String[anSTNameables.length];
	for (int i = 0; i < anSTNameables.length; i++) {
		retVal[i] = anSTNameables[i].getName();
	}
	return retVal;
}
public static String[] toNormalizedTypes(String[] aTypes) {
	String[] retVal = new String[aTypes.length];
	for (int anIndex = 0; anIndex < aTypes.length; anIndex++) {
		retVal[anIndex] = toNormalizedType(aTypes[anIndex]);		
	}
	return retVal;
}
public static String toNormalizedType(String aType) {
  if (aType.equals("void")) {
    return aType;
  }
	if (isExternalType(aType))  {
		return aType;
	}
	STType anSTType = SymbolTableFactory.getSymbolTable().getSTClassByFullName(aType);
	if (anSTType == null) {
		return aType; // not an internal type
	}
	Set<String> aTags = getNonComputedTags(anSTType);
	if (aTags.size() == 1) {
		return aTags.iterator().next();
	}
//	return ".*";
	return MATCH_ANYTHING_REGULAR_EXPERSSION;

	
}
public static Set<String> getNonComputedTags (STType anSTType) {
	Set<String> retVal = new HashSet();
	
	retVal.addAll(Arrays.asList(toStrings(anSTType.getDerivedTags())));
	retVal.addAll(Arrays.asList(toStrings(anSTType.getConfiguredTags())));
	retVal.addAll(Arrays.asList(toStrings(anSTType.getTags())));
	return retVal;


}
public static List<String> getNonComputedTagsList (STType anSTType) {
	List<String> retVal = new ArrayList();
	
	retVal.addAll(Arrays.asList(toStrings(anSTType.getDerivedTags())));
	retVal.addAll(Arrays.asList(toStrings(anSTType.getConfiguredTags())));
	retVal.addAll(Arrays.asList(toStrings(anSTType.getTags())));
	return retVal;


}
public static STNameable[] getAllTagsAsArray (STType anSTType) {
  getAllTags(anSTType);
  return anSTType.getAllTags();
  
}
public static Set<STNameable> getAllTags (STType anSTType) {
  Set<STNameable> retVal = new HashSet();
  if (anSTType == null) {
    return retVal;
  }
  if (anSTType.getAllTagsSet() != null) {
    return anSTType.getAllTagsSet();
  }
  if (anSTType.getDerivedTags() != null) {
  retVal.addAll(Arrays.asList(anSTType.getDerivedTags()));
  }
  if (anSTType.getComputedTags() != null ) {
  retVal.addAll(Arrays.asList(anSTType.getConfiguredTags()));
  }
  if (anSTType.getTags() != null) {
  retVal.addAll(Arrays.asList(anSTType.getTags()));
  }
  if (anSTType.getComputedTags() != null) {
  retVal.addAll(Arrays.asList(anSTType.getComputedTags()));
  }
  anSTType.setAllTagsSet(retVal);
  return retVal;


}
public static Set<String> tagsNotContainedIn(STType aContainee, STType aContainer ) {
	Set<String> aContaineeSet = getNonComputedTags(aContainee);
	Set<String> aContainerSet = getNonComputedTags(aContainer);
	aContainerSet.removeAll(aContaineeSet);
	return aContainerSet;
}
/**
 * 
 *External prefixes gets priority over  internal
 */
public static boolean isProjectImport(String aFullName) {
	for (String aString:STBuilderCheck.getExternalPackagePrefixes()) {
		if (aFullName.startsWith(aString)) {
//			externalImports.add(aFullName);
			return false;
		}
	}
	for (String aString:STBuilderCheck.getBuiltInExternalPackagePrefixes()) {
		if (aFullName.startsWith(aString)) {
//			externalImports.add(aFullName);
			return false;
		}
	}
	 for (String aPrefix:STBuilderCheck.getProjectPackagePrefixes()) {
		 if (/*aPrefix.equals("*") ||*/ aFullName.startsWith(aPrefix)) return true;
		 if (aFullName.matches(aPrefix)) return true;
	 }
//	for (String aString:STBuilderCheck.getExternalPackagePrefixes()) {
//		if (aFullName.startsWith(aString)) {
////			externalImports.add(aFullName);
//			return false;
//		}
//	}
	 if (STBuilderCheck.isNonInteractive() || !STBuilderCheck.isDoAutoPassChange()) {
     return true;
   }
	 Set<String> aPackageNames = SymbolTableFactory.getOrCreateSymbolTable().getPackageNames();
	 for (String aPackageName:aPackageNames) {
		 if (isMaybeProjectImport(aPackageName, aFullName))
			 return true;
	 }
	 
	 return false;
}
static List<DetailAST> emptyASTList = new ArrayList();
public static DetailAST getFirstInOrderMatchingNode(DetailAST ast, List<Integer> aType  ) {
	return findFirstInOrderUnmatchedMatchingNode(ast, aType, emptyASTList);
//	if (ast.getType() == aType)
//		return ast;
////	if (ast.getChildCount() > 0) return null;
//	DetailAST aResult = null;
//	DetailAST aChild = ast.getFirstChild();
//	while (true) {
//		if (aChild == null)
//			return null;
//		aResult = getFirstInOrderMatchingNode(aChild, aType);
//		if (aResult != null)
//			return aResult;
//		aChild = aChild.getNextSibling();		
//	}
}
public static DetailAST findFirstContainingNode(DetailAST ast, List<Integer> aTypes) {
//	if (aast.getType() == aType &&
	if (ast == null) {
		return null;
	}
	if (aTypes.contains(ast.getType())) {
		return ast;
	}
//	if (ast.getChildCount() > 0) return null;
//	DetailAST aResult = null;
	return findFirstContainingNode(ast.getParent(), aTypes);
}
// look up one level in case after that there is nothing
// if there is then return the next one
public static DetailAST findLastContainingNode(DetailAST ast, List<Integer> aTypes) {
	DetailAST nextLevel = findFirstContainingNode(ast, aTypes);
	if (nextLevel == null)
		return null;
	DetailAST lastLevel = findLastContainingNode(nextLevel.getParent(), aTypes);
	if (lastLevel == null)
		return nextLevel;
	return lastLevel;
}
	

public static DetailAST findFirstInOrderUnmatchedMatchingNode(DetailAST ast, List<Integer> aTypes, List<DetailAST> aMatchedNodes  ) {
//	if (aast.getType() == aType &&
	if (aTypes.contains(ast.getType()) &&

			!aMatchedNodes.contains(ast)) {
		aMatchedNodes.add(ast);
		return ast;
	}
//	if (ast.getChildCount() > 0) return null;
	DetailAST aResult = null;
	DetailAST aChild = ast.getFirstChild();
	while (true) {
		if (aChild == null)
			return null;
		aResult = findFirstInOrderUnmatchedMatchingNode(aChild, aTypes, aMatchedNodes);
		if (aResult != null)
			return aResult;
//		if (aResult != noAST && 
//				!aMatchedNodes.contains(aResult)) {
//			aMatchedNodes.add(aResult);
//			return aResult;
//		}
		aChild = aChild.getNextSibling();		
	}
}
public static DetailAST findFirstInOrderMatchingNode(DetailAST ast, List<Integer> aTypes ) {
	return findFirstInOrderUnmatchedMatchingNode(ast, aTypes, new ArrayList());
}
public static DetailAST findFirstInOrderMatchingNode(DetailAST ast, int aType ) {
	return findFirstInOrderMatchingNode(ast, Arrays.asList(new Integer[] {aType}));
}

public static DetailAST findFirstInOrderUnmatchedNode(DetailAST ast, List<Integer> aTypes, List<DetailAST> aMatchedNodes  ) {
//	if (aast.getType() == aType &&
	if (aTypes.contains(ast.getType()) &&

			!aMatchedNodes.contains(ast)) {
		aMatchedNodes.add(ast);
		return ast;
	}
//	if (ast.getChildCount() > 0) return null;
	DetailAST aResult = null;
	DetailAST aChild = ast.getFirstChild();
	while (true) {
		if (aChild == null)
			return null;
		aResult = findFirstInOrderUnmatchedMatchingNode(aChild, aTypes, aMatchedNodes);
		if (aResult != null)
			return aResult;
//		if (aResult != noAST && 
//				!aMatchedNodes.contains(aResult)) {
//			aMatchedNodes.add(aResult);
//			return aResult;
//		}
		aChild = aChild.getNextSibling();		
	}
}
public static DetailAST findFirstInOrderMatchingNodeAfter(DetailAST ast, List<Integer> aType  ) {
	// first see if someone below our sibling succeeds
	DetailAST aNextSibiling = ast.getNextSibling();
	if (aNextSibiling != null) {
		DetailAST result = getFirstInOrderMatchingNode(aNextSibiling, aType);
		if (result != null) {
			return result;
		}
	}
	// get the first node after our parent, who has not not been visited
	DetailAST aParent = ast.getParent();
	if (aParent == null) {
		return noAST;
	}
	return findFirstInOrderMatchingNodeAfter(aParent, aType);
}
public static List<DetailAST>  findAllInOrderMatchingNodes(DetailAST ast, int aType ) {
	 List<DetailAST> result = new ArrayList();
	fillAllInOrderMatchingNodes(ast, aType, result);
	return result;

}
public static void fillAllInOrderMatchingNodes(DetailAST ast, int aType, List<DetailAST>  aList ) {
	if (ast.getType() == aType)
		aList.add(ast);
	DetailAST aChild = ast.getFirstChild();
	while (true) {
		if (aChild == null)
			return;
		fillAllInOrderMatchingNodes(aChild, aType, aList);		
		aChild = aChild.getNextSibling();		
	}
}
public static DetailAST findLastDescendentOfFirstChild(DetailAST ast) {
	return findLastDescendent(ast.getFirstChild());
//	DetailAST result = ast.getFirstChild();
//	while (result.getChildCount() > 0)
//		result = result.getLastChild();    	
//	return result;    	
}
public static DetailAST findLastDescendent(DetailAST ast) {
	DetailAST result = ast;
	while (result != null && result.getChildCount() > 0)
		result = result.getLastChild();    	
	return result;    	
}
public void visitStaticImport(DetailAST ast) {
	 DetailAST anImportAST = ast.getFirstChild().getNextSibling();
	 FullIdent anImport = FullIdent.createFullIdent(
               anImportAST);
	 STNameable anSTNameable = new AnSTNameable(ast, anImport.getText());
	 allImportsOfThisClass.add(anSTNameable);
}
public static boolean isArray(String aShortClassName) {
	 return aShortClassName.endsWith("[]");
}
public void doBeginTree(DetailAST ast) {  
	 super.doBeginTree(ast);
	 	
	 	typeTags = emptyNameableList;
	 	computedTypeTags = emptyNameableList;
//	 	typeScope.clear();
	 	// not sure why we need this as 
	 	setFullTypeName(null);
//	 	fullTypeName = null;
//	 	isInterface = false;
//	 	isGeneric = false;
//	 	isElaboration = false;
////	 	stMethods.clear();
//	 	imports.clear();
//	 	globalVariables.clear();
	 	currentTree = ast;
	 	typeTagsInitialized = false;
//	 	maybeCleanUpPendingChecks(ast);
//		pendingChecks().clear();
   }
public static DetailAST getExpression (DetailAST anIfAST) {
	return anIfAST.getFirstChild().getNextSibling();
}
public static DetailAST getThenPart (DetailAST anIfAST) {
	return getExpression(anIfAST).getNextSibling().getNextSibling();
}
public static DetailAST getElsePart (DetailAST anIfAST) {
	DetailAST aThenPart = getThenPart(anIfAST);
	DetailAST aThenSibling = aThenPart.getNextSibling();
	if (aThenSibling == null || aThenSibling.getType() != TokenTypes.LITERAL_ELSE) {
		return null;
	}
	return aThenPart.getNextSibling().getFirstChild();
}
public static DetailAST getEnclosingMethodDeclaration(DetailAST anAST) {
	return getEnclosingOrRightTokenType(anAST, TokenTypes.METHOD_DEF);
}



public static DetailAST getEnclosingClassDeclaration(DetailAST anAST) {
	return getEnclosingOrRightTokenType(anAST, TokenTypes.CLASS_DEF);
}
public static DetailAST getEnclosingPackageDeclaration(DetailAST anAST) {
	return getEnclosingOrLeftTokenType(anAST, TokenTypes.PACKAGE_DEF);
}
public static DetailAST getEnclosingInterfaceDeclaration(DetailAST anAST) {
	return getEnclosingOrRightTokenType(anAST, TokenTypes.INTERFACE_DEF);
}
public static DetailAST getEnclosingTreeDeclaration(DetailAST anAST) {
	DetailAST root = anAST;
	if (root == null) {
	  return null;
	}
	while (true) {		
		DetailAST aParent = root.getParent();
		if (aParent == null)
			break;
		root = aParent;
	}
	while (true) {
		if (root.getType() == TokenTypes.PACKAGE_DEF) {
			break;
		}
		DetailAST aLeftSibling = root.getPreviousSibling();
		if (aLeftSibling == null)
			break;
		root = aLeftSibling;
	}
	return root;
}
public static DetailAST getEnclosingEnumDeclaration(DetailAST anAST) {
	DetailAST root = anAST;
	while (true) {
		if (root.getType() == TokenTypes.ENUM)
			return anAST;
		DetailAST aParent = root.getParent();
		if (aParent == null)
			break;
		root = aParent;
	}
	
//	DetailAST result = root.findFirstToken(TokenTypes.ENUM);
	DetailAST anEnumDef = root;
	while (true)  {
		if (anEnumDef == null) {
			break;
		}
		if (anEnumDef.getType() == TokenTypes.ENUM_DEF) 
			break;
		anEnumDef = anEnumDef.getNextSibling();
	}
	return anEnumDef;
//	return anEnumDef.getFirstChild().getNextSibling();
//	return getEnumNameAST(anEnumAST);
}
public static DetailAST getEnclosingAnnotationDeclaration(DetailAST anAST) {
	DetailAST root = anAST;
	while (true) {
		if (root.getType() == TokenTypes.ANNOTATION_DEF)
			return anAST;
		DetailAST aParent = root.getParent();
		if (aParent == null)
			break;
		root = aParent;
	}
//	DetailAST result = root.findFirstToken(TokenTypes.ENUM);
	DetailAST anAnnotationDef = root;
	while (true)  {
		if (anAnnotationDef == null) {
			break;
		}
		if (anAnnotationDef.getType() == TokenTypes.ANNOTATION_DEF) 
			break;
		anAnnotationDef = anAnnotationDef.getNextSibling();
	}
	return anAnnotationDef;
//	return anEnumDef.getFirstChild().getNextSibling();
//	return getEnumNameAST(anEnumAST);
}
public static String getFullTypeName(DetailAST aTree) {
	String aTypeName = getName(getEnclosingTypeDeclaration(aTree));
	if (aTypeName == null) {
    System.err.println("Could not get class name from ast");
    return null;
  }
//	int i = 1;
	DetailAST aPackageAST = getEnclosingPackageDeclaration(aTree);
	String aPackageName = DEFAULT_PACKAGE;
	if (aPackageAST != null)
		 aPackageName = getPackageName(aPackageAST);
	return aPackageName + "." + aTypeName;
}
public  STType getSTType(DetailAST aTreeAST) {
//	int i = 1;
	
	String aFullName = getFullTypeName();
	if (aFullName == null) {
		aFullName = getFullTypeName(aTreeAST);
	}
	if (aFullName == null) {
	  System.err.println("Could not get class name for file:" + currentFullFileName);
	}
//	STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aFullName);
//	if (anSTType == null) {
//		System.out.println("Null symbol table entry for:" + aFullName);
//	}
//	return anSTType;
	STType result = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aFullName);
	if (result == null && ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
		result = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(toShortTypeOrVariableName(aFullName));
		
	}
	return result;
	
}
public static final int OBSERVED_IMPORT_TYPE = 152;
public static DetailAST getOutermostTypeDeclaration (DetailAST ast) {
	DetailAST anEnclosingType = getEnclosingTypeDeclaration(ast);
	if (anEnclosingType == null) {
		return null; // this should never happen on first call
	}
	DetailAST aPreviousAST = anEnclosingType.getPreviousSibling(); 
	if (aPreviousAST == null) {
		return anEnclosingType;
	}
	int aPreviousASTType = aPreviousAST.getType();
	if (aPreviousAST == null || aPreviousAST.getType() == OBSERVED_IMPORT_TYPE || aPreviousAST.getType() == TokenTypes.IMPORT || aPreviousAST.getType() == TokenTypes.PACKAGE_DEF) // just check if it is class def?
		return anEnclosingType; // this means no package
	DetailAST anAncestorType = getOutermostTypeDeclaration(aPreviousAST); // keep going left till you find one
	if (anAncestorType == null) {
		DetailAST aParentAST = anEnclosingType.getParent();
		if (aParentAST == null)
			return anEnclosingType;
		anAncestorType = getOutermostOrEnclosingTypeDeclaration(aParentAST);
	}
	if (anAncestorType == null)
		return anEnclosingType;
	return anAncestorType;
//	DetailAST aParent = ast.getParent();
//	if (aParent == null) {
//		// this must be the type, but still et us call getenclosing
//		return getEnclosingTypeDeclaration(ast);
//	}
//	DetailAST aParentType = getEnclosingTypeDeclaration(aParent);
//	if (aParentType == null)
//		return getEnclosingTypeDeclaration(ast); // topMost
//	// we have a type here, let us try its parent
//	return getOutermostTypeDeclaration(aParentType); //go to parent and try again
}
public static DetailAST getOutermostOrEnclosingTypeDeclaration(DetailAST ast) {
	if (ProjectSTBuilderHolder.getSTBuilder().visitInnerClasses)
		return getEnclosingTypeDeclaration(ast);
	return getOutermostTypeDeclaration(ast);
}
public static String getOutermostOrEnclosingShortTypeName(DetailAST ast) {
	return getName(getOutermostOrEnclosingTypeDeclaration(ast));
}
public static DetailAST getEnclosingTypeDeclaration(DetailAST anAST) {
//	DetailAST result = null;
	if (anAST == null) {
//		System.err.println ("Null AST:" + anAST);
		return null;
	}
	if (anAST.getType() == TokenTypes.INTERFACE_DEF) {
		return getEnclosingInterfaceDeclaration(anAST);
	}
	if (anAST.getType() == TokenTypes.ENUM_DEF) {
		return getEnclosingEnumDeclaration(anAST);
	}
	if (anAST.getType() == TokenTypes.CLASS_DEF) {
		return getEnclosingClassDeclaration(anAST);
	}
	if (anAST.getType() == TokenTypes.ANNOTATION_DEF) {
		return getEnclosingAnnotationDeclaration(anAST);
	}
	DetailAST result =		getEnclosingClassDeclaration(anAST);
	if (result != null)
		return result;
	result = getEnclosingInterfaceDeclaration(anAST);
	if (result != null)
		return result;
	result = getEnclosingEnumDeclaration(anAST);
	if (result != null)
		return result;
	result = getEnclosingAnnotationDeclaration(anAST);
	if (result != null)
    return result;
	
//	System.err.println(" could not find a type declaration for:" + anAST + "" + anAST.getText());
	return result;
	
//		return aClassDef;
//	if (aClassDef == null)
//		return getEnclosingInterfaceDeclaration(anAST);
//	else
//		return aClassDef;
}
public static String getEnclosingShortClassName(DetailAST anAST) {
	return getName(getEnclosingClassDeclaration(anAST));
}
public static String getEnclosingShortTypeName(DetailAST anAST) {
	return getName(getEnclosingTypeDeclaration(anAST));
}
public static String getEnclosingMethodName(DetailAST anAST) {
	return getName(getEnclosingMethodDeclaration(anAST));
}
// not physically but logically enclosing
public static DetailAST getEnclosingOrRightTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	DetailAST aParent = anAST.getParent();
	if (aParent != null)
	   return getEnclosingOrRightTokenType(aParent, aTokenType);
	return 
			getFirstRightSiblingTokenType(anAST, aTokenType);
//			getFirstLeftSiblingTokenType(anAST, aTokenType);

}
public static DetailAST getEnclosingOrLeftTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	DetailAST aParent = anAST.getParent();
	if (aParent != null)
	   return getEnclosingOrRightTokenType(aParent, aTokenType);
	return 
			getFirstLeftSiblingTokenType(anAST, aTokenType);
//			getFirstLeftSiblingTokenType(anAST, aTokenType);

}
//public static DetailAST getFirstRightSiblingTokenType(DetailAST anAST, int aTokenType) {
//	if (anAST == null) return null;
//	if (anAST.getType() == aTokenType) return anAST;
//	return getFirstRightSiblingTokenType(anAST.getNextSibling(), aTokenType);
//	
//}
public static DetailAST getFirstLeftSiblingTokenType(DetailAST anAST, int aTokenType) {
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	return getFirstLeftSiblingTokenType(anAST.getPreviousSibling(), aTokenType);
	
}
public static DetailAST getFirstRightSiblingTokenType(DetailAST anAST, int aTokenType) {
  if (anAST != null && anAST.getType() != TokenTypes.IMPORT) {
//    System.err.println ("found non import of type:" + anAST.getType());
  }
	if (anAST == null) return null;
	if (anAST.getType() == aTokenType) return anAST;
	return getFirstRightSiblingTokenType(anAST.getNextSibling(), aTokenType);
	
}
protected void setIntValueOfType(String newVal) {
	String[] aTypeAndValue = newVal.split (TYPE_SEPARATOR);
	String aType = MATCH_ANYTHING;
	String aValueString = "";
	if (aTypeAndValue.length == 1) {
		aValueString = aTypeAndValue[0];
		
	} else if (aTypeAndValue.length == 2){
		aType = aTypeAndValue[0];
		aValueString = aTypeAndValue[1];			
	}
	try {				
		int aValue = Integer.parseInt(aValueString);
		typeToInt.put(aType, aValue);

	} catch (Exception e) {
		System.err.println ("Did not get int type value");
		e.printStackTrace();
	}	
}
public static boolean isPrimitive(List<String> aTypes) {
	return aTypes.size() == 1 && isPrimitive(aTypes.get(0));
}

public static boolean isShape(String aType) {
	return aType.equals(PointPatternCheck.POINT_PATTERN) ||
			aType.equals(LinePatternCheck.LINE_PATTERN) ||
			aType.equals(OvalPatternCheck.OVAL_PATTERN) ||
			aType.equals(RectanglePatternCheck.RECTANGLE_PATTERN) ||
			aType.equals(StringShapePatternCheck.STRING_PATTERN) ||
			aType.equals(ImagePatternCheck.IMAGE_PATTERN);
}
public static boolean isShape(List<String> aTypes) {
	if (isPrimitive(aTypes)) return false;
	for (String aType:aTypes) {
		if (isShape(aType)) return true;
	}
	return false;
}
public static boolean isPrimitive(String aType) {
	return primitiveTypesSet.contains(aType);
}
public static boolean isOEAtomic(String aType) {
	return aType.equals("String") || isPrimitive(aType);
}

protected boolean hasSingleType(String[] aStrings) {
	return aStrings.length > 0 && !aStrings[0].contains(TYPE_SEPARATOR);
}
protected Map<String, String[]> typeToStrings = new HashMap<>();
protected String[] strings;
protected void setExpectedStrings(String[] aPatterns) {
//	if (aPatterns.length == 0) {
//		return;
//	}
//	if (!aPatterns[0].contains(TYPE_SEPARATOR)) {
//		properties = aPatterns;
//		return;
//	}
	if (hasSingleType(aPatterns)) {
//		strings = aPatterns;
		setStrings(aPatterns);
		return;
	}
	for (String aPattern : aPatterns) {
		setExpectedStringsOfType(aPattern);
	}

}
protected void setStrings(String[] aStrings) {
	strings = aStrings;	
}
public void setExpectedStringsOfType(String aPattern) {
//	String[] extractTypeAndProperties = aPattern.split(TYPE_SEPARATOR);
//	String aType = extractTypeAndProperties[0].trim();
//	String[] aProperties = extractTypeAndProperties[1].split(TagBasedCheck.SET_MEMBER_SEPARATOR);
//
//	typeToProperty.put(aType, aProperties);
	setExpectedStringsOfType(typeToStrings, aPattern);
	
}
protected void setExpectedStringsOfType(Map<String, String[]> aTypeToStrings, String aType, String[] aStrings) {
	aTypeToStrings.put(aType, aStrings);
}

protected void setExpectedStringsOfType(Map<String, String[]> aTypeToStrings, String aPattern) {
	String[] extractedTypeAndStrings = aPattern.split(TYPE_SEPARATOR);
	String aType = extractedTypeAndStrings[0].trim();
//	String[] aProperties = extractTypeAndProperties[1].split("\\|");
	String[] aStrings = extractedTypeAndStrings[1].split(TagBasedCheck.SET_MEMBER_SEPARATOR);
	setExpectedStringsOfType(aTypeToStrings, aType, aStrings);
//	aTypeToProperty.put(aType, aProperties);
}
protected  String[] getStringArrayToBeChecked(STType anSTType, Map<String, String[]> aMap, String[] aStrings ){
	String[] aSpecifiedProperties = aStrings;
	if (aSpecifiedProperties == null) {

		String aSpecifiedType = findMatchingType(aMap.keySet(), anSTType);
		if (aSpecifiedType == null)
			return aSpecifiedProperties; // the constraint does not apply to us

		aSpecifiedProperties = aMap.get(aSpecifiedType);
	}
	return aSpecifiedProperties;
}
protected  String[] getStringArrayToBeChecked(DetailAST anAST, DetailAST aTree){
	STType anSTType = getSTType(aTree);
	if (anSTType == null) {
		System.err.println("ST Type is null!");
//		System.err.println("Symboltable names" + SymbolTableFactory.getOrCreateSymbolTable().getAllTypeNames());
		 return null; // this was commented out
	}
	if (anSTType.isEnum() || anSTType.isInterface() || anSTType.isAnnotation()) // why duplicate
														// checking for
														// interfaces
		return null;
	String[] retVal = strings;
	if (retVal == null) {

		String aSpecifiedType = findMatchingType(typeToStrings.keySet(), anSTType);
		if (aSpecifiedType == null)
			return retVal; // the constraint does not apply to us

		retVal = typeToStrings.get(aSpecifiedType);
	}
	return retVal;
}
protected  Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {
	return true;
}
 protected boolean doCheck(STType anSTType) {
	return true;
}
 public static String toTypeOrTag(STType anSTType) {
   String aMatchedTags = anSTType.getMatchedTags();
   String aTypeOrTag = aMatchedTags == null ?anSTType.getName():aMatchedTags;
   return aTypeOrTag;
 }

public Boolean doStringArrayBasedPendingCheck(DetailAST anAST, DetailAST aTree) {
	STType anSTType = null;
	if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
		anSTType = getSTType(anAST);
	} else {
		anSTType =getSTType(aTree);
	}
//	STType anSTType = getSTType(aTree);
	if (anSTType == null) {
		System.err.println("ST Type is null!");
		System.err.println("Symboltable names" + SymbolTableFactory.getOrCreateSymbolTable().getAllTypeNames());
		 return true; // this was commented out
	}
	if (anSTType.isEnum() || anSTType.isInterface() || anSTType.isAnnotation()) // why duplicate
														// checking for
														// interfaces
		return true;
	if (!doCheck(anSTType)) {
		return true;
	}
	String[] aStrings = strings;
	Boolean retVal = true;
	if (aStrings != null) {
		return processStrings(anAST, aTree, anSTType, null, aStrings);
	}
//	if (aStrings == null) {

//		String aSpecifiedType = findMatchingType(typeToStrings.keySet(), anSTType);
		List<String> aSpecifiedTypes = findMatchingTypes(typeToStrings.keySet(),
				anSTType);
		if (aSpecifiedTypes == null) {
			return true;
		}
//		Boolean retVal = true;
		for (String aSpecifiedType:aSpecifiedTypes) {

			aStrings = typeToStrings.get(aSpecifiedType);
			if (aStrings == null) {
				retVal = true;
				continue;
			}
			Boolean aCurrentMatch = processStrings(anAST, aTree, anSTType, aSpecifiedType, aStrings);
			if (retVal != null) {
				retVal = aCurrentMatch == null ? null : aCurrentMatch && retVal;
			}
		}
//	}
//	if (aStrings == null) {
//		return true;
//	}
//	return processStrings(anAST, aTree, anSTType, aStrings);
	return retVal;
}


 static {
 	javaLangTypesSet = new HashSet();
 	primitiveTypesSet = new HashSet();
 	for (String aClass:javaLangTypes)
 		javaLangTypesSet.add(aClass);
 	for (String aPrimitive:primitiveTypes)
 		primitiveTypesSet.add(aPrimitive);
 	javaLangTypesSet.addAll(primitiveTypesSet);
// 	for (String aPrimitive:primitiveTypes)
// 		javaLangTypesSet.add(aPrimitive);
 }
 
// public static void main (String[] args) {
//	 System.out.println("String".matches(".*\\[\\]"));
// }
}
