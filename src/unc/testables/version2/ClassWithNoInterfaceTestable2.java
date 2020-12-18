package unc.testables.version2;

import unc.testables.classInterfaces.InterfaceExtended1Testable;

public class ClassWithNoInterfaceTestable2 implements InterfaceExtended1Testable {

  public static final String GREETING = "Hello Workd";

  public void signature1(boolean aFlag) {
    if (2 == 2) {
      signature2(0);
    } else {

    }
  }

  // @Override
  public void signature2(int anInt) {

    System.out.println(GREETING);

  }
}
