package selectAlgorithm;

import java.util.concurrent.Callable;

public class ArrayPack implements Runnable, Callable<Boolean> {

	private static int[] preFixedChunkTypes;
	private static long[] newPack;
	private int chunkIndex;
	private static int q;
	
	@Override
	public Boolean call() throws Exception {
		return new Boolean(true);
	}

	@Override
	public void run() {
		int from = chunkIndex*q;
		int to = (chunkIndex+1)*q;
		for(int i = from; i < to; i++) {
			
		}
	}

}
