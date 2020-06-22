package unc.checks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
//import com.puppycrawl.tools.checkstyle.api.ScopeUtils;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
//import com.puppycrawl.tools.checkstyle.checks.CheckUtils;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
import com.puppycrawl.tools.checkstyle.utils.CheckUtil;
import com.puppycrawl.tools.checkstyle.utils.ScopeUtil;

import unc.symbolTable.ACallInfo;
import unc.symbolTable.AStaticBlocks;
import unc.symbolTable.AccessModifierUsage;
import unc.symbolTable.AnSTMethod;
import unc.symbolTable.AnSTMethodFromMethod;
import unc.symbolTable.AnSTNameable;
import unc.symbolTable.AnSTVariable;
import unc.symbolTable.CallInfo;
import unc.symbolTable.PropertyInfo;
import unc.symbolTable.STMethod;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.STVariable;
import unc.symbolTable.SymbolTableFactory;
import unc.symbolTable.TypeType;
import unc.symbolTable.VariableKind;
import unc.tools.checkstyle.ProjectSTBuilderHolder;

public abstract class ComprehensiveVisitCheck extends TagBasedCheck
        implements ContinuationProcessor {
  public static final Map<Integer, Integer> accessTokenToAccessDegree = new HashMap();
  public static final Map<Integer, String> accessTokenToAccessString = new HashMap();

  public static final String CLASS_START = "CLASS_DEF ";
  public static final String INTERFACE_START = "INTERFACE_DEF ";
  public static final String METHOD_START = "METHOD_DEF ";
  public static final String VARIABLE_START = "VARIABLE_DEF ";
  public static final String PARAMETER_START = "PARAMETER_DEF ";
  public static final Integer DEFAULT_ACCESS_TOKEN = -1;
  // protected boolean inMethodOrConstructor;
  // protected int methodOrConstructorNesting = 0;

  protected Map<String, List<DetailAST>> globalIdentToRHS = new HashMap();
  protected Map<String, List<DetailAST>> globalIdentToLHS = new HashMap();

  // public static final String MSG_KEY = "stBuilder";
  protected boolean leftCurlySeen;
  // protected boolean isEnum;
  // protected boolean isInterface;
  protected boolean isElaboration;
  protected STNameable superClass;
  protected STNameable[] interfaces;
  protected boolean currentMethodIsConstructor;
  protected STMethod currentStaticBlocks;
  protected STType currentSTType;
  protected String currentMethodName;
  DetailAST currentMethodNameAST;
  protected String currentMethodType;
  protected DetailAST currentMethodAST;
  protected boolean currentMethodIsPublic;
  protected boolean currentMethodAssignsToGlobalVariable;
  // protected List<String[]> methodsCalledByCurrentMethod = new ArrayList();
  protected List<CallInfo> methodsCalledByCurrentMethod = new ArrayList();
  protected Map<String, Set<DetailAST>> globalsAccessedByCurrentMethod = new HashMap();
  protected Map<String, Set<DetailAST>> globalsAssignedByCurrentMethod = new HashMap();

  // protected List<String> unknownVariablesAccessedByCurrentMethod = new ArrayList();
  protected Map<String, Set<DetailAST>> unknownVariablesAccessedByCurrentMethod = new HashMap();
  protected Map<String, Set<DetailAST>> unknownVariablesAssignedByCurrentMethod = new HashMap();

  // protected List<String> unknownVariablesAssignedByCurrentMethod = new ArrayList();
  // protected List<String> unknownMethodsCalledByCurrentMethod = new ArrayList();

  protected List<STVariable> parametersAssignedByCurrentMethod = new ArrayList();
  protected List<STVariable> localsAssignedByCurrentMethod = new ArrayList();

  protected boolean currentMethodIsInstance;

  protected boolean currentMethodIsVisible;
  protected boolean currentMethodIsSynchronized;
  protected List<String> currentMethodParameterTypes = new ArrayList();
  protected List<String> currentMethodParameterNames = new ArrayList();

  // protected List<STNameable> imports = new ArrayList();
  // protected static Set<String> externalImports = new HashSet();
  // protected static Set<String> javaLangClassesSet;
  protected List<STNameable> propertyNames;
  protected List<STNameable> editablePropertyNames;
  // protected List<STNameable> typeTags;
  // protected List<STNameable> currentMethodTags;
  protected Map<String, String> typeScope = new HashMap();
  protected List<STVariable> globalSTVariables = new ArrayList();
  protected List<STVariable> localSTVariables = new ArrayList();
  protected List<STVariable> parameterSTVariables = new ArrayList();

  protected List<STNameable> globalVariables = new ArrayList();

  protected Map<String, String> globalVariableToType = new HashMap();
  // protected Map<String, DetailAST> globalVariableToRHS = new HashMap();

  protected Map<String, List<CallInfo>> globalVariableToCall = new HashMap();
  protected Map<String, String> currentMethodScope = new HashMap();

  protected List<STNameable> typesInstantiated = new ArrayList();
  protected List<STNameable> typesInstantiatedByCurrentMethod = new ArrayList();
  protected Stack<DetailAST> openBlocksInCurrentMethod = new Stack();
  protected Stack<Map<String, String>> openScopesInCurrentMethod = new Stack();
  protected List<DetailAST> assertsInCurrentMethod = new ArrayList();
  protected int numberOfTernaryIfsInCurrentMethod = 0;

  protected int maxOpenBlocksInCurrentMethod = 0;

  protected String methodsDeclaredString;
  protected String variablesDeclaredString;
  protected String propertiesDeclaredString;
  protected String statisticsString;
  protected List<String> typeParameterNames = new ArrayList();
  public static final String NORMALIZED_TYPE_PARAMETER_NAME = "TypeParam";

  // protected Map<String, String> importShortToLongName = new HashMap();

  // protected Set<String> excludeTags;
  // protected Set<String> includeTags;
  // protected DetailAST currentTree;
  // protected boolean tagsInitialized;
  // public static String[] javaLangClasses = {
  // "Integer",
  // "Double",
  // "Character",
  // "String",
  // "Boolean",
  // };

  // protected STNameable structurePattern;
  Map<DetailAST, List<DetailAST>> astToPendingChecks = new HashMap();
  Map<DetailAST, Object> astToContinuationData = new HashMap();

  Map<DetailAST, FileContents> astToFileContents = new HashMap();
  Map<String, DetailAST> fileNameToTree = new HashMap();

  protected Set<String> excludeStructuredTypes = new HashSet();

  @Override
  public int[] getDefaultTokens() {
    return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
        TokenTypes.ENUM_DEF, TokenTypes.ANNOTATION_DEF, TokenTypes.ANNOTATION_FIELD_DEF,
        TokenTypes.TYPE_ARGUMENTS, TokenTypes.TYPE_PARAMETERS, TokenTypes.VARIABLE_DEF,

        TokenTypes.PARAMETER_DEF, TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF, TokenTypes.IMPORT,
        TokenTypes.STATIC_IMPORT, TokenTypes.METHOD_CALL, TokenTypes.IDENT, TokenTypes.ENUM_DEF,
        TokenTypes.LITERAL_NEW, TokenTypes.LITERAL_RETURN, TokenTypes.LITERAL_ASSERT,
        TokenTypes.COLON, TokenTypes.LITERAL_SWITCH, TokenTypes.LITERAL_IF, TokenTypes.LITERAL_ELSE,
        TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_WHILE, TokenTypes.LITERAL_CATCH,
        TokenTypes.LCURLY, TokenTypes.RCURLY };
  }

  protected void resetProject() {
    super.resetProject();
    astToPendingChecks.clear();
    astToContinuationData.clear();

    astToFileContents.clear();
    ;
    fileNameToTree.clear();

  }

  public STNameable[] getInterfaces(DetailAST aClassDef) {
    List<STNameable> anInterfaces = new ArrayList();
    int numInterfaces = 0;
    DetailAST implementsClause = aClassDef.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
    if (implementsClause == null)
      return emptyNameableArray;
    DetailAST anImplementedInterface = implementsClause.findFirstToken(TokenTypes.IDENT);
    while (anImplementedInterface != null) {
      if (anImplementedInterface.getType() == TokenTypes.IDENT) {
        // String anInterfaceDeclaredName = anImplementedInterface.getText();
        // String anInterfaceName = anInterfaceDeclaredName;
        // String aPossibleLongName = importShortToLongName.get(anInterfaceDeclaredName);
        // if (aPossibleLongName != null) {
        // anInterfaceName = aPossibleLongName;
        // }
        // anInterfaces.add(new AnSTNameable(anImplementedInterface,
        // anImplementedInterface.getText()));

        anInterfaces.add(new AnSTNameable(anImplementedInterface,
                toLongTypeName(anImplementedInterface.getText())));
      }
      anImplementedInterface = anImplementedInterface.getNextSibling();
    }
    return (STNameable[]) anInterfaces.toArray(emptyNameableArray);
  }

  protected Map<STMethod, String> methodToSignature = new HashMap();

  public List<STMethod> signaturesToMethods(String[] aSignatures) {
    List<STMethod> aMethods = new ArrayList();
    // do not clear it, as this is set before any signatures
    // methodToSignature.clear();
    for (String aSignature : aSignatures) {
      aSignature = aSignature.trim();
      STMethod aMethod = signatureToMethodorOrConstructor(maybeStripComment(aSignature));

      // aMethods.add(signatureToMethodorOrConstructor(aSignature));
      aMethods.add(aMethod);
      if (!aMethod.getSignature().equals(aSignature))
        methodToSignature.put(aMethod, aSignature);

    }
    return aMethods;
  }

  public STMethod signatureToMethodorOrConstructor(String aSignature) {
    return signatureToMethod(aSignature);
  }

  public static Boolean isIdentifier(String aString) {
    if (aString == null || aString.length() == 0)
      return false;
    char aFirstChar = aString.charAt(0);
    if (!Character.isLetter(aFirstChar) && (aFirstChar != TAG_CHAR)) {
      return false;
    }
    for (int index = 1; index < aString.length(); index++) { // in case of
      // tag will
      // allow
      // first
      // char to
      // be digit
      if (!Character.isLetter(aString.charAt(index)) && !Character.isDigit(aString.charAt(index))) {
        return false;
      }
    }
    return true;

  }

  public Boolean matchReturnTypeISA(STMethod aSpecification, STMethod aMethod) {
    String aSpecifiedReturnType = aSpecification.getReturnType();
    String aMethodReturnType = aMethod.getReturnType();
    return matchTypeISA(aSpecifiedReturnType, aMethodReturnType);

  }

  public Boolean matchTypeISA(String aSpecifiedReturnType, String anActualType) {

    Boolean result = matchReturnType(aSpecifiedReturnType, anActualType);

    if (result == null)
      return null;
    if (result) {
      return result;
    }
    // check if actual type IS-A specified type
    STType aReturnSTType = SymbolTableFactory.getOrCreateSymbolTable()
            .getSTClassByShortName(anActualType);
    if (aReturnSTType == null) {
      return null; // should not happen, can happen if return type is not in table
    }

    // List<STNameable> aSuperTypes = aReturnSTType.getAllSuperTypes();
    List<STNameable> aSuperTypes = aReturnSTType.getAllTypes();

    if (aSuperTypes == null) {
      return null;
    }
    // if (aSpecifiedReturnType.contains("Runnable")) {
    // System.out.println ("found type");
    // }
    for (STNameable aSuperType : aSuperTypes) {
      if (aSuperType.equals(aReturnSTType))
        continue;
      result = matchReturnType(aSpecifiedReturnType, aSuperType.getName());
      if (result == null) {
        return null;
      }
      if (result) {
        return result;
      }
    }
    return false;
  }

  public Boolean matchReturnType(String aSpecifiedReturnType, String aMethodReturnType) {
    STNameable[] typeTags = null;

    if (aSpecifiedReturnType != null && aSpecifiedReturnType.startsWith(TAG_STRING)) {
      STType aReturnSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aMethodReturnType);
      if (aReturnSTType == null)
        return null;
      typeTags = aReturnSTType.getComputedTags();
    }
    return unifyingMatchesNameVariableOrTag(aSpecifiedReturnType, aMethodReturnType, typeTags);

  }

  public Boolean matchReturnType(STMethod aSpecification, STMethod aMethod) {
    String aSpecifiedReturnType = aSpecification.getReturnType();
    String anActualReturnType = aMethod.getReturnType();

    STNameable[] anActualTypeTags = null;
    if (aSpecifiedReturnType != null && aSpecifiedReturnType.startsWith(TAG_STRING)) {
      STType aReturnSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(anActualReturnType);

      // STType aReturnSTType =
      // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aSpeifiedReturnType.substring(1));
      if (aReturnSTType == null)
        return null;
      anActualTypeTags = aReturnSTType.getComputedTags();
    }
    return (aSpecifiedReturnType == null || unifyingMatchesNameVariableOrTag(aSpecifiedReturnType,
            aMethod.getReturnType(), anActualTypeTags));

  }

  public Boolean matchParameters(String[] aSpecificationParameterTypes,
          String[] aMethodParameterTypes) {

    if (aSpecificationParameterTypes == null)
      return true;
    if (aSpecificationParameterTypes.length == 1) {
      if (aSpecificationParameterTypes[0].equals(MATCH_ANYTHING))
        return true;
    }
    if (aSpecificationParameterTypes.length != aMethodParameterTypes.length) {
      return false;
    }
    for (int i = 0; i < aSpecificationParameterTypes.length; i++) {

      String aParameterType = aSpecificationParameterTypes[i];

      STNameable[] parameterTags = null;
      if (aParameterType.startsWith(TAG_STRING)) {

        STType aParameterSTType = SymbolTableFactory.getOrCreateSymbolTable()
                .getSTClassByShortName(aParameterType.substring(1));
        if (aParameterSTType == null)
          return null;
        parameterTags = aParameterSTType.getComputedTags();
      }

      if (!unifyingMatchesNameVariableOrTag(aSpecificationParameterTypes[i],
              aMethodParameterTypes[i], parameterTags)) {
        // backTrackUnification();
        return false;
      }
    }
    return true;

  }

  public Boolean matchParameters(STMethod aSpecification, STMethod aMethod) {
    return matchParameters(aSpecification.getParameterTypes(), aMethod.getParameterTypes());
  }

  public Boolean matchSignature(STMethod aSpecification, STMethod aMethod) {
    // let someone else add this , some instance method that calls it
    variablesAdded.clear();

    String aDescriptor = aSpecification.getName();
    String aName = aMethod.getName();
    // New replacement lines
    // Boolean aMatchesName = unifyingMatchesNameVariableOrTag(aSpecification.getName(),
    // aMethod.getName(), aMethod.getComputedTags());
    Boolean aMatchesName = unifyingMatchesNameVariableOrTag(aDescriptor, aName,
            aMethod.getComputedTags());
    if (aMatchesName == null)
      return null;
    if (!aMatchesName) {
      // aMatchesName = matchISA(aDescriptor, aName);
      // if (!Matches)
      return false;
    }
    // Boolean aMatchesType = matchReturnType(aSpecification, aMethod);
    Boolean aMatchesType = matchReturnTypeISA(aSpecification, aMethod);

    if (aMatchesType == null) {
      return null;
    }
    if (!aMatchesType) {
      return false;
    }
    // end lines

    return matchParameters(aSpecification, aMethod);

    // String[] aSpecificationParameterTypes = aSpecification
    // .getParameterTypes();
    // String[] aMethodParameterTypes = aMethod.getParameterTypes();
    //
    // if (aSpecificationParameterTypes == null)
    // return true;
    // if (aSpecificationParameterTypes.length == 1) {
    // if (aSpecificationParameterTypes[0].equals(MATCH_ANYTHING))
    // return true;
    // }
    // if (aSpecificationParameterTypes.length != aMethodParameterTypes.length) {
    // return false;
    // }
    // for (int i = 0; i < aSpecificationParameterTypes.length; i++) {
    //
    // String aParameterType = aSpecificationParameterTypes[i];
    //
    // STNameable[] parameterTags = null;
    // if (aParameterType.startsWith(TAG_STRING)) {
    //
    // STType aParameterSTType = SymbolTableFactory
    // .getOrCreateSymbolTable().getSTClassByShortName(
    // aParameterType.substring(1));
    // if (aParameterSTType == null)
    // return null;
    // parameterTags = aParameterSTType.getComputedTags();
    // }
    //
    // if (!unifyingMatchesNameVariableOrTag(
    // aSpecificationParameterTypes[i], aMethodParameterTypes[i],
    // parameterTags)) {
    // // backTrackUnification();
    // return false;
    // }
    // }
    // return true;

  }

  protected List<STMethod> getMatchingMethods(STType aTargetSTType, STMethod aSpecifiedMethod) {
    List<STMethod> result = new ArrayList();
    int i = 0;
    STMethod[] aMethods = aTargetSTType.getMethods();
    if (aMethods == null)
      return null;
    boolean hadNullMatch = false;
    for (STMethod anSTMethod : aMethods) {
      Boolean aMatch = matchSignature(aSpecifiedMethod, anSTMethod);
      if (aMatch == null) {
        hadNullMatch = true;
        continue;
      }

      // if (!matchSignature(aSpecifiedMethod, anSTMethod))

      if (!aMatch)
        continue;
      result.add(anSTMethod);
      // if (anSTMethod.getName().equals(aCallInfo.getCalleee()) &&
      // anSTMethod.getParameterTypes().length ==
      // aCallInfo.getActuals().size()) {
      // return hasTag(anSTMethod, aSpecifiedMethod.getName());
      // }
    }
    if (hadNullMatch)
      return null; // either way we do not know if something bad happened
    return result;

  }

  public static void addAllNoDuplicates(List anOriginal, Set aNew) {
    for (Object newElement : aNew) {
      if (anOriginal.contains(newElement))
        continue;
      anOriginal.add(newElement);
    }
  }

  public Boolean matchesCallingMethod(STType anSTType, STMethod aSpecifiedMethod,
          STMethod anActualMethod) {
    // added not tested
    if (aSpecifiedMethod == null) {
      return true;
    }

    // int i = 0;
    Boolean aMatch = matchSignature(aSpecifiedMethod, anActualMethod);
    if (aMatch == null) {
      return null;
    }
    if (aMatch) // check if there is a direct call by the specified method
      // if (retVal)
      return true;
    // now go through the call graph and see if the specified method calls a
    // method that matches the actual method
    List<STMethod> aMatchingSpecifiedMethods = getMatchingMethods(anSTType, aSpecifiedMethod);
    if (aMatchingSpecifiedMethods == null) {
      return null;
    }
    for (STMethod aRootMethod : aMatchingSpecifiedMethods) {
      if (aRootMethod == null)
        continue;
      Boolean callsInternally = aRootMethod.callsInternally(anActualMethod);
      if (callsInternally == null) {
        continue;
      }
      if (callsInternally)
        // if (aRootMethod.callsInternally(anActualMethod))
        return true;
      // List<STMethod> aCalledMethods =
      // aRootMethod.getLocalCallClosure();
      // for (STMethod aCalledMethod:aCalledMethods) {
      // if (anActualMethod == aCalledMethod)
      // return true;
      // }
    }
    return false;

  }

  public static String[] splitCamelCaseHyphenDash(String aCamelCaseName) {
    // return
    // aCamelCaseName.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[0-9])(?=[A-Z][a-z])|(?<=[a-zA-Z])(?=[0-9])");
    // return
    // aCamelCaseName.split("_|-|\\.|(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[0-9])(?=[A-Z][a-z])|(?<=[a-zA-Z])(?=[0-9])");
    return aCamelCaseName.split(
            "@|\\+|_|-|\\.|(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[0-9])(?=[A-Z][a-z])|(?<=[a-zA-Z])(?=[0-9])");

  }

  public static String[] splitDashHyphen(String aDashHyphenName) {
    return aDashHyphenName.split("_|-");
  }

  static Map<String, STMethod> signatureToSTMethod = new HashMap();

  public static STMethod signatureToMethod(String aSignature) {
    STMethod retVal = signatureToSTMethod.get(aSignature);
    if (retVal == null) {
      retVal = createMethodFromSignature(aSignature);
      signatureToSTMethod.put(aSignature, retVal);
    }
    return retVal;
  }

  public static String METHOD_NAME_REST_SEPARATOR = ":";

  public static STMethod createMethodFromSignature(String aSignature) {
    String[] aNameAndRest = aSignature.split(METHOD_NAME_REST_SEPARATOR);
    if (aNameAndRest.length == 1) {
      if (!aSignature.equals(MATCH_ANYTHING) && !isIdentifier(aSignature)) {
        System.err.println("Illegal signature," + aSignature
                + ", missing :\n Assuming parameters and return types do not matter");
      }
      return new AnSTMethod(null, aSignature.trim(), null, null, null, true, true, false, false,
              null, false, null, null, false, null, null, null, null, null, null, null, null, null,
              null, null, 0, null, null);
    }
    if (aNameAndRest.length > 2) {
      System.err.println("Illegal signature," + aSignature + ",  too many :" + aSignature);
      return null;
    }
    // if (aNameAndRest.length != 2) {
    // System.err.println("Illegal signature, missing :" + aSignature);
    // return null;
    // }

    String aName = aNameAndRest[0].trim();
    String[] aReturnTypeAndParameters = aNameAndRest[1].split("->");
    if (aReturnTypeAndParameters.length != 2) {
      System.err.println("Illegal signature, missing ->" + aSignature);
      return null;
    }
    String aReturnType = aReturnTypeAndParameters[1].trim();
    String aParametersString = aReturnTypeAndParameters[0];
    String[] aParameterTypes = aParametersString.equals("") ? new String[0]
            : aParametersString.split(STMethod.PARAMETER_SEPARATOR);
    for (int i = 0; i < aParameterTypes.length; i++) {
      aParameterTypes[i] = aParameterTypes[i].trim();

    }
    return new AnSTMethod(null, aName, null, null, aParameterTypes, true, true, false, false,
            aReturnType, true, null, null, false, null, null, null, null, null, null, null, null,
            null, null, null, 0, null, null);

  }

  public STMethod signatureToConstructor(String aSignature) {
    int aColonIndex = aSignature.indexOf(':');
    // String[] aNameAndRest = aSignature.split(":");
    if (aColonIndex == -1) {
      System.err.print("Illegal signature, missing :" + aSignature);
      return null;
    }
    String aNameAndRest = aSignature.substring(aColonIndex + 1);
    String aName = "";
    String aReturnType = "";

    String[] aReturnTypeAndParameters = aNameAndRest.split("->");
    String aParametersString = aReturnTypeAndParameters[0].trim();
    String[] aParameterTypes = aParametersString.equals("") ? new String[0]
            : aParametersString.split(STMethod.PARAMETER_SEPARATOR);
    String[] aLongParameterTypes = toLongTypeNames(aParameterTypes);
    for (int i = 0; i < aParameterTypes.length; i++) {
      aParameterTypes[i] = aParameterTypes[i].trim();

    }
    return new AnSTMethod(null, aName, null, null, aLongParameterTypes, true, true, false, false,
            aReturnType, true, null, null, false, null, null, null, null, null, null, null, null,
            null, null, null, 0, null, null);

  }

  protected Map<DetailAST, FileContents> getAstToFileContents() {

    return astToFileContents;
  }

  public static List<String> toNames(Collection<STNameable> aNameables) {
    if (aNameables == null) {
      return null;
    }

    List result = new ArrayList<>(aNameables.size());
    for (STNameable aNameable : aNameables) {
      result.add(aNameable.getName());
    }
    return result;
  }

  public static List<String> toNames(STNameable[] aNameables) {
    if (aNameables == null) {
      return null;
    }
    List result = new ArrayList<>(aNameables.length);
    for (STNameable aNameable : aNameables) {
      result.add(aNameable.getName());
    }
    return result;
  }

  static StringBuilder genericStringBuilder = new StringBuilder();
  static List genericList = new ArrayList();

  public static String toString(STNameable[] aNameables) {
    if (aNameables == null) {
      return null;
    }
    genericList.clear();
    // List result = new ArrayList<>(aNameables.length);
    for (STNameable aNameable : aNameables) {
      genericList.add(aNameable.getName());
    }
    String retVal = genericList.toString();
    genericList.clear();
    return retVal;

  }

  public static List<STType> toSTTypes(Collection<STNameable> aNameables) {
    List<STType> result = new ArrayList<>(aNameables.size());
    for (STNameable aNameable : aNameables) {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aNameable.getName());
      if (anSTType == null) {
        return null;
      }
      result.add(anSTType);
    }
    return result;
  }

  public STNameable[] getSuperTypes(DetailAST aClassDef) {
    // List<STNameable> aSuperTypes = new ArrayList();
    List<STNameable> aSuperTypes = null;

    STNameable[] emptyArray = {};
    int numInterfaces = 0;
    DetailAST extendsClause = aClassDef.findFirstToken(TokenTypes.EXTENDS_CLAUSE);
    if (extendsClause == null)
      return emptyArray;
    DetailAST anExtendedType = extendsClause.findFirstToken(TokenTypes.IDENT);
    while (anExtendedType != null) {
      if (anExtendedType.getType() == TokenTypes.IDENT) {
        String aDeclaredName = anExtendedType.getText();
        // String aStoredName = aDeclaredName;
        // String aPossibleLongName = importShortToLongName.get(aDeclaredName);
        // if (aPossibleLongName != null) {
        // aStoredName = aPossibleLongName;
        // }
        // aSuperTypes.add(new AnSTNameable(anExtendedType, aStoredName));
        // aSuperTypes.add(new AnSTNameable(anExtendedType, anExtendedType
        // .getText()));
        (aSuperTypes = AnSTNameable.nullToNewList(aSuperTypes))
                .add(new AnSTNameable(anExtendedType, toLongTypeName(anExtendedType.getText())));
      }
      anExtendedType = anExtendedType.getNextSibling();
    }
    // return (STNameable[]) aSuperTypes.toArray(emptyArray);
    return AnSTNameable.toSTNameableArray(aSuperTypes);

  }

  // public static List<STNameable> getArrayLiterals (DetailAST
  // parentOfArrayInitializer) {
  // List<STNameable> result = new ArrayList<>();
  // DetailAST arrayInit =
  // parentOfArrayInitializer.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT);
  // if (arrayInit == null)
  // arrayInit = parentOfArrayInitializer; // single element array
  // DetailAST anArrayElementExpression =
  // arrayInit.findFirstToken(TokenTypes.EXPR);
  //
  // while (anArrayElementExpression != null) {
  // DetailAST anArrayElementAST = anArrayElementExpression.getFirstChild();
  // result.add(new AnSTNameable(anArrayElementAST,
  // anArrayElementAST.getText()));
  // if (anArrayElementExpression.getNextSibling() == null)
  // break;
  // anArrayElementExpression =
  // anArrayElementExpression.getNextSibling().getNextSibling();
  // }
  // return result;
  // }

  // protected List emptyArrayList = new ArrayList();
  public void maybeVisitPropertyNames(DetailAST ast) {
    // if (isEnum)
    // return;
    // not putting dependency on OE
    DetailAST annotationAST = AnnotationUtil.getAnnotation(ast, "PropertyNames");
    if (annotationAST == null) {
      propertyNames = emptyArrayList;
      return;
    }
    propertyNames = getArrayLiterals(annotationAST);
  }

  public void maybeVisitEditablePropertyNames(DetailAST ast) {
    // if (isEnum) {
    // return;
    // }
    DetailAST annotationAST = AnnotationUtil.getAnnotation(ast, "EditablePropertyNames");
    if (annotationAST == null) {
      editablePropertyNames = emptyArrayList;
      return;
    }
    editablePropertyNames = getArrayLiterals(annotationAST);
  }

  public void maybeVisitStructurePattern(DetailAST ast) {
    DetailAST annotationAST = AnnotationUtil.getAnnotation(ast, "StructurePattern");
    if (annotationAST == null) {
      structurePattern = null;
      return;
    }
    // if (structurePattern != null)
    // return;
    DetailAST expressionAST = annotationAST.findFirstToken(TokenTypes.EXPR);
    DetailAST actualParamAST = expressionAST.getFirstChild();
    String actualParamText = null;
    if (actualParamAST.getType() == TokenTypes.STRING_LITERAL) {
      actualParamText = actualParamAST.getText();
    } else {
      FullIdent aFullIdent = FullIdent.createFullIdent(actualParamAST);
      actualParamText = aFullIdent.getText();
    }
    // structurePattern = new AnSTNameable(actualParamAST,
    // actualParam.getText());
    structurePattern = new AnSTNameable(actualParamAST, actualParamText);
  }

  public void maybeVisitVisible(DetailAST ast) {
    currentMethodIsVisible = true;
    DetailAST annotationAST = AnnotationUtil.getAnnotation(ast, "Visible");
    if (annotationAST == null)
      return;
    DetailAST expressionAST = annotationAST.findFirstToken(TokenTypes.EXPR);
    DetailAST actualParamAST = expressionAST.getFirstChild();
    FullIdent actualParamIDent = FullIdent.createFullIdent(actualParamAST);
    currentMethodIsVisible = !"false".equals(actualParamIDent.getText());
  }

  // protected List<STNameable> typeTags( ) {
  // if (!tagsInitialized) {
  // DetailAST aTypeAST = getEnclosingTypeDeclaration(currentTree);
  // maybeVisitTypeTags(aTypeAST);
  // }
  // return typeTags;
  // }
  // public void maybeVisitTypeTags(DetailAST ast) {
  // if (tagsInitialized) return;
  // tagsInitialized = true;
  // DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");
  // if (annotationAST == null) {
  // typeTags = emptyArrayList;
  // return;
  // }
  // typeTags = getArrayLiterals(annotationAST);
  // }
  // public void maybeVisitMethodTags(DetailAST ast) {
  // DetailAST annotationAST = AnnotationUtility.getAnnotation(ast, "Tags");
  // if (annotationAST == null) {
  // currentMethodTags = emptyArrayList;
  // return;
  // }
  // currentMethodTags = getArrayLiterals(annotationAST);
  // }
  protected void leaveAnnotationDef(DetailAST ast) {

    leaveType(ast);

  }

  // public void visitEnumDef(DetailAST anEnumDef) {
  // visitType(anEnumDef);
  //// propertyNames = emptyArrayList; //no properties
  //// isEnum = true;
  // typeType = TypeType.ENUM;
  // typeNameAST = getEnumNameAST(anEnumDef);
  // // shortTypeName = getEnumName(anEnumDef);
  // shortTypeName = typeNameAST.getText();
  //// fullTypeName = packageName + "." + shortTypeName;
  // setFullTypeName(packageName + "." + shortTypeName);
  // typeAST = anEnumDef;
  // superClass = null;
  // interfaces = emptyNameableArray;
  //// isInterface = false;
  // typeNameable = new AnSTNameable(typeNameAST, getFullTypeName());
  //
  // // shortTypeName = anEnumDef.getNextSibling().toString();
  // // DetailAST anEnumIdent =
  // // anEnumDef.getNextSibling().findFirstToken(TokenTypes.IDENT);
  // // if (anEnumIdent == null) {
  // // System.out.println("null enum ident");
  // // }
  // // shortTypeName = anEnumIdent.getText();
  // }

  // protected static String getEnumName(DetailAST anEnumDef) {
  // return getEnumAST(anEnumDef).toString();
  // }
  // protected static DetailAST getEnumAST(DetailAST anEnumDef) {
  // return anEnumDef.getNextSibling();
  // }
  // protected boolean foundSupuriousInnerClass = false; // should be an int
  // protected boolean foundInnerClassToBeNotVisited() {
  // boolean retVal = (!getVisitInnerClasses() && getFullTypeName() != null);
  //// if (retVal) {
  // foundSupuriousInnerClass = retVal;
  //// }
  // return retVal;
  // }
  public void visitType(DetailAST typeDef) {

    if (getVisitInnerClasses()) {
      initializeTypeState();
    }
    super.visitType(typeDef);

    // this should move to minimal
    maybeVisitStructurePattern(typeDef);
    // why this?
    // if (!checkIncludeExcludeTagsOfCurrentType())
    // return;
    // maybeVisitStructurePattern(typeDef);
    maybeVisitPropertyNames(typeDef);
    maybeVisitEditablePropertyNames(typeDef);
    maybeVisitTypeTags(typeDef);

  }

  public STNameable getPattern(String aShortClassName) {
    // List<STNameable> aTags = emptyList;

    // these classes have no tags
    // if ( aShortClassName.endsWith("[]") ||
    // allKnownImports.contains(aShortClassName) ||
    // javaLangClassesSet.contains(aShortClassName) ) {
    // return emptyList;
    // }
    if (isArray(aShortClassName) || isJavaLangClass(aShortClassName)) {
      return null;
    }
    if (shortTypeName == null || // guaranteed to not be a pending check
            (aShortClassName.equals(shortTypeName)
                    || aShortClassName.endsWith("." + shortTypeName))) {
      return structurePattern;
    } else {
      STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByShortName(aShortClassName);

      if (anSTType == null) {
        if (isExternalImportCacheCheckingShortName(aShortClassName)) // check last as we are
          // not really sure about
          // external
          return null;
        return null;
      }
      if (anSTType.isEnum() || anSTType.isAnnotation())
        return null;
      return anSTType.getStructurePatternName();
    }

  }

  protected void processPreviousMethodData() {

  }

  // protected void processPreviousMethodData() {
  // if (currentMethodName != null) {
  // String[] aParameterTypes = currentMethodParameterTypes.toArray(new
  // String[0]);
  // STMethod anSTMethod = new AnSTMethod(
  // currentMethodAST,
  // currentMethodName,
  // typeName,
  // aParameterTypes,
  // currentMethodIsPublic,
  // currentMethodType,
  // currentMethodIsVisible);
  // stMethods.add(anSTMethod);
  // }
  //
  // }
  protected void visitMethod(DetailAST methodDef) {
    // if (inMethodOrConstructor) {
    // methodOrConstructorNesting++;
    // return;
    // }
    if (incrementMethodOrConstructorNesting()) {
      return;
    }
    processPreviousMethodData(); // this should be done in a leave method
    currentMethodIsConstructor = false;
    visitMethodOrConstructor(methodDef);
    // maybeVisitMethodTags(methodDef);
  }

  // protected boolean incrementMethodOrConstructorNesting() {
  // if (inMethodOrConstructor) {
  // methodOrConstructorNesting++;
  // return true;
  // }
  // return false;
  // }
  protected void visitConstructor(DetailAST methodDef) {
    // if (inMethodOrConstructor) {
    // methodOrConstructorNesting++;
    // return;
    // }
    if (incrementMethodOrConstructorNesting()) {
      return;
    }
    processPreviousMethodData();
    currentMethodIsConstructor = true;
    visitMethodOrConstructor(methodDef);
    // maybeVisitMethodTags(methodDef); // shouls bw in visitMethodOr

  }

  // protected void leaveMethodOrConstructor(DetailAST methodDef) {
  // inMethodOrConstructor = false;
  //// System.err.println("Not in method");
  //// resetMethodOrConstructor(methodDef);
  // }
  protected void leaveMethodCall(DetailAST methodDef) {
  }

  protected void leaveMethod(DetailAST methodDef) {
    // if (methodOrConstructorNesting > 0) {
    // methodOrConstructorNesting--;
    // return;
    // }
    leaveMethodOrConstructor(methodDef);
  }

  protected void leaveMethodOrConstructor(DetailAST methodDef) {
    inMethodOrConstructor = false;
    // System.err.println("Not in method");
    // resetMethodOrConstructor(methodDef);
  }

  protected void leaveConstructor(DetailAST methodDef) {
    // if (methodOrConstructorNesting > 0) {
    // methodOrConstructorNesting--;
    // return;
    // }
    leaveMethodOrConstructor(methodDef);
  }

  protected void resetMethodOrConstructor(DetailAST methodDef) {
    currentMethodName = null;
    currentMethodType = "";
    currentMethodParameterTypes.clear();
    currentMethodParameterNames.clear();
    currentMethodScope.clear();
    localSTVariables.clear();
    parameterSTVariables.clear();
    methodsCalledByCurrentMethod.clear();
    globalsAccessedByCurrentMethod.clear();
    globalsAssignedByCurrentMethod.clear();
    unknownVariablesAccessedByCurrentMethod.clear();
    unknownVariablesAssignedByCurrentMethod.clear();
    // unknownMethodsCalledByCurrentMethod.clear();
    localsAssignedByCurrentMethod.clear();
    parametersAssignedByCurrentMethod.clear();
    currentMethodAssignsToGlobalVariable = false;
    currentMethodTags = emptyNameableList;
    currentMethodComputedTags = emptyNameableList;
    typesInstantiatedByCurrentMethod.clear();
    openBlocksInCurrentMethod.clear();
    assertsInCurrentMethod.clear();
    numberOfTernaryIfsInCurrentMethod = 0;
    maxOpenBlocksInCurrentMethod = 0;
  }

  protected void visitMethodOrConstructor(DetailAST methodDef) {

    resetMethodOrConstructor(methodDef);
    inMethodOrConstructor = true;
    // System.err.println("in method");

    // currentMethodType = "";
    // currentMethodParameterTypes.clear();
    // currentMethodParameterNames.clear();
    // currentMethodScope.clear();
    // localSTVariables.clear();
    // parameterSTVariables.clear();
    // methodsCalledByCurrentMethod.clear();
    // globalsAccessedByCurrentMethod.clear();
    // globalsAssignedByCurrentMethod.clear();
    // unknownVariablesAccessedByCurrentMethod.clear();
    // unknownVariablesAssignedByCurrentMethod.clear();
    // localsAssignedByCurrentMethod.clear();
    // parametersAssignedByCurrentMethod.clear();
    // currentMethodAssignsToGlobalVariable = false;
    // currentMethodTags = emptyNameableList;
    // currentMethodComputedTags = emptyNameableList;
    // typesInstantiatedByCurrentMethod.clear();
    // openBlocksInCurrentMethod.clear();
    // assertsInCurrentMethod.clear();
    // numberOfTernaryIfsInCurrentMethod = 0;
    // maxOpenBlocksInCurrentMethod = 0;
    // resetMethodOrConstructor(methodDef);

    currentMethodName = getName(methodDef);

    currentMethodIsPublic = isPublic(methodDef);
    currentMethodIsInstance = !isStatic(methodDef);
    if (!currentMethodIsConstructor) {
      // DetailAST typeDef = methodDef.findFirstToken(TokenTypes.TYPE);
      currentMethodType = typeASTToString(methodDef);
      // DetailAST firstChild = typeDef.getFirstChild();
      // if (firstChild.getType() == TokenTypes.ARRAY_DECLARATOR) {
      // // if (firstChild.getText().startsWith("[")) {
      // currentMethodType = firstChild.getFirstChild().getText() + "[]";
      // } else {
      // FullIdent aTypeFullIdent = FullIdent
      // .createFullIdent(firstChild);
      // currentMethodType = aTypeFullIdent.getText();
      // }
    }
    currentMethodAST = methodDef;
    currentMethodIsSynchronized = isSynchronized(methodDef);
    maybeVisitVisible(methodDef);
    maybeVisitMethodTags(methodDef);

  }

  protected void visitParamDef(DetailAST paramDef) {
    final DetailAST grandParentAST = paramDef.getParent().getParent();

    // if ((grandParentAST.getType() != TokenTypes.METHOD_DEF && grandParentAST
    // .getType() != TokenTypes.CTOR_DEF))
    // return;
    final DetailAST aType = paramDef.findFirstToken(TokenTypes.TYPE);
    final DetailAST aParameterNameAST = aType.getNextSibling();
    final String aParameterName = aParameterNameAST.getText();

    // DetailAST aTypeNameAST = aType.findFirstToken(TokenTypes.IDENT);
    // String aTypeName = aTypeNameAST.getText();
    // final DetailAST arrayDeclAST = aType
    // .findFirstToken(TokenTypes.ARRAY_DECLARATOR);
    String text = "";
    List<DetailAST> anIdentifiers = findAllInOrderMatchingNodes(aType, TokenTypes.IDENT);
    if (anIdentifiers.size() > 1) {
      text = anIdentifiers.get(0).getText();
    } else {
      final FullIdent anIdentifierType = CheckUtil.createFullType(aType);

      text = anIdentifierType.getText();
    }

    final DetailAST arrayDeclAST = aType.findFirstToken(TokenTypes.ARRAY_DECLARATOR);
    if (arrayDeclAST != null)
      text = text + "[]";
    if ((grandParentAST.getType() != TokenTypes.METHOD_DEF
            && grandParentAST.getType() != TokenTypes.CTOR_DEF)) {
      Map<String, String> anOpenScope = currentOpenScope();
      if (anOpenScope != null) {
        anOpenScope.put(aParameterName, text);
      }
      return;
    }

    currentMethodParameterTypes.add(text);
    currentMethodParameterNames.add(aParameterName);
    addToMethodScope(paramDef, VariableKind.PARAMETER); // add a parameter to say param and return
                                                        // value perhaps

  }

  protected void visitTypeParameters(DetailAST aTypeParameters) {
    List<DetailAST> aTypeParameterASTs = findAllInOrderMatchingNodes(aTypeParameters,
            TokenTypes.IDENT);
    isGeneric = true;
    typeParameterNames = new ArrayList();
    for (int i = 0; i < aTypeParameterASTs.size(); i++) {
      typeParameterNames.add(aTypeParameterASTs.get(i).getText());
    }

  }

  protected void visitTypeArguments(DetailAST typeParameters) {
    isElaboration = true;

  }

  protected void visitClass(DetailAST ast) {
    visitType(ast);
    // moving to leave so we can fill symbol table correctly
    // if (!checkIncludeExcludeTagsOfCurrentType())
    // return;
    // STNameable[] superTypes = getSuperTypes(ast);
    // if (superTypes.length == 0)
    // superClass = null;
    // else
    // superClass = superTypes[0];
    // interfaces = getInterfaces(ast);
    // isInterface = false;
  }

  // protected void visitAnnotationDef(DetailAST ast) {
  // annotationDef = true;
  // DetailAST anAnnotationAST =
  // ast.getFirstChild()//Modifiers
  // .getNextSibling()// @
  // .getNextSibling()//interface
  // .getNextSibling();//name
  // String anAnnotationName = anAnnotationAST.getText();
  // }
  // protected void visitAnnotationFieldDef(DetailAST ast) {
  // annotationDef = true;
  // }

  protected void visitInterface(DetailAST ast) {
    visitType(ast);
    // moving to leave so we can fill symbol table correctly
    // if (!checkIncludeExcludeTagsOfCurrentType())
    // return;
    // superClass = null;
    // interfaces = getSuperTypes(ast);
    // isInterface = true;
  }

  protected void leaveClass(DetailAST ast) {
    if (checkIncludeExcludeTagsOfCurrentType()) {
      STNameable[] superTypes = getSuperTypes(ast);
      if (superTypes.length == 0)
        superClass = null;
      else
        superClass = superTypes[0];
      interfaces = getInterfaces(ast);
      // isInterface = false;
      typeType = TypeType.CLASS;
      // System.err.println ("Setting type to class");

    }
    leaveType(ast);

  }

  protected void leaveInterface(DetailAST ast) {
    if (checkIncludeExcludeTagsOfCurrentType()) {
      superClass = null;
      interfaces = getSuperTypes(ast);
      // isInterface = true;
      typeType = TypeType.INTERFACE;
    }
    leaveType(ast);

  }

  protected void leaveEnum(DetailAST ast) {
    // if (checkIncludeExcludeTagsOfCurrentType()) { // why conditional?
    //
    // isEnum = true;
    // }
    leaveType(ast);

  }

  // public static boolean isProjectImport(String aFullName) {
  // for (String aPrefix:STBuilderCheck.geProjectPackagePrefixes())
  // if (aFullName.startsWith(aPrefix)) return true;
  // return false;
  // }
  // public void visitImport(DetailAST ast) {
  // FullIdent anImport = FullIdent.createFullIdentBelow(ast);
  // String aLongClassName = anImport.getText();
  // String aShortClassName = getLastDescendent(ast).getText();
  //
  // STNameable anSTNameable = new AnSTNameable(ast, aLongClassName);
  // imports.add(anSTNameable);
  // if (!isProjectImport(aLongClassName))
  // externalImports.add(aShortClassName);
  // }
  // public void visitStaticImport(DetailAST ast) {
  // DetailAST anImportAST = ast.getFirstChild().getNextSibling();
  // FullIdent anImport = FullIdent.createFullIdent(
  // anImportAST);
  // STNameable anSTNameable = new AnSTNameable(ast, anImport.getText());
  // imports.add(anSTNameable);
  // }
  protected void maybePushAST(DetailAST anAST) {
    if (currentMethodAST != null) {
      openBlocksInCurrentMethod.push(anAST);
      openScopesInCurrentMethod.push(new HashMap());
    }
  }

  protected void maybePopAST() {
    if (!openBlocksInCurrentMethod.isEmpty()) {
      openBlocksInCurrentMethod.pop();
      openScopesInCurrentMethod.pop();
    }
  }

  protected void visitLCurly(DetailAST ast) {
    leftCurlySeen = true;
    // if (currentMethodAST != null)
    // maybePushAST(ast);

  }

  protected void leaveLCurly(DetailAST ast) {

  }

  protected void visitRCurly(DetailAST ast) {
    // maybePopAST();

  }

  protected void leaveRCurly(DetailAST ast) {
    // maybePopAST();

  }
  // not used, so delete it
  // protected void visitVariableOrParameterDef(DetailAST ast) {
  // // if (!checkIncludeExcludeTagsOfCurrentType())
  // // return;
  // if (ScopeUtils.inCodeBlock(ast))
  // // ||
  // // ast.getParent().getType() == TokenTypes.LITERAL_CATCH)
  // addToMethodScope(ast);
  // else
  // addToTypeScope(ast);
  // }

  public static boolean isArrayIndex(String aVariable) {
    return aVariable != null && aVariable.endsWith("]");
  }

  public static String removeBrackets(String anArrayIndex) {
    return anArrayIndex.substring(0, anArrayIndex.indexOf("["));
  }

  public static boolean isClassIdent(String anIdent) {
    if (anIdent.isEmpty()) {
      return false;
    }
    // if (anIdent.contains("out")) {
    // System.err.println ("found oout");
    // }
    int aStartIndex = 0;
    String aLastComponent = anIdent;
    int aDotIndex = anIdent.lastIndexOf(".");
    if (aDotIndex >= 0) {
      // aLastComponent = anIdent.substring(aDotIndex + 1);
      aStartIndex = aDotIndex + 1;
      if (anIdent.length() <= aStartIndex) {
        return false;
      }
    }
    if (Character.isLowerCase(anIdent.charAt(aStartIndex))
            || ((aStartIndex + 1) >= anIdent.length())) { // assume no one character classes
      return false;
    }
    return Character.isLowerCase(anIdent.charAt(anIdent.length() - 1));

  }

  public static final String VARIABLE_PREFIX = "$";

  public String lookupType(String aVariable) {
    boolean isArrayIndex = isArrayIndex(aVariable);
    if (isArrayIndex)
      aVariable = removeBrackets(aVariable);
    String result = lookupOpenStack(aVariable);
    if (result == null)
      result = lookupLocal(aVariable);

    // String result = lookupLocal(aVariable);
    if (result == null)
      result = lookupGlobal(aVariable);
    if (result == null) { // method on a class?
      if (aVariable != null && isClassIdent(aVariable)) {
        
        result = toLongTypeName(aVariable);
      } else {
        
        result = VARIABLE_PREFIX + toLongTypeName(aVariable);
      }
    }
    if (isArrayIndex) {
      if (!isArrayIndex(result)) {
        return result; // occurs when the variable is not stored but is
        // looked up, IllegalInitCall
      }
      result = removeBrackets(result);
    }
    return result;
  }

  public String lookupLocal(String aVariable) {
    return currentMethodScope.get(aVariable);
  }

  public String lookupOpenStack(String aVariable) {
    for (int i = openScopesInCurrentMethod.size() - 1; i >= 0; i--) {
      Map<String, String> aScope = openScopesInCurrentMethod.get(i);
      String retVal = aScope.get(aVariable);
      if (retVal != null) {
        return retVal;
      }
    }
    return null;
  }

  public String lookupGlobal(String aVariable) {
    if (aVariable == null)
      return null;
    if (aVariable.equals("this")) {
      return getFullTypeName(); // short type name?
    }
    return typeScope.get(aVariable);
  }

  public void visitVariableDef(DetailAST ast) {
    if (ScopeUtil.isInCodeBlock(ast)) {
      // if (ScopeUtil.inCodeBlock(ast)) {
      // ||
      // ast.getParent().getType() == TokenTypes.LITERAL_CATCH)
      addToMethodScope(ast, VariableKind.LOCAL);
    } else {
      addToTypeScope(ast);
    }

  }

  // public void visitParameterDef(DetailAST ast) {
  // if (!checkIncludeExcludeTagsOfCurrentType())
  // return;
  // visitVariableOrParameterDef(ast);
  // }
  // This is really kludgy, what aot for parameters etc. we need to open and
  // close scopes.
  // actually we are only looking at granparents that are methods
  public void addToMethodScope(DetailAST paramOrVarDef, VariableKind aVariableKind) {

    addToScope(paramOrVarDef, currentMethodScope, aVariableKind);
  }

  public static String getTypeName(DetailAST paramOrVarDef) {
    DetailAST aTypeAST = paramOrVarDef.findFirstToken(TokenTypes.TYPE);
    //
    DetailAST aSpecificTypeAST = aTypeAST.getFirstChild();
    if (aSpecificTypeAST.getType() == TokenTypes.ARRAY_DECLARATOR) {
      String anElementType = FullIdent.createFullIdentBelow(aSpecificTypeAST).getText();
      return anElementType + "[]";
    }
    // return FullIdent.createFullIdentBelow(
    // paramOrVarDef.findFirstToken(TokenTypes.TYPE)).getText();
    return FullIdent.createFullIdentBelow(aTypeAST).getText();

  }

  public static String typeASTToString(DetailAST parentAST) {
    DetailAST typeDef = parentAST.findFirstToken(TokenTypes.TYPE);
    if (typeDef == null) {
      return "";
    }

    DetailAST firstChild = typeDef.getFirstChild();
    String result;
    if (firstChild.getType() == TokenTypes.ARRAY_DECLARATOR) {
      // if (firstChild.getText().startsWith("[")) {
      result = firstChild.getFirstChild().getText() + "[]";
    } else {
      FullIdent aTypeFullIdent = FullIdent.createFullIdent(firstChild);
      result = aTypeFullIdent.getText();
    }
    return result;
  }

  STNameable[] dummyArray = new STNameable[0];

  protected DetailAST currentBlock() {
    return openBlocksInCurrentMethod.isEmpty() ? null : openBlocksInCurrentMethod.peek();
  }

  protected Map<String, String> currentOpenScope() {
    return openScopesInCurrentMethod.isEmpty() ? null : openScopesInCurrentMethod.peek();
  }

  public static Integer[] MODIFIERS_ARRAY = { TokenTypes.ABSTRACT, TokenTypes.LITERAL_PROTECTED,
      TokenTypes.LITERAL_PUBLIC, TokenTypes.LITERAL_PRIVATE, TokenTypes.LITERAL_STATIC };
  public static Set<Integer> MODIFIERS_SET = new HashSet(Arrays.asList(MODIFIERS_ARRAY));

  public static Set<Integer> extractModifiers(DetailAST modifiersToken) {

    if (modifiersToken == null)
      return AnSTNameable.emptySet;
    Set<Integer> retVal = new HashSet();

    for (DetailAST token = modifiersToken.getFirstChild(); token != null; token = token
            .getNextSibling()) {

      final int tokenType = token.getType();
      if (MODIFIERS_SET.contains(tokenType)) {
        retVal.add(tokenType);
      }

    }
    return retVal;

  }

  public static Set<DetailAST> extractAnnotations(DetailAST aModifierAST) {

    if (aModifierAST == null)
      return AnSTNameable.emptySet;
    DetailAST aMaybeAnnotation = aModifierAST.getFirstChild();
    if (aMaybeAnnotation == null || aMaybeAnnotation.getType() != TokenTypes.ANNOTATION) {
      return AnSTNameable.emptySet;
    }
    Set<DetailAST> retVal = new HashSet();
    while (true) {
      retVal.add(aMaybeAnnotation);
      // final DetailAST firstChild = aMaybeAnnotation.findFirstToken(TokenTypes.AT);
      // final String name =
      // FullIdent.createFullIdent(firstChild.getNextSibling()).getText();
      aMaybeAnnotation = aMaybeAnnotation.getNextSibling();
      if (aMaybeAnnotation == null || aMaybeAnnotation.getType() != TokenTypes.ANNOTATION) {
        return retVal;
      }
    }

  }

  public void createSTVariable(DetailAST paramOrVarDef, DetailAST anIdentifier, String aTypeName,
          VariableKind aVariableKind) {
    DetailAST anRHS = null;
    DetailAST aMaybeAssign = anIdentifier.getNextSibling();
    if (aMaybeAssign != null && aMaybeAssign.getType() == TokenTypes.ASSIGN) {
      anRHS = aMaybeAssign.getFirstChild();
    }
    DetailAST modifierAST = paramOrVarDef.findFirstToken(TokenTypes.MODIFIERS);
    Set<Integer> aModifiers = extractModifiers(modifierAST);
    // Set<DetailAST> anAnnotations = extractAnnotations(modifierAST);
    STVariable anSTVariable = new AnSTVariable(getFullTypeName(),
            // currentSTType,
            currentBlock(), paramOrVarDef, anIdentifier.getText(), aTypeName, anRHS, aVariableKind,
            getAccessToken(paramOrVarDef),
            getAllTags(paramOrVarDef, anIdentifier, aTypeName,
                    (aVariableKind == VariableKind.PARAMETER) ? PARAMETER_START : VARIABLE_START)
                            .toArray(dummyArray).clone(),
            aModifiers);
    switch (aVariableKind) {
      case GLOBAL:
        globalSTVariables.add(anSTVariable);
        break;
      case LOCAL:
        localSTVariables.add(anSTVariable);
        break;
      case PARAMETER:
        parameterSTVariables.add(anSTVariable);
        break;
    }

  }

  // protected String toLongTypeName (String aShortName) {
  // String retVal = aShortName;
  // String aPossibleLongName = importShortToLongName.get(aShortName);
  // if (aPossibleLongName != null) {
  // retVal = aPossibleLongName;
  // }
  // return retVal;
  // }
  public void addToScope(DetailAST paramOrVarDef, Map<String, String> aScope,
          VariableKind aVariableKind) {

    final DetailAST anIdentifier = paramOrVarDef.findFirstToken(TokenTypes.IDENT);

    String aTypeName = getTypeName(paramOrVarDef);
    String aLongTypeName = toLongTypeName(aTypeName);

    aScope.put(anIdentifier.getText(), aTypeName);
    createSTVariable(paramOrVarDef, anIdentifier, aLongTypeName, aVariableKind);

    if (aScope == typeScope) {
      // this code should go eventually
      DetailAST aMaybeAssign = anIdentifier.getNextSibling();
      DetailAST anRHS = null;
      if (aMaybeAssign != null && aMaybeAssign.getType() == TokenTypes.ASSIGN) {
        anRHS = aMaybeAssign.getFirstChild();
        // globalVariableToRHS.put(anIdentifier.getText(), anRHS);
      }
      aTypeName = toLongTypeName(aTypeName);
      // this code should go eventually
      STNameable anSTNameable = new AnSTNameable(paramOrVarDef, anIdentifier.getText(), aTypeName);
      globalVariables.add(anSTNameable);
      globalVariableToType.put(anIdentifier.getText(), aTypeName);
      // STVariable anSTVariable = new AnSTVariable (
      // paramOrVarDef,
      // anIdentifier.getText(),
      // aTypeName, anRHS,
      // VariableKind.GLOBAL,
      // getAllTags(paramOrVarDef, anIdentifier, aTypeName).toArray(dummyArray)
      // );

    }
  }

  // public void addToScopeOld(DetailAST paramOrVarDef, Map<String, String> aScope, VariableKind
  // aVariableKind) {
  // int i = 0;
  // // final DetailAST aType =
  // // paramOrVarDef.findFirstToken(TokenTypes.TYPE);
  // final DetailAST anIdentifier = paramOrVarDef
  // .findFirstToken(TokenTypes.IDENT);
  // // final FullIdent anIdentifierType = CheckUtils.createFullType(aType);
  // // final FullIdent anIdentifierType =
  // // FullIdent.createFullIdentBelow(aType);
  // String aTypeName = getTypeName(paramOrVarDef);
  //
  // aScope.put(anIdentifier.getText(), aTypeName);
  // if (aScope == typeScope) {
  // // final DetailAST aTypeParent = paramOrVarDef
  // // .findFirstToken(TokenTypes.TYPE);
  // // FullIdent aTypeIdent =
  // // FullIdent.createFullIdentBelow(aTypeParent);
  // // final DetailAST anIdentifier = paramOrVarDef
  // // .findFirstToken(TokenTypes.IDENT);
  // DetailAST aMaybeAssign = anIdentifier.getNextSibling();
  // DetailAST anRHS = null;
  // if (aMaybeAssign != null
  // && aMaybeAssign.getType() == TokenTypes.ASSIGN) {
  // anRHS = aMaybeAssign.getFirstChild();
  //// globalVariableToRHS.put(anIdentifier.getText(), anRHS);
  // }
  //
  // ;
  // STNameable anSTNameable = new AnSTNameable(paramOrVarDef,
  // anIdentifier.getText(), aTypeName);
  // globalVariables.add(anSTNameable);
  // globalVariableToType.put(anIdentifier.getText(), aTypeName);
  // STVariable anSTVariable = new AnSTVariable (
  // currentSTType,
  // null,
  // paramOrVarDef,
  // anIdentifier.getText(),
  // aTypeName, anRHS,
  // VariableKind.GLOBAL,
  // getAccessToken(paramOrVarDef),
  // getAllTags(paramOrVarDef, anIdentifier, aTypeName, VARIABLE_START).toArray(dummyArray)
  // );
  //
  // }
  // }

  public void addToTypeScope(DetailAST paramOrVarDef) {

    addToScope(paramOrVarDef, typeScope, VariableKind.GLOBAL);
  }

  public Boolean isGlobalDeclaredVariable(String aName) {
    // for (STNameable aGlobal : globalVariables) {
    // if (aGlobal.getName().equals(aName))
    // return true;
    // }
    // return false;
    return getGlobalDeclaredVariable(aName) != null;
  }

  public STNameable getGlobalDeclaredVariable(String aName) {
    for (STNameable aGlobal : globalVariables) {
      if (aGlobal.getName().equals(aName))
        return aGlobal;
    }
    return null;
  }

  public STVariable getLocalVariable(String aName) {
    for (STVariable aVariable : localSTVariables) {

      // for (STVariable aVariable : parameterSTVariables) {
      if (aVariable.getName().equals(aName))
        return aVariable;
    }
    return null;
  }

  public STVariable getParameterVariable(String aName) {
    // for (STVariable aVariable : localSTVariables) {

    for (STVariable aVariable : parameterSTVariables) {
      if (aVariable.getName().equals(aName))
        return aVariable;
    }
    return null;
  }

  public static List<DetailAST> getEListComponents(DetailAST anEList) {
    List<DetailAST> result = new ArrayList();
    DetailAST aParameter = anEList.getFirstChild();

    while (true) {
      if (aParameter == null)
        return result;
      else
        result.add(aParameter);
      DetailAST aCommaAST = aParameter.getNextSibling();
      if (aCommaAST == null)
        return result;
      aParameter = aCommaAST.getNextSibling();
    }

  }

  // ouch from subclass
  public static String toLongName(String[] aNormalizedName) {
    StringBuffer retVal = new StringBuffer();
    int index = 0;
    while (true) {
      if (index >= aNormalizedName.length) {
        return retVal.toString();
      }
      if (index > 0)
        retVal.append(".");
      retVal.append(aNormalizedName[index]);
      index++;
    }
  }

  /*
   * Ouch, I am going to ad side effects
   */
  public CallInfo registerMethodCallAndtoNormalizedParts(DetailAST ast, DetailAST aTreeAST) {
    String aTargetName = null;
    String shortMethodName = null;
    String aCastType = null;

    // if (ast.getType() == TokenTypes.CTOR_CALL) {
    // aTargetName = getTypeName(aTreeAST);
    // shortMethodName = aTargetName;
    //
    // } else {
    // DetailAST aLeftMostMethodTargetAST = null;
    // String shortMethodName = null;
    // if (ast.getType() == TokenTypes.METHOD_CALL) {
    // FullIdent aFullIndent = FullIdent.createFullIdentBelow(ast);
    // int i = 0;
    // int j = 0;
    currentMethodNameAST = findLastDescendentOfFirstChild(ast);
    // String shortMethodName = currentMethodNameAST.getText();
    shortMethodName = currentMethodNameAST.getText();

    // String aCastType = null;

    DetailAST aLeftMostMethodTargetAST = currentMethodNameAST.getPreviousSibling();

    if (aLeftMostMethodTargetAST != null) { // this should be a while?
      while (aLeftMostMethodTargetAST.getType() == TokenTypes.RPAREN) {
        DetailAST anLParen = aLeftMostMethodTargetAST.getPreviousSibling();
        if (anLParen.getType() == TokenTypes.RPAREN) {
          aLeftMostMethodTargetAST = anLParen;
          continue;
        } else if (anLParen.getType() == TokenTypes.LPAREN) {
          DetailAST aTypeAST = anLParen.getFirstChild();
          if (typeAST == null) { // this should not happen
            aLeftMostMethodTargetAST = anLParen;
            break;
          } else if (aTypeAST.getType() == TokenTypes.TYPE) {
            // int i = 0;
            // this is a hack, need to search down the tree for
            // parens
            // and find the first
            // idnetifier or indexop in the tree
            aCastType = aTypeAST.getFirstChild().getText();
            DetailAST anRTypeParen = aTypeAST.getNextSibling();
            DetailAST aCastExpression = anRTypeParen.getFirstChild();
            aLeftMostMethodTargetAST = aCastExpression;
            break;

          }
        } else if (anLParen.getType() == TokenTypes.IDENT) {
          aLeftMostMethodTargetAST = anLParen;
          break;
        } else {
          break;
        }
        break;
      }
      if (aLeftMostMethodTargetAST != null) {
        while (aLeftMostMethodTargetAST.getType() == TokenTypes.METHOD_CALL) { // target
          // is
          // result
          // of
          // method
          // call
          DetailAST down = aLeftMostMethodTargetAST.getFirstChild();
          if (down != null && down.getType() == TokenTypes.DOT)
            aLeftMostMethodTargetAST = down.getFirstChild(); // go
          // to
          // next
          // call
          else
            break;
        }
      }
      // if (aLeftMostMethodTargetAST != null
      // && aLeftMostMethodTargetAST.getType() == TokenTypes.DOT) {
      // // DetailAST aFirstChild =
      // // aLeftMostMethodTargetAST.getFirstChild();
      // if (aLeftMostMethodTargetAST.getFirstChild().getText()
      // .equals("this")) {
      // // System.out.println ("found doot");
      // aLeftMostMethodTargetAST = aLeftMostMethodTargetAST
      // .getLastChild();
      // // aLeftMostMethodTargetAST =
      // // aLeftMostMethodTargetAST.getLastChild();
      // }
      // }
      // not syre we need this while with the previous code, but maybe
      // public variables
      if (aLeftMostMethodTargetAST != null) {
        while (aLeftMostMethodTargetAST.getType() == TokenTypes.DOT) {
          aLeftMostMethodTargetAST = aLeftMostMethodTargetAST.getFirstChild();
        }

      }
      // if (aLeftMostMethodTargetAST.getType() ==
      // TokenTypes.LITERAL_THIS) {
      // if (aLeftMostMethodTargetAST.getNextSibling() != null) {
      // aLeftMostMethodTargetAST =
      // aLeftMostMethodTargetAST.getNextSibling() ;
      // }
      // }
      // if (aLeftMostMethodTargetAST.getType() ==
      // TokenTypes.LITERAL_THIS) {
      // aTargetName = shortTypeName;
      // }
      // else
      if (aLeftMostMethodTargetAST == null) {
        aTargetName = aCastType;
      } else if (aLeftMostMethodTargetAST.getType() == TokenTypes.INDEX_OP) {
        aTargetName = aLeftMostMethodTargetAST.getFirstChild().getText() + "[]";
      } else {
        aTargetName = aLeftMostMethodTargetAST.getText();
      }

      // }
      // aLeftMostMethodTargetAST = aMethodNameAST
      // .getPreviousSibling();
      // } else if (ast.getType() == TokenTypes.LITERAL_NEW) {
      // final FullIdent anIdentifierType =
      // FullIdent.createFullIdentBelow(ast);
      // shortMethodName = toShortTypeName(anIdentifierType.getText());
      // aLeftMostMethodTargetAST = null;
      // }
    }
    DetailAST aCallEList = ast.findFirstToken(TokenTypes.ELIST);
    List<DetailAST> aCallParameters = getEListComponents(aCallEList);

    // String shortMethodName = aMethodNameAST.getText();
    // shortMethodName = aMethodNameAST.getText();

    String[] aNormalizedParts = null;
    // if (ast.getType() == 27) {

    // DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
    List<DetailAST> aList = findAllInOrderMatchingNodes(ast, TokenTypes.TYPE);

    if (aList.size() > 0) {
      DetailAST aType = aList.get(0);
      DetailAST aCast = aType.getLastChild();

      if (aCast != null && isClassIdent(aCast.getText())) // kludgy, we know cast should be a class
      {
        aNormalizedParts = new String[] { aCast.getText(), shortMethodName };
      }

      // aNormalizedParts = new String[] {aList
    }
    // }
    if (aNormalizedParts == null) {
      if (currentTree != aTreeAST) {
        aNormalizedParts = (String[]) astToContinuationData.get(ast);
        if (aNormalizedParts == null) {
          System.err.println("Normalizedname not saved");
        }
        // } else if (aLeftMostMethodTargetAST.getType() ==
        // TokenTypes.STRING_LITERAL) {
      } else {
        String anInstantiatedType = maybeReturnInstantiatedType(ast.getFirstChild());
        if (anInstantiatedType != null) {

          aNormalizedParts = new String[] { anInstantiatedType, shortMethodName };

        } else {
          // if (ast.getType() == 27) {
          //
          //// DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
          // List<DetailAST> aList = findAllInOrderMatchingNodes(ast, TokenTypes.TYPE );
          //
          // if (aList.size() > 0) {
          // DetailAST aType = aList.get(0);
          // DetailAST aCast = aType.getLastChild();
          // if (aCast != null) {
          // aNormalizedParts = new String[]{aCast.getText(), shortMethodName};
          // }
          //
          //// aNormalizedParts = new String[] {aList
          // }
          // }

          FullIdent aFullIdent = FullIdent.createFullIdentBelow(ast);
          String longMethodName = aFullIdent.getText();
          String[] aCallParts;
          if (longMethodName.length() > 0 && !Character.isLetter(longMethodName.charAt(0))) {
            aCallParts = new String[] { aTargetName, shortMethodName };
          } else {
            aCallParts = longMethodName.split("\\.");

            // String[] aCallParts = longMethodName.split("\\.");
            if (aTargetName != null && isIdentifier(aTargetName)) {
              aCallParts[0] = aTargetName;
            }
          }
          if (aNormalizedParts == null) {
            aNormalizedParts = toNormalizedClassBasedCall(aCallParts);
          }
        }
      }
    }
    // FullIdent aFullIdent =
    // FullIdent.createFullIdentBelow(aLeftMostMethodTargetAST.getParent().getParent());

    String aNormalizedLongName = toLongName(aNormalizedParts);
    String aCallee = toShortTypeOrVariableName(aNormalizedLongName);
    // String aNormalizedLongTypeName = toLongTypeName(aNormalizedParts[0]);
    StringBuilder aNormalizedLongTypeName = new StringBuilder();
    for (int i = 0; i < aNormalizedParts.length - 1; i++) {

      String aCurrentPart = aNormalizedParts[i];
      // if (aCurrentPart.contains("QUIT")) {
      // System.out.println ("found QUIT");
      // }
      if (i == 0) {
        aCurrentPart = toLongTypeName(aCurrentPart);
      }
      // if (aCurrentPart.equals("QUIT")) {
      // System.out.println("QUIT");
      // }
      if (i > 0) {
        aNormalizedLongTypeName.append(".");
      }
      aNormalizedLongTypeName.append(aCurrentPart);

    }
    if (!aNormalizedLongTypeName.toString().startsWith(VARIABLE_PREFIX)) {
      if (!isClassIdent(aNormalizedLongTypeName.toString())) {
        aNormalizedLongTypeName.insert(0, VARIABLE_PREFIX);
      }
    }
    CallInfo result = new ACallInfo(ast, getFullTypeName(), currentMethodName,
            new ArrayList(
                    // currentMethodParameterTypes), toLongTypeName(aNormalizedParts[0]), aCallee,
                    currentMethodParameterTypes),
            aNormalizedLongTypeName.toString(), aCallee,

            aCallParameters, aNormalizedParts, aCastType);
    if (aLeftMostMethodTargetAST != null) {
      // String aTargetName;
      //
      // if (aLeftMostMethodTargetAST.getType() == TokenTypes.INDEX_OP) {
      // aTargetName = aLeftMostMethodTargetAST.getFirstChild()
      // .getText() + "[]";
      // } else {
      // aTargetName = aLeftMostMethodTargetAST.getText();
      // }
      // // String aTargetName = aLeftMostMethodTargetAST.getText();

      if (isGlobalDeclaredVariable(aTargetName)) {
        List<CallInfo> aVariableCalls = getVariableCalls(aTargetName);
        // CallInfo aCall = new ACallInfo(
        // currentMethodName, aNormalizedParts[0], aNormalizedParts[1],
        // aCallParameters, aNormalizedParts );
        aVariableCalls.add(result);
      }
    }
    astToContinuationData.put(ast, aNormalizedParts);

    return result;

  }

  // do nto really need this, the register method call works just fine!
  public CallInfo registerConstructorCallAndtoNormalizedParts(DetailAST ast, DetailAST aTreeAST) {

    String shortMethodName = null;
    if (ast.getText().startsWith("this")) {// cannot find a type
      shortMethodName = toShortTypeOrVariableName(getFullTypeName(currentTree));
    } else {

      FullIdent anIdentifierType = FullIdent.createFullIdentBelow(ast);
      shortMethodName = toShortTypeOrVariableName(anIdentifierType.getText());
    }

    // final FullIdent anIdentifierType =
    // FullIdent.createFullIdentBelow(ast);
    // String shortMethodName = toShortTypeName(anIdentifierType.getText());

    DetailAST aCallEList = ast.findFirstToken(TokenTypes.ELIST);
    // currentMethodNameAST = aCallEList; // previous sibling?
    currentMethodNameAST = aCallEList.getPreviousSibling(); // current
    // calling
    // method or
    // called
    // method?

    List<DetailAST> aCallParameters = getEListComponents(aCallEList);

    // String shortMethodName = aMethodNameAST.getText();
    // shortMethodName = aMethodNameAST.getText();

    String[] aNormalizedParts = null;
    if (currentTree != aTreeAST) {
      aNormalizedParts = (String[]) astToContinuationData.get(ast);
      if (aNormalizedParts == null) {
        System.err.println("Normalizedname not saved");
      }
      // } else if (aLeftMostMethodTargetAST.getType() ==
      // TokenTypes.STRING_LITERAL) {
    } else {

      aNormalizedParts = new String[] { shortMethodName, shortMethodName };
    }
    // need to worry about cast at some point I assume

    CallInfo result = new ACallInfo(ast, getFullTypeName(), currentMethodName,
            new ArrayList(currentMethodParameterTypes), toLongTypeName(aNormalizedParts[0]),
            aNormalizedParts[1], aCallParameters, aNormalizedParts, null);

    astToContinuationData.put(ast, aNormalizedParts);

    return result;

  }

  List<CallInfo> getVariableCalls(String aName) {
    List<CallInfo> aVariableCalls = globalVariableToCall.get(aName);
    if (aVariableCalls == null) {
      aVariableCalls = new ArrayList();
      globalVariableToCall.put(aName, aVariableCalls);
    }
    return aVariableCalls;

  }

  protected void visitMethodCall(DetailAST ast) {
    // if (ast.getType() == 27) {
    // String aString = toStringList(ast);
    // ast.getFirstChild();
    // DetailAST aType = ast.findFirstToken(TokenTypes.TYPE);
    // List aList = findAllInOrderMatchingNodes(ast, TokenTypes.TYPE );
    // System.out.println(aString + + TokenTypes.TYPE + aType);
    // }
    // if (!checkIncludeExcludeTagsOfCurrentType())
    // return;
    CallInfo aCallInfo = registerMethodCallAndtoNormalizedParts(ast, currentTree);
    methodsCalledByCurrentMethod.add(aCallInfo);

    // String[] aNormalizedParts =
    // registerMethodCallAndtoNormalizedParts(ast,
    // currentTree).getNotmalizedCall();
    // methodsCalledByCurrentMethod.add(aNormalizedParts);
  }

  protected void visitConstructorCall(DetailAST ast) {
    if (!checkIncludeExcludeTagsOfCurrentType())
      return;
    CallInfo aCallInfo = registerConstructorCallAndtoNormalizedParts(ast, currentTree);
    // String[] aNormalizedParts =
    // registerConstructorCallAndtoNormalizedParts(ast,
    // currentTree).getNotmalizedCall();
    // methodsCalledByCurrentMethod.add(aNormalizedParts);
    methodsCalledByCurrentMethod.add(aCallInfo);
  }

  public static boolean isMethodDefOrCall(DetailAST anIdentAST) {
    DetailAST aParent = anIdentAST.getParent();
    if (aParent != null && aParent.getType() == TokenTypes.DOT) {
      return isMethodDefOrCall(aParent);
    }
    DetailAST aRightSibling = anIdentAST.getNextSibling();
    return aRightSibling != null && (aRightSibling.getType() == TokenTypes.LPAREN
            || aRightSibling.getType() == TokenTypes.ELIST);
  }

  public static boolean isType(DetailAST anIdentAST) {

    DetailAST aParent = anIdentAST.getParent();
    if (aParent != null && (aParent.getType() == TokenTypes.TYPE)) {
      return true;
    }
    ;
    if (aParent == null) {
      return false;
    }
    return isType(aParent); // for arrays
  }

  public static boolean isAnnotation(DetailAST anIdentAST) {

    DetailAST aParent = anIdentAST.getParent();
    return aParent != null && (aParent.getType() == TokenTypes.ANNOTATION);
  }

  public static boolean isInstantiation(DetailAST anIdentAST) {

    DetailAST aParent = anIdentAST.getParent();
    return aParent != null && (aParent.getType() == TokenTypes.LITERAL_NEW);
  }

  protected DetailAST toFullIdentAST(DetailAST anIdentAST) {
    DetailAST aParent = anIdentAST.getParent();
    if (aParent == null || aParent.getType() != TokenTypes.DOT) {
      return anIdentAST;
    }
    return toFullIdentAST(aParent);

  }

  protected void addToUnknownsAccessed(String aName, DetailAST anAST) {
    Map<String, Set<DetailAST>> aMap = (currentMethodName == null)
            ? currentStaticBlocks.getUnknownAccessed() : unknownVariablesAccessedByCurrentMethod;
    addToKeyToSetMap(aName, anAST, aMap);

    // Set<DetailAST> aReferences = unknownVariablesAccessedByCurrentMethod.get(aName);
    // if (aReferences == null) {
    // aReferences = new HashSet();
    // unknownVariablesAccessedByCurrentMethod.put(aName, aReferences);
    // }
    // aReferences.add(anAST);
  }

  protected void addToUnknownsAssigned(String aName, DetailAST anAST) {
    Map<String, Set<DetailAST>> aMap = (currentMethodName == null)
            ? currentStaticBlocks.getUnknownAssigned() : unknownVariablesAssignedByCurrentMethod;
    addToKeyToSetMap(aName, anAST, aMap);
    // Set<DetailAST> aReferences = unknownVariablesAssignedByCurrentMethod.get(aFullName);
    // if (aReferences == null) {
    // aReferences = new HashSet();
    // unknownVariablesAssignedByCurrentMethod.put(aFullName, aReferences);
    // }
    // aReferences.add(anAST);
  }

  protected void addToGlobalsAccessed(String aName, DetailAST anAST) {
    Map<String, Set<DetailAST>> aMap = (currentMethodName == null)
            ? currentStaticBlocks.getGlobalsAccessedMap() : globalsAccessedByCurrentMethod;
    addToKeyToSetMap(aName, anAST, aMap);
    // Set<DetailAST> aReferences = globalsAccessedByCurrentMethod.get(aName);
    // if (aReferences == null) {
    // aReferences = new HashSet();
    // globalsAccessedByCurrentMethod.put(aName, aReferences);
    // }
    // aReferences.add(anAST);
  }

  public static <KeyType, MapElementType> void addToKeyToSetMap(KeyType aKey,
          MapElementType anElement, Map<KeyType, Set<MapElementType>> aStringToSet) {
    Set<MapElementType> aSet = aStringToSet.get(aKey);
    if (aSet == null) {
      aSet = new HashSet();
      aStringToSet.put(aKey, aSet);
    }
    aSet.add(anElement);
  }

  protected void addToGlobalsAssigned(String aName, DetailAST anAST) {
    Map<String, Set<DetailAST>> aMap = (currentMethodName == null)
            ? currentStaticBlocks.getGlobalsAssignedMap() : globalsAssignedByCurrentMethod;
    addToKeyToSetMap(aName, anAST, aMap);
    //
    //
    // Set<DetailAST> aReferences = globalsAssignedByCurrentMethod.get(aName);
    // if (aReferences == null) {
    // aReferences = new HashSet();
    // globalsAssignedByCurrentMethod.put(aName, aReferences);
    // }
    // aReferences.add(anAST);
  }

  protected void addToLocalsAssigned(STVariable aLocalVariable) {
    if (aLocalVariable != null) {
//      ((currentMethodName == null) ? currentStaticBlocks.getLocalsAssigned()
//              : localsAssignedByCurrentMethod).add(aLocalVariable);
      
      if (currentMethodName == null && currentStaticBlocks != null && currentStaticBlocks.getLocalsAssigned() != null ) {
        currentStaticBlocks.getLocalsAssigned().add(aLocalVariable);
      } else if (localsAssignedByCurrentMethod != null) {
        localsAssignedByCurrentMethod.add(aLocalVariable);
      } else {
        System.err.println("Null locals assigned in current method or static blocks:" + currentFullFileName);
      }
      return;
      
      
    }
    // localsAssignedByCurrentMethod
  }

  protected void addToParametersAssigned(STVariable aLocalVariable) {
    if (aLocalVariable != null) {
      ((currentMethodName == null) ? currentStaticBlocks.getParametersAssigned()
              : parametersAssignedByCurrentMethod).add(aLocalVariable);
      return;
    }
  }

  // protected DetailAST lastFullIdentAST = null; /// hack hack !!!
  protected void visitIdent(DetailAST anIdentAST) {
    // if (!checkIncludeExcludeTagsOfCurrentType())
    // return;
    // if (currentMethodIsConstructor) {
    // System.out.println("constructor");
    // }
    if (!leftCurlySeen) {
      return;
    }
    if (getFullTypeName() == null)
      return;
    // if (currentMethodName == null)
    // return;
    DetailAST aFullIdentAST = toFullIdentAST(anIdentAST);
    DetailAST aNextSibling = anIdentAST.getNextSibling();
    if (aFullIdentAST != anIdentAST && aNextSibling != null
            && aNextSibling.getType() == TokenTypes.IDENT) {
      return;
    }
    if (isMethodDefOrCall(aFullIdentAST) || isType(anIdentAST) || isAnnotation(anIdentAST)
            || isInstantiation(anIdentAST)) {
      return;
    }
    String anIdentName = anIdentAST.getText();
    // if (fullTypeName.equals(anIdentName) || fullTypeName.endsWith("." + anIdentName )) {
    // return;
    // }
    Map<String, String> anOpenMethodScope = currentOpenScope();

    if (anOpenMethodScope != null) {
      if (anOpenMethodScope.get(anIdentName) != null) {
        return;
      }
    }
    FullIdent aFullIdent = FullIdent.createFullIdent(aFullIdentAST);

    String aFullIdentName = aFullIdent.getText();

    boolean isLHSOfAssignment = isLHSOfAssignment(anIdentAST);
    // STVariable aLocalVariable = getLocalVariable(anIdentName);
    STVariable aLocalVariable = getLocalVariable(anIdentName);

    STVariable aParameter = null;
    if (aLocalVariable == null) {
      aParameter = getParameterVariable(anIdentName);
    }
    if (anIdentAST.getParent().getType() == TokenTypes.DOT) {
      aLocalVariable = null;
      aParameter = null;
    }
    if (isLHSOfAssignment) {
      // STVariable aVariable = getLocalVariable(anIdentName);
      if (aLocalVariable != null) {
        addToLocalsAssigned(aLocalVariable);
        // localsAssignedByCurrentMethod.add(aLocalVariable);
        return;
      }
      // aLocalVariable = getParameterVariable(anIdentName); //this was commented out
      if (aParameter != null) { // this was aLocalVariable
        addToParametersAssigned(aParameter);
        // parametersAssignedByCurrentMethod.add(aLocalVariable);
        return;
      }
    }
    if (aLocalVariable != null || aParameter != null) {
      return; // not interested in accesses
    }
    // if (!isGlobal(anIdentName))
    // return;
    // if (isLHSOfAssignment(anIdentAST)) {
    // if (isLHSOfAssignment) {
    // List<DetailAST> aLHSs = globalIdentToLHS.get(anIdentName);
    // if (aLHSs == null) {
    // aLHSs = new ArrayList();
    // globalIdentToLHS.put(anIdentName, aLHSs);
    // }
    // aLHSs.add(anIdentAST);
    // } else {
    // List<DetailAST> aRHSs = globalIdentToRHS.get(anIdentName);
    // if (aRHSs == null) {
    // aRHSs = new ArrayList();
    // globalIdentToRHS.put(anIdentName, aRHSs);
    // }
    // aRHSs.add(anIdentAST);
    // }
    STNameable aGlobal = getGlobalDeclaredVariable(anIdentName);
    boolean isGlobal = aGlobal != null;
    // boolean isGlobal = isGlobalDeclaredVariable(anIdentName);
    if (!isGlobal) {
      // unknownVariablesAccessedByCurrentMethod.add(aFullIdentName);
      addToUnknownsAccessed(aFullIdentName, anIdentAST);
    } else {
      addToGlobalsAccessed(anIdentName, anIdentAST);
      // Set<DetailAST> anAccesses = globalsAccessedByCurrentMethod.get(anIdentName);
      // if (anAccesses == null) {
      // anAccesses = new HashSet();
      // globalsAccessedByCurrentMethod.put(anIdentName, anAccesses);
      // }
      // anAccesses.add(anIdentAST);
      // if (!globalsAccessedByCurrentMethod.contains(anIdentName)) {
      //// globalsAccessedByCurrentMethod.add(anIdentName);
      // globalsAccessedByCurrentMethod.add(fullTypeName + "." + anIdentName);
      // }

    }

    boolean isGlobalAssignedVariable = isGlobalAssignedVariable(anIdentAST);
    if (isLHSOfAssignment) {
      if (isGlobal) {
        currentMethodAssignsToGlobalVariable = true; // this now redundant
        addToGlobalsAssigned(anIdentName, anIdentAST);
        // Set<DetailAST> anAssignments = globalsAssignedByCurrentMethod.get(anIdentName);
        // if (anAssignments == null) {
        // anAssignments = new HashSet();
        // globalsAssignedByCurrentMethod.put(anIdentName, anAssignments);
        // }
        // anAssignments.add(anIdentAST);
        // if (!globalsAssignedByCurrentMethod.contains(anIdentName)) {
        //
        // globalsAssignedByCurrentMethod.add(fullTypeName + "." + anIdentName);
        // }
        List<DetailAST> aLHSs = globalIdentToLHS.get(anIdentName);
        if (aLHSs == null) {
          aLHSs = new ArrayList();
          globalIdentToLHS.put(anIdentName, aLHSs);
        }
        aLHSs.add(anIdentAST);
      } else {
        // unknownVariablesAssignedByCurrentMethod.add(anIdentName);
        addToUnknownsAssigned(aFullIdentName, anIdentAST);
      }

    } else {
      List<DetailAST> aRHSs = globalIdentToRHS.get(anIdentName);
      if (aRHSs == null) {
        aRHSs = new ArrayList();
        globalIdentToRHS.put(anIdentName, aRHSs);
      }
      aRHSs.add(anIdentAST);
    }
    // if (isGlobalAssignedVariable) {
    // currentMethodAssignsToGlobalVariable = true; // this now redundant
    // if (!globalsAssignedByCurrentMethod.contains(anIdentName)) {
    //
    // globalsAssignedByCurrentMethod.add(anIdentName);
    // }
    // List<DetailAST> aLHSs = globalIdentToLHS.get(anIdentName);
    // if (aLHSs == null) {
    // aLHSs = new ArrayList();
    // globalIdentToLHS.put(anIdentName, aLHSs);
    // }
    // aLHSs.add(anIdentAST);
    //
    // } else {
    // List<DetailAST> aRHSs = globalIdentToRHS.get(anIdentName);
    // if (aRHSs == null) {
    // aRHSs = new ArrayList();
    // globalIdentToRHS.put(anIdentName, aRHSs);
    // }
    // aRHSs.add(anIdentAST);
    // }
    //

  }

  protected void oldVisitIdent(DetailAST anIdentAST) {
    // if (!checkIncludeExcludeTagsOfCurrentType())
    // return;
    // if (currentMethodIsConstructor) {
    // System.out.println("constructor");
    // }

    // if (currentMethodName == null)
    // return;
    DetailAST aFullIdentAST = toFullIdentAST(anIdentAST);
    DetailAST aNextSibling = anIdentAST.getNextSibling();
    if (aFullIdentAST != anIdentAST && aNextSibling != null
            && aNextSibling.getType() == TokenTypes.IDENT) {
      return;
    }
    if (isMethodDefOrCall(aFullIdentAST) || isType(anIdentAST) || isAnnotation(anIdentAST)) {
      return;
    }
    String anIdentName = anIdentAST.getText();
    Map<String, String> anOpenMethodScope = currentOpenScope();

    if (anOpenMethodScope != null) {
      if (anOpenMethodScope.get(anIdentName) != null) {
        return;
      }
    }
    FullIdent aFullIdent = FullIdent.createFullIdent(aFullIdentAST);

    String aFullIdentName = aFullIdent.getText();

    boolean isLHSOfAssignment = isLHSOfAssignment(anIdentAST);
    STVariable aLocalVariable = getLocalVariable(anIdentName);
    STVariable aParameter = null;
    if (aLocalVariable == null) {
      aParameter = getParameterVariable(anIdentName);
    }
    if (isLHSOfAssignment) {
      // STVariable aVariable = getLocalVariable(anIdentName);
      if (aLocalVariable != null) {
        localsAssignedByCurrentMethod.add(aLocalVariable);
        return;
      }
      // aLocalVariable = getParameterVariable(anIdentName);
      if (aLocalVariable != null) {
        parametersAssignedByCurrentMethod.add(aLocalVariable);
        return;
      }
    }
    if (aLocalVariable != null || aParameter != null) {
      return; // not interested in accesses
    }
    // if (!isGlobal(anIdentName))
    // return;
    // if (isLHSOfAssignment(anIdentAST)) {
    // if (isLHSOfAssignment) {
    // List<DetailAST> aLHSs = globalIdentToLHS.get(anIdentName);
    // if (aLHSs == null) {
    // aLHSs = new ArrayList();
    // globalIdentToLHS.put(anIdentName, aLHSs);
    // }
    // aLHSs.add(anIdentAST);
    // } else {
    // List<DetailAST> aRHSs = globalIdentToRHS.get(anIdentName);
    // if (aRHSs == null) {
    // aRHSs = new ArrayList();
    // globalIdentToRHS.put(anIdentName, aRHSs);
    // }
    // aRHSs.add(anIdentAST);
    // }
    STNameable aGlobal = getGlobalDeclaredVariable(anIdentName);
    boolean isGlobal = aGlobal != null;
    // boolean isGlobal = isGlobalDeclaredVariable(anIdentName);
    if (!isGlobal) {
      // unknownVariablesAccessedByCurrentMethod.add(aFullIdentName);
      addToUnknownsAccessed(aFullIdentName, anIdentAST);
    } else {
      Set<DetailAST> anAccesses = globalsAccessedByCurrentMethod.get(anIdentName);
      if (anAccesses == null) {
        anAccesses = new HashSet();
        globalsAccessedByCurrentMethod.put(anIdentName, anAccesses);
      }
      anAccesses.add(anIdentAST);
      // if (!globalsAccessedByCurrentMethod.contains(anIdentName)) {
      //// globalsAccessedByCurrentMethod.add(anIdentName);
      // globalsAccessedByCurrentMethod.add(fullTypeName + "." + anIdentName);
      // }

    }

    boolean isGlobalAssignedVariable = isGlobalAssignedVariable(anIdentAST);
    if (isLHSOfAssignment) {
      if (isGlobal) {
        currentMethodAssignsToGlobalVariable = true; // this now redundant
        Set<DetailAST> anAssignments = globalsAssignedByCurrentMethod.get(anIdentName);
        if (anAssignments == null) {
          anAssignments = new HashSet();
          globalsAssignedByCurrentMethod.put(anIdentName, anAssignments);
        }
        anAssignments.add(anIdentAST);
        // if (!globalsAssignedByCurrentMethod.contains(anIdentName)) {
        //
        // globalsAssignedByCurrentMethod.add(fullTypeName + "." + anIdentName);
        // }
        List<DetailAST> aLHSs = globalIdentToLHS.get(anIdentName);
        if (aLHSs == null) {
          aLHSs = new ArrayList();
          globalIdentToLHS.put(anIdentName, aLHSs);
        }
        aLHSs.add(anIdentAST);
      } else {
        // unknownVariablesAssignedByCurrentMethod.add(anIdentName);
        addToUnknownsAssigned(aFullIdentName, anIdentAST);
      }

    } else {
      List<DetailAST> aRHSs = globalIdentToRHS.get(anIdentName);
      if (aRHSs == null) {
        aRHSs = new ArrayList();
        globalIdentToRHS.put(anIdentName, aRHSs);
      }
      aRHSs.add(anIdentAST);
    }
    // if (isGlobalAssignedVariable) {
    // currentMethodAssignsToGlobalVariable = true; // this now redundant
    // if (!globalsAssignedByCurrentMethod.contains(anIdentName)) {
    //
    // globalsAssignedByCurrentMethod.add(anIdentName);
    // }
    // List<DetailAST> aLHSs = globalIdentToLHS.get(anIdentName);
    // if (aLHSs == null) {
    // aLHSs = new ArrayList();
    // globalIdentToLHS.put(anIdentName, aLHSs);
    // }
    // aLHSs.add(anIdentAST);
    //
    // } else {
    // List<DetailAST> aRHSs = globalIdentToRHS.get(anIdentName);
    // if (aRHSs == null) {
    // aRHSs = new ArrayList();
    // globalIdentToRHS.put(anIdentName, aRHSs);
    // }
    // aRHSs.add(anIdentAST);
    // }
    //

  }

  public static boolean inAssignment(DetailAST anIdentAST) {
    return hasAncestor(anIdentAST, TokenTypes.ASSIGN);

  }

  public static boolean hasAncestor(DetailAST aChildAST, int anAncestorTokenType) {
    DetailAST aParentAST = aChildAST.getParent();
    if (aParentAST == null) {
      return false;
    }
    if (aParentAST.getType() == anAncestorTokenType) {
      return true;
    }
    return hasAncestor(aParentAST, anAncestorTokenType);

  }

  /*
   * Code taken from anIdentAST
   */
  public static boolean isLHSOfAssignment(DetailAST anIdentAST) {
    DetailAST aParentAST = anIdentAST.getParent();
    final int parentType = aParentAST.getType();
    if (aParentAST.getType() == TokenTypes.DOT)
      return isLHSOfAssignment(aParentAST);
    // TODO: is there better way to check is ast
    // in left part of assignment?

    return ((TokenTypes.POST_DEC == parentType || TokenTypes.DEC == parentType
            || TokenTypes.POST_INC == parentType || TokenTypes.INC == parentType
            || TokenTypes.ASSIGN == parentType || TokenTypes.PLUS_ASSIGN == parentType
            || TokenTypes.MINUS_ASSIGN == parentType || TokenTypes.DIV_ASSIGN == parentType
            || TokenTypes.STAR_ASSIGN == parentType || TokenTypes.MOD_ASSIGN == parentType
            || TokenTypes.SR_ASSIGN == parentType || TokenTypes.BSR_ASSIGN == parentType
            || TokenTypes.SL_ASSIGN == parentType || TokenTypes.BXOR_ASSIGN == parentType
            || TokenTypes.BOR_ASSIGN == parentType || TokenTypes.BAND_ASSIGN == parentType)
            && anIdentAST.getParent().getFirstChild() == anIdentAST); // this should be
    // checked
    // first?
  }

  public boolean isGlobalAssignedVariable(DetailAST anIdentAST) {
    String aName = anIdentAST.getText();

    return (lookupLocal(aName) == null || (anIdentAST.getPreviousSibling() != null
            && (anIdentAST.getPreviousSibling().getType() == TokenTypes.LITERAL_THIS)
            || anIdentAST.getPreviousSibling().getText().equals(shortTypeName)

    )) && isLHSOfAssignment(anIdentAST);

  }

  public boolean isGlobalOrUnknownVariable(DetailAST anIdentAST) {
    String aName = anIdentAST.getText();

    return (lookupLocal(aName) == null || (anIdentAST.getPreviousSibling() != null
            && anIdentAST.getPreviousSibling().getType() == TokenTypes.LITERAL_THIS));

  }

  protected List<DetailAST> pendingChecks() {
    List<DetailAST> result = astToPendingChecks.get(currentTree);
    if (result == null) {
      result = new ArrayList<>();
      astToPendingChecks.put(currentTree, result);
      astToFileContents.put(currentTree, getFileContents());
      fileNameToTree.put(getFileContents().getFileName(), currentTree);

    }
    return result;
  }

  protected String getShortFileName(DetailAST aTreeAST) {
    return shortFileName(astToFileContents.get(aTreeAST).getFileName());
    // String aFileName;
    // if (aTreeAST == currentTree) {
    // aFileName = getFileContents().getFilename();
    // } else {
    // aFileName = astToFileContents.get(aTreeAST)
    // .getFilename();
    // }
    // return shortFileName(aFileName);
  }

  protected void maybeCleanUpPendingChecks(DetailAST aNewTree) {
    String aFileName = getFileContents().getFileName();
    DetailAST prevIncarnation = fileNameToTree.get(aFileName);
    if (prevIncarnation != null) {
      astToFileContents.remove(prevIncarnation);
      astToPendingChecks.get(prevIncarnation).clear();
    }
    pendingChecks().clear(); // this is needed to allocate a new entry

  }

  protected void initializeTypeState() {
    typeTags = emptyNameableList;
    computedTypeTags = emptyNameableList;
    typeScope.clear();
    // fullTypeName = null;
    setFullTypeName(null);
    // shortTypeName = null;
    setShortTypeName(null);

    // isInterface = false;
    leftCurlySeen = false;
    // isEnum = false;
    isGeneric = false;
    typeParameterNames.clear();
    isElaboration = false;
    // stMethods.clear();
    allImportsOfThisClass.clear();
    globalVariables.clear();
    globalSTVariables.clear();
    typesInstantiated.clear();
    typesInstantiatedByCurrentMethod.clear();
    globalVariableToCall.clear();
    globalVariableToType.clear();
    globalIdentToLHS.clear();
    globalIdentToRHS.clear();
    // globalVariableToRHS.clear();
    typeTagsInitialized = false;
    propertyNames = emptyArrayList;
    editablePropertyNames = emptyArrayList;
    methodsDeclaredString = null;
    variablesDeclaredString = null;
    propertiesDeclaredString = null;
    statisticsString = null;
  }

  @Override
  public void doBeginTree(DetailAST ast) {
    super.doBeginTree(ast);
    // method initializations, should be in visit method
    currentStaticBlocks = new AStaticBlocks(STType.STATIC_BLOCKS_NAME);
    currentMethodName = null;
    currentMethodAssignsToGlobalVariable = false;
    currentMethodScope.clear();
    methodsCalledByCurrentMethod.clear();
    globalsAccessedByCurrentMethod.clear();
    globalsAssignedByCurrentMethod.clear();
    unknownVariablesAssignedByCurrentMethod.clear();
    unknownVariablesAccessedByCurrentMethod.clear();
    // unknownMethodsCalledByCurrentMethod.clear();
    // globalIdentToLHS.clear();
    // globalIdentToRHS.clear();

    // type initializations
    if (!getVisitInnerClasses())
      initializeTypeState();
    // typeTags = emptyNameableList;
    // computedTypeTags = emptyNameableList;
    // typeScope.clear();
    // fullTypeName = null;
    // shortTypeName = null;
    // isInterface = false;
    // isEnum = false;
    // isGeneric = false;
    // isElaboration = false;
    // // stMethods.clear();
    // imports.clear();
    // globalVariables.clear();
    // typesInstantiated.clear();
    // typesInstantiatedByCurrentMethod.clear();
    // globalVariableToCall.clear();
    // globalVariableToType.clear();
    // globalVariableToRHS.clear();
    // typeTagsInitialized = false;
    // propertyNames = emptyArrayList;
    // editablePropertyNames = emptyArrayList;

    currentTree = ast;

    maybeCleanUpPendingChecks(ast);
    // pendingChecks().clear();
  }

  protected void processMethodAndClassData() {

  }

  @Override
  public void doFinishTree(DetailAST ast) {
    // if (checkIncludeExcludeTagsOfCurrentType()) {
    // // System.out.println("finish tree called:" + ast + " "
    // // + getFileContents().getFilename());
    // if (currentMethodName != null)
    // processPreviousMethodData();
    // processMethodAndClassData();
    // }
    // this is more efficient
    processDeferredChecks();

  }

  public void processDeferredChecks() {
    doPendingChecks();

  }

  // public static boolean isPublicAndInstance(DetailAST methodOrVariableDef)
  // {
  // boolean foundPublic = false;
  // final DetailAST modifiersAst = methodOrVariableDef
  // .findFirstToken(TokenTypes.MODIFIERS);
  // if (modifiersAst.getFirstChild() != null) {
  //
  // for (DetailAST modifier = modifiersAst.getFirstChild(); modifier != null;
  // modifier = modifier
  // .getNextSibling()) {
  // // System.out.println("Checking modifier:" + modifier);
  // if (modifier.getType() == TokenTypes.LITERAL_STATIC) {
  // // System.out.println("Not instance");
  // return false;
  // }
  // if (modifier.getType() == TokenTypes.LITERAL_PUBLIC) {
  // foundPublic = true;
  // }
  //
  // }
  // }
  //
  // return foundPublic;
  // }
  // public static boolean isPublicAndInstance(DetailAST methodOrVariableDef)
  // {
  // return isPublic(methodOrVariableDef)
  // && ! isStatic(methodOrVariableDef);
  // }
  // public static boolean isPublic(DetailAST methodOrVariableDef) {
  // return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PUBLIC);
  //
  // }
  // public static boolean isStatic(DetailAST methodOrVariableDef) {
  // return methodOrVariableDef.branchContains(TokenTypes.LITERAL_STATIC);
  //
  // }
  // public static boolean isFinal(DetailAST methodOrVariableDef) {
  // return methodOrVariableDef.branchContains(TokenTypes.FINAL);
  //
  // }
  // public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef)
  // {
  // boolean foundStatic = false;
  // final DetailAST modifiersAst = methodOrVariableDef
  // .findFirstToken(TokenTypes.MODIFIERS);
  // if (modifiersAst.getFirstChild() != null) {
  //
  // for (DetailAST modifier = modifiersAst.getFirstChild(); modifier != null;
  // modifier = modifier
  // .getNextSibling()) {
  // // System.out.println("Checking modifier:" + modifier);
  // if (modifier.getType() == TokenTypes.FINAL) {
  // // System.out.println("Not instance");
  // return false;
  // }
  // if (modifier.getType() == TokenTypes.LITERAL_STATIC) {
  // foundStatic = true;
  // }
  //
  // }
  // }
  //
  // return foundStatic;
  // }
  // public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef)
  // {
  // return isStatic (methodOrVariableDef)
  // && ! isFinal(methodOrVariableDef);
  // }
  public Boolean doPendingCheck(DetailAST ast, DetailAST aTreeAST) {
    return false;
  }

  protected DetailAST checkedTree;

  // pending check stuff
  public void doPendingChecks() {
    // for (List<FullIdent> aPendingTypeUses:astToPendingTypeUses.values())
    // {

    for (DetailAST aPendingAST : astToPendingChecks.keySet()) {
      if (aPendingAST == currentTree)
        continue;
      List<DetailAST> aPendingChecks = astToPendingChecks.get(aPendingAST);
      // FileContents aFileContents = astToFileContents.get(anAST);
      // setFileContents(aFileContents);

      if (aPendingChecks.isEmpty())
        continue;
      List<DetailAST> aPendingTypeChecksCopy = new ArrayList(aPendingChecks);
      for (DetailAST aPendingCheck : aPendingTypeChecksCopy) {
        specificationVariablesToUnifiedValues.clear();
        checkedTree = aPendingAST;
        // System.out.println ("Doing pending check: " +
        // getName(getEnclosingTypeDeclaration(aPendingCheck)));
        deferLogging();
        if (doPendingCheck(aPendingCheck, aPendingAST) != null && isFirstPass()) {
          aPendingChecks.remove(aPendingCheck);
          flushLogAndResumeLogging();
        } else {
          clearLogAndResumeLogging();
        }

      }
    }
  }

  protected void maybeAddToPendingTypeChecks(DetailAST ast) {
    if (!checkIncludeExcludeTagsOfCurrentType())
      return;
    specificationVariablesToUnifiedValues.clear();
    checkedTree = currentTree;

    deferLogging();
    // checkedTree = currentTree;
    if (doPendingCheck(ast, currentTree) == null && isFirstPass()) {
      clearLogAndResumeLogging();
      // System.out.println ("added to pending checks:" +
      // getName(getEnclosingTypeDeclaration(ast)));
      List<DetailAST> aPendingChecks = pendingChecks();
      if (!aPendingChecks.contains(ast))
        aPendingChecks.add(ast);
      // pendingChecks().add(ast);
    } else {
      flushLogAndResumeLogging();
    }

    // if (isMatchingClassName(ident.getText())) {
    // log(ident.getLineNo(), ident.getColumnNo(), msgKey(),
    // ident.getText());
    // }
  }

  public static String shortFileName(String longName) {
    int index = longName.lastIndexOf('/');
    if (index <= 0)
      index = longName.lastIndexOf('\\');
    if (index <= 0)
      return longName;
    return longName.substring(index + 1);
  }

  // public static DetailAST getEnclosingMethodDeclaration(DetailAST anAST) {
  // return getEnclosingTokenType(anAST, TokenTypes.METHOD_DEF);
  // }
  //
  // public static DetailAST getEnclosingClassDeclaration(DetailAST anAST) {
  // return getEnclosingTokenType(anAST, TokenTypes.CLASS_DEF);
  // }
  // public static DetailAST getEnclosingInterfaceDeclaration(DetailAST anAST)
  // {
  // return getEnclosingTokenType(anAST, TokenTypes.INTERFACE_DEF);
  // }
  // public static DetailAST getEnclosingTypeDeclaration(DetailAST anAST) {
  // DetailAST aClassDef = getEnclosingClassDeclaration(anAST);
  // if (aClassDef == null)
  // return getEnclosingInterfaceDeclaration(anAST);
  // else
  // return aClassDef;
  // }

  // public static String getEnclosingShortClassName(DetailAST anAST) {
  // return getName(getEnclosingClassDeclaration(anAST));
  // }
  // public static String getEnclosingShortTypeName(DetailAST anAST) {
  // return getName(getEnclosingTypeDeclaration(anAST));
  // }
  // public static String getEnclosingMethodName(DetailAST anAST) {
  // return getName(getEnclosingMethodDeclaration(anAST));
  // }
  // // not physically but logically enclosing
  // public static DetailAST getEnclosingTokenType(DetailAST anAST, int
  // aTokenType) {
  // if (anAST == null) return null;
  // if (anAST.getType() == aTokenType) return anAST;
  // DetailAST aParent = anAST.getParent();
  // if (aParent != null)
  // return getEnclosingTokenType(aParent, aTokenType);
  // return
  // getFirstRightSiblingTokenType(anAST, aTokenType);
  // }
  // public static DetailAST getFirstRightSiblingTokenType(DetailAST anAST,
  // int aTokenType) {
  // if (anAST == null) return null;
  // if (anAST.getType() == aTokenType) return anAST;
  // return getFirstRightSiblingTokenType(anAST.getNextSibling(), aTokenType);
  //
  // }
  public static DetailAST getRoot(DetailAST anAST, int aTokenType) {
    if (anAST == null)
      return null;
    DetailAST aParent = anAST.getParent();
    if (aParent == null)
      return aParent;
    return getRoot(aParent, aTokenType);

  }

  public int lineNo(DetailAST ast, DetailAST aTreeAST) {
    return aTreeAST == currentTree ? ast.getLineNo() : 0;
  }

  public int lineNo(FullIdent aFullIdent, DetailAST aTreeAST) {
    return aTreeAST == currentTree ? aFullIdent.getLineNo() : 0;
  }

  public int columnNo(DetailAST ast, DetailAST aTreeAST) {
    return aTreeAST == currentTree ? ast.getColumnNo() : 0;
  }

  public int columnNo(FullIdent aFullIdent, DetailAST aTreeAST) {
    return aTreeAST == currentTree ? aFullIdent.getColumnNo() : 0;
  }

  // public boolean contains(List<STNameable> aTags, String aTag) {
  // for (STNameable aNameable:aTags) {
  // if (matchesStoredTag(aNameable.getName(), aTag))
  //
  // // if (aNameable.getName().equals(aTag))
  // return true;
  // }
  // return false;
  // }
  //
  // public static String maybeStripQuotes(String aString) {
  // if (aString.indexOf("\"") != -1) // quote rather than named constant
  // return aString.substring(1, aString.length() -1);
  // return aString;
  // }
  // public static Boolean matchesStoredTag(String aStoredTag, String
  // aDescriptor) {
  // return
  // maybeStripQuotes(aStoredTag).equals(maybeStripQuotes(aDescriptor));
  //
  // }
  //
  // public Boolean matchesMyType( String aDescriptor, String aShort) {
  // String aClassName = shortTypeName;
  // if (aDescriptor == null || aDescriptor.length() == 0)
  // return true;
  // if (aDescriptor.startsWith("@")) {
  // String aTag = aDescriptor.substring(1);
  // return contains(typeTags(), aTag);
  // }
  // return aClassName.equals(aDescriptor);
  // }

  // static List<STNameable> emptyList = new ArrayList();
  // public static boolean isArray(String aShortClassName) {
  // return aShortClassName.endsWith("[]");
  // }
  // public static boolean isJavaLangClass(String aShortClassName) {
  // return javaLangClassesSet.contains(aShortClassName);
  // }
  // public static boolean isExternalImport(String aShortClassName) {
  // return externalImports.contains(aShortClassName);
  // }
  // public List<STNameable> getTags(String aShortClassName) {
  // List<STNameable> aTags = emptyList;
  //
  // // these classes have no tags
  // // if ( aShortClassName.endsWith("[]") ||
  // // allKnownImports.contains(aShortClassName) ||
  // // javaLangClassesSet.contains(aShortClassName) ) {
  // // return emptyList;
  // // }
  // if ( isArray(aShortClassName) ||
  // isJavaLangClass(aShortClassName) ) {
  // return emptyList;
  // }
  // if (shortTypeName == null || // guaranteed to not be a pending check
  // aShortClassName.equals(shortTypeName)) {
  // aTags = typeTags();
  // } else {
  // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
  // .getSTClassByShortName(aShortClassName);
  // if (anSTType == null) {
  // if (isExternalImport(aShortClassName)) // check last as we are not really
  // sure about external
  // return emptyList;
  // return null;
  // }
  // aTags = Arrays.asList(anSTType.getTags());
  // }
  // return aTags;
  //
  // }
  // public Boolean matchesType(String aDescriptor, String aShortClassName) {
  // if (aDescriptor == null || aDescriptor.length() == 0)
  // return true;
  // if (!aDescriptor.startsWith("@")) {
  // return aShortClassName.equals(aDescriptor);
  // }
  // List<STNameable> aTags = getTags(aShortClassName);
  // if (aTags == null)
  // return null;
  // // List<STNameable> aTags = null;
  // // if (shortTypeName == null || // guaranteed to not be a pending check
  // // aShortClassName.equals(shortTypeName)) {
  // // aTags = typeTags();
  // // } else {
  // // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
  // // .getSTClassByShortName(aShortClassName);
  // // if (anSTType == null)
  // // return null;
  // // aTags = Arrays.asList(anSTType.getTags());
  // // }
  // String aTag = aDescriptor.substring(1);
  //
  // return contains(aTags, aTag);
  // }
  protected boolean isPrefix(String aTarget, List<String> aPrefixes, String aSourceClassName) {
    for (String aPrefix : aPrefixes) {
      String[] aPrefixParts = aPrefix.split(TYPE_SEPARATOR);
      if ((aPrefixParts.length == 2) && !matchesTypeUnifying(aPrefixParts[0], aSourceClassName))
        continue; // not relevant
      String aTruePrefix = aPrefixParts.length == 2 ? aPrefixParts[1] : aPrefix;
      if (aTarget.startsWith(aTruePrefix) || aTruePrefix.equals(MATCH_ANYTHING))
        return true;
    }
    return false;
  }

  protected Boolean containedInClasses(String aTarget, List<String> aList,
          String aSourceClassName) {
    for (String aMember : aList) {
      String[] aMemberParts = aMember.split(TYPE_SEPARATOR);
      // if ((aMemberParts.length == 2) && !matchesMyType(aMemberParts[0],
      // aSourceClassName))
      Boolean match = matchesTypeUnifying(aMemberParts[0], aSourceClassName);
      if (match == null) {
        return null;
      }
      if ((aMemberParts.length == 2) && !match)

        // if ((aMemberParts.length == 2) &&
        // !matchesType(aMemberParts[0], aSourceClassName))
        continue; // not relevant
      String aTrueMember = aMemberParts.length == 2 ? aMemberParts[1] : aMember;
      if (aTarget.equals(aTrueMember))
        return true;
    }
    return false;
  }

  // public static DetailAST getLastDescendent(DetailAST ast) {
  // DetailAST result = ast.getFirstChild();
  // while (result.getChildCount() > 0)
  // result = result.getLastChild();
  // return result;
  // }

  // to share with a couple of subclsases
  public void setExcludeStructuredTypes(String[] newVal) {
    excludeStructuredTypes = new HashSet(Arrays.asList(newVal));
  }

  /*
   * checking if the target of call is an instantiated type
   */
  public static String maybeReturnInstantiatedType(DetailAST ast) {
    DetailAST aNewAST = ast.findFirstToken(TokenTypes.LITERAL_NEW);
    if (aNewAST != null) {
      DetailAST anIdentAST = aNewAST.findFirstToken(TokenTypes.IDENT);
      if (anIdentAST == null) {
        // System.out.println("No id found after new in:" + aNewAST.getText());
        FullIdent aFullIdent = FullIdent.createFullIdentBelow(aNewAST);
        if (aFullIdent != null) {
          String aText = aFullIdent.getText();
          int anIndex = aText.lastIndexOf(".");
          String aRetVal = aText.substring(anIndex + 1);
          return aRetVal;
        }

        return null;
      }
      if (aNewAST.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null
              || aNewAST.findFirstToken(TokenTypes.ARRAY_INIT) != null) {
        return anIdentAST.getText() + "[]";
      } else
        return anIdentAST.getText();
    } else if (ast.findFirstToken(TokenTypes.STRING_LITERAL) != null) {
      return "String";
    } else

      return null;
  }

  public String[] toNormalizedClassBasedCall(String[] aCallParts) {
    if (aCallParts.length == 3 && ("this".equals(aCallParts[0]) || "super".equals(aCallParts[0]))) { // unncessary
      // this.global
      aCallParts = new String[] { aCallParts[1], aCallParts[2] };
    }
    List<String> aCallPartsList = new ArrayList();
    if (aCallParts.length == 1 || "this".equals(aCallParts[0]) || "super".equals(aCallParts[0])) { // put
                                                                                                   // the
      // name
      // of
      // the
      // class
      // in
      // which
      // the
      // call
      // occurs
      aCallPartsList.add(getFullTypeName());
      aCallPartsList.add(aCallParts[aCallParts.length - 1]);
    } else if (aCallParts.length == 2) {
      String aVariable =aCallParts[0]; 
      if (aVariable == null) {
        System.err.println("Null variable in call parts " + Arrays.toString(aCallParts) + "of file" + currentFullFileName);
      }
//      String aType = lookupType(aCallParts[0]);
      String aType = lookupType(aVariable);

      if (aType != null && !aType.isEmpty()) { // not a static method
        aCallPartsList.add(aType);
        aCallPartsList.add(aCallParts[1]);
      } else {
        return aCallParts; // static call or sub method scope
      }
    } else {
      return aCallParts; // System.out.println() probabluy
    }
    return aCallPartsList.toArray(new String[0]);
  }

  // static {
  // javaLangClassesSet = new HashSet();
  // for (String aClass:javaLangClasses)
  // javaLangClassesSet.add(aClass);
  // }
  public void visitNew(DetailAST ast) {
    if (ast.findFirstToken(TokenTypes.ELIST) != null)
      visitConstructorCall(ast);
    else if (ast.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null)
      ;
    visitInstantiation(ast);
    // System.out.println ("array declaration");
  }

  public void visitTypeUse(DetailAST ast) {

  }

  public void visitReturn(DetailAST ast) {
    DetailAST anExprAST = ast.getFirstChild();
    if (anExprAST == null) {
      return;
    }

    DetailAST aDOTAST = anExprAST.getFirstChild();
    if (aDOTAST == null || aDOTAST.getType() != TokenTypes.DOT) {
      return;
    }
    DetailAST aLeftOfDotAST = aDOTAST.getFirstChild();
    if (aLeftOfDotAST == null) {
      return;
    }
    DetailAST aRightOfDotAST = aDOTAST.getLastChild();
    if (aRightOfDotAST == null || aRightOfDotAST.getType() != TokenTypes.LITERAL_CLASS) {
      return;
    }
    String aClassReturned = TypeVisitedCheck.toStringList(aLeftOfDotAST);
    int aLastIndexOfDot = aClassReturned.lastIndexOf('.');
    String aShortClassReturned = null;
    if (aLastIndexOfDot == -1) {
      aShortClassReturned = aClassReturned;
    } else {
      aShortClassReturned = aClassReturned.substring(aLastIndexOfDot + 2);
    }

    String anAstString = TypeVisitedCheck.toStringList(ast);
    String anAstStringParent = TypeVisitedCheck.toStringList(ast.getParent());
    String anAstStringSibiling = TypeVisitedCheck.toStringList(ast.getParent().getLastChild());

  }

  public static List<STNameable> getComputedAndExplicitTags(DetailAST ast, DetailAST aNameAST,
          String aTypeName) {
    List<STNameable> explicitTags = getExplicitTags(ast);
    STNameable aVariableNameable = new AnSTNameable(aNameAST, aNameAST.getText());
    STNameable aTypeNameable = new AnSTNameable(aTypeName);
    explicitTags.add(aVariableNameable);
    explicitTags.add(aTypeNameable);
    return explicitTags;

  }

  protected List<STNameable> getAllTags(DetailAST anAST, DetailAST aNameAST, String aTypeName,
          String aStart) {
    return getComputedAndExplicitTags(anAST, aNameAST, aTypeName);
  }

  public void maybeVisitMethodTags(DetailAST ast) {
    super.maybeVisitMethodTags(ast);
    List<STNameable> aComputedList = new ArrayList(currentMethodTags); // do we need a new list if
                                                                       // we use toArray later?
    aComputedList.add(new AnSTNameable(currentMethodNameAST, currentMethodName));
    currentMethodComputedTags = aComputedList;
  }

  protected Boolean isStructuredProperty(PropertyInfo aPropertyInfo) {
    String aType = aPropertyInfo.getType();
    STNameable aSetter = aPropertyInfo.getSetter();
    if (aSetter instanceof AnSTMethodFromMethod)
      return false;// external class
    if (isOEAtomic(aType) || aSetter == null)
      return false;
    STType aPropertyType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aType);
    if (aPropertyType != null) {
      if (!aPropertyType.hasSetter())
        return false; // immutable
      STNameable[] aTags = aPropertyType.getComputedTags();
      // STNameable[] aTags = aPropertyType.getAllComputedTags();
      if (excludeStructuredTypes.size() > 0) {
        if (aTags == null)
          return null;
        if (matchesSomeSpecificationTags(Arrays.asList(aTags), excludeStructuredTypes))
          return false;
      }
      return true;
    }
    return null;

  }

  protected void log(DetailAST anAST) {
    String aTypeName = getEnclosingShortClassName(anAST);

    log(anAST, currentTree, aTypeName);

  }

  protected void log(DetailAST ast, String... anExplanations) {
    DetailAST aTreeAST = getEnclosingTreeDeclaration(ast);
    // String aTypeName = getEnclosingShortClassName(ast);
    log(msgKey(), ast, aTreeAST, anExplanations);

  }

  protected void log(String aMessageKey, DetailAST ast, String... anExplanations) {
    DetailAST aTreeAST = getEnclosingTreeDeclaration(ast);
    // String aTypeName = getEnclosingShortClassName(ast);
    log(aMessageKey, ast, aTreeAST, anExplanations);

  }

  // move this up to ComprehensiveVisitCheck
  protected void log(DetailAST ast, DetailAST aTreeAST, Object... anExplanations) {
    String aMsgKey = isInfo() ? msgKeyInfo() : msgKeyWarning();

    // log(msgKey(), ast, aTreeAST, anExplanations);
    log(aMsgKey, ast, aTreeAST, anExplanations);

  }

  // move this up to ComprehensiveVisitCheck
  protected void log(FullIdent ast, DetailAST aTreeAST, Object... anExplanations) {
    String aMsgKey = isInfo() ? msgKeyInfo() : msgKeyWarning();
    log(aMsgKey, ast, aTreeAST, anExplanations);

    // log(msgKey(), ast, aTreeAST, anExplanations);

  }

  protected String getLongFileName(DetailAST aTreeAST) {
    if (currentFullFileName != null) {
      return currentFullFileName; // why was this not there earlier?
    }
    if (aTreeAST == currentTree)
      return getFileContents().getFileName();
    FileContents aFileContents = astToFileContents.get(aTreeAST);
    if (aFileContents != null) {
      return aFileContents.getFileName();
    }
    aFileContents = ProjectSTBuilderHolder.getSTBuilder().getAstToFileContents().get(aTreeAST);
    if (aFileContents != null) {
      return aFileContents.getFileName();
    } else {
      System.err.println("Could not find long file name");
      // String anASTName = getName(getEnclosingTypeDeclaration(aTreeAST));
      // Map<DetailAST, FileContents> aTable =
      // ProjectSTBuilderHolder.getSTBuilder().getAstToFileContents();
      //
      // for (DetailAST aKey:aTable.keySet()) {
      // FileContents aFileContents2 = aTable.get(aKey);
      // String aFileName = aFileContents2.getFilename();
      // if (aFileName.contains("Illegal")) {
      // System.out.println("Found illegal");
      // }
      // }
    }
    return "";
  }

  // protected String composeSourceName (String aFileName, int aLineNo) {
  // return "(" + aFileName + anAST.getLineNo() + ")";
  // }
  protected String composeSourceName(String aFileName, int aLineNo) {
    if (aFileName.isEmpty())
      return aFileName;
    return "(" + aFileName + ":" + aLineNo + ")";
  }

  protected String composeMessageKey(String aMessageKey) {
    return aMessageKey + ":";
  }

  protected Object[] composeArgsOld(String aMessageKey, DetailAST anAST, DetailAST aTreeAST,
          int aLineNo, Object... anExplanations) {
    String aLongFileName = getLongFileName(aTreeAST);

    String aSourceName = shortFileName(aLongFileName);
    Object[] anArgs = new String[anExplanations.length + 3];

    // Object[] anArgs = new String[anExplanations.length + 2];
    anArgs[0] = composeMessageKey(aMessageKey);
    if (aSourceName.isEmpty()) {
      anArgs[1] = aSourceName;
      anArgs[2] = aSourceName;

    } else {
      anArgs[1] = composeSourceName(aSourceName, aLineNo);

      // String aShortTypeName = shortTypeName;
      String aShortTypeName = shortTypeName;

      if (aShortTypeName == null) {
        aShortTypeName = getOutermostOrEnclosingShortTypeName(anAST);

      }
      anArgs[2] = aShortTypeName;

      if (anArgs[2] == null || anArgs[2].toString().isEmpty()) {
        System.err.println("Empty name");
      }

    }
    for (int i = 3; i < anArgs.length; i++) {

      anArgs[i] = anExplanations[i - 3].toString();

    }
    return anArgs;
  }

  protected Object[] composeArgs(String aMessageKey, DetailAST anAST, DetailAST aTreeAST,
          int aLineNo, Object... anExplanations) {
    return anExplanations;
    // String aLongFileName = getLongFileName(aTreeAST);
    //
    // String aSourceName = shortFileName(aLongFileName);
    // Object[] anArgs = new String[anExplanations.length + 3];
    //
    //
    // // Object[] anArgs = new String[anExplanations.length + 2];
    // anArgs[0] = composeMessageKey(aMessageKey);
    // if (aSourceName.isEmpty()){
    // anArgs[1] = aSourceName;
    // anArgs[2] = aSourceName;
    //
    // } else {
    // anArgs[1] = composeSourceName(aSourceName, aLineNo);
    //
    //// String aShortTypeName = shortTypeName;
    // String aShortTypeName = shortTypeName;
    //
    // if (aShortTypeName == null) {
    // aShortTypeName = getOutermostOrEnclosingShortTypeName(anAST);
    //
    // }
    // anArgs[2] = aShortTypeName;
    //
    // if (anArgs[2] == null || anArgs[2].toString().isEmpty()) {
    // System.err.println("Empty name");
    // }
    //
    // }
    // for (int i = 0; i < anArgs.length; i++) {
    //
    //
    // anArgs[i] = anExplanations[i].toString();
    //
    // }
    // return anArgs;
  }

  protected void log(String aMessageKey, DetailAST ast, DetailAST aTreeAST,
          Object... anExplanations) {
    if (ast == null) {
      System.err.println("Null ast:" + currentFile);
      return;
    }
    // Object[] anArgs = composeArgs(aMessageKey, ast, aTreeAST, ast.getLineNo(),
    // anExplanations);
    Object[] anArgs = composeArgs(aMessageKey, ast, aTreeAST, ast.getLineNo(), anExplanations);

    if (aTreeAST == currentTree) { // standard form?
      extendibleLog(ast.getLineNo(),

              aMessageKey, anArgs

      );
      // } else {
      // log(ast.getLineNo(),
      // // msgKey(),
      // aMessageKey,
      // aSourceName + ":" + ast.getLineNo());
      // }
    } else { // standard form?
      extendibleLog(0,

              aMessageKey, anArgs

      );
      // } else {
      // log(0,
      // // msgKey(),
      // aMessageKey,
      // aSourceName + ":"
      // + ast.getLineNo());
      // }
    }
  }

  // protected boolean doCheck(STType anSTType) {
  // return true;
  // }

  protected void log(String aMessageKey, FullIdent ast, DetailAST aTreeAST,
          Object... anExplanations) {

    Object[] anArgs = composeArgs(aMessageKey, aTreeAST, aTreeAST, ast.getLineNo(), anExplanations);

    if (aTreeAST == currentTree) { // standard form?
      extendibleLog(ast.getLineNo(),

              aMessageKey, anArgs

      );

    } else {
      extendibleLog(0,

              aMessageKey, anArgs

      );

    }
  }

  // protected void log(String aMessageKey, FullIdent ast, DetailAST aTreeAST,
  // Object ... anExplanations) {
  // String aLongFileName = aTreeAST ==
  // currentTree?getFileContents().getFilename():
  // astToFileContents.get(aTreeAST).getFilename();
  // String aSourceName =
  // shortFileName(aLongFileName);
  // // String aSourceName =
  // // shortFileName(astToFileContents.get(aTreeAST).getFilename());
  // if (aTreeAST == currentTree) {
  // // if (anExplanation != null) {
  // log(ast.getLineNo(),
  // ast.getColumnNo(),
  // // msgKey(),
  // aMessageKey,
  // aSourceName + ":" + ast.getLineNo(),
  // anExplanations
  // );
  // // } else {
  // // log(ast.getLineNo(),
  // //// msgKey(),
  // // aMessageKey,
  // // aSourceName + ":" + ast.getLineNo());
  // // }
  // } else {
  // // if (anExplanation != null) {
  // log(0,
  // aSourceName + ":"
  // + ast.getLineNo(),
  // // msgKey(),
  // aMessageKey,
  // anExplanations
  // );
  // // } else {
  // // log(0,
  // //// msgKey(),
  // // aMessageKey,
  // // aSourceName + ":"
  // // + ast.getLineNo());
  // // }
  // }
  // }
  protected void visitInstantiation(DetailAST ast) {
    final FullIdent anIdentifierType = FullIdent.createFullIdentBelow(ast);
    String anInstantiatedTypeName = anIdentifierType.getText();
    // String aPossibleLongName = importShortToLongName.get(anInstantiatedTypeName);
    // if (aPossibleLongName != null) {
    // anInstantiatedTypeName = aPossibleLongName;
    // }

    // STNameable anInstantiatedNameable = new AnSTNameable(ast,
    // anInstantiatedTypeName);
    STNameable anInstantiatedNameable = new AnSTNameable(ast,
            toLongTypeName(anInstantiatedTypeName));
    if (currentMethodName == null)
      typesInstantiated.add(anInstantiatedNameable);
    else
      typesInstantiatedByCurrentMethod.add(anInstantiatedNameable);
  }

