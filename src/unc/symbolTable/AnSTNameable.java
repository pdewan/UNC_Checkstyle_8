package unc.symbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

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
	public static Object[] emptyArray = {};
	 public static List emptyList = Arrays.asList(emptyArray);
	  public static Set emptySet = new EmptyHashSet();
	  public static STNameable[] emptyNameables = new STNameable[0];
	  public static Map emptyMap = new EmptyHashMap();


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
		if ("Object".equals(name)) {
		  this.name = "java.lang.Object";
		} else {
		this.name = name;
		}
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
	public String getStringTree(boolean showLineInfo) {
		if (stringTree == null) {
//			stringTree = ast.toStringTree();
	     stringTree = UNCAstTreeStringPrinter.printTree(ast, showLineInfo);

		}
		return stringTree;
	}
	@Override
	public String[] getStatements(boolean showLineInfo) {
		if (statements == null) {
			String aStringTree = getStringTree(showLineInfo);			
			statements = aStringTree.split(BulkierThenCheck.STATEMENT_SEPARATOR);
		}
		return statements;
	}
	@Override
	public Integer getSize() {
		return getStatements(false).length;
	}
	public static List nullToEmptyList (List aMaybeNullList) {
    if (aMaybeNullList == null) {
      return emptyList;
    }
    return aMaybeNullList;
  }
   
  public static Set nullToEmptySet (Set aMaybeNullSet) {
    if (aMaybeNullSet == null) {
      return emptySet;
    }
    return aMaybeNullSet;
  }
  public static Map nullToEmptyMap (Map aMaybeNullMap) {
    if (aMaybeNullMap == null) {
      return emptyMap;
    }
    return aMaybeNullMap;
  }
  public static Set nullToNewSet (Set aMaybeNullSet) {
    if (aMaybeNullSet == null) {
      return new HashSet();
    }
    return aMaybeNullSet;
  }
  
  public static List nullToNewList (List aMaybeNullList) {
    if (aMaybeNullList == null) {
      return new ArrayList();
    }
    return aMaybeNullList;
  }
  public static Map nullToNewMap (Map aMaybeNullMap) {
    if (aMaybeNullMap == null) {
      return new HashMap();
    }
    return aMaybeNullMap;
  }
  
  public static List copy (List anOriginal) {
    if (anOriginal.isEmpty()) {
      return emptyList;
    }
    return new ArrayList(anOriginal);
  }
  public static Map copy (Map anOriginal) {
    if (anOriginal.isEmpty()) {
      return emptyMap;
    }
    return new HashMap(anOriginal);
  }
  public static Set copy (Set anOriginal) {
    if (anOriginal.isEmpty()) {
      return emptySet;
    }
    return new HashSet(anOriginal);
  }
  public static String[] emptyStringArray = {};

  public static STNameable[] emptyNameableArray = {};
  public static STNameable[] toSTNameableArray (List<STNameable> anOriginal) {
    if (anOriginal == null || anOriginal.isEmpty()) {
      return emptyNameableArray;
    }
    return anOriginal.toArray(emptyNameableArray);
  }
  public static String[] toStringArray (List<String> anOriginal) {
    if (anOriginal == null || anOriginal.isEmpty()) {
      return emptyStringArray;
    }
    return anOriginal.toArray(emptyStringArray);
  }
  @Override
  public boolean hasAnnotation(String anAnnotationName) {
    return AnnotationUtil.containsAnnotation(ast, anAnnotationName);
  }
  @Override
  public boolean hasAnnotation(List<String> anAnnotationNames) {
    return AnnotationUtil.containsAnnotation(ast, anAnnotationNames );
  }
	
//	public static void main (String[] args) {
//		String[] aSplit = ComprehensiveVisitCheck.splitCamelCase("hel_loABC23Goo-dbye");
//		System.out.println(Arrays.toString(aSplit));
//	}
}
