package unc.symbolTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

import unc.checks.STBuilderCheck;
import unc.checks.TagBasedCheck;

public class ASymbolTable implements SymbolTable{
  protected SymbolTable previousSymbolTable;

  STType objectType;

  Set<String> packageNames =  new HashSet<>();
	Map<String, DetailAST> methodCallToAST = new HashMap();
	Map<String, STType>   typeNameToSTClass = new HashMap<>();	
	@Override

	public Map<String, DetailAST> getMethodCallToAST() {
		return methodCallToAST;
	}

	public static boolean typeMatches(String aFullName, String aShortOrFullName) {
		return aFullName.equals(aShortOrFullName) || aFullName.endsWith("." + aShortOrFullName);
	}
	@Override
	public boolean isType(String aTypeName) {
		STType aClass = getSTClassByShortName(aTypeName);
		return aClass != null;
	}

	@Override
	public boolean isInterface (String aTypeName) {
		STType aClass = getSTClassByShortName(aTypeName);
		return aClass != null && aClass.isInterface();

	}
	@Override
	public boolean isClass (String aTypeName) {
		STType aClass = getSTClassByShortName(aTypeName);
		return aClass != null && !aClass.isInterface() && !aClass.isEnum() && !aClass.isAnnotation();
//		return matchingFullClassNames(aTypeName).size() >= 1;
	}

	@Override
	public List<String> matchingFullSTTypeNames (String aTypeName) {
		List<String> result = new ArrayList();
		Set<String> aFullNames = typeNameToSTClass.keySet();
		for (String aFullName:aFullNames) {
			if (typeMatches(aFullName, aTypeName)) {
				result.add(aFullName);
			}
		}
		return result;
	}

	@Override
	public STType getSTClassByShortName(String aTypeName) {
		STType retVal = getSTClassByFullName(aTypeName);
		if (retVal != null) {
			return retVal;
		}
		List<String> aFullNames =  matchingFullSTTypeNames(aTypeName);
		if (aFullNames.size() == 0) {
//			System.err.println("No full type name with short name" + aTypeName);
			return null;
		}
		if (aFullNames.size()> 1) {
//			System.err.println("Ambiguous short type names in " + aFullNames + ", returning null:" + aTypeName);
			return null;
		}
		return getSTClassByFullName(aFullNames.get(0));
	}
	@Override
	public STType getSTClassByFullName(String aTypeName) {
//	  if (aTypeName.equals("System")) {
//	    System.err.println("Found system");
//	  }
		if (aTypeName == null) {
			System.err.println("Null type name");
			return null;
		}
		

		STType anSTType = typeNameToSTClass.get(aTypeName);

		if (anSTType == null) {
		  if (getPreviousSymbolTable() != null) {
		    anSTType = getPreviousSymbolTable().getSTClassByFullName(aTypeName);
		  }
		  if (anSTType == null) {
			boolean isExternalClass = aTypeName.startsWith("java.lang") ;
			
			// we do not need to guess external class with two pass scheme
//			        || 
//					STBuilderCheck.isExternalImportCacheChecking(aTypeName) ;
					
			if (!isExternalClass && STBuilderCheck.isJavaLangClass(aTypeName)) {
				if (Character.isUpperCase(aTypeName.charAt(0))) {
					aTypeName = "java.lang." + aTypeName;
					isExternalClass = true;
					 anSTType = typeNameToSTClass.get(aTypeName);
					 if (anSTType != null) {
						 return anSTType;
					 }

				}
			
			}
	     if (STBuilderCheck.getImportsAsExistingClasses() && (isExternalClass || !STBuilderCheck.getLatestInstance().isFirstPass())) {
	       
	     
	       
	    

//			if (isExternalClass && STBuilderCheck.getImportsAsExistingClasses()) {
				try {
					Class aClass = Class.forName(aTypeName);
					return STBuilderCheck.addExistingClassSTType(aClass);

				} catch (ClassNotFoundException e) {
					return STBuilderCheck.addExistingClassSTType(aTypeName);
				}
			}
		}
		}
		return anSTType;

	}

	@Override
	public STType putSTType(String aName, STType anSTType) {
		packageNames.add(aName);
//		if (aName.equals("io.reactivex.rxjava3.internal.util.NotificationLite")) {
//		  System.err.println("Found target type");
//		}
		if (aName.equals("java.lang.Object")) {
		  objectType = anSTType;
		}
		return typeNameToSTClass.put(aName, anSTType);
	}
	@Override
	public List<String> getAllTypeNames() {
		return new ArrayList(typeNameToSTClass.keySet());
	}
	@Override
	public List<String> getAllClassNames() {
		List<String> aResult = new ArrayList();
		for (String aTypeName: typeNameToSTClass.keySet() ) {
			STType aType = typeNameToSTClass.get(aTypeName);
			if (!aType.isInterface())
				aResult.add(aTypeName);
		}
		return aResult;
		
	}
	@Override
	public List<String> getAllInterfaceNames() {
		List<String> aResult = new ArrayList();
		for (String aTypeName: typeNameToSTClass.keySet() ) {
			STType aType = typeNameToSTClass.get(aTypeName);
			if (aType.isInterface())
				aResult.add(aTypeName);
		}
		return aResult;
		
	}
	
	@Override
	public Collection<STType> getAllSTTypes() {
//		return new ArrayList(typeNameToSTClass.values());
//    return new ArrayList(typeNameToSTClass.values());
	  return typeNameToSTClass.values();

	}
	@Override
	public void clear() {
		methodCallToAST.clear();
		typeNameToSTClass.clear();
		
	}
	@Override
	public Set<String> getTypeNamesKeySet() {
		return typeNameToSTClass.keySet();
	}
	@Override
	public Set<String> getPackageNames() {
		return packageNames;
	}
	@Override
	public int size() {
		return typeNameToSTClass.size();
	}
	@Override
	 public STType getAndMaybePutObjectType() {
	  if (objectType == null) {
	    if (getPreviousSymbolTable() != null) {
	      objectType = getPreviousSymbolTable().getAndMaybePutObjectType();
	    }
	    if (objectType == null) {
	    
	    putSTType(Object.class.getCanonicalName(), new AnSTTypeFromClass(Object.class));
	    }
	  }
	    return objectType;
	  }
	@Override
  public SymbolTable getPreviousSymbolTable() {
    return previousSymbolTable;
  }
	@Override
  public void setPreviousSymbolTable(SymbolTable newVal) {
    this.previousSymbolTable = newVal;
  }

 
}
