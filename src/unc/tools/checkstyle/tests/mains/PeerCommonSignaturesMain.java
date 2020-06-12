package unc.tools.checkstyle.tests.mains;

import unc.tools.checkstyle.PostProcessingMain;

public class PeerCommonSignaturesMain extends PostProcessingMain {


static final String SOURCE = "src/unc/testables/commonSignatures";

static final String CHECKSTYLE_CONFIGURATION = "testChecks/peer_signatures_checks.xml";

//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE};

	public static void main (String[] args) {
		setPrintOnlyTaggedClasses(true);
		PostProcessingMain.main(ARGS);
			
	}

}
