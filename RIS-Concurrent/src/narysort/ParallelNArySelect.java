package narysort;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ParallelNArySelect {

	private int threadCount = 2;
	private int chunkCount = 10;
	private Executor executor;
	private CountDownLatch latch  = new CountDownLatch(threadCount);
	
	public ParallelNArySelect(Executor executor) {
		//これって自前でもってもええな・・・けっこうロジックに依存してるんではなかろうか？
		this.executor = executor;
	}
	
	public static void main(String[] args) {
		int[] data = {423,42,34,234,2,654,635,27,463,46385,746,236,7624,63,676245,5473,662,45256,35,251,426,73664,25};
		Arrays.sort(data);
		System.out.println(Arrays.toString(data));
		
		Executor executor = Executors.newFixedThreadPool(2);
		ParallelNArySelect pSorter = new ParallelNArySelect(executor);
		int clue = pSorter.search(data,25);
		System.out.println("answer : " + clue);
	}
	
	public int search(int[] data, int key) {
		int start = 0;
		int end = data.length;
		Answer answer = new Answer();
		int [] dividientIndex = new int[chunkCount];
		int[] dividientType = new int[chunkCount];
		while(start<end) {
			System.out.println("############");
			for(int i=0; i < chunkCount-1; i++) {
				//ここの割り算の結果が少数切り捨てにしてるからこうなるんだ。
				dividientIndex[i] = start + (end-start)/(chunkCount-1)*i;
			}
			dividientIndex[chunkCount-1] = end-1;
			System.out.println(Arrays.toString(dividientIndex));
			
			for(int threadId=0; threadId < threadCount; threadId++) {
				Sorter sorter = new Sorter(dividientIndex, threadId, chunkCount, data, dividientType, key, latch, threadCount, answer);
				executor.execute(sorter);
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(answer.index != -1) {
				break;
			}
			//dividientは固定長にしてるけど全要素ちゃんと更新されていく？
			System.out.println(Arrays.toString(dividientType));
			//-1, 1の切り替え点を次のstart endとする
			//最後に切り替え点があった場合大丈夫？
			for(int i=0; i < chunkCount-1; i++) {
				if(dividientType[i] == 1 && dividientType[i+1] == -1) {
					start = dividientIndex[i]+1;
					end = dividientIndex[i+1];
					break;
				}
			}
			latch = new CountDownLatch(threadCount);
		}
		return answer.index;
	}
}

class Sorter implements Runnable{
	
	private static int[] dividientIndex;
	private static int chunkCount;
	private static int[] data;
	private static int[] dividientType;
	private static CountDownLatch latch;
	private static int key;
	private int threadId;
	private static int threadCount;
	private static Answer answer;
	
	public Sorter(int[] dividientIndex,int threadId, int chunkCount, int[] data, int[] dividientType, int key, CountDownLatch latch, int threadCount, Answer answer) {
		Sorter.dividientIndex = dividientIndex;
		Sorter.chunkCount = chunkCount;
		Sorter.data = data;
		Sorter.dividientType = dividientType;
		Sorter.key = key;
		Sorter.latch = latch;
		this.threadId = threadId;
		Sorter.threadCount = threadCount;
		Sorter.answer = answer;
	}
	
	@Override
	public void run() {
		System.out.println("looping");
		int end = chunkCount/threadCount*(threadId+1);
		if(threadId == threadCount-1) end = chunkCount;
		for(int i=0; i < end; i++) {
			if(data[dividientIndex[i]] == key) {
				answer.index = dividientIndex[i];
				break;
			}else if (data[dividientIndex[i]] > key) {
				dividientType[i] = -1;
			}else if (data[dividientIndex[i]] < key) {
				dividientType[i] = 1;
			}
		}
		latch.countDown();
	}
}

class Answer{
	
	int index = -1;
}
