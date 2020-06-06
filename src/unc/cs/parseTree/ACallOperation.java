package unc.cs.parseTree;

import java.util.ArrayList;
import java.util.List;

import unc.cs.checks.ComprehensiveVisitCheck;
import unc.cs.symbolTable.STMethod;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ACallOperation extends ATransitiveOperation implements CallOperation {
//	STMethod method; // called method
//	String signatureWithTarget;
	
//	public static final String PARAM_START = "$";
//	List<String> params;
	public ACallOperation(String aSpecification) {
		super(new Integer[]{TokenTypes.METHOD_CALL}, aSpecification);
//		try {
//			String[] aNameParts = name.split("\\$");
//			signatureWithTarget = aNameParts[0];
////			method = ComprehensiveVisitCheck.signatureToMethod(aNameParts[0]);
//			if (aNameParts.length > 1) {
//				params = new ArrayList<>();
//				for (int i = 1; i < aNameParts.length; i++) {
//					params.add(aNameParts[i]);
//				}							
//			}			
//		} catch (Exception e) {
////			method = null;
//			params = null;
//			signatureWithTarget = null;
//			
//		}
	}
//	@Override
//	public STMethod getMethod() {
//		return method;
//	}
//	@Override
//	public List<String> getParams() {
//		return params;
//	}
//	public String getSignatureWithTarget() {
//		return signatureWithTarget;
//	}

}
