package oddeventranspositionsort;

import java.util.concurrent.atomic.AtomicInteger;

public class ParallelOddEvenSort {

	public static void main(String[] args) {
		int[] data = {532,45,34,523,52,345,2,52457,67,8467,847613,52,6236,564};
	}
}

class Chunk implements Runnable{

	private static int[] data;
	private static AtomicInteger transportedCounter;
	private static int startIndex;
	private static int threadCount;
	private int threadId;
	
	public Chunk(int[] data,int threadCount, int threadId) {
		Chunk.data = data;
		//全てのタスクを生成した後に実行すること。startIndexm ifTransportedに不整合が生じる
		transportedCounter = new AtomicInteger(threadCount-1);
		Chunk.startIndex = 0;
		this.threadId = threadId;
		Chunk.threadCount = threadCount;
	}
	
	//このアルゴリズムではシリの同期とatiomicintegeバリアを持つことになる。
	
	//とりあえず2スレッドの前提で実装
	@Override
	public void run() {
		int endIndex;
		if(data.length%2 == 0) {
			endIndex = data.length/threadCount-1;
		}else {
			endIndex = data.length/threadCount;
		}
		
		//countdownlatchは同期用だから多分普通にカウントしたほうがいいが、
		while(transportedCounter.get() == 0 || startIndex == 1) {
			//このifTransportedの初期化についても、先に実行したスレッドが同じループを行なっている未実施のスレッドのwhile評価に
			//影響を与えてしまう
			//この問題はCountDownLechで解決できる。
			transportedCounter.decrementAndGet();
			//ここにバリアを引く必要ができる
			if(threadId == 1) {
				endIndex = data.length - 1;
				startIndex = startIndex + data.length/2;
			}
			
			for(int i = startIndex; i < endIndex; i++) {
				if(data[i] > data[i+1]) {
					int temp = data[i+1];
					data[i+1] = data[i];
					data[i] = temp;
					//reset
					//transportしたスレッドがあるなら、while分判定はそのあとのdecrementを受けても全て入れる。
					//ここでcounterが初期化されて、その後に上のdecrementに入ったらどうなるの？
					//全てのスレッドがwhileを評価する前にcounter == thread - 1という前提がくずれる
					//別にディクリメントの前にリセットされたとしても、次のループにその減らされた値がはいってへんなことになるねえ・
					//全てのディクリメントが終わった後でリセットするような組み合わせだといいけど。。。
					transportedCounter = new AtomicInteger(threadCount-1);
					//これcritialにしているけどそんな必要ある？
				}
			}
			
			//次のループに入るまでに他のスレッドの処理が完了している必要がある。
			//この処理は1スレッドのみが実行する。これをjavaで「正しく」実装する方法がわからないし、実装できるけどとても重そう
			if(startIndex == 0) {
				startIndex = 1;
			}else {
				startIndex = 0;
			}
		}
	}
}