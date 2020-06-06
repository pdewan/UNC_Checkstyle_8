package com.puppycrawl.tools.checkstyle;

import java.lang.reflect.Method;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

public class DelegatingAstTreeStringPrinter {
  protected static Method printTreeMethod;
  public static String printTree(DetailAST anAST) {
    try {
    if (printTreeMethod == null) {
      printTreeMethod = AstTreeStringPrinter.class.getDeclaredMethod(
              "printTree", 
              new Class[]{
                  DetailAST.class
              });
      printTreeMethod.setAccessible(true);
    }
   
    String aResult = (String) printTreeMethod.invoke(AstTreeStringPrinter.class, new Object[] {anAST}); 
    return aResult;
    } catch (Exception e) {
      e.printStackTrace();
      return "Could not print tree";
    }
    
        
    }
    
   
  
 

}
