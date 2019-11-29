package selectAlgorithm;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class CountAndMark implements Runnable, Callable<Boolean>{

	private final static int LESS_THEN_Q = 0;
	private final static int EQUAL_TO_Q = 1;
	private final static int LARGER_THEN_Q = 2;
	//これがいわゆるコード値ってやつか・・・・可読性をあげるためには必要になるな・・・でもさすがに会社のやつは多すぎじゃね？
	
	private static long medianOfMedians;
	private static long[] data;
	private static int[] typeCounter;
	private static int[] chunkTypes;
	private int chunkIndex;
	private static int q;
	private static int chunkCount;
	
	CountAndMark(long[] data, int[] typeCounter, int[] chunkTypes, long medianOfMedians, int chunkIndex, int q, int chunkCount) {
		CountAndMark.data = data;
		CountAndMark.medianOfMedians = medianOfMedians;
		CountAndMark.typeCounter = typeCounter;
		CountAndMark.chunkTypes = chunkTypes;
		this.chunkIndex = chunkIndex;
		CountAndMark.q = q;
		CountAndMark.chunkCount = chunkCount;
	}
	
	@Override
	public Boolean call() throws Exception {
		return new Boolean(true);
	}

	@Override
	public void run() {
		//いろんな位置でこれを計算しているけどこれは定数のはず
		int from = chunkIndex*q;
		int to = (chunkIndex+1)*q;
		if(chunkIndex == chunkCount - 1) to = data.length;
		for(int i = from; i < to; i++) {
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
	}
	
}
