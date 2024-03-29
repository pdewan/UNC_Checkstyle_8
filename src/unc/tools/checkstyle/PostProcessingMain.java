package unc.tools.checkstyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.XMLLogger;
import com.puppycrawl.tools.checkstyle.Main;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean.OutputStreamOptions;

import unc.checks.ClassDefinedCheck;
import unc.checks.ComprehensiveVisitCheck;
import unc.checks.MethodCallCheck;
import unc.checks.MissingMethodCallCheck;
import unc.checks.MissingMethodTextCheck;
import unc.checks.MnemonicNameCheck;
import unc.checks.STBuilderCheck;
import unc.checks.TagBasedCheck;
import unc.checks.UNCCheck;
import unc.parseTree.AnIFStatement;
import unc.symbolTable.AccessModifierUsage;
import unc.symbolTable.AnAbstractSTMethod;
import unc.symbolTable.CallInfo;
import unc.symbolTable.PropertyInfo;
import unc.symbolTable.STMethod;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.STVariable;
import unc.symbolTable.SymbolTable;
import unc.symbolTable.SymbolTableFactory;

public class PostProcessingMain {
  //// static final String SOURCE =
  //// "C:\\Users\\dewan\\Downloads\\twitter-heron";
  //// static final String SOURCE =
  //// "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce";
  // static final String SOURCE =
  //// "D:\\dewan_backup\\Java\\UNCCheckStyle\\src\\test";
  //
  //// static final String SOURCE =
  //// "C:\\Users\\dewan\\Downloads\\twitter-heron\\contrib\\bolts\\kafka\\src\\java\\org\\apache\\heron\\bolts\\kafka\\KafkaBolt.java";
  // static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
  // static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, SOURCE};
  static boolean traceSteps = false;
  

  static boolean printOnlyTaggedClasses = false;
  
  static Set<String> ignoreExternalTypes = new HashSet<>(
      Arrays.asList(
              "java.lang.String", 
              "java.lang.List",  
              "java.lang.Map",
              "java.lang.Throwable"));
  

  static SymbolTable symbolTable;


  static STBuilderCheck sTBuilderCheck;
  static String[] externalPackagePrefixes;
  static String[] externalMethodRegularExpressions;
  static String[] externalClassRegularExpressions;
//  static Collection<STType> sTTypes;
//  static Collection<STType> sTTypes = new ArrayList();
  static List<STType> sTTypes = new ArrayList();

  static final String DEFAULT_GENERATED_CHECKS_FILE_NAME = "generated_checks.xml";
  static final String DUMMY_FILE_NAME = "firstpassresults.text";
  static final String EXTERNALS_FILE_NAME = "externals.csv";

  static PrintStream checksPrintStream, outPrintStream;
  static String[] emptyStrings = {};

  public static void initGlobals() {
    symbolTable = SymbolTableFactory.getSymbolTable();
    sTBuilderCheck = STBuilderCheck.getLatestInstance();
    externalPackagePrefixes = sTBuilderCheck.getExternalPackagePrefixes();
    externalMethodRegularExpressions = sTBuilderCheck.getExternalMethodRegularExpressions();
    externalClassRegularExpressions = sTBuilderCheck.getExternalTypeRegularExpressions();
//    sTTypes = symbolTable.getAllSTTypes();
    sTTypes.clear();
    sTTypes.addAll (symbolTable.getAllSTTypes());

  }

  public static STMethod[] getDeclaredOrAllMethods(STType anSTType) {
    STMethod[] retVal = anSTType.getMethods();
    if (retVal == null) {
      retVal = anSTType.getDeclaredMethods();
    }
    return retVal;
  }

  public static void refreshUnknowns(List<STType> anSTTypes) {
    // create some side effects first
    for (STType anSTType : anSTTypes) {
      if (anSTType.isExternal()) {
        continue; // these methods have no callers
      }
      if (anSTType.getStaticBlocks() != null) {

        anSTType.getStaticBlocks().refreshUnknowns();
      }
      STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
      for (STMethod aMethod : aMethods) {
        aMethod.getLocalMethodsCalled(); // side effect of adding caller
        aMethod.refreshUnknowns();
        // aMethod.getAllMethodsCalled();
      }

    }
  }

  public static void doSecondPass(STType anSTType) {
    STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
    for (STMethod aMethod : aMethods) {
      // if (anSTType.getName().contains("Client") && aMethod.getName().contains("imula")) {
      // System.err.println("found target method");
      // }
      aMethod.getLocalMethodsCalled(); // side effect of adding caller
      aMethod.refreshUnknowns();
      aMethod.getAllMethodsCalled();// side effect of adding caller
      aMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();// side effect of adding caller
      Set<STMethod> aCallingMethods = aMethod.getCallingMethods();

    }

  }

  public static void doSecondPass(Collection<STType> anOriginalSTTypes) {
    if (isTraceSteps())
    System.err.println("Start O(n2) inter- and intra class method calls:"
            + new Date(System.currentTimeMillis()));
    List<STType> anSTTypes = new ArrayList(anOriginalSTTypes);
    // System.err.println("Types:" + anSTTypes);
    for (STType anSTType : anSTTypes) {
      if (anSTType.isExternal()) {
        continue; // these methods have no callers
      }
      anSTType.getAllTypes();
    }

    // create some side effects first
    // try {
    for (STType anSTType : anSTTypes) {
      if (anSTType.isExternal()) {
        continue; // these methods have no callers
      }
      if (anSTType.getStaticBlocks() != null) {

        anSTType.getStaticBlocks().refreshUnknowns();
      }
      STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
      for (STMethod aMethod : aMethods) {
        aMethod.getLocalMethodsCalled(); // side effect of adding caller
        aMethod.refreshUnknowns();
        // aMethod.getAllMethodsCalled();
      }

    }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    for (STType anSTType : anSTTypes) {
      // System.err.println("All methods called:" + anSTType.getName());
      if (anSTType.isExternal()) {
        continue; // these methods have no callers
      }
      STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
      for (STMethod aMethod : aMethods) {
        // aMethod.getLocalMethodsCalled();
        aMethod.getAllMethodsCalled();// side effect of adding caller
      }

    }
    for (STType anSTType : anSTTypes) {
      if (anSTType.isExternal()) {
        continue; // these methods have no callers
      }
      STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
      for (STMethod aMethod : aMethods) {
        // System.err.println("All internal methods called:" + anSTType.getName());

        // aMethod.getLocalMethodsCalled();
        aMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();// side effect of adding caller

      }

    }
    // this seems duplication of previous code
    // for (STType anSTType : anSTTypes) {
    // if (anSTType.isExternal()) {
    // continue; // these methods have no callers
    // }
    // STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
    // for (STMethod aMethod : aMethods) {
    //// aMethod.getLocalMethodsCalled();
    // aMethod.getAllDirectlyOrIndirectlyCalledMethods();// side effect of adding caller
    // }
    //
    // }
    for (STType anSTType : anSTTypes) {
      // if (!anSTType.isExternal()) {
      // continue; // these methods have no callers
      // }
      // System.err.println("All calling methods:" + anSTType.getName());

      STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
      if (aMethods == null) {
        aMethods = anSTType.getDeclaredMethods();
      }
      for (STMethod aMethod : aMethods) {
        // if (anSTType.getName().contains("Client") &&
        // aMethod.getName().contains("createSimulation")) {
        // System.err.println("found target method:" + aMethod + " in class " + anSTType);
        // }
        // aMethod.getLocalMethodsCalled();
        Set<STMethod> aCallingMethods = aMethod.getCallingMethods();
        // if (aCallingMethods != null) {
        // System.out.println (anSTType + ":" + aMethod + ":" + aMethod.getCallingMethods());
        // }
      }

    }
    // for (STType anSTType : anSTTypes) {
    // generateCheckData(anSTType);
    // // List<STNameable> anInterfaces = anSTType.getAllInterfaces();
    // // if (anInterfaces == null) {
    // // anInterfaces = Arrays.asList(anSTType.getDeclaredInterfaces());
    // // }
    //
    // }
    if (isTraceSteps())
    System.err.println("End O(n2) inter- and intra class method calls:"
            + new Date(System.currentTimeMillis()));

  }

