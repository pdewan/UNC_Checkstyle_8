package unc.parseTree;

import unc.symbolTable.STMethod;

public class AMethodSpecification implements MethodSpecification {
	STMethod method;
	public AMethodSpecification(STMethod method) {
		super();
		this.method = method;
	}
	@Override
	public STMethod getMethod() {
		return method;
	}
	

}
