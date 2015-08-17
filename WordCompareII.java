import java.util.*;
import java.lang.Math.*;
import java.util.regex.*;
import java.util.Stack;
/*
 * Author -- Andrew Moore
 *
 * Uses WordAction class.
 *
 * Contains useful methods that are used in the Solutions and WordAction classes.
 *
 * Contains the main method that when run takes hardcoded text of correct and OCR text 
 * and finds the difference that are required to change the OCR to the correct in XML
 * format, also at the moment prints out the Levenshtein distance of the correct and OCR
 * text after it has been evaluated and should be 1 if the program has succeed any less than
 * 1 then it has failed at a least one word in the OCR text.  
 *
 * At the moment there are a few example paragraphs commented out that can be used to 
 * demo it. Also NOTE at in this version the XML that it prints out 
 * needs to be changed so that it prints the correct XML this can be changed in the 
 * Solutions class. 
*/
public class WordCompareII
{
	// The number of rules created in the Solutions.java file.
	public static final int numOfSol = 5;
	
	/*
	 * Used by Solutions class as well.
	 * 
	 * word1 param -- The word list of the correct paragraph version of the OCR paragraph.
	 * word2 param -- The current OCR word that is longer than the correct current word.
	 * val param -- The index of the current word in the word list used for the word1 param.
	 *
	 * Find the best split of word2 with respect to the corresponding correct words e.g. if
	 * the best split is two words then OCR first word of the split should match well with 
	 * respect to the Levenshtein distance to the correct current word and the second half of
	 * the split OCR word should match well with the current + 1 correct word. bestSplit method
	 * finds the best split with respect to the Levenshtein distance. Then returns a word list
	 * of split words in the last example it would contain two words. 
	*/
	public static ArrayList<String> splittingCombined(ArrayList<String> word1, String word2,int val)
	{
		WordCompareII compare = new WordCompareII();
		
		ArrayList<String> splitWords = new ArrayList<String>();
		boolean keepGoing = true;
		while(keepGoing)
		{
			String splitWord = compare.bestSplit(word1.get(val),word2);
			if(compare.levDistance(word1.get(val),splitWord)>new Solutions().threshold)
			{
				splitWords.add(splitWord);
				word2 = word2.substring(splitWord.length());
				val+=1;
			}
			else if(word2.equals(""))
			{
				keepGoing=false;
			}
			else
			{
				keepGoing=false;
			}
			if(val==word1.size())
			{
				keepGoing=false;
			}
			
		
		}
		return splitWords;
		
	
	}
	/*
	 * Used by the splittingCombined method.
	 * Find the best place in the string to split with respect to Levenshtein distance 
	 * with word1 e.g. word 1 = "(last" and word2 = "(lasttime)" then it should
	 * return "(last"
	*/
	public static String bestSplit(String word1, String word2)
	{
		WordCompareII compare = new WordCompareII();
		// need to create a for loop removing part of a string at a time.
		float maxDist = 0;
		String subWord = "";
		for(int i = 0;i<word2.length();i++)
		{
			String tempWord = word2.substring(0,word2.length()-i);
			float tempDist = compare.levDistance(word1,tempWord);
			if(tempDist>=maxDist)
			{
				subWord = tempWord;
				maxDist = tempDist;
			}
		}
		return subWord;
	}
	
