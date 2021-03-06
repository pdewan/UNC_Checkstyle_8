package unc.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STType;


public  class ClassIsGenericCheck extends STClassVisitedComprehensively {
	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY = "classIsGeneric";
  public static final String MSG_KEY_WARNING = "classIsNotGeneric";
  public static final String MSG_KEY_INFO = "classIsGeneric";
  

//	/** flag to control whether marker interfaces are allowed. */
//	private boolean allowMarkerInterfaces = true;
	
	public  ClassIsGenericCheck () {
		
	}
	
	
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] { TokenTypes.CLASS_DEF };
//	}
//	@Override
//    public void visitClass(DetailAST typeDef) { 
//		super.visitClass(typeDef);
//    	
//		STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
//    			getSTClassByFullName(fullTypeName);
//    	if (!typeCheck(anSTClass))
//    		log(typeDef);
//
//    }
//	
//	protected void log(DetailAST ast) {
//	    log(getNameAST(ast).getLineNo(), msgKey(), fullTypeName);
//    }


	protected Boolean typeCheck(STType anSTClass) {
		if (typeAST == null) {
			return false;
		}
		DetailAST generic = typeAST.findFirstToken(TokenTypes.TYPE_PARAMETERS);
				return generic != null;
	}

//	protected void log(DetailAST ast) {
//		log(ast.getLineNo(), msgKey(), typeName);
//	}
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
	
	
}
