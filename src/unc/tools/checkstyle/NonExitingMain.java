package unc.tools.checkstyle;

import java.io.IOException;

import com.puppycrawl.tools.checkstyle.Main;

public class NonExitingMain {
  public static void anExiter(){
    System.exit(-1);
  }
	public static void main (String[] args) {
	  try {
	    SecurityManager oldSecurityManager = System.getSecurityManager();
	    NonExitingSecurityManager secManager = new NonExitingSecurityManager();
	    System.setSecurityManager(secManager);
//	    anExiter();

	   
      Main.main(args);
      System.setSecurityManager(oldSecurityManager);
    } catch (Exception e) {
      e.printStackTrace();
    }
	}

}
