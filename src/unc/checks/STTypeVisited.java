package unc.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;

public abstract class STTypeVisited extends ComprehensiveVisitCheck {
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.ENUM_DEF, TokenTypes.ANNOTATION_DEF};
	} 

	public STTypeVisited() {

	}
	
	protected void checkSTType(DetailAST ast, STType anSTClass) {
		if (!typeCheck(anSTClass))
    		super.logType(ast);
	}
	protected abstract  boolean typeCheck(STType anSTClass) ;

	public void visitType(DetailAST ast) {
    	super.visitType(ast);
    	
    	STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
    			getSTClassByFullName(getFullTypeName());
    	checkSTType(ast, anSTClass);
//    	if (!typeCheck(anSTClass))
//    		super.logType(ast);

    }
//	@Override
//  protected void doLeaveToken(DetailAST ast) {
//    // TODO Auto-generated method stub
//    if (leavingSpuriousInnerClass(ast)) {
//      return;
//    } 
//    
//  }
//	protected void leaveMethodOrConstructor(DetailAST methodDef) {
//    inMethodOrConstructor = false;
////    System.err.println("Not in method");
////    resetMethodOrConstructor(methodDef);
//  }
	
}
