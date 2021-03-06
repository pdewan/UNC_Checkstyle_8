package unc.checks;

import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STMethod;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;

public class SetterAssignsGlobalCheck extends MethodEffectCheck{
	public static final String MSG_KEY = "setterAssignsGlobal";	


	@Override
	protected String msgKey() {
		// TODO Auto-generated method stub
		return MSG_KEY ;
	}

@Override
protected boolean shouldVisitRootMethod(STMethod aMethod) {
	return aMethod.isPublicSetter();
}

@Override
protected boolean shouldVisitCalledMethod(STMethod aMethod) {
	return !aMethod.isPublicSetter() && aMethod.isProcedure(); //assuming functions do not assign globals
}

@Override
protected boolean shouldTraverseVisitedMethod(STMethod aMethod) {
	return true;
}

@Override
protected boolean methodEffectCheck(STMethod anSTMethod) {
	boolean retVal = anSTMethod.assignsToGlobal();
//	if (!retVal) {
//		System.err.println("Setter assigns global false");
//	}
//	return anSTMethod.assignsToGlobal();	
	return retVal;
}

@Override
protected boolean stopOnFailure() { // stop on success
	return false;
}
	

}
