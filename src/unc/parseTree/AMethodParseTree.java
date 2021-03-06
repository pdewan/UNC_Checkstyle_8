package unc.parseTree;

import unc.symbolTable.STMethod;

public class AMethodParseTree extends AMethodSpecification implements MethodParseTree {
	CheckedNode parseTree;
	public AMethodParseTree(STMethod method, CheckedNode parseTree) {
		super(method);
		this.method = method;
		this.parseTree = parseTree;
	}
	
	@Override
	public CheckedNode getParseTree() {
		return parseTree;
	}

}
