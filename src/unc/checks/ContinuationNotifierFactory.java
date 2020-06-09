package unc.checks;

public class ContinuationNotifierFactory {
	static ContinuationNotifier singleton;
	public static ContinuationNotifier getOrCreateSingleton() {
		if (singleton == null) {
			singleton = new AContinuationNotifier();
		}
		return singleton;
	}

}
