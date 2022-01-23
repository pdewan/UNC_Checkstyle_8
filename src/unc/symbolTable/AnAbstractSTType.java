package unc.symbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

import unc.checks.ComprehensiveVisitCheck;
import unc.checks.STBuilderCheck;
import unc.checks.TagBasedCheck;
import unc.checks.TypeVisitedCheck;

public abstract class AnAbstractSTType extends AnSTNameable implements STType {
  // protected STNameable[] declaredPropertyNames,
  // declaredEditablePropertyNames;
  // protected final STNameable[] imports;
  protected Set<STType> subSTTypes;
  protected TypeType typeType;

  protected boolean external = false;

  protected STMethod staticBlocks;
  
//  protected Map<STMethod, List<STType>> methodToOverriddingType;

  protected STMethod[] initMethods;
  // protected List<STVariable> globalSTVariables;

  protected STMethod[] declaredMethods; // initialized by subclass
  protected STMethod[] methods; // initialized on demand
  protected STMethod[] nonExternalMethods; // initialized on demand
  protected STNameable[] allComputedTags; // initialized on demand
  protected STMethod[] declaredConstructors;
  protected STNameable[] declaredInterfaces;
  protected STNameable[] interfaces;
  protected STNameable[] declaredFields;
  protected List<STVariable> globalSTVariables;
  protected Set<STVariable> allGlobalSTVariables;

  protected String packageName;
  // protected final boolean isInterface, isGeneric, isElaboration;
  protected STNameable superClass;
  // protected final STNameable structurePatternName;
  // protected STMethod[] inits;
  // protected Map<String, PropertyInfo> declaredPropertyInfo = new HashMap();
  protected Map<String, PropertyInfo> declaredPropertyInfo = null;

  protected Map<String, PropertyInfo> allPropertyInfos; // initialized on demand
  // protected List<STMethod> declaredInits = new ArrayList();
  // protected Map<String, List<CallWithoutArguments>> globalVariableToCall =
  // new HashMap();
  protected Set<STType> delegates;
  protected Set<STType> delegators;

  protected Set<String> stringDelegates;

  protected boolean hasSetter = false;
  protected List<String> subTypes;
  protected List<STNameable> superTypes;

  protected Set<STType> stSubTypes;
  // protected Set<STType> superClasses = new HashSet();

  protected STType stSuperClass;
  protected List<STNameable> allTypes;
  // protected AccessModifier accessModifier = AccessModifier.PACKAGE;
  protected boolean isAbstract = false;
  protected Set<STType> referenceTypes;
  protected List<AccessModifierUsage> accessModifierUsage;

  protected long timeOfEntry;
  protected boolean isRecursive;

  // Set<STType> superTypes = new HashSet();

//  public Map<STMethod, List<STType>> getMethodToOverriddingType() {
//    if (methodToOverriddingType == null) {
//      methodToOverriddingType = new HashMap<>();
//    }
//    return methodToOverriddingType;
//  }

  public AnAbstractSTType(DetailAST ast, String name) {
    super(ast, name);
    timeOfEntry = System.currentTimeMillis();

  }

  @Override
  public boolean waitForSuperTypeToBeBuilt() {
    return true;
  }

  public STMethod[] getDeclaredMethods() {
    return declaredMethods;
  }

  @Override
  public STMethod[] getDeclaredConstructors() {
    return declaredConstructors;
  }

  @Override
  public STNameable[] getDeclaredInterfaces() {
    if (declaredInterfaces == null) {
      declaredInterfaces = emptyNameableArray;
    }
    return declaredInterfaces;
  }

  @Override
  public String getPackage() {
    return packageName;
  }

  // public boolean isInterface() {
  // return isInterface;
  // }
  public static void addToList(List aList, Object[] anAdditions) {
    for (Object anAddition : anAdditions) {
      if (aList.contains(anAddition))
        continue;
      aList.add(anAddition);
    }
  }

  protected STMethod[] emptyMethods = new STMethod[0];
  protected List<CallInfo> emptyCalls = new ArrayList();

  @Override
  public STMethod[] getMethods() {
    if (methods == null) {
      methods = computeMethods();
      if (methods != null) {
        if (getStaticBlocks() != null) {
          getStaticBlocks().refreshUnknowns();
        }
        for (STMethod aMethod : methods) {
          aMethod.refreshUnknowns();
        }
      }
    }
    return methods;
  
  }
  
  
  @Override
  public STMethod[] getNonExternalMethods() {
    
    STMethod[] aMethods = getMethods();
    if (aMethods == null){
      return null;
    }
    if (aMethods.length == 0) {
      return emptyMethodArray;
    }
    List<STMethod> aListRetVal = new ArrayList();
    for (STMethod aMethod:aMethods) {
      if (aMethod.getDeclaringSTType() != null && aMethod.getDeclaringSTType().isExternal()) {
        continue;
      }
      aListRetVal.add(aMethod);
    }
    return aListRetVal.toArray(emptyMethodArray);
  
  }

