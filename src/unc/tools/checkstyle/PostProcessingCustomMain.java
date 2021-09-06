package unc.tools.checkstyle;

import java.io.File;

public class PostProcessingCustomMain extends PostProcessingMain {

//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\twitter-heron";
//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\twitter-heron\\heron\\common\\src\\java\\org\\apache\\heron\\common\\basics\\NIOLooper.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src";
//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava-3.x/src";
//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\RxJava_java_only\\java_only\\history\\1002-ac4aed1bdb7a67a3fae70c41549aef7e0d4fc6d1\\commit_changes\\rxjava-core\\src\\main\\java\\rx\\Observable.java";
//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\RxJava_java_only\\java_only\\history\\1018-7babfaf1dcf8f20d02e0404c2f13f47c46a55391\\commit_changes\\rxjava-contrib\\rxjava-swing\\src\\main\\java\\rx\\subscriptions\\SwingSubscriptions.java";
//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava_java_only/java_only/history/2057-b22f24fc086085d66842ffde0a30c6652843d6b0/commit_changes";
//Recursive rx.Single in version 2289
//C:\Users\dewan\Downloads\RxJava_java_only\java_only\history\107-4353bf8126544452721b1cb85ad44532ec2a651e\commit_changes\rxjava-core\src\main\java\rx\Observable.java
//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava-3.x/src/test/java/io/reactivex/rxjava3/internal/operators/observable/ObservableTakeUntilPredicateTest.java";
//C:\Users\dewan\Downloads\RxJava_java_only\java_only\history\1004-9b61c134e929dbd5b17216a58453bc8baea9a666\commit_changes\rxjava-core\src\main\java\rx\observers\SafeSubscriber.java
//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava-3.x/src/main/java/io/reactivex/rxjava3/internal/functions/Functions.java";
//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava_java_only/java_only/history/1980-2d5ce6935910cb3046384254329bd46236802796/commit_changes";
//static final String SOURCE = "D:/dewan_backup/Java/PLProjs/PLProjsJava/src/greeting/Hello.java";
//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava_java_only/java_only/history/1980-2d5ce6935910cb3046384254329bd46236802796/commit_changes/src/main/java/rx/Observable.java";

//Null variable in call parts [Ljava.lang.String;@420e17cdof fileC:\Users\dewan\Downloads\RxJava_java_only\java_only\history\1004-9b61c134e929dbd5b17216a58453bc8baea9a666\commit_changes\rxjava-core\src\main\java\rx\operators\SafeObserver.java

//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava-3.x/src/jmh/java/io/reactivex/rxjava3/core/BinaryFlatMapPerf.java";
//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava-3.x/src/main/java/io/reactivex/rxjava3/internal/fuseable/HasUpstreamSingleSource.java";
//static final String SOURCE = "C:/Users/dewan/Downloads/RxJava-3.x/src/main/java/io/reactivex/rxjava3/internal/fuseable/QueueDisposable.java";

//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\RxJava-3.x\\src\\main\\java\\io\\reactivex\\rxjava3\\annotations\\BackpressureSupport.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src";
//D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\checkstyle\\TrickOrTreatPostProcessingCustomMain.java
//static final String SOURCE = "D:\\dewan_backup\\Java\\UNCCheckStyle\\src\\test";
//static final String SOURCE = "src\\test";

//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\RxJava-3.x\\src\\jmh\\java\\io\\reactivex\\rxjava3\\core\\StrictPerf.java";
//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\RamA1-A5\\Assignment5OG\\src";
//static final String SOURCE = "C:\\Users\\dewan\\Downloads\\A3\\src";


//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\ATokenCountingModel.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\AnAbstractModel.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\ATokenCountingModel.java";
//static final String SOURCE2 = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\AnAbstractModel.java";
//static final String SOURCE2 = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\TokenCountingModel.java";


//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\AnAbstractModel.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\AMultiThreadController.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\AMultiThreadController.java";
//static final String SOURCE = "D:\\dewan_backup\\Java\\NIOTrickOrTreat\\src\\mapreduce\\TokenCountingClientLauncher.java";


//static final String SOURCE = "D:\\dewan_backup\\Java\\UNCCheckStyle\\src\\test";

//static final String SOURCE  = "C:\\Users\\dewan\\Downloads\\twitter-heron\\contrib\\bolts\\kafka\\src\\java\\org\\apache\\heron\\bolts\\kafka\\KafkaBolt.java";
//static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:\\dewan_backup\\Java\\UNCCheckStyle\\checks\\533-s20\\unc_checks_533_A3.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:\\dewan_backup\\Java\\UNCCheckStyle\\checks\\533-s20\\unc_checks_app_independent.xml";
//static final String CHECKSTYLE_CONFIGURATION = "checks/533-s20/unc_checks_533_A6.xml";
//C:\Users\dewan\Downloads\RxJava-3.x\src\main\java\io\reactivex\rxjava3\internal\schedulers\SchedulerPoolFactory.java (null st method)

//static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/UNC_Checkstyle_8/checks/301-ss21/unc_checks_301_A1.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/grail13/Logs/LocalChecks/unc_checks_301_A1.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/grail13/Logs/LocalChecks/unc_checks_301_A2.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/Assignment12Daniel/Logs/LocalChecks/unc_checks_301_A1_1.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/Assignment12Daniel/Logs/LocalChecks/unc_checks_301_A2_1.xml";

//static final String SOURCE = "D:/dewan_backup/Java/Assignment12Daniel/src/mp/scanner/ScanningIterator.java";
//static final String SOURCE = "D:/dewan_backup/Java/Assignment12Daniel/src";
static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/PLProjs/PLProjsJava/Logs/LocalChecks/unc_checks_524_A1.xml";

static final String SOURCE = "D:/dewan_backup/Java/PLProjs/PLProjsJava/src";
//static final String SOURCE = "D:/dewan_backup/Java/PLProjs/PLProjsJava/src/safeSocialization/util/SafeSocializationUtil.java";
//static final String SOURCE = "D:/dewan_backup/Java/grail13/src";
//static final String SOURCE = "D:/dewan_backup/Java/grail13/src/main/Assignment1.java";

//static final String SOURCE = "D:/dewan_backup/Java/grail13/src/shapes/AMagnifiablePolarLine.java";
//static final String SOURCE = "D:/dewan_backup/Java/grail13/src/test/ATest.java";

//static final String SOURCE = "C:/Users/dewan/Downloads/80135216/A1/src";

//static final String SOURCE = "D:/dewan_backup/Java/grail13/src/shapes";

//static final String SOURCE = "D:/dewan_backup/Java/grail13/src/";

//static final String SOURCE = "D:/dewan_backup/Java/grail13/src/shapes/APopulatedQuestGorge.java";
//static final String SOURCE = "D:/dewan_backup/Java/grail13/src/shapes/AShape.java";




  
//static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/UNC_Checkstyle_8/unc_checks.xml";
//public static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/UNC_Checkstyle_8/checks/533-s21/unc_checks_533_A1.xml";
//public static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/UNC_Checkstyle_8/checks/533-s21/unc_checks_533_A7.xml";

//static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/UNC_Checkstyle_8/checks/533-s21/unc_checks_533_A2.xml";

//static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/UNC_Checkstyle_8/checks/533-s21/unc_checks_533_A3.xml";

//static final String SOURCE = "D:/dewan_backup/Java/PLProjs/PLProjsJava/src/";
//static final String SOURCE = "C:/Users/dewan/Downloads/Comp533Assignments/S20/Assignment 1/Wortas, Andrew(ajwortas)/Submission attachment(s)/Assignment1 - Andrew Wortas/Assignment1/src";
//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Andrew533Assignment1/src";
//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Andrew533Assignment3/src";
//public static final String SOURCE = "D:/dewan_backup/Java/Comp533/Andrew533Assignment4/src";
//public static final String SOURCE = "D:/dewan_backup/Java/Comp533/Andrew533Assignment7/src";

//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Ram533Assignment7/src";

//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Ram533Assignment6/src";

//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Ram533Assignment5/src";

//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Ram533Assignment4/src";

//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Ram533Assignment3/src";

//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Ram533Assignment2/src";


//static final String SOURCE = "D:/dewan_backup/Java/Comp533/Ram533Assignment1/src";


//static final String CHECKSTYLE_CONFIGURATION = "testChecks/class_decomposition_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "testChecks/class_interfaces_checks.xml";



//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE};
//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE, SOURCE2};


	public static void main (String[] args) {
    String[] anActualArgs = args.length == 0?ARGS:args;
		setPrintOnlyTaggedClasses(true);
		setRedirectSecondPassOutput(true);
		PostProcessingMain.setGenerateChecks(true);
		PostProcessingMain.setSecondPassFile(new File("myresults.txt"));
    PostProcessingMain.setGeneratedChecksFile(new File("mychecks.xml"));

		PostProcessingMain.main(anActualArgs);
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
