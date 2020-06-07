package unc.tools.checkstyle;

import java.io.File;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.FileText;

public class ANonCachingTreeWalker extends AnExtendibleTreeWalker{
	// always process file
  protected void processFiltered(File file, FileText fileText) throws CheckstyleException {
    super.processFiltered(file, fileText);
  }



}
