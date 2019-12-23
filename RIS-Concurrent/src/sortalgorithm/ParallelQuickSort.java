package sortalgorithm;

import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import util.DataReader;

public class ParallelQuickSort {

	private int threadCount;
	static private ExecutorService executorService;
	private CountDownLatch latch;
	private Semaphore semaphore = new Semaphore(0);
	private BlockingDeque<int[]> indexQueue = new LinkedBlockingDeque<int[]>();

	public ParallelQuickSort(int threadCount, ExecutorService executorService) {
		this.threadCount = threadCount;
		latch = new CountDownLatch(threadCount); //wait and notifyで十分？
	}
	
	public static void main(String[] args) {
		//long[] data = DataReader.readData2("src/unsorted_nums.txt");
        int[] data = {4325,43,5234,7,6584,7656,245,36847,8,7536425,1425673,586,742,312,567,654,7545,31542657,647687,5342,467};
        int threadCount = 2;
		executorService = Executors.newFixedThreadPool(threadCount);
        ParallelQuickSort sorter = new ParallelQuickSort(threadCount, executorService);
        System.out.println(Arrays.toString(data));
        sorter.sort(data);
        System.out.println(Arrays.toString(data));
        executorService.shutdown();
    }
	
	public void sort(int[] data) {
		//prepare workers
		AtomicInteger sortedCounter = new AtomicInteger(data.length);
		for (int i = 0; i < threadCount; i++) {
			Worker worker = new Worker(data, sortedCounter, semaphore, indexQueue, latch);
			executorService.execute(worker);
		}
		
		//throw task in queue
		int[] initial_index = {0, data.length - 1};
		indexQueue.push(initial_index);
		semaphore.release();
		
		//wait for workers to finish
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Worker implements Runnable {
	
	static private Semaphore semaphore;
	static private BlockingDeque<int[]> indexQueue;
	static private int[] data;
	static private AtomicInteger sortedCounter;
	final static private int[] FLAG_INDEX = {-1,1};
	static private CountDownLatch latch;
	
	Worker(int[] unsorted_data, AtomicInteger sortedCounter, Semaphore semaphore, BlockingDeque<int[]> indexQueue, CountDownLatch latch) {
		Worker.indexQueue = indexQueue;
		Worker.semaphore = semaphore;
		Worker.sortedCounter = sortedCounter;
		Worker.latch = latch;
		Worker.data = unsorted_data;
	}
	
	@Override
	public void run() {
		while(sortedCounter.get() > 0) {
			System.out.println("count");
			System.out.println(sortedCounter.get());
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int[] sort_index = indexQueue.pop();
			System.out.println(Arrays.toString(sort_index));
			int left = sort_index[0];
			int right = sort_index[1];
			
			//区間がソート済みである場合
			if(left >= right) {
				sortedCounter.decrementAndGet();
				System.out.println("data abandoned");
				continue;
			}else {
				//partisionを設定し、新しい区間を求める
				int partition = data[(left+right)/2];
		        int l = left, r = right;
		        int tmp;
		        
		        //ここのアルゴリズムかdecrementするタイミング、新たに晶出する区間の定義が曖昧なので、
		        //couterが0になってもソートが終わっていない。
		        while(l < r) {
		            while(data[l] < partition) { l++; }
		            while(data[r] > partition) { r--; }
		            if (l<=r) {
		                tmp = data[l]; data[l] = data[r]; data[r] = tmp;
		                l++; r--;
		            }
		        }	        
		        sortedCounter.decrementAndGet();
		       
		        //新たに生成した区間のindexをqueueにpush
		        int[] right_index = {l, right};
		        int[] left_index = {left, r};
		        indexQueue.push(right_index);
		        indexQueue.push(left_index);
				semaphore.release();
				semaphore.release();
			}
		}
		latch.countDown();
	}
}