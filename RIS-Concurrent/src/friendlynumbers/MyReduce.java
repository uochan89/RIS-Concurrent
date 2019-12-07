package friendlynumbers;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MyReduce implements Runnable{

	private static int[][] abundancies;
	private static List<int[]> pairs;
	private int threadId;
	private static int threadCount;
	private static CountDownLatch latch;
	
	MyReduce(int[][] abundancies, List<int[]> pairs, int threadId, CountDownLatch latch, int threadCount){
		MyReduce.abundancies = abundancies;
		MyReduce.pairs = pairs;
		this.threadId = threadId;
		MyReduce.latch = latch;
		MyReduce.threadCount = threadCount;
	}
	
	@Override
	public void run() {
		for(int i = threadId; i < abundancies.length-1; i+=threadCount) {
			System.out.println(i);
			for(int j = i+1 ; j < abundancies.length; j++) {
				if(abundancies[i][0] == abundancies[j][0] && abundancies[i][1] == abundancies[j][1]) {
					int[] pair = {abundancies[i][2], abundancies[j][2]};
					pairs.add(pair);
				}
			}
		}
		latch.countDown();
		System.out.println("finished");
	}
}
