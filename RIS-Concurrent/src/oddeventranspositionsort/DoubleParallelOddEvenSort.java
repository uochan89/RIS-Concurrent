package oddeventranspositionsort;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DoubleParallelOddEvenSort {
	
	private int threadCount;
	private Executor executor;
	
	public DoubleParallelOddEvenSort(int threadCount, Executor executor) {
		this.threadCount = threadCount;
		this.executor = executor;
	}
	
	public static void main(String[] args) {
		int[] data = {532,45,34,523,52,345,2,52457,67,8467,847613,52,6236,564};
		System.out.println(Arrays.toString(data));
		int threadCount = 2;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		DoubleParallelOddEvenSort sorter = new DoubleParallelOddEvenSort(2, executorService);
		sorter.sort(data);
		System.out.println(Arrays.toString(data));
		executorService.shutdown();
	}
	
	public void sort(int[] data) {
		//まあ一番革新的なところはインスタンスごとに定義されるthreadIdをどうやってクラスを作る以外に渡すのか＝＞ない。javaの問題な気がする
		Boolean ifPhaseEvenTransported = true;
		Boolean ifPhaseUnEvenTransported = true;
		Boolean ifFirstLoop = false;
		
		while(ifPhaseUnEvenTransported) {
			System.out.println("phase1");
			ifPhaseEvenTransported = new Boolean(false);
			ifPhaseUnEvenTransported = new Boolean(false);
			
			CountDownLatch latch = new CountDownLatch(threadCount-1);
			for (int threadId = 0; threadId < threadCount; threadId++) {
				EvenNumberSorter chunker = new EvenNumberSorter(data, threadCount, threadId, ifPhaseEvenTransported, latch);
				executor.execute(chunker);
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Arrays.toString(data));
			
			if(ifPhaseEvenTransported == false || !ifFirstLoop) {
				System.out.println("phase2");
				latch = new CountDownLatch(threadCount-1);
				for (int threadId = 0; threadId < threadCount; threadId++) {
					UnEvenNumberSorter chunker = new UnEvenNumberSorter(data, threadCount, threadId, ifPhaseUnEvenTransported, latch);
					executor.execute(chunker);
				}
				ifFirstLoop = true;
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}