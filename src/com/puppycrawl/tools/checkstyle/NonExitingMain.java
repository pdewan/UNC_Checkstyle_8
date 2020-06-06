package com.puppycrawl.tools.checkstyle;

import java.io.IOException;

public class NonExitingMain {
  public static void anExiter(){
    System.exit(-1);
  }
	public static void main (String[] args) {
	  try {
	    NonExitingSecurityManager secManager = new NonExitingSecurityManager();
	    System.setSecurityManager(secManager);
//	    anExiter();

	   
      Main.main(args);
    } catch (Exception e) {
      e.printStackTrace();
    }
	}

}