  public static void generateChecks(Collection<STType> anSTTypes) {
    Set<String> aTags = new HashSet(); // this does not seem to be used
    Set<String> anAndedTagsSet = new HashSet();
    for (STType anSTType : anSTTypes) {
//      if (anSTType.getName().contains("lient")) {
//        System.out.println("found type");
//      }
      STNameable[] anExplicitTags = anSTType.getTags();
      STNameable[] aConfiguredTags = anSTType.getConfiguredTags();
      String anAndedTags = toTaggedType(anSTType);
      if (anAndedTags != null) {
//        System.err.println ("An anded string:" + anAndedTags);
        anAndedTagsSet.add(anAndedTags);
      }
      // what is the point of the two loops as tags are no longer used
      for (STNameable aNameable : anExplicitTags) {
        aTags.add(TagBasedCheck.TAG_CHAR + aNameable.getName());
      }
      for (STNameable aNameable : aConfiguredTags) {
        aTags.add(TagBasedCheck.TAG_CHAR + aNameable.getName());
      }
    }

//    printSingleProperty("expectedTypes", aTags.toArray(emptyStrings));
//    printModuleSingleProperty(aModule, aSeverity, aScopingType, aProperty, aPropertyValues);("expectedTypes", aTags.toArray(emptyStrings));
    List<String> anAndedTagsList = new ArrayList(anAndedTagsSet);
    Collections.sort(anAndedTagsList);
    printModuleSingleProperty("ClassDefined", "info", null,
            "expectedTypes", 
//            aTags.toArray(emptyStrings)
//            anAndedTagsSet.toArray(emptyStrings)
            anAndedTagsList.toArray(emptyStrings)

            );
    for (STType anSTType : anSTTypes) {
      generateCheckData(anSTType);

    }
  }

