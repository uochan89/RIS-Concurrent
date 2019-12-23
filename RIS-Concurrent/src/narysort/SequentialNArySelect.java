package narysort;

import java.util.Arrays;

public class SequentialNArySelect {

	public static void main(String[] args) {
		
		int[] data = {423,42,34,234,2,654,635,27,463,46385,746,236,7624,63,676245,5473,662,45256,35,251,426,73664,25};
		Arrays.sort(data);
		
		int n = 6;
		int key = 423;
		int start = 0;
		int end = data.length;
		int answer = -1;
		loop:
		while(start<end) {
			//TODO:start,endで３つめのコメントまでを修正する。
			System.out.println(Arrays.toString(data));
			System.out.println("loop");
			System.out.println(start);
			System.out.println(end);
			int [] dividientIndex = new int[n];
			for(int i=0; i < n-1; i++) {
				dividientIndex[i] = start + (end-start)/(n-1)*i;
			}
			dividientIndex[n-1] = end-1;
			System.out.println(Arrays.toString(dividientIndex));
			
			int[] dividientType = new int[n];
			// -1 right arrow, 0 corret, 1, left arrow
			for(int i=0; i < n; i++) {
				if(data[dividientIndex[i]] == key) {
					answer = dividientIndex[i];
					System.out.println(dividientIndex[i]);
					break loop;
					//whileがbreakできてないんか・・・
				}else if (data[dividientIndex[i]] > key) {
					dividientType[i] = -1;
				}else if (data[dividientIndex[i]] < key) {
					dividientType[i] = 1;
				}
			}
			System.out.println(Arrays.toString(dividientType));
			
			//-1, 1の切り替え点を次のstart endとする
			//最後に切り替え点があった場合大丈夫？
			for(int i=0; i < n-1; i++) {
				if(dividientType[i] == 1 && dividientType[i+1] == -1) {
					start = dividientIndex[i]+1;
					end = dividientIndex[i+1];
					break;
				}
			}
		}
		
		System.out.println("compare");
		System.out.println(key);
		System.out.println(data[answer]);
		
	}
}
