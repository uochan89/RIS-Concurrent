package selectAlgorithm;

import java.util.concurrent.Callable;

public class PackingMove implements Runnable, Callable<Boolean> {

	private int chunkIndex;
	private static int q;
	private static long[] data;
	private static long[] newPack;
	private static int[] dataMark;
	
	public PackingMove(int chunkIndex, int q, long[] data, long[] newPack, int[] dataMark) {
		this.chunkIndex = chunkIndex;
		PackingMove.q = q;
		PackingMove.data = data;
		PackingMove.newPack = newPack;
		PackingMove.dataMark = dataMark;
	}
	
	@Override
	public Boolean call() throws Exception {
		return new Boolean(true);
	}

	@Override
	public void run() {
		int from = chunkIndex*q;
		int to = (chunkIndex+1)*q;
		if(from == 0) {
			from = 1;
			newPack[0] = data[0];
		}
		for(int i = from; i < to; i++) {
			if(dataMark[i] != dataMark[i-1]) {
				newPack[dataMark[i-1]] = data[i];
			}
		}
	}

}