  public static void outputExternalReferences(Collection<STType> anSTTypes) {
    // File aFile = new File(EXTERNALS_FILE_NAME);
    String aProject = ProjectDirectoryHolder.currentProject;
    File aFile = new File(aProject + "/" + EXTERNALS_FILE_NAME);

    try {
      PrintStream anExternalsPrintStream = new PrintStream(aFile);

      for (STType anSTType : anSTTypes) {
        if (!anSTType.isExternal()) {
          continue;
        }
        if (anSTType.getDeclaredMethods().length == 0) {
          continue;
        }
        for (STMethod aMethod : anSTType.getDeclaredMethods()) {
          Set<STMethod> aCallingMethods = aMethod.getCallingMethods();
          if (aCallingMethods == null || aCallingMethods.isEmpty()) {
            continue;
          }
          for (STMethod aCallingMethod : aCallingMethods) {
            anExternalsPrintStream.println(anSTType.getName() + "," + aMethod.getName() + ","
                    + aCallingMethod.getDeclaringClass() + "," + aCallingMethod.getName());
          }
        }
      }
      anExternalsPrintStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static String toShortString(STType anSTType) {
    return TagBasedCheck.isExplicitlyTagged(anSTType) ? toTaggedType(anSTType)
            : ComprehensiveVisitCheck.toShortTypeName(anSTType.getName());

  }

  static String toLongString(STType anSTType) {
    return TagBasedCheck.isExplicitlyTagged(anSTType) ? toTaggedType(anSTType) : anSTType.getName();

  }

  static List<String> externalorTaggedTypes = new ArrayList();

  public static void outputExternalOrTaggedCallInfos(Collection<STType> anSTTypes) {
    // File aFile = new File(EXTERNALS_FILE_NAME);
    String aProject = ProjectDirectoryHolder.currentProject;
    String aProjectDirectoryName = (new File(aProject)).getName();
    File aFile = new File(aProject + "/" + EXTERNALS_FILE_NAME);
    // File aFile = new File(aProjectDirectoryName+ "_" + EXTERNALS_FILE_NAME);

    try {
      PrintStream anExternalsPrintStream = new PrintStream(aFile);

      anExternalsPrintStream.println("Caller Type" + "," + "Caller Type Words" + "," + "Caller Tag"
              + "," + "Caller Tag Words" + "," + "Caller Method" + "," + "Caller Method Words" + ","
              + "Caller Super Types" + "," + "Calling Super Types Words" + "," + "Called  Type"
              + "," + "Called Type Words" + "," + "Called  Tagged Type" + ","
              + "Called Tagged Type Words" + "," + "Called Method" + "," + "Called Method Words");
      boolean externalTypesInitialized = false;
      externalorTaggedTypes.clear();

      for (STType anSTType : anSTTypes) {
        externalTypesInitialized = false;
//        if (anSTType.getName().equals("mapreduce.ATokenCountingModel")) {
//          System.err.println("found mapreduce.ATokenCountingModel");
//        }

        for (STMethod aMethod : anSTType.getDeclaredMethods()) {
          CallInfo[] aCallInfos = aMethod.getCallInfoOfMethodsCalled();
          if (aCallInfos == null) {
            continue;
          }
          for (CallInfo aCallInfo : aCallInfos) {
            STType aCalledSTType = aCallInfo.getCalledSTType();
            String aCalledTypeTaggedString = null;
            String aShortCalledTypeTaggedString = null;

            if (aCalledSTType != null) {
              // if (!aCalledSTType.isExternal()) {
              if (!isExternalOrTaggedType(aCalledSTType)) {
                continue;
              }
              aCalledTypeTaggedString = toLongString(aCalledSTType);
              aShortCalledTypeTaggedString = toShortString(aCalledSTType);
            } else {
              aCalledTypeTaggedString = aCallInfo.getCalledType();
              if (aCalledTypeTaggedString == null) {
                continue;
              }
              aShortCalledTypeTaggedString = ComprehensiveVisitCheck
                      .toShortTypeName(aCalledTypeTaggedString);
            }
            String aCalledType = aCallInfo.getCalledType();
            String aShortCalldType = ComprehensiveVisitCheck.toShortTypeName(aCalledType);
            // String aCalledType = aCallInfo.getCalledType();
            // if (aCalledType == null) {
            // continue;
            // }
            String aCalledMethod = aCallInfo.getCallee();
            if (aCalledMethod == null) {
              continue;
            }
            if (!externalTypesInitialized) {
              externalTypesInitialized = true;

              // if (anSTType.getName().equals("mapreduce.ATokenCountingModel")) {
              // System.err.println("found mapreduce.ATokenCountingModel");
              // }
              List<STNameable> anAllTypes = anSTType.getAllTypes();
              for (STNameable aSuperType : anAllTypes) {
                if (aSuperType instanceof STType) {
                  STType aSuperSTType = (STType) aSuperType;
                  // if (aSuperSTType.isExternal() &&
                  // SymbolTableFactory.getSymbolTable().getObjectType() != aSuperSTType) {
                  if (aSuperType != anSTType
                          && SymbolTableFactory.getSymbolTable().getAndMaybePutObjectType() != aSuperSTType
                          && isExternalOrTaggedType(aSuperSTType)) {

                    externalorTaggedTypes.add(toShortString(aSuperSTType));
                  }
                }

              }
            }
            String aTaggedCallerTypeString = toShortString(anSTType);
            // if (!externalorTaggedTypes.isEmpty()) {
            // System.err.println("external or tagged type");
            // }
            anExternalsPrintStream.println(anSTType.getName() + ","
                    + toString(MnemonicNameCheck.getDictonaryComponents(
                            ComprehensiveVisitCheck.toShortTypeName(anSTType.getName())))
                    + "," + aTaggedCallerTypeString + ","
                    + toString(MnemonicNameCheck.getDictonaryComponents(aTaggedCallerTypeString))
                    + "," + aMethod.getName() + ","
                    + toString(MnemonicNameCheck.getDictonaryComponents(aMethod.getName())) + ","
                    + toString(externalorTaggedTypes) + ","
                    + toStringDictionaryComponents(externalorTaggedTypes) + "," + aCalledType + ","
                    + toString(MnemonicNameCheck.getDictonaryComponents(aShortCalldType)) + ","
                    + aCalledTypeTaggedString + ","
                    + toString(
                            MnemonicNameCheck.getDictonaryComponents(aShortCalledTypeTaggedString))
                    + "," + aCalledMethod + ","
                    + toString(MnemonicNameCheck.getDictonaryComponents(aCalledMethod)));
          }
        }
      }
      anExternalsPrintStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static StringBuilder stringBuilder = new StringBuilder();
  static List list = new ArrayList();

  public static String toStringDictionaryComponents(List aList) {
    list.clear();
    for (Object anElement : aList) {
      String anElementString = anElement.toString();
      List anElementComponents = MnemonicNameCheck.getDictonaryComponents(anElementString);
      list.addAll(anElementComponents);

    }
    return toString(list);

  }

  public static String toString(List aList) {
    stringBuilder.setLength(0);
    for (int anIndex = 0; anIndex < aList.size(); anIndex++) {
      if (anIndex > 0) {
        stringBuilder.append(":");
      }
      Object anElement = aList.get(anIndex);
      String anElementString = anElement instanceof List ? toString((List) anElement)
              : anElement.toString();
      stringBuilder.append(anElementString);

    }
    return stringBuilder.length() == 0 ? "none" : stringBuilder.toString();
  }

  public static void generateCheckData(STType anSTType) {
    if (anSTType.isInterface()) {
      return;
    }
//    if (anSTType.getName().contains ("MapperFactory")) {
//      System.err.println("Found check data class");
//    }
    printTypeInterfaces(anSTType);
    printTypeSuperTypes(anSTType);
    printTypeIsGeneric(anSTType);
    processTypeProperties(anSTType);
    processTypeEditableProperties(anSTType);
    // processTypeSuperTypes(anSTType);
    // processTypeCallInfos(anSTType);
    processDeclaredMethods(anSTType);
    processMethodsCalled(anSTType);
    processUnknownVariablesAccessed(anSTType);
    processClassInstantiations(anSTType);
//    processAccessModifiersUsed(anSTType);
    // processReferencesPerVariable(anSTType);

  }

  // <module name="IllegalPropertyNotification">
  // <property name="severity" value="warning" />
  // <property name="excludeProperties" value="this" />
  // </module>
  public static void printProperty(String aProperty, String aValue) {
    if (aValue == null) return;
    String aFiller = aValue.contains(",") ? "\n\t\t" : "";
    checksPrintStream.println(
            "\t\t<property name=\"" + aProperty + "\" value=\"" + aValue + aFiller + "\"/>");

  }

  // <module name="ExpectedGetters">
  // <property name="severity" value="warning" />
  // <property name="includeTypeTags" value="KeyValueClass" />
  // <property name="expectedProperties"
  // value="
  // Key:.*,
  // Value:.*
  // "
  // />
  // </module>
  public static void printExectedPairs(String aCheckName, String aPropertyName, String aScopingType,
          String[] aPairs) {
    if (aPairs.length % 2 != 0) {
      if (isTraceSteps())
      System.err.println(">odd array");
      return;
    }
    StringBuilder aPropertiesAndTypesString = new StringBuilder();

    for (int i = 0; i < aPairs.length; i = i + 2) {
      aPropertiesAndTypesString.append("\n\t\t\t" + aPairs[i] + ":" + aPairs[i + 1] + ",");
    }
    String[] aPropertyNameAndValue = { aPropertyName, aPropertiesAndTypesString.toString() };

    printWarningModuleAndProperties(aCheckName, aScopingType, aPropertyNameAndValue);
    printInfoModuleAndProperties(aCheckName, aScopingType, aPropertyNameAndValue);

  }

  public static String toChecksList(String[] aList) {

    StringBuilder aPropertiesString = new StringBuilder();

    for (int i = 0; i < aList.length; i++) {
      aPropertiesString.append("\n\t\t\t" + aList[i] + ",");
    }
    return aPropertiesString.toString();

  }

  // public static void printExpectedInterfaces(String aScopingType, String[] aPropertyNameAndType)
  // {
  // printExectedPairs("ExpectedInterfaces", "expectedInterfaces", aScopingType,
  // aPropertyNameAndType);
  // }
  public static void printExpectedGetters(String aScopingType, String[] aPropertyNameAndType) {
    printExectedPairs("ExpectedGetters", "expectedProperties", aScopingType, aPropertyNameAndType);
  }

  // <module name="ExpectedSetters">
  // <property name="severity" value="warning" />
  // <property name="includeTypeTags" value="ModelClass" />
  // <property name="expectedProperties"
  // value="
  // InputString:String,
  // "
  // />
  // </module>
  public static void printExpectedSetters(String aScopingType, String[] aPropertyNameAndType) {
    printExectedPairs("ExpectedSetters", "expectedProperties", aScopingType, aPropertyNameAndType);
  }

  // <module name="ExpectedSignatures">
  // <property name="severity" value="warning" />
  // <property name="includeTypeTags" value="ModelClass" />
  // <property name="expectedSignatures"
  // value="
  // addPropertyChangeListener:PropertyChangeListener->void,
  // "
  // />
  // </module>
  public static void printExpectedSignatures(String aScopingType, String[] aPropertyNameAndType) {
    printExectedPairs("ExpectedSignatures", "expectedSignatures", aScopingType,
            aPropertyNameAndType);
  }

  // <module name="MissingMethodCall">
  // <property name="severity" value="warning" />
  // <property name="includeTypeTags" value="ControllerClass" />
  // <property name="expectedCalls"
  // value="
  // .*!setInputString:String->void,
  // "
  // />
  // </module>
  public static void printExpectedCalls(String aScopingType, String[] aPropertyNameAndType) {
    printExectedPairs("MissingMethodCall", "expectedCalls", aScopingType, aPropertyNameAndType);
  }

  public static void printWarningModuleAndProperties(String aModule, String aScopingType,
          String[] aPropertyNamesAndValues) {
    printModuleAndProperties(aModule, "warning", aScopingType, aPropertyNamesAndValues);
  }

  public static void printInfoModuleAndProperties(String aModule, String aScopingType,
          String[] aPropertyNamesAndValues) {
    printModuleAndProperties(aModule, "info", aScopingType, aPropertyNamesAndValues);
  }

  public static void printModuleAndProperties(String aModule, String aSeverity, String aScopingType,
          String[] aPropertyNamesAndValues) {
    if (aPropertyNamesAndValues.length % 2 != 0) {
      if (isTraceSteps())
      System.err.println("mismatched property name and values ");
    }
    // checksPrintStream.println ("\t<module name=\"" + aModule + "\">");
    // printModuleStart(aModule);
    // printProperty("severity", aSeverity);
    // printProperty("includeTypeTags", aScopingType);
    printModuleStart(aModule, aSeverity, aScopingType);

    for (int i = 0; i < aPropertyNamesAndValues.length; i = i + 2) {
      printProperty(aPropertyNamesAndValues[i], aPropertyNamesAndValues[i + 1]);
    }
    // checksPrintStream.println ("</module>");
    printModuleEnd();
  }

  public static void printModuleStart(String aModule) {
    checksPrintStream.println("\t<module name=\"" + aModule + "\">");

  }

  public static void printModuleStart(String aModule, String aSeverity, String aScopingType) {
    printModuleStart(aModule);
    printProperty("severity", aSeverity);
    printProperty("includeTypeTags", aScopingType);

  }

  public static void printModuleEnd() {
    checksPrintStream.println("\t</module>");

  }

  public static void printModuleSingleProperty(String aModule, String aSeverity,
          String aScopingType, String aProperty, String[] aPropertyValues) {

    printModuleStart(aModule, aSeverity, aScopingType);
    printSingleProperty(aProperty, aPropertyValues);
    // if (aPropertyValues != null && aPropertyValues.length > 0) {
    // String aPropertiesString = toChecksList(aPropertyValues);
    // checksPrintStream.println ("\t\t<property name=\"" + aProperty + "\" value=\"" +
    // aPropertiesString + "\"/>");
    // }
    printModuleEnd();
  }

  public static void printSingleProperty(String aProperty, String[] aPropertyValues) {

    // checksPrintStream.println ("\t<module name=\"" + aModule + "\">");
    // printModuleStart(aModule, aSeverity, aScopingType);
    if (aPropertyValues != null && aPropertyValues.length > 0) {
      String aPropertiesString = toChecksList(aPropertyValues);
      printProperty(aProperty, aPropertiesString);
      // checksPrintStream.println ("\t\t<property name=\"" + aProperty + "\" value=\"" +
      // aPropertiesString + "\"/>");
    }
    // printModuleEnd();
  }

  public static boolean isExternalType(String aFullName) {
    // return !TagBasedCheck.isProjectImport(aFullName) || TagBasedCheck.isExternalType(aFullName);
    return TagBasedCheck.isExternalType(aFullName);

  }

  public static void printImplementsExternal(STType anSTType, String anInterfaceFullName) {
    System.out.println(
            "printImplementsExternal:" + anSTType.getName() + "implements:" + anInterfaceFullName);
  }

  public static void printImplementsTagged(STType anSTType, STType anInterfaceType) {

    Set<String> aMissingTags = TagBasedCheck.tagsNotContainedIn(anSTType, anInterfaceType);
    if (aMissingTags.isEmpty()) {
      return;
    }
    System.out.println(
            "printImplementsTagged:" + anSTType.getName() + "," + anInterfaceType.getName());

  }

  public static void processTypeSuperTypes(STType anSTType) {
    if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
      return;
    }
    List<STNameable> aSuperClasses = anSTType.getAllSuperTypes();
    if (aSuperClasses == null) {
      STNameable aSuperClass = anSTType.getSuperClass();
      if (aSuperClass == null) {
        return;
      }
      aSuperClasses = new ArrayList();
      aSuperClasses.add(aSuperClass);
    }

    for (STNameable aClass : aSuperClasses) {
      String aFullName = aClass.getName();
      if (isExternalType(aFullName)) {
        nalSuperClass(anSTType, aFullName);
        return;
      }
      STType aClassSTType = symbolTable.getSTClassByFullName(aClass.getName());
      if (aClassSTType == null) {
        // continue;
        aClassSTType = symbolTable.getSTClassByShortName(aClass.getName());
      }
      if (TagBasedCheck.isExplicitlyTagged(aClassSTType)) {
        printTaggedSuperclass(anSTType, aClassSTType);
      }
    }

  }

  private static void printTaggedSuperclass(STType anSTType, STType aSuperType) {
    Set<String> aMissingTags = TagBasedCheck.tagsNotContainedIn(anSTType, aSuperType);
    if (aMissingTags.isEmpty()) {
      return;
    }
    System.out.println("printTaggedSuperclass:" + anSTType.getName() + "," + aSuperType.getName());
  }

  private static void nalSuperClass(STType anSTType, String aFullName) {
    System.out.println("printTaggedSuperclass:" + anSTType.getName() + "," + aFullName);

  }

  public static void printTypeInterfaces(STType anSTType) {
    if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
      return;
    }
    String aTypeOutputName = toOutputType(anSTType);

    List<STNameable> anInterfaces = anSTType.getAllInterfaces();
    if (anInterfaces == null) {
      anInterfaces = Arrays.asList(anSTType.getDeclaredInterfaces());
    }
    Collections.sort(anInterfaces);
    List<String> aRequiredInterfaces = new ArrayList();
    for (STNameable anInterface : anInterfaces) {
      String aFullName = anInterface.getName();
      // String anOutputName = toOutputType(aFullName);
      // if (anOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSON ||
      // anOutputName.equals(aTypeOutputName)) {
      //
      // continue;
      // }
      if (!TagBasedCheck.isExternalType(aFullName)) {
        continue;
      }
      // aRequiredInterfaces.add(anOutputName);
      aRequiredInterfaces.add(aFullName);

      // if (isExternalType(aFullName) && TagBasedCheck.isExplicitlyTagged(anSTType)) {
      // printImplementsExternal(anSTType, aFullName);
      // return;
      // }
      // STType anInterfaceSTType = symbolTable.getSTClassByFullName(anInterface.getName());
      // if (anInterfaceSTType == null) {
      // // continue;
      // anInterfaceSTType = symbolTable.getSTClassByShortName(anInterface.getName());
      // }
      // if (TagBasedCheck.isExplicitlyTagged(anInterfaceSTType) &&
      // TagBasedCheck.isExplicitlyTagged(anSTType)) {
      // printImplementsTagged(anSTType, anInterfaceSTType);
      // }
    }
    if (aRequiredInterfaces.size() == 0) {
      return;
    }
    printModuleSingleProperty("ExpectedInterfaces", "warning", aTypeOutputName,
            "expectedInterfaces", aRequiredInterfaces.toArray(stringArray));
    printModuleSingleProperty("ExpectedInterfaces", "info", aTypeOutputName,
            "expectedInterfaces", aRequiredInterfaces.toArray(stringArray));

  }

  public static void printTypeSuperTypes(STType anSTType) {
    // if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
    // return;
    // }
    String aTypeOutputName = toOutputType(anSTType);

    if (aTypeOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
      return;
    }
    List<STNameable> aSuperClasses = anSTType.getAllSuperTypes();
    if (aSuperClasses == null) {
      aSuperClasses = Arrays.asList(anSTType.getSuperClass());
    }
    Collections.sort(aSuperClasses);
    List<String> aRequiredClasses = new ArrayList();
    for (STNameable aSuperClass : aSuperClasses) {
      String aFullName = aSuperClass.getName();
      String anOutputName = toOutputType(aFullName);
      if (anOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION
              || anOutputName.equals(aTypeOutputName) || anOutputName.equals("java.lang.Object")) {

        continue;
      }
      // if (!TagBasedCheck.isExternalType(aFullName)) {
      // continue;
      // }
      aRequiredClasses.add(anOutputName);
      // aRequiredClasses.add(aFullName);

      // if (isExternalType(aFullName) && TagBasedCheck.isExplicitlyTagged(anSTType)) {
      // printImplementsExternal(anSTType, aFullName);
      // return;
      // }
      // STType anInterfaceSTType = symbolTable.getSTClassByFullName(anInterface.getName());
      // if (anInterfaceSTType == null) {
      // // continue;
      // anInterfaceSTType = symbolTable.getSTClassByShortName(anInterface.getName());
      // }
      // if (TagBasedCheck.isExplicitlyTagged(anInterfaceSTType) &&
      // TagBasedCheck.isExplicitlyTagged(anSTType)) {
      // printImplementsTagged(anSTType, anInterfaceSTType);
      // }
    }
    if (aRequiredClasses.size() == 0) {
      return;
    }
    printModuleSingleProperty("ExpectedSuperTypes", "warning", aTypeOutputName,
            "expectedSuperTypes", aRequiredClasses.toArray(stringArray));
    printModuleSingleProperty("ExpectedSuperTypes", "info", aTypeOutputName,
            "expectedSuperTypes", aRequiredClasses.toArray(stringArray));

  }

  public static void printTypeIsGeneric(STType anSTType) {
    // if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
    // return;
    // }
    
//    if (anSTType.getName().contains("ey")) {
//      System.err.println("found generic class");
//    }
    
    String aTypeOutputName = toOutputType(anSTType);
    
    

    if (aTypeOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
      return;
    }
    if (anSTType == null || !anSTType.isGeneric()) {
      return;
    }

    printModuleSingleProperty("ClassIsGeneric", "warning", aTypeOutputName, null, null);
    printModuleSingleProperty("ClassIsGeneric", "info", aTypeOutputName, null, null);


  }

  public static void processUnknownVariablesAccessed(STType aSubjectType, STMethod aRootMethod,
          STMethod aMethod) {
    Map<String, Set<DetailAST>> anUnKnownsAccessed = aMethod.getUnknownAccessed();
    if (anUnKnownsAccessed == null) {
      return;
    }
    Set<String> anUnknownsAccessedSet = anUnKnownsAccessed.keySet();
    List<String> aMethodAndVariables = new ArrayList();
    for (String anUnknown : anUnknownsAccessedSet) {
      if (!anUnknown.contains("."))
        continue;
      if (anUnknown.contains("System."))
        continue;
      String aClassName = TagBasedCheck.fromVariableToTypeName(anUnknown);
      if (TagBasedCheck.isExternalType(aClassName)) {
        String aSimpleMethodSignature = aRootMethod.getSimpleChecksTaggedSignature();
        int aLastDotIndex = anUnknown.lastIndexOf(".");
        String aRegularExpression = TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION
                + anUnknown.substring(aLastDotIndex + 1)
                + TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION;
        aMethodAndVariables.add(aSimpleMethodSignature
                + MissingMethodTextCheck.CALLER_TEXT_SEPARATOR + aRegularExpression);

        // aMethodAndVariables.add (anUnkown);
        // System.out.println ("type:" + aSubjectType.getName() + " method " + aRootMethod.getName()
        // + " global " + anUnkown);

      }
    }
    if (aMethodAndVariables.size() == 0) {
      return;
    }
    String aTypeOutputName = toOutputType(aSubjectType);
    printModuleSingleProperty("MethodAccessesGlobal", "warning", aTypeOutputName,
            "expectedReferences", aMethodAndVariables.toArray(stringArray));

  }

  public static void processUnknownVariablesAccessed(STType aSubjectType, STMethod aRootMethod,
          Set<STMethod> aMethods) {
    if (aMethods == null) {
      return;
    }
    for (STMethod aMethod : aMethods) {
      processUnknownVariablesAccessed(aSubjectType, aRootMethod, aMethod);
    }
  }

  public static void processUnknownVariablesAccessed(STType anSTType) {
    if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
      return;
    }
    STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
    // if (anSTType.getName().contains("Controller")) {
    // System.err.println("Found controller");
    // }
    for (STMethod aMethod : aMethods) {
      if (!aMethod.isPublic()) {
        continue;
      }
      Set<STMethod> aCalledMethods = aMethod.getAllDirectlyOrIndirectlyCalledMethods();
      // Set<String> anUnknownsAccessed = aMethod.getUnknownAccessed().keySet();
      processUnknownVariablesAccessed(anSTType, aMethod, aMethod);
      processUnknownVariablesAccessed(anSTType, aMethod, aCalledMethods);

      // Set<String> anUnknownAssigned = aMethod.getUnknownAssigned().keySet();
      // if (anUnknownsAccessed != null) {
      // for (String anUnknown:anUnknownsAccessed) {
      // if (anUnknown.contains(".")) {
      // System.out.println ("type:" + anSTType.getName() + " method " + aMethod.getName() + "
      // anKnown " + anUnknown);
      // }
      // }
      // }

    }
  }
  public static void processClassInstantiations(STType anSTType) {
    String aTypeOutputName = toOutputType(anSTType);
    if (aTypeOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
      return;
    }
    if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
      return;
    }
    List<STNameable> aTypesInstantiated = anSTType.getTypesInstantiated();
    if (aTypesInstantiated == null || aTypesInstantiated.size() == 0) {
      return;
    }
    List<String> aClassesInstantiated = new ArrayList();
    for (STNameable anInstantiatedType : aTypesInstantiated) {
     
      String anInstantiatedTypeName = anInstantiatedType.getName();
      STType anInstatiatedSTType = SymbolTableFactory.getSymbolTable().getSTClassByFullName(anInstantiatedTypeName);
       if (anInstatiatedSTType == null) {
         anInstatiatedSTType = SymbolTableFactory.getSymbolTable().getSTClassByShortName(anInstantiatedTypeName);
       }
      if (anInstatiatedSTType == null || 
              (!TagBasedCheck.isExplicitlyTagged(anSTType)) && !anSTType.isExternal()) { 
                                                                
        continue;
      }
      aClassesInstantiated.add(anInstantiatedTypeName);
    }
    printModuleSingleProperty("ExpectedClassInstantiations", "warning", aTypeOutputName, "instantiations",
            aClassesInstantiated.toArray(stringArray));

    printModuleSingleProperty("ExpectedClassInstantiations", "info", aTypeOutputName, "instantiations",
            aClassesInstantiated.toArray(stringArray)); 
 
  }
  
  

