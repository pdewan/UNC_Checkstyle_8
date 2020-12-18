package unc.tools.checkstyle.commits;

import java.io.File;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class DistellerDriver {
//  public static final String FILE_NAME_1 = "src/unc/testables/classInterfaces/ClassWithNoInterfaceTestable.java";
//  public static final String FILE_NAME_2 = "src/unc/testables/classInterfaces/ClassWithExtendingInterfaceTestable.java";
  public static final String FILE_NAME_1 = "src/unc/testables/version1/ClassWithNoInterfaceTestable.java";
  public static final String FILE_NAME_2 = "src/unc/testables/version2/ClassWithNoInterfaceTestable2.java";
  static FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);

  public static void main (String[] args) {
    File aFile1 = new File(FILE_NAME_1);
    File aFile2 = new File(FILE_NAME_2);
    try {
      if (!aFile1.exists() || !aFile2.exists()) {
        System.exit(-1);
      }
      distiller.extractClassifiedSourceCodeChanges(aFile1, aFile2);
  

      List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
      if(changes != null) {
          for(SourceCodeChange change : changes) {
             System.err.println(change.getClass() + "change:" + change.getChangedEntity() + " " + change.getChangeType());
          }
      }
    } catch(Exception e) {
      /* An exception most likely indicates a bug in ChangeDistiller. Please file a
         bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
         attach the full stack trace along with the two files that you tried to distill. */
      System.err.println("Warning: error while change distilling. " + e.getMessage());
      e.printStackTrace();
  }
  }

}
