package prefixscanner.parrarel;

import java.util.concurrent.Callable;

class ChunkPrefixer implements Runnable, Callable<Boolean>{

	private static int[] data;
	private static int[] chunkData;
	private static int threadCount;
	private int threadId;
	
	ChunkPrefixer(int[] data, int threadCount, int threadId, int[] chunkData){
		ChunkPrefixer.data = data;
		ChunkPrefixer.threadCount = threadCount;
		this.threadId = threadId;
		ChunkPrefixer.chunkData = chunkData;
	}
	
	@Override
	public Boolean call() throws Exception {
		return new Boolean(true);
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
		chunkData[threadId] = data[end-1];
	}
	
}