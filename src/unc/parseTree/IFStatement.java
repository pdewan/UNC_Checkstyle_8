package unc.parseTree;

public interface IFStatement extends CheckedNode{

	CheckedNode getThenPart();

	CheckedNode getElsePart();

	String getExpression();

}