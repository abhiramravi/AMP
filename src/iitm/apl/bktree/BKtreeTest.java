package iitm.apl.bktree;

import java.io.IOException;
import java.util.HashMap;

public class BKtreeTest
{
	public static void main(String[] args) throws IOException 
	{
		String[] wordList = new String[] 
		{
				"jornada","del","muerto","faint","crawling","one","one","step","the","closer","cure","for","the","itch","point","of","authority","foreword","the","requiem","nobodys","listening","papercut","somewhere","i","belong","with","you","what","i've","done","from","the","inside","the","catalyst","waiting","for","the","end","iridescent","lying","from","you","robot","boy","fallout","high","voltage","hit","the","floor","don't","stay","breaking","the","habit","here","i","go","again","easier","to","run","in","the","end","wrectches","and","kings","faint","breaking","the","habit","when","they","come","for","me","the","messenger","blackout","pushing","me","away","empty","spaces","wisdom","justice","and","love","dont","say","a","place","for","my","head","the","radiance","figure","burning","in","the","skies","session","numb"
		};
		
		BKTree<String> bkTree = new BKTree<String>(new LevenshteinDistance());
		
		for (String word : wordList) 
		{
			bkTree.add(word);
		}
		
		HashMap<String,Integer> queryMap = bkTree.makeQuery("the", 2);
		System.out.println(queryMap);
		
		String searchTerm = "one";
		System.out.println("Best match for '"+searchTerm+"' = "+bkTree.bestWordMatchWithDistance(searchTerm));
		
		searchTerm = "the";
		System.out.println("Best match for '"+searchTerm+"' = "+bkTree.bestWordMatchWithDistance(searchTerm));
}
	}
