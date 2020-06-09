package unc.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STMethod;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;

public class MinCalledMethodsCheck extends ComprehensiveVisitCheck {
	
	public static final String MSG_KEY = "minCalledMethods";	

//    private int max = 30;
    private int minCalledMethods = 2;

    public MinCalledMethodsCheck() {
    	
    }
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF };
    }

    public void setMinCalledMethods(int newVal) {
        minCalledMethods = newVal;
    }

//    public void doVisitToken(DetailAST ast) {
//////    	System.out.println("Check called:" + msgKey());
////        // find the OBJBLOCK node below the CLASS_DEF/INTERFACE_DEF
////        DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
////        // count the number of direct children of the OBJBLOCK
////        // that are METHOD_DEFS
////        int methodDefs = objBlock.getChildCount(TokenTypes.METHOD_DEF);
////        // report error if limit is reached
////        if (methodDefs < minCalledMethods) {
//////            log(ast.getLineNo(), msgKey(), min);
////            log(ast, ast, methodDefs, minCalledMethods);
////        }
//    }
    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
    public void visitType(DetailAST ast) {
    	super.visitType(ast);    	
    	STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
    			getSTClassByFullName(getFullTypeName());
    	if (anSTClass == null) {
    		System.err.println("No ST Clas for:" + getFullTypeName());
    		return;
    	}
    	STMethod[] anSTMethods = anSTClass.getDeclaredMethods();
    	int aMaxMethods = 0;
    	for (STMethod aMethod:anSTMethods) {
    		aMaxMethods = Math.max(aMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods().size(), aMaxMethods);   		
    	}
    	if (aMaxMethods < minCalledMethods) {
//    		if (getFullTypeName().contains("Cell")) {
//    			System.err.println("Found inner class:");
//    		}
//          log(ast.getLineNo(), msgKey(), min);
          log(ast, currentTree, aMaxMethods, minCalledMethods);
        }
    

    }
}
