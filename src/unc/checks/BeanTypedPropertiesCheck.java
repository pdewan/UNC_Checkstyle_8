package unc.checks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.PropertyInfo;
import unc.symbolTable.STMethod;
import unc.symbolTable.STType;

public abstract class BeanTypedPropertiesCheck extends BeanPropertiesCheck {
	public static final String MSG_KEY = "beanProperties";

//	protected Map<String, String[]> typeToStrings = new HashMap<>();
//	protected String[] strings;
//	public static final String SEPARATOR = ">";

//	public void doVisitToken(DetailAST ast) {
//		// System.out.println("Check called:" + MSG_KEY);
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF:
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//		case TokenTypes.INTERFACE_DEF:
//			visitType(ast);
//			return;
//		default:
//			System.err.println("Unexpected token");
//		}
//	}
//
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] { TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
//				TokenTypes.PACKAGE_DEF };
//	}

//	public void setExpectedPropertiesOfType(String aPattern) {
////		String[] extractTypeAndProperties = aPattern.split(TYPE_SEPARATOR);
////		String aType = extractTypeAndProperties[0].trim();
////		String[] aProperties = extractTypeAndProperties[1].split(TagBasedCheck.SET_MEMBER_SEPARATOR);
////
////		typeToProperty.put(aType, aProperties);
//		setExpectedPropertiesOfType(typeToStrings, aPattern);
//		
//	}
//	public void setExpectedPropertiesOfType(Map<String, String[]> aTypeToProperty, String aPattern) {
//		String[] extractedTypeAndProperties = aPattern.split(TYPE_SEPARATOR);
//		String aType = extractedTypeAndProperties[0].trim();
////		String[] aProperties = extractTypeAndProperties[1].split("\\|");
//		String[] aProperties = extractedTypeAndProperties[1].split(TagBasedCheck.SET_MEMBER_SEPARATOR);
//
//		aTypeToProperty.put(aType, aProperties);
//	}
//	
//	protected boolean hasSingleType(String[] aStrings) {
//		return aStrings.length > 0 && aStrings[0].contains(TYPE_SEPARATOR);
//	}

	/*
	 * @StructurePatternNames.LinePattern> X:int | Y:int | Width:int
	 * |Height:int,
	 * 
	 * @StructurePatternNames.OvalPatetrn> X:int | Y:int | Width:int |Height:int
	 */
	public void setExpectedProperties(String[] aPatterns) {
		super.setExpectedStrings(aPatterns);
//		if (hasSingleType(aPatterns)) {
//			strings = aPatterns;
//			return;
//		}
//		for (String aPattern : aPatterns) {
//			setExpectedPropertiesOfType(aPattern);
//		}

	}

	// public void visitType(DetailAST ast) {
	// super.visitType(ast);
	// }
	// public void doVisitToken(DetailAST ast) {
	// // System.out.println("Check called:" + MSG_KEY);
	// switch (ast.getType()) {
	// case TokenTypes.PACKAGE_DEF:
	// visitPackage(ast);
	// return;
	// case TokenTypes.CLASS_DEF:
	// visitType(ast);
	// return;
	// case TokenTypes.INTERFACE_DEF:
	// visitType(ast);
	// return;
	// default:
	// System.err.println("Unexpected token");
	// }
	// }

	// public static Boolean matchType (Set<String> aSpecifiedTypes, STType
	// anSTType) {
	// for (String aSpecifiedType:aSpecifiedTypes) {
	// checkTagsOfType(aSpecifiedType, anSTType);
	// }
	// }
	protected void logPropertyNotMatched(DetailAST aTreeAST, String aProperty,
			String aType) {
//		System.out.println ("Bean:" + aType + " " + aProperty);
		log (aTreeAST, aTreeAST, aProperty, aType);
//		String aSourceName = shortFileName(astToFileContents.get(aTreeAST)
//				.getFilename());
//		if (aTreeAST == currentTree) {
//			DetailAST aLoggedAST = aTreeAST;
//
//			aLoggedAST = aTreeAST;
//
//			log (aLoggedAST.getLineNo(), msgKey(), aProperty, aType, aSourceName);
//		} else {
//			log(0, msgKey(), aProperty, aType, aSourceName);
//		}

	}

	public Boolean matchProperties(String[] aSpecifiedProperties,
			Map<String, PropertyInfo> aPropertyInfos, DetailAST aTreeAST) {
		boolean retVal = true;
		for (String aSpecifiedProperty : aSpecifiedProperties) {
			String[] aPropertyAndType = aSpecifiedProperty.split(":");
			String aType = aPropertyAndType[1].trim();
			String aPropertySpecification = aPropertyAndType[0].trim();
//			String[] aPropertiesPath = aPropertySpecification.split(".");
			Boolean matched = matchProperty(maybeStripComment(aType), maybeStripComment(aPropertySpecification), aPropertyInfos);
			if (matched ==null) {
				return null;
			}
//			if (!matchProperty(aType, aPropertySpecification, aPropertyInfos)) {
			if (!matched) {

				logPropertyNotMatched(aTreeAST, aPropertySpecification, aType);
				retVal = false;
			}
		}
		return retVal;
	}
	public Boolean matchProperties(String[] aSpecifiedProperties,
			Collection <PropertyInfo> aPropertyInfos, DetailAST aTreeAST) {
		boolean retVal = true;
		for (String aSpecifiedProperty : aSpecifiedProperties) {
			String[] aPropertyAndType = aSpecifiedProperty.split(":");
			String aType = aPropertyAndType[1].trim();
			String aPropertySpecification = aPropertyAndType[0].trim();
//			String[] aPropertiesPath = aPropertySpecification.split(".");			
			if (!matchProperty(aType, aPropertySpecification, aPropertyInfos)) {
				logPropertyNotMatched(aTreeAST, aPropertySpecification, aType);
				retVal = false;
			}
		}
		return retVal;
	}
	public List<PropertyInfo> matchedOrUnMatchedProperties(List<String> aSpecifiedProperties,
			Collection <PropertyInfo> aPropertyInfos, boolean aDoMatch) {
		List<PropertyInfo> retVal = new ArrayList();
		for (PropertyInfo aProperty : aPropertyInfos) {
		
//			String[] aPropertiesPath = aPropertySpecification.split(".");	
			Boolean aMatched = matchProperty(aSpecifiedProperties, aProperty);
			if (aMatched && aDoMatch || !aMatched && !aDoMatch) {
				retVal.add(aProperty);
			}
		}
		return retVal;
	}

	public Boolean matchProperty(String aSpecifiedType,
			String aSpecifiedProperty, Map<String, PropertyInfo> aPropertyInfos) {
		for (String aProperty : aPropertyInfos.keySet()) {
			if (aSpecifiedProperty.equalsIgnoreCase(aProperty)) {
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				Boolean retVal = matchType(aSpecifiedType, aProperty, aPropertyInfos);
				// may want to match a property against many specifications as in ClearableHistory
//
//				if (retVal != null && retVal) {
////					aPropertyInfos.remove(aProperty);
//				}
				return retVal;
			}

		}
		return false;
	}
	public Boolean matchProperty(String aSpecifiedType,
			String aSpecifiedProperty, Collection<PropertyInfo> aPropertyInfos) {
		for (PropertyInfo aProperty : aPropertyInfos) {
			if (unifyingMatchesNameVariableOrTag(aSpecifiedProperty, aProperty.getName(), null))
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return matchType(aSpecifiedType, aProperty);

			else
				continue;
		}
		return false;
	}
	public Boolean matchProperty (String aSpecification, PropertyInfo aProperty) {
		String[] aPropertyAndType = aSpecification.split(":");
		String aTypeSpecification = aPropertyAndType[1].trim();
		String aPropertySpecification = aPropertyAndType[0].trim();
		return unifyingMatchesNameVariableOrTag(aTypeSpecification, aProperty.getType(), null) &&
				unifyingMatchesNameVariableOrTag(aPropertySpecification, aProperty.getName(), null);
	}
	public Boolean matchProperty(
			List<String> aSpecifications, PropertyInfo aProperty) {
		for (String aSpecification : aSpecifications) {
			
			if (matchProperty(aSpecification, aProperty))
				// return
				// aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty).getGetter().getReturnType());
				return true;

			else 
				continue;
		}
		return false;
	}

//	public Boolean matchType(String aSpecifiedType, String aProperty,
//			Map<String, PropertyInfo> aPropertyInfos) {
//
//		return matchGetter(aSpecifiedType, aProperty, aPropertyInfos);
//
//	}
	
	public  Boolean matchType(String aSpecifiedType, String aProperty,
			Map<String, PropertyInfo> aPropertyInfos) {
		return matchType(aSpecifiedType, aPropertyInfos.get(aProperty) );
	}
	public   Boolean matchType(String aSpecifiedType, PropertyInfo aProperty) {
		return unifyingMatchesNameVariableOrTag(aSpecifiedType, aProperty.getName(), null);
	}
		
	

	public Boolean matchGetter(String aSpecifiedType, String aProperty,
			Map<String, PropertyInfo> aPropertyInfos) {
		// yuk, sometimes using a different method
		return super.matchesTypeUnifying(aSpecifiedType, aPropertyInfos.get(aProperty)
				.getGetter().getReturnType());
//		return aSpecifiedType.equalsIgnoreCase(aPropertyInfos.get(aProperty)
//				.getGetter().getReturnType());

	}
	public Boolean matchGetter(String aSpecifiedType, PropertyInfo aProperty) {
		// yuk, sometimes using a different method
//		return super.matchesType(aSpecifiedType, aProperty
//				.getGetter().getReturnType());
		return super.matchesTypeUnifying(aSpecifiedType, aProperty.getType());


	}

	// public boolean match(String aSpecifiedType, String aSpecifiedPoperty,
	// Map<String, PropertyInfo> aPropertyInfos) {
	// return matchGetter(aSpecifiedType, aSpecifiedPoperty, aPropertyInfos);
	// }
	public Boolean matchSetter(String aSpecifiedType, String aProperty,
			Map<String, PropertyInfo> aPropertyInfos) {
		STMethod aSetter = aPropertyInfos.get(aProperty)
		.getSetter();		
		Boolean matches = matchesTypeUnifying(aSpecifiedType, aSetter.getParameterTypes()[0]);
		if (matches == null)
			return null;

		return aSetter != null && 
//				aSpecifiedType.equalsIgnoreCase(aSetter.getParameterTypes()[0]);
				matches;
//				 matchesType(aSpecifiedType, aSetter.getParameterTypes()[0]);

	}
	public Boolean matchSetter(String aSpecifiedType, PropertyInfo aProperty) {
		STMethod aSetter = aProperty
		.getSetter();	
		if (aSetter == null || aSetter.getParameterTypes() == null) {
			return false;
		}
		Boolean matches = matchesTypeUnifying(aSpecifiedType, aSetter.getParameterTypes()[0]);
		if (matches == null)
			return null;

		return aSetter != null && 
//				aSpecifiedType.equalsIgnoreCase(aSetter.getParameterTypes()[0]);
//				 matchesType(aSpecifiedType, aSetter.getParameterTypes()[0]);
				matches;
		

	}

//	protected  String[] getStringArrayToBeChecked(STType anSTType, Map<String, String[]> aMap, String[] aStrings ){
//		String[] aSpecifiedProperties = aStrings;
//		if (aSpecifiedProperties == null) {
//
//			String aSpecifiedType = findMatchingType(aMap.keySet(), anSTType);
//			if (aSpecifiedType == null)
//				return aSpecifiedProperties; // the constraint does not apply to us
//
//			aSpecifiedProperties = aMap.get(aSpecifiedType);
//		}
//		return aSpecifiedProperties;
//	}
	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
		return super.doStringArrayBasedPendingCheck(anAST, aTree);
		
////		STType anSTType = getSTType(aTree);
////		if (anSTType == null) {
////			System.out.println("ST Type is null!");
////			System.out.println("Symboltable names" + SymbolTableFactory.getOrCreateSymbolTable().getAllTypeNames());
////			 return true; // this was commented out
////		}
////		if (anSTType.isEnum() || anSTType.isInterface()) // why duplicate
////															// checking for
////															// interfaces
////			return true;
////
////		String[] aSpecifiedProperties = getStringArrayToBeChecked(anSTType, typeToStrings, strings);
//		String[] aSpecifiedProperties = getStringArrayToBeChecked(anAST, aTree);
//		if (aSpecifiedProperties == null) {
//			return true;
//		}
//		
////		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();
//		Map<String, PropertyInfo> aPropertyInfos = stTypeToBeChecked.getPropertyInfos();
//
//		if (aPropertyInfos == null)
//			return null;
//
//		Map<String, PropertyInfo> anUmmatchedPropertyInfos = new HashMap(aPropertyInfos);
//		return matchProperties(aSpecifiedProperties, anUmmatchedPropertyInfos, aTree);
	}
	@Override
	protected Boolean processStrings(DetailAST anAST, DetailAST aTree, STType anSTType, String aSpecifiedType, String[] aStrings) {
		Map<String, PropertyInfo> aPropertyInfos = anSTType.getPropertyInfos();

		if (aPropertyInfos == null)
			return null;

		Map<String, PropertyInfo> anUmmatchedPropertyInfos = new HashMap(aPropertyInfos);
		return matchProperties(aStrings, anUmmatchedPropertyInfos, aTree);
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}

//	public void doFinishTree(DetailAST ast) {
//		// STType anSTType =
//		// SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//		// for (STMethod aMethod: anSTType.getMethods()) {
//		// visitMethod(anSTType, aMethod);
//		// }
//		maybeAddToPendingTypeChecks(ast);
//		super.doFinishTree(ast);
//
//	}
}
