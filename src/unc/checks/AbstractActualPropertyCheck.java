package unc.checks;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.CallInfo;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;


public abstract  class AbstractActualPropertyCheck extends ComprehensiveVisitCheck {
	

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "abstactActualProperty";
   
    @Override
	public int[] getDefaultTokens() {
		return new int[] {
//				 TokenTypes.PACKAGE_DEF,
//				TokenTypes.CLASS_DEF,
//				 TokenTypes.INTERFACE_DEF,
				
		};

	}
    protected void logPropertyNotFound(DetailAST aTreeAST, DetailAST anErroneousAST, String aProperty) {
//		String aSourceName = getShortFileName(aTreeAST);
//		 if (aTreeAST == currentTree) {
//
//			log(anErroneousAST.getLineNo(), anErroneousAST.getColumnNo(), msgKey(), aProperty, aSourceName);
//		} else {
//			log(0, msgKey(), aProperty,aSourceName);
//		}
    	super.log(anErroneousAST, aTreeAST, aProperty);

	}
    public abstract Boolean checkActualProperty (STType anSTType, String aDeclarePropertyName ) ;
    abstract STNameable[] getDeclaredPropertyNames(STType anSTType);
    public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
    	String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
//		STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
		STType anSTType = getSTType(aTreeAST);

		if (anSTType == null) {
			System.err.println("Probably an inner class:" + aTypeName);
			return true;
		}
		if (anSTType.isEnum() || anSTType.isInterface() || anSTType.isAnnotation())
			return true;
		Boolean retVal = true;
		
//		STNameable[] anSTNameables = anSTType.getAllDeclaredPropertyNames();
		STNameable[] anSTNameables = getDeclaredPropertyNames(anSTType);
		if (anSTNameables == null)
			return null;
		for (STNameable aNameable:anSTNameables) {
			String aPropertyName = maybeStripQuotes(aNameable.getName());
//			Boolean hasProperty = anSTType.hasActualProperty(aPropertyName);
			Boolean hasProperty = checkActualProperty(anSTType, aPropertyName);
			if (hasProperty == null) return null;
			if (hasProperty) 
				continue;
			logPropertyNotFound(aTreeAST, aNameable.getAST(), aPropertyName);
			retVal = false;
			
		}
		return retVal;
	}
    public void doFinishTree(DetailAST ast) {
		
		maybeAddToPendingTypeChecks(ast);
		super.doFinishTree(ast);

	}
    
	@Override
	protected String msgKey() {
		return MSG_KEY;
	}
	
	
}
