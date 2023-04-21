package isSubsequence;

public class Subsequence {

	public static void main(String[] args) {
		
		Subsequence s=new  Subsequence();
		boolean n=s.isSubsequence("aec", "ahgbucte");
		System.out.println(n);
		
	}
		
		
		
		    public boolean isSubsequence(String s, String t) {
		    	int si=0;
		    	if(s.length()<1)
		    		
		    		return true;

		        for(int i=0;i<t.length();i++){
		        	
		        	if(s.charAt(si)==t.charAt(i)) 
		        		
		        	si++;
		        }
		        	if(si==s.length()) {
		        		
					return true;
		        	}
					return false;
		        
		            
		        
		        
		    }
		    }

		

	
