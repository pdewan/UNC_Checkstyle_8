package unc.cs.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public abstract class STClassVisited extends STTypeVisited {

	public STClassVisited() {

	}
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF,  TokenTypes.PACKAGE_DEF};
	} 
	public void doVisitToken(DetailAST ast) {		
		switch (ast.getType()) {
		case TokenTypes.PACKAGE_DEF: 
			visitPackage(ast);
			return;
		case TokenTypes.CLASS_DEF:
			if (getFullTypeName() == null)

			visitType(ast);
			return;		
		default:
			System.err.println("Unexpected token");
		}
		
	}
	
}
