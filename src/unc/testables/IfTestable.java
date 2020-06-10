package unc.testables;

public class IfTestable {
  static boolean someCondition;
  int someInteger;

  public void bulkierThen() {
    if (someCondition) {
      someInteger++;
      someInteger++;
      someInteger++;
      someInteger++;
    } else {
      someInteger++;
    }
  }

  public void bulkierElse() {
    if (someCondition) {
      someInteger++;
    } else {
      someInteger++;
      someInteger++;
      someInteger++;
      someInteger++;
    }
  }

  public void equalThenElse() {
    if (someCondition) {
      someInteger++;
      someInteger++;
      someInteger++;
      someInteger++;
    } else {
      someInteger++;
      someInteger++;
      someInteger++;
      someInteger++;
    }
  }

  public void moreOrLessEqualThenElse() {
    if (someCondition) {
      someInteger++;
      someInteger++;
      someInteger++;
      someInteger++;
    } else {
      someInteger++;
      someInteger++;
      someInteger++;
    }
  }

  public void thenBranching() {
    if (someCondition) {
      if (someCondition) {
        if (someCondition) {
          someInteger++;
        } else {
          someInteger++;
        }
      } else {
        someInteger++;
      }
    } else {
      someInteger++;
    }
    
  }

  public void elseBranching() {
    if (!someCondition) {
      someInteger++;
    } else if (!someCondition) {
      someInteger++;
    } else if (!someCondition) {
      someInteger++;
    } else {
      someInteger++;
    }
     
  }
  
}
