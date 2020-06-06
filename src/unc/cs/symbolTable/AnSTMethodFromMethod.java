package unc.cs.symbolTable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.checks.STBuilderCheck;

public class AnSTMethodFromMethod extends AnAbstractSTMethod implements STMethod{
	Method method;
	static STNameable[] emptyList = {};
	Integer accessToken;
	
	public AnSTMethodFromMethod(Method aMethod) {
		super(null, aMethod.getName());
		method = aMethod;	
		accessToken = ComprehensiveVisitCheck.getAccessToken(method);
		accessModifier = STBuilderCheck.toAccessModifier(aMethod.getModifiers());
		isAbstract = Modifier.isAbstract(aMethod.getModifiers());
		introspect();
	}
	@Override
	public boolean isParsedMethod() {
		return false;
	}
	
	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public DetailAST getAST() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public String getDeclaringClass() {
		return method.getDeclaringClass().getName();
	}

//	@Override
//	public String getName() {
//		return method.getName();
//	}

	@Override
	public String[] getParameterTypes() {
		Class[] parameterTypes = method.getParameterTypes();
		String[] result = new String[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
//			result[i] = parameterTypes[i].getSimpleName();
			result[i] = parameterTypes[i].getName();
			if (result[i].startsWith("[L")) {
				result[i] = result[i].substring("[L".length(), result[i].length()-1) + "[]";
			}
			result[i] = result[i].replaceAll("java.lang.", "");

		}
		return result;
	}
	@Override
	public int getNumParameters() {
		return getParameterTypes().length;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return method.getReturnType().getSimpleName();
	}

	@Override
	public boolean isPublic() {
		return Modifier.isPublic(method.getModifiers());
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public STNameable[] getTags() {
		return emptyList;
	}

	@Override
	public boolean assignsToGlobal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CallInfo[] getCallInfoOfMethodsCalled() {
		// TODO Auto-generated method stub
//		return new String[0][0];
		return new CallInfo[0];

	}

	@Override
	public boolean isProcedure() {
		return method.getReturnType() == Void.TYPE;
	}

//	@Override
//	public boolean isSetter() {
//		return false;
//	}
//
//	@Override
//	public boolean isGetter() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isInit() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	@Override
//	public String getSignature() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public boolean isInstance() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public STNameable[] getComputedTags() {
		// TODO Auto-generated method stub
		return getTags();
	}
	@Override
	public boolean isConstructor() {
		// TODO Auto-generated method stub
		return false;
	}
	List<STMethod> emptyMethodList = new ArrayList();
	@Override
	public List<STMethod> getLocalMethodsCalled() {
		return emptyMethodList;
	}
	@Override
	public List<STMethod> getAllMethodsCalled() {
		// TODO Auto-generated method stub
		return emptyMethodList;
	}
	@Override
	public void fillAllCallClosure(List<STMethod> aList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void fillLocalCallClosure(List<STMethod> aList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<STMethod> getAllCallClosure() {
		// TODO Auto-generated method stub
		return emptyMethodList;

	}
	@Override
	public List<STMethod> getLocalCallClosure() {
		// TODO Auto-generated method stub
		return emptyMethodList;

	}
	@Override
	public List<STNameable> getTypesInstantiated() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Boolean instantiatesType(String aShortOrLongName) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String[] getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<String> getGlobalsAssigned() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<String> getGlobalsAccessed() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getAccessToken() {
		return accessToken;
	}
	@Override
	public List<STVariable> getLocalVariables() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<STVariable> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isSynchronized() {
		return Modifier.isSynchronized(method.getModifiers());
	}
	@Override
	public int getNumberOfTernaryConditionals() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<STType> getAsserts() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getNumberOfAsserts() {
		// TODO Auto-generated method stub
		return 0;
	}
//	@Override
//	public void addFullNamesToUnknowns() {
//		// TODO Auto-generated method stub
//		
//	}
	@Override
	public Map<String, Set<DetailAST>> getUnknownAccessed() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Set<DetailAST>> getUnknownAssigned() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void refreshUnknowns() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void processGlobals() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Map<String, Set<DetailAST>> getGlobalsAssignedMap() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Set<DetailAST>> getGlobalsAccessedMap() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<STVariable> getParametersAssigned() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<STVariable> getLocalsAssigned() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setPublic(boolean isPublic) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isGeneric() {
		return method.getTypeParameters().length > 0;
	}
	

}
