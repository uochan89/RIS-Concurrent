package friendlynumbers;

import java.util.concurrent.CountDownLatch;

public class MyMap implements Runnable{

	//理想としては、runnbleをimplementしているので、アルゴリズムとどのスレッドクラスを使うかという判断。
	//すなわち、具体と抽象を分離したい。なので、このクラスはサポートできるかぎりのスレッドの抽象クラスを保持し、それを利用する
	//実際にその組み合わせを行うのはParrareFriendly....クラスの責務とするべき。
	
	private static int[][] abundancy;
	private int number;
	private static CountDownLatch latch;
	
	public MyMap(int[][] abundancy, int number, CountDownLatch latch) {
		MyMap.abundancy = abundancy;
		this.number = number;
		MyMap.latch = latch;
	}
	
	@Override
	public void run() {
		int sum = 0;
		for(int i = 1; i <= Math.pow(number, 0.5); i++) {
			if(number%i == 0) {
				sum += i;
				if(i != number/i) {
					sum += number/i;
				}
			}
		}
		int gcd = gcd(sum, number);
		abundancy[number][0] = sum/gcd;
		abundancy[number][1] = number/gcd;
		abundancy[number][2] = sum;
		latch.countDown();
	}
	
	private int gcd(int a, int b) {
		int temp;
		while((temp = a%b)!=0) {
			a = b;
			b = temp;
		}
		return b;
	}
}