  protected static String getSubtypeTagged(STType anSTType) {
    if (TagBasedCheck.isExplicitlyTagged(anSTType)) {
      return anSTType.getName();
    }
    List<String> aSubtypes = anSTType.getSubTypes();
    if (aSubtypes == null) {
      return null;
    }
    for (String aSubtype : aSubtypes) {
      if (TagBasedCheck.isExplicitlyTagged(aSubtype)) {
        return aSubtype;
      }
    }
    return null;
  }

  protected static STType getSTSubtypeTagged(STType anSTType) {
    String aSubtypeName = getSubtypeTagged(anSTType);
    if (aSubtypeName != null) {
      return SymbolTableFactory.getSymbolTable().getSTClassByShortName(aSubtypeName);
    }
    return null;
  }

  protected static String getSubtypeExternal(STType anSTType) {
    if (TagBasedCheck.isExternalType(anSTType.getName())) {
      return anSTType.getName();
    }
    List<String> aSubtypes = anSTType.getSubTypes();
    for (String aSubtype : aSubtypes) {
      if (TagBasedCheck.isExternalType(aSubtype)) {
        return aSubtype;
      }
    }
    return null;
  }

  public static void processAccessModifiersUsed(STType anSTType) {
    System.out.println(
            anSTType.getName() + " " + STBuilderCheck.toAccessModifiersUsedString(anSTType));
    STMethod[] aMethods = anSTType.getDeclaredMethods();
    if (aMethods != null) {
      for (STMethod aMethod : aMethods) {
        List<AccessModifierUsage> aUsage = aMethod.getAccessModifiersUsed();
        if (aUsage != null)
          // System.out.println("Access Modifier Usage:" + anSTType.getName() + "," + aUsage);
          writeXMLMessage(anSTType.getFileName(), anSTType.getAST(),
                  anSTType.getName() + "," + aUsage);
      }
    }
    List<STVariable> aVariables = anSTType.getDeclaredSTGlobals();
    if (aVariables != null) {
      for (STVariable aVariable : anSTType.getDeclaredSTGlobals()) {
        // System.out.println("Access Modifier Usage:" + anSTType.getName() + "," +
        // aVariable.getAccessModifiersUsed());
        writeXMLMessage(anSTType.getFileName(), anSTType.getAST(),
                anSTType.getName() + "," + aVariable.getAccessModifiersUsed());

      }

    }
  }

