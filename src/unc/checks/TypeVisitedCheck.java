package unc.checks;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.AnSTNameable;
import unc.symbolTable.STNameable;
import unc.symbolTable.TypeType;
import unc.tools.checkstyle.ProjectSTBuilderHolder;

public abstract class TypeVisitedCheck extends UNCCheck {
	public static final String MSG_KEY = "typeDefined";	
	public static final String DEFAULT_PACKAGE = "default"; 
	protected String packageName;
	private String fullTypeName;
	protected String shortTypeName;
	protected TypeType typeType = null;
	
//	protected boolean isAnnotation = false;
//	protected boolean isEnum = false;
//	protected boolean isInterface = false;


	protected List<DetailAST> annotationParameters = new ArrayList();

	protected DetailAST typeAST;
	protected DetailAST typeNameAST;
	protected STNameable typeNameable;
	protected Stack<String> fullTypeNameStack = new Stack();
	protected Stack<String> shortTypeNameStack = new Stack();
	protected Stack<DetailAST> typeNameASTStack = new Stack();
	protected Stack<STNameable> typeNameableStack = new Stack();
	protected Stack<DetailAST> typeASTStack = new Stack();
	protected Stack<Boolean> isGenericStack = new Stack();
	protected List<DetailAST> innerTypeASTs = new ArrayList();
	protected List<String> innerTypeNames = new ArrayList();


//	protected STNameable packageNameable;
	protected Boolean isGeneric;
	@Override
    public void doBeginTree(DetailAST ast) {
        packageName = DEFAULT_PACKAGE;
    } 
//	@Override
//	public int[] getDefaultTokens() {
//		return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  
//						TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF, TokenTypes.PARAMETER_DEF };
//	}
	
	protected void resetProject() {
	  super.resetProject();
	  resetCheck();
	  

	}
	 protected void resetCheck(DetailAST ast) {
//	   resetCheck();
	   if (foundSupuriousInnerClass) {
	      foundSupuriousInnerClass = false;
//	      System.err.println("Resetting suprious inner class in reset check " + currentFullFileName + " " + this);
	    }
	 }

	protected void resetCheck() {
//		super.resetProject();
//		fullTypeName = null;
//	  if (foundSupuriousInnerClass) {
//      foundSupuriousInnerClass = false;
//      System.err.println("Resetting suprious inner class in reset check " + currentFullFileName);
//    }
		setFullTypeName(null);
//		isAnnotation = false;
//		isEnum = false;
//		isInterface = false;
		typeType = null;

		shortTypeName = null;
		typeAST = null;
		typeNameAST = null;
		typeNameable = null;
		fullTypeNameStack.clear();
		shortTypeNameStack.clear();
		typeNameASTStack.clear();
		typeNameableStack.clear();
		typeASTStack.clear();
		isGenericStack.clear();
		annotationParameters.clear();		
		
	}
	
