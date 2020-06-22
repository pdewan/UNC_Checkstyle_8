package unc.checks;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CheckUtil;

import unc.symbolTable.AnSTTypeFromClass;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;

public final class VariableHasClassTypeCheck extends ComprehensiveVisitCheck implements
		ContinuationProcessor {

	
	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY_WARNING = "variableHasClassType";
	public static final String MSG_KEY_INFO = "variableHasInterfaceType";

//	Map<DetailAST, List<DetailAST>> astToPendingChecks = new HashMap();
//	Map<DetailAST, FileContents> astToFileContents = new HashMap();
	// List<FullIdent> pendingTypeUses = new ArrayList();

	/*
	 * Actually ignored types not ncessary with deferred processing but reduces
	 * size of pending checkables
	 */
	static final String[] PREDEFINED_IGNORED_TYPES = { "void", "int", "double", "float",
			"boolean", "short", "char", "Integer", "Double", "Float",
			"Boolean", "Short", "Character", "String", "Scanner", "List",
			"HashSet", "Set" };

	final  Set<String> ignoreTypesSet = new HashSet();
	
	

	@Override
	public int[] getDefaultTokens() {
		return new int[] { 
				TokenTypes.VARIABLE_DEF, TokenTypes.PARAMETER_DEF, 
				TokenTypes.PACKAGE_DEF, 
				TokenTypes.CLASS_DEF,
				TokenTypes.METHOD_DEF,
				TokenTypes.IMPORT
				};
	}
	
	public VariableHasClassTypeCheck() {
		ContinuationNotifierFactory.getOrCreateSingleton()
				.addContinuationProcessor(this);
		ignoreTypesSet.addAll(Arrays.asList(PREDEFINED_IGNORED_TYPES));
		
	}

	
	public void setIgnoredTypes(String[] anIgnoredClasses) {
		ignoreTypesSet.addAll(Arrays.asList(anIgnoredClasses));
	
	}

//	@Override
//	public void doVisitToken(DetailAST ast) {
////		System.out.println("Check called:" + MSG_KEY);
//
//		switch (ast.getType()) {
//		case TokenTypes.METHOD_DEF:
//			visitMethod(ast);
//			break;
//		case TokenTypes.VARIABLE_DEF:
//			visitVariableDef(ast);
//			break;
//		case TokenTypes.PARAMETER_DEF:
//			visitParameterDef(ast);
//			break;
//
//		default:
//			throw new IllegalStateException(ast.toString());
//		}
//	}

//	@Override
//	protected String msgKey() {
//		// TODO Auto-generated method stub
//		return MSG_KEY;
//	}

	/**
	 * Checks return type of a given method.
	 * 
	 * @param methodDef
	 *            method for check.
	 */
	@Override
	public void visitMethod(DetailAST methodDef) {
		// if (isCheckedMethod(methodDef)) {
		maybeAddToPendingTypeChecks(methodDef);
		// }
	}

	/**
	 * Checks type of parameters.
	 * 
	 * @param paradef
	 *            parameter list for check.
	 */
	@Override
	public void visitParamDef(DetailAST paradef) {
		final DetailAST grandParentAST = paradef.getParent().getParent();
// why this condition?
		if (grandParentAST.getType() == TokenTypes.METHOD_DEF || grandParentAST.getType() == TokenTypes.CTOR_DEF )
		// && isCheckedMethod(grandParentAST)) {
		maybeAddToPendingTypeChecks(paradef);
		// }
	}

	/**
	 * Checks type of given variable.
	 * 
	 * @param variableDef
	 *            variable to check.
	 */
	@Override
	public void visitVariableDef(DetailAST variableDef) {
		super.visitVariableDef(variableDef);
		if (!checkIncludeExcludeTagsOfCurrentType())
			return;
		maybeAddToPendingTypeChecks(variableDef);
	}

	/**
	 * Checks type of given method, parameter or variable.
	 * 
	 * @param ast
	 *            node to check.
	 */
	protected void maybeAddToPendingTypeChecks(DetailAST ast) {
		final DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
//		final DetailAST anIdentifier = ast.findFirstToken(TokenTypes.IDENT);
		final FullIdent anIdentifierType = CheckUtil.createFullType(aType);
		if (ignoreTypesSet.contains(anIdentifierType.getText()))
			return;
		super.maybeAddToPendingTypeChecks(ast);
//		if (doPendingCheck(ast, currentTree) == null)
//			pendingChecks().add(ast);

		
	}

//	public static String shortFileName(String longName) {
//		int index = longName.lastIndexOf('/');
//		if (index <= 0)
//			index = longName.lastIndexOf('\\');
//		if (index <= 0)
//			return longName;
//		return longName.substring(index + 1);
//	}

	public static String toTypeName(DetailAST aTreeAST) {
		DetailAST aTypeDeclaration = aTreeAST
				.findFirstToken(TokenTypes.CLASS_DEF);
		DetailAST aTypeName = aTreeAST.findFirstToken(TokenTypes.IDENT);
		return aTypeName.getText();
	}

	/*
	 * check returns non null if check finished
	 */
//	public Boolean checkIdentifierType(DetailAST ast, DetailAST aTreeAST) {
//		final DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
//		final DetailAST anIdentifier = ast.findFirstToken(TokenTypes.IDENT);
//		final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
//		String aTypeName = anIdentifierType.getText();
//		if (!SymbolTableFactory.getOrCreateSymbolTable().isType(aTypeName))
//			return null;
//		if (isMatchingClassName(anIdentifierType.getText())) {
//			 String aSourceName =
//			 shortFileName(astToFileContents.get(aTreeAST).getFilename());
////			String aSourceName = toTypeName(aTreeAST);
//			log(anIdentifierType.getLineNo(), anIdentifierType.getColumnNo(),
//					msgKey(), anIdentifierType.getText(),
//					anIdentifier.getText(),
//					aSourceName + ":" + anIdentifier.getLineNo());
//			return true;
//		}
//		return false;
//	}
	protected final String[] tagInterfacesArray = {"Serializable", "Remote"};
	protected final Set<String> tagInterfaces = new HashSet (Arrays.asList(tagInterfacesArray));
	protected boolean includesOnlyTagInterface(List<STNameable> anInterfaces) {
		if (anInterfaces.size() != 1) {
			return false;
		}
		String anInterface = anInterfaces.get(0).getName();
		return tagInterfaces.contains(anInterface);
	}
	protected List<String> getNonTagIntefaces(List<STNameable> aNameableList) {
		List<String> retVal = new ArrayList();
		for (STNameable aNameable:aNameableList) {
			String aName = aNameable.getName();
			if (tagInterfaces.contains(aName)) {
				continue;
			}
			retVal.add(aName);
		}
		return retVal;
	}
	protected Boolean checkType(STType anSTClass) {
		if (anSTClass.isInterface())
			return true;
		List<STNameable> anAllInterfaces = anSTClass.getAllInterfaces();
		STNameable[] aDeclaredInterfaces = anSTClass.getDeclaredInterfaces();
		if (aDeclaredInterfaces == null) {
		  return true; // could not use interface as type
		}
		List<String> aNormalizedInterfaces = getNonTagIntefaces(Arrays.asList(aDeclaredInterfaces));
		if (aNormalizedInterfaces.size() != 0) {
			return false; 
		}
		
		
		STNameable aSuperType = anSTClass.getSuperClass();
		
		if (anAllInterfaces == null && aSuperType != null) { // has superclass, but we do not have its symbol table
			return null;
		}
		if (anAllInterfaces == null) { 
			System.err.println("all interfaces should not be null");
			return null;
		}
		aNormalizedInterfaces = getNonTagIntefaces(anAllInterfaces);
		
		return anAllInterfaces != null && 
				
				(aNormalizedInterfaces.size() == 0) ;
		
		// if it has no interface, then interface could not have been used
		
//		return anSTClass.isInterface();
	}
	@Override
	public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
		final DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
		final DetailAST anIdentifier = ast.findFirstToken(TokenTypes.IDENT);
		final FullIdent anIdentifierType = CheckUtil.createFullType(aType);
		String aTypeName = anIdentifierType.getText();
		String aLongTypeName = toLongTypeName(aTypeName);
//		STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
//				getSTClassByShortName(aLongTypeName);
		STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
		        getSTClassByFullName(aLongTypeName);
		
	
		if (anSTClass instanceof AnSTTypeFromClass)
			return true;
		if (anSTClass == null)
//		if (!SymbolTableFactory.getOrCreateSymbolTable().isType(aTypeName))
			return null;
		if (anSTClass.isEnum() || anSTClass.isAnnotation())
				//|| anSTClass.isInterface())
			return true;
		Boolean aTypeCheck = checkType(anSTClass);
		if (aTypeCheck == null)
			return null;
		
		boolean aDoLog = 
				isInfo()?
						aTypeCheck && anSTClass.isInterface():
						!aTypeCheck;
//		if (!aTypeCheck)
		if (aDoLog) {
    		
		
//		if (!checkType(anSTClass)) {

			log (anIdentifierType, aTreeAST, anIdentifierType.getText(),
					anIdentifier.getText());
			  

//			return true;
		}
		return false;
	}
	
	


