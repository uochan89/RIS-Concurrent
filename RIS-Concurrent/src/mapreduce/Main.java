package mapreduce;


public class Main {

	public static void main(String[] args) {
		
		int threadCount = 20;
		EasyBarrier barrier = new EasyBarrier(threadCount);
		
		for(int i = 0; i < threadCount; i++) {
			Thread thread = new Thread(new Counter(barrier));
			thread.start();
		}
		
	}

}

class Counter implements Runnable{
	
	private int color = 0;
	private static EasyBarrier barrier;
	
	public Counter(EasyBarrier barrier) {
		Counter.barrier = barrier;
	}
	
	@Override
	public void run() {
		while(true) {
			System.out.println("hi im gosu");
			barrier.mywait(color);
			//ここでwaitせずに同じスレッドが２回連続ループしてるからへんなことになってる。
			//colorがあったらうまくいく。
			if(color == 1) {
				color = 0;
			}else {
				color = 1;
			}
		}
	}
	
}