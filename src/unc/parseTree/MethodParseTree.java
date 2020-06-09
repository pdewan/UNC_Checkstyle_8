package unc.parseTree;

import unc.symbolTable.STMethod;

public interface MethodParseTree extends MethodSpecification {

	public abstract CheckedNode getParseTree();

}