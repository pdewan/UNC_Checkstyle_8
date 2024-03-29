package unc.symbolTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

import unc.checks.ComprehensiveVisitCheck;
import unc.checks.STBuilderCheck;

public class AnSTTypeFromClass extends AnAbstractSTType implements STType {
	Class reflectedClass;
	STNameable[] computedTags, tags, configuredTags;
	Map emptyTable = new HashMap();
	static Object[] emptyArray = {};
//	AccessModifier accessModifier = AccessModifier.PUBLIC;
//	boolean isAbstract = false;
//C:\Users\dewan\Downloads\RxJava_java_only\java_only\history\1018-7babfaf1dcf8f20d02e0404c2f13f47c46a55391\commit_changes\rxjava-contrib\rxjava-swing\src\main\java\rx\subscriptions\SwingSubscriptions.java
	public AnSTTypeFromClass(String aClassName) {
		super (null, aClassName);
//		System.err.println("Added existing class:" + aClassName);
//		if (STBuilderCheck.isFirstPass()) {
//		  System.err.println("Adding class in first pass!" + );
//		}
//		if (aClassName.contains("ArrayList")) {
//			System.err.println("found ArrayList");
//		}
				

		
		declaredMethods = this.emptyMethodArray;
//    methods = null;
    allComputedTags = emptyNameableArray;
    tags =  emptyNameableArray;
    configuredTags =  emptyNameableArray;
    declaredInterfaces =  emptyNameableArray;
//    interfaces  =  emptyNameableArray ;
    declaredFields =  emptyNameableArray;
    globalSTVariables = emptyList;
    

     

		
//		globalSTVariables = new ArrayList();

	}
	public AnSTTypeFromClass(Class aClass) {
		super (null, aClass.getName());
//		if (aClass.getName().contains("ArrayList")) {
//			System.out.println("Found arraylist");
//		}
		external = true;
		reflectedClass = aClass;
		Method[] aMethods = aClass.getDeclaredMethods();	
		declaredMethods = new STMethod[aMethods.length];
		for (int i = 0; i < declaredMethods.length; i++) {
			declaredMethods[i] = new AnSTMethodFromMethod(aMethods[i]);
		}
		Constructor[] aConstructors = aClass.getDeclaredConstructors();
		declaredConstructors = new STMethod[aConstructors.length];
		for (int i = 0; i < declaredConstructors.length; i++) {
			declaredConstructors[i] = new AnSTMethodFromConstructor(aConstructors[i]);
		}	
		Package aPackage = aClass.getPackage();
		if (aPackage == null) {
		  packageName = "default";
		} else {
		packageName = aClass.getPackage().getName();
		}
		Class[] anInterfaces = aClass.getInterfaces();
		declaredInterfaces = new AnSTNameable[anInterfaces.length];
		for (int i = 0; i < declaredInterfaces.length; i++) {
			declaredInterfaces[i] = new AnSTNameable(anInterfaces[i].getName());
		}
		Field[] aFields = aClass.getFields();
		declaredFields = new STNameable[aFields.length];
		globalSTVariables = new ArrayList(aFields.length);
		for (int i = 0; i < declaredFields.length; i++) {
			declaredFields[i] = new AnSTNameable(aFields[i].getName());
			AccessModifier aFieldAccessModifier = STBuilderCheck.toAccessModifier(aFields[i].getModifiers());
//			globalSTVariables.add(
//						new AnSTVariable(this, null, null, aFields[i].getName(), this.getName(), null, VariableKind.GLOBAL, ComprehensiveVisitCheck.getAccessToken(aFields[i]), 
//								null, aFieldAccessModifier ));
			globalSTVariables.add(
					new AnSTVariableFromField(this, aFields[i]));
		}
		
		Class aSuperClass = aClass.getSuperclass();
		if (aSuperClass != null ) {
		   superClass = STBuilderCheck.getExistingClassSTType(aSuperClass);
		   if (superClass == null) {
			   superClass = new AnSTNameable(aSuperClass.getName());
		   }
//		   if (superClass != null) {
//		   STBuilderCheck.addSTType(((STType) superClass)); 
//		   }
		}
		STNameable typeNameable = new AnSTNameable(null, getName());
		computedTags = new STNameable[] {typeNameable};
		
		accessModifier = STBuilderCheck.toAccessModifier(aClass.getModifiers());
		isAbstract = Modifier.isAbstract(aClass.getModifiers());
		typeType = computeTypeType();
//    isClass = typeType == TypeType.CLASS;
		
		
	}
	
	
	

//	@Override
//	public String getName() {
//		return reflectedClass.getName();
//	}

	@Override
	public Object getData() {
		return null;
	}

//	@Override
//	public DetailAST getAST() {
//		return null;
//	}

//	@Override
//	public STMethod[] getDeclaredMethods() {
//		return null;
//	}
//
//	@Override
//	public STMethod[] getMethods() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public STNameable[] getInterfaces() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public STMethod getMethod(String aName, String[] aParameterTypes) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getPackage() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public boolean isInterface() {
//		// TODO Auto-generated method stub
//		if (reflectedClass == null) {
//			return false;
//		}
//		return reflectedClass.isInterface();
//	}
//	Boolean isClass;
//	 @Override
//	  public boolean isClass() {
//	   return isClass;
//	  }

//	@Override
//	public STNameable getSuperClass() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public STNameable getStructurePatternName() {
		// TODO Auto-generated method stub
		return null;
	}
	static STNameable[] emptyNemable = new AnSTNameable[0];

	@Override
	public STNameable[] getDeclaredPropertyNames() {
		return emptyNemable;
	}

	@Override
	public STNameable[] getDeclaredEditablePropertyNames() {
		return emptyNemable;
	}

	@Override
	public STNameable[] getTags() {
		return emptyNemable;
	}

	
	@Override
	public STNameable[] getImports() {
		// TODO Auto-generated method stub
		return emptyNemable;
	}

	

	@Override
	public STNameable[] getAllDeclaredPropertyNames() {
		// TODO Auto-generated method stub
		return emptyNemable;
	}

	
