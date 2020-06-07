package unc.cs.checks;

import java.util.List;

import unc.cs.symbolTable.STType;

public class InterfaceHasProjectSuperType extends STClassVisitedComprehensively {

	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY_INFO = "interfaceHasProjectSuperType";
	public static final String MSG_KEY_WARNING = "classDoesNotHaveProjectSuperClass";



//	/** flag to control whether marker interfaces are allowed. */
//	private boolean allowMarkerInterfaces = true;
//	public int[] getDefaultTokens() {
//		return new int[] {
//				TokenTypes.CLASS_DEF, 
//				TokenTypes.INTERFACE_DEF, 
//				TokenTypes.PACKAGE_DEF
//				};
//	}

	
	public InterfaceHasProjectSuperType() {

	}
	
	protected
	boolean doCheck(STType anSTType)  {
    	return anSTType.isInterface();
    }
	protected Boolean typeCheck(STType anSTType) {
		List<String> aSuperTypes = anSTType.getSuperTypeNames();
		if (aSuperTypes == null || aSuperTypes.size() == 0) {
			return false;
		}
		for (String aSuperType:aSuperTypes) {
			if (!TagBasedCheck.isExternalClass(aSuperType)) {
				return true;
			}
		}
		return false;		

	}
	
//	protected void log(DetailAST ast) {
//		log(ast.getLineNo(), msgKey(), typeName);
//	}
//	public void visitType(DetailAST ast) {  
//
//    	super.visitType(ast);
////		log(ast.getLineNo(), MSG_KEY, typeName);
//    	STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
//    			getSTClassByFullName(typeName);
//    	if (!classCheck(anSTClass))
//    		log(ast);
//    	
//
//    }

//	public void visitToken(DetailAST ast) {		
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF: 
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//			visitType(ast);
//			return;		
//		default:
//			System.err.println("Unexpected token");
//		}
//		
//	}
//	@Override
//	protected String msgKey() {
//		// TODO Auto-generated method stub
//		return MSG_KEY;
//	}

//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] { TokenTypes.CLASS_DEF};
//	}

//	@Override
//	public int[] getRequiredTokens() {
//		return getDefaultTokens();
//	}
//
//	@Override
//	public int[] getAcceptableTokens() {
//		return new int[] { TokenTypes.CLASS_DEF };
//	}

//	boolean hasOneInterface(DetailAST aClassDef) {
//		
//		int numInterfaces = 0;
//		DetailAST implementsClause = aClassDef
//				.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
//
//		if (implementsClause == null)
//			return false;
//		DetailAST anImplementedInterface = implementsClause
//				.findFirstToken(TokenTypes.IDENT);
//		while (anImplementedInterface != null) {
//			if (anImplementedInterface.getType() == TokenTypes.IDENT)
//				numInterfaces++;
//			anImplementedInterface = anImplementedInterface.getNextSibling();
//		}
//		return numInterfaces == 1;
//	}

//	@Override
//	public void visitToken(DetailAST ast) {	
//    	System.out.println("Check called:" + MSG_KEY);
//		DetailAST classNameAST = ast.findFirstToken(TokenTypes.IDENT);
//    	String name = classNameAST.getText();
//    	System.out.println ("Visiting class:" + name);
//		final DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
//		if (hasOneInterface(ast)) {
//			return;
//		}
//
//		DetailAST methodDef = objBlock.findFirstToken(TokenTypes.METHOD_DEF);
//		while (methodDef != null) {
//			if (methodDef.getType() == TokenTypes.METHOD_DEF) // for some reason
//																// curly brace
//																// is last
//																// sibling {
//
//				// System.out.println("Checking:" + methodDef);
//				if (isPublicInstanceMethod(methodDef)) {
//					// System.out.println("Started Logging");
//
//					extendibleLog(ast.getLineNo(), MSG_KEY);
//					// System.out.println("Ended Logging");
//
//					return;
//				}
//
//			// System.out.println("Setting next sibling");
//
//			methodDef = methodDef.getNextSibling();
//
//			// System.out.println("Set next sibling");
//
//		}
//
//		// final DetailAST variableDef = objBlock
//		// .findFirstToken(TokenTypes.VARIABLE_DEF);
//		// final boolean methodRequired = !allowMarkerInterfaces
//		// || variableDef != null;
//		//
//		// if (methodDef == null && methodRequired) {
//		// log(ast.getLineNo(), MSG_KEY);
//		// }
//
//	}

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
}
