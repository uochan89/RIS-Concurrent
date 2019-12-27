package sortalgorithm;

import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.org.apache.bcel.internal.generic.NEW;

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
		//int[] data = DataReader.readDataInInt("src/unsorted_nums.txt");
        int[] data = {8,3,5,8,2,5,5,8,7,4,7,9,3,5,1,23,4,6,87,9,45,5,7,4,87,4,56,7,9,8,6,6,43,3,7,98,4,67,5};
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
	static private CountDownLatch latch;
	static private Object lock = new Object();
	
	Worker(int[] unsorted_data, AtomicInteger sortedCounter, Semaphore semaphore, BlockingDeque<int[]> indexQueue, CountDownLatch latch) {
		Worker.indexQueue = indexQueue;
		Worker.semaphore = semaphore;
		Worker.sortedCounter = sortedCounter;
		Worker.latch = latch;
		Worker.data = unsorted_data;
	}
	
	@Override
	public void run() {
		//while分を評価している時は正しいけど、sysoutを出力するときには正しくない値になってしまっている。
		while(true) {
			//ここの処理がatomicでないといけない
			synchronized (lock) {
				System.out.println("count");
				System.out.println(sortedCounter.get());
				if(sortedCounter.get() <= 0) break;
				sortedCounter.decrementAndGet();
			}
			
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//わざわざセマフォを使うのはからループをやめたいからだっけ？
			//それとも同期とかの関係で必要なのだっけ？
			int[] sort_index = indexQueue.pop();
			System.out.println(Arrays.toString(sort_index));
			int left = sort_index[0];
			int right = sort_index[1];
			
			
			//区間がソート済みでない場合
			if(left < right){
				//partisionを設定し、新しい区間を求める
				//結局はこのパーティションの処理があやしい陽数
				int pivotIndex = partition(left, right);
				synchronized (lock) {
					sortedCounter.decrementAndGet();
				}
		        //新たに生成した区間のindexをqueueにpush
		        int[] right_index = {pivotIndex, right};
		        int[] left_index = {left, pivotIndex-1};
		        indexQueue.push(right_index);
		        indexQueue.push(left_index);
				semaphore.release();
				semaphore.release();
			}
		}
		latch.countDown();
	}
	
	private static int partition(int left, int right) {
		//partisionを設定し、新しい区間を求める
		int pivotIndex = (left+right)/2;
		int pivot = data[pivotIndex];
        int tmp;
        
        //ここのアルゴリズムかdecrementするタイミング、新たに晶出する区間の定義が曖昧なので、
        //couterが0になってもソートが終わっていない。
        while(left < right) {
            while(data[left] <= pivot) { left++; }
            while(data[right] > pivot) { right--; }
            if (left<=right) {
                tmp = data[left]; data[left] = data[right]; data[right] = tmp;
                left++; right--;
            }
        }
        
        return right;
	}
}