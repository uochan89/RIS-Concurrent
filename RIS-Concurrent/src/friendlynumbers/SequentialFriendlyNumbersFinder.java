package friendlynumbers;

import java.util.ArrayList;
import java.util.List;

class SequentialFriendlyNumbersFinder {

	public static void main(String[] args) {
		List<ArrayList<Integer>> result = SequentialFriendlyNumbersFinder.find(10); 
		System.out.println(result.toString());
	}
	
	public static List<ArrayList<Integer>> find(int endNumber) {
		List<ArrayList<Integer>> allDivisors = new ArrayList<ArrayList<Integer>>();
		for(int i = 1; i <= endNumber; i++) {
			ArrayList<Integer> divisors = getDivisors(i);
			allDivisors.add(divisors);
		}
		return allDivisors;
	}
	
	private static ArrayList<Integer> getDivisors(int number){
		ArrayList<Integer> divisors = new ArrayList<Integer>();
		for(int i = 1; i < Math.pow(1, 0.5); i++) {
			if(number%i == 0) {
				divisors.add(i);
				if(i != number/i) {
					divisors.add(number/i);
				}
			}
		}
		return divisors;
	}
}
