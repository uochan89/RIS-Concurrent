package selectAlgorithm;

import java.util.concurrent.Callable;

public class ArrayPack implements Runnable, Callable<Boolean> {

	@Override
	public Boolean call() throws Exception {
		return new Boolean(true);
	}

	@Override
	public void run() {
		
	}

}
