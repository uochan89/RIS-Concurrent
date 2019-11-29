package selectAlgorithm;

import java.util.Arrays;
import java.util.concurrent.Callable;

class FindMedians implements Runnable, Callable<Boolean> {
	
	private static long[] data;
	private static int q;
	private static long[] medians;
	private int chunkIndex;
	private static int chunkCount;
	
	FindMedians(long[] data, int q, long[] medians, int chunkIndex, int chunkCount) {
		FindMedians.data = data;
		FindMedians.q = q;
		FindMedians.medians = medians;
		this.chunkIndex = chunkIndex;
		FindMedians.chunkCount = chunkCount;
	}
	
	@Override
	public void run() {
		int from = chunkIndex*q;
		int to = (chunkIndex+1)*q;
		//最後のチャンクはmaxまで大きさがあるとは限らないので帳尻を合わせるが毎回if判定が実行されるのが無駄
		if(chunkIndex == chunkCount - 1) to = data.length;
		medians[chunkIndex] = getMedian(Arrays.copyOfRange(data, from, to));; 
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
