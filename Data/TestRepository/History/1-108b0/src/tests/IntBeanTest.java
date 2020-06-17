package tests;

import beans.Int1Bean;
import beans.Int2Bean;

public class IntBeanTest {
  
  public void test() {
    Int1Bean aBean1 = new Int1Bean();
    aBean1.setInt1(5);
    aBean1.getInt1();
    Int2Bean aBean2 = new Int2Bean();
    aBean2.setInt2(5);
    aBean2.getInt2();

  }

}
