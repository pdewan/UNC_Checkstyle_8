package unc.tools.checkstyle;

import java.security.Permission;

class NonExitingSecurityManager extends SecurityManager {
  @Override public void checkExit(int status) {
    throw new SecurityException();
  }

  @Override 
  public void checkPermission(Permission perm) {
      // Allow other activities by default
  }
}