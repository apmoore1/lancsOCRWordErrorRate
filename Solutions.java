import java.util.*;
/*
 * Author -- Andrew Moore
 * 
 * Finds the best rule to apply to the OCR word that is not
 * the same as the perfect word and writes the correcting XML
 * for that rule and stores that in the relevant sol*XML variable.
 * Currently only 5 rules (solutions) and get methods for each of
 * those rules XML variables. 
 * Solution 1 -- Replacement rule
 * Solution 2 -- Merge rule
 * Solution 3 -- Splitting rule
 * Solution 4 -- Delete rule
 * Solution 5 -- Insert rule
 *
 * NOTE:
 * The XML it creates may be wrong for the VARD tool, this needs to be 
 * checked.
 *
 * Called by WordAction class.
 *
 * Improvements:
 * See solution2 and solution3 method.
*/
public class Solutions
{
	/*
	 * Stores the XML for each rule so that it can be stored on the stack
	 * when the rule is selected as the best rule.
	*/
	private String sol1XML = "";
	private String sol2XML = "";
	private String sol3XML = "";
	private String sol4XML = "";
	private String sol5XML = "";
	
	/*
	 * Threshold for the Levinstain distance lower the threshold the 
	 * more potentially incorrect words will be replaced the higher the
	 * threshold the more likely the word will not be replaced when potentially 
	 * it should be replaced. Also used in line 20 of WordCompareII.
	*/
	public static final double threshold = 0.77;
	
	/*
	 * PARAMETERS 
	 * solMap param -- Used to add entry into HashMap and return it,
	 * entry will be the solution number which is 1 and the lev distance 
	 * between the perfect paragraph and the OCR paragraph that has been changed,
	 * if it has not been changed then the distance is 0 (A change here would be 
	 * a replacement).
	 * wordArray1 param -- Is the perfect paragraph in a word list.
	 * wordArray2 param -- Is the OCR paragraph in a word list.
	 * val param -- The index of the word that is being examined in the word lists.
	 * solutions param -- Instance of this class used to add XML to the instance of the
	 * class so it can be accessed later in the WordAction.java class using the get
	 * methods.
	 * acc param -- Tells the method if there is one or two words left in the word lists 
	 * after the current word. 1 == one word left, 2 == at least two words left after the
	 * current word.
	 * NOTE: if these parameter names appear in any other methods in this class
	 * then please use this as the explanation of the parameter. 
	 *
	 * What it does:
	 * Replacement rule:
	 * Are the two words close enough to each other using the Levenshtein distance 
	 * with a corresponding threshold (this.threshold), else are the words in front
	 * of the current word close enough to each corresponding words, 
	 * if so then the current word is likely a replacement. 
	*/
	public HashMap<Integer,Float> solution1(HashMap<Integer,Float> solMap,ArrayList<String> wordArray1, ArrayList<String> wordArray2, int val,Solutions solutions, int acc)
	{
		WordCompareII compare = new WordCompareII();
		
		double threshold = solutions.threshold;
		
		String word1 = wordArray1.get(val);
		String word2 = wordArray2.get(val);
		float max = 0;
		/*
		 * If the length of the word is the same and 2 then 
		 * their can be one letter difference. 
		*/
		if(word1.length() == 2 && word1.length()==2)
		{
			threshold = 0.75;
		}
	
		if(compare.levDistance(word1,word2) >= threshold)
		{
			wordArray2.remove(val);
			wordArray2.add(val,word1);
			solutions.sol1XML = "o=\""+word2+"\">"+word1+"</rep>";
			
			String paragraph1 = solutions.arrayListToString(wordArray1);
			String paragraph2 = solutions.arrayListToString(wordArray2);
			
			max = compare.levDistance(paragraph1,paragraph2);
		}
		/*
		 * If one word in front is close to its corresponding word then it is more
		 * likely a replacement this is only called if their is only one word left
		 * after the current word.
		*/
		if(acc == 1)
		{
			String oneWordInFront1 = wordArray1.get(val+1);
			String oneWordInFront2 = wordArray2.get(val+1);
			if(compare.levDistance(oneWordInFront1,oneWordInFront2) >= threshold)
			{
				wordArray2.remove(val);
				wordArray2.add(val,word1);
				solutions.sol1XML = "o=\""+word2+"\">"+word1+"</rep>";
			
				String paragraph1 = solutions.arrayListToString(wordArray1);
				String paragraph2 = solutions.arrayListToString(wordArray2);
				
				float maxAcc = compare.levDistance(paragraph1,paragraph2);
				if(maxAcc>max)
				{
					max = maxAcc;
				}
			}
		}
		/*
		 * If one word and two words in front is close enough to their corresponding 
		 * words then it is most likely a replacement this is only called if there are 
		 * more than one word left after the current word.
		*/
		else if(acc == 2)
		{
			String oneWordInFront1 = wordArray1.get(val+1);
			String twoWordInFront1 = wordArray1.get(val+2);
			String oneWordInFront2 = wordArray2.get(val+1);
			String twoWordInFront2 = wordArray2.get(val+2);
			
			if(compare.levDistance(oneWordInFront1,oneWordInFront2) >= threshold && compare.levDistance(twoWordInFront1,twoWordInFront2) >= threshold)
			{
				wordArray2.remove(val);
				wordArray2.add(val,word1);
				solutions.sol1XML = "o=\""+word2+"\">"+word1+"</rep>";
			
				String paragraph1 = solutions.arrayListToString(wordArray1);
				String paragraph2 = solutions.arrayListToString(wordArray2);
				
				float maxAcc = compare.levDistance(paragraph1,paragraph2);
				if(maxAcc>max)
				{
					max = maxAcc;
				}
			}
		}
		
		
		solMap.put(1,max);
		
		return solMap;
	}
	/*
	 * Merge (for the OCR words) with the word in front.
	 *
	 * IMPROVEMENTS that can be done
	 * Done something similar to the splitting method that checks whether 
	 * the current word + (word+1) + (word+2) + (word+n) is the best merge 
	 * as it may require more than just one merge. 
	*/
	public HashMap<Integer,Float> solution2(HashMap<Integer,Float> solMap,ArrayList<String> wordArray1, ArrayList<String> wordArray2, int val, Solutions solutions)
	{
		WordCompareII compare = new WordCompareII();
		
		String word1 = wordArray1.get(val);
		String word2 = wordArray2.get(val);
		String mergeWord = word2+wordArray2.get(val+1);
		
		float max = 0;
	
		if(compare.levDistance(word1,mergeWord) >= solutions.threshold)
		{
			wordArray2.remove(val+1);
			wordArray2.remove(val);
			wordArray2.add(val,word1);
			solutions.sol2XML = "o=\""+mergeWord+"\">"+word1+"</merge>";
			
			String paragraph1 = solutions.arrayListToString(wordArray1);
			String paragraph2 = solutions.arrayListToString(wordArray2);
			
			max = compare.levDistance(paragraph1,paragraph2);
		}
		
		solMap.put(2,max);
		
		return solMap;
	}
	
