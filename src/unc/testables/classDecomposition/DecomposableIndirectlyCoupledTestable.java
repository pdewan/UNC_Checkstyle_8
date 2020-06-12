package unc.testables.classDecomposition;

public class DecomposableIndirectlyCoupledTestable {
  int variable1, variable2, variable3, variable4;

  public void inc1() {
    variable1++;
    variable2++;
  }

  public void inc3() {
    variable3++;
    variable4++;
  }
  
}
