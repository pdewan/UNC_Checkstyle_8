package unc.tools.checkstyle;

public class PostProcessingTestingMain extends PostProcessingMain {

//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\twitter-heron";
//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\twitter-heron\\heron\\common\\src\\java\\org\\apache\\heron\\common\\basics\\NIOLooper.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src";
//static final String SOURCE = "D:\\dewan_backup\\Java\\UNCCheckStyle\\src\\test";
static final String SOURCE = "src/test";


//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\RamA1-A5\\Assignment5OG\\src";
//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\A3\\src";


//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\ATokenCountingModel.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\AnAbstractModel.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\AMultiThreadController.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\AMultiThreadController.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\TokenCountingClientLauncher.java";


//static final String SOURCE = "D:\\dewan_backup\\Java\\UNCCheckStyle\\src\\test";

//static final String SOURCE  = "C:\\Users\\dewan\\Downloads\\twitter-heron\\contrib\\bolts\\kafka\\src\\java\\org\\apache\\heron\\bolts\\kafka\\KafkaBolt.java";
//static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:\\dewan_backup\\Java\\UNCCheckStyle\\unc_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:\\dewan_backup\\Java\\UNCCheckStyle\\checks\\533-s20\\unc_checks_533_A3.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:\\dewan_backup\\Java\\UNCCheckStyle\\checks\\533-s20\\unc_checks_app_independent.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:\\dewan_backup\\Java\\UNCCheckStyle\\checks\\533-s20\\unc_checks_533_A6.xml";

static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";

//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE};

	public static void main (String[] args) {
		setPrintOnlyTaggedClasses(true);
		PostProcessingMain.main(ARGS);
//		    Main.main(ARGS);

//			try {
//				NonExitingMain.main(ARGS);
//				initGlobals();
//				processTypes(sTTypes);
//			} catch (UnsupportedEncodingException | FileNotFoundException | CheckstyleException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
			
	}

}
