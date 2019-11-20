package prefixScan;

import util.DataReader;

public class PrefixScan {

	//TODO:TaskCounterを提供されているクラスで実装可能か検討
	//busy waitをしないようにしたい。
	
	private TaskCounter taskCounter = new TaskCounter();
	private TaskCounter taskCounter2 = new TaskCounter();
	private int threadCount;
	
	public PrefixScan(int threadCount){
		this.threadCount = threadCount;
	}
	
	public static void main(String[] args) {
		long[] data = DataReader.readData("src/unsorted_nums.txt");
		long s1 = System.currentTimeMillis();
		long[] result1 = getExclusivePrefixSum(data);
		long s2 = System.currentTimeMillis();
		System.out.println(s2-s1);
		// 500822が正しい答え
		int threadCount = 2;
		PrefixScan prefixScan = new PrefixScan(threadCount);
		prefixScan.scan(data);
		System.out.println(data[data.length-2]);
	}
	
	public void scan(long data[]) {
		synchronized(this) {
			Worker[] workers = new Worker[threadCount];
			for (int i = 0; i < threadCount; i++) {
				Worker counter = new Worker(data, threadCount, this.taskCounter, this.taskCounter2);
				workers[i] = counter;
				counter.start();
			}
			long start = System.currentTimeMillis();
			//wait for step1 to finish.
			while(true) {
				System.out.println("main thread waiting for workers to get things done... : " + this.taskCounter.getCounter());
				if(this.taskCounter.getCounter() == threadCount) {
					break;
				}
				//you can put time loop
			}
			System.out.println("main thread has noticed that workers have get things done.");
			System.out.println("main thread prefixing step1 result...");
			int stride = data.length/threadCount;
			long[] lastElements = new long[threadCount];
			for (int id = 0; id < threadCount; id++) {
				int end = (id+1)*stride;
				lastElements[id] = data[end-1];
			}

			long[] step1Result = getExclusivePrefixSum(lastElements);
			Worker.setStep1Result(step1Result);
			System.out.println("main thread finished prefixing step1 result.");
			System.out.println("notifying workers...");
			//これでいけるかな？
			for (Worker worker : workers) {
				worker.wakeUp();
			}
			System.out.println("finish!");
			//処理が完了した場合子スレッドは死ぬので、isAliveとかで処理完了の確認をするのは。。。よくない？
			while(true) {
				if(taskCounter2.getCounter() == threadCount) {
					break;
				}
			}
			long end = System.currentTimeMillis();
			System.out.println(end - start);
			System.out.println("all task completed successfully.");
		}
	}
	
	private static long[] getExclusivePrefixSum(long[] data) {
		long[] output = new long[data.length];
		output[0] = 0;
		for(int i = 0; i < data.length - 1; i++) {
			output[i+1] = data[i] + output[i];
		}
		return output;
	}
}

class Worker extends Thread{
	
	private static long[] data;
	private static long[] step1Result;
	private static int threadCount;
	private static TaskCounter taskCounter;
	private static TaskCounter taskCounter2;
	
	final private int threadId = Integer.parseInt(this.getName().substring(7));
	
	public void wakeUp() {
		synchronized(this){
			notify();
		}
	}
	
	Worker(long[] data, int threadCount, TaskCounter taskCounter, TaskCounter taskCounter2){
		Worker.data = data;
		Worker.threadCount = threadCount;
		Worker.taskCounter = taskCounter;
		Worker.taskCounter2 = taskCounter2;
	}
	
	static void setStep1Result(long[] step1Result) {
		Worker.step1Result = step1Result;
	}
	
	@Override
	public void run() {
		synchronized(this) {
			System.out.println(this.getName() + " has started the task...");
			preFix();
			taskCounter.incrementCounter();
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			addResultToChunks();
			System.out.println(this.getName() + " waking up the main thread.");
			//全てのworkerスレッドが計算を終了した時に、通知をするようにしたい。
			//現在の実装のままではthread3が処理を実施する以前に完了通知がされてしまう。
			taskCounter2.incrementCounter();
			System.out.println(getName() + " decided to die.");
		}
	}
	
	private void addResultToChunks() {
		System.out.println(this.getName() + " has started destributing.");
		int stride = data.length/threadCount;
		int start = threadId*stride;
		int end = (threadId+1)*stride;
		if(threadId == threadCount - 1) end = data.length;
		for(int i = start+1; i < end; i++) {
			data[i] += step1Result[threadId];
		}
		System.out.println(this.getName() + " finished distributing.");
	}
	
	private void preFix() {
		System.out.println(this.getName() + " has started prefixing.");
		int stride = data.length/threadCount;
		int start = threadId*stride;
		int end = (threadId+1)*stride;
		//ちなみにこの実装だと最後のスレッドにひっぱられる可能性がある
		//おそらく最後のスレッドにおしつけるのではなく均等にdistributeしたほうが効率がいいはず。
		if(threadId == threadCount - 1) end = data.length;
		for(int i = start+1; i < end; i++) {
			data[i] = data[i-1] + data[i];
		}
		System.out.println(this.getName() + " finished prefixing.");
	}
}
