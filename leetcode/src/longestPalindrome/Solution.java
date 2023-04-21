package longestPalindrome;

import java.util.HashMap;
import java.util.Map;

public class Solution {
	public static void main(String args[]) {
		Solution s=new Solution();
		s.longestPalindrome("abccccdd");
		
	}
	public void longestPalindrome(String s) {
		int odd_Count=0;
		HashMap<Character,Integer> map=new HashMap<>();
		for(char ch:s.toCharArray()) {
			
			map.put(ch, map.getOrDefault(ch, 0)+1);
		System.out.println("list"+map);
			
			
		
			
		}
		
	}

}

