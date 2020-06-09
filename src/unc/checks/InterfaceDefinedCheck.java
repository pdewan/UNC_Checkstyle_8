package unc.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class InterfaceDefinedCheck extends TypeDefinedCheck{
	public static final String MSG_KEY = "interfaceDefined";

	public InterfaceDefinedCheck() {
		
	}
	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.PACKAGE_DEF,
				TokenTypes.CLASS_DEF, // for inner classes
				TokenTypes.INTERFACE_DEF,
//				TokenTypes.ANNOTATION,
				};
	}
	protected void visitClass(DetailAST ast) { // get the full type name
		super.visitType(ast);
	}
	protected void visitInterface(DetailAST ast) { // get the full type name
		super.visitType(ast);
		super.visitClassOrInterface(ast);
	}

	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	
}
