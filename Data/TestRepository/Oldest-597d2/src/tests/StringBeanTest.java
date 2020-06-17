package tests;

import beans.StringBean;
import beans.Int2Bean;

public class StringBeanTest {
  
  public void test() {
    StringBean aBean1 = new StringBean();
    aBean1.setString("5");
    aBean1.getString();
  }

}