	/*
	 * Splitting rule
	 * Splits OCR word such that the word is best matched with the corresponding correct
	 * word. E.g. if the OCR word is split in two then the first word should match 
	 * well with the current correct word and then the second part of the split should match
	 * well with the next correct word. See WordCompareII methods for the splitting:
	 * splittingCombined method and bestSplit method.
	 *
	 * Improvement could be to only split if the OCR word is at a significant length difference
	 * to the correct word, of which this lengthDiff threshold can be varied in one masive
	 * loop of which this can be said for the overall Levenshtein threshold.  
	*/
	public HashMap<Integer,Float> solution3(HashMap<Integer,Float> solMap,ArrayList<String> wordArray1, ArrayList<String> wordArray2, int val, Solutions solutions)
	{
		WordCompareII compare = new WordCompareII();
		
		String word1 = wordArray1.get(val);
		String word2 = wordArray2.get(val);
		
		float max = 0;
		
		if(word2.length() > word1.length())
		{
			ArrayList<String> splitWords = compare.splittingCombined(wordArray1,word2,val);
			String allWords = "";
		  	for(int y = 0;y<splitWords.size();y++)
		    {
		    	if(y==0)
		    	{
		    		wordArray2.remove(val);
		    	}
		    	wordArray2.add(val+y,wordArray1.get(val+y));
		    	allWords+="o=\""+splitWords.get(y)+"\">"+wordArray1.get(val+y)+"</split>\n";
		    }
		   	
			solutions.sol3XML = allWords;
			
			String paragraph1 = solutions.arrayListToString(wordArray1);
			String paragraph2 = solutions.arrayListToString(wordArray2);
				
			max = compare.levDistance(paragraph1,paragraph2);
		}
		
		solMap.put(3,max);
		
		return solMap;
	}
	
