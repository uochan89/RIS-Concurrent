package prefixscanner.parrarel;

import java.util.concurrent.CountDownLatch;

class AddValue implements Runnable {
	
	private static long[] data;
	private static int threadCount;
	private static long[] tailOfChunks;
	private static int stride;
	private int threadId;
	private static CountDownLatch latch;
	
	AddValue(long[] data, int threadCount, long[] tailOfChunks, int threadId, CountDownLatch latch){
		AddValue.data = data;
		AddValue.threadCount = threadCount;
		this.threadId = threadId;
		AddValue.tailOfChunks = tailOfChunks;
		stride = data.length/threadCount;
		AddValue.latch = latch;
	}
	
	@Override
	public void run() {
		int start = threadId*stride;
		int end = (threadId+1)*stride;
		if(threadId == threadCount - 1) end = data.length;
		for(int i = start; i < end; i++) {
			data[i] += tailOfChunks[threadId];
		}
		latch.countDown();
	}
	
}
