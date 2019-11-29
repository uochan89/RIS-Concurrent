package selectAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import prefixscanner.parrarel.ParrarelPrefix;
import util.DataReader;

public class ParrarelSelect {

	//これって私のローカル環境では結局チャンクを別個のコアで動かすことはできないから並行処理にしかならんわけで、そうなった場合にスレッドを生成するOHが無駄になるんではないでしょうか。
	private int q = 5;
	private ExecutorService executorService;
	private int threadCount;
	private final static int LESS_THEN_Q = 0;
	private final static int EQUAL_TO_Q = 1;
	private final static int LARGER_THEN_Q = 2;
	
	public ParrarelSelect(int threadCount, ExecutorService executorService) {
		this.threadCount = threadCount;
		this.executorService = executorService;
	}
	
	public static void main(String[] args) {
		long[] data = DataReader.readData("src/unsorted_nums.txt");
		Arrays.sort(data);
		System.out.println(data[5555]);
		int targetIndex = 5555;
		int threadCount = 2;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		ParrarelSelect select = new ParrarelSelect(threadCount, executorService);
		long result = select.select(data, targetIndex-1);
		System.out.println("result : " + result);
		executorService.shutdown();
	}
	
	//return the value in data[targetIndex-1]
	public long select(long[] data, int targetIndex) {
		while(true) {
			int chunkCount;
			if(data.length%q == 0) {
				chunkCount = data.length/q;
			}else {
				chunkCount = data.length/q + 1;
			}
			System.out.println("新しいループです");
			System.out.println("dataサイズ : " + data.length);
			System.out.println(Arrays.toString(data));
			System.out.println("targetIndex : " + targetIndex);
			if(data.length < q) {
				Arrays.sort(data);
				return data[targetIndex];
			}
			
			//get median from each chunk
			long[] medians = new long[chunkCount];
			List<Callable<Boolean>> tasks1 = new ArrayList<Callable<Boolean>>();
			//ここで毎回chunkIndexを与えるためにインスタンスを生成しているけどもっと効率的なやり方があるはず。
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				FindMedians finder = new FindMedians(data, q, medians, chunkIndex, chunkCount);
				tasks1.add(toCallable(finder));
			}
			try{
	            List<Future<Boolean>> futures = executorService.invokeAll(tasks1);  
	        } catch(Exception err){
	            err.printStackTrace();
	        }
			System.out.println("各チャンクのメディアンが出力されました。");
			System.out.println(Arrays.toString(medians));
			
			//get medians of medians sequentially
			long medianOfMedians = getMedian(medians);
			System.out.println("MoM : " + medianOfMedians);
			
			//classify each chunk element based on medianOfMedians
			//typeCounterの和がdataの数と等しくならない。staticでハマってるとか？
			int[] typeCounter = {0, 0, 0};
			int[] elementTypes = new int[data.length];
			System.out.println("elementTypes : " + elementTypes.length);
			List<Callable<Boolean>> tasks2 = new ArrayList<Callable<Boolean>>();
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				CountAndMark counter = new CountAndMark(data, typeCounter, elementTypes, medianOfMedians, chunkIndex, q, chunkCount);
				tasks2.add(toCallable(counter));
			}
			try{
	            List<Future<Boolean>> futures2 = executorService.invokeAll(tasks2);  
	        } catch(Exception err){
	            err.printStackTrace();
	        }
			System.out.println(Arrays.toString(typeCounter));
			
			int targetType = -1;
			if(typeCounter[0] > targetIndex) {
				targetType = LESS_THEN_Q;
			}else if((typeCounter[0] + typeCounter[1]) > targetIndex) {
				return medianOfMedians;
			}else {
				targetType = LARGER_THEN_Q;
				targetIndex = targetIndex - (typeCounter[0] + typeCounter[1]); 
			}
			System.out.println("targettype : " + targetType);
			//ここで作成したprefixスキャンを使うrunnableで実装しておけば、既存のthreadにタスクをsubmitするっていう使い方ができたのにね。。。
			//もしかしてここでdataMarkを初期化していないから参照先のアドレスが取得できていなくて、スレッドあが値を更新できていない？
			System.out.println(Arrays.toString(elementTypes));
			
			//check if ecach element is in the target group
			int[] dataMark = new int[data.length];
			List<Callable<Boolean>> tasks3 = new ArrayList<Callable<Boolean>>();
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				PackingScan scanner = new PackingScan(chunkIndex, elementTypes, targetType, q, dataMark);
				tasks3.add(toCallable(scanner));
			}
			try{
	            List<Future<Boolean>> futures3 = executorService.invokeAll(tasks3);  
	        } catch(Exception err){
	            err.printStackTrace();
	        }
			System.out.println("各エレメントが指定のグループに属するかどうかを出力しました。");
			System.out.println(Arrays.toString(dataMark));
			//dataMarkをpreFixScanする
			//実際に必要なのはpreFixScanじゃなくてPrefixだけDIの設計にしてもっとexecutorserviceへの依存を
			ParrarelPrefix parrarelPrefix = new ParrarelPrefix(executorService, threadCount, dataMark);
			parrarelPrefix.preFix();
			System.out.println("チャンク属性をprefixしました。");
			System.out.println(Arrays.toString(dataMark));
			
			//上の結果をもとに、新しい配列をpackingする。
			//targetType
			long[] newPack = new long[dataMark[dataMark.length-1]];
			List<Callable<Boolean>> tasks4 = new ArrayList<Callable<Boolean>>();
			for(int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				PackingMove mover = new PackingMove(chunkIndex, q, data, newPack, dataMark);
				tasks4.add(toCallable(mover));
			}
			try{
	            List<Future<Boolean>> futures4 = executorService.invokeAll(tasks4);  
	        } catch(Exception err){
	            err.printStackTrace();
	        }
			System.out.println("newPackを作成しました");
			System.out.println(Arrays.toString(newPack));
			data = newPack;
		}
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
	
	private long getMedian(long[] data) {
		int medianIndex = data.length/2;
		long[] clonedData = data.clone();
		Arrays.sort(clonedData);
		return clonedData[medianIndex];
	}

}
