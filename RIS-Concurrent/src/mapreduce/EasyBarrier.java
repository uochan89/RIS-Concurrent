package mapreduce;

public class EasyBarrier {

	private static int count;
	private static int originalCount;
	private static Object mutex = new Object();
	private static int color = 0;
	
	public EasyBarrier(int count) {
		EasyBarrier.count = count-1;
		EasyBarrier.originalCount = count-1;
	}
	
	void mywait(int color) {
		synchronized (mutex) {
			System.out.println("############");
			System.out.println(count);
			System.out.println(color);
			System.out.println(EasyBarrier.color);
			//waitしないのは連続で同じスレッドがmutexを獲得し、count==0の状態で処理にはいるから。
			//count != 0のもよう
			if(count != 0) {
//				if(EasyBarrier.color == color) {
//					count--;
//					try {
//						mutex.wait();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
				count--;
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("im bad");
//				if(EasyBarrier.color == color) {
//					System.out.println("unlock all my friend!!");
//					if(EasyBarrier.color == 0) {
//						EasyBarrier.color = 1;
//					}else {
//						EasyBarrier.color = 0;
//					}
//					this.count = originalCount;
//					mutex.notifyAll();
//				}
				System.out.println("unlock all my friend!!");
				EasyBarrier.count = originalCount;
				System.out.println(EasyBarrier.count);
				System.out.println(EasyBarrier.color);
				mutex.notifyAll();
				//この時点ではこれを実行したスレッドがmutexを保持しているので、waitから解放されたスレッドはmutexを競い合う
			}
		}
	}
}
