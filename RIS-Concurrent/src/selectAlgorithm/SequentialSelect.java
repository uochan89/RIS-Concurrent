package selectAlgorithm;

import java.util.Arrays;
import java.util.Stack;

public class SequentialSelect {

	private int q;
	private final static int LESS_THEN_Q = 0;
	private final static int EQUAL_TO_Q = 1;
	private final static int LARGER_THEN_Q = 2;
	
	public SequentialSelect(int q) {
		this.q = q;
	}
	
	public static void main(String[] args) {
		//long[] data = DataReader.readData("src/unsorted_nums.txt");
		long[] data = {431234,123412,34123,41234,1234,1234123,41235,3453,6257,24745,6724572,457245,72457,24724,57245,724573,47345,73457,3457,4571,41543,53451,476456,734567,45674567,345345};
//		Arrays.sort(data);
//		System.out.println(data[9]);
		int targetIndex = 1;
		SequentialSelect select = new SequentialSelect(5);
		System.out.println(select.select2(data, targetIndex));
	}
	
	public long select2(long[] data, int targetIndex) {
		while(true) {
			System.out.println("ITS A BRAND NEW DAY");
			
			if(data.length < q) {
				Arrays.sort(data);
				return data[targetIndex];
			}
			
			int chunkCount = 0;
			System.out.println("data size : " + data.length);
			System.out.println("targetIndex : " + targetIndex);
			System.out.println("chunk size : " + q);
			
			//get median from each chunk
			if(data.length%q == 0) {
				chunkCount = data.length/q;
			}else {
				chunkCount = data.length/q + 1;
			}
			long[] medians = new long[chunkCount];
			System.out.println("chunkCount : " + chunkCount);
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				int from = chunkIndex*q;
				int to = (chunkIndex+1)*q;
				System.out.println("from : " + from);
				System.out.println("to : " + to);
				//最後のチャンクはmaxまで大きさがあるとは限らないので帳尻を合わせるが毎回if判定が実行されるのが無駄
				if(chunkIndex == chunkCount - 1) to = data.length;
				medians[chunkIndex] = getMedian(Arrays.copyOfRange(data, from, to));
			}
			
			//classify based on median
			long medianOfMedians = getMedian(medians);
			int[] chunkTypeCounter = {0, 0, 0};
			int[] chunkTypes = new int[chunkCount];
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				if(medians[chunkIndex] < medianOfMedians) {
					chunkTypeCounter[0]++;
					chunkTypes[chunkIndex] = LESS_THEN_Q;
				}else if(medians[chunkIndex] == medianOfMedians) {
					chunkTypeCounter[1]++;
					chunkTypes[chunkIndex] = EQUAL_TO_Q;
				}else{
					chunkTypeCounter[2]++;
					chunkTypes[chunkIndex] = LARGER_THEN_Q;
				}
			}
			System.out.println("chunkTypeCounter : " + chunkTypeCounter.toString());
			
			//compose new target data
			if(chunkTypeCounter[0]*q > targetIndex) {
				data = composeSingleData(data, q, chunkTypes, LESS_THEN_Q);
			}else if((chunkTypeCounter[0] + chunkTypeCounter[1])*q > targetIndex) {
				return medianOfMedians;
			}else {
				data = composeSingleData(data, q, chunkTypes, LARGER_THEN_Q);
				targetIndex = targetIndex - 5*(chunkTypeCounter[0] + chunkTypeCounter[1]);
			}
		}
	}

	private long[] composeSingleData(long[] data, int chunkLength, int[] chunkTypes, int targetType) {
		System.out.println("##############################");
		System.out.println("targetType : " + targetType);
		//多分これも引数に値を入れたらメモリを節約できる。たぶん実際に値を入れるまでうんぬんだけど。
		Stack<long[]> dataStack = new Stack<long[]>();
		int chunkCount = data.length/chunkLength;
		int dataCount = 0;
		for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
			long[] miniData = null;
			if(chunkTypes[chunkIndex] == targetType) {
				int from = chunkIndex*q;
				int to = (chunkIndex+1)*q;
				miniData = new long[to-from];
				for(int index = from; index < to; index++) {
					miniData[index-from] = data[index];
					dataCount++;
				}
				dataStack.push(miniData);
			}
		}
		long[] output = new long[dataCount];
		int indexCount = 0;
		while(!dataStack.empty()) {
			long[] dat = dataStack.pop();
			for(int i = 0; i < dat.length; i++) {
				output[indexCount] = dat[i];
				indexCount++;
			}
		}
		System.out.println("偽造配列の長さ : " + output.length);
		return output;
	}
	
	private long getMedian(long[] data) {
		int medianIndex = data.length/2;
		Arrays.sort(data);
		return data[medianIndex];
	}

}
