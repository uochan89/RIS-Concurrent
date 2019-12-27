package parrarelSum;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.DataReader;

public class ParrarelSum {

	private int threadCount;
	private ExecutorService executorService;
	
	public static void main(String[] args) {
		long[] data = DataReader.readDataInLong("src/unsorted_nums.txt");
		ExecutorService executorService = Executors.newFixedThreadPool(6);
		ParrarelSum summer = new ParrarelSum(6, executorService);
		System.out.println(summer.getSum(data));
		executorService.shutdown();
	}
	
	public ParrarelSum(int threadCount, ExecutorService executorService) {
		this.threadCount = threadCount;
		this.executorService = executorService;
	}
	
	public long getSum(long[] data) {
		CountDownLatch latch = new CountDownLatch(threadCount);
		long[] results = new long[threadCount];
		for (int i = 0; i < threadCount; i++) {
			Counter counter = new Counter(data, results, i, threadCount, latch);
			executorService.execute(counter);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Arrays.stream(results).sum();
	}
	
}

class Counter implements Runnable{
	
	private static long[] data;
	private static long[] results;
	private int threadId;
	private static int threadCount;
	private static CountDownLatch latch;
	
	Counter(long[] data, long[] results, int threadId, int threadCount, CountDownLatch latch){
		Counter.data = data;
		Counter.results = results;
		this.threadId = threadId;
		Counter.threadCount = threadCount;
		Counter.latch = latch;
	}

	@Override
	public void run() {
		long sum = 0;
		for(int i = threadId; i < data.length; i+= threadCount) {
			sum += data[i];
		}
		results[threadId] = sum;
		latch.countDown();
	}
	
}