package unc.tools.checkstyle;

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
static final String SOURCE = "D:/dewan_backup/Java/PLProjs/PLProjsJava/src/safeSocialization/main/Hello.java";
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

//static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "unc_checks.xml";
static final String CHECKSTYLE_CONFIGURATION = "D:/dewan_backup/Java/UNC_Checkstyle_8/unc_checks.xml";

//static final String CHECKSTYLE_CONFIGURATION = "testChecks/class_decomposition_checks.xml";
//static final String CHECKSTYLE_CONFIGURATION = "testChecks/class_interfaces_checks.xml";



//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION, "-f", "xml", SOURCE};
static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE};
//static final String[] ARGS = {"-c", CHECKSTYLE_CONFIGURATION,  SOURCE, SOURCE2};


	public static void main (String[] args) {
		setPrintOnlyTaggedClasses(true);
		setRedirectSecondPassOutput(true);
		PostProcessingMain.setGenerateChecks(true);
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
