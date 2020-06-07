package unc.cs.checks;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

//public class IllegalTypeDefinedCheck extends TypeVisitedCheck{
	public class IllegalTypeDefinedCheck extends ComprehensiveVisitCheck{

	
	public static final String MSG_KEY = "illegalTypeDefined";
	Collection<String> illegalTypeNames = new HashSet();
	
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, 
//				TokenTypes.ANNOTATION, 
				TokenTypes.INTERFACE_DEF, TokenTypes.PACKAGE_DEF};
	}
	
	public void setIllegalTypeNames(String[] aNames) {
		illegalTypeNames = Arrays.asList(aNames);
		
	}
	
	public void visitType(DetailAST ast) {
    	super.visitType(ast);    	
    	if (illegalTypeNames.contains(this.shortTypeName))
//		   log(getNameAST(ast).getLineNo(), msgKey(), shortTypeName);
		   super.log(getNameAST(ast), shortTypeName);


    }
	@Override
	public void leaveType(DetailAST ast) {
    	super.leaveType(ast);

    }
//	public void doVisitToken(DetailAST ast) {
//		
//		switch (ast.getType()) {
//		case TokenTypes.PACKAGE_DEF: 
//			visitPackage(ast);
//			return;
//		case TokenTypes.CLASS_DEF:
//		case TokenTypes.INTERFACE_DEF:
//			if (fullTypeName == null)
//
//			visitType(ast);
//			return;
//		
//		default:
//			System.err.println(checkAndFileDescription + "Unexpected token");
//		}
//		
//	}
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	
}
