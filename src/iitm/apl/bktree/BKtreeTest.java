package iitm.apl.bktree;

import java.io.IOException;
import java.util.HashMap;

public class BKtreeTest
{
	public static void main(String[] args) throws IOException 
	{
		String[] wordList = new String[] 
		{
				"Abhiram","Kaushik","Sandeep","Shashank","Hrishikesh","Anup","Abhineet","Suhas","VarunMalani"
		};
		
		BKTree<String> bkTree = new BKTree<String>(new LevenshteinDistance());
		
		for (String word : wordList) 
		{
			bkTree.add(word);
		}
		
		HashMap<String, Integer> queryMap = bkTree.makeQuery("Abhilash", 5);
		System.out.println(queryMap);
		
		String searchTerm = "cowsick";
		System.out.println("Best match for '"+searchTerm+"' = "+bkTree.bestWordMatchWithDistance(searchTerm));
		
		searchTerm = "handgeep";
		System.out.println("Best match for '"+searchTerm+"' = "+bkTree.bestWordMatchWithDistance(searchTerm));
}
	}
