package unc.tools.checkstyle.tests.mains;

import unc.tools.checkstyle.PostProcessingMain;

public class EqualAvoidsNullTestMain extends PostProcessingMain {


static final String SOURCE = "src/unc/testables/EqualAvoidNullTestable.java";

static final String CHECKSTYLE_CONFIGURATION = "equal_avoided_null_check.xml";

//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE};

	public static void main (String[] args) {
		setPrintOnlyTaggedClasses(true);
		PostProcessingMain.main(ARGS);
			
	}

}