	/*
	 * Deletion rule
	 * If the current correct word matches well with next OCR word, then it is 
	 * most likely an deletion rule. If got at least two words left after current in the OCR
	 * array and at least one in the perfect after current then acc can be 2 and compare lev distance 
	 * of the current perfect word with the OCR + 1 and perfect + 1 with OCR + 2.
	 * If got at least one word left in the OCR array after current then acc can be 1 
	 * and compare lev distance of the current perfect word with the OCR + 1. Else just
	 * delete the OCR word.
	*/
	public HashMap<Integer,Float> solution4(HashMap<Integer,Float> solMap,ArrayList<String> wordArray1, ArrayList<String> wordArray2, int val,Solutions solutions,int accuracy)
	{
		WordCompareII compare = new WordCompareII();
		double threshold = solutions.threshold;
		
		
		String word1 = wordArray1.get(val);
		String word2 = wordArray2.get(val);
		
		float max = 0;
		
		if(compare.levDistance(word1,word2) < threshold)
		{
			String paragraph1 = solutions.arrayListToString(wordArray1);
	
			if(accuracy == 2)
			{
				String oneWordInFront1 = wordArray1.get(val+1);
				String oneWordInFront2 = wordArray2.get(val+1);
				String twoWordInFront2 = wordArray2.get(val+2);
				if(compare.levDistance(word1,oneWordInFront2)>threshold && compare.levDistance(oneWordInFront1,twoWordInFront2)>threshold)
				{
					wordArray2.remove(val);
				
					solutions.sol4XML = "o=\""+word2+"\">"+word1+"</delete>";
					String paragraph2 = solutions.arrayListToString(wordArray2);
					max = compare.levDistance(paragraph1,paragraph2);
				}	
			}
			else if(accuracy==1)
			{
				String oneWordInFront2 = wordArray2.get(val+1);
				if(compare.levDistance(word1,oneWordInFront2)>threshold)
				{
					wordArray2.remove(val);

					solutions.sol4XML = "o=\""+word2+"\">"+word1+"</delete>";
					String paragraph2 = solutions.arrayListToString(wordArray2);
					max = compare.levDistance(paragraph1,paragraph2);
				}
			}
			else
			{
				wordArray2.remove(val);
				solutions.sol4XML = "o=\""+word2+"\">"+word1+"</delete>";
				String paragraph2 = solutions.arrayListToString(wordArray2);
				max = compare.levDistance(paragraph1,paragraph2);
			}
		}
		if(max>0)
		{
			wordArray2.add(val,word2);
		}
		
		solMap.put(4,max);
		
		return solMap;
	}
	
	/*
	 * Insertion rule
	 * If the current OCR word matches well with next correct word, then it is 
	 * most likely an insertion rule. If got at least two words left after current in the perfect
	 * array and at least one in the OCR after current then acc can be 2 and compare lev distance 
	 * of the current OCR word with the correct + 1 and OCR + 1 with correct + 2.
	 * If got at least one word left in the perfect array after current then acc can be 1 
	 * and compare lev distance of the current OCR word with the correct + 1. Else just
	 * insert into the OCR the correct current word which is essentially a replacement
	 * without the lev distance check.
	*/
	public static HashMap<Integer,Float> solution5(HashMap<Integer,Float> solMap,ArrayList<String> wordArray1, ArrayList<String> wordArray2, int val,Solutions solutions,int acc)
	{
		WordCompareII compare = new WordCompareII();
		double threshold = solutions.threshold;
		
		
		String word1 = wordArray1.get(val);
		String word2 = wordArray2.get(val);
		
		float max = 0;
		
		if(acc == 1)
		{
			String oneWordInFront1 = wordArray1.get(val+1);
			if(compare.levDistance(oneWordInFront1,word2) >= threshold)
			{
				wordArray2.add(val,word1);
				solutions.sol5XML = "o=\""+word2+"\">"+word1+"</insert>";
			
				String paragraph1 = solutions.arrayListToString(wordArray1);
				String paragraph2 = solutions.arrayListToString(wordArray2);
				
				float maxAcc = compare.levDistance(paragraph1,paragraph2);
				if(maxAcc>max)
				{
					max = maxAcc;
				}
			}
		}
		else if(acc == 2)
		{
			String oneWordInFront1 = wordArray1.get(val+1);
			String twoWordInFront1 = wordArray1.get(val+2);
			String oneWordInFront2 = wordArray2.get(val+1);

			if(compare.levDistance(oneWordInFront1,word2) >= threshold && compare.levDistance(twoWordInFront1,oneWordInFront2) >= threshold)
			{
				wordArray2.add(val,word1);
				solutions.sol5XML = "o=\""+word2+"\">"+word1+"</insert>";
			
				String paragraph1 = solutions.arrayListToString(wordArray1);
				String paragraph2 = solutions.arrayListToString(wordArray2);
				
				float maxAcc = compare.levDistance(paragraph1,paragraph2);
				if(maxAcc>max)
				{
					max = maxAcc;
				}
			}
			
		}
		else if(acc == 0)
		{
			wordArray2.add(val,word1);
			solutions.sol5XML = "o=\""+word2+"\">"+word1+"</insert>";
			
			String paragraph1 = solutions.arrayListToString(wordArray1);
			String paragraph2 = solutions.arrayListToString(wordArray2);
				
			float maxAcc = compare.levDistance(paragraph1,paragraph2);
			if(maxAcc>max)
			{
				max = maxAcc;
			}
		}
		if(max>0)
		{
			wordArray2.add(val,word2);
		}
		
		solMap.put(5,max);
		
		return solMap;
	}
	/*
	 * Get methods for the XML of the corresponding solutions.
	*/
	public String getS1XML()
	{
		return sol1XML;
	}
	
	public String getS2XML()
	{
		return sol2XML;
	}
	
	public String getS3XML()
	{
		return sol3XML;
	}
	
	public String getS4XML()
	{
		return sol4XML;
	}
	
	public String getS5XML()
	{
		return sol5XML;
	}
	
	// Convert a word list into a String, used by WordCompareII.correctOCR()
	public static String arrayListToString(ArrayList<String> arrayList)
	{
		String paragraph = "";
		for(int i = 0;i < arrayList.size();i++)
		{
			paragraph += arrayList.get(i)+ " ";
		}
		return paragraph.trim();
	}

}