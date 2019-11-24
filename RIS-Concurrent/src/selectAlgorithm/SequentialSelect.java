package selectAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.DataReader;

public class SequentialSelect {

	private int q = 5;
	private final static int LESS_THEN_Q = 0;
	private final static int EQUAL_TO_Q = 1;
	private final static int LARGER_THEN_Q = 2;
	
	public static void main(String[] args) {
		long[] data = DataReader.readData("src/unsorted_nums.txt"); 
		int targetIndex = 5678;
		SequentialSelect select = new SequentialSelect();
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
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				int from = chunkIndex*q;
				int to = (chunkIndex+1)*q;
				//最後のチャンクはmaxまで大きさがあるとは限らないので帳尻を合わせるが毎回if判定が実行されるのが無駄
				if(chunkIndex == chunkCount - 1) to = data.length;
				medians[chunkIndex] = getMedian(Arrays.copyOfRange(data, from, to));
			}
			
			//classify each element based on medianOfMedians
			long medianOfMedians = getMedian(medians);
			int[] typeCounter = {0, 0, 0};
			int[] chunkTypes = new int[data.length];
			for(int i = 0; i < data.length; i++) {
				if(data[i] < medianOfMedians) {
					typeCounter[0]++;
					chunkTypes[i] = LESS_THEN_Q;
				}else if(data[i] == medianOfMedians) {
					typeCounter[1]++;
					chunkTypes[i] = EQUAL_TO_Q;
				}else{
					typeCounter[2]++;
					chunkTypes[i] = LARGER_THEN_Q;
				}
			}
			
			//retrieve element with particular type
			if(typeCounter[0] > targetIndex) {
				data = retrieveTargetElements(data, chunkTypes, LESS_THEN_Q);
			}else if((typeCounter[0] + typeCounter[1]) > targetIndex) {
				return medianOfMedians;
			}else {
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
