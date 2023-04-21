package besttimetobuyandsell;

import java.util.Scanner;

public class Buy_Sell {
	public static void main(String args[]) {
		Buy_Sell buysell=new Buy_Sell();
	System.out.println("enter the prices");
	
		
	}
	
	public  int maxProfit(int[] prices) {
		
		int buy=Integer.MAX_VALUE;
		int sell=0;
		for(int i=0;i<prices.length;i++) {
			buy=Math.min(buy, prices[i]);
			sell=Math.max(sell, prices[i]-buy);
		}
		return sell;
	}

}