  // static List superType = new ArrayList(1);
  public STMethod[] computeMethods() {

    List<STMethod> retVal = new ArrayList();
    if (getTypeType() == null) { // existing unknown class with no methods
//      addToList(retVal, SymbolTableFactory.getOrCreateSymbolTable().getAndMaybePutObjectType().getMethods());
      return retVal.toArray(emptyMethods);

    }

    addToList(retVal, getDeclaredMethods());

    if (!isClass() && !isInterface()) {
      return retVal.toArray(emptyMethods);
    }

    List<STNameable> aDeclaredTypes = null;

    if (isInterface()) {
      // anAllTypes = getAllSuperTypes();
      aDeclaredTypes = Arrays.asList(getDeclaredInterfaces());
    } else {

      aDeclaredTypes = new ArrayList(1);
      STNameable aSuperClass = getSuperClass();
      if (aSuperClass != null) {
        aDeclaredTypes.add(getSuperClass());
      } else {
        return retVal.toArray(emptyMethods);
      }
    }

    if (aDeclaredTypes == null) { // should never happen
      return null;
    }

    for (STNameable aSuperType : aDeclaredTypes) {
      if (aSuperType == null || aSuperType.getName() == null) {
        return null;
      }
      // if (TagBasedCheck.isExternalType(aSuperType.getName()) &&
      // !STBuilderCheck.getImportsAsExistingClasses()) {
      if (!STBuilderCheck.getImportsAsExistingClasses()
              && TagBasedCheck.isExternalType(aSuperType.getName())) {
        continue;
      }
      Object aSuperTypeMethods = addMethodsOfSuperType(retVal, aSuperType);

      if (aSuperTypeMethods == null) {
        return null;
      }
    }
    //
    // STNameable aSuperType = getSuperClass(); // so this does not look at
    // // super interfaces
    // Object aSuperTypeMethods = addMethodsOfSuperType(retVal, aSuperType);
    // if (aSuperTypeMethods == null) {
    // return null;
    // }

    // if (aSuperType != null
    // && !TagBasedCheck.isExternalClass(aSuperType.getName())) {
    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aSuperType.getName());
    // if (anSTType == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return retVal.toArray(emptyMethods);
    // }
    // STMethod[] superTypeMethods = anSTType.getMethods();
    // if (superTypeMethods == null) // some supertype not compiled
    // return null;
    // // addToList(retVal, anSTType.getMethods());
    // addToList(retVal, superTypeMethods);
    //
    // }
    return retVal.toArray(emptyMethods);
  }

  public STMethod[] oldComputeMethods() {

    List<STMethod> retVal = new ArrayList();
    addToList(retVal, getDeclaredMethods());
    List<STNameable> anAllTypes = null;
    anAllTypes = getAllSuperTypes(); // why only for interfaces

    // if (isInterface()) {
    // anAllTypes = getAllSuperTypes();
    // } else {
    // anAllTypes = new ArrayList();
    // STNameable aSuperClass = getSuperClass();
    // if (aSuperClass != null) {
    // anAllTypes.add(getSuperClass());
    // }
    // }

    if (anAllTypes == null) { // should never happen
      return null;
    }

    for (STNameable aSuperType : anAllTypes) {
      if (aSuperType == null || aSuperType.getName() == null) {
        return null;
      }
      // if (TagBasedCheck.isExternalType(aSuperType.getName()) &&
      // !STBuilderCheck.getImportsAsExistingClasses()) {
      if (!STBuilderCheck.getImportsAsExistingClasses()
              && TagBasedCheck.isExternalType(aSuperType.getName())) {
        continue;
      }
      Object aSuperTypeMethods = addMethodsOfSuperType(retVal, aSuperType);
      if (aSuperTypeMethods == null) {
        return null;
      }
    }
    //
    // STNameable aSuperType = getSuperClass(); // so this does not look at
    // // super interfaces
    // Object aSuperTypeMethods = addMethodsOfSuperType(retVal, aSuperType);
    // if (aSuperTypeMethods == null) {
    // return null;
    // }

    // if (aSuperType != null
    // && !TagBasedCheck.isExternalClass(aSuperType.getName())) {
    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aSuperType.getName());
    // if (anSTType == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return retVal.toArray(emptyMethods);
    // }
    // STMethod[] superTypeMethods = anSTType.getMethods();
    // if (superTypeMethods == null) // some supertype not compiled
    // return null;
    // // addToList(retVal, anSTType.getMethods());
    // addToList(retVal, superTypeMethods);
    //
    // }
    return retVal.toArray(emptyMethods);
  }

  // public STMethod[] computeMethodsOld() {
  //
  // List<STMethod> retVal = new ArrayList();
  // addToList(retVal, getDeclaredMethods());
  // STNameable aSuperType = getSuperClass(); // so this does not look at super interfaces
  // if (aSuperType != null
  // && !TagBasedCheck.isExternalClass(aSuperType.getName())) {
  // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
  // .getSTClassByShortName(aSuperType.getName());
  // if (anSTType == null) {
  // if (waitForSuperTypeToBeBuilt())
  // return null;
  // else
  // return retVal.toArray(emptyMethods);
  // }
  // STMethod[] superTypeMethods = anSTType.getMethods();
  // if (superTypeMethods == null) // some supertype not compiled
  // return null;
  //// addToList(retVal, anSTType.getMethods());
  // addToList(retVal, superTypeMethods);
  //
  // }
  // return retVal.toArray(emptyMethods);
  // }
  public List<STMethod> addMethodsOfSuperType(List<STMethod> retVal, STNameable aSuperType) {

    if (aSuperType != null && (!TagBasedCheck.isExternalClass(aSuperType.getName())
            || STBuilderCheck.getImportsAsExistingClasses())) {
      // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
      // .getSTClassByShortName(aSuperType.getName());
      STType anSTType = null;
      if (aSuperType instanceof STType) {
        anSTType = (STType) aSuperType;
      } else {
        anSTType = SymbolTableFactory.getOrCreateSymbolTable()
                .getSTClassByFullName(aSuperType.getName());
      }
      if (anSTType == null) {
        if (waitForSuperTypeToBeBuilt())
          return null;
        else
          return retVal;
      }
      STMethod[] superTypeMethods = anSTType.getMethods();
      if (superTypeMethods == null) // some supertype not compiled
        return null;
      // addToList(retVal, anSTType.getMethods());
      addToList(retVal, superTypeMethods);

    }
    return retVal;
  }

  // static STMethod[] emptySTMethods = {};
  //
  // public static STMethod[] toSTMethods (CallInfo aCallInfo) {
  //
  // String[] aCalledMethod = aCallInfo.getNormalizedCall();
  // String aCalledMethodName = aCalledMethod[1];
  // String aCalledMethodClassName = aCalledMethod[0];
  // if (aCalledMethod.length > 2 || aCalledMethodClassName == null ||
  // TagBasedCheck.isExternalClass(aCalledMethodClassName))
  // return emptySTMethods;
  // STType aCalledMethodClass =
  // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aCalledMethodClassName);
  // if (aCalledMethodClass == null) {
  //// System.err.println("Null called method class:" + aCalledMethodClassName);
  // return null;
  // }
  // return aCalledMethodClass.getMethods(aCalledMethodName);
  //
  //
  // }
  //
  // public static STMethod[] initAllCalledAndCallingMethods (STMethod aMethod) {
  // List<STMethod> result = new ArrayList();
  //// STType aDeclaringType = aMethod.getDeclaringSTType();
  //// if (aDeclaringType == null) {
  //// System.err.println("Declaring type should not be null");
  //// return null;
  //// }
  // CallInfo[] aCalledMethods = aMethod.methodsCalled();
  // for (CallInfo aCallInfo:aCalledMethods) {
  // STMethod[] anAllDirectlyCalledMethods = toSTMethods(aCallInfo);
  // if (anAllDirectlyCalledMethods == null)
  // return null;
  // result.addAll(Arrays.asList(anAllDirectlyCalledMethods));
  // for (STMethod aDirectlyCalledMethod:anAllDirectlyCalledMethods) {
  // STMethod[] anAllIndirectlyCalledMethods =
  // initAllCalledAndCallingMethods(aDirectlyCalledMethod);
  // if (anAllIndirectlyCalledMethods == null) {
  // return null;
  // }
  // result.addAll(Arrays.asList(anAllIndirectlyCalledMethods));
  // }
  // }
  // return result.toArray(emptySTMethods);
  //
  //
  // }

  public STNameable[] computeAllComputedTags() {
    List<STNameable> retVal = null;
    addToList(retVal, getComputedTags());
    STNameable aSuperType = getSuperClass();
    if (aSuperType != null && !TagBasedCheck.isExternalClass(aSuperType.getName())) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aSuperType.getName());
      if (anSTType == null) {
        if (waitForSuperTypeToBeBuilt())
          return null;
        else
          return (STNameable[]) nullToEmptyList(retVal).toArray(emptyMethods);
      }
      STNameable[] superTypeTags = anSTType.getAllComputedTags();
      if (superTypeTags == null) // some supertype not compiled
        return null;
      // addToList(retVal, anSTType.getMethods());
      addToList(retVal, superTypeTags);

    }
    return retVal.toArray(emptyNameables);
  }

  public STNameable[] getAllComputedTags() {
    if (allComputedTags == null)
      allComputedTags = computeAllComputedTags();
    return allComputedTags;
    // List<STNameable> retVal = new ArrayList();
    // addToList(retVal, getComputedTags());
    // STNameable aSuperType = getSuperClass();
    // if (aSuperType != null
    // && !TagBasedCheck.isExternalClass(aSuperType.getName())) {
    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aSuperType.getName());
    // if (anSTType == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return retVal.toArray(emptyMethods);
    // }
    // STNameable[] superTypeTags = anSTType.getAllComputedTags();
    // if (superTypeTags == null) // some supertype not compiled
    // return null;
    //// addToList(retVal, anSTType.getMethods());
    // addToList(retVal, superTypeTags);
    //
    // }
    // return retVal.toArray(emptyNameables);
  }

  @Override
  public STMethod getMethod(String aName, String[] aParameterTypes) {
    // STMethod[] aMethods = getMethods();
    // if (aMethods == null) {
    // return null;
    // }
    STMethod retVal = getMethod(getDeclaredMethods(), aName, aParameterTypes);
    if (retVal != null) {
      return retVal;
    }
    retVal = getMethod(getMethods(), aName, aParameterTypes);
    if (retVal == null) {
      System.err.println ("No method found for " + aName + " in " + this.getName());
    }
    return retVal;

    // for (STMethod aMethod : aMethods) {
    // if (aMethod.getName().equals(aName)
    // && aMethod.getParameterTypes().equals(aParameterTypes))
    // return aMethod;
    // }
    // return null;
  }

  public static STMethod getMethod(STMethod[] aMethods, String aName, String[] aParameterTypes) {
    if (aMethods == null) {
      return null;
    }

    // int i = 5;
    // Set<STMethod> aMatchedNames = new HashSet();
    Set<STMethod> aMatchedNames = null;

    for (STMethod aMethod : aMethods) {
      // if (aMethod.getName().equals(aName)
      // && Arrays.asList(aMethod.getParameterTypes()).equals(Arrays.asList(aParameterTypes)))
      // return aMethod;
//      if (aMethod.getName().equals(aName)) {
//        aMatchedNames = nullToNewSet(aMatchedNames);
//        aMatchedNames.add(aMethod);
//      }
//      if (Arrays.asList(aMethod.getParameterTypes()).equals(Arrays.asList(aParameterTypes))) {
//        return aMethod;
//      }
      if (aMethod.getName().equals(aName)) {
        aMatchedNames = nullToNewSet(aMatchedNames);
        aMatchedNames.add(aMethod);
//      }
      if (Arrays.asList(aMethod.getParameterTypes()).equals(Arrays.asList(aParameterTypes))) {
        return aMethod;
      }
      }

    }
    if (aMatchedNames == null || aMatchedNames.size() == 0) {
//      System.err.println("no method found for  " + Arrays.toString(aMethods));
      return null;
    }
    if (aMatchedNames.size() == 1) {
      return aMatchedNames.iterator().next();
    }
    if (aMatchedNames.size() > 1) {
      System.err.println("multiple methods found:" + aMatchedNames + " " + Arrays.toString(aMethods));

      return null;
      // return aMatchedNames.iterator().next();;
    }
    return null;
  }

  @Override
  public STMethod getDeclaredMethod(String aName, String[] aParameterTypes) {
    STMethod retVal = getMethod(getDeclaredMethods(), aName, aParameterTypes);
    if (retVal == null) {
      System.err.println ("No method found for " + aName + " in " + this.getName());
    }
    return retVal;

  }

  protected STMethod[] emptyMethodArray = new STMethod[0];

  public STMethod[] computeAllMethods(String aName) {
    // List<STMethod> resultList = new ArrayList();
    List<STMethod> resultList = null;

    STMethod[] aMethods = getMethods();

    if (aMethods == null) {
      if (waitForSuperTypeToBeBuilt())
        return null;
      else
        aMethods = getDeclaredMethods();
    }
    for (STMethod aMethod : aMethods) {
      if (aMethod.getName().equals(aName)) {
        resultList = nullToNewList(resultList);

        resultList.add(aMethod);
      }
    }
    return (STMethod[]) nullToEmptyList(resultList).toArray(emptyMethodArray);

  }

  @Override
  public STMethod[] getMethods(String aName) {
    // List<STMethod> resultList = new ArrayList();
    List<STMethod> resultList = null;

    STMethod[] aMethods = getMethods();

    if (aMethods == null) {
      if (waitForSuperTypeToBeBuilt())
        return null;
      else
        aMethods = getDeclaredMethods();
    }
    for (STMethod aMethod : aMethods) {
      if (aMethod.getName().equals(aName)) {
        resultList = nullToNewList(resultList);
        resultList.add(aMethod);
      }
    }
    return (STMethod[]) nullToEmptyList(resultList).toArray(emptyMethodArray);

  }

  @Override
  public STMethod[] getDeclaredMethods(String aName) {
    // List<STMethod> resultList = new ArrayList();
    List<STMethod> resultList = null;

    STMethod[] aMethods = getDeclaredMethods();

    // if (aMethods == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // aMethods = getDeclaredMethods();
    // }
    for (STMethod aMethod : aMethods) {
      if (aMethod.getName().equals(aName))
        (resultList = nullToNewList(resultList)).add(aMethod);
    }
    return (STMethod[]) nullToEmptyList(resultList).toArray(emptyMethodArray);

  }

  @Override
  public STMethod[] getDeclaredMethods(String aName, int aNumParameters) {
    // List<STMethod> resultList = new ArrayList();
    List<STMethod> resultList = null;
    STMethod[] aMethods = getDeclaredMethods();

    // if (aMethods == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // aMethods = getDeclaredMethods();
    // }
    for (STMethod aMethod : aMethods) {
      if (aMethod.getParameterTypes() == null) {
        System.err.println("Nll parameter names");
      }
      if (aMethod.getName().equals(aName) && aMethod.getParameterTypes().length == aNumParameters)
        (resultList = nullToNewList(resultList)).add(aMethod);
    }
    return (STMethod[]) nullToEmptyList(resultList).toArray(emptyMethodArray);

  }

  @Override
  public STMethod[] getMethods(String aName, int aNumParameters) {
    // List<STMethod> resultList = new ArrayList();
    List<STMethod> resultList = null;
    STMethod[] aMethods = getMethods();
    if (aMethods == null) {
      return null;
    }

    // if (aMethods == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // aMethods = getDeclaredMethods();
    // }
    for (STMethod aMethod : aMethods) {
      if (aMethod.getParameterTypes() == null) {
        System.err.println("Nll parameter names");
      }
      if (aMethod.getName().equals(aName) && aMethod.getParameterTypes().length == aNumParameters)
        (resultList = nullToNewList(resultList)).add(aMethod);
    }
    return (STMethod[]) nullToEmptyList(resultList).toArray(emptyMethodArray);

  }

  @Override
  public STNameable getSuperClass() {
    return superClass;
  }


  // @Override
  // public STNameable[] getDeclaredPropertyNames() {
  // return declaredPropertyNames;
  // }
  // recursion is safer
  @Override
  public STNameable[] getAllDeclaredPropertyNames() {
    List<STNameable> result = null;
    // STNameable[] aPropertyNames = getDeclaredPropertyNames();
    STNameable[] aPropertyNames;

    STType anSTClass = this;
    while (true) {
      aPropertyNames = anSTClass.getDeclaredPropertyNames();
      for (STNameable aNameable : aPropertyNames) {
        (result = nullToNewList(result)).add(aNameable);
      }
      STNameable aSuperClass = anSTClass.getSuperClass();
      if (aSuperClass == null || TagBasedCheck.isExternalClass(aSuperClass.getName()))
        break;
      STType anSTSuperClass = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aSuperClass.getName());
      if (anSTSuperClass == null) {
        if (anSTClass.waitForSuperTypeToBeBuilt())
          return null;
        else
          break;
        // return null; // assume that we are only inheriting our own
        // types
      }
      anSTClass = anSTSuperClass;

    }
    return (STNameable[]) nullToEmptyList(result).toArray(new STNameable[0]);
  }

  @Override
  public STNameable[] getAllDeclaredEditablePropertyNames() {
    List<STNameable> result = null;
    // STNameable[] aPropertyNames = getDeclaredPropertyNames();
    STNameable[] aPropertyNames;

    STType anSTClass = this;
    while (true) {
      aPropertyNames = anSTClass.getDeclaredEditablePropertyNames();
      for (STNameable aNameable : aPropertyNames) {
        (result = nullToNewList(result)).add(aNameable);
      }
      STNameable aSuperClass = anSTClass.getSuperClass();
      if (aSuperClass == null || TagBasedCheck.isExternalClass(aSuperClass.getName()))
        break;
      STType anSTSuperClass = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aSuperClass.getName());
      if (anSTSuperClass == null) {
        if (anSTClass.waitForSuperTypeToBeBuilt())
          return null;
        else
          break;
        // return null; // assume that we are only inheriting our own
        // types
      }
      anSTClass = anSTSuperClass;

    }
    return (STNameable[]) nullToEmptyList(result).toArray(new STNameable[0]);
  }
  // public void initDeclaredPropertyNames(STNameable[] propertyNames) {
  // this.declaredPropertyNames = propertyNames;
  // }
  // @Override
  // public STNameable[] getDeclaredEditablePropertyNames() {
  // return declaredEditablePropertyNames;
  // }
  // // public void initEditablePropertyNames(STNameable[]
  // editablePropertyNames) {
  // // this.declaredEditablePropertyNames = editablePropertyNames;
  // // }
  // @Override
  // public STNameable[] getTags() {
  // return tags;
  // }
  // @Override
  // public STNameable[] getImports() {
  // return imports;
  // }
  // public void initTags(STNameable[] tags) {
  // this.tags = tags;
  // }
  // @Override
  // public STNameable getStructurePatternName() {
  // return structurePatternName;
  // }
  // @Override
  // public void initStructurePatternName(STNameable structurePatternName) {
  // this.structurePatternName = structurePatternName;
  // }
  // public static final String GET = "get";
  // public static final String SET = "set";
  // public static final String INIT = "init";
  // public static boolean isInit(STMethod anSTMethod) {
  // return isInit(anSTMethod.getName());
  // }
  // public static boolean isInit(String aMethodName) {
  // return aMethodName.startsWith(INIT);
  // }
  // protected void maybeProcessInit(STMethod anSTMethod) {
  // // if (!anSTMethod.getName().startsWith(INIT)) return;
  // // if (isInit(anSTMethod)) return;
  // if (anSTMethod.isInit())
  // return;
  //
  // String aPropertyName = anSTMethod.getName()
  // .substring(AnSTMethod.GET.length()).toLowerCase();
  // String aPropertyType = anSTMethod.getReturnType();
  // PropertyInfo aPropertyInfo = actualPropertyInfo.get(aPropertyName);
  // if (aPropertyInfo == null) {
  // aPropertyInfo = new APropertyInfo(aPropertyName, aPropertyType);
  // actualPropertyInfo.put(aPropertyName, aPropertyInfo);
  // }
  // aPropertyInfo.setGetter(anSTMethod);
  // }

  // public static boolean isGetter(STMethod anSTMethod) {
  // return anSTMethod.getName().startsWith(GET) &&
  // anSTMethod.isPublic() &&
  // anSTMethod.getParameterTypes().length == 0;
  // }

  protected void maybeProcessGetter(STMethod anSTMethod) {
    if (name.contains("Angle")) {
      int i = 0;
      i++;
    }
    // if (!anSTMethod.isPublicGetter())
    // return;
    if (!anSTMethod.isGetter())
      return;

    String aPropertyName = anSTMethod.getName().substring(AnSTMethod.GET.length());
    if (aPropertyName.equals("Class")) {
      return; // all objects have this
    }
    String aPropertyType = anSTMethod.getReturnType();
    // PropertyInfo aPropertyInfo = declaredPropertyInfo.get(aPropertyName);
    PropertyInfo aPropertyInfo = null;

    // if (aPropertyInfo == null) {
    if (declaredPropertyInfo == null
            || (aPropertyInfo = declaredPropertyInfo.get(aPropertyName)) == null) {

      aPropertyInfo = new APropertyInfo(this, aPropertyName, aPropertyType);
      if (declaredPropertyInfo == null) {
        declaredPropertyInfo = new HashMap();
      }
      declaredPropertyInfo.put(aPropertyName, aPropertyInfo);
    }
    aPropertyInfo.setGetter(anSTMethod);
  }

  // public STNameable[] getDeclaredFields() {
  // return declaredFields;
  // }
  // public static boolean isSetter(STMethod anSTMethod) {
  // return anSTMethod.getName().startsWith(SET) &&
  // anSTMethod.isPublic() &&
  // anSTMethod.getParameterTypes().length != 1 &&
  // "void".equals(anSTMethod.getReturnType());
  // }
  protected void maybeProcessSetter(STMethod anSTMethod) {
//    if (name.contains("Angle")) {
//      int i = 0;
//      i++;
//    }

    // if (!anSTMethod.isPublicSetter())
    // return;
    if (!anSTMethod.isSetter())
      return;
    hasSetter = true;

    String aPropertyName = anSTMethod.getName().substring(AnSTMethod.SET.length());

    String aPropertyType = anSTMethod.getParameterTypes()[0];
    // PropertyInfo aPropertyInfo = declaredPropertyInfo.get(aPropertyName);
    PropertyInfo aPropertyInfo = null;

    // if (aPropertyInfo == null) {
    if (declaredPropertyInfo == null
            || (aPropertyInfo = declaredPropertyInfo.get(aPropertyName)) == null) {

      aPropertyInfo = new APropertyInfo(this, aPropertyName, aPropertyType);
      (declaredPropertyInfo = nullToNewMap(declaredPropertyInfo)).put(aPropertyName, aPropertyInfo);
    }
    aPropertyInfo.setSetter(anSTMethod);
  }

  @Override
  public boolean hasSetter() {
    return hasSetter;
  }

  @Override
  public void introspect() {
    for (STMethod anSTMethod : getDeclaredMethods()) {
      anSTMethod.setDeclaringSTType(this);
      maybeProcessGetter(anSTMethod);
      maybeProcessSetter(anSTMethod);
    }
  }

  // @Override
  // public void findDelegateTypes() {
  // Collection<List<CallWithoutArguments>> aCalls =
  // globalVariableToCall.values();
  // for (List<CallWithoutArguments> aCallList:aCalls){
  // for (CallWithoutArguments aCall:aCallList) {
  // if (aCall.getCalleee().equals(aCall.getCaller())) {
  // delegates.add(aCall.getCalledType());
  // }
  // }
  // }
  // }
  @Override
  public Boolean isSubtypeOf(String aName) {
    // STType anSTType =
    // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
    // if (anSTType == null) return null;
    // List<STNameable> anAllTypes = anSTType.getAllTypes();
    // STType anSTType =
    // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
    // if (anSTType == null) return null;
    List<STNameable> anAllTypes = getAllTypes();
    if (anAllTypes == null) {
      if (waitForSuperTypeToBeBuilt())
        return null;
      else
        return false;
    }
    for (STNameable aNameable : anAllTypes) {
      if (aNameable.getName().equals(aName))
        return true;
    }
    return false;
  }

  public Boolean oldIsDelegate(String aName) {
    for (String aDelegateType : stringDelegates) {
      if (aName.equals(aDelegateType))
        return true;
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aName);
      if (anSTType == null) {
        if (waitForSuperTypeToBeBuilt())
          return null;
        else
          continue;
      }

      Boolean isSubType = anSTType.isSubtypeOf(aDelegateType);
      if (isSubType == null)
        if (waitForSuperTypeToBeBuilt())
          return null;
        else
          continue;
      // if (anSTType.isSubtypeOf(aDelegateType))
      if (isSubType)

        return true;
    }
    return false;
  }

  // protected static Map emptyMap = new HashMap();
  @Override
  public Map<String, PropertyInfo> getDeclaredPropertyInfos() {
    if (declaredPropertyInfo == null) {
      declaredPropertyInfo = emptyMap;
    }
    return declaredPropertyInfo;
  }

  public static PropertyInfo getPropertyInfo(String aName,
          Map<String, PropertyInfo> aPropertyInfos) {
    if (aPropertyInfos == null)
      return null;
    Set<String> aPropertyNames = aPropertyInfos.keySet();
    for (String aPropertyName : aPropertyNames) {
      if (aPropertyName.equalsIgnoreCase(aName))
        return aPropertyInfos.get(aPropertyName);
    }
    return null;
  }

  @Override
  public Boolean hasActualProperty(String aName) {
    Map<String, PropertyInfo> aPropertyInfos = getPropertyInfos();
    if (aPropertyInfos == null) {
      if (waitForSuperTypeToBeBuilt())
        return null;
      else
        return false;
    }

    PropertyInfo aPropertyInfo = getPropertyInfo(aName, aPropertyInfos);
    return aPropertyInfo != null && aPropertyInfo.getGetter() != null;
  }

  @Override
  public Boolean hasActualEditableProperty(String aName) {
    Map<String, PropertyInfo> aPropertyInfos = getPropertyInfos();
    if (aPropertyInfos == null) {
      if (waitForSuperTypeToBeBuilt())
        return null;
      else
        return false;
    }
    PropertyInfo aPropertyInfo = getPropertyInfo(aName, aPropertyInfos);
    if (aPropertyInfo == null)
      return false;
    return (aPropertyInfo.getSetter() != null);

  }

  void mergingPut(Map<String, PropertyInfo> aPropertyInfos, String aProperty,
          PropertyInfo aPropertyInfo) {
    PropertyInfo anOriginal = aPropertyInfos.get(aProperty);
    if (anOriginal == null)
      aPropertyInfos.put(aProperty, aPropertyInfo);
    else
      merge(anOriginal, aPropertyInfo);
  }

  protected void merge(PropertyInfo anOriginal, PropertyInfo aNew) {
    if (anOriginal.getGetter() == null)
      anOriginal.setGetter(aNew.getGetter());
    if (anOriginal.getSetter() == null)
      anOriginal.setSetter(aNew.getSetter());
  }

  // public Map<String, PropertyInfo> computeAllPropertyInfos() {
  // Map<String, PropertyInfo> result = new HashMap<>();
  // Map<String, PropertyInfo> aPropertyInfos = new HashMap();
  // STType anSTClass = this;
  // while (true) {
  // aPropertyInfos = anSTClass.getDeclaredPropertyInfos();
  // for (String aPropertyName : aPropertyInfos.keySet()) {
  //// result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
  // mergingPut(result, aPropertyName, aPropertyInfos.get(aPropertyName));
  // }
  // STNameable aSuperClass = anSTClass.getSuperClass();
  // if (aSuperClass == null
  // || TagBasedCheck.isExternalClass(aSuperClass.getName()))
  // break;
  // STType aSuperClassSTType = SymbolTableFactory.getOrCreateSymbolTable()
  // .getSTClassByShortName(aSuperClass.getName());
  // if (aSuperClassSTType == null) {
  // if (anSTClass.waitForSuperTypeToBeBuilt()) {
  // return null; // assume that we are only inheriting our own types
  // } else {
  // break;
  // }
  //
  // }
  // anSTClass = aSuperClassSTType;
  // }
  // return result;
  // }
  public Map<String, PropertyInfo> computeAllPropertyInfos() {
    Map<String, PropertyInfo> result = new HashMap<>();
    Map<String, PropertyInfo> aPropertyInfos = getDeclaredPropertyInfos();
    for (String aPropertyName : aPropertyInfos.keySet()) {
      // result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
      mergingPut(result, aPropertyName, aPropertyInfos.get(aPropertyName));
    }

    List<STNameable> allSuperTypes = getAllSuperTypes();
    if (allSuperTypes == null)
      return null;
    for (STNameable aSuperType : allSuperTypes) {
      if (TagBasedCheck.isExternalClass(aSuperType.getName())) {
        continue;
      }
      STType aSuperClassSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aSuperType.getName());
      if (aSuperClassSTType == null || isRecursive() || aSuperClassSTType.isRecursive()) {
        return null; // this should not happen
      }
      Map<String, PropertyInfo> aSuperPropertyInfos = aSuperClassSTType.getPropertyInfos();
      if (aSuperPropertyInfos == null)
        return null;
      for (String aPropertyName : aSuperPropertyInfos.keySet()) {
        // result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
        mergingPut(result, aPropertyName, aSuperPropertyInfos.get(aPropertyName));
      }
    }

    return nullToEmptyMap(result);
  }

  public Set<STVariable> computeAllGlobalVariables() {
    Set<STVariable> anAllGlobalSTVariables = new HashSet();
    List<STVariable> aDeclaredGlobals = getDeclaredSTGlobals();
    if (aDeclaredGlobals == null) {
      return null;
    }
    anAllGlobalSTVariables.addAll(aDeclaredGlobals);
    STNameable aSuperType = getSuperClass();
    if (aSuperType == null) {
      return anAllGlobalSTVariables;
    }
    STType aSuperClassSTType = SymbolTableFactory.getOrCreateSymbolTable()
            .getSTClassByFullName(aSuperType.getName());
    if (aSuperClassSTType == null) {
      if (TagBasedCheck.isExternalClass(aSuperType.getName())) {
        return anAllGlobalSTVariables;
      }
      return null;
    }
    Set<STVariable> anInheritedVariables = aSuperClassSTType.getAllGlobalVariables();
    if (anInheritedVariables == null) {
      return null;
    }
    anAllGlobalSTVariables.addAll(anInheritedVariables);
    return anAllGlobalSTVariables;

  }

  // // should use recursion actually
  @Override
  public Map<String, PropertyInfo> getPropertyInfos() {
    if (isRecursive()) {
      return emptyMap;
    }
    if (allPropertyInfos == null) {
      allPropertyInfos = computeAllPropertyInfos();
    }
    return allPropertyInfos;

  }

  @Override
  public Set<STVariable> getAllGlobalVariables() {
    if (allGlobalSTVariables == null) {
      allGlobalSTVariables = computeAllGlobalVariables();
    }
    // allPropertyInfos = computeAllPropertyInfos();
    return allGlobalSTVariables;
    // Map<String, PropertyInfo> result = new HashMap<>();
    // Map<String, PropertyInfo> aPropertyInfos = new HashMap();
    // STType anSTClass = this;
    // while (true) {
    // aPropertyInfos = anSTClass.getDeclaredPropertyInfos();
    // for (String aPropertyName : aPropertyInfos.keySet()) {
    // result.put(aPropertyName, aPropertyInfos.get(aPropertyName));
    // }
    // STNameable aSuperClass = anSTClass.getSuperClass();
    // if (aSuperClass == null
    // || TagBasedCheck.isExternalClass(aSuperClass.getName()))
    // break;
    // STType aSuperClassSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aSuperClass.getName());
    // if (aSuperClassSTType == null) {
    // if (anSTClass.waitForSuperTypeToBeBuilt()) {
    // return null; // assume that we are only inheriting our own types
    // } else {
    // break;
    // }
    //
    // }
    // anSTClass = aSuperClassSTType;
    // }
    // return result;
  }

  public static List<STNameable> getAllTypes(STNameable aParentType, STType anOriginalType) {
    // STType aParentSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aParentType.getName());
    STType aParentSTType = SymbolTableFactory.getOrCreateSymbolTable()
            .getSTClassByFullName(aParentType.getName());
    if (aParentSTType == null) {
      if (anOriginalType.waitForSuperTypeToBeBuilt())
        return null;
      else
        return emptyList;
    } else {
      // return getAllTypes(aParentSTType);
      return aParentSTType.getAllTypes();

    }

  }

  public static List<STNameable> getAllInterfaces(STNameable aParentType, STType anOriginalType) {
    // STType aParentSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aParentType.getName());
    STType aParentSTType = SymbolTableFactory.getOrCreateSymbolTable()
            .getSTClassByFullName(aParentType.getName());
    if (aParentSTType == null) {
      if (anOriginalType.waitForSuperTypeToBeBuilt())
        return null;
      else
        return emptyList;
    } else {
//      return getAllInterfaces(aParentSTType);
      return aParentSTType.getAllInterfaces();

    }

  }
  public static List<STNameable> getAllInterfaces(List<STNameable>  result, STNameable aParentType, STType anOriginalType) {
    // STType aParentSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aParentType.getName());
    STType aParentSTType = SymbolTableFactory.getOrCreateSymbolTable()
            .getSTClassByFullName(aParentType.getName());
    if (aParentSTType == null) {
      if (anOriginalType.waitForSuperTypeToBeBuilt())
        return null;
      else
        return emptyList;
    } else {
      return getAllInterfaces(aParentSTType);
    }

  }

  /**
   * Recursively look at both the extends and implements chain
   *
   */
  public static List<STNameable> getAllTypes(STType anSTType) {
    // if (TagBasedCheck.isExternalClass(TypeVisitedCheck
    // .toShortTypeName(anSTType.getName())) && !STBuilderCheck.getImportsAsExistingClasses())
    if (TagBasedCheck.isExternalClass(anSTType.getName())
            && !STBuilderCheck.getImportsAsExistingClasses())
      return emptyList;
    List<STNameable> result = null;
    result = new ArrayList();
    result.add(anSTType);
    // List<STNameable> aSuperTypes = getAllSuperTypes(anSTType);
    List<STNameable> aSuperTypes = anSTType.getAllSuperTypes();

    if (aSuperTypes == null) {
      return computeMissingNameableList();
    }

    addAllNonDuplicates(result, aSuperTypes);
    // List<STNameable> anInterfaces = getAllInterfaces(anSTType);
    List<STNameable> anInterfaces = anSTType.getAllInterfaces();

    if (anInterfaces == null)
      return computeMissingNameableList();
    addAllNonDuplicates(result, anInterfaces);
    // result.addAll(aSuperType);
    return result;
  }
  

  public static List<STNameable> getAllInterfaces(STType anSTType) {
    // if (TagBasedCheck.isExternalClass(TypeVisitedCheck
    // .toShortTypeName(anSTType.getName())))
    if (TagBasedCheck.isExternalClass(anSTType.getName()))
      return emptyList;
    List<STNameable> result = new ArrayList();
    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aType.getName());
    // if (anSTType == null)
    // if (anSTType.waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return result;
    if (anSTType.isInterface()) {
      // System.err.println("An interface does not have an interface:"
      // + anSTType.getName());
      return emptyList;
    }
    // if (anSTType.getName().contains("Vertical") || anSTType.getName().contains("Anim")) {
    // System.err.println("checking all ointerfaces " + anSTType.getName());
    // }
    STNameable[] anInterfaces = anSTType.getDeclaredInterfaces();
    if (anInterfaces == null) {
      return null;
    }
    for (STNameable anInterface : anInterfaces) {
      // STType anSTInterface = SymbolTableFactory.getOrCreateSymbolTable()
      // .getSTClassByShortName(anInterface.getName());
      // if (anSTInterface == null)
      // if (anSTType.waitForSuperTypeToBeBuilt())
      // return null;
      // else
      // continue;
      // int i = 0;
      List<STNameable> anInterfaceTypes = getAllTypes(anInterface, anSTType);
      if (anInterfaceTypes == null) {
        // if (TagBasedCheck.isExternalClass(TypeVisitedCheck
        // .toShortTypeName(anInterface.getName()))) {
        if (TagBasedCheck.isExternalClass(anInterface.getName())) {
          // result.add(anInterface);
          anInterfaceTypes = new ArrayList();
          anInterfaceTypes.add(anInterface);
        } else if (anSTType.waitForSuperTypeToBeBuilt())
          return computeMissingNameableList();
        else
          continue;
      }
      // result.addAll(anInterfaceTypes);
      addAllNonDuplicates(result, anInterfaceTypes);
    }
    STNameable aSuperClass = anSTType.getSuperClass();
    // STType anSTSuperClass = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aSuperClass.getName());
    // if (anSTSuperClass == null)
    // if (anSTType.waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return result;
    if (aSuperClass == null)
      return nullToEmptyList(result);
    // List<STNameable> aSuperInterfaces = getAllInterfaces(anSTSuperClass, anSTType);
    List<STNameable> aSuperInterfaces = getAllInterfaces(aSuperClass, anSTType);
    if (aSuperInterfaces == null) {
      if (anSTType.waitForSuperTypeToBeBuilt())
        return null;
      else
        return nullToEmptyList(result);
    }
    addAllNonDuplicates(result, aSuperInterfaces);
    return nullToEmptyList(result);
  }
  public static List<STNameable>  getAllInterfaces(List<STNameable> result, STType anSTType) {
    // if (TagBasedCheck.isExternalClass(TypeVisitedCheck
    // .toShortTypeName(anSTType.getName())))
    if (TagBasedCheck.isExternalClass(anSTType.getName()))
      return result;
//    List<STNameable> result = new ArrayList();
    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aType.getName());
    // if (anSTType == null)
    // if (anSTType.waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return result;
    if (anSTType.isInterface()) {
      // System.err.println("An interface does not have an interface:"
      // + anSTType.getName());
      return result ;
    }
    // if (anSTType.getName().contains("Vertical") || anSTType.getName().contains("Anim")) {
    // System.err.println("checking all ointerfaces " + anSTType.getName());
    // }
    STNameable[] anInterfaces = anSTType.getDeclaredInterfaces();
    if (anInterfaces == null) {
      return result;
    }
    for (STNameable anInterface : anInterfaces) {
      // STType anSTInterface = SymbolTableFactory.getOrCreateSymbolTable()
      // .getSTClassByShortName(anInterface.getName());
      // if (anSTInterface == null)
      // if (anSTType.waitForSuperTypeToBeBuilt())
      // return null;
      // else
      // continue;
      // int i = 0;
      List<STNameable> anInterfaceTypes = getAllTypes(anInterface, anSTType);
      if (anInterfaceTypes == null) {
        // if (TagBasedCheck.isExternalClass(TypeVisitedCheck
        // .toShortTypeName(anInterface.getName()))) {
        if (TagBasedCheck.isExternalClass(anInterface.getName())) {
          // result.add(anInterface);
          anInterfaceTypes = new ArrayList();
          anInterfaceTypes.add(anInterface);
        } else if (anSTType.waitForSuperTypeToBeBuilt()) {
            return computeMissingNameableList() == null ? null:result;
        }  else
          continue;
      }
      // result.addAll(anInterfaceTypes);
      addAllNonDuplicates(result, anInterfaceTypes);
    }
    STNameable aSuperClass = anSTType.getSuperClass();
    // STType anSTSuperClass = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aSuperClass.getName());
    // if (anSTSuperClass == null)
    // if (anSTType.waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return result;
    if (aSuperClass == null)
      return result;
    // List<STNameable> aSuperInterfaces = getAllInterfaces(anSTSuperClass, anSTType);
    List<STNameable> aSuperInterfaces = getAllInterfaces(aSuperClass, anSTType);
    if (aSuperInterfaces == null) {
      if (anSTType.waitForSuperTypeToBeBuilt())
        return null;
      else
        return result;
    }
    addAllNonDuplicates(result, aSuperInterfaces);
    return nullToEmptyList(result);
  }

  public static void addAllNonDuplicates(List aList, List anAdditions) {
    for (Object anAddition : anAdditions) {
      if (aList.contains(anAddition))
        continue;
      aList.add(anAddition);
    }
  }

  // public static List emptyList = new ArrayList();
  // public static Set emptySet = new HashSet<>();
  // public static STNameable[] emptyNameables = new STNameable[0];

  public static List computeMissingNameableList() {
    if (STBuilderCheck.isFirstPass()) {
      return null;
    }
    return emptyList;
  }

  public static List computeMissingSuperTypeList() {
    if (STBuilderCheck.isFirstPass()) {
      return null;
    }

    if (objectOnlySuperType.isEmpty()) {
      objectOnlySuperType.add(SymbolTableFactory.getOrCreateSymbolTable().getAndMaybePutObjectType());
    }
    return objectOnlySuperType;

  }

  public static Set computeMissingNameableSet() {
    if (STBuilderCheck.isFirstPass()) {
      return null;
    }
    return emptySet;
  }

  static List<STNameable> objectOnlySuperType = new ArrayList();

  public static List<STNameable> getAllSuperTypes(STNameable aType) {
    List<STNameable> aSuperTypes = computeAllSuperTypesExceptObject(aType);

    STType anObjectType = SymbolTableFactory.getOrCreateSymbolTable().getAndMaybePutObjectType();
    try {
      if (anObjectType == null || aSuperTypes.contains(anObjectType)
              || anObjectType.getName().equals(aType.getName())) {
        return aSuperTypes;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    if (aSuperTypes.isEmpty()) {
      if (objectOnlySuperType.isEmpty()) {
        objectOnlySuperType.add(anObjectType);
      }
      return objectOnlySuperType;

    }

    aSuperTypes.add(anObjectType);

    return aSuperTypes;
  }

  /**
   * Go up the extends chain, do not look at the implements chain
   *
   */

  // public static List nullToEmptyList (List aMaybeNullList) {
  // if (aMaybeNullList == null) {
  // return emptyList;
  // }
  // return aMaybeNullList;
  // }
  //
  // public static Set nullToEmptySet (Set aMaybeNullSet) {
  // if (aMaybeNullSet == null) {
  // return emptySet;
  // }
  // return aMaybeNullSet;
  // }
  // public static Map nullToEmptyMap (Map aMaybeNullMap) {
  // if (aMaybeNullMap == null) {
  // return emptyMap;
  // }
  // return aMaybeNullMap;
  // }
  // public static Set nullToNewSet (Set aMaybeNullSet) {
  // if (aMaybeNullSet == null) {
  // return new HashSet();
  // }
  // return aMaybeNullSet;
  // }
  //
  // public static List nullToNewList (List aMaybeNullList) {
  // if (aMaybeNullList == null) {
  // return new ArrayList();
  // }
  // return aMaybeNullList;
  // }
  // public static Map nullToNewMap (Map aMaybeNullMap) {
  // if (aMaybeNullMap == null) {
  // return new HashMap();
  // }
  // return aMaybeNullMap;
  // }

  public static List<STNameable> computeAllSuperTypesExceptObject(STNameable aType) {
    // if (TagBasedCheck.isExternalClass(TypeVisitedCheck
    // .toShortTypeName(aType.getName())) && !STBuilderCheck.getImportsAsExistingClasses())
    if (TagBasedCheck.isExternalClass(aType.getName())
            && !STBuilderCheck.getImportsAsExistingClasses())
      return emptyList;
    // List<STNameable> result = new ArrayList();
    // List<STNameable> result = null;
    // result.add(aType);

    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aType.getName());
    STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
            .getSTClassByFullName(aType.getName());
    if (anSTType == null) {
      return computeMissingNameableList();
      // return null;
    }
    List<STNameable> result = null;

    if (anSTType.isInterface()) {
      STNameable[] anInterfaces = anSTType.getDeclaredInterfaces();
      for (STNameable anInterface : anInterfaces) {
        // nullToNewList(result);
        if ((result = nullToNewList(result)).contains(anInterface)) // an interface may be
          // extended by many
          continue;
        result.add(anInterface);
        List<STNameable> anInterfaceTypes = getAllSuperTypes(anInterface);
        if (anInterfaceTypes == null)
          if (anSTType.waitForSuperTypeToBeBuilt())
            // return null;
            return computeMissingNameableList();
          else
            continue;
        // result.addAll(anInterfaceTypes);
        addAllNonDuplicates(nullToNewList(result), anInterfaceTypes);
      }
    } else {

      STNameable aSuperClass = anSTType.getSuperClass();
      if (aSuperClass == null) {

        return nullToEmptyList(result);
      }
      (result = nullToNewList(result)).add(aSuperClass);
      List<STNameable> aSuperTypes = computeAllSuperTypesExceptObject(aSuperClass);
      if (aSuperTypes == null) {
        if (anSTType.waitForSuperTypeToBeBuilt())
          // return null; // not made completely
          return computeMissingNameableList();
        else {
          if (result == null) {
            result = emptyList;
          }
          return nullToEmptyList(result);
        }
      }
      // nullToNewList(result);
      (result = nullToNewList(result)).addAll(aSuperTypes);
      // addAllNonDuplicates(result, aSuperTypes);
    }
    // if (result == null) {
    // result = emptyList;
    // }
    return nullToEmptyList(result);
  }

  public static List<STNameable> computeAllSuperTypes(STNameable aType) {
    // if (TagBasedCheck.isExternalClass(TypeVisitedCheck
    // .toShortTypeName(aType.getName())) && !STBuilderCheck.getImportsAsExistingClasses())
    // if (TagBasedCheck.isExternalClass(aType.getName()) &&
    // !STBuilderCheck.getImportsAsExistingClasses())
    if (!STBuilderCheck.getImportsAsExistingClasses()
            && TagBasedCheck.isExternalClass(aType.getName()))

      return emptyList;
    // List<STNameable> result = new ArrayList();
    // List<STNameable> result = null;
    // result.add(aType);

    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aType.getName());

    STType anSTType = null;
    if (aType instanceof STType) {
      anSTType = (STType) aType;
    } else {

      anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aType.getName());
    }
    if (anSTType == null) {
      return computeMissingNameableList();
      // return computeMissingSuperTypeList();
      // return null;
    }
    List<STNameable> result = null;
    if (anSTType.isRecursive()) {
      return nullToEmptyList(result);
    }

    if (anSTType.isInterface()) {
      STNameable[] anInterfaces = anSTType.getDeclaredInterfaces();
      for (STNameable anInterface : anInterfaces) {
        // nullToNewList(result);
        if ((result = nullToNewList(result)).contains(anInterface)) // an interface may be
          // extended by many
          continue;
       STType anSTTypeInterface = null;
       if (anInterface instanceof STType) {
         anSTTypeInterface = (STType) anInterface;
       } else {
         anSTTypeInterface = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(anInterface.getName());
       }
       if (anSTTypeInterface != null){
         anInterface = anSTTypeInterface;
       }
        result.add(anInterface);
        List<STNameable> anInterfaceTypes = null;
        if (anInterface instanceof STType) {
          anInterfaceTypes = ((STType) anInterface).getAllSuperTypes();
        } else {

          anInterfaceTypes = computeAllSuperTypes(anInterface);
        }
        if (anInterfaceTypes == null)
          if (anSTType.waitForSuperTypeToBeBuilt())
            // return null;
            return computeMissingNameableList();
          else
            continue;
        // result.addAll(anInterfaceTypes);
        addAllNonDuplicates(nullToNewList(result), anInterfaceTypes);
      }
    } else if (anSTType.isClass()) {

      STNameable aSuperClass = anSTType.getSuperClass();
      if (aSuperClass == null) {
        STType anObjectType = SymbolTableFactory.getOrCreateSymbolTable().getAndMaybePutObjectType();
        if (anObjectType == null) {
          System.err.println("Object type is null");
        } else {
          (result = nullToNewList(result)).add(anObjectType);
        }

        return nullToEmptyList(result);
      }
//      if (anSTType.getName().equals(aSuperClass.getName())) {
//        System.err.println("Recursive super class:" + anSTType);
//        anSTType.setRecursive(true);
//      }
      STType aSuperSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByFullName(aSuperClass.getName());
      if (aSuperSTType != null) {
        aSuperClass = aSuperSTType;
      }
      if (anSTType.getName().equals(aSuperClass.getName())) {
        System.err.println("Recursive super class:" + anSTType);
        anSTType.setRecursive(true);
        if (aSuperSTType != null) {
          aSuperSTType.setRecursive(true);
        }
      }
      (result = nullToNewList(result)).add(aSuperClass);
      // List<STNameable> aSuperTypes = computeAllSuperTypes(aSuperClass);
      List<STNameable> aSuperTypes = null;
      // STType aSuperSTType = null;

      // if (aSuperClass instanceof STType) {
      // aSuperSTType = (STType) aSuperClass;
      // } else {
      // aSuperSTType =
      // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aSuperClass.getName());
      // }
      if (aSuperSTType != null) {
        aSuperTypes = aSuperSTType.getAllSuperTypes();
      } else {
        aSuperTypes = computeAllSuperTypes(aSuperClass);
      }
      // if (aSuperClass instanceof STType) {
      // aSuperTypes = ((STType) aSuperClass).getAllSuperTypes();
      // } else {
      // aSuperTypes = computeAllSuperTypes(aSuperClass);
      // }

      if (aSuperTypes == null) {
        if (anSTType.waitForSuperTypeToBeBuilt())
          // return null; // not made completely
          return computeMissingNameableList();
        else {
          // if (result == null) {
          // result = emptyList;
          // }
          return nullToEmptyList(result);
        }
      }
      // nullToNewList(result);
      (result = nullToNewList(result)).addAll(aSuperTypes);
      // addAllNonDuplicates(result, aSuperTypes);
    }
    // if (result == null) {
    // result = emptyList;
    // }
    return nullToEmptyList(result);
  }
  // List<STNameable> allTypes;

  @Override
  public List<STNameable> getAllTypes() {
    if (allTypes == null) {
      // List<STNameable> result = new ArrayList();
      allTypes = getAllTypes(this);
    }
    // List<STNameable> result = new ArrayList();
    // return getAllTypes(this);
    return allTypes;

  }

  List<STNameable> allInterfaces;
  /*
   * (non-Javadoc)
   * @see unc.symbolTable.STType#getAllInterfaces()
   * A classes interfaces is the interface tree.
   * An interface's interface is the empty list
   */
  @Override
  public List<STNameable> getAllInterfaces() {
    if (isRecursive()) {
      return emptyList;
    }
    List<STNameable> retVal = null;
    if (allInterfaces == null) {
      if (name.contains("ontroller")) { 
        int i  = 0;
        i++;
      };
      // List<STNameable> result = new ArrayList();
      retVal = getAllInterfaces(this);
      if (retVal == null) {
//        System.err.println("Null all interfaces:" + this);
        return null;
      }
      setTypeType(retVal);
      allInterfaces = retVal;

    }
    return allInterfaces;
  }

  public List<String> computeAllTypeNames() {
    List<STNameable> allTypes = getAllTypes();
    if (allTypes == null)
      if (waitForSuperTypeToBeBuilt())
        // return null;
        return computeMissingNameableList();
      else
        return emptyList;
    return toNameList(allTypes);
  }

  List<String> allTypeNames;

  @Override
  public List<String> getAllTypeNames() {
    if (allTypeNames == null) {
      allTypeNames = computeAllTypeNames();
    }
    return allTypeNames;
    // List<STNameable> allTypes = getAllTypes();
    // if (allTypes == null)
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return emptyList;
    // return toNameList(allTypes);
  }

  public List<String> computeSuperTypeNames() {
    List<STNameable> aTypes = getAllSuperTypes();
    if (aTypes == null) {
      if (waitForSuperTypeToBeBuilt())
        // return null;
        return computeMissingNameableList();
      else
        return emptyList;
    }
    return toNameList(aTypes);
  }

  List<String> superTypeNames;

  @Override
  public List<String> getSuperTypeNames() {
    if (superTypeNames == null) {
      superTypeNames = computeSuperTypeNames();
    }
    return superTypeNames;
    // List<STNameable> aTypes = getSuperTypes();
    // if (aTypes == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return emptyList;
    // }
    // return toNameList(aTypes);
  }
  public static void setTypeType (List<STNameable> aSuperTypes){
    if (aSuperTypes == null) {
      return;
    }
    for (STNameable aSuperType:aSuperTypes) {
      if (aSuperType instanceof STType) {
        STType aSuperSTType = (STType) aSuperType;
        if (aSuperSTType.getTypeType() == null) {
          aSuperSTType.setTypeType(TypeType.INTERFACE);
        }
      }
    }
  }
  // List<STNameable> superTypes;
  /**
   * A classes super types is the superclass chain.
   * An interfaces super types is the super interface tree
   */
  @Override
	public List<STNameable> getAllSuperTypes() {
		if (superTypes == null) {
//			superTypes = getAllSuperTypes(this);
	     superTypes = computeAllSuperTypes(this);
	     STType anObjectType = SymbolTableFactory.getOrCreateSymbolTable().getAndMaybePutObjectType();
	     if ( superTypes != null && isClass() && !superTypes.contains(anObjectType)) {
	       for (STNameable aSuperType:superTypes) {
	         if (aSuperType instanceof STType) {
	           STType aSuperSTType = (STType) aSuperType;
	           if (aSuperSTType.getTypeType() == null) {
	             aSuperSTType.setTypeType(TypeType.CLASS);
	           }
	         }
	       }
	       if (!superTypes.isEmpty()) {
	         superTypes.add(anObjectType);
	       } else {
	         System.err.println ("empty super type");
	       }
	      
	     }
	     if (isInterface()) {
	       if (superTypes == null) {
	         System.err.println("Null super type:" + this);
	         ;
	       } else {
	       setTypeType(superTypes);
	       }
//         for (STNameable aSuperType:superTypes) {
//           if (aSuperType instanceof STType) {
//             STType aSuperSTType = (STType) aSuperType;
//             if (aSuperSTType.getTypeType() == null) {
//               aSuperSTType.setTypeType(TypeType.INTERFACE);
//             }
//           }
//         }
	     }
	    

//		superTypes = getAllSuperTypes(this);
//		STType anObjectType = SymbolTableFactory.getOrCreateSymbolTable().getObjectType();
//		if (superTypes != null && anObjectType != null && this != anObjectType && !superTypes.contains(anObjectType)) {
//		  superTypes.add(anObjectType);
//		}
		}
		
		return superTypes;
		// List<STNameable> result = new ArrayList();
//		return getAllSuperTypes(this);

	}

  public static List<String> toNameList(List<? extends STNameable> aNameableList) {
    if (aNameableList == null)
      return null;
    if (aNameableList.isEmpty()) {
      return emptyList;
    }
    List<String> result = new ArrayList();
    for (STNameable aNameable : aNameableList) {
      // String aShortName = TypeVisitedCheck.toShortTypeName(aNameable
      // .getName());
      String aLongName = aNameable.getName();
      // if (!result.contains(aShortName))
      // result.add(aShortName);
      if (!result.contains(aLongName))
        (result = nullToNewList(result)).add(aLongName);
    }
    return nullToEmptyList(result);
  }

  public static List<String> toNormalizedList(List<String> anOriginal) {
    if (anOriginal == null)
      return null;
    if (anOriginal.isEmpty()) {
      return emptyList;
    }
    List<String> result = null;
    for (String aNonNormalizedEntry : anOriginal) {
      (result = nullToNewList(result)).add(TypeVisitedCheck.toShortTypeName(aNonNormalizedEntry));
    }
    return nullToEmptyList(result);
  }

  public List<String> computeNonSuperTypes() {
    SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
    List<String> anAllTypes;
    if (isInterface())
      anAllTypes = aSymbolTable.getAllInterfaceNames();
    else
      anAllTypes = aSymbolTable.getAllClassNames();
    // List<String> aNormalizedTypes = toNormalizedList(anAllTypes);
    List<String> aNormalizedTypes = anAllTypes;

    // List<String> anAllMyTypes = toNameList(getAllSuperTypes());
    List<String> anAllMyTypes = toNameList(getAllTypes());

    return difference(aNormalizedTypes, anAllMyTypes);
  }

  List<String> nonSuperTypes;

  @Override
  public List<String> getNonSuperTypes() {
    if (nonSuperTypes == null)
      nonSuperTypes = computeNonSuperTypes();
    return nonSuperTypes;
    // SymbolTable aSymbolTable = SymbolTableFactory.getOrCreateSymbolTable();
    // List<String> anAllTypes;
    // if (isInterface())
    // anAllTypes = aSymbolTable.getAllInterfaceNames();
    // else
    // anAllTypes = aSymbolTable.getAllClassNames();
    // List<String> aNormalizedTypes = toNormalizedList(anAllTypes);
    // List<String> anAllMyTypes = toNameList(getSuperTypes());
    // return difference(aNormalizedTypes, anAllMyTypes);
  }

  /*
   * remove delegates non super types
   */

  public List<String> computeImmediateSubTypes() {

    List<String> aNonSuperTypes = getNonSuperTypes();
    // List<String> result = new ArrayList();
    List<String> result = null;
    // String myShortName = TypeVisitedCheck.toShortTypeName(name);
    // String myName = TypeVisitedCheck.toShortTypeName(name);

    // String myShortName = name;

    for (String aNonSuperType : aNonSuperTypes) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aNonSuperType);
      // if (anSTType == null) {
      // System.err.println("nul st type");
      // }
      if (anSTType == null)
        // return null;
        return computeMissingNameableList();
      if (!anSTType.waitForSuperTypeToBeBuilt())
        continue;
      // makes sense to move it up
      // if (anSTType == null)
      // return null;
      // List<String> aSuperTypes = toNormalizedList(anSTType
      // .getSuperTypeNames());
      List<String> aSuperTypes = anSTType.getSuperTypeNames();
      if (aSuperTypes == null)
        // return null;
        return computeMissingNameableList();
      if (aSuperTypes.contains(name)) {
        (result = nullToNewList(result)).add(aNonSuperType);
      }
    }
    // result.remove(getShortName());
    return nullToEmptyList(result);
  }
  
  protected void maybeAddOverridingMethods(STType anSTType) {
    
  }
  public STType getSuperClassSTType() {
    if (stSuperClass == null) {
    
    STNameable aSuperClassName = getSuperClass();
    if (aSuperClassName == null) {
      return null;
    }
    stSuperClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aSuperClassName.getName());
    }
    return stSuperClass;
  }
  protected boolean initializedOverrides = false;
  @Override
  public void initializeOverrides() {
    if (initializedOverrides)
      return;
    initializedOverrides = true;
    if (!isClass()) {
      return;
    }
    STType aSuperClass = getSuperClassSTType();
    if (aSuperClass == null) {
      return;
    }
    aSuperClass.getSubSTTypes();
    
  }
  
  protected void setMethodOverridenBy(STMethod anSTMethod, STType aSubSTType) {
    STMethod[] aSubSTMethods = aSubSTType.getDeclaredMethods();
    for (STMethod aSubSTMethod:aSubSTMethods) {
        if (aSubSTMethod.overrides(anSTMethod)) {
          
          anSTMethod.addOverridingSubtypeMethod(aSubSTMethod);
          aSubSTMethod.addOverridenSupertypeMethod(anSTMethod);
          return;
        }
    }
   
  }
  
  @Override
  public void setMethodsOverridenBy(STType aSubSTType) {
    STMethod[] anSTMethods = getDeclaredMethods();
    for (STMethod anSTMethod:anSTMethods) {
        setMethodOverridenBy(anSTMethod, aSubSTType);       
    }   
  }

  public List<String> computeSubTypes() {

    List<String> aNonSuperTypes = getNonSuperTypes();
    // List<String> result = new ArrayList();
    List<String> result = null;
    // String myShortName = TypeVisitedCheck.toShortTypeName(name);
    // String myName = TypeVisitedCheck.toShortTypeName(name);

    // String myShortName = name;

    for (String aNonSuperType : aNonSuperTypes) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aNonSuperType);
      // if (anSTType == null) {
      // System.err.println("nul st type");
      // }
      if (anSTType == null)
        // return null;
        return computeMissingNameableList();
      if (!anSTType.waitForSuperTypeToBeBuilt())
        continue;
      // makes sense to move it up
      // if (anSTType == null)
      // return null;
      // List<String> aSuperTypes = toNormalizedList(anSTType
      // .getSuperTypeNames());
      List<String> aSuperTypes = toNameList(anSTType.getAllTypes());

      if (aSuperTypes == null)
        // return null;
        return computeMissingNameableList();
      if (aSuperTypes.contains(name)) {
        // result.add(aNonSuperType);
        (result = nullToNewList(result)).add(aNonSuperType);
        setMethodsOverridenBy(anSTType);
      }

    }
    // result.remove(getShortName());
    return nullToEmptyList(result);
  }

  // List<String> subTypes;
  public List<String> getSubTypes() {
    if (subTypes == null)
      subTypes = computeSubTypes();
    return subTypes;
    // List<String> aNonSuperTypes = getNonSuperTypes();
    // List<String> result = new ArrayList();
    // String myShortName = TypeVisitedCheck.toShortTypeName(name);
    // for (String aNonSuperType : aNonSuperTypes) {
    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aNonSuperType);
    //// if (anSTType == null) {
    //// System.err.println("nul st type");
    //// }
    // if (anSTType == null)
    // return null;
    // if (!anSTType.waitForSuperTypeToBeBuilt())
    // continue;
    // // makes sense to move it up
    //// if (anSTType == null)
    //// return null;
    // List<String> aSuperTypes = toNormalizedList(anSTType
    // .getSuperTypeNames());
    // if (aSuperTypes == null)
    // return null;
    // if (aSuperTypes.contains(myShortName))
    // result.add(aNonSuperType);
    // }
    // return result;
  }

  // @Override
  public List<String> computePeerTypes() {
    // List<String> aNonSuperTypes = toNormalizedList(getNonSuperTypes());
    List<String> aNonSuperTypes = getNonSuperTypes();

    if (aNonSuperTypes == null) {
      if (waitForSuperTypeToBeBuilt())
        // return null;
        return computeMissingNameableList();
      else
        return emptyList;
    }
    // List<String> aSubTypes = toNormalizedList(getSubTypes());
    List<String> aSubTypes = getSubTypes();
    if (aSubTypes == null) {
      if (waitForSuperTypeToBeBuilt())
        return computeMissingNameableList();
      else
        return emptyList;
    }
    List<String> aResult = difference(aNonSuperTypes, aSubTypes);
    if (aResult != null) {
      // aResult.remove(getShortName());
      aResult.remove(getName());
    }
    if (isInterface() || aResult == null)
      return aResult;
    // find delegates
    // if (getDelegates().size() == 0)
    // return aResult;
    // List<String> aFinalResult = new ArrayList();
    List<String> aFinalResult = null;

    for (String aType : aResult) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
      if (anSTType == null)
        return computeMissingNameableList();
      // Boolean isDelegate = isDelegate(aType);
      // if (isDelegate == null) {
      // if (waitForSuperTypeToBeBuilt())
      // return computeMissingNameableList();
      // else
      // return emptyList;
      // }
      // Boolean isDelegator = anSTType.isDelegate(getShortName());
      // if (isDelegator == null) {
      // if (waitForSuperTypeToBeBuilt())
      // return computeMissingNameableList();
      // else
      // return emptyList;
      // }
      // if (!isDelegator && !isDelegate) {
      // // if (!delegates.contains(aType))
      // aFinalResult.add(aType);
      // }
      (aFinalResult = nullToNewList(aFinalResult)).add(aType);
    }
    return nullToEmptyList(aFinalResult);
  }

  List<String> peerTypes;

  public List<String> getPeerTypes() {
    if (peerTypes == null) {
      peerTypes = computePeerTypes();
      if (peerTypes == null) {
        return emptyList;
      }
      if (peerTypes.isEmpty()) {
        return emptyList;
      }
    }
    return peerTypes;
    // List<String> aNonSuperTypes = toNormalizedList(getNonSuperTypes());
    // if (aNonSuperTypes == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return emptyList;
    // }
    // List<String> aSubTypes = toNormalizedList(getSubTypes());
    // if (aSubTypes == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return emptyList;
    // }
    // List<String> aResult = difference(aNonSuperTypes, aSubTypes);
    // if (isInterface() || aResult == null)
    // return aResult;
    // // find delegates
    // if (getDelegates().size() == 0)
    // return aResult;
    // List<String> aFinalResult = new ArrayList();
    // for (String aType : aResult) {
    // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
    // .getSTClassByShortName(aType);
    // if (anSTType == null)
    // return null;
    // Boolean isDelegate = isDelegate(aType);
    // if (isDelegate == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return emptyList;
    // }
    // Boolean isDelegator = anSTType.isDelegate(getShortName());
    // if (isDelegator == null) {
    // if (waitForSuperTypeToBeBuilt())
    // return null;
    // else
    // return emptyList;
    // }
    // if (!isDelegator && !isDelegate) {
    // // if (!delegates.contains(aType))
    // aFinalResult.add(aType);
    // }
    // }
    // return aFinalResult;
  }

  public String getShortName() {
    return TypeVisitedCheck.toShortTypeName(getName());
  }

  @Override
  public Boolean isNonSuperType(String aTypeName) {
    return getNonSuperTypes().contains(TypeVisitedCheck.toShortTypeName(aTypeName));
  }

  @Override
  public Boolean isType(String aTypeName) {
    List<STNameable> aTypes = getAllTypes();
    if (aTypes == null) {
      if (waitForSuperTypeToBeBuilt())
        return null;
      else
        return false;
    }
    return toNameList(aTypes).contains(TypeVisitedCheck.toShortTypeName(aTypeName));
  }

  @Override
  public Boolean hasPublicMethod(String aSignature) {
    STMethod[] stMethods = getMethods();
    if (stMethods == null) {
      if (waitForSuperTypeToBeBuilt())
        return null;
      else
        return false;
    }
    return Arrays.asList(stMethods).contains(aSignature);
  }

  @Override
  public Boolean hasDeclaredMethod(String aSignature) {
    STMethod[] stMethods = getDeclaredMethods();
    if (stMethods == null)
      return null;
    return Arrays.asList(stMethods).contains(aSignature);
  }

  public static List intersect(Collection aList1, Collection aList2) {
    // List aResult = new ArrayList();
    List aResult = null;
    for (Object anElement1 : aList1) {
      for (Object anElement2 : aList2) {
        if (anElement1 == null) {
          System.err.println("An lement 1" + anElement1 + " 2:" + anElement2);
          // continue;
        }

        if (anElement1.equals(anElement2)) {
          // if (aResult == null) {
          // aResult = new ArrayList();
          // }
          (aResult = nullToNewList(aResult)).add(anElement1);
          break;
        }
      }
    }
    // if (aResult == null) {
    // aResult = emptyList;
    // }
    return nullToEmptyList(aResult);
  }

  public static List difference(List aList1, List aList2) {

    // List aResult = new ArrayList();
    List aResult = null;

    if (aList2 == null) {
      (aResult = nullToNewList(aResult)).addAll(aList1);
      return aResult;
    }
    for (Object anElement : aList1) {
      if (!aList2.contains(anElement))
        (aResult = nullToNewList(aResult)).add(anElement);
    }

    return nullToEmptyList(aResult);
  }

  public static List<STNameable> commonSuperTypes(String aType1, String aType2) {
    // List<STNameable> result = new ArrayList();
    STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
    if (anSTType1 == null)
      return computeMissingNameableList();
    STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
    if (anSTType2 == null)
      return computeMissingNameableList();
    // List<STNameable> aSuperTypes1 = anSTType1.getSuperTypes();
    // if (aSuperTypes1 == null)
    // return null;
    // List<STNameable> aSuperTypes2 = anSTType2.getSuperTypes();
    // if (aSuperTypes2 == null)
    // return null;
    // return intersect (aSuperTypes1, aSuperTypes2);
    return commonSuperTypes(anSTType1, anSTType2);
  }

  public static List<STType> commonTypes(String aType1, String aType2) {
    // List<STNameable> result = new ArrayList();
    STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
    if (anSTType1 == null)
      return computeMissingNameableList();
    STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
    if (anSTType2 == null)
      return computeMissingNameableList();
    // List<STNameable> aSuperTypes1 = anSTType1.getSuperTypes();
    // if (aSuperTypes1 == null)
    // return null;
    // List<STNameable> aSuperTypes2 = anSTType2.getSuperTypes();
    // if (aSuperTypes2 == null)
    // return null;
    // return intersect (aSuperTypes1, aSuperTypes2);
    return commonTypes(anSTType1, anSTType2);
  }

  public static List<STNameable> commonSuperTypes(STType anSTType1, STType anSTType2) {
    //
    List<STNameable> aSuperTypes1 = anSTType1.getAllSuperTypes();
    if (aSuperTypes1 == null)
      return computeMissingNameableList();
    List<STNameable> aSuperTypes2 = anSTType2.getAllSuperTypes();
    if (aSuperTypes2 == null)
      return computeMissingNameableList();
    return intersect(aSuperTypes1, aSuperTypes2);
  }

  public static List<STType> commonTypes(STType anSTType1, STType anSTType2) {
    //
    List<STNameable> aSuperTypes1 = anSTType1.getAllTypes();
    if (aSuperTypes1 == null)
      return computeMissingNameableList();
    List<STNameable> aSuperTypes2 = anSTType2.getAllTypes();
    if (aSuperTypes2 == null)
      return computeMissingNameableList();
    return intersect(aSuperTypes1, aSuperTypes2);
  }

  protected Map<String, List<STNameable>> typeToCommonTypes = new HashMap();

  @Override
  public List<STType> typesInCommonWith(String anOtherType) {
    List<STNameable> retVal = typeToCommonTypes.get(anOtherType);
    // return null;
    return commonTypes(this.getName(), anOtherType);
  }

  @Override
  public List<STType> typesInCommonWith(STType anOtherType) {
    if (System.identityHashCode(this) > System.identityHashCode(anOtherType)) {
      return anOtherType.typesInCommonWith(this);
    }
    return typesInCommonWith(anOtherType.getName());
  }

  @Override
  public List<STNameable> superTypesInCommonWith(String anOtherType) {
    return commonSuperTypes(this.getName(), anOtherType);
  }

  @Override
  public List<String> namesOfSuperTypesInCommonWith(String anOtherType) {
    return toNameList(superTypesInCommonWith(anOtherType));
  }

  @Override
  public List<String> namesOfTypesInCommonWith(String anOtherType) {
    return toNameList(typesInCommonWith(anOtherType));
  }

  @Override
  public List<STNameable> superTypesInCommonWith(STType anOtherType) {
    return commonSuperTypes(this, anOtherType);
  }

  List<String> allSignatures;

  public List<String> computeAllSignatures() {
    // List<String> result = new ArrayList();
    List<String> result = null;

    STMethod[] anSTMethods = getMethods();
    if (anSTMethods == null)
      return computeMissingNameableList();
    for (STMethod anSTMethod : anSTMethods) {
      (result = nullToNewList(result)).add(anSTMethod.getSignature());
    }
    return nullToEmptyList(result);
  }

  @Override
  public List<String> getAllSignatures() {
    if (allSignatures == null)
      allSignatures = computeAllSignatures();

    return allSignatures;
    // List<String> result = new ArrayList();
    // STMethod[] anSTMethods = getMethods();
    // if (anSTMethods == null)
    // return null;
    // for (STMethod anSTMethod : anSTMethods) {
    // result.add(anSTMethod.getSignature());
    // }
    // return result;
  }

  public List<String> computePublicInstanceSignatures() {
    // List<String> result = new ArrayList();
    List<String> result = null;

    STMethod[] anSTMethods = getMethods();
    if (anSTMethods == null)
      return computeMissingNameableList();
    for (STMethod anSTMethod : anSTMethods) {
      if (anSTMethod.isPublic() && anSTMethod.isInstance()) {

        (result = nullToNewList(result)).add(anSTMethod.getSignature());
      }
    }
    // if (result == null) {
    // return emptyList;
    // }
    return nullToEmptyList(result);
  }

  @Override
  public List<String> getDeclaredPublicInstanceSignatures() {
    List<String> result = null;
    STMethod[] anSTMethods = getDeclaredMethods();
    if (anSTMethods == null)
      return computeMissingNameableList();
    for (STMethod anSTMethod : anSTMethods) {
      if (anSTMethod.isPublic() && anSTMethod.isInstance())
        (result = nullToNewList(result)).add(anSTMethod.getSignature());
    }
    return nullToEmptyList(result);
  }

  public List<String> computeInstanceSignatures() {
    List<String> result = null;
    STMethod[] anSTMethods = getMethods();
    if (anSTMethods == null)
      return computeMissingNameableList();
    for (STMethod anSTMethod : anSTMethods) {
      if (anSTMethod.isInstance())
        (result = nullToNewList(result)).add(anSTMethod.getSignature());
    }
    return nullToEmptyList(result);
  }

  List<String> publicInstanceSignatures;

  @Override
  public List<String> getPublicInstanceSignatures() {
    if (publicInstanceSignatures == null)
      publicInstanceSignatures = computePublicInstanceSignatures();
    return publicInstanceSignatures;

  }

  List<String> instanceSignatures;

  @Override
  public List<String> getInstanceSignatures() {
    if (instanceSignatures == null)
      instanceSignatures = computeInstanceSignatures();
    return instanceSignatures;

  }

  public static List<String> commonSignatures(String aType1, String aType2) {
    STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
    if (anSTType1 == null)
      return computeMissingNameableList();
    STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
    if (anSTType2 == null)
      return computeMissingNameableList();
    return commonSignatures(anSTType1, anSTType2);
    //
  }

  public static List<STMethod> commonMethods(String aType1, String aType2) {
    STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
    if (anSTType1 == null)
      return computeMissingNameableList();
    STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
    if (anSTType2 == null)
      return computeMissingNameableList();
    return commonMethods(anSTType1, anSTType2);
    //
  }

  public static List<String> commonSignatures(STType aType1, STType aType2) {
    List<String> aSignatures1 = aType1.getPublicInstanceSignatures();
    if (aSignatures1 == null)
      return computeMissingNameableList();
    List<String> aSignatures2 = aType2.getPublicInstanceSignatures();
    if (aSignatures2 == null)
      return computeMissingNameableList();
    return intersect(aSignatures1, aSignatures2);
    //
  }

  public static List<STMethod> commonMethods(STType aType1, STType aType2) {
    STMethod[] aMethods1 = aType1.getMethods();
    if (aMethods1 == null)
      return computeMissingNameableList();
    STMethod[] aMethods2 = aType2.getMethods();
    if (aMethods2 == null)
      return computeMissingNameableList();
    return intersect(Arrays.asList(aMethods1), Arrays.asList(aMethods2));
    //
  }

  public static List<STMethod> commonDeclaredMethods(STType aType1, STType aType2) {
    STMethod[] aMethods1 = aType1.getDeclaredMethods();
    if (aMethods1 == null)
      return computeMissingNameableList();
    STMethod[] aMethods2 = aType2.getDeclaredMethods();
    if (aMethods2 == null)
      return computeMissingNameableList();
    List<STMethod> aRetVal = intersect(Arrays.asList(aMethods1), Arrays.asList(aMethods2));
    if (aRetVal.size() == 0) {
      return emptyList;
    }
    return aRetVal;
    // return intersect(Arrays.asList(aMethods1), Arrays.asList(aMethods2));
    //
  }

  public static List<PropertyInfo> commonProperties(String aType1, String aType2) {
    STType anSTType1 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType1);
    if (anSTType1 == null)
      return computeMissingNameableList();
    STType anSTType2 = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType2);
    if (anSTType2 == null)
      return computeMissingNameableList();
    return commonProperties(anSTType1, anSTType2);
    //
  }

  public static List<PropertyInfo> commonProperties(STType aType1, STType aType2) {
    Map<String, PropertyInfo> anInfoMap1 = aType1.getPropertyInfos();
    if (anInfoMap1 == null)
      return computeMissingNameableList();
    Collection<PropertyInfo> anInfos1 = anInfoMap1.values();
    Map<String, PropertyInfo> anInfoMap2 = aType2.getPropertyInfos();
    if (anInfoMap2 == null)
      return computeMissingNameableList();
    Collection<PropertyInfo> anInfos2 = anInfoMap2.values();

    return intersect(anInfos1, anInfos2);
    //
  }

  @Override
  public List<String> signaturesCommonWith(STType aType) {
    return commonSignatures(this, aType);
  }

  protected Map<STType, List<STMethod>> typeToCommonMethods = new HashMap();

  @Override
  public List<STMethod> methodsCommonWith(STType aType) {
    if (aType == null) {
      return null;
    }

    if (System.identityHashCode(this) > System.identityHashCode(aType)) {
      return aType.methodsCommonWith(this);
    }
    List<STMethod> aResult = typeToCommonMethods.get(aType);
    if (aResult == null) {
      // aResult = commonMethods(this, aType);
      aResult = commonDeclaredMethods(this, aType);

      typeToCommonMethods.put(aType, aResult);
    }
    return aResult;

    // return commonMethods(this, aType);
  }

  @Override
  public List<PropertyInfo> propertiesCommonWith(STType aType) {
    return commonProperties(this, aType);
  }

  @Override
  public List<String> signaturesCommonWith(String aTypeName) {
    STType aPeerType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    if (aPeerType == null)
      return computeMissingNameableList();
    return commonSignatures(this, aPeerType);
  }

  @Override
  public List<STMethod> methodsCommonWith(String aTypeName) {
    STType aPeerType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    if (aPeerType == null)
      return computeMissingNameableList();
    return methodsCommonWith(aPeerType);
  }

  @Override
  public List<PropertyInfo> propertiesCommonWith(String aTypeName) {
    STType aPeerType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    if (aPeerType == null)
      return computeMissingNameableList();
    return commonProperties(this, aPeerType);
  }

  public static Boolean containsSignature(String aTypeName, String aSignature) {
    STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    if (anSTType == null)
      return null;
    return containsSignature(anSTType, aSignature);
  }

  public static Boolean containsMethod(String aTypeName, STMethod aMethod) {
    STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    if (anSTType == null)
      return null;
    return containsMethod(anSTType, aMethod);
  }

  public static STType containsDeclaredMethod(String aTypeName, STMethod aMethod) {
    STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    if (anSTType == null)
      return null;
    // return containsDeclaredMethod(anSTType, aMethod);
    if (containsDeclaredMethod(anSTType, aMethod)) {
      return anSTType;
    }
    return null;
  }

  public static Boolean containsProperty(String aTypeName, PropertyInfo aProperty) {
    STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    if (anSTType == null)
      return null;
    return containsProperty(anSTType, aProperty);
  }

  public static Boolean containsSignature(STType aType, String aSignature) {
    List<String> aSignatures = aType.getPublicInstanceSignatures();
    if (aSignatures == null)
      return null;
    return aSignatures.contains(aSignature);
  }

  // public static Boolean containsMethod(STType aType, STMethod aMethod) {
  // List<STMethod> aMethods = Arrays.asList(aType.getMethods());
  // if (aMethods == null)
  // return null;
  // return aMethods.contains(aMethod);
  // }
  public static Boolean containsDeclaredMethod(STType aType, STMethod aMethod) {
    List<STMethod> aMethods = Arrays.asList(aType.getDeclaredMethods());
    if (aMethods == null)
      return null;
    // return aMethods.contains(aMethod);
    return aMethods.contains(aMethod);

  }

  public static Boolean containsMethod(STType aType, STMethod aMethod) {
    List<STMethod> aMethods = Arrays.asList(aType.getDeclaredMethods());
    if (aMethods == null)
      return null;
    return aMethods.contains(aMethod);
  }

  public static Boolean containsProperty(STType aType, PropertyInfo aProperty) {
    Collection<PropertyInfo> aProperties = aType.getPropertyInfos().values();
    if (aProperties == null)
      return null;
    return aProperties.contains(aProperty);
  }

  // public static Boolean haveDelegateRelatonship (String aTypeName1, String
  // aTypeName2) {
  // STType aType1 =
  // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName1);
  // if (aType1 == null)
  // return null;
  //
  // STType aType2 =
  // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName1);
  //
  // }
  // public static Boolean containsSignature (List<STType> aList) {
  // Boolean retVal = false;
  // for (STType aType:aList) {
  // retVal = containsSignature(aType);
  // if (retVal)
  // return true;
  // }
  // return retVal;
  // }
  public static Boolean containsSignature(List<String> aList, String aSignature) {
    Boolean retVal = false;
    for (String aType : aList) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
      if (anSTType == null) {
        retVal = null;
        continue;
      }
      retVal = containsSignature(aType, aSignature);
      if (retVal)
        return true;
    }
    return retVal;
  }

  public static Boolean containsMethod(List<String> aList, STMethod aMethod) {
    Boolean retVal = false;
    for (String aType : aList) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
      if (anSTType == null) {
        retVal = null;
        continue;
      }
      retVal = containsMethod(aType, aMethod);
      if (retVal)
        return true;
    }
    return retVal;
  }
  
  

  public static STType containsDeclaredMethod(List<String> aList, STMethod aMethod) {
    // Boolean retVal = false;
    STType retVal = null;
    boolean foundUnknownExternal = false;
    for (String aType : aList) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
      if (anSTType == null) {
        // retVal = null;
        continue;
      }      
      
     
      if (anSTType == SymbolTableFactory.getOrCreateSymbolTable().getAndMaybePutObjectType()) {
        continue;
      }
      if (anSTType.isUnknownExternal()) {
        foundUnknownExternal = true; 
      }
      retVal = containsDeclaredMethod(aType, aMethod);
      if (retVal != null) {
        return retVal;
      }

    }
    if (retVal == null && foundUnknownExternal) {
      return SymbolTableFactory.getOrCreateSymbolTable().getAndMaybePutObjectType();
    }
    return retVal;
  }

  public static Boolean containsProperty(List<String> aList, PropertyInfo aSignature) {
    Boolean retVal = false;
    for (String aType : aList) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
      if (anSTType == null) {
        retVal = null;
        continue;
      }
      retVal = containsProperty(aType, aSignature);
      if (retVal)
        return true;
    }
    return retVal;
  }

  static STMethod[] emptySTMethods = {};

  public static STMethod[] computeInitMethods(STMethod[] aMethods) {
    List<STMethod> result = null;
    for (STMethod aMethod : aMethods) {
      if (aMethod.isInit())
        (result = nullToNewList(result)).add(aMethod);
    }
    return (STMethod[]) nullToEmptyList(result).toArray(emptySTMethods);

  }

  @Override
  public STMethod[] getDeclaredInitMethods() {
    if (initMethods == null)
      initMethods = computeInitMethods(getDeclaredMethods());
    return initMethods;
  }

  public static boolean contains(List<STNameable> anInterfaces, String anInterface) {
    String aShortInterface = ComprehensiveVisitCheck.toShortTypeOrVariableName(anInterface);
    for (STNameable aNameable : anInterfaces) {
      String aMember = ComprehensiveVisitCheck.toShortTypeOrVariableName(aNameable.getName());
      return aMember.equals(aShortInterface);
    }
    return false;
  }

  public static List<STType> getImplementations(String anInterface) {
    List<STType> result = null;
    SymbolTable aSymbolTable = SymbolTableFactory.getSymbolTable();
    for (STType anSTType : aSymbolTable.getAllSTTypes()) {
      if (anSTType.isInterface())
        continue;
      List<STNameable> interfaces = anSTType.getAllInterfaces();
      if (interfaces == null)
        return computeMissingNameableList();
      if (contains(interfaces, anInterface)) {
        (result = nullToNewList(result)).add(anSTType);
      }
    }
    return nullToEmptyList(result);
  }

  @Override
  public STMethod getGetter(String aPropertyName) {
    STMethod[] aDeclaredMethods = getDeclaredMethods();
    for (STMethod aMethod : aDeclaredMethods) {
      if (aMethod.getName().toLowerCase().equals("get" + aPropertyName.toLowerCase())
              && aMethod.getParameterNames().length == 0) {
        return aMethod;
      }
    }
    return null;
  }

  @Override
  public STMethod getSetter(String aPropertyName) {
    STMethod[] aDeclaredMethods = getDeclaredMethods();
    for (STMethod aMethod : aDeclaredMethods) {
      if (aMethod.getName().toLowerCase().equals("set" + aPropertyName.toLowerCase())
              && aMethod.getParameterNames().length == 1) {
        return aMethod;
      }
    }
    return null;
  }

  @Override
  public boolean isExternal() {
    return external;
  }
  @Override
  public boolean isUnknownExternal() {
    return false;
  }

  @Override
  public void setExternal(boolean external) {
    this.external = external;
  }

  protected Integer numberOfFunctions;

  @Override
  public int getNumberOfFunctions() {
    if (numberOfFunctions == null) {
      numberOfFunctions = 0;
      for (STMethod aMethod : getDeclaredMethods()) {
        numberOfFunctions += (aMethod.isProcedure() ? 0 : 1);
      }
    }
    return numberOfFunctions;
  }

  protected Integer numberOfNonGetterFunctions;

  @Override
  public int getNumberOfNonGetterFunctions() {
    if (numberOfNonGetterFunctions == null) {
      numberOfNonGetterFunctions = 0;
      for (STMethod aMethod : getDeclaredMethods()) {
        numberOfNonGetterFunctions += (aMethod.isProcedure() || aMethod.isGetter() ? 0 : 1);
      }
    }
    return numberOfNonGetterFunctions;
  }

  protected Integer numberOfGettersAndSetters;

  @Override
  public int getNumberOfDeclaredGettersAndSetters() {
    if (numberOfGettersAndSetters == null) {
      numberOfGettersAndSetters = 0;
      for (STMethod aMethod : getDeclaredMethods()) {
        numberOfGettersAndSetters += (aMethod.isSetter() || aMethod.isGetter() ? 1 : 0);
      }
    }
    return numberOfGettersAndSetters;
  }

  protected Integer numberOfMethods;

  @Override
  public int getNumberOfDeclaredMethods() {
    if (numberOfMethods == null) {
      numberOfMethods = getDeclaredMethods().length;
    }
    return numberOfMethods;
  }

  protected Integer numberOfNonGettersAndSetters;

  @Override
  public int getNumberOfNonGettersAndSetters() {
    if (numberOfNonGettersAndSetters == null) {
      numberOfNonGettersAndSetters = getNumberOfDeclaredMethods()
              - getNumberOfDeclaredGettersAndSetters();
      // numberOfMethods = 0;
      // for (STMethod aMethod: getDeclaredMethods()) {
      // numberOfMethods += 1;
    }

    return numberOfNonGettersAndSetters;
  }

  protected Integer numberOfPublicMethods;

  @Override
  public int getNumberOfPublicMethods() {
    if (numberOfPublicMethods == null) {
      numberOfPublicMethods = getNumberOfDeclaredMethodsWithAccessMode(AccessModifier.PUBLIC);

    }
    return numberOfPublicMethods;
  }

  protected Integer numberOfProtectedMethods;

  @Override
  public int getNumberOfProtectedMethods() {
    if (numberOfProtectedMethods == null) {
      numberOfProtectedMethods = getNumberOfDeclaredMethodsWithAccessMode(AccessModifier.PROTECTED);

    }
    return numberOfProtectedMethods;
  }

  protected Integer numberOfPackageAccessMethods;

  @Override
  public int getNumberOfPackageAccessMethods() {
    if (numberOfPackageAccessMethods == null) {
      numberOfPackageAccessMethods = getNumberOfDeclaredMethodsWithAccessMode(
              AccessModifier.PACKAGE);

    }
    return numberOfPackageAccessMethods;
  }

  protected Integer numberOfPrivateMethods;

  @Override
  public int getNumberOfPrivateMethods() {
    if (numberOfPrivateMethods == null) {
      numberOfPrivateMethods = getNumberOfDeclaredMethodsWithAccessMode(AccessModifier.PRIVATE);

    }
    return numberOfPrivateMethods;
  }

  protected Integer numberOfNonPublicMethods;

  @Override
  public int getNumberOfNonPublicMethods() {
    if (numberOfNonPublicMethods == null) {
      numberOfNonPublicMethods = getNumberOfPrivateMethods() + getNumberOfProtectedMethods()
              + getNumberOfPackageAccessMethods();

    }
    return numberOfNonPublicMethods;
  }

  @Override
  public double getFractionOfPrivateMethods() {
    return ((double) getNumberOfPrivateMethods()) / getNumberOfDeclaredMethods();
  }

  @Override
  public double getFractionOfProtectedMethods() {
    return ((double) getNumberOfProtectedMethods()) / getNumberOfDeclaredMethods();
  }

  @Override
  public double getFractionOfPackageAccessMethods() {
    return ((double) getNumberOfPackageAccessMethods()) / getNumberOfDeclaredMethods();
  }

  @Override
  public double getFractionOfPublicMethods() {
    return ((double) getNumberOfPublicMethods()) / getNumberOfDeclaredMethods();
  }

  @Override
  public double getAverageAccessModeOfMethods() {

    return getFractionOfPrivateMethods() * (AccessModifier.PRIVATE.ordinal())
            + getFractionOfProtectedMethods() * (AccessModifier.PROTECTED.ordinal())
            + getFractionOfPackageAccessMethods() * (AccessModifier.PACKAGE.ordinal())
            + getFractionOfPublicMethods() * (AccessModifier.PUBLIC.ordinal());

  }

  //

  protected Integer numberOfDeclaredVariables;

  @Override
  public int getNumberOfDeclaredVariables() {
    if (numberOfDeclaredVariables == null) {
      numberOfDeclaredVariables = getDeclaredSTGlobals().size();

    }
    return numberOfDeclaredVariables;
  }

  protected Integer numberOfPublicVariables;

  @Override
  public int getNumberOfPublicVariables() {
    if (numberOfPublicVariables == null) {
      numberOfPublicVariables = getNumberOfDeclaredVariablesWithAccessMode(AccessModifier.PUBLIC);

    }
    return numberOfPublicVariables;
  }

  protected Integer numberOfProtectedVariables;

  @Override
  public int getNumberOfProtectedVariables() {
    if (numberOfProtectedVariables == null) {
      numberOfProtectedVariables = getNumberOfDeclaredVariablesWithAccessMode(
              AccessModifier.PROTECTED);

    }
    return numberOfProtectedVariables;
  }

  protected Integer numberOfPackageAccessVariables;

  @Override
  public int getNumberOfPackageAccessVariables() {
    if (numberOfPackageAccessVariables == null) {
      numberOfPackageAccessVariables = getNumberOfDeclaredVariablesWithAccessMode(
              AccessModifier.PACKAGE);

    }
    return numberOfPackageAccessVariables;
  }

  protected Integer numberOfPrivateVariables;

  @Override
  public int getNumberOfPrivateVariables() {
    if (numberOfPrivateVariables == null) {
      numberOfPrivateVariables = getNumberOfDeclaredVariablesWithAccessMode(AccessModifier.PRIVATE);

    }
    return numberOfPrivateVariables;
  }

  @Override
  public double getFractionOfPrivateVariables() {
    return ((double) getNumberOfPrivateVariables()) / getNumberOfDeclaredVariables();
  }

  @Override
  public double getFractionOfProtectedVariables() {
    return ((double) getNumberOfProtectedVariables()) / getNumberOfDeclaredVariables();
  }

  @Override
  public double getFractionOfPackageAccessVariables() {
    return ((double) getNumberOfPackageAccessVariables()) / getNumberOfDeclaredVariables();
  }

  @Override
  public double getFractionOfPublicVariables() {
    return ((double) getNumberOfPublicVariables()) / getNumberOfDeclaredVariables();
  }

  @Override
  public double getAverageAccessModeOfVariables() {

    return getFractionOfPrivateVariables() * (AccessModifier.PRIVATE.ordinal())
            + getFractionOfProtectedVariables() * (AccessModifier.PROTECTED.ordinal())
            + getFractionOfPackageAccessVariables() * (AccessModifier.PACKAGE.ordinal())
            + getFractionOfPublicVariables() * (AccessModifier.PUBLIC.ordinal());

  }

  // -----------
  @Override
  public int getNumberOfDeclaredProperties() {
    // return declaredPropertyInfo.size();
    return getDeclaredPropertyInfos().size();

  }

  protected Integer numberOfDeclaredProperties;

  protected Integer numberOfPublicProperties;

  @Override
  public int getNumberOfPublicProperties() {
    if (numberOfPublicProperties == null) {
      numberOfPublicProperties = getNumberOfDeclaredPropertiesWithAccessMode(AccessModifier.PUBLIC);

    }
    return numberOfPublicProperties;
  }

  protected Integer numberOfProtectedProperties;

  @Override
  public int getNumberOfProtectedProperties() {
    if (numberOfProtectedProperties == null) {
      numberOfProtectedProperties = getNumberOfDeclaredPropertiesWithAccessMode(
              AccessModifier.PROTECTED);

    }
    return numberOfProtectedProperties;
  }

  protected Integer numberOfPackageAccessProperties;

  @Override
  public int getNumberOfPackageAccessProperties() {
    if (numberOfPackageAccessProperties == null) {
      numberOfPackageAccessProperties = getNumberOfDeclaredPropertiesWithAccessMode(
              AccessModifier.PACKAGE);

    }
    return numberOfPackageAccessProperties;
  }

  protected Integer numberOfPrivateProperties;

  @Override
  public int getNumberOfPrivateProperties() {
    if (numberOfPrivateProperties == null) {
      numberOfPrivateProperties = getNumberOfDeclaredPropertiesWithAccessMode(
              AccessModifier.PRIVATE);

    }
    return numberOfPrivateProperties;
  }

  protected Integer numberOfEditableProperties;

  @Override
  public int getNumberOfEditableProperties() {
    if (numberOfEditableProperties == null) {
      numberOfEditableProperties = getNumberOfDeclaredEditableProperties(
              getDeclaredPropertyInfos().values());

    }
    return numberOfEditableProperties;
  }

  protected Integer numberOfReadOnlyProperties;

  @Override
  public int getNumberOfReadOnlyProperties() {

    if (numberOfReadOnlyProperties == null) {
      numberOfReadOnlyProperties = getNumberOfDeclaredReadOnlyProperties(
              getDeclaredPropertyInfos().values());

    }
    return numberOfReadOnlyProperties;
  }

  protected Integer numberOfWriteOnlyProperties;

  @Override
  public int getNumberOfWriteOnlyProperties() {

    if (numberOfWriteOnlyProperties == null) {
      numberOfWriteOnlyProperties = getNumberOfDeclaredWriteOnlyProperties(
              getDeclaredPropertyInfos().values());

    }
    return numberOfWriteOnlyProperties;
  }

  @Override
  public double getFractionOfPrivateProperties() {
    return ((double) getNumberOfPrivateProperties()) / getNumberOfDeclaredProperties();
  }

  @Override
  public double getFractionOfProtectedProperties() {
    return ((double) getNumberOfProtectedProperties()) / getNumberOfDeclaredProperties();
  }

  @Override
  public double getFractionOfPackageAccessProperties() {
    return ((double) getNumberOfPackageAccessProperties()) / getNumberOfDeclaredProperties();
  }

  @Override
  public double getFractionOfPublicProperties() {
    return ((double) getNumberOfPublicProperties()) / getNumberOfDeclaredProperties();
  }

  @Override
  public double getFractionOfReadOnlyProperties() {
    return ((double) getNumberOfReadOnlyProperties()) / getNumberOfDeclaredProperties();
  }

  @Override
  public double getFractionOfEditableProperties() {
    return ((double) getNumberOfEditableProperties()) / getNumberOfDeclaredProperties();
  }

  @Override
  public double getFractionOfWriteOnlyProperties() {
    return ((double) getNumberOfWriteOnlyProperties()) / getNumberOfDeclaredProperties();
  }

  @Override
  public double getAverageAccessModeOfProperties() {

    return getFractionOfPrivateProperties() * (AccessModifier.PRIVATE.ordinal())
            + getFractionOfProtectedProperties() * (AccessModifier.PROTECTED.ordinal())
            + getFractionOfPackageAccessProperties() * (AccessModifier.PACKAGE.ordinal())
            + getFractionOfPublicProperties() * (AccessModifier.PUBLIC.ordinal());

  }
  // -----------

  public static int getNumberOfMethodsWithAccessMode(STMethod[] anSTMethods,
          AccessModifier anAccessModifier) {
    if (anSTMethods == null) {
      return 0;
    }
    int retVal = 0;
    for (STMethod aMethod : anSTMethods) {
      retVal += (aMethod.getAccessModifier() == anAccessModifier ? 1 : 0);
    }

    return retVal;

  }

  /*
   * Ugh, should really combine STMethod and STVariable in one type with getAccessmodifier.
   */
  public static int getNumberOfVariablesWithAccessMode(List<STVariable> anSTVariables,
          AccessModifier anAccessModifier) {
    if (anSTVariables == null) {
      return 0;
    }
    int retVal = 0;
    for (STVariable aVariable : anSTVariables) {
      retVal += (aVariable.getAccessModifier() == anAccessModifier ? 1 : 0);
    }

    return retVal;

  }

  public static int getNumberOfDeclaredPropertiesWithAccessMode(
          Collection<PropertyInfo> aProperties, AccessModifier anAccessModifier) {
    if (aProperties == null) {
      return 0;
    }
    int retVal = 0;
    for (PropertyInfo aProperty : aProperties) {
      retVal += (aProperty.getAccessModifier() == anAccessModifier ? 1 : 0);
    }

    return retVal;

  }

  public static int getNumberOfDeclaredEditableProperties(Collection<PropertyInfo> aProperties) {
    if (aProperties == null) {
      return 0;
    }
    int retVal = 0;
    for (PropertyInfo aProperty : aProperties) {
      retVal += (aProperty.isEditable() ? 1 : 0);
    }

    return retVal;

  }

  public static int getNumberOfDeclaredReadOnlyProperties(Collection<PropertyInfo> aProperties) {
    if (aProperties == null) {
      return 0;
    }
    int retVal = 0;
    for (PropertyInfo aProperty : aProperties) {
      retVal += (aProperty.isReadOnly() ? 1 : 0);
    }

    return retVal;

  }

  public static int getNumberOfDeclaredWriteOnlyProperties(Collection<PropertyInfo> aProperties) {
    if (aProperties == null) {
      return 0;
    }
    int retVal = 0;
    for (PropertyInfo aProperty : aProperties) {
      retVal += (aProperty.isWriteOnly() ? 1 : 0);
    }

    return retVal;

  }

  protected int getNumberOfDeclaredMethodsWithAccessMode(AccessModifier anAccessModifier) {
    return getNumberOfMethodsWithAccessMode(getDeclaredMethods(), anAccessModifier);

  }

  protected int getNumberOfDeclaredVariablesWithAccessMode(AccessModifier anAccessModifier) {
    return getNumberOfVariablesWithAccessMode(getDeclaredSTGlobals(), anAccessModifier);

  }

  protected int getNumberOfDeclaredPropertiesWithAccessMode(AccessModifier anAccessModifier) {
    return getNumberOfDeclaredPropertiesWithAccessMode(getDeclaredPropertyInfos().values(),
            anAccessModifier);

  }

  @Override
  public STVariable getDeclaredGlobalSTVariable(String aGlobal) {
    if (globalSTVariables == null) {
      return null;
    }
    // STVariable result = null;
    for (STVariable anSTVariable : globalSTVariables) {
      String aShortGlobalName = TagBasedCheck.toShortTypeOrVariableName(aGlobal);
      if (anSTVariable.getName().equals(aShortGlobalName)) {
        return anSTVariable;
      }
    }
    return null;
  }

  @Override
  public STVariable getGlobalSTVariable(String aGlobal) {
    Set<STVariable> anAllGlobals = getAllGlobalVariables();
    if (anAllGlobals == null) {
      return null;
    }
    // STVariable result = null;
    for (STVariable anSTVariable : anAllGlobals) {
      String aShortGlobalName = TagBasedCheck.toShortTypeOrVariableName(aGlobal);
      if (anSTVariable.getName().equals(aShortGlobalName)) {
        return anSTVariable;
      }
    }
    return null;
  }
  // @Override
  // public boolean isParsedClass() {
  // return true;
  // }

  @Override
  public boolean isAbstract() {
    return isAbstract;
  }

  @Override
  public Set<STType> getReferenceTypes() {
    if (referenceTypes == null) {
      // referenceTypes = new HashSet();
      List<STVariable> aDeclaredSTGlobals = getDeclaredSTGlobals();
      if (aDeclaredSTGlobals != null) {
        for (STVariable anSTVariable : aDeclaredSTGlobals) {
          Set<STType> aVariableReferences = anSTVariable.getReferenceTypes();
          if (aVariableReferences != null) {
            if (referenceTypes == null) {
              referenceTypes = new HashSet();
            }
            referenceTypes.addAll(anSTVariable.getReferenceTypes());
          }
        }
      }
      STMethod[] aDeclaredMethods = getDeclaredMethods();

      for (STMethod anSTMethod : aDeclaredMethods) {
        Set<STType> aMethodReferences = anSTMethod.getCallingTypes();
        if (aMethodReferences != null) {
          if (referenceTypes == null) {
            referenceTypes = new HashSet();
          }
          referenceTypes.addAll(aMethodReferences);
        }
      }
    }
    if (referenceTypes == null) {
      referenceTypes = emptySet;
    }
    return referenceTypes;
  }

  @Override
  public List<AccessModifierUsage> getAccessModifiersUsed() {
    if (accessModifierUsage == null) {
      accessModifierUsage = AnSTVariable.getAccessModifiersUsed(this, this.getAccessModifier(),
              this, getReferenceTypes(), null);
    }
    return accessModifierUsage;

    // return AnSTVariable.getAccessModifiersUsed (this, this.getAccessModifier(), this,
    // getReferenceTypes(), null);

  }

  @Override
  public double getNumberOfReferencesPerVariable() {
    List<STVariable> aGlobals = getDeclaredSTGlobals();
    if (aGlobals == null || aGlobals.size() == 0) {
      return -1;
    }
    double aTotalReferences = 0.0;

    for (STVariable aVariable : aGlobals) {
      Set<DetailAST> aReferences = aVariable.getReferences();
      aTotalReferences += aReferences == null ? 0 : aReferences.size();
    }
    return aTotalReferences / getDeclaredSTGlobals().size();
  }

  @Override
  public double getNumberOfReferencesPerConstant() {
    List<STVariable> aGlobals = getDeclaredSTGlobals();
    if (aGlobals == null || aGlobals.size() == 0) {
      return -1;
    }
    double aTotalReferences = 0.0;
    int aNumConstants = 0;
    for (STVariable aVariable : aGlobals) {
      if (!aVariable.isFinal())
        continue;
      aNumConstants++;
      Set<DetailAST> aReferences = aVariable.getReferences();
      aTotalReferences += aReferences == null ? 0 : aReferences.size();
    }
    return aTotalReferences / aNumConstants;
  }

  @Override
  public double getNumberOfAssignmentsPerVariable() {
    List<STVariable> aGlobals = getDeclaredSTGlobals();
    if (aGlobals == null || aGlobals.size() == 0) {
      return -1;
    }
    double aTotalAssignments = 0.0;

    for (STVariable aVariable : aGlobals) {
      Set<DetailAST> anAssignments = aVariable.getAssignments();
      aTotalAssignments += anAssignments == null ? 0 : anAssignments.size();
    }
    return aTotalAssignments / getDeclaredSTGlobals().size();
  }

  @Override
  public STMethod getStaticBlocks() {
    return staticBlocks;
  }

  // @Override
  // public AccessModifier getAccessModifier() {
  // return accessModifier;
  // }
  @Override
  public void setStaticBlocks(STMethod staticBlocks) {
    this.staticBlocks = staticBlocks;
  }

  @Override
  public Set<STType> getSubSTTypes() {
    if (stSubTypes == null) {
      List<String> aSubTypes = getSubTypes();
      if (aSubTypes == null) {
        return computeMissingNameableSet();
      }
      // Set<STType> anSTSubTypes = new HashSet();
      Set<STType> anSTSubTypes = null;

      for (String aSubTypeName : aSubTypes) {
        STType anSTType = SymbolTableFactory.getSymbolTable().getSTClassByFullName(aSubTypeName);
        if (anSTType == null) {
          anSTType = SymbolTableFactory.getSymbolTable().getSTClassByShortName(aSubTypeName);
        }
        if (anSTType != null) {
          if (anSTSubTypes == null) {
            anSTSubTypes = new HashSet();
          }
          anSTSubTypes.add(anSTType);
        } else {
          return computeMissingNameableSet();
        }
      }
      stSubTypes = anSTSubTypes;
    }
    if (stSubTypes == null) {
      stSubTypes = emptySet;
    }
    return stSubTypes;
  }

  protected Integer numberOfAbstractMethods;

  @Override
  public int getNumberOfDeclaredAbstractMethods() {
    if (numberOfAbstractMethods == null) {
      numberOfAbstractMethods = 0;
      for (STMethod aMethod : getDeclaredMethods()) {
        if (aMethod.isAbstract()) {
          numberOfAbstractMethods++;
        }
      }
    }
    return numberOfAbstractMethods;
  }

  protected Integer numberOfGenericMethods;

  @Override
  public int getNumberOfDeclaredGenericMethods() {
    if (numberOfGenericMethods == null) {
      numberOfGenericMethods = 0;
      for (STMethod aMethod : getDeclaredMethods()) {
        if (aMethod.isGeneric()) {
          numberOfGenericMethods++;
        }
      }
    }
    return numberOfGenericMethods;
  }

  @Override
  public long getTimeOfEntry() {
    return timeOfEntry;
  }

  @Override
  public Set<STType> getDelegates() {
    return delegates;
  }

  @Override
  public Set<STType> getDelegators() {
    return delegators;
  }

  @Override
  public void addDelegate(STType aDelegate) {
    delegates.add(aDelegate);
  }

  @Override
  public void addDelegator(STType aDelegator) {
    delegators.add(aDelegator);
  }

  @Override
  public TypeType getTypeType() {
    return typeType;
  }

  public void setTypeType(TypeType aTypeType) {
    typeType = aTypeType;
  }

  @Override
  public boolean isEnum() {
    return typeType == TypeType.ENUM;

  }

  @Override
  public boolean isAnnotation() {
    return typeType == TypeType.ANNOTATION;
  }

  public boolean isInterface() {
    return typeType == TypeType.INTERFACE;
  }

  @Override
  public boolean isClass() {
    return typeType == TypeType.CLASS;
  }
  @Override
  public boolean isRecursive() {
    return isRecursive;
  }
  @Override
  public void setRecursive(boolean isRecursive) {
    this.isRecursive = isRecursive;
  }
}
