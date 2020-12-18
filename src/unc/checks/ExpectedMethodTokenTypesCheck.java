package unc.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STMethod;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;

public class ExpectedMethodTokenTypesCheck extends STTypeVisited{
  public static final String MSG_KEY_INFO = "expectedTokenTypes";
  public static final String MSG_KEY_WARNING = "missingTokenTypes";
  
  public static Object[][] tokenTypeToString = {
      
      
      {TokenTypes.TYPE_ARGUMENTS,"TYPE_ARGUMENTS"},
      {TokenTypes.TYPE_PARAMETERS, "TYPE_PARAMETERS"},
      {TokenTypes.VARIABLE_DEF, "VARIABLE_DEF"},
      {TokenTypes.METHOD_CALL, "METHOD_CALL"},
      {TokenTypes.IDENT, "IDENT"},
      {TokenTypes.ENUM_DEF, "ENUM_DEF"},
      {TokenTypes.PARAMETER_DEF, "PARAMETER_DEF"},      
      {TokenTypes.LITERAL_CASE,"CASE"},      
      {TokenTypes.LITERAL_CATCH, "CATCH"},
      {TokenTypes.LITERAL_SWITCH, "SWITCH"},
      {TokenTypes.LITERAL_IF, "IF"},      
      {TokenTypes.LITERAL_ELSE, "ELSE"},
      {TokenTypes.LITERAL_FOR, "FOR"},      
      {TokenTypes.LITERAL_WHILE, "WHILE"},
      {TokenTypes.LITERAL_BREAK, "BREAK"},
      {TokenTypes.LITERAL_DO, "DO"},
      {TokenTypes.LITERAL_CONTINUE, "CONTINUE"},
      {TokenTypes.LITERAL_FINALLY, "FINALLY"},
      {TokenTypes.LITERAL_INSTANCEOF,"INSTANCEOF"},
      {TokenTypes.LITERAL_NEW, "NEW"},
      {TokenTypes.LITERAL_RETURN, "RETURN"},
      {TokenTypes.LITERAL_ASSERT, "ASSERT"},
      {TokenTypes.LITERAL_SYNCHRONIZED, "SYNCHRONIZED"},
      {TokenTypes.LITERAL_THIS, "THIS"},
      {TokenTypes.LITERAL_THROW, "THROW"},
      {TokenTypes.LITERAL_THROWS,"THROWS"},
      {TokenTypes.LITERAL_TRANSIENT, "TRANSIENT"},
      {TokenTypes.LITERAL_FALSE, "FALSE"},
      {TokenTypes.LITERAL_TRUE, "TRUE"},
      {TokenTypes.LITERAL_TRY, "TRY"},
      {TokenTypes.LITERAL_VOLATILE, "VOLATILE"},
      {TokenTypes.LAND, "LAND"},
      {TokenTypes.LOR, "LOR"},
      {TokenTypes.LNOT, "LNOT"},
      {TokenTypes.BAND, "BAND"},
      {TokenTypes.BNOT, "BNOT"},
      {TokenTypes.BXOR, "BXOR"},
      {TokenTypes.BSR,"BSR"},
      {TokenTypes.BAND_ASSIGN, "BAND_ASSIGN"},
      {TokenTypes.BOR_ASSIGN, "BOR_ASSIGN"},      
      {TokenTypes.BSR_ASSIGN, "BSR_ASSIGN"},
      {TokenTypes.LT, "LT"},
      {TokenTypes.GT, "GT"},
      {TokenTypes.LE, "LE"},
      {TokenTypes.GE, "GE"},
      {TokenTypes.EQUAL, "EQUAL"},
      {TokenTypes.NOT_EQUAL, "NOT_EQUAL"},
      {TokenTypes.LCURLY, "LCURLY"},
      {TokenTypes.RCURLY, "RCURLY"},  
      
  };
  
  public static String toTokenString(int aTokenType) {
    for (Object[] aPair: tokenTypeToString) {
      if (aPair[0].equals(aTokenType)) {
        return (String) aPair[1];
      }
    }
    return null;
  }
  
  public static Integer toTokenType(String aTokenString) {
    String aTokenStringUpperCase = aTokenString.toUpperCase();
    for (Object[] aPair: tokenTypeToString) {
      if (aPair[1].equals(aTokenStringUpperCase)) {
        return (Integer) aPair[0];
      }
    }
    return null;
  }

  protected String expectedTokenTypes;
  protected List<Integer> expectedTokenTypeIntegers;
  protected Integer minimumCount;
  protected Integer maximumCount;
//  protected boolean processCalledMethods;
//  public boolean isProcessCalledMethods() {
//    return processCalledMethods;
//  }
//
//  public void setProcessCalledMethods(boolean processCalledMethods) {
//    this.processCalledMethods = processCalledMethods;
//  }

  public int[] getDefaultTokens() {
    return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  TokenTypes.INTERFACE_DEF};
  }
  
  public int getMinimumCount() {
    return minimumCount;
  }
  public void setMinimumCount(int newVal) {
    minimumCount = newVal;
  }
  public int getMaximumCount() {
    return maximumCount;
  }
  public void setMaximumCount(int newVal) {
    maximumCount = newVal;
  }

  public String getExpectedTokenTypes() {
    return expectedTokenTypes;
  }
  public void setExpectedTokenTypes(String newVal) {
      expectedTokenTypes = newVal;
      String[] anExpectedTokenTypeStrings = newVal.split(",");
      expectedTokenTypeIntegers = new ArrayList(anExpectedTokenTypeStrings.length);
      for (String anExpectedTokenTypeString:anExpectedTokenTypeStrings) {
        String aTokenString = anExpectedTokenTypeString.trim().toUpperCase();
        Integer aTokenType = toTokenType(aTokenString);
        if (aTokenType != null) {
          expectedTokenTypeIntegers.add(aTokenType);
        }
      }
  }
  @Override
  protected String msgKeyWarning() {
    // TODO Auto-generated method stub
    return MSG_KEY_WARNING;
  }
  @Override
  protected String msgKeyInfo() {
    // TODO Auto-generated method stub
    return MSG_KEY_INFO;

  }
  @Override
  protected String msgKey() {
    // TODO Auto-generated method stub
    return msgKeyWarning();
  }
