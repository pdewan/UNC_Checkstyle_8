package unc.checks;

import com.puppycrawl.tools.checkstyle.api.FullIdent;

public interface IdentifierClassNamePair {

	FullIdent getIdentifier();

	String getClassName();

}
