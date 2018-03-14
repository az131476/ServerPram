package us;

public class OutParamters {
	private static boolean equiptmentInit;
	private static boolean equiptmentFree;
	private static boolean lockErrStock;

	public static boolean isEquiptmentInit() {
		return equiptmentInit;
	}

	public static void setEquiptmentInit(boolean equiptmentInit) {
		OutParamters.equiptmentInit = equiptmentInit;
	}

	public static boolean isEquiptmentFree() {
		return equiptmentFree;
	}

	public static void setEquiptmentFree(boolean equiptmentFree) {
		OutParamters.equiptmentFree = equiptmentFree;
	}

	public static boolean isLockErrStock() {
		return lockErrStock;
	}

	public static void setLockErrStock(boolean lockErrStock) {
		OutParamters.lockErrStock = lockErrStock;
	}
	
}
