package prefixscanner.parrarel;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.DataReader;

public class ParallelPrefix{

	//ここのstaticはなんも考えていない。
	private int threadCount;
	private ExecutorService executorService;
	
	public ParallelPrefix(ExecutorService executorService, int threadCount) {
		this.executorService = executorService;
		this.threadCount = threadCount;
	}
	
	public static void main(String[] args) {
		long[] data = DataReader.readDataInLong("src/unsorted_nums.txt");
		int threadCount = 6;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		ParallelPrefix prifixer = new ParallelPrefix(executorService, threadCount);
		prifixer.preFix(data);
		System.out.println(Arrays.toString(data));
		executorService.shutdown();
	}
	
	public void preFix(long[] data) {
		long[] prefixResults = new long[threadCount];
		CountDownLatch latch = new CountDownLatch(threadCount);
		for(int chunkIndex = 0; chunkIndex < threadCount; chunkIndex++) {
			ChunkPrefixer prefixer = new ChunkPrefixer(data, threadCount, chunkIndex, prefixResults, latch);
			executorService.execute(prefixer);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long[] step1Result = getExclusivePrefixSum(prefixResults);
		
		//このあと足し算して分散
		latch = new CountDownLatch(threadCount);
		for(int i = 0; i < threadCount; i++) {
			AddValue counter = new AddValue(data, threadCount, step1Result, i, latch);
			executorService.execute(counter);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static long[] getExclusivePrefixSum(long[] data) {
		long[] output = new long[data.length];
		output[0] = 0;
		for(int i = 0; i < data.length - 1; i++) {
			output[i+1] = data[i] + output[i];
		}
		return output;
	}
	
}