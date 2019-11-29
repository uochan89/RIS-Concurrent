package prefixscanner.parrarel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParrarelPrefix{

	//ここのstaticはなんも考えていない。
	private int threadCount;
	private ExecutorService executorService;
	private int[] data;
	
	public ParrarelPrefix(ExecutorService executorService, int threadCount, int[] data) {
		this.executorService = executorService;
		this.threadCount = threadCount;
		this.data = data;
	}
	
	public void preFix() {
		//今までlongの引数を扱っていたものでintの引数を扱いたい
		//threadIndex = chunkIndexどっちの変数うに統一するのが正しい？リーダブルコード？
		int[] chunkData = new int[threadCount];
		List<Callable<Boolean>> task = new ArrayList<Callable<Boolean>>();
		for(int chunkIndex = 0; chunkIndex < threadCount; chunkIndex++) {
			ChunkPrefixer prefixer = new ChunkPrefixer(data, threadCount, chunkIndex, chunkData);
			task.add(toCallable(prefixer));
		}	
		List<Future<Boolean>> future = null;
		try{
            future = executorService.invokeAll(task);  
        } catch(Exception err){
            err.printStackTrace();
        }
		int[] step1Result = getExclusivePrefixSum(chunkData);
		
		//このあと足し算して分散
		List<Callable<Boolean>> task1 = new ArrayList<Callable<Boolean>>();
		for(int i = 0; i < threadCount; i++) {
			AddValue counter = new AddValue(data, threadCount, step1Result, i);
			task1.add(toCallable(counter));
		}
		try{
            List<Future<Boolean>> futures1 = executorService.invokeAll(task1);  
        } catch(Exception err){
            err.printStackTrace();
        }
	}
	
	private static int[] getExclusivePrefixSum(int[] data) {
		int[] output = new int[data.length];
		output[0] = 0;
		for(int i = 0; i < data.length - 1; i++) {
			output[i+1] = data[i] + output[i];
		}
		return output;
	}
	
	private Callable<Boolean> toCallable(final Runnable runnable) {
	    return new Callable<Boolean>() {
	        @Override
	        public Boolean call() {
	            runnable.run();
	            return null;
	        }
	    };
	}
	
}