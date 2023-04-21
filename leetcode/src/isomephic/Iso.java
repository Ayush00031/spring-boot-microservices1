package isomephic;

public class Iso {

	public static void main(String[] args) {
		
		Iso is=new Iso();
	boolean b=	is.isIsomorphic("fuo", "too");
	System.out.println(b);
		
				
	}
		
		    public static boolean isIsomorphic(String s, String t) {
		        if(s.length()!=t.length())
		            return false;
		        

		        int [] map1=new int[250];
		        int[] map2=new int[250];

		        for(int i=0;i<s.length();i++){

		            if(map1[s.charAt(i)]!=map2[t.charAt(i)])

		            return false;

		            map1[s.charAt(i)]=i+1;
		            map2[t.charAt(i)]=i+1;

		        }
		      return true;
		            
		    }
		}


