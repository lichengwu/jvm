package oliver.jvm.util;

import java.util.concurrent.TimeUnit;

import sun.dc.pr.PathDasher;

/**
 * Crash the JVM for some special test.
 * 
 * @author lichengwu
 * @created 2012-6-3
 * 
 * @version 1.0
 */
public class CrashJVM {
	public static void main(String[] args) {
		Thread thread = new Thread(new Runnable() {
			// new a thread
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.setName("oliver");
		thread.start();
		// crash jvm
		@SuppressWarnings("unused")
		PathDasher dasher = new PathDasher(null);

	}
}