	public boolean maybeVisitPackage(DetailAST ast) {
		if (ast.getType() == TokenTypes.PACKAGE_DEF) {
//			packageName = ast.findFirstToken(TokenTypes.IDENT).getText();
//			System.out.println("found package:" + packageName);	
			visitPackage(ast);
			return true;
		}
		return false;
	}
	 public void visitType(DetailAST ast) { 
		 visitTypeMinimal(ast);

//	    	typeAST = ast;
//	    	typeNameAST = ast.findFirstToken(TokenTypes.IDENT);
//	    	isGeneric =  (typeNameAST.getNextSibling().getType() == TokenTypes.TYPE_PARAMETERS);
//			 shortTypeName = typeNameAST.getText();
//			 fullTypeName = packageName + "." + shortTypeName;
//			 typeNameable = new AnSTNameable(typeNameAST, fullTypeName);
	 }
	 public void leaveType(DetailAST ast) {
		 leaveTypeMinimal(ast);
	 }
	 public void visitEnumDef(DetailAST anEnumDef) {
			visitType(anEnumDef);
//			propertyNames = emptyArrayList; //no properties
//			isEnum = true;
			typeType = TypeType.ENUM;
//			System.err.println ("Setting type to enum");
			typeNameAST = getEnumNameAST(anEnumDef);
			// shortTypeName = getEnumName(anEnumDef);
			shortTypeName = typeNameAST.getText();
//			fullTypeName = packageName + "." + shortTypeName;
			setFullTypeName(packageName + "." + shortTypeName);
			typeAST = anEnumDef;
//			superClass = null;
//			interfaces = emptyNameableArray;
//			isInterface = false;
			
			typeNameable = new AnSTNameable(typeNameAST, getFullTypeName());

			// shortTypeName = anEnumDef.getNextSibling().toString();
			// DetailAST anEnumIdent =
			// anEnumDef.getNextSibling().findFirstToken(TokenTypes.IDENT);
			// if (anEnumIdent == null) {
			// System.out.println("null enum ident");
			// }
			// shortTypeName = anEnumIdent.getText();
		}
		protected void visitAnnotationDef(DetailAST ast) {
//			isAnnotation = true;
			typeType = TypeType.ANNOTATION;
			typeAST = ast;
			typeNameAST = 
					ast.getFirstChild()//Modifiers
					.getNextSibling()// @
					.getNextSibling()//interface
					.getNextSibling();//name
			 shortTypeName = typeNameAST.getText();
			 setFullTypeName(packageName + "." + shortTypeName);

			 
			 typeNameable = new AnSTNameable(typeNameAST, fullTypeName);
		}
		protected void visitAnnotationFieldDef(DetailAST ast) {
			annotationParameters.add(ast);
		}
	 protected void visitTypeMinimal(DetailAST ast) { 
//		 DetailAST generic = ast.getFirstChild().getNextSibling().getNextSibling().getNextSibling();
//		 generic = ast.findFirstToken(TokenTypes.TYPE_PARAMETERS);
//		 if (generic.getType() == TokenTypes.TYPE_PARAMETERS) {
//			 int i = 0;
//		 }
//	    	
//			maybeVisitPackage(ast);
	    	typeAST = ast;
	    	typeNameAST = ast.findFirstToken(TokenTypes.IDENT);
	    	isGeneric =  (typeNameAST.getNextSibling().getType() == TokenTypes.TYPE_PARAMETERS);
			 shortTypeName = typeNameAST.getText();
//			 if (shortTypeName.contains("Bridge")) {
//				 System.out.println ("found outer class");
//			 }
//			 if (shortTypeName.contains("ListImp")) {
//				 System.out.println("inner interface");
//			 }
//			 fullTypeName = packageName + "." + shortTypeName;
			 setFullTypeName(packageName + "." + shortTypeName);

			 
			 typeNameable = new AnSTNameable(typeNameAST, fullTypeName);
		if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
	 
		    typeASTStack.push(typeAST);
		    typeNameASTStack.push(typeNameAST);
		    isGenericStack.push(isGeneric);
		    shortTypeNameStack.push(shortTypeName);		    
			 fullTypeNameStack.push(fullTypeName);
			 typeNameableStack.push(typeNameable);
		}

//			aFullTypeName = aFullName;
	 }
	 
	 public static <T> T myPop (Stack<T> aStack) {
		 if (aStack.isEmpty()) {
			 System.err.println ("empty stack!");
			 return null;
		 }
		 aStack.pop();
		 if (aStack.isEmpty())
			 return null;
		 return aStack.peek();
	 }
	 
	 // this may be danderous as have assumed one class per file so far
	 // and finishTree might assume these values to be set
	 // so not calling it
	 protected void leaveTypeMinimal (DetailAST ast) {
		 if (ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) {
			 typeAST = myPop(typeASTStack);
//		 typeASTStack.pop();
//		 typeAST = typeASTStack.peek();
			 typeNameAST = myPop(typeNameASTStack);
//	    	typeNameASTStack.pop();
//	    	typeNameAST = typeNameASTStack.peek();
			 isGeneric = myPop(isGenericStack);
//	    	isGenericStack.pop();
//	    	isGeneric = isGenericStack.peek();
//	    	isGeneric =  (typeNameAST.getNextSibling().getType() == TokenTypes.TYPE_PARAMETERS);
			 
			 shortTypeName = myPop(shortTypeNameStack);
//			 if (shortTypeName == null) {
//			   System.err.println ("null short type name");
//			 }
//			 shortTypeNameStack.pop();
//			 shortTypeName = shortTypeNameStack.peek();
//			fullTypeName = myPop(fullTypeNameStack);
			 
			 String aFullTypeName = myPop(fullTypeNameStack);
//			 if (aFullTypeName == null) {
//			   System.err.println("Null full type name:" + currentFullFileName);
//			 }
			 setFullTypeName(aFullTypeName);
//			setFullTypeName(myPop(fullTypeNameStack));
//			fullTypeNameStack.pop();
//			fullTypeName = fullTypeNameStack.peek();
			typeNameable = myPop(typeNameableStack);
//			typeNameableStack.pop();
//			typeNameable = typeNameableStack.peek();
		 }
	 }
