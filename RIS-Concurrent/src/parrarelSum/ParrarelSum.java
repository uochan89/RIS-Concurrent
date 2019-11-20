package parrarelSum;

import java.util.Arrays;

import util.DataReader;

public class ParrarelSum {

	private static int threadCount = 2;
	private static long[] data;
	
	//TODO:スレッドの設定を外出ししておけば、生成時間を外出しできる。runが終わったらスレッド死ぬってあるけど、ローカル変数として作成しない場合
	//は参照先がなくなってGCされるんだろうか？
	
	public static void main(String[] args) {
		data = DataReader.readData("src/unsorted_nums.txt");
		System.out.println("all data has been inmported on memory.");
		System.out.println("start sum in sequential mode.");
		long start = System.currentTimeMillis();
		System.out.println(sequentialSum(data));
		long end = System.currentTimeMillis();
		System.out.println("time required : " + (end - start));
		System.out.println("start sum in concurrent mode.");
		start = System.currentTimeMillis();
		System.out.println(concurrentSum(data));
		end = System.currentTimeMillis();
		System.out.println("time required : " + (end - start));
	}
	
	private static long concurrentSum(long[] data) {
		long[] results = new long[threadCount];
		Counter[] counters = new Counter[threadCount];
		for (int i = 0; i < threadCount; i++) {
			Counter counter = new Counter(data, results, threadCount);
			counters[i] = counter;
			counter.run();
		}
		for (int i = 0; i < counters.length; i++) {
			try {
				counters[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return sequentialSum(results);	
	}
	
	private static long sequentialSum(long[] data) {
		return Arrays.stream(data).sum();
	}
}

class Counter extends Thread{
	
	private static long[] data;
	private static long[] results;
	private static int threadCount;
	
	Counter(long[] data, long[] results, int threadCount){
		Counter.data = data;
		Counter.results = results;
		Counter.threadCount = threadCount;
	}

	@Override
	public void run() {
		System.out.println(this.getName() + " has started cuculating...");
		int id = Integer.parseInt(this.getName().substring(7));
		long sum = 0;
		for(int i = id; i < data.length; i+= threadCount) {
			sum += data[i];
		}
		results[id] = sum;
		System.out.println(this.getName() + " finished cuculating.");
	}
	
}