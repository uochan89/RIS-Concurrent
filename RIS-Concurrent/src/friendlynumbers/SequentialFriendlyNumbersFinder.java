package friendlynumbers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SequentialFriendlyNumbersFinder {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		SequentialFriendlyNumbersFinder.getAnswer(200000);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	public static void getAnswer(int n) {
		List<int[]> answer = findPairs(getAbundancy((find(n))));
		for(int[] a : answer) {
			System.out.println(Arrays.toString(a));
		}
	}
	
	private static List<int[]> findPairs(int[][] abundancies){
		List<int[]> pairs = new ArrayList<int[]>();
		for(int i = 0; i < abundancies.length-1; i++) {
			for(int j = i+1 ; j < abundancies.length; j++) {
				if(abundancies[i][0] == abundancies[j][0] && abundancies[i][1] == abundancies[j][1]) {
					int[] pair = {abundancies[i][2], abundancies[j][2]};
					pairs.add(pair);
				}
			}
		}
		return pairs;
	}
	
	private static int[][] getAbundancy(List<ArrayList<Integer>> divisors) {
		int[][] abundancy = new int[divisors.size()][3];
		for(int index = 0; index < divisors.size(); index++) {
			int sum = 0;
			for(Integer div : divisors.get(index)) {
				sum += div;
			}
			int lcm = gcd(sum, index+1);
			abundancy[index][0] = sum/lcm;
			abundancy[index][1] = (index + 1)/lcm;
			abundancy[index][2] = sum;
		}
		return abundancy;
	}
	
	private static List<ArrayList<Integer>> find(int endNumber) {
		List<ArrayList<Integer>> allDivisors = new ArrayList<ArrayList<Integer>>();
		for(int i = 1; i <= endNumber; i++) {
			ArrayList<Integer> divisors = getDivisors(i);
			allDivisors.add(divisors);
		}
		ArrayList<Integer> one = new ArrayList<Integer>();
		one.add(1);
		allDivisors.set(0, one);
		return allDivisors;
	}
	
	private static ArrayList<Integer> getDivisors(int number){
		ArrayList<Integer> divisors = new ArrayList<Integer>();
		for(int i = 1; i <= Math.pow(number, 0.5); i++) {
			if(number%i == 0) {
				divisors.add(i);
				if(i != number/i) {
					divisors.add(number/i);
				}
			}
		}
		return divisors;
	}
	
	private static int gcd(int a, int b) {
		int temp;
		while((temp = a%b)!=0) {
			a = b;
			b = temp;
		}
		return b;
	}
}