//	 public void visitEnum(DetailAST ast) {  
////	    	
////			maybeVisitPackage(ast);
//	    	typeAST = ast;
//	    	typeNameAST = ast.getNextSibling();
//	    	
//			 fullTypeName = packageName + "." + shortTypeName;
////			aFullTypeName = aFullName;
//	 }
	 protected static String getEnumName(DetailAST anEnum) {
	    	return getEnumNameAST(anEnum).getText();
	    }
	 protected static DetailAST getEnum(DetailAST anEnumDef) {
	    	return anEnumDef.getFirstChild().getNextSibling();

	 }
	    protected static DetailAST getEnumNameAST(DetailAST anEnumDef) {
	    	return getEnum(anEnumDef).getNextSibling();
	    }
	    public static String getPackageName (DetailAST ast) {
	    	FullIdent aFullIdent = FullIdent.createFullIdent(ast.getFirstChild().getNextSibling());
//			DetailAST anEnclosingType = TagBasedCheck.getEnclosingTypeDeclaration(ast);
//			packageName = getName(ast);
			return aFullIdent.getText();
	    }
	public void visitPackage(DetailAST ast) {
//		FullIdent aFullIdent = FullIdent.createFullIdent(ast.getFirstChild().getNextSibling());
//		packageName = aFullIdent.getText();
		packageName = getPackageName(ast);

	}
	protected void logType(DetailAST ast) {
//	    log(getNameAST(ast).getLineNo(), msgKey(), fullTypeName);
	    log(getNameAST(ast), fullTypeName);

    }
	// not a full name I assume
	public static String getName (DetailAST anAST) {
//		return anAST.findFirstToken(TokenTypes.IDENT).getText();
		if (anAST == null) {
//			System.err.println("null ast in get name, returning null name");
			return null;
		}
		if (anAST.getType() == TokenTypes.ENUM)
			return getEnumName(anAST);
			
		DetailAST aNameAST = anAST.findFirstToken(TokenTypes.IDENT);
//		if (aNameAST == null) {
//			System.err.println("no ident!");
//			return null;
//		}
		return aNameAST.getText();
		
//		return getNameAST(anAST).getText();

	}
//	  protected static String getEnumName(DetailAST anEnumDef) {
//	    	return anEnumDef.getNextSibling().toString();
//	    }
//	    
	
	public static DetailAST getNameAST (DetailAST anAST) {
		return anAST.findFirstToken(TokenTypes.IDENT);
		
	}
	public static String toShortTypeName (String aTypeName) {
		return TagBasedCheck.toShortTypeOrVariableName(aTypeName);
	}
