Read me file, Author: Andrew Moore, Date: 14/8/2015

Abstract:

Attempts to find the errors in the OCR text against the Ground Truth text on a word level. 
From this output XML that describes the changes that needs to occur to get from OCR to Ground
Truth text for each word.

Improvements that can be done:

This is relevant to the Solutions.solution2 method:
Done something similar to the splitting method that checks whether 
the current word + (word+1) + (word+2) + (word+n) is the best merge 
as it may require more than just one merge.

This is relevant to the Solutions.solution3 method:
Could be to only split if the OCR word is at a significant length difference
to the correct word, of which this lengthDiff threshold can be varied in one masive
loop of which this can be said for the overall Levenshtein threshold.   

As stated just above create a loop within the WordCompareII.correctOCR method 
that tests the solutions/rules on different threshold levels as each text may 
have a different optimal threshold level. This could also be done for the lengthDiff
threshold. 

Other improvements are adding two arguments at the command line one being the 
file address to the ground truth/correct text file and the other the OCR text 
file destination address, instead of manually adding text into a String variable
in the WordCompareII class. 

The XML in the Solutions class needs to be re-written to be used with the VARD tool.

How to add a rule/solution to the code where Y here means the new unique rule/solution number:
1) Add a global private variable called solYXML and public get method to get this 
global variable in the Solutions class.
2) Add the rule/solution method in the solutions class and call it solutionY
3) Add a case statement in the switch statement around line 66 in the WordAction class
under the case: caseY, then add the same case the switch statement below around line 153
and info[2] = solutions.getYXML() and info[3] should equal a string value that is 
meaningful to what the rule does e.g. "Merge".
4) In WordCompareII class add one onto the value of the global variable numOfSol so this
variable should now equal Y.
5) In WordCompareII class in the switch statement around line 328 add a case that is equal 
to the String that info[3] equals in WordAction class. Then create a method in this class
that puts your rule/solution in action on the variable stringArray2 then within the new case
statement you created equal stringArray2 to the return value of the method you just created.