//  protected boolean leavingSpuriousInnerClass(DetailAST ast) {
//    if (foundSupuriousInnerClass) {
//      switch (ast.getType()) {
//        case TokenTypes.CLASS_DEF:
//
//        case TokenTypes.ENUM_DEF:
//
//        case TokenTypes.ANNOTATION_DEF:
//
//          foundSupuriousInnerClass = false;
//
//        default:
//          return true;
//      }
//    }
//    return false;
//
//  }

  protected void doLeaveToken(DetailAST ast) {
//    if (foundSupuriousInnerClass) {
//      switch (ast.getType()) {
//        case TokenTypes.CLASS_DEF:
//
//        case TokenTypes.ENUM_DEF:
//
//        case TokenTypes.ANNOTATION_DEF:
//
//          foundSupuriousInnerClass = false;
//
//        default:
//          return;
//      }
//    }
    if (leavingSpuriousInnerClass(ast)) {
      return;
    }
//    if (methodOrConstructorNesting > 0) {
//      switch (ast.getType()) {
//        case TokenTypes.METHOD_DEF:
//          // return;
//        case TokenTypes.CTOR_DEF:
//          methodOrConstructorNesting--;
//          return;
//        default:
//          return;
//      }
//    }
    if (leavingNestedMethod(ast)) {
      return;
    }
    switch (ast.getType()) {
      case TokenTypes.METHOD_CALL:
        leaveMethodCall(ast);
        break;
      case TokenTypes.METHOD_DEF:
        leaveMethod(ast);
        break;
      case TokenTypes.CTOR_CALL:
        // leaveConstructor(ast);
        break;
      case TokenTypes.CTOR_DEF:
        leaveConstructor(ast);
        break;
      case TokenTypes.INTERFACE_DEF:
        leaveInterface(ast);
        break;
      case TokenTypes.CLASS_DEF:
        // if (foundSupuriousInnerClass) {
        // foundSupuriousInnerClass = false;
        // return;
        // }
        leaveClass(ast);
        break;
      case TokenTypes.ENUM_DEF:
        // if (foundSupuriousInnerClass) {
        // foundSupuriousInnerClass = false;
        // return;
        // }
        leaveEnum(ast);
        break;
      case TokenTypes.ANNOTATION_DEF:
        // if (foundSupuriousInnerClass) {
        // foundSupuriousInnerClass = false;
        // return;
        // }
        leaveAnnotationDef(ast);
        break;
      case TokenTypes.LITERAL_IF:
        leaveLiteralIf(ast);
        return;
      case TokenTypes.LITERAL_ELSE:
        leaveLiteralElse(ast);
        return;
      case TokenTypes.LITERAL_SWITCH:
        leaveLiteralSwitch(ast);
        return;
      case TokenTypes.LITERAL_WHILE:
        leaveLiteralWhile(ast);
        return;
      case TokenTypes.LITERAL_FOR:
        leaveLiteralFor(ast);
        return;
      case TokenTypes.LITERAL_RETURN:
        leaveReturn(ast);
        return;
      case TokenTypes.LITERAL_ASSERT:
        leaveAssert(ast);
        return;
      case TokenTypes.LITERAL_CATCH:
        leaveAssert(ast);
        return;
      case TokenTypes.COLON:
        leaveColon(ast);
        return;
      case TokenTypes.LCURLY:
        leaveLCurly(ast);
        return;
      case TokenTypes.RCURLY:
        leaveRCurly(ast);
        return;
      default:
        // System.err.println(checkAndFileDescription + "Unexpected token");

    }
  }

  private void leaveLiteralElse(DetailAST ast) {
    maybePopAST();

  }

  private void leaveColon(DetailAST ast) {
    // TODO Auto-generated method stub

  }

  private void leaveAssert(DetailAST ast) {
    // TODO Auto-generated method stub

  }

  private void leaveReturn(DetailAST ast) {
    // TODO Auto-generated method stub

  }

  // protected boolean checkMethodNesting(DetailAST ast) {
  // if (methodOrConstructorNesting > 0) {
  // switch (ast.getType()) {
  // case TokenTypes.METHOD_DEF:
  // case TokenTypes.CTOR_DEF:
  // methodOrConstructorNesting++;
  // return true;
  // default: return true;
  // }
  // }
  // return false;
  // }
  // protected boolean checkSpuriosInnerClasses() {
  // return foundSupuriousInnerClass;
  // }
  protected void doVisitToken(DetailAST ast) {
    // if (foundSupuriousInnerClass) {
    // return;
    // }
    if (checkSpuriosInnerClasses()) {
      return;
    }
    if (visitingNestedMethod(ast)) {
      return;
    }

    // if (methodOrConstructorNesting > 0) {
    // switch (ast.getType()) {
    // case TokenTypes.METHOD_DEF:
    // case TokenTypes.CTOR_DEF:
    // methodOrConstructorNesting++;
    // return;
    // default: return;
    // }
    // }
    // System.out.println("Check called:" + MSG_KEY);
    switch (ast.getType()) {
      case TokenTypes.ANNOTATION_FIELD_DEF:
        visitAnnotationFieldDef(ast);
        return;
      case TokenTypes.ANNOTATION_DEF:
        if (foundInnerClassToBeNotVisited()) {
          return;
        }

        visitAnnotationDef(ast);
        return;
      case TokenTypes.PACKAGE_DEF:
        visitPackage(ast);
        return;
      case TokenTypes.CLASS_DEF:
        // if (getFullTypeName() == null // outer class
        // || ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses()) // avoid inner class if
        // we haev visited
        if (foundInnerClassToBeNotVisited()) {
          return;
        }

        visitClass(ast);
        return;
      case TokenTypes.INTERFACE_DEF:
        // if (getFullTypeName() == null // avoid inner class if we have visited
        // // outer class
        // || ProjectSTBuilderHolder.getSTBuilder().getVisitInnerClasses())
        if (foundInnerClassToBeNotVisited()) {
          return;
        }

        visitInterface(ast);
        return;
      case TokenTypes.METHOD_DEF:
        visitMethod(ast);
        return;
      case TokenTypes.CTOR_DEF:
        visitConstructor(ast);
        return;
      case TokenTypes.PARAMETER_DEF:
        visitParamDef(ast);
        return;
      case TokenTypes.TYPE_PARAMETERS:
        visitTypeParameters(ast);
        return;
      case TokenTypes.TYPE_ARGUMENTS:
        visitTypeArguments(ast);
        return;
      case TokenTypes.IMPORT:
        visitImport(ast);
        return;
      case TokenTypes.STATIC_IMPORT:
        visitStaticImport(ast);
        return;
      case TokenTypes.LCURLY:
        visitLCurly(ast);
        return;
      case TokenTypes.RCURLY:
        visitRCurly(ast);
        return;
      case TokenTypes.VARIABLE_DEF:
        visitVariableDef(ast);
        return;
      case TokenTypes.LITERAL_NEW:
        // if (ast.findFirstToken(TokenTypes.ELIST) != null)
        // visitConstructorCall(ast);
        // else if (ast.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null)
        // System.out.println ("array declaration");
        visitNew(ast);
        return;
      case TokenTypes.METHOD_CALL:
        visitMethodCall(ast);
        return;
      case TokenTypes.CTOR_CALL:
        visitConstructorCall(ast);
        return;
      case TokenTypes.IDENT:
        visitIdent(ast);
        return;
      case TokenTypes.ENUM_DEF:
        if (foundInnerClassToBeNotVisited()) {
          return;
        }
        visitEnumDef(ast);
        return;
      case TokenTypes.TYPE:
        visitTypeUse(ast);
        return;
      case TokenTypes.LITERAL_IF:
        visitLiteralIf(ast);
        return;
      case TokenTypes.LITERAL_ELSE:
        visitLiteralElse(ast);
        return;
      case TokenTypes.LITERAL_SWITCH:
        visitLiteralSwitch(ast);
        return;
      case TokenTypes.LITERAL_WHILE:
        visitLiteralWhile(ast);
        return;
      case TokenTypes.LITERAL_FOR:
        visitLiteralFor(ast);
        return;
      case TokenTypes.LITERAL_RETURN:
        visitReturn(ast);
        return;
      case TokenTypes.LITERAL_ASSERT:
        visitAssert(ast);
        return;
      case TokenTypes.LITERAL_CATCH:
        visitLiteralCatch(ast);
        return;
      case TokenTypes.COLON:
        visitColon(ast);
        return;

      default:
        System.err.println(checkAndFileDescription + "Unexpected token");
    }

  }

  private void visitLiteralElse(DetailAST anAST) {
    maybePushAST(anAST);

  }

  private void visitColon(DetailAST ast) {
    numberOfTernaryIfsInCurrentMethod++;

  }

  private void visitAssert(DetailAST ast) {
    assertsInCurrentMethod.add(ast);
  }

  private void visitLiteralSwitch(DetailAST ast) {
    maybePushAST(ast);

  }

  private void leaveLiteralSwitch(DetailAST ast) {
    maybePopAST();

  }

  protected void visitLiteralIf(DetailAST anAST) {
    maybePushAST(anAST);
  }

  protected void leaveLiteralIf(DetailAST anAST) {
    maybePopAST();

  }

  protected void visitLiteralWhile(DetailAST anAST) {
    maybePushAST(anAST);

  }

  protected void leaveLiteralWhile(DetailAST anAST) {
    maybePopAST();

  }

  protected void visitLiteralFor(DetailAST anAST) {
    maybePushAST(anAST);

  }

  protected void visitLiteralCatch(DetailAST anAST) {
    maybePushAST(anAST);

  }

  protected void leaveLiteralFor(DetailAST anAST) {
    maybePopAST();
  }

  protected void leaveLiteralCatch(DetailAST anAST) {
    maybePopAST();
  }

  protected Map<String, String[]> typeToSpecifications = new HashMap<>();

  protected void registerSpecifications(Map<String, String[]> aTypeToSpecifications, String aType,
          String[] aSpecification) {
    aTypeToSpecifications.put(aType, aSpecification);
  }

  protected void registerSpecifications(String aType, String[] aSpecification) {
    registerSpecifications(typeToSpecifications, aType, aSpecification);
  }

  public void setExpectedSpecificationOfType(Map<String, String[]> aTypeToSpecifications,
          String aPattern) {
    String[] extractTypeAndSpecification = aPattern.split(TYPE_SEPARATOR);
    String aType = extractTypeAndSpecification[0].trim();
    String[] aSpecifications = extractTypeAndSpecification[1].split(SET_MEMBER_SEPARATOR);
    // typeToSpecification.put(aType, aSpecification);
    registerSpecifications(aTypeToSpecifications, aType, aSpecifications);
  }

  public void setExpectedTypesAndSpecifications(Map<String, String[]> aTypeToSpecifications,
          String[] aPatterns) {
    for (String aPattern : aPatterns) {
      setExpectedSpecificationOfType(aTypeToSpecifications, aPattern);
    }
  }

  public void setExpectedTypesAndSpecifications(String[] aPatterns) {
    setExpectedTypesAndSpecifications(typeToSpecifications, aPatterns);
  }

  // public void setExpectedTypesAndSpecifications(Map<String, String[]> aTypeToSpecifications,
  // String[] aPatterns) {
  // for (String aPattern : aPatterns) {
  // setExpectedSpecificationOfType(aTypeToSpecifications, aPattern);
  // }
  // }

  public static boolean matchesRegexes(String aText, Collection<String> aRegexCollection) {
    for (String aRegex : aRegexCollection) {
      if (aText.matches(aRegex))
        return true;
    }
    return false;
  }

  public static boolean isPublic(DetailAST methodOrVariableDef) {
    return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PUBLIC);

  }

  public static boolean isPublic(Field aField) {
    return Modifier.isPublic(aField.getModifiers());

  }

  public static boolean isProtected(Field aField) {
    return Modifier.isPublic(aField.getModifiers());

  }

  public static boolean isPrivate(Field aField) {
    return Modifier.isPrivate(aField.getModifiers());

  }

  public static Integer getAccessToken(Field aField) {
    int aModifiers = aField.getModifiers();
    if (Modifier.isPublic(aModifiers)) {
      return TokenTypes.LITERAL_PUBLIC;
    }
    if (Modifier.isProtected(aModifiers)) {
      return TokenTypes.LITERAL_PROTECTED;
    }
    if (Modifier.isPrivate(aModifiers)) {
      return TokenTypes.LITERAL_PRIVATE;
    }
    return null;
  }

  public static Integer getAccessToken(Method aMethod) {
    int aModifiers = aMethod.getModifiers();
    if (Modifier.isPublic(aModifiers)) {
      return TokenTypes.LITERAL_PUBLIC;
    }
    if (Modifier.isProtected(aModifiers)) {
      return TokenTypes.LITERAL_PROTECTED;
    }
    if (Modifier.isPrivate(aModifiers)) {
      return TokenTypes.LITERAL_PRIVATE;
    }
    return null;
  }

  public static Integer getAccessToken(Constructor aMethod) {
    int aModifiers = aMethod.getModifiers();
    if (Modifier.isPublic(aModifiers)) {
      return TokenTypes.LITERAL_PUBLIC;
    }
    if (Modifier.isProtected(aModifiers)) {
      return TokenTypes.LITERAL_PROTECTED;
    }
    if (Modifier.isPrivate(aModifiers)) {
      return TokenTypes.LITERAL_PRIVATE;
    }
    return null;
  }

  public static Integer getAccessToken(DetailAST methodOrVariableDef) {
    if (isPublic(methodOrVariableDef)) {
      return TokenTypes.LITERAL_PUBLIC;
    }
    if (isProtected(methodOrVariableDef)) {
      return TokenTypes.LITERAL_PROTECTED;
    }
    if (isPrivate(methodOrVariableDef)) {
      return TokenTypes.LITERAL_PRIVATE;
    }
    return null;
  }

  public static String toAccessString(Integer aToken) {
    if (aToken == null) {
      return "default ";
    }
    switch (aToken) {
      case TokenTypes.LITERAL_PUBLIC:
        return "public ";
      case TokenTypes.LITERAL_PROTECTED:
        return "protected ";
      case TokenTypes.LITERAL_PRIVATE:
        return "private ";
      default:
        return "default";

    }
  }

  public static boolean isProtected(DetailAST methodOrVariableDef) {
    return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PROTECTED);

  }

  public static boolean isPrivate(DetailAST methodOrVariableDef) {
    return methodOrVariableDef.branchContains(TokenTypes.LITERAL_PRIVATE);

  }

  public static boolean isStatic(DetailAST methodOrVariableDef) {
    return methodOrVariableDef.branchContains(TokenTypes.LITERAL_STATIC);

  }

  public static boolean isSynchronized(DetailAST methodOrVariableDef) {
    return methodOrVariableDef.branchContains(TokenTypes.LITERAL_SYNCHRONIZED);

  }

  public static boolean isFinal(DetailAST methodOrVariableDef) {
    return methodOrVariableDef.branchContains(TokenTypes.FINAL);
  }

  public static boolean isStaticAndNotFinal(DetailAST methodOrVariableDef) {
    return isStatic(methodOrVariableDef) && !isFinal(methodOrVariableDef);
  }

  public static boolean isPublicAndInstance(DetailAST methodOrVariableDef) {
    return isPublic(methodOrVariableDef) && !isStatic(methodOrVariableDef);
  }

  protected boolean returnValueOnMatch() {
    return true;
  }

  // check all on success, check one on failure
  // should this really be stopOnExpectedReturnValue?
  protected boolean stopOnFailure() {
    return true;
  }

  public static String toMethodsDeclaredString(STType anSTType) {
    return "NonGetterFunctions:" + getNonGetterFunctions(anSTType) + "NonSetterProcedures:"
            + getNonSetterProcedures(anSTType) + "Getters:" + getGetters(anSTType) + "Setters:"
            + getSetters(anSTType);
  }

  public static String toVariablesDeclaredString(STType anSTType) {
    return "Variables:" + anSTType.getDeclaredSTGlobals().toString();
    // return
    // "Global Variables:" + getNonGetterFunctions (anSTType) +
    // "NonSetterProcedures:" + getNonSetterProcedures (anSTType) +
    // "Getters:" + getGetters(anSTType) +
    // "Setters:" + getSetters(anSTType);
  }

  public static String toAccessModifiersUsedString(STType anSTType) {
    List<AccessModifierUsage> anAccessMidifersUsed = anSTType.getAccessModifiersUsed();
    return "Access Modifiers Used: "
            + (anAccessMidifersUsed == null ? "" : anAccessMidifersUsed.toString());

  }

  public static String toPropertiesDeclaredString(STType anSTType) {
    return "Properties:" + anSTType.getDeclaredPropertyInfos().values().toString();
    // return
    // "Global Variables:" + getNonGetterFunctions (anSTType) +
    // "NonSetterProcedures:" + getNonSetterProcedures (anSTType) +
    // "Getters:" + getGetters(anSTType) +
    // "Setters:" + getSetters(anSTType);
  }

  public static List<STMethod> getNonGetterFunctions(STType anSTType) {
    STMethod[] aMethods = anSTType.getDeclaredMethods();
    List<STMethod> retVal = new ArrayList();
    for (STMethod aMethod : aMethods) {
      if (!aMethod.isProcedure() && !aMethod.isGetter()) {
        retVal.add(aMethod);
      }
    }
    return retVal;
  }

  public static List<STMethod> getNonSetterProcedures(STType anSTType) {
    STMethod[] aMethods = anSTType.getDeclaredMethods();
    List<STMethod> retVal = new ArrayList();
    for (STMethod aMethod : aMethods) {
      if (aMethod.isProcedure() && !aMethod.isSetter()) {
        retVal.add(aMethod);
      }
    }
    return retVal;
  }

  public static List<STMethod> getGetters(STType anSTType) {
    STMethod[] aMethods = anSTType.getDeclaredMethods();
    List<STMethod> retVal = new ArrayList();
    for (STMethod aMethod : aMethods) {
      if (aMethod.isGetter()) {
        retVal.add(aMethod);
      }
    }
    return retVal;
  }

  public static List<STMethod> getSetters(STType anSTType) {
    STMethod[] aMethods = anSTType.getDeclaredMethods();
    List<STMethod> retVal = new ArrayList();
    for (STMethod aMethod : aMethods) {
      if (aMethod.isSetter()) {
        retVal.add(aMethod);
      }
    }
    return retVal;
  }

  public static int toTokenAccessDegree(Integer aTokenAccess) {
    return accessTokenToAccessDegree.get(aTokenAccess);
  }

  public static String toTokenAccessString(Integer aTokenAccess) {
    return accessTokenToAccessString.get(aTokenAccess);
  }

  public static Set<STMethod> getMatchingCalledMethods(STType aCalledType, CallInfo aCallInfo) {
    Set<STMethod> retVal = new HashSet();
    // if (aCallInfo.getCallee().equals("println")) {
    // System.err.println ("found println");
    // }
    // STType aCalledType = aCallInfo.getCalledSTType();

    if (aCalledType == null)
      return null;
    STMethod[] aMethods;
    if (aCallInfo.isConstructor()) {
      aMethods = aCalledType.getDeclaredConstructors();

    } else {
      aMethods = aCalledType.getDeclaredMethods();
    }
    if (aMethods == null) {
      return null;
    }
    for (STMethod anSTMethod : aMethods) {
      if (anSTMethod.getName().equals(aCallInfo.getCallee())
              && anSTMethod.getParameterTypes().length == aCallInfo.getActuals().size()) { // at
                                                                                           // some
                                                                                           // point
                                                                                           // do
        // overload
        // resolution?
        retVal.add(anSTMethod);
        // anSTMethod.addCaller(callingMethod);
        // break;
      }
    }

    return retVal;
  }

  protected static Set<STMethod> emptyMethods = new HashSet();;

  public static Set<STMethod> getMatchingCalledMethodsInSomeSTType(CallInfo aCallInfo) {
    Collection<STType> anSTTypes = SymbolTableFactory.getOrCreateSymbolTable().getAllSTTypes();
    Set<STMethod> retVal = new HashSet();
    for (STType anSTType : anSTTypes) {
      Set<STMethod> aMatch = getMatchingCalledMethods(anSTType, aCallInfo);
      if (aMatch != null && aMatch.size() > 0) {
        retVal.addAll(aMatch);
      }
    }
    return retVal;

  }

  static {
    accessTokenToAccessDegree.put(TokenTypes.LITERAL_PRIVATE, 0);
    accessTokenToAccessDegree.put(DEFAULT_ACCESS_TOKEN, 1);
    accessTokenToAccessDegree.put(null, 1);

    accessTokenToAccessDegree.put(TokenTypes.LITERAL_PROTECTED, 2);
    accessTokenToAccessDegree.put(TokenTypes.LITERAL_PUBLIC, 3);
    accessTokenToAccessString.put(TokenTypes.LITERAL_PRIVATE, "private ");
    accessTokenToAccessString.put(DEFAULT_ACCESS_TOKEN, "default ");
    accessTokenToAccessString.put(null, "default ");

    accessTokenToAccessString.put(TokenTypes.LITERAL_PROTECTED, "protected ");
    accessTokenToAccessString.put(TokenTypes.LITERAL_PUBLIC, "public ");
  }

  public String toNormalizedTypeParameterName(String aShortName) {
    if (typeParameterNames == null) {
      return null;
    }
    int anIndex = typeParameterNames.indexOf(aShortName);
    if (anIndex < 0) {
      return null;
    }
    return NORMALIZED_TYPE_PARAMETER_NAME + anIndex;
  }

  protected String[] toLongTypeNames(String[] aShortNames) {
    if (aShortNames.length == 0) {
      return aShortNames;
    }
    String[] retVal = new String[aShortNames.length];
    for (int i = 0; i < aShortNames.length; i++) {
      String aShortName = aShortNames[i];
      // retVal[i] = toNormalizedTypeParameterName(aShortName);
      // if (retVal[i] == null) {
      retVal[i] = toLongTypeName(aShortName);

      // }

    }
    return retVal;

  }

  protected String toLongTypeName(String aShortOrLongName) {

    String aTypeParameterName = toNormalizedTypeParameterName(aShortOrLongName);
    if (aTypeParameterName != null) {
      return aTypeParameterName;
    }
    // String retVal = aShortName;
    if (aShortOrLongName == null) {
      System.err.println("null aShortTypeName");
      return null;
    }
    int aSubscriptStart = aShortOrLongName.indexOf("[");
    String aSubscriptSuffix = "";
    String aShortOrLongNameWithoutSuffix = aShortOrLongName;
    if (aSubscriptStart >= 0) {
      aShortOrLongNameWithoutSuffix = aShortOrLongName.substring(0, aSubscriptStart);
      aSubscriptSuffix = aShortOrLongName.substring(aSubscriptStart);
    }
    String aNameWithoutSuffix = toLongTypeNameNoArray(aShortOrLongNameWithoutSuffix);
    return aNameWithoutSuffix + aSubscriptSuffix;

  }

  public static boolean isTypeParameter(String aType) {
    return aType.startsWith(NORMALIZED_TYPE_PARAMETER_NAME);
  }

  public static void main(String[] args) {
    System.out.println("Math.PI".matches("Math.(.*)"));
  }
}