//	public void doPendingChecks() {
//		// for (List<FullIdent> aPendingTypeUses:astToPendingTypeUses.values())
//		// {
//
//		for (DetailAST aPendingAST : astToPendingChecks.keySet()) {
//			List<DetailAST> aPendingChecks = astToPendingChecks.get(aPendingAST);
//			// FileContents aFileContents = astToFileContents.get(anAST);
//			// setFileContents(aFileContents);
//
//			if (aPendingChecks.isEmpty())
//				continue;
//			List<DetailAST> aPendingTypeChecksCopy = new ArrayList(
//					aPendingChecks);
//			for (DetailAST aPendingCheck : aPendingTypeChecksCopy) {
//				if (doPendingCheck(aPendingCheck, aPendingAST) != null)
//					aPendingChecks.remove(aPendingCheck);
//
//			}
//		}
//	}

	/**
	 * @param className
	 *            class name to check.
	 * @return true if given class name is one of illegal classes or if it
	 *         matches to abstract class names pattern.
	 */
	boolean isMatchingClassName(String className) {
		return SymbolTableFactory.getOrCreateSymbolTable().isClass(className);
	}
	protected String msgKeyWarning() {
		// TODO Auto-generated method stub
		return MSG_KEY_WARNING;
	}
	@Override
	protected String msgKeyInfo() {
		// TODO Auto-generated method stub
		return MSG_KEY_INFO;
	}
	protected String msgKey() {
		// TODO Auto-generated method stub
		return msgKeyWarning();
	}
//	@Override
//	public void processDeferredChecks() {
//		doPendingChecks();
//
//	}

//	List<DetailAST> pendingChecks() {
//		List<DetailAST> result = astToPendingChecks.get(currentTree);
//		if (result == null) {
//			result = new ArrayList<>();
//			astToPendingChecks.put(currentTree, result);
//			astToFileContents.put(currentTree, getFileContents());
//
//		}
//		return result;
//	}

//	DetailAST currentTree;

//	@Override
//	public void beginTree(DetailAST ast) {
////		System.out.println("ID =" + getId());
////		System.out.println("Message Bundle:" + getMessageBundle());
////		astToFileContents.put(ast, getFileContents());
////		currentTree = ast;
////		pendingChecks().clear();
//	}

//	@Override
//	public void finishTree(DetailAST ast) {
//		ContinuationNotifierFactory.getOrCreateSingleton()
//				.notifyContinuationProcessors();
//	}
//	

//	static {
//		for (String aType : PREDEFINED_IGNORED_TYPES) {
//			ignoreTypesSet.add(aType);
//		}
//	}

}
