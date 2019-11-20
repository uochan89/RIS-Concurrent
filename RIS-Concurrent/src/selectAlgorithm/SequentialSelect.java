package selectAlgorithm;

import java.util.Arrays;


import util.DataReader;

public class SequentialSelect {

	private int q;
	private long[] data;
	private int index;
	
	public SequentialSelect(int q, long[] data, int index) {
		this.q = q;
		this.data = data;
		this.index = index;
	}
	
	public static void main(String[] args) {
		//long[] data = DataReader.readData("src/unsorted_nums.txt");
		long[] data = {431234,123412,34123,41234,1234,1234123,41235,3453,6257,24745,6724572,457245,72457,24724,57245,724573,47345,73457,3457,4571,41543,53451,476456,734567,45674567,345345};
		int index = 9;
		SequentialSelect select = new SequentialSelect(5, data, index);
		select.select();
	}
	
	public void select() {
		int chunkCount = 0;
		//just return normally
		//おそらくdataの参照を再帰的に修正していく必要がある
		if(data.length < q) {
			Arrays.sort(data);
			//return data[index];
		}
		
		//get median from each chunk
		if(data.length%q == 0) {
			chunkCount = data.length/q;
		}else {
			chunkCount = data.length/q + 1;
		}
		long[] medians = new long[chunkCount];
		for(int i = 0; i < chunkCount; i++) {
			int from = i;
			int to = i + q;
			//最後のチャンクはmaxまで大きさがあるとは限らないので帳尻を合わせる
			if(i == chunkCount - 1) to = data.length;
			medians[i] = getMedian(Arrays.copyOfRange(data, from, to));
		}
		
	}
	
	private long getMedian(long[] data) {
		int medianIndex = data.length/2;
		//引数のdataの参照先のポインタは変更していないので、メソッドの呼び出し側には影響はないはず
		Arrays.sort(data);
		return data[medianIndex];
	}

}