	// converts a string into an array of strings depending on the given regular expression
	private static ArrayList<String> wordToArrayList(String word,String regex)
	{
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(word);
		ArrayList<String> wArray = new ArrayList<String>();
		// Reference: http://www.vogella.com/tutorials/JavaRegularExpressions/article.html
		while(m.find())
		{
			 wArray.add(m.group());
		}
		return wArray;
	}
	/*
	 * Reference wikipedia article that contained a pseudocode representation of the 
	 * Levenshtein distance that is used here:
	 * https://en.wikipedia.org/wiki/Levenshtein_distance
	 *
	 * Given a correct word -- word1 and OCR word -- word2 find the least amount 
	 * of changes that is required to go from the OCR word to the correct word using 
	 * deletion, substitution and insertion of characters each one of these costs 1.
	 * Once the overall cost is found then it returns: 
	 * 1 - (cost/the total number of characters) 
	 * Here the cost == distance as distance is how far one word is away from the other.
	*/
	public static float levDistance(String word1, String word2)
	{
		
		ArrayList<String> charArray1 = new WordCompareI().wordToArrayList(word1,"[\\p{L}\\w',-\\.\\(\\);]");
		ArrayList<String> charArray2 = new WordCompareI().wordToArrayList(word2,"[\\p{L}\\w',-\\.\\(\\);]");
		int charSize1 = charArray1.size()+1;
		int charSize2 = charArray2.size()+1;
		int[][] levDistanceArray = new int[charSize1][charSize2];
		// preset values in the matrix
		for(int k=0;k<charSize1;k++)
		{
			levDistanceArray[k][0] = k;
		}
		for(int k=0;k<charSize2;k++)
		{
			levDistanceArray[0][k] = k;
		}
		// creates a matrix of cost values
		for(int k=1;k<charSize1;k++)
		{
			for(int j=1;j<charSize2;j++)
			{
				// When the characters are the same no cost
				if(charArray1.get(k-1).equals(charArray2.get(j-1)))
				{
					levDistanceArray[k][j] = levDistanceArray[k-1][j-1];
				}
				// When the characters are not the same find the least costly change.
				else
				{   
					levDistanceArray[k][j] = Math.min(levDistanceArray[k-1][j]+1, levDistanceArray[k][j-1]+1); // insertion,deletion 
					levDistanceArray[k][j] = Math.min(levDistanceArray[k][j], levDistanceArray[k-1][j-1]+1); // substitution
				}
			}
		}
		float totalLength = charSize1 + charSize2 - 2;
		// Bottom right of the matrix always contains the lowest overall cost.
		float distance = new Integer(levDistanceArray[charSize1-1][charSize2-1]).floatValue();
		return (1-(distance/totalLength));
	}
	/*
	 * Used by the correctOCR method.
	 *
	 * paragraph1 param -- The paragraph that contains the correct version of the OCR paragraph
	 * paragraph2 param -- The OCR paragraph
	 * stack param -- contains the solutions to convert the OCR to the correct paragraph.
	 * See WordAction class for more details on the stack param as WordAction class is called
	 * to update this stack.
	 *
	 * Goes through the correct text and OCR text until there is a word that is different 
	 * according to the Levenshtein distance. When this occurs it calls the the WordAction class
	 * method actionToTake to find the best solution to resolve the word difference this solution 
	 * is then added to the stack and returned to the calling correctOCR method in this class. 
	*/
	private static Stack<String[]> scanParagraph(String paragraph1, String paragraph2, Stack<String[]> stack)
	{
		WordCompareII compare = new WordCompareII();
		// Plus 1 as requires an OCR word array that does not
		// get modified by the rules at each stage
		int numberOfArrays = compare.numOfSol+1;
		ArrayList<String> stringArray1 = compare.wordToArrayList(paragraph1,"[\\p{L}\\w',-\\.\\(\\);]+");
		ArrayList<String> stringArray2 = compare.wordToArrayList(paragraph2,"[\\p{L}\\w',-\\.\\(\\);]+");
		ArrayList<ArrayList<String>> arrayList2 = new ArrayList<ArrayList<String>>();
		for(int i=0;i<numberOfArrays;i++)
		{
			arrayList2.add(compare.wordToArrayList(paragraph2,"[\\p{L}\\w',-\\.\\(\\);]+"));
		}
		// Finds the largest array list
		int size = stringArray1.size();
		if(stringArray2.size()>size)
		{
			size = stringArray2.size();
		}
		// Go through the two word lists a word at a time
		for(int i = 0;i < size;i++)
		{
			String corWord;
			String wrongWord;
			// Ensures that each array list is the same size to avoid index out of bounds
			if(stringArray1.size()-1<i)
			{
				corWord = "";
				stringArray1.add("");
				wrongWord = stringArray2.get(i);
			}
			else if(stringArray2.size()-1<i)
			{
				wrongWord = "";
				for(int j=0;j<numberOfArrays;j++)
				{
					arrayList2.get(j).add("");
				}
				corWord = stringArray1.get(i);
			}
			else
			{
				corWord = stringArray1.get(i);
				wrongWord = stringArray2.get(i);
			}
			
			
			// OCR word does not match the correct text
			if(compare.levDistance(corWord,wrongWord)<1)
			{
				stack = new WordAction().actionToTake(stack,stringArray1,arrayList2,i);
				return stack;
				
			}
		}
		
		return stack;
		
	
	}
	// If substituting is the best choice then this method will convert the old sentence to the new sentence
	public static ArrayList<String> substitute(ArrayList<String> wArray2, String word1, int val) 
	{
		wArray2.remove(val);
		wArray2.add(val,word1);
		return wArray2;
	
	}
	// If merge is the best choice then this method will implement the merge into the OCR sentence
	public static ArrayList<String> merge(ArrayList<String> wArray2, String word1, int val) 
	{
		wArray2.remove(val+1);
		wArray2.remove(val);
		wArray2.add(val,word1);
		return wArray2;
	}
	/* 
	 * If split is the best choice then this method will implement the split into the OCR sentence.
	 * Uses the splittingCombined method in this class.
	*/
	public static ArrayList<String> splitting(ArrayList<String> wArray1, ArrayList<String> wArray2, int val)
	{
		WordCompareII compare = new WordCompareII();
		
		String word2 = wArray2.get(val);
		
		ArrayList<String> splitWords = compare.splittingCombined(wArray1,word2,val);
		
		for(int y = 0;y<splitWords.size();y++)
		{
		    if(y==0)
		    {
		    	wArray2.remove(val);
		    }
		    wArray2.add(val+y,wArray1.get(val+y));
		}
		return wArray2;
	}
	// If deleting is the best choice then this method will delete the word that is to be deleted.
	public static ArrayList<String> delete(ArrayList<String> wArray2, int val)
	{
		wArray2.remove(val);
		return wArray2;
	}
	// If inserting is the best choice then this method will insert the word that is to be inserted.
	public static ArrayList<String> insert(String word1, ArrayList<String> wArray2, int val)
	{
		wArray2.add(val,word1);
		return wArray2;
	}
	/*
	 * Given paragraph1 the correct paragraph and paragraph2 the OCR paragraph,
	 * return the OCR paragraph that has been corrected according to the rule 
	 * based system that has been created of which the rules are in the Solutions class. 
	 * At the end of the method it prints out all of the XML that states the changes 
	 * that are required to the OCR text to convert it into correct text. 
	*/
	private static String correctOCR(String paragraph1, String paragraph2)
	{
		Stack<String[]> stack = new Stack<String[]>();
		WordCompareII compare = new WordCompareII();
		
		ArrayList<String> stringArray1 = compare.wordToArrayList(paragraph1,"[\\p{L}\\w',-\\.\\(\\);]+");
		ArrayList<String> stringArray2 = compare.wordToArrayList(paragraph2,"[\\p{L}\\w',-\\.\\(\\);]+");
		
		int size = stringArray1.size();
		if(stringArray2.size()>size)
		{
			size = stringArray2.size();
		}
		// I needs to be changed depending on the size of the paragraph. 
		for(int i = 0;i < 40;i++)
		{
			stack = compare.scanParagraph(paragraph1, paragraph2, stack);
			/*
			 * If the correct and ocr paragraph are the same or the stack has not changed
			 * in size then finish as the OCR paragraph has been corrected.
			*/
			if(stack.empty()||stack.size()!=i+1)
			{
				i=42;
			}
			// Correct the OCR word according to the best solution/rule.
			else
			{
				stringArray2 = compare.wordToArrayList(paragraph2,"[\\p{L}\\w',-\\.\\(\\);]+");
				String[] info = stack.peek();
				int val = Integer.valueOf(info[4]);
				switch(info[3])
				{
					case "SUB":
						stringArray2 = compare.substitute(stringArray2,info[0],val);
						break;
					case "MERGE":
						stringArray2 = compare.merge(stringArray2,info[0],val);
						break;
					case "SPLIT":
						stringArray2 = compare.splitting(stringArray1,stringArray2,val);
						break;
					case "DELETE":
						stringArray2 = compare.delete(stringArray2,val);
						break;
					case "INSERT":
						stringArray2 = compare.insert(stringArray1.get(val),stringArray2,val);
						break;
					case "LEAVE":
						System.out.println(info[1]+ " "+info[0]);
						System.out.println("Leave");
						break;
			
				}
				paragraph2 = new Solutions().arrayListToString(stringArray2);
			}
			
		}
		/*
		 * Count is required as the size of the stack dynamically changes when
		 * an element is poped off.
		*/
		int count = stack.size();
		for(int i = 0;i<count;i++)
		{
			String[] stackOutput = stack.pop();
			String xml = stackOutput[2];
			System.out.println("XML: "+xml);
		}
		return paragraph2;
	}
	
