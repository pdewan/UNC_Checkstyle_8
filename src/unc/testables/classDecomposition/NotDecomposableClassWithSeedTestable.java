package unc.testables.classDecomposition;

public class NotDecomposableClassWithSeedTestable {
  int variable1;
  String variable2;
  public int getVariable1() {
    return variable1;
  }
  public void setVariable1(int variable1) {
    this.variable1 = variable1;
  }
  public String getVariable2() {
    return variable2;
  }
  public void setVariable2(String variable2) {
    this.variable2 = variable2;
  }
  
  public void incVariable1() {
    variable1++;
  }
  public void decVariable1() {
    variable1--;
  }
  public void resetVariable2() {
    variable2 = "";;
  }
  
}
