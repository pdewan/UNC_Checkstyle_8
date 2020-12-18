package unc.checks;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

import unc.symbolTable.APropertyInfo;
import unc.symbolTable.AccessModifierUsage;
import unc.symbolTable.STMethod;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;

public class MethodAccessModifierCheck extends ComprehensiveVisitCheck {
	public static final String MSG_KEY_WARNING = "methodAccessModifierUnmatched";
	public static final String MSG_KEY_INFO = "methodAccessModifierMatched";

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
				TokenTypes.CLASS_DEF, 
				TokenTypes.PACKAGE_DEF,
//				TokenTypes.METHOD_DEF,
//				TokenTypes.PARAMETER_DEF,
//				TokenTypes.TYPE_PARAMETERS,
//				TokenTypes.IMPORT
				};
	} 
	@Override
	protected String msgKeyWarning() {
		return MSG_KEY_WARNING;
	}
	@Override
	protected String msgKeyInfo() {
		return MSG_KEY_INFO;
	}
	
	@Override
	protected String msgKey() {
		return isInfo()?msgKeyInfo():msgKeyWarning();
	}
	
	static String[] emptyArray = {};
//	@Override
//	protected void doLeaveToken(DetailAST ast) {
////		if (STBuilderCheck.isFirstPass) {
////			return;
////		}
//		super.doLeaveToken(ast);
//	}
//	@Override
//	protected void doVisitToken(DetailAST ast) {
////		if (STBuilderCheck.isFirstPass) {
////			return;
////		}
//		super.doVisitToken(ast);
//	}
	
	protected void processMethod(STMethod anSTMethod) {
		
	  if ("static public  main:String[]->void".equals(anSTMethod.toString())) {
	    return;
	  }
	  
	

		if (anSTMethod == null) {
			System.err.println("null st method in " + currentFullFileName);
		  

			return;
		}
		DetailAST methodDef = anSTMethod.getAST();
		List<AccessModifierUsage> aUsages = anSTMethod.getAccessModifiersUsed();
		if (aUsages == null || aUsages.size() == 0) {
			
				if (!isInfo()) {
					
		
				
				log (anSTMethod.getAST(), anSTMethod.getSimpleChecksSignature(), anSTMethod.getAccessModifier().toString(), "None", "" + (AccessModifier.PRIVATE.ordinal() + 1 - anSTMethod.getAccessModifier().ordinal()),
						"No Referencing Method", "No Access Modifiers" );
			}

			
			return;
		}
		boolean aMatch = true;
		int aMinDifference = APropertyInfo.INFINITE_ACCESS_DIFFERENCE;
		List<String> aReferencingTypes = new ArrayList();
		List<String> aUsedAccessModifiers = new ArrayList();
		if (aUsages == null) {
			System.err.println ("Null usages");
			return;
		}
		for (AccessModifierUsage aUsage:aUsages) {
			if (aUsage == null) {
//				System.err.println ("Null usage");
				continue;
			}
			aMinDifference = Math.min(aMinDifference, aUsage.getDifference());
			aReferencingTypes.add(aUsage.getTypeReferencing().getName());
			aUsedAccessModifiers.add(aUsage.getUsed().toString());
		}
		if (aMinDifference == 0 && isInfo()) {
			log (methodDef, anSTMethod.getSimpleChecksSignature(), anSTMethod.getAccessModifier().toString(),
					aReferencingTypes.toString(), aUsedAccessModifiers.toString() );
		} 
		else if (aMinDifference > 0 && !isInfo()) {	
//		  if (anSTMethod.toString().contains("isGivenSafe")) {
//		    int i = 1;
//		  }
			AccessModifier anAccessNeeded = AccessModifier.values()[anSTMethod.getAccessModifier().ordinal() +  aMinDifference];
			log (methodDef, anSTMethod.getSimpleChecksSignature(), anSTMethod.getAccessModifier().toString(), anAccessNeeded.toString(), "" + aMinDifference,
					aReferencingTypes.toString(), aUsedAccessModifiers.toString() );
		}
		
	}
	
//  public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
	@Override
    public void doFinishTree(DetailAST anAST) {
	  if (isFirstPass()) {
	    return;
	  }

    String aTypeName = getFullTypeName();
    if (aTypeName == null) {
//      System.err.println("Null full type name:" + currentFullFileName);
      return;
      
    }
    STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aTypeName);
    if (anSTType == null) {
      System.err.println("Null STType :" + currentFullFileName);
      return;
    }
    if (!checkIncludeTagsOfCurrentType(anSTType)) {
      return;
    }
    if (anSTType.getDeclaredMethods() == null) {
      return;
    }
    List<STNameable>  anAllInterfaces = anSTType.getAllInterfaces();
    STNameable[] aDeclaredInterfaces = anSTType.getDeclaredInterfaces();
    boolean anAllowPublicMethods = anSTType.isAbstract() || 
            (anAllInterfaces != null && !anAllInterfaces.isEmpty()) ||
            (aDeclaredInterfaces != null && aDeclaredInterfaces.length != 1);
    
    
    for (STMethod anSTMethod:anSTType.getDeclaredMethods()) {
      if ((anSTMethod.getAccessModifier() == AccessModifier.PUBLIC) && 
              
              (anAllowPublicMethods || anSTMethod.isGetter() || anSTMethod.isSetter())) {
        continue;
      }
      processMethod(anSTMethod);
    }
  }
	
	

}