	public static void main(String[] args)
	{
		/*String paragraph1 = "THEATRE ROYAL, HAYMARKET.-Benjamin\n"+
"Webster, Lessee.-Last six Nights of Performance before the\n"+
"Easter Holidays.-Mr. J. Parry every evening.-On Monday, Taming\n"+
"of the Shrew. After which, will be produced, a new Drama in two\n"+
"acts, called Lolah; or the Wreck Light; with A Kiss in the Dark.-\n"+
"Tuesday, Taming of the Shrew; with a Drama; and Out of Place.-\n"+
"Wednesday, Taming of the Shrew; with a Drama; and the Trumpeter's\n"+
"Daughter.-Thursday, Taming of the Shrew; with Josephine.-Friday,\n"+
"Taming of the Shrew; with a Drama; and the Trumpeter's Daughter.-\n"+
"Saturday, Taming of the Shrew; and other Entertainments.\n"+
"Orchestra Stalls, 5s. each; Boxes, 5s.; Pit, 3s.; Gallery, 2s.; Upper\n"+
"Gallery, 1s. Doors open at half-past Six, commence at Seven.";
		String paragraph2 = "TIHEATRE ROYAL, HAYMARKEET. Benjamin\n"+
"1IL' Webster, Lessee. Las six Nights of Performance before the\n"+
"Easter HEaldays. 3Mr, J. Parry every evelitng. Oit Ionday, Taming\n"+
"of the Shrew. After which, 'will be produced, a new Drama is two\n"+
"acts, called Lolah. or the Wreck Lift; with A Kiss in the Dark,-\n"+
"TuesayTamng ofthe Shrew; wR%~ a Drama; and Out of Place.-\n"+
"Wednesday, Taimingofthe shrerw with a Drama; and the Trumpeter's\n"+
"Dauohter. Thursdal, Taming of the Shrew; vwith Josephine. riday,\n"+
"Taming of the Shrew* with a Drama; andthe Trumpeter's Daughter-\n"+
"Saturday, Taming of the Shrew; and other Entertainments.\n"+
"Orchestra Stalls, Is. each; Boxes, Os.; lt, 3s.; Gallery, 2H.; Upper\n"+
"Gallery, Is. Dears open at balt-past Six, commence Seven.";
		/*String paragraph1 = "PRINCESS'S THEATRE, OXFORD-STREET.-\n"+
"Rossini's Tragic Opera of \"Otello\", a great and decided hit.-\n"+
"Last Week before Easter.-Last Week of Mr. and Mrs. Keeley.\n"+
"On Monday, Otello, with the Young Scamp, and the New Ballet of\n"+ 
"Leaola-Tuesday, Otello, with Gone to Texas, and the Ballet of Leola-\n"+
"Wednesday, Otello, a Concert, and entertainments in which Mr. and\n"+
"Mrs. Keeley will perform-Thursday, a variety of entertainments, for\n"+
"the Benefit of Mr. and Mrs. Keeley-Friday, Otello, a Farce, and the\n"+
"Ballet, being the last night before Easter.\n"+
"Private Boxes and places may be had of Mr. Massingham at the theatre.";

		String paragraph2 = "P RINCESS'S THEATRE, OXFORD STREET.-\n"+
"Rossini's Tragic Opera of \" Otello,\" a great and decided hit.-\n"+
"Last Week before Easter. Last Week of Mr. and Mrs. Keeley.\n"+
"On Monday, Otello, with the Young Scamp, and the New Ballet ol\n"+
"Leola Tuesday, Oteilo, with Gone to Texas, and the Ballet of Leola-\n"+
"Wednesday, Otello, a Concert, and entertainments in which Mr. and\n"+
"Mrs. Keeley Ivill perform Thursday, a variety of entertainments, fol\n"+
"the Benefit of Mr. and Mrs. Keeley Friday, Otello, a Farce, and the\n"+
"Ballet, being the last night before Easter.\n"+
"Private Boxes and places may be had of Mr. Massingham at the\n"+
"theatre.";*/
		String paragraph1 = "THEATRE ROYAL, DRURY LANE.-\n"+
"On Monday, THE FAVORITE. Ferdinand, M. Duprez, And\n"+
"THE BEAUTY OF GHENT.\n"+
"On Tuesday and Saturday (last time), THE BOHEMIAN GIRL, and\n"+
"the new ballet of THE BEAUTY OF GHENT.\n"+
"On Wednesday, an Opera, in which Mons. Duprez will appear, and\n"+
"THE BEAUTY OF GHENT.\n"+
"On Thursday, THE BOHEMIAN GIRL, a Concert, and a New\n"+
"Divertissement, being for the benifit of Mr. Bunn.\n"+
"On Friday, an Opera, in which Mons. Duprez will appear (positively\n"+
"the last night of his engagement).\n"+
"Dress Boxes, 7s., Second Price, 3s. 6d.; Upper Circle, 5s., Second\n"+
"Price, 3s.; Pit, 3s., Second Price, 2s.; Middle Gallery, 2s., Second Price,\n"+
"1s.; Upper Gallery, 1s., Second Price, 6d.";

		String paragraph2 = "TIHEATRE ROYAL, DRURY LANE.-\n"+
"T On Monday, THE FAVORITE. Ferdinand, M. Duprez, And\n"+
"THE BEAUTY OF GHIENT.\n"+
"Onl Tuesday and Saturday (lasttirmre),THE BOHEMIAN GIRIL and the new ballet of THE BEAUTY OF GHEfiT\n"+
"On Wednesday. an Opera, in which Mons. buprez will appear, and\n"+
"THE B3EAUTY OF GHENT.\n"+
"On Thursday, THE BOHEMIAN GIRL, a Concert, and a New\n"+
"Disertissetnent, being for the benefit of Mr. Bunn.\n"+
"On Friday, an Opera, in which Mons. Duprez will appear (positively\n"+
"the last night of his engagement).\n"+
"Dress Boxes, 7s., Second Price, 39. Cd.; Upper Circle, 5s., Second\n"+
"Price, 3s.; Pit,3s., Second Price, 2s.; Middle Gallery, 2s., Second Price,\n"+
"Is.; Upper Gallery, nothing is., Second Price, 6d.";
		
		String test = new WordCompareII().correctOCR(paragraph1,paragraph2);
		System.out.println(new WordCompareII().levDistance(test,paragraph1));
	}
	
	
	


}