package unc.tools.checkstyle.tests.mains;

import unc.tools.checkstyle.PostProcessingMain;

public class ClassDecompositionMain extends PostProcessingMain {


static final String SOURCE = "src/unc/testables/classDecomposition";

static final String CHECKSTYLE_CONFIGURATION = "testChecks/class_decomposition_checks.xml";

//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE};

	public static void main (String[] args) {
		setPrintOnlyTaggedClasses(true);
		PostProcessingMain.main(ARGS);
			
	}

}
