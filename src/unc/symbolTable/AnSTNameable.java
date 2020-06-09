package unc.symbolTable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

import unc.checks.BulkierThenCheck;
import unc.checks.ComprehensiveVisitCheck;
import unc.tools.checkstyle.UNCAstTreeStringPrinter;


public  class AnSTNameable implements STNameable {
	protected DetailAST ast;
	protected String name;
	protected Object data;
	
	protected String stringTree;
	protected String[] statements;
	protected Integer size;
	protected AccessModifier accessModifier = AccessModifier.PACKAGE;

//	int numReferences;
	Set<DetailAST> references = new HashSet<>();
//	String[] components;
	public AnSTNameable( String name) {
		super();
		this.name = name;
	}
	public AnSTNameable(DetailAST ast, String name) {
		super();
		this.ast = ast;
		this.name = name;
//		components = ComprehensiveVisitCheck.splitCamelCaseHyphenDash(name);
	}
	public AnSTNameable(DetailAST ast, String name, String aData) {
		this(ast, name);
		data = aData;
	}

	@Override
	public DetailAST getAST() {
		return ast;
	}
	public boolean equals(Object anObject) {
		if (anObject instanceof STNameable) {
			return ((STNameable) anObject).getName().equals(name);
		} else 
			return super.equals(anObject);
	}

//	public boolean equals (Object anObject) {
//		if (anObject instanceof STNameable) {
//			return name.equals(((STNameable) anObject).getName());
//		}
//		return super.equals(anObject);
//	}
	

	@Override
	public String getName() {
		return name;
	}
	@Override
	public Object getData() {
		return data;
	}
	public String toString() {
		return name;
	}
	@Override
	public int getNumReferences() {
		return references.size();
	}
	@Override
	public Set<DetailAST> getReferences() {
		return references;
	}
	@Override
	public AccessModifier getAccessModifier() {
		return accessModifier;
	}
//	@Override
//	public void setNumReferences(int numReferences) {
//		this.numReferences = numReferences;
//	}
//	public void incrementNumReferences() {
//		numReferences++;
//	}
//	@Override
//	public String[] getComponents() {
//		return components;
//	}
	@Override
	public List<AccessModifierUsage> getAccessModifiersUsed() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getStringTree() {
		if (stringTree == null) {
//			stringTree = ast.toStringTree();
	     stringTree = UNCAstTreeStringPrinter.printConcreteTree(ast);

		}
		return stringTree;
	}
	@Override
	public String[] getStatements() {
		if (statements == null) {
			String aStringTree = getStringTree();			
			statements = aStringTree.split(BulkierThenCheck.STATEMENT_SEPARATOR);
		}
		return statements;
	}
	@Override
	public Integer getSize() {
		return getStatements().length;
	}
	
//	public static void main (String[] args) {
//		String[] aSplit = ComprehensiveVisitCheck.splitCamelCase("hel_loABC23Goo-dbye");
//		System.out.println(Arrays.toString(aSplit));
//	}
}