//
//
//	@Override
//	public List<String> signaturesCommonWith(String aTypeName) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getSubTypes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getPeerTypes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<STNameable> superTypesInCommonWith(String anOtherType) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<STNameable> superTypesInCommonWith(STType anOtherType) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> namesOfSuperTypesInCommonWith(String anOtherType) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getSignatures() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
	@Override
	public void findDelegateTypes() {
		
	}
//
//	@Override
//	public List<STNameable> getAllInterfaces() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Boolean isSubtypeOf(String aName) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public Boolean isDelegate(String aName) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public boolean isParsedClass() {
		return false;
	}
	static Set emptySet = new HashSet();
	@Override
	public Set<STType> getDelegates() {
		// TODO Auto-generated method stub
		return emptySet;
	}
    @Override
	public boolean waitForSuperTypeToBeBuilt() {
		return false;
	}




//	@Override
//	public boolean isEnum() {
//		if (reflectedClass == null) return false;
//		
//		return reflectedClass.isEnum();
//		
//	}




	@Override
	public STNameable[] getComputedTags() {
		// TODO Auto-generated method stub
		return computedTags;
	}



//
//	@Override
//	public Set<String> getDeclaredGlobals() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//	@Override
//	public String getDeclaredGlobalVariableType(String aGlobal) {
//		// TODO Auto-generated method stub
//		return null;
//	}



//	@Override
	public List<CallInfo> getMethodsCalled() {
		// TODO Auto-generated method stub
		return new ArrayList();
	}


	static protected List<STNameable> emptyNameableList = new ArrayList();
	@Override
	public List<STNameable> getTypesInstantiated() {
		// TODO Auto-generated method stub
		return emptyNameableList;
	}


	List<STMethod> emptyMethodList = new ArrayList();

	@Override
	public List<STMethod> getInstantiatingMethods(String aTypeName) {
		// TODO Auto-generated method stub
		return emptyMethodList;
	}




	@Override
	public Boolean instantiatesType(String aShortOrLongName) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public List<CallInfo> getAllMethodsCalled() {
		// TODO Auto-generated method stub
		return getMethodsCalled();
	}




//	@Override
//	public DetailAST getDeclaredGlobalVariableToRHS(String aGlobal) {
//		// TODO Auto-generated method stub
//		return null;
//	}




	@Override
	public List<STVariable> getDeclaredSTGlobals() {
		// TODO Auto-generated method stub
		return globalSTVariables;
	}




//	@Override
//	public STVariable getDeclaredGlobalSTVariable(String aGlobal) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	@Override
	public STMethod getGetter(String aPropertyName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public STMethod getSetter(String aPropertyName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public STNameable[] getConfiguredTags() {
		return emptyNameables;
	}
	@Override
	public STNameable[] getDerivedTags() {
		// TODO Auto-generated method stub
		return emptyNameables;
	}
	@Override
	public int getNumberOfAsserts() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getNumberOfTernaryConditionals() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public STNameable[] getDeclaredFields() {
		// TODO Auto-generated method stub
		return declaredFields;
	}


	@Override
	public boolean isExternal() {
		return true;
	}
	 @Override
	  public boolean isUnknownExternal() {
	    return reflectedClass == null ;
	  }
//	@Override
//	public Map<String, PropertyInfo> getDeclaredPropertyInfos() {
//		return null;
//	}
	@Override
	public String getFileName() {
		return getName();
	}
	@Override
	public boolean isGeneric() {
		return reflectedClass.getTypeParameters().length > 0;
	}
	@Override
	public List<String> getTypeParameterNames() {
		return null;
	}
//	@Override
//	public boolean isAnnotation() {
//		if (reflectedClass == null) return false;
//		
//		return reflectedClass.isAnnotation();
//		// TODO Auto-generated method stub
//	}
  @Override
  public FileContents getFileContents() {
    // TODO Auto-generated method stub
    return null;
  }
//  @Override
  public void checkIfDelegate(STType aPeerType, List<STMethod> anOverriddenMethods) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public String getMatchedTags() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public void setMatchedTags(String matchedTags) {
    // TODO Auto-generated method stub
    
  }
  protected TypeType computeTypeType() {
   return reflectedClass.isInterface()?TypeType.INTERFACE:
       reflectedClass.isAnnotation()?TypeType.ANNOTATION:
         reflectedClass.isEnum()?TypeType.ENUM:TypeType.CLASS;
     
  }
  @Override
  public TypeType getTypeType() {
    
    return typeType;
  }
  @Override
  public List<DetailAST> getInnerTypesAST() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public List<String> getInnerTypeNames() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public STNameable[] getAllTags() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public void setAllTags(STNameable[] newVal) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void setAllTagsSet(Set<STNameable> newVal) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public Set<STNameable> getAllTagsSet() {
    // TODO Auto-generated method stub
    return null;
  }


//
//
//	@Override
//	public Set<String> getDelegates() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