//	public static String toShortTypeName (String aTypeName) {
//		if (aTypeName == null)
//			return aTypeName;
//		int aDotIndex = aTypeName.lastIndexOf(".");
//		String aShortTypeName = aTypeName;
//		if (aDotIndex != -1)
//			aShortTypeName = aTypeName.substring(aDotIndex + 1);
//		return aShortTypeName;
//	}
	public static final Integer[] ignoreTokenTypesArray = {
		TokenTypes.MODIFIERS,
		TokenTypes.TYPE,
		TokenTypes.PARAMETERS,
//		TokenTypes.PARAMETER_DEF,
		TokenTypes.OBJBLOCK,
		TokenTypes.ELIST
	};
	public static final List<Integer> ignoreTokenTypesList =  Arrays.asList (ignoreTokenTypesArray);
	public static final Set<Integer> ignoreTokenTypesSet =  new HashSet(ignoreTokenTypesList);
  
	public static final Integer[] infixTokenTypesArray = {
		TokenTypes.ASSIGN,
		TokenTypes.EQUAL,
		TokenTypes.NOT_EQUAL,
		TokenTypes.PLUS,
		TokenTypes.PLUS_ASSIGN,
		TokenTypes.MINUS,
		TokenTypes.MINUS_ASSIGN,
		TokenTypes.DIV,
		TokenTypes.DIV_ASSIGN,
		TokenTypes.STAR,
		TokenTypes.STAR_ASSIGN,
		TokenTypes.BAND,
		TokenTypes.BAND_ASSIGN,
		TokenTypes.BOR,
		TokenTypes.BOR_ASSIGN,
		TokenTypes.LOR,
		TokenTypes.LAND,
		TokenTypes.SL_ASSIGN,
		TokenTypes.SR_ASSIGN,		
		TokenTypes.DOT,
		
	};
	public static final List<Integer> infixTokenTypesList =  Arrays.asList (infixTokenTypesArray);
	public static final Set<Integer> infixTokenTypesSet =  new HashSet(infixTokenTypesList);
	
	public static String toStringListPrefix(DetailAST anAST) {
		DetailAST t = anAST;
		String ts = "";
		boolean printSelf = !ignoreTokenTypesSet.contains(anAST.getType());
		// boolean printInfix =
		// printSelf?infixTokenTypesSet.contains(anAST.getType()):false;
		// boolean printPrefix = printSelf && !printInfix;

		// if (t.getFirstChild() != null) ts += " (";
		// if (printPrefix
		if (printSelf)
			ts += " " + t.getText();
		if (t.getFirstChild() != null) {
			ts += toStringListPrefix(t.getFirstChild());
		}
		// if (printInfix)
		// ts += " " + t.getText();
		// if (t.getFirstChild() != null) ts += " )";
		if (t.getNextSibling() != null) {
			ts += toStringListPrefix(t.getNextSibling());
		}
		return ts;
	}
	public static String toStringListPrefixOrInfix(DetailAST anAST) {
		String ts = "";
		boolean printSelf = !ignoreTokenTypesSet.contains(anAST.getType());
		 boolean printInfix =
		 printSelf?infixTokenTypesSet.contains(anAST.getType()):false;
		 boolean printPrefix = printSelf && !printInfix;

		// if (t.getFirstChild() != null) ts += " (";
		// if (printPrefix
		if (printPrefix)
			ts += " " + anAST.getText();
		if (anAST.getFirstChild() != null) {
			ts += toStringListPrefixOrInfix(anAST.getFirstChild());
			 if (printInfix) {
				 ts += " " + anAST.getText();
			 }
			 DetailAST child = anAST.getFirstChild().getNextSibling();
			 while (child != null) {
					ts += toStringListPrefixOrInfix(child);
					child = child.getNextSibling();

			 }
			
		}
		// if (printInfix)
		// ts += " " + t.getText();
		// if (t.getFirstChild() != null) ts += " )";
		
//		if (t.getNextSibling() != null) {
//			ts += toStringList(t.getNextSibling());
//		}
		return ts;
	}
	public static String toStringList(DetailAST anAST) {
		return toStringListPrefixOrInfix(anAST);
	}
	protected boolean getVisitInnerClasses() {
		return ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses();
	}
	public static void trim (String[] aStrings) {
		for (int i = 0; i < aStrings.length; i++) {
			aStrings[i] = aStrings[i].trim();
		}
	}
	protected String getFullTypeName() {
		return fullTypeName;
	}
	protected void setFullTypeName(String newVal) {
	  if (fullTypeName == newVal) {
	    return;
	  }
		this.fullTypeName = newVal;
	}
	public String getShortTypeName() {
		return shortTypeName;
	}
	public void setShortTypeName(String shortTypeName) {
		this.shortTypeName = shortTypeName;
	}
	public static boolean isType(DetailAST anAST) {
	  int aType = anAST.getType();
	  return aType == TokenTypes.CLASS_DEF ||
	          aType == TokenTypes.INTERFACE_DEF ||
	          aType == TokenTypes.ANNOTATION_DEF ||
	          aType == TokenTypes.ENUM_DEF;
	}
	public static int typeNestingLevel(DetailAST anAST) {
	  int result = 0;
	  while (anAST != null) {
	    int aType = anAST.getType();
	    if (isType(anAST)) {
	      result++;
	    }
	    anAST = anAST.getParent();
	  }
	  return result;
	  
	}
	protected boolean foundSupuriousInnerClass = false; // should be an int
  protected boolean inOrEnteringInnerClassToBeNotVisited(DetailAST anAST) {
    boolean retVal = typeNestingLevel(anAST) > 1;
//    if (retVal) {
//      System.err.println ("found spurious inner class:" + currentFullFileName + " " + this);
//
//    }
    return retVal;
  }
  protected boolean stateFulFoundInnerClassToBeNotVisited(DetailAST anAST) {
    boolean retVal = (!getVisitInnerClasses() && getFullTypeName() != null);
//    if (!retVal && foundSupuriousInnerClass) {
//      System.err.println ("resetting spurious inner class:" + currentFullFileName);
 //
//    }
    if (retVal && !foundSupuriousInnerClass) {
      System.err.println ("setting spurious inner class:" + currentFullFileName + " " + this);

    }
//    
//    if (retVal) {
      foundSupuriousInnerClass = retVal;
//    }
    
    return retVal;
   }
  protected boolean inMethodOrConstructor;
  protected int methodOrConstructorNesting = 0;
  protected boolean incrementMethodOrConstructorNesting() {
    if (inMethodOrConstructor) {
      methodOrConstructorNesting++;
      return true;
    }
    return false;
  }
  protected boolean visitingNestedMethod(DetailAST ast) {
    if (methodOrConstructorNesting > 0) {
      switch (ast.getType()) {
        case TokenTypes.METHOD_DEF:
        case TokenTypes.CTOR_DEF:
          methodOrConstructorNesting++;
          return true;
         default: return true;
      }
    }
    return false;

  }
  protected boolean leavingNestedMethod(DetailAST ast) {
    if (methodOrConstructorNesting > 0) {
      switch (ast.getType()) {
        case TokenTypes.METHOD_DEF:
        case TokenTypes.CTOR_DEF:
          methodOrConstructorNesting--;
          return true;
         default: return true;
      }
    }
    return false;

  }
  protected boolean inSpuriosInnerClasses(DetailAST ast) {
//    boolean result = ast.getType() != TokenTypes.CLASS_DEF && enteringInnerClassToBeNotVisited(ast);
    boolean result = !isType(ast) && 
            inOrEnteringInnerClassToBeNotVisited(ast);

    return result;
  }
  protected boolean statefulCheckSpuriosInnerClasses(DetailAST ast) {
    return foundSupuriousInnerClass;
  }
  protected boolean statefulLeavingSpuriousInnerClass(DetailAST ast) {
//    if (!foundSupuriousInnerClass && ast.getType() == TokenTypes.CLASS_DEF ) {
//      System.err.println("not spurious inner class and class def");
//    }
    if (foundSupuriousInnerClass) {
      switch (ast.getType()) {
        case TokenTypes.CLASS_DEF:
          int i = 0;

        case TokenTypes.ENUM_DEF:

        case TokenTypes.ANNOTATION_DEF:

          foundSupuriousInnerClass = false;
          System.err.println("Resetting found spurious inner class:" + fullTypeName + " " + innerTypeNames.get(innerTypeNames.size() - 1));
          return true; //do not

        default:
          return true;
      }
    }
    return false;

  }
  public static DetailAST getClassOrInterfaceName (DetailAST aClassOrInterfaceDef) {
    return   aClassOrInterfaceDef.findFirstToken(TokenTypes.IDENT);

  }
  public static DetailAST getAnnotationTypeName (DetailAST anAnnotationDef) {
    return anAnnotationDef.getFirstChild()//Modifiers
            .getNextSibling()// @
            .getNextSibling()//interface
            .getNextSibling();//name
  }
}
