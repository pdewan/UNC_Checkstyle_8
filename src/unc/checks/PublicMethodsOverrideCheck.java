package unc.checks;

import java.util.ArrayList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.symbolTable.STMethod;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;


public  class PublicMethodsOverrideCheck extends STClassVisitedComprehensively {
	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 */
	public static final String MSG_KEY_INFO = "publicMethodsOverride";
	public static final String MSG_KEY_WARNING = "publicMethodsDoNotOverride";
	
	protected List<STMethod> publicNonOverriddenMethods = new ArrayList();
	public static final String OVERRIDE = "Override";


//	/** flag to control whether marker interfaces are allowed. */
//	private boolean allowMarkerInterfaces = true;
	
	public  PublicMethodsOverrideCheck () {
		
	}
//	 protected Boolean callTypeCheck(DetailAST ast, DetailAST aTreeAST, STType anSTType) {
//	   Boolean retVal = super.callTypeCheck(ast, aTreeAST, anSTType);
//	   return true;// return non null value
//	 }
	 protected Boolean callTypeCheck(DetailAST ast, DetailAST aTreeAST, STType anSTType) {
	    Boolean aTypeCheck = typeCheck(anSTType);
	    if (aTypeCheck == null)
	      return null;
	    boolean aDoLog = 
	        isInfo()?
	            aTypeCheck:
	            !aTypeCheck;
//	    if (!aTypeCheck)
	    if (aDoLog)
	        log(ast, aTreeAST, publicNonOverriddenMethods.toString() );
	    return aTypeCheck;
	  }

	@Override
	protected Boolean typeCheck(STType anSTClass) {
	  STMethod[] aDeclaredMethods = anSTClass.getDeclaredMethods();
	  if (aDeclaredMethods.length == 0) {
	    return null; // assuming in first pass
	  }
	  publicNonOverriddenMethods.clear();
	  boolean aFoundPublicOverriddenMethod = false;
	  for (STMethod anSTMethod:aDeclaredMethods) {
	    if (anSTMethod.isPublic()) {
	      if (anSTMethod.hasAnnotation(OVERRIDE)) {
	        aFoundPublicOverriddenMethod = true;
	        continue;
	      }
	      publicNonOverriddenMethods.add(anSTMethod);
	    }
	  }
	  if (publicNonOverriddenMethods.isEmpty() && !aFoundPublicOverriddenMethod) {
	    return null; // no public methods
	  }
	  if (publicNonOverriddenMethods.isEmpty()) {
	    return true;
	  }
	  return false; // found only overriidden methods
	  
	}
	@Override
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
