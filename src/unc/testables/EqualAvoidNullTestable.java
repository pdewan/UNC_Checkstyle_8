package unc.testables;

public class EqualAvoidNullTestable {
  String aVariable = null;
 public final String A_CONSTANT = "hello";
  protected boolean didNotAvoidNullLiteral() {
    return aVariable.equals("goodbye");
   }
  protected boolean didNotAvoidNullNamedConstant() {
    return aVariable.equals(A_CONSTANT);
   }
  protected boolean avoidesNullLiteral() {
    return 
            "goodbye".equals(aVariable);
   }  
  protected boolean avoidesNullConstant() {
    return 
            A_CONSTANT.equals(aVariable);
   }
  protected boolean doesNotReferenceConstants() {
    return  aVariable.equals(aVariable);
   }
  protected boolean alsoDoesNotReferenceConstants(String aParameter) {
    final String anotherVariable = aParameter;
    return  anotherVariable.equals(aVariable);
   }

}