  public static void processReferencesPerVariable(STType anSTType) {
    if (anSTType.isExternal()) {
      return; // these methods have no callers
    }
    writeXMLMessage(anSTType.getFileName(), anSTType.getAST(), anSTType.getName()
            + " Average references per constant:" + anSTType.getNumberOfReferencesPerConstant());
    writeXMLMessage(anSTType.getFileName(), anSTType.getAST(), anSTType.getName()
            + " Average references per variable:" + anSTType.getNumberOfReferencesPerVariable());
    writeXMLMessage(anSTType.getFileName(), anSTType.getAST(), anSTType.getName()
            + " Average assignments per variable:" + anSTType.getNumberOfAssignmentsPerVariable());

    // System.out.println(anSTType.getName() + " Average references per constant:" +
    // anSTType.getNumberOfReferencesPerConstant());
    //
    // System.out.println(anSTType.getName() + " Average references per variable:" +
    // anSTType.getNumberOfReferencesPerVariable());
    //
    // System.out.println(anSTType.getName() + " Average assignments per variable:" +
    // anSTType.getNumberOfAssignmentsPerVariable());

  }

  public static void writeXMLMessage(String aFileName, DetailAST anAST, String aMessage) {
    if (xmlLogger == null) {
      // xmlLogger = new XMLLogger(System.out, true);
      xmlLogger = new XMLLogger(System.out, OutputStreamOptions.CLOSE);

      xmlLogger.auditStarted(null);
    }
    int aLineNumber = 0;
    int aColumnNumber = 0;
    if (anAST != null) {
      aLineNumber = anAST.getLineNo();
      aColumnNumber = anAST.getColumnNo();
    }
    final LocalizedMessage message = new LocalizedMessage(aLineNumber, aColumnNumber,
            "messages.properties", aMessage, null, SeverityLevel.INFO, "module",
            PostProcessingMain.class, null);
    final AuditEvent evstart = new AuditEvent(new Object(), aFileName, null);
    xmlLogger.fileStarted(evstart);
    final AuditEvent ev = new AuditEvent(new Object(), aFileName, message);

    // xmlLogger.fileStarted(ev);
    xmlLogger.addError(ev);
    // xmlLogger.fileFinished(ev);
    xmlLogger.fileFinished(evstart);
    ;

  }

  /*
   * <error line="8" column="9" severity="info"
   * message="test.TestSuperClass.Global Constant superConstant2 Identifier Components= [super, Constant, 2]"
   * source="unc.cs.checks.MnemonicNameCheck"/>
   * 
   */
  static XMLLogger xmlLogger;

  // public static void
  public static void testXMLLogger() {
    // xmlLogger = new XMLLogger(System.out, false);
    xmlLogger = new XMLLogger(System.out, OutputStreamOptions.CLOSE);

    xmlLogger.auditStarted(null);
    String[] args = { "FooClass", "FooTag", "2", "3", "4" };
    final LocalizedMessage message = new LocalizedMessage(1, 1, "messages.properties",
            "classDefined", args, SeverityLevel.INFO, "module", ClassDefinedCheck.class, null);
    final AuditEvent evstart = new AuditEvent(new Object(), "Test.java", null);

    final AuditEvent ev = new AuditEvent(new Object(), "Test.java", message);
    xmlLogger.fileStarted(evstart);

    // xmlLogger.fileStarted(ev);
    xmlLogger.addError(ev);
    // xmlLogger.fileFinished(ev);
    xmlLogger.fileFinished(evstart);
    xmlLogger.fileStarted(evstart);

    // xmlLogger.fileStarted(ev);
    xmlLogger.addError(ev);
    // xmlLogger.fileFinished(ev);
    xmlLogger.fileFinished(evstart);

    xmlLogger.auditFinished(null);
    // verifyXml(getPath("ExpectedXMLLoggerError.xml"), outStream, message.getMessage());
  }

