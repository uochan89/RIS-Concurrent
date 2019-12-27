package sortalgorithm;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.DataReader;

public class ParallelShellSort {

	private int threadCount;
	private ExecutorService executorService;
	
	public static void main(String[] args) {
		//long[] data = {9,8,3,7,3,6,5,9,8,2,5,7,4,8,5,6,4,5,6,7,4,3,2,1};
		long[] data = DataReader.readDataInLong("src/unsorted_nums.txt");
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		ParallelShellSort sorter = new ParallelShellSort(2, executorService);
		sorter.sort(data);
		System.out.println(Arrays.toString(data));
		executorService.shutdown();
	}
	
	public ParallelShellSort(int threadCount, ExecutorService executorService) {
		this.threadCount = threadCount;
		this.executorService = executorService;
	}
	
	public void sort(long[] data) {
		for(int h = threadCount; h > 1; h--) {
			CountDownLatch latch = new CountDownLatch(h-1);
			for(int threadId = 0; threadId < h; threadId++) {
				Chunk chunk = new Chunk(h, threadId, data, latch);
				executorService.execute(chunk);
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		shellSort(data);
	}
	
	private void shellSort(long[] data) {
		for(int i = 1; i < data.length; i++) {
			long value = data[i];
			for(int j = i-1; j >= 0; j--) {
				if(value >= data[j]) {
					data[j+1] = value;
					break;
				}else {
					data[j+1] = data[j];
					if(j == 0) {
						data[j] = value;
					}
				}
			}
		}
	}
	
}

class Chunk implements Runnable{

	private static int h;
	private int threadId;
	private static long[] data;
	private static CountDownLatch latch;
	
	public Chunk(int h, int threadId, long[] data, CountDownLatch latch) {
		Chunk.h = h;
		this.threadId = threadId;
		Chunk.data = data;
		Chunk.latch = latch;
	}
	
	@Override
	public void run() {
		for(int i = threadId+h; i < data.length; i+=h) {
			long value = data[i];
			for(int j = i-h; j >= threadId; j-=h) {
				if(value >= data[j]) {
					data[j+h] = value;
					break;
				}else {
					data[j+h] = data[j];
					if(j == threadId) {
						data[j] = value;
					}
				}
			}
		}
		latch.countDown();
	}
	
}
