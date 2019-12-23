package oddeventranspositionsort;

import java.util.concurrent.CountDownLatch;

class EvenNumberSorter implements Runnable{

	private static int[] data;
	private static int threadCount;
	private int threadId;
	private static Boolean ifPhaseEvenTransported;
	private static CountDownLatch latch;
	
	EvenNumberSorter(int[] data, int threadCount, int threadId, Boolean ifPhaseEvenTransported, CountDownLatch latch) {
		EvenNumberSorter.data = data;
		EvenNumberSorter.threadCount = threadCount;
		this.threadId = threadId;
		EvenNumberSorter.ifPhaseEvenTransported = ifPhaseEvenTransported;
		EvenNumberSorter.latch = latch;
	}
	
	@Override
	public void run() {
		//このあたりの要素数の偶奇とスレッド数の偶奇の担当期浮かんの場合わけができていない。
		int dinmsum;
		if(data.length/threadCount%2 == 0) {
			dinmsum = data.length/threadCount;
		}else{
			dinmsum = data.length/threadCount+1;
		}
		int end = dinmsum*(threadId+1);
		if(threadId == threadCount-1) end = data.length;
		//threadIdによって並行処理したいので、sequentiallyに差分をたしていくことはできない。
		//しかも、chunkのindexによってはけつが偶数でおさまったりおさまらなかったりするので、それをどうコントロールするかの問題。
		//多分教科書に簡単なやりkタガ書いてあるはず。
		for(int i = data.length/threadCount*threadId; i+1 < end; i+=2) {
			if(data[i] > data[i+1]) {
				int temp = data[i+1];
				data[i+1] = data[i];
				data[i] = temp;
				ifPhaseEvenTransported = Boolean.TRUE;
			}
		}
		latch.countDown();
	}
}