package prefixscanner.parrarel;

import java.util.concurrent.CountDownLatch;

class ChunkPrefixer implements Runnable{

	private static long[] data;
	private static long[] prefixResults;
	private static int threadCount;
	private int threadId;
	private static CountDownLatch latch;
	
	ChunkPrefixer(long[] data, int threadCount, int threadId, long[] prefixResults, CountDownLatch latch){
		ChunkPrefixer.data = data;
		ChunkPrefixer.threadCount = threadCount;
		this.threadId = threadId;
		ChunkPrefixer.prefixResults = prefixResults;
		ChunkPrefixer.latch = latch;
	}

	@Override
	public void run() {
		int stride = data.length/threadCount;
		int start = threadId*stride;
		int end = (threadId+1)*stride;
		//ちなみにこの実装だと最後のスレッドにひっぱられる可能性がある
		//おそらく最後のスレッドにおしつけるのではなく均等にdistributeしたほうが効率がいいはず。
		if(threadId == threadCount - 1) end = data.length;
		//ここのスレッドのつなぎが間違っている奇数だとだめだったりするのでは
		for(int i = start+1; i < end; i++) {
			data[i] = data[i-1] + data[i];
		}
		prefixResults[threadId] = data[end-1];
		latch.countDown();
	}
	
}