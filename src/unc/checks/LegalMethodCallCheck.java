package unc.checks;


import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.CallInfo;
import unc.symbolTable.STType;


public  class LegalMethodCallCheck extends MethodCallVisitedCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "legalMethodCall";
    protected String[] expectedMethods;
    protected Set<String> expectedMethodsSet = new HashSet();
//   
//    @Override
//    public int[] getDefaultTokens() {
//        return new int[] {TokenTypes.METHOD_CALL};
//    }

//    @Override
//    public int[] getRequiredTokens() {
//        return getDefaultTokens();
//    }
//
//    @Override
//    public int[] getAcceptableTokens() {
//        return new int[] {TokenTypes.PACKAGE_DEF};
//    }

//    @Override
//    public void beginTree(DetailAST ast) {    	
//        defined = false;
//    }

//    @Override
//    public void finishTree(DetailAST ast) {
////        if (!defined) {
//////            log(ast.getLineNo(), MSG_KEY);
////        }
//    }
    
//    public static String getMethodName(DetailAST ast) {
//    	DetailAST methodComponent = ast.getFirstChild();
//    	while (methodComponent.getType() == TokenTypes.DOT)
//    		methodComponent = methodComponent.getLastChild();
//    	
//    	return methodComponent.getText();
//    	
//    }
//    public static DetailAST getLastDescendent(DetailAST ast) {
//    	DetailAST result = ast.getFirstChild();
//    	while (result.getChildCount() > 0)
//    		result = result.getLastChild();    	
//    	return result;    	
//    }
//
//    @Override
//    public void visitToken(DetailAST ast) {
//    	if (ast.getType() != TokenTypes.METHOD_CALL)
//    		return;
//        System.out.println("Method text:" + getLastDescendent(ast).getText());
//        log(ast.getLineNo(), msgKey(), getLastDescendent(ast).getText());
//
//    }
    @Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
    
    public String[] getExpectedMethods() {
    	return expectedMethods;
    }
    
    public void setExpectedMethods (String[] newValue) {
    	expectedMethods = newValue;
//		String[] toArray = newValue.split(",");
		for (String s:expectedMethods) {
			expectedMethodsSet.add(s);
		}		
	}
    // "fail" if method is in expected set
	@Override
	protected Boolean check(STType aCallingType, DetailAST ast, String aShortMethodName, String aLongMethodName, CallInfo aCallInfo) {
		return !expectedMethodsSet.contains(aShortMethodName);
	}

	
	
}
