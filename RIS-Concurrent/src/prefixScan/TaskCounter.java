package prefixScan;

public class TaskCounter {

	private int counter = 0;

	public int getCounter() {
		return counter;
	}

	public synchronized void incrementCounter() {
		this.counter++;
	}
	
	
}
