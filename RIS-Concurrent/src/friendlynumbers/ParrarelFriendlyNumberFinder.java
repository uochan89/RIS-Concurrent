package friendlynumbers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParrarelFriendlyNumberFinder {

	
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		int threadCount = 6;
		int n = 200000;
		int[][] abundancies = new int[n][3];
		//別にスレッドが繰り返し何かの処理を実行するわけではないので、スレッドが待機するだけ時間が無駄になる。
		//countdownlatchをカウントダウンしたほうがいいかもね。
		CountDownLatch latch = new CountDownLatch(n-1);
		//正直これだとlatchの更新待ちによるOHが高いから、配列をスレッド数に割ってやるほうがいい。
		//こっちのほうが単位ごとで実装は簡単だけど、データ分割が細かすぎるのでOHが大きすぎるという基本の話になる
		//特に値が大きい方のabundancyを求める方が負荷が高いので、はじめに実装したようにスレッド数のとびとびで処理対象の数字定指定していく
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		for(int number = 1; number < n+1; number++) {
			MyMap map = new MyMap(abundancies, number, latch);
			executorService.submit(map);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("abundancy all calculated");
//		for(int[] abundancy : abundancies) {
//			System.out.println(Arrays.toString(abundancy));
//		}
		List<int[]> pairs = new ArrayList<int[]>();
		latch = new CountDownLatch(threadCount-1);
		for(int i = 0; i < threadCount; i++) {
			MyReduce reduce = new MyReduce(abundancies, pairs, i, latch, threadCount);
			executorService.submit(reduce);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int[] pair : pairs) {
			System.out.println(Arrays.toString(pair));
		}
		executorService.shutdown();
		System.out.println("all process finished");
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
