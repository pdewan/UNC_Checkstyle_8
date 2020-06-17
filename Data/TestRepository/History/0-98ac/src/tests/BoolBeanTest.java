package tests;

import beans.Bool1Bean;
import beans.Bool2Bean;

public class BoolBeanTest {
  
  public void test() {
    Bool1Bean aBean1 = new Bool1Bean();
    aBean1.setBool1(true);
    aBean1.getBool1();
    Bool2Bean aBean2 = new Bool2Bean();
    aBean2.setBool2(true);
    aBean2.getBool2();

  }

}
