package unc.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.PropertyInfo;
import unc.tools.checkstyle.ProjectSTBuilderHolder;

public class ExpectedGettersCheck extends BeanTypedPropertiesCheck {
	public static final String MSG_KEY = "expectedGetter";
  public static final String MSG_KEY_WARNING = "missingGetter";
  public static final String MSG_KEY_INFO = "expectedGetter";
  public ExpectedGettersCheck() {
    super();
  }
  @Override
  public int[] getDefaultTokens() {
    return super.getDefaultTokens();
  }
	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY;
	}
	@Override
  protected String msgKeyWarning() {
    return MSG_KEY_WARNING;
  }

  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_INFO;
  }


//	public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
//		STType anSTType = getSTType(aTree);
//		if (anSTType.getName().contains("roceed")) {
//			System.out.println("Doing pending check for " + anSTType.getName());
//
//		}
//
//		Boolean retVal = super.doPendingCheck(anAST, aTree);
//		if (retVal == null) {
//			System.out.println ("returning null in pending check");
//		}
//		return retVal;
//	}





	@Override
	public Boolean matchType(String aSpecifiedType, PropertyInfo aProperty) {
		// TODO Auto-generated method stub
		Boolean retVal = matchGetter(aSpecifiedType, aProperty);
		
		return retVal;
//		return matchGetter(aSpecifiedType, aProperty);
	}

	 public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
	    return super.doPendingCheck(anAST, aTree);
	 }
	 
	 public void doFinishTree(DetailAST ast) {
//	    // STType anSTType =
//	    // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(fullTypeName);
//	    // for (STMethod aMethod: anSTType.getMethods()) {
//	    // visitMethod(anSTType, aMethod);
//	    // }
//	    if (!ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
//
//	    maybeAddToPendingTypeChecks(ast);
//	    }
	    super.doFinishTree(ast);

	  }
	 protected void visitClass(DetailAST ast) {
	   super.visitClass(ast);
	 }
	 public void doVisitToken(DetailAST ast) {
	   super.doVisitToken(ast);
	 }
	  public void visitToken(DetailAST ast) {
	    super.visitToken(ast);
	  }
}
