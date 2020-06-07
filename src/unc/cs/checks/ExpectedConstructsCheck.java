package unc.cs.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unc.cs.parseTree.ACallOperation;
import unc.cs.parseTree.AMethodParseTree;
import unc.cs.parseTree.AMethodSpecification;
import unc.cs.parseTree.AMethodStrings;
import unc.cs.parseTree.AReturnOperation;
import unc.cs.parseTree.AnIndependentNodes;
import unc.cs.parseTree.AnIFStatement;
import unc.cs.parseTree.AtomicOperation;
import unc.cs.parseTree.CallOperation;
import unc.cs.parseTree.IFStatement;
import unc.cs.parseTree.Body;
import unc.cs.parseTree.MethodParseTree;
import unc.cs.parseTree.CheckedNode;
import unc.cs.parseTree.MethodStrings;
import unc.cs.parseTree.TransitiveOperation;
import unc.cs.parseTree.TreeSpecificationParser;
import unc.cs.symbolTable.CallInfo;
import unc.cs.symbolTable.STMethod;
import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ExpectedConstructsCheck extends ConstructsCheck {
	public static final String MSG_KEY = "expectedConstructs";
//	protected Map<String, MethodStrings> specificationToMethodStrings = new HashMap();
//	static protected Map<String, List<Integer>> constructNamesToCodes = new HashMap();
//	static {
//		constructNamesToCodes.put(TreeSpecificationParser.IF, Arrays.asList(new Integer[] {TokenTypes.LITERAL_IF}));
//		constructNamesToCodes.put(TreeSpecificationParser.CALL, Arrays.asList(new Integer[] {TokenTypes.METHOD_CALL}));
//		constructNamesToCodes.put(TreeSpecificationParser.FOR, Arrays.asList(new Integer[] {TokenTypes.LITERAL_FOR}));
//
//	}
//	
//	@Override
//	protected String msgKey() {
//		return MSG_KEY;
//	}
//	public int[] getDefaultTokens() {
//		return new int[] { };
//
//	}
//	
//	protected void registerSpecifications (String aType, String[] aSpecifications) {
//		super.registerSpecifications(aType, aSpecifications);
//		for (String aSpecification:aSpecifications) {
//			MethodStrings aMethodStrings = toMethodStrings(aSpecification);
//			specificationToMethodStrings.put(aSpecification, aMethodStrings);
//		}
//	}
//	public static MethodStrings toMethodStrings (String aSpecification) {
//		String[] aMethodAndStrings = aSpecification.split(MethodCallCheck.CALLER_TYPE_SEPARATOR);
//		String aStringsSpecification = null;
//		String aMethodSpecification = null;
//		STMethod anSTMethod = null;
//
//		if (aMethodAndStrings.length == 2) {
//			aMethodSpecification = aMethodAndStrings[0].trim();
//			anSTMethod = signatureToMethod(aMethodSpecification);
//			aStringsSpecification = maybeStripComment(aMethodAndStrings[1]);			
//		} else {
//			aStringsSpecification = maybeStripComment(aMethodAndStrings[0]);
//		}
//		String[] aStrings = aStringsSpecification.split(" ");
//		for (int index = 0; index < aStrings.length; index++) {
//			aStrings[index] = aStrings[index].trim();
//		}
//		return new AMethodStrings(anSTMethod, aStrings);
//		
////		return new AMethodParseTree(anSTMethod, aParseTreeSpecification);
//	}
//		
//	public void setExpectedConstructs(String[] aPatterns) {
//		setExpectedTypesAndSpecifications(aPatterns);
//	}
	
//	protected void logConstructNotMatched(DetailAST aTreeAST, DetailAST aSpecificAST, String aSpecification,
//			String aType, String aKeyword) {
//		log (aTreeAST, aTreeAST, aSpecification, aType, aKeyword);
//
//	}
	protected void constructMatched(DetailAST aTreeAST, String aSpecification,
			String aType, String aKeyword) {
//		log (aTreeAST, aTreeAST, aSpecification, aType, aKeyword);

	}
	protected void constructNotMatched(DetailAST aSpecificAST, String aSpecification,
			String aType, String aKeyword) {
		logConstructNotMatched(aSpecificAST, aSpecification, aType, aKeyword);
	}
	
//	public  Boolean matchConstruct(List<STMethod> anSTMethods, String aConstruct, List<DetailAST> aMatchedNodes) {
//		boolean foundNull = false;
//		
//		for (STMethod anSTMethod:anSTMethods) {
//			Boolean aMatch = matchConstruct(anSTMethod, anSTMethod.getAST(), aConstruct, aMatchedNodes);
//			if (aMatch == null) {
//				foundNull = true;
//				
//			} else if (aMatch) {
//				return true;
//			}
//		}
//		if (foundNull)
//			return null;
//		
//		return false;		
//	}
//
//	public static Boolean matchAtomicOperation(STMethod aMethod,
//			DetailAST anAST, AtomicOperation anAtomicOperation, List<DetailAST> aMatchedNodes) {
//		DetailAST aMatchingNode =  getFirstInOrderUnmatchedMatchingNode(anAST, anAtomicOperation.getTokenTypes(), aMatchedNodes);
//		
//		return (aMatchingNode != null) ;
////		return false
//	}
//	public static Boolean matchTransitiveOperation(STMethod aMethod,
//			DetailAST anAST, TransitiveOperation aTransitiveOperation, List<DetailAST> aMatchedNodes) {
//		DetailAST aMatchingNode =  getFirstInOrderUnmatchedMatchingNode(anAST, aTransitiveOperation.getTokenTypes(), aMatchedNodes);
//		if (aMatchingNode == null) {
//			return false;
//		}
//		DetailAST anOperand = aMatchingNode.getNextSibling();
//		
//		String anOperandText = anOperand.toStringTree();
//		return anOperandText.matches(aTransitiveOperation.getOperand());
//		
//		
////		return false;
//	}
//	
//	
//	
//	public  Boolean matchCall(STMethod aMethod,
//			DetailAST anAST, CallOperation aCallOperation, List<DetailAST> aMatchedNodes) {
//		CallInfo[] aCalls = aMethod.getMethodsCalled();
//		Boolean foundNull = false;	
//
//		for (CallInfo aCallInfo:aCalls) {
//			if (aMatchedNodes.contains(aCallInfo.getAST())) {
//				continue;
//			}
////			STMethod aSpecifiedMethod = aCallOperation.getMethod();
//			String aSpecification = aCallOperation.getOperand();
////			List<STMethod> anActualMethods = aCallInfo.getMatchingCalledMethods();
////			if (anActualMethods == null) {
////				return null;
////			}
////			int i = 0;
////			for (STMethod anSTMethod:anActualMethods) {
//				String aNormalizedLongName = ComprehensiveVisitCheck.toLongName(aCallInfo.getNormalizedCall());
//				String shortMethodName = ComprehensiveVisitCheck.toShortTypeName(aNormalizedLongName);
//				Boolean aMatch = matches(aMethod.getDeclaringSTType(), maybeStripComment(aSpecification), shortMethodName, aNormalizedLongName, aCallInfo);
//				if (aMatch == null) {
//					foundNull = true;
//				
//				} 
//				else if (aMatch) {
//					aMatchedNodes.add(aCallInfo.getAST());
//					return true;
//				}
//			}					
////		}
//		
//		if (foundNull)
//			return null;
//		return false;		
//		
////		return false;
//	}
//	public  Boolean matchMethodBody(STMethod aMethod, DetailAST anAST,
//		Body aBody) {
//		String aNormalizedBody = substituteParameters(aBody.getOperand(), aMethod);
//		String aBodyText = anAST.toStringTree();
//		return aBodyText.matches(aNormalizedBody);
//		
//		
////	Boolean aMatch = matches(aMethod.getDeclaringSTType(), maybeStripComment(aSpecification), shortMethodName, aNormalizedLongName, aCallInfo);
//				
//		
//		
//		
////		return false;
//	}
//	
//	public  Boolean matchConstructs(String aType, String aSpecification, STMethod aMethod, DetailAST anAST, String[] aConstructs, List<DetailAST> aMatchedNodes) {
//		Boolean foundNull = false;
//		Boolean aMatch = true;		
//		for (String aConstruct: aConstructs) {
//			aMatch = matchConstruct(aMethod, anAST, aConstruct, aMatchedNodes);
//			if (aMatch == null) {
//				foundNull = true;
//			}
//			else if (!aMatch) {
//				logConstructNotMatched(anAST, aSpecification, aType, aConstruct);
//				aMatch = false;
//			}
//		}
//		if (foundNull)
//			return null;
//		return aMatch;		
//	}
//	
//	public  Boolean matchConstruct(STMethod aMethod, DetailAST anAST, String aConstruct, List<DetailAST> aMatchedNodes) {
//		List<Integer> aTypes = constructNamesToCodes.get(aConstruct.toLowerCase());
//		
//		if (aTypes == null) {
//			System.err.println("Unexpected construct name:" + aConstruct);
//			return false;
//		}
////		List<Integer> aTokenTypes = Arrays.asList(new Integer[] {aType});
//		DetailAST aMatchingNode =  getFirstInOrderUnmatchedMatchingNode(anAST, aTypes, aMatchedNodes);
//		return (aMatchingNode != null) ;
//			
////		String aStringTree = anAST.toStringTree();
////		String aStringList = anAST.toStringList();
////		St
//		
//	}
////	public static Boolean matchParseTree(DetailAST anAST, 
////			CheckedStatement aStatement, 
////			List<DetailAST> aMatchedASTs) {
////		if (aStatement instanceof AStatementNodes) {
////			return matchNodes(anAST, aStatement, aMatchedASTs) != null;			
////		} else if (aStatement instanceof AReturnOperation) {
////			return matchAtomicOperation(anAST, (AtomicOperation) aStatement, aMatchedASTs );
////		}
////		return false;
////		
////	}
//// this should be integrated with matchParseTree into a matchSpecification that calls abstract methods
//	public Boolean matchConstructs(STType anSTType, String[] aSpecifications) {
//		Boolean foundNull = false;
//		boolean retVal = true;
//		Boolean aMatch = null;
//	
//		for (String aSpecification : aSpecifications) {
//			MethodStrings aMethodStrings = specificationToMethodStrings
//					.get(aSpecification);
//			STMethod aSpecifiedMethod = aMethodStrings.getMethod();
//			String[] aKeywords = aMethodStrings.getSpecifications();
//			if (aSpecifiedMethod == null) {
//				List<DetailAST> aMatchedNodes = new ArrayList();
//				aMatch= matchConstructs(toShortTypeName(anSTType.getName()), aSpecification, null, anSTType.getAST(), aKeywords, aMatchedNodes);
//				if (aMatch == null) {
//					foundNull = true;
//					continue;
//				}
//				if (!aMatch) {
//					retVal = false;
//					continue;
//				}
//			} else {
//				List<STMethod> aMatchingMethods = getMatchingMethods(anSTType,
//						aSpecifiedMethod);
//				if (aMatchingMethods == null)
//					return true;
//				if (aMatchingMethods.size() == 0) {
//					return false;
//				}
//				boolean specificationMatched = false;
////				retVal = false; // set to true if any method matched
//				for (STMethod anSTMethod : aMatchingMethods) {
//					List<DetailAST> aMatchedNodes = new ArrayList();
//
//					aMatch = matchConstructs(toShortTypeName(anSTType.getName()), aSpecification, anSTMethod, anSTMethod.getAST(), aKeywords, aMatchedNodes);
//					if (aMatch == null) {
//						foundNull = true;
//						continue;
//					}
//					if (aMatch) {
//						specificationMatched = true;
////						retVal = true;
//						break;
//						
//					}
//
//				}
//				if (!specificationMatched && !foundNull) {
//					retVal = false;
//				}
//				
//
//			}
//		}
//		if (foundNull)
//			return null;
//		return retVal;
//	}
//	
//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
////		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
////				.getSTClassByShortName(
////						getName(getEnclosingTypeDeclaration(aTree)));
//		STType anSTType = getSTType(aTree);
//		if (anSTType == null) {
//			System.out.println("ST Type is null!");
//			System.out.println("Symboltable names" + SymbolTableFactory.getOrCreateSymbolTable().getAllTypeNames());
////			return true;
//		}
//		if (anSTType.isEnum() ||
//				anSTType.isInterface()) // why duplicate checking for interfaces
//			return true;
//		String aSpecifiedType = findMatchingType(typeToSpecifications.keySet(),
//				anSTType);
//		if (aSpecifiedType == null)
//			return true; // the constraint does not apply to us
//		
//		
//		String[] aSpecifications = typeToSpecifications.get(aSpecifiedType);
//
//		return matchConstructs(anSTType, aSpecifications);
//	}
//
//	@Override
//	
//
//	public void doFinishTree(DetailAST ast) {
//		
//		maybeAddToPendingTypeChecks(ast);
//		super.doFinishTree(ast);
//
//	}

	@Override
	protected boolean returnValueOnMatch() {
		// TODO Auto-generated method stub
		return true;
	}

}
