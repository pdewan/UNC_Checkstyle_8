package unc.tools.checkstyle;

public class PostProcessingTestingMain extends PostProcessingMain {


static final String SOURCE = "src/unc/testables";

static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";

//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE};

	public static void main (String[] args) {
		setPrintOnlyTaggedClasses(true);
		PostProcessingMain.main(ARGS);
			
	}

}