  public static void printCallInfo(STType aCallerSTType, STType aCalledSTType, CallInfo aCallInfo) {
    String aCallingTypeName = aCallerSTType.getName();
    String aCalledTypeName = aCallInfo.getCalledType();
    STMethod aCallingMethod = aCallInfo.getCallingMethod();
    Set<STMethod> aCalledMethods = aCallInfo.getMatchingCalledMethods();
    String aCallee = aCallInfo.getCallee();
    System.out.println(aCallingTypeName + ":" + aCallingMethod + ":" + aCalledTypeName + ":"
            + aCallee + ":" + aCalledSTType + aCalledMethods);
  }

  public static String toTaggedType(String aTypeName) {
    if (aTypeName.equals("void")) 
      return aTypeName;
    STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    if (anSTType == null) {
      return aTypeName;
      // if (isExternalType(aTypeName) || (aTypeName.equals("void"))) {
      // return aTypeName;
      // } else {
      // return ".*";
      // }
    }
    String aRetVal = toTaggedType(anSTType);
    if (aRetVal == null) {
      return aTypeName;
      // if (isExternalType(aTypeName)) {
      // return aTypeName;
      // } else {
      // return ".*";
      // }
    }
    return aRetVal;
  }

  public static String toTaggedType(STType anSTType) {
    List<String> aTags = TagBasedCheck.getNonComputedTagsList(anSTType);
    if (aTags.size() == 0) {
      return null;
    }
    StringBuilder aTagsString = new StringBuilder();
    for (int i = 0; i < aTags.size(); i++) {
      if (i > 0) {
        aTagsString.append(TagBasedCheck.AND_SYMBOL);
      }
      aTagsString.append(TagBasedCheck.TAG_CHAR + aTags.get(i));
    }
    return aTagsString.toString();
    // if (aTags.size() == 1)
    // return aTags.get(0);
    //
    // return TagBasedCheck.MATCH_SOMETHING_REGULAR_EXPERSSON;
  }

  public static String toOutputType(String aTypeName) {
    if (TagBasedCheck.isExternalType(aTypeName)
            || ComprehensiveVisitCheck.isTypeParameter(aTypeName)) {
      return aTypeName;
    }
    String anElementTypeName = TagBasedCheck.toElementTypeName(aTypeName);
    STType anSTType = SymbolTableFactory.getOrCreateSymbolTable()
            .getSTClassByFullName(anElementTypeName);
    return toOutputType(anSTType);
    // if (anSTType != null) {
    // String aTag = toTaggedType(anSTType);
    // if (aTag != null) {
    // return "@" + aTag;
    // }
    //// if (TagBasedCheck.isExplicitlyTagged(anSTType)) {
    //// return aTypeName;
    //// }
    // }
    // return TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSON;

    // return ".*";
  }

  public static String toOutputType(STType anSTType) {

    if (anSTType != null) {
      String aTag = toTaggedType(anSTType);
      if (aTag != null) {
        // return "@" + aTag;
        return aTag;

      }
      // if (TagBasedCheck.isExplicitlyTagged(anSTType)) {
      // return aTypeName;
      // }
    }
    return TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION;

    // return ".*";
  }

  public static String toOutputSubtypeOrExternalType(STType anSTType) {
    if (anSTType.isExternal()) {
      return anSTType.getName();
    }

    if (anSTType != null) {
      STType aSubtype = getSTSubtypeTagged(anSTType);
      if (aSubtype == null) {
        return TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION;
      }
      String aTag = toTaggedType(aSubtype);
      if (aTag != null) {
        // return "@" + aTag;
        return aTag;
      }
      // if (TagBasedCheck.isExplicitlyTagged(anSTType)) {
      // return aTypeName;
      // }
    }
    return TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION;

    // return ".*";
  }

  static String[] stringArray = {};

  public static boolean isExternalOrTaggedType(STType anSTType) {
    // if (anSTType.getName().equals("gradingTools.comp533s19.assignment0.AMapReduceTracer")) {
    // System.err.println("found A map reduce tracer");
    // }
    return anSTType != null
            && (anSTType.isExternal() || TagBasedCheck.isExplicitlyTagged(anSTType));
  }

