package unc.testables.classDecomposition;

public class NonDecomposableIndirectlyCoupledTestable {
  int variable1, variable2, variable3, variable4;

  public void inc1() {
    variable1++;
    variable2++;
  }
  public void inc2() {
    variable2++;
    variable3++;
  }
  public void inc3() {
    variable3++;
    variable4++;
  }
  
}
