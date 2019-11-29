package selectAlgorithm;

import java.util.concurrent.Callable;

public class PackingScan implements Runnable, Callable<Boolean> {

	private static int[] chunkTypes;
	private int chunkIndex;
	private static int targetType;
	private static int q;
	private static int[] dataMark;
	
	public PackingScan(int chunkIndex, int[] chunkTypes, int targetType, int q, int[] dataMark) {
		this.chunkIndex = chunkIndex;
		PackingScan.chunkTypes = chunkTypes;
		PackingScan.targetType = targetType;
		PackingScan.q = q;
		PackingScan.dataMark = dataMark;
	}
	
	
	@Override
	public void run() {
		int from = chunkIndex*q;
		int to = (chunkIndex+1)*q;
		for(int i = from; i < to; i++) {
			if(chunkTypes[i] == targetType) {
				dataMark[i] = 1;
			} else {
				dataMark[i] = 0;
			}
		}
	}
	
	@Override
	public Boolean call() throws Exception {
		return new Boolean(true);
	}

}
