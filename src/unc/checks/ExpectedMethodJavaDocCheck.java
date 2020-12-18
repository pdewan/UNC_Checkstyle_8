package unc.checks;

import java.util.Arrays;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.STMethod;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;

public class ExpectedMethodJavaDocCheck extends STTypeVisited{
  public static final String MSG_KEY_INFO = "expectedMethodJavaDoc";
  public static final String MSG_KEY_WARNING = "missingMethodJavaDoc";
  
  

  protected String expectedJavaDoc;
  
  public int[] getDefaultTokens() {
    return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,  TokenTypes.INTERFACE_DEF};
  }

  public String getExpectedJavaDoc() {
    return expectedJavaDoc;
  }
  public void setExpectedJavaDoc(String newVal) {
//    this.expectedJavaDoc = multipSpaceToSingleSpace(newVal);
    String aRemoveStars = newVal.replaceAll("[\\*\n\\[,/\\]]", "");
    String aRemoveMultipleSpaces = multipSpaceToSingleSpace(aRemoveStars).trim().toLowerCase();
    expectedJavaDoc = aRemoveMultipleSpaces;
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
  @Override
  protected void checkSTType(DetailAST ast, STType anSTClass) {
   if (!checkIncludeTagsOfCurrentType(anSTClass)) {
    return;
  }
    if (isProcessCalledMethods() && isFirstPass()) {
      return ;
    }
    if (anSTClass == null)
      return;
    if (expectedJavaDoc == null) {
      return ;
    }
    STMethod[] aMethods = anSTClass.getMethods();
    if (aMethods == null) {
      return;
    }
    STMethod aFoundMethod = null;
    for (STMethod anSTMethod:aMethods) {
      if (!checkCallingMethod(anSTMethod)) {
        continue;
      }
      TextBlock aJavaDocComment = anSTMethod.getJavaDocComment();
      if (aJavaDocComment == null) {
        continue;
      }
      String aRemoveStars = Arrays.toString(aJavaDocComment.getText()).replaceAll("[\\*\n\\[,/\\]]", "");
      String aRemoveMultipleSpaces = multipSpaceToSingleSpace(aRemoveStars).trim().toLowerCase();
      if (aRemoveMultipleSpaces.contains(expectedJavaDoc)) {

//      if (aRemoveMultipleSpaces.contains(expectedJavaDoc.trim().toLowerCase())) {
        aFoundMethod = anSTMethod;
        break;
      }
      
    }
    if (aFoundMethod != null && isInfo()) {
        log(anSTClass.getAST(), aFoundMethod.getAST(), expectedJavaDoc, aFoundMethod.getName(), toTagInformation() );
        return ;
      
    } else if (aFoundMethod == null && !isInfo()) {
      log(anSTClass.getAST(), expectedJavaDoc, toTagInformation() );   
    
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
