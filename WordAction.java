import java.util.*;
import java.util.Stack;
/*
 * Author -- Andrew Moore
 *
 * Called by WordCompareII class and uses Solutions class.
*/
public class WordAction
{
	/*
	 * PARAMETERS
	 * stack param -- This is also the return object, used to the most recent change that
	 * needs to occur to the OCR paragraph to correct it. The stack contains a String array
	 * of which this contains 5 Strings:
	 * 1) The correct word at the current location in the word Array list
	 * 2) The OCR word at the current location in the word Array list 
	 * 3) The XML of what needs to occur to change the OCR into the correct word
	 * 4) A word that denotes the action that was denoted as the best action to change the 
	 * current OCR word into the correct word. NOTE that even through it is denoted the best
	 * action for the current word it may not be the best overall action.
	 * 5) A String representation of the Integer value that denotes the index of the current 
	 * word in the word Array list.
	 *
	 * perfectArray param -- word list of all the words in the correct version of the OCR 
	 * paragraph
	 *
	 * ocrList param -- A list of word lists of all the words in the OCR paragraph. There has 
	 * to be a word list for each solution test as each solution test changes the values in 
	 * the word array list of which if the same word array list was used it would get modified 
	 * by each solution/rule test that happens therefore making the test inaccurate. Also there
	 * is an additional word list that is not modified by an rules/solutions to be used to make 
	 * sure that the solution/rule test can be applied without any errors occurring. 
	 *
	 * val param -- the index of the current word that is being evaluated
	 *
	 * What it does:
	 * Finds the best method of changing the OCR word into the corrected word while improving 
	 * the overall paragraph Levenshtein distance as just substituting the correct for the OCR
	 * is not necessarily the right choice therefore all options have to be considered. Once the 
	 * best option has been chosen using the solution map variable to find the best option and storing
	 * the best option, this is then added to the stack variable and return variable with the corresponding
	 * XML. 
	*/
	public static Stack<String[]> actionToTake(Stack<String[]> stack,ArrayList<String> perfectArray, ArrayList<ArrayList<String>> ocrList, int val)
	{
		Solutions solutions = new Solutions();
		
		ArrayList<String> ocrArray = ocrList.get(0);
		
		String word1 = perfectArray.get(val);
		String word2 = ocrArray.get(val);
		int solMethods = 6; // number that is one more than the number of methods in solutions
		
		String[] info = new String[5];
		info[0] = word1;
		info[1] = word2;
		info[4] = Integer.toString(val);
		
		float max = 0;
		int solution = 0;
		
		// Tests each solution with error checking to ensure no index is out of bounds.
		HashMap<Integer,Float> solutionMap = new HashMap<Integer,Float>();
		for(int i = 1 ; i < new WordCompareII().numOfSol+1 ; i++)
		{
			switch(i)
			{
				case 1:
					if(val<ocrArray.size()-2)
					{
						solutionMap = solutions.solution1(solutionMap,perfectArray,ocrList.get(i),val,solutions,2);
					}
					else if(val<ocrArray.size()-1)
					{
						solutionMap = solutions.solution1(solutionMap,perfectArray,ocrList.get(i),val,solutions,1);
					}
					else
					{
						solutionMap = solutions.solution1(solutionMap,perfectArray,ocrList.get(i),val,solutions,0);
					}
					break;
				case 2:
					if(val<ocrArray.size()-1)
					{
						solutionMap = solutions.solution2(solutionMap,perfectArray,ocrList.get(i),val,solutions);
					}
					else
					{
						solutionMap.put(2,new Float(0).floatValue());
					}
					break;
				case 3:
					if(word1!="")
					{
						solutionMap = solutions.solution3(solutionMap,perfectArray,ocrList.get(i),val,solutions);
					}
					else
					{
						solutionMap.put(3,new Float(0).floatValue());
					}
					break;
				case 4:
					if(val<ocrArray.size()-2 && val<perfectArray.size()-1)
					{
						solutionMap = solutions.solution4(solutionMap,perfectArray,ocrList.get(i),val,solutions,2);
					}
					else if(val<ocrArray.size()-1)
					{
						solutionMap = solutions.solution4(solutionMap,perfectArray,ocrList.get(i),val,solutions,1);
					}
					else
					{
						solutionMap = solutions.solution4(solutionMap,perfectArray,ocrList.get(i),val,solutions,0);
					}
					break;
				case 5:
					if(val<ocrArray.size()-1 && val<perfectArray.size()-2)
					{
						solutionMap = solutions.solution5(solutionMap,perfectArray,ocrList.get(i),val,solutions,2);
					}
					else if(val<perfectArray.size()-1)
					{
						solutionMap = solutions.solution5(solutionMap,perfectArray,ocrList.get(i),val,solutions,1);
					}
					else
					{
						solutionMap = solutions.solution5(solutionMap,perfectArray,ocrList.get(i),val,solutions,0);
					}
					break;
				default:
					System.out.println("Error this should never happen\n"+
									   "Class: WordAction\n Method: actionToTake");
					break;
			
			}
		}
		/*
		 * Gets the best solution based on the overall Levenshtein distance of the 
		 * correct and OCR paragraph once changed by the solution.
		*/
		for(int i = 1; i<solMethods;i++)
		{
			if(max<solutionMap.get(i))
			{
				max = solutionMap.get(i);
				solution = i;
			}
		}
		/*
		 * Find the best solution and add it to the String[] that will be pushed on
		 *to the stack.
		*/
		switch(solution)
		{
			case 1:
				info[2] = solutions.getS1XML();
				info[3] = "SUB";
				break;
			case 2:
				info[2] = solutions.getS2XML();
				info[3] = "MERGE";
				break;
			case 3:
				info[2] = solutions.getS3XML();
				info[3] = "SPLIT";
				break;
			case 4:
				info[2] = solutions.getS4XML();
				info[3] = "DELETE";
				break;
			case 5:
				info[2] = solutions.getS5XML();
				info[3] = "INSERT";
				break;
			default:
				info[2] = "";
				info[3] = "LEAVE";
				break;
		}
		stack.push(info);
		return stack;
	}
	
}