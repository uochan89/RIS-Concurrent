package prefixscanner.parrarel;

import java.util.concurrent.Callable;

class AddValue implements Runnable, Callable<Boolean>{
	
	private static int[] data;
	private static int threadCount;
	private static int[] tailOfChunks;
	private static int stride;
	private int threadId;
	
	AddValue(int[] data, int threadCount, int[] tailOfChunks, int threadId){
		AddValue.data = data;
		AddValue.threadCount = threadCount;
		this.threadId = threadId;
		AddValue.tailOfChunks = tailOfChunks;
		stride = data.length/threadCount;
	}
	
	@Override
	public void run() {
		int start = threadId*stride;
		int end = (threadId+1)*stride;
		if(threadId == threadCount - 1) end = data.length;
		for(int i = start; i < end; i++) {
			data[i] += tailOfChunks[threadId];
		}
	}

	@Override
	public Boolean call() throws Exception {
		return new Boolean(true);
	}
	
}