//  @Override
//  protected String msgKey() {
//    return msgKeyWarning();
//  }
  public static  void updateTokenCounts(Map<Integer, Integer> aCounts, STMethod aMethod) {
     Map<Integer, Integer> aMethodCounts = aMethod.getTokenTypeCounts();
     if (aMethodCounts == null) {
       return;
     }
     for (Integer aKey: aMethodCounts.keySet()) {
       Integer aValue = aMethodCounts.get(aKey);
       Integer aCurrentValue = aCounts.get(aKey);
       Integer aNewValue = aCurrentValue == null?aValue:aValue + aCurrentValue;
       aCounts.put(aKey, aNewValue);
     }
     
    
  }
  @Override
  protected void checkSTType(DetailAST ast, STType anSTClass) {
    if (!checkIncludeTagsOfCurrentType(anSTClass)) {
      return;
    }
//    if (!typeCheck(anSTClass)) {
//      return;
//    }
    if (isProcessCalledMethods() && isFirstPass()) {
      return ;
    }
    if (anSTClass == null)
      return;
    if (expectedTokenTypes == null || (minimumCount == null && maximumCount == null)) {
      return ;
    }
    int anActualTokens = 0;
    STMethod[] aMethods = anSTClass.getMethods();
    STMethod aFoundMethod = null;
    Set<STMethod> aProcessedMethods = new HashSet<>();
    Map<Integer, Integer> aTokenCounts = new HashMap<Integer, Integer>();
    DetailAST aLoggedAST = ast;
    if (aMethods == null) {
      return;
    }
    for (STMethod aDeclaredSTMethod:aMethods) {
      if (!checkCallingMethod(aDeclaredSTMethod)) {
        continue;
      }
      Set<STMethod> anAllMethods = new HashSet<>();
      if (aProcessedMethods.contains(aDeclaredSTMethod)) {
        continue;
      }

      anAllMethods.add(aDeclaredSTMethod);
      if (isProcessCalledMethods()) {
        
        Set<STMethod> aCalledMethods = aDeclaredSTMethod.getAllDirectlyOrIndirectlyCalledMethods();
        if (aCalledMethods != null) {
        anAllMethods.addAll(aCalledMethods);
        aLoggedAST = aDeclaredSTMethod.getAST();
        }
      }
      for (STMethod anSTMethod: anAllMethods ) {
        if (aProcessedMethods.contains(anSTMethod)) {
          continue;
        }
        aProcessedMethods.add(anSTMethod);
        updateTokenCounts(aTokenCounts, anSTMethod);        
      }           
    }
//    DetailAST aLoggedAST = callingMethod != null?callingMethod.getAST():ast;
    checkTokenCounts(aLoggedAST, aTokenCounts);
    
    
  }
  StringBuffer aStringBuffer = new StringBuffer();

  protected String generateLoggedMessage(int aTokenType) {
    String aTokenString = toTokenString(aTokenType);
    aStringBuffer.setLength(0);
    aStringBuffer.append("The number of occurences of " + aTokenString);
    if (!isInfo()) {
      aStringBuffer.append(" not ");
    }
    
    if (minimumCount != null) {
      aStringBuffer.append(" >= " + minimumCount);
    } 
    if (maximumCount != null) {
      aStringBuffer.append(" <= " + maximumCount);
    }
    if (callingMethod != null) {
      aStringBuffer.append(" in " + callingMethod.getSignature() ); 
      if (processCalledMethods) {
        aStringBuffer.append(" and its callees");
      } 
//      else {
//        aStringBuffer.append(".");
//
//      }
    } 
//    else {
//      aStringBuffer.append(".");
//    }
//    if (isInfo()) {
//      aStringBuffer.append(" Good!");
//    } 
    return aStringBuffer.toString();
  }
  
  protected void checkTokenCounts(DetailAST anAST, Map<Integer, Integer> aTokenCounts) {
    int aMinimumCount = minimumCount == null?0:minimumCount;
    int aMaximumCount = maximumCount == null?Integer.MAX_VALUE:maximumCount;

  
    for (Integer aTokenType:expectedTokenTypeIntegers) {
      Integer anActualValue = aTokenCounts.get(aTokenType);
      if (anActualValue == null || anActualValue < aMinimumCount || anActualValue > aMaximumCount) {
        if (isInfo()) {
          continue;
        } else {
          log(anAST, generateLoggedMessage(aTokenType), toTagInformation() );
        }
      } else {
        if (!isInfo()) {
          continue;
        } else {
          log(anAST, generateLoggedMessage(aTokenType), toTagInformation());

        }
      }
    }
  }
  

  @Override
  protected boolean typeCheck(STType anSTClass) {
    // TODO Auto-generated method stub
    return false;
  }
//  public void visitType(DetailAST ast) {
//    super.visitType(ast);
//    
//    STType anSTClass = SymbolTableFactory.getOrCreateSymbolTable().
//        getSTClassByFullName(getFullTypeName());
//    checkSTType(ast, anSTClass);
////    if (!typeCheck(anSTClass))
////      super.logType(ast);
//
//  }
}
