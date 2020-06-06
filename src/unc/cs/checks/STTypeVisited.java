package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.cs.symbolTable.STType;
import unc.cs.symbolTable.SymbolTableFactory;

public abstract class STTypeVisited extends TypeVisitedCheck {
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.ENUM_DEF};
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

	
}
