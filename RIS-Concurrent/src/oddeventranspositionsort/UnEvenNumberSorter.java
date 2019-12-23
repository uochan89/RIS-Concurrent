package oddeventranspositionsort;

import java.util.concurrent.CountDownLatch;

class UnEvenNumberSorter implements Runnable{

	private static int[] data;
	private static int threadCount;
	private int threadId;
	private static Boolean ifPhaseUnEvenTransported;
	private static CountDownLatch latch;
	
	UnEvenNumberSorter(int[] data, int threadCount, int threadId, Boolean ifPhaseUnEvenTransported, CountDownLatch latch) {
		UnEvenNumberSorter.data = data;
		UnEvenNumberSorter.threadCount = threadCount;
		this.threadId = threadId;
		UnEvenNumberSorter.latch = latch;
		UnEvenNumberSorter.ifPhaseUnEvenTransported = ifPhaseUnEvenTransported;
	}
	
	@Override
	public void run() {
		for(int i = data.length/threadCount*threadId+1; i+1 < data.length/threadCount*(threadId+1)+1; i+=2) {
			if(data[i] > data[i+1]) {
				int temp = data[i+1];
				data[i+1] = data[i];
				data[i] = temp;
				ifPhaseUnEvenTransported = true;
			}
		}
		latch.countDown();
	}
	
}