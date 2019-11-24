package selectAlgorithm;

import java.util.Arrays;
import java.util.concurrent.Callable;

class FindMedians implements Runnable, Callable<Boolean>{
	
	static private long[] data;
	static private int q;
	static private long[] medians;
	private int chunkIndex;
	private static int chunkCount;
	
	FindMedians(long[] data, int q, long[] medians, int chunkIndex) {
		FindMedians.data = data;
		FindMedians.q = q;
		FindMedians.medians = medians;
		this.chunkIndex = chunkIndex;
		if(data.length%q == 0) {
			FindMedians.chunkCount = data.length/q;
		}else {
			FindMedians.chunkCount = data.length/q + 1;
		}
	}
	
	@Override
	public void run() {
		int from = chunkIndex*q;
		int to = (chunkIndex+1)*q;
		//最後のチャンクはmaxまで大きさがあるとは限らないので帳尻を合わせるが毎回if判定が実行されるのが無駄
		if(chunkIndex == chunkCount - 1) to = data.length;
		medians[chunkIndex] = getMedian(Arrays.copyOfRange(data, from, to));
	}
	
	private long getMedian(long[] data) {
		int medianIndex = data.length/2;
		long[] clonedData = data.clone();
		Arrays.sort(clonedData);
		return clonedData[medianIndex];
	}

	@Override
	public Boolean call() throws Exception {
		return new Boolean(true);
	}
}
