package selectAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import util.DataReader;

public class ParrarelSelect {

	//これって私のローカル環境では結局チャンクを別個のコアで動かすことはできないから並行処理にしかならんわけで、そうなった場合にスレッドを生成するOHが無駄になるんではないでしょうか。
	private int q = 5;
	private ExecutorService executorService;
	private final static int LESS_THEN_Q = 0;
	private final static int EQUAL_TO_Q = 1;
	private final static int LARGER_THEN_Q = 2;
	
	public ParrarelSelect(int threadCount) {
		executorService = Executors.newFixedThreadPool(threadCount);
	}
	
	public static void main(String[] args) {
		long[] data = DataReader.readData("src/unsorted_nums.txt"); 
		int targetIndex = 23;
		int threadCount = 2;
		ParrarelSelect select = new ParrarelSelect(threadCount);
		long result = select.select(data, targetIndex-1);
		System.out.println(result);
	}
	
	public long select(long[] data, int targetIndex) {
		while(true) {
			if(data.length < q) {
				Arrays.sort(data);
				return data[targetIndex];
			}
			
			//get median from each chunk
			int chunkCount = 0;
			if(data.length%q == 0) {
				chunkCount = data.length/q;
			}else {
				chunkCount = data.length/q + 1;
			}
			long[] medians = new long[chunkCount];
			List<Callable<Boolean>> tasks1 = new ArrayList<Callable<Boolean>>();
			//ここで毎回chunkIndexを与えるためにインスタンスを生成しているけどもっと効率的なやり方があるはず。
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				FindMedians finder = new FindMedians(data, chunkCount, medians, chunkIndex);
				tasks1.add(finder);
			}
			try{
	            List<Future<Boolean>> futures = executorService.invokeAll(tasks1);  
	        } catch(Exception err){
	            err.printStackTrace();
	        }
			
			//get medians of medians sequentially
			long medianOfMedians = getMedian(medians);
			
			//classify each element based on medianOfMedians
			int[] typeCounter = {0, 0, 0};
			int[] chunkTypes = new int[data.length];
			List<Callable<Boolean>> tasks2 = new ArrayList<Callable<Boolean>>();
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				CountAndMark counter = new CountAndMark(medians, typeCounter, chunkTypes, medianOfMedians, chunkIndex, q);
				tasks2.add(counter);
			}
			try{
	            List<Future<Boolean>> futures2 = executorService.invokeAll(tasks2);  
	        } catch(Exception err){
	            err.printStackTrace();
	        }
			
			//retrieve element with particular type
			if(typeCounter[0] > targetIndex) {
				//プリフィックススキャンを利用したarrayPackを行う。
				data = retrieveTargetElements(data, chunkTypes, LESS_THEN_Q);
			}else if((typeCounter[0] + typeCounter[1]) > targetIndex) {
				return medianOfMedians;
			}else {
				//プリフィックススキャンを利用したarrayPackを行う。
				data = retrieveTargetElements(data, chunkTypes, LARGER_THEN_Q);
				targetIndex = targetIndex - (typeCounter[0] + typeCounter[1]);
			}
		}
	}
	
	private long[] retrieveTargetElements(long[] data, int[] chunkTypes, int targetType) {
		//TODO:このあたりの処理をjavaでどうかけばいいのかわからない
		
		List<Long> retrivedData = new ArrayList<Long>();
		int j = 0;
		for(int i = 0; i < data.length; i++) {
			if(chunkTypes[i] == targetType) {
				retrivedData.add(data[i]);
			}
		}
		
		//change type
		long[] output = new long[retrivedData.size()];
		j = 0;
		for(Object i : retrivedData) {
			output[j++] = (long) i;
		}
		return output;
	}
	
	private long getMedian(long[] data) {
		int medianIndex = data.length/2;
		long[] clonedData = data.clone();
		Arrays.sort(clonedData);
		return clonedData[medianIndex];
	}

}
