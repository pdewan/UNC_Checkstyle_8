package unc.testables;

public class IfTestable {
  static boolean someCondition;
  static int someInteger;

  public static void bulkierThen() {
    if (someCondition) {
      someInteger++;
      someInteger++;
      someInteger++;
      someInteger++;
    } else {
      someInteger++;
    }
  }

  public static void bulkierElse() {
    if (someCondition) {
      someInteger++;
    } else {
      someInteger++;
      someInteger++;
      someInteger++;
      someInteger++;
    }
  }

  public static void equalThenElse() {
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

  public static void moreOrLessEqualThenElse() {
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

  public static void thenBranching() {
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

  public static void elseBranching() {
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