  public static void processTypeProperties(STType anSTType) {
    if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
      return;
    }
    String aTaggedType = toOutputType(anSTType.getName());
    Map<String, PropertyInfo> aProperties = anSTType.getPropertyInfos();
    if (aProperties == null) {
      aProperties = anSTType.getDeclaredPropertyInfos();
    }
    Set<String> aKeys = aProperties.keySet();
    if (aKeys.size() == 0) {
      return;
    }
    List<String> aNamesAndTypes = new ArrayList(aKeys.size() * 2);
    // int anIndex = 0;
    for (String aKey : aKeys) {
      PropertyInfo aPropertyInfo = aProperties.get(aKey);
      if (aPropertyInfo.getGetter() != null && aPropertyInfo.getGetter().isPublic()) {
        String aPropertyName = aPropertyInfo.getName();
        String aPropertyType = aPropertyInfo.getType();
        STType aDeclaringType = aPropertyInfo.getDefiningSTType();
        if (aDeclaringType != anSTType && aDeclaringType != null & aDeclaringType.isExternal()) {
          continue;

        }
        // if (aPropertyName.equals("Key")) {
        // System.err.println("found key");
        // }
        String anOutputPropertyType = toOutputType(aPropertyType);
        aNamesAndTypes.add(aPropertyName);
        aNamesAndTypes.add(anOutputPropertyType);
        //
        // aNamesAndTypes[anIndex] = aPropertyName;
        // aNamesAndTypes[anIndex+1] = anOutputPropertyType;
        // anIndex = anIndex + 2;
        // System.out.println (anSTType.getName() +
        // "name:" + aPropertyName +
        // " type:" + anOutputPropertyType);

      }
    }
    // printExpectedGetters(anSTType.getName(), aNamesAndTypes);
    printExpectedGetters(aTaggedType, aNamesAndTypes.toArray(stringArray));

  }
  public static void processTypeEditableProperties(STType anSTType) {
    if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
      return;
    }
    String aTaggedType = toOutputType(anSTType.getName());
    Map<String, PropertyInfo> aProperties = anSTType.getPropertyInfos();
    if (aProperties == null) {
      aProperties = anSTType.getDeclaredPropertyInfos();
    }
    Set<String> aKeys = aProperties.keySet();
    if (aKeys.size() == 0) {
      return;
    }
    List<String> aNamesAndTypes = new ArrayList(aKeys.size() * 2);
    // int anIndex = 0;
    for (String aKey : aKeys) {
      PropertyInfo aPropertyInfo = aProperties.get(aKey);
      if (aPropertyInfo.getSetter() != null && aPropertyInfo.getSetter().isPublic()) {
        String aPropertyName = aPropertyInfo.getName();
        String aPropertyType = aPropertyInfo.getType();
        STType aDeclaringType = aPropertyInfo.getDefiningSTType();
        if (aDeclaringType != anSTType && aDeclaringType != null & aDeclaringType.isExternal()) {
          continue;

        }
        // if (aPropertyName.equals("Key")) {
        // System.err.println("found key");
        // }
        String anOutputPropertyType = toOutputType(aPropertyType);
        aNamesAndTypes.add(aPropertyName);
        aNamesAndTypes.add(anOutputPropertyType);
        //
        // aNamesAndTypes[anIndex] = aPropertyName;
        // aNamesAndTypes[anIndex+1] = anOutputPropertyType;
        // anIndex = anIndex + 2;
        // System.out.println (anSTType.getName() +
        // "name:" + aPropertyName +
        // " type:" + anOutputPropertyType);

      }
    }
    // printExpectedGetters(anSTType.getName(), aNamesAndTypes);
    printExpectedSetters(aTaggedType, aNamesAndTypes.toArray(stringArray));

  }

  public static void processTypeCallInfos(STType anSTType) {

    String aTypeOutputName = toOutputType(anSTType);
    if (aTypeOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
      return;
    }

    // if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
    //
    // return;
    // }
    List<CallInfo> aCallInfos = anSTType.getAllMethodsCalled();
    if (aCallInfos == null) {
      aCallInfos = anSTType.getMethodsCalled();
      // aCallInfos = anSTType.getAllMethodsCalled();

    }
    List<String> aCalledTypeAndMethods = new ArrayList();
    for (CallInfo aCallInfo : aCallInfos) {
      // if (aCallInfo.toString().contains("setInput") && (aCallInfo.toString().contains("input")) )
      // {
      // System.out.println ("found set input");
      // }
      String aCalledType = aCallInfo.getCalledType();

      STType aCalledSTType = SymbolTableFactory.getSymbolTable().getSTClassByShortName(aCalledType);
      // if (aCalledSTType == null) {
      // aCalledSTType = SymbolTableFactory.getSymbolTable().getSTClassByShortName(aCalledType);
      // }
      if (aCalledSTType == null || aCalledSTType == anSTType) { // do not check internal method
                                                                // calls
        continue;
      }
      String anOutputType = toOutputSubtypeOrExternalType(aCalledSTType);
      if (anOutputType == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
        continue;
      }
      STMethod aCallingMethod = aCallInfo.getCallingMethod();
      String aCallingMethodSignature = aCallingMethod.isPublic()
//              ? aCallingMethod.getSimpleChecksSignature()
                      ? aCallingMethod.getSimpleChecksTaggedSignature()

              : TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION;
      Set<STMethod> aCallMethods = aCallInfo.getMatchingCalledMethods();
      String aCalledMethodSignature = null;
      if (aCallMethods.size() == 1) {
        STMethod aCalledMethod = aCallMethods.iterator().next();
        if (!aCalledMethod.isPublic() || aCalledMethod.isConstructor()) {
          continue;
        }
        aCalledMethodSignature = aCalledMethod.getSimpleChecksTaggedSignature();
//        if (aCalledMethodSignature.contains("setMap")) {
//          System.err.println("Found wrongly tagged signature");
//        }
      } else { // we could not really identify the method
        aCalledMethodSignature = AnAbstractSTMethod.getMatchAnyHeader(aCallInfo.getCallee());
        if (Character.isUpperCase(aCalledMethodSignature.charAt(0))) {
          continue;// guess it isconstructor call
        }
      }
      aCalledTypeAndMethods.add(
              anOutputType + MethodCallCheck.TYPE_SIGNATURE_SEPARATOR + aCalledMethodSignature);

    }
    if (aCalledTypeAndMethods.size() == 0) {
      return;
    }
    printModuleSingleProperty("MissingMethodCall", "warning", aTypeOutputName, "expectedCalls",
            aCalledTypeAndMethods.toArray(stringArray));
    printModuleSingleProperty("MissingMethodCall", "info", aTypeOutputName, "expectedCalls",
            aCalledTypeAndMethods.toArray(stringArray));
  }
  

  public static void processMethodsCalled(STType anSTType) {
//    if (anSTType.getName().contains("RMIRegistryMain")) {
//      System.err.println("Found problematic type");
//    }
    String aCallingTypeOutputName = toOutputType(anSTType);
    if (aCallingTypeOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
      return;
    }
    // if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
    // return;
    // }
    STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
    Set<String> aCalledTypeAndMethods = new HashSet();



    for (STMethod aCallingMethod : aMethods) {
      String aCallingMethodSignature = aCallingMethod.isPublic()
              ? aCallingMethod.getSimpleChecksTaggedSignature()
              : TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION;
//      if (aCallingMethodSignature.contains("atomicBroad")) {
//        System.err.println("Found problematic calling method");
//      }
      
//      Set<STMethod> aCalledMethods = aCallingMethod.getAllDirectlyOrIndirectlyCalledMethods();
              
      Set<STMethod> aCalledMethods = 
              MissingMethodCallCheck.isProcessCalledMethodsStatic()?
              
              
              aCallingMethod.getAllDirectlyOrIndirectlyCalledMethods():
                new HashSet(aCallingMethod.getAllMethodsCalled());

      if (aCalledMethods == null) {
        continue;
      }
      for (STMethod aCalledMethod : aCalledMethods) {
//         if (aCalledMethod.getName().contains("educe")) {
//         System.err.println("found problematic method:");
//         }
        
        
        // if (!aCalledMethod.isPublic()) {
        // continue;
        // }
        STType aCalledType = aCalledMethod.getDeclaringSTType();
        
        if (aCalledType != null && aCalledType.isExternal()) {
          if (ignoreExternalTypes.contains(aCalledType.getName())) {
            continue;
          }
        }
        
        if (aCalledType != null && !aCalledType.isExternal()) {
          String aCalledOutputType = toOutputType(aCalledType);
          if (aCalledOutputType == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
            continue;
          }
        }

        if ((!aCalledMethod.isPublic() && 
                (aCalledType != null) && 
                !aCalledType.isExternal()) 
            || aCalledMethod.isConstructor()) {
          continue;
        }
//        if (!aCalledMethod.isPublic() && 
//                (aCalledMethod.getDeclaringSTType() != null) && 
//                !aCalledMethod.getDeclaringSTType().isExternal() 
//            || aCalledMethod.isConstructor()) {
//          continue;
//        }
//        STType aCalledType = aCalledMethod.getDeclaringSTType();
//        if (aCalledType == null || aCalledType == anSTType) {
//          continue;
//        }
        
        if (aCalledType == anSTType) {
          continue;
        }
       
       
        
//        String anOutputType = toOutputSubtypeOrExternalType(aCalledType);
//        if (anOutputType == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
//          continue;
//        }
        
        String anOutputType = aCalledType == null? 
                TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION:                  
                
                toOutputSubtypeOrExternalType(aCalledType);
        
//        if (anOutputType == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
//          continue;
//        }
       
        String aCalledMethodSignature = aCalledMethod.isAmbiguouslyOverloadedMethods()
                || aCalledMethod.isUnresolvedMethod()
                        ? AnAbstractSTMethod.getMatchAnyHeader(aCalledMethod.getName()) :
                          aCalledMethod.getSimpleChecksTaggedSignature();

//                        aCalledMethod.getSimpleChecksSignature();
//        if (aCalledMethodSignature.contains("TypeParam")) {
//          System.err.println("Found wrongly tagged signature");
//        }
        aCalledTypeAndMethods.add(
                anOutputType + MethodCallCheck.TYPE_SIGNATURE_SEPARATOR + aCalledMethodSignature);
        // if (aCalledMethodSignature.contains("ABarrier")) {
        // System.err.println("ABarrier");
        // }

        // if (aSubtype != null) {
        // System.out.println("Calling type:" + anSTType + " calling method: " + aCallingMethod + "
        // called type " + aSubtype + " called method " + aCalledMethod);
        //
        // }

      }

    }
    if (aCalledTypeAndMethods.size() == 0) {
      return;
    }
    printModuleSingleProperty("MissingMethodCall", "warning", aCallingTypeOutputName,
            "expectedCalls", aCalledTypeAndMethods.toArray(stringArray));
    printModuleSingleProperty("MissingMethodCall", "info", aCallingTypeOutputName,
            "expectedCalls", aCalledTypeAndMethods.toArray(stringArray));

  }

  public static void processDeclaredMethods(STType anSTType) {
    String aCallingTypeOutputName = toOutputType(anSTType);
    if (aCallingTypeOutputName == TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION) {
      return;
    }
    Set<String> aDeclaredSignatures = new HashSet();
    // if (!TagBasedCheck.isExplicitlyTagged(anSTType)) {
    // return;
    // }
    STMethod[] aMethods = getDeclaredOrAllMethods(anSTType);
    for (STMethod aMethod : aMethods) {
      if (!aMethod.isPublic()) {
        continue;
      }
      STType aDeclaringSTType = aMethod.getDeclaringSTType();
      if (aDeclaringSTType != anSTType && aDeclaringSTType != null
              && aDeclaringSTType.isExternal()) {
        // isExternalOrTaggedType(aDeclaringSTType)) {
        continue;
      }
      // if (aDeclaringSTType != null && aDeclaringSTType.isExternal()) {
      // continue;
      // }
      if (aMethod.isGetter() || aMethod.isSetter()) {
        continue;
      }

//      aDeclaredSignatures.add(aMethod.getSimpleChecksTaggedSignature());
      // what is this code doingG
//      String[] aNormalizedTypes = TagBasedCheck.toNormalizedTypes(aMethod.getParameterTypes());
//      String aReturnType = aMethod.getReturnType();
//      String aNormalizedReturnType = TagBasedCheck.toNormalizedType(aReturnType);
//      if (aReturnType.contains("Linked")) {
//        System.out.println("Found wrong generated signature");
//      }
      
      aDeclaredSignatures.add(aMethod.getSimpleChecksTaggedSignature());


      // System.out.println("Types:" + Arrays.toString(aNormalizedTypes) + " return:"
      // +aNormalizedReturnType);

    }
    if (aDeclaredSignatures.size() == 0) {
      return;
    }
    printModuleSingleProperty("ExpectedSignatures", "warning", aCallingTypeOutputName,
            "expectedSignatures", aDeclaredSignatures.toArray(stringArray));
    printModuleSingleProperty("ExpectedSignatures", "info", aCallingTypeOutputName,
            "expectedSignatures", aDeclaredSignatures.toArray(stringArray));

  }

  public static boolean isPrintOnlyTaggedClasses() {
    return printOnlyTaggedClasses;
  }

  public static void setPrintOnlyTaggedClasses(boolean printOnlyTaggedClasses) {
    PostProcessingMain.printOnlyTaggedClasses = printOnlyTaggedClasses;
  }

  protected static boolean redirectSecondPassOutput = false;

  public static boolean isRedirectSecondPassOutput() {
    return redirectSecondPassOutput;
  }

  public static void setRedirectSecondPassOutput(boolean redirectSecondPassOutput) {
    PostProcessingMain.redirectSecondPassOutput = redirectSecondPassOutput;
  }
  
  protected static boolean redirectFirstPassOutput = false;

  public static boolean isRedirectFirstPassOutput() {
    return redirectFirstPassOutput;
  }

  public static void setRedirectFirstPassOutput(boolean newVal) {
    PostProcessingMain.redirectFirstPassOutput = newVal;
  }

  protected static boolean generateChecks = false;
  protected static boolean generateExternals = true;

  public static boolean isGenerateExternals() {
    return generateExternals;
  }

  public static void setGenerateExternals(boolean generateExternals) {
    PostProcessingMain.generateExternals = generateExternals;
  }

  public static boolean isGenerateChecks() {
    return generateChecks;
  }

  public static void setGenerateChecks(boolean newVal) {
    generateChecks = newVal;
  }

  static PrintStream oldOut, initialOut;

  public static void redirectOut() {
	 
    oldOut = System.out;

    try {
      File aDummyFile = new File(DUMMY_FILE_NAME);
      aDummyFile.createNewFile();
      PrintStream aPrintStream = new PrintStream(aDummyFile);
      System.setOut(aPrintStream);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  public static void restoreInitialOut() {
	  System.setOut(initialOut);
  }
  public static void restoreOut() {
    System.setOut(oldOut);
  }
  static  File secondPassFile = null;
  public static void setSecondPassFile(File newVal) {
    secondPassFile = newVal;
  }
  public static File getSecondPassFile() {
    return secondPassFile;
  }
  public static final String DEFAULT_SECOND_PASS_SUFFIX = "_check_results.txt";
  public static void maybeInitializeRedirectSecondPassOutput(String[] args) {
    if (isRedirectSecondPassOutput()) {
      if (secondPassFile == null) {
      String aFileName = args[args.length - 1];
      File aFile = new File(aFileName);
      String aShortFileName = aFile.getName();
      if ("src".equals(aShortFileName)) {
        aFile = aFile.getParentFile();
        if (aFile != null) {
          aShortFileName = aFile.getName();
        }
      }
//       secondPassFile = new File(aShortFileName + "_check_results.txt");
       secondPassFile = new File(aShortFileName + DEFAULT_SECOND_PASS_SUFFIX);
      }

      try {
//        aFile.createNewFile();
        oldOut = System.out;
        outPrintStream = new PrintStream(secondPassFile);
        System.setOut(outPrintStream);

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  static  File generatedChecksFile = null;
  public static void setGeneratedChecksFile(File newVal) {
    generatedChecksFile = newVal;
  }
  public static File getGeneratedChecksFile() {
    return generatedChecksFile;
  }
  public static void maybeInitializeGeneratedChecksFile() {
    if (isGenerateChecks()) {
      if (generatedChecksFile == null) {
      generatedChecksFile = new File(DEFAULT_GENERATED_CHECKS_FILE_NAME);
      }
      try {
        generatedChecksFile.createNewFile();
//        checksPrintStream = new PrintStream(new File(DEFAULT_GENERATED_CHECKS_FILE_NAME));
        checksPrintStream = new PrintStream(generatedChecksFile);


      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  public static SymbolTable getSymbolTable() {
    return symbolTable;
  }

  public static List<STType> getsTTypes() {
    return sTTypes;
  }

  public static XMLLogger getXmlLogger() {
    return xmlLogger;
  }
  
  public static STBuilderCheck getsTBuilderCheck() {
    return sTBuilderCheck;
  }

  public static void main(String[] args) {
//    UNCCheck.setManualProjectDirectory(true);
//    if (isRedirectSecondPassOutput()) {
//      String aFileName = args[args.length - 1];
//      File aFile = new File(aFileName);
//      String aShortFileName = aFile.getName();
//      if ("src".equals(aShortFileName)) {
//        aFile = aFile.getParentFile();
//        if (aFile != null) {
//          aShortFileName = aFile.getName();
//        }
//      }
//       secondPassFile = new File(aShortFileName + "_check_results.txt");
//      try {
////        aFile.createNewFile();
//        outPrintStream = new PrintStream(secondPassFile);
//        System.setOut(outPrintStream);
//
//      } catch (IOException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//      }
//    }
	  initialOut = System.out;
    maybeInitializeRedirectSecondPassOutput(args);
    ACheckStyleLogFileManager.setPrintLogInconsistency(false);
    // Set<String> aSet = UnixDictionarySet.getUnixDictionary();
    maybeInitializeGeneratedChecksFile();
//    if (isGenerateChecks()) {
//      File aFile = new File(DEFAULT_GENERATED_CHECKS_FILE_NAME);
//      try {
//        aFile.createNewFile();
//        checksPrintStream = new PrintStream(new File(DEFAULT_GENERATED_CHECKS_FILE_NAME));
//
//      } catch (IOException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//      }
//    }

    // Main.main(ARGS);

    try {
      if (isTraceSteps())
      System.err.println("Building symbol table and running first pass checks:"
              + new Date(System.currentTimeMillis()));
      STBuilderCheck.setNonInteractive(true);
      STBuilderCheck.setDoAutoPassChange(false); // always true
      UNCCheck.setDoNotVisit(true);
      if (!STBuilderCheck.isDoAutoPassChange()) {
        STBuilderCheck.setFirstPass(true);

      }
      if (isRedirectFirstPassOutput()) {
      redirectOut();
      }
  
      NonExitingMain.main(args);
      if (isTraceSteps())
      System.err.println("Symbol table size:" + SymbolTableFactory.getOrCreateSymbolTable().size());
      UNCCheck.setDoNotVisit(false);
      if (isRedirectFirstPassOutput()) {
      restoreOut();
      }
      if (isTraceSteps())
      System.err.println("Running second pass checks " + new Date(System.currentTimeMillis()));
      if (!STBuilderCheck.isDoAutoPassChange()) {
        STBuilderCheck.setFirstPass(false);
        PostProcessingMain
                .doSecondPass(SymbolTableFactory.getOrCreateSymbolTable().getAllSTTypes());
      }
      NonExitingMain.main(args);
      if (isTraceSteps())
      System.err.println("Finished second pass checks:" + new Date(System.currentTimeMillis()));
      if (isRedirectSecondPassOutput()) {
        restoreInitialOut();
        outPrintStream.close();
      }
      if (isGenerateChecks()) {
        initGlobals();
        if (isTraceSteps())
        System.err.println("Generating checks:" + new Date(System.currentTimeMillis()));

//        generateChecks(sTTypes);
        Collections.sort(sTTypes);
      generateChecks(sTTypes);
      if (isTraceSteps())
        System.err.println("Finished Generating checks:" + new Date(System.currentTimeMillis()));

      }
    } catch (Exception e) {

      // } catch (UnsupportedEncodingException | FileNotFoundException | CheckstyleException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (isGenerateExternals()) {
      if (isTraceSteps())
      System.err.println("Generating externals:" + new Date(System.currentTimeMillis()));

      initGlobals();
      // outputExternalReferences(sTTypes);
      outputExternalOrTaggedCallInfos(sTTypes);
      if (isTraceSteps())
      System.err.println("Finished Generating externals:" + new Date(System.currentTimeMillis()));

    }
    if (xmlLogger != null)
      xmlLogger.auditFinished(null);
    // String[] aPropertyNamesAndValues = {"prop", "1", "prop2", "2"};
    //
    // printWarningModuleAndProperties("test module", "KeyValueClass", aPropertyNamesAndValues);
    // String[] aGetterProperties = {"Key", ".*", "Value", ".*"};
    // printExpectedGetters("KeyValueClass", aGetterProperties);
    if (isGenerateChecks()) {
    checksPrintStream.close();
    }
    // testXMLLogger();

  }
  public static boolean isTraceSteps() {
    return traceSteps;
  }

  public static void setTraceSteps(boolean traceSteps) {
    PostProcessingMain.traceSteps = traceSteps;
  }

}
