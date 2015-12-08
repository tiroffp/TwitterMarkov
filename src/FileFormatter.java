import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Queue;
import java.nio.charset.Charset;

public class FileFormatter {
    // Hashmap
    public static Hashtable<String, Vector<String>> markovChain = new Hashtable<String, Vector<String>>();
    static Random rnd = new Random();

    public static void main(String[] args) throws IOException {


        //int sentences = Integer.parseInt(args[0]);
        int numLinesRead = 0;
        // Create the first two entries (k:_start, k:_end)
        //markovChain.put("_start", new Vector<String>());
        //markovChain.put("_end", new Vector<String>());

        StringBuffer allLines = new StringBuffer();

        for (int i=0; i<args.length; i++) {

            String filePath0 = args[i];
            FileInputStream file_in0 = new FileInputStream(filePath0);
            //BufferedReader reader0  = new BufferedReader(new InputStreamReader( file_in0, Charset.forName("US-ASCII")));
            BufferedReader reader0  = new BufferedReader(new InputStreamReader( file_in0, Charset.forName("UTF-8")));
            String line = "";

            while ((line = reader0.readLine()) != null) {
                StringBuffer trimLine = new StringBuffer(line.trim());
                if (trimLine.toString().compareTo("")!=0) {
                    trimLine.append(" ");
                    allLines.append(trimLine.toString());
                    numLinesRead++;
                }
            }
            reader0.close();
        }

        //System.out.println(allLines);
        //System.out.println("Total Lines Read: " + numLinesRead);

        String allLinesStr = allLines.toString();
        String[] split1 = allLinesStr.split("\\.");

        Queue<String> stringQueue0 = new LinkedList<String>();
        Queue<String> stringQueue1 = new LinkedList<String>();
        Queue<String> stringQueue2 = new LinkedList<String>();

        for (int i=0; i<split1.length; i++) {
            StringBuffer trimLine = new StringBuffer(split1[i].trim());
            //System.out.println(trimLine);
            if (trimLine.toString().compareTo("")!=0) {
                trimLine.append(".");
                stringQueue0.add(trimLine.toString());
            }
        }

        while (! stringQueue0.isEmpty()) {
            String line = stringQueue0.poll();

//            if ( line.contains(",") ) {
//                String[] split2 = line.split(",");
//                for (int i=0; i<split2.length; i++) {
//                    StringBuffer trimLine = new StringBuffer(split2[i].trim());
//                    if (trimLine.toString().compareTo("")!=0 &&
//                            trimLine.toString().charAt(trimLine.toString().length()-1) != '.') {
//                        trimLine.append(",");
//                    }
//                    stringQueue1.add(trimLine.toString());
//                }
//            }
//            else {
                stringQueue1.add(line);
//            }
        }

        while (! stringQueue1.isEmpty()) {
            String line = stringQueue1.poll();
            //System.out.println(line);
            if ( line.contains("?") ) {
                String[] split2 = line.split("\\?");
                for (int i=0; i<split2.length; i++) {
                    StringBuffer trimLine = new StringBuffer(split2[i].trim());
                    if (trimLine.toString().compareTo("")!=0 &&
                            trimLine.toString().charAt(trimLine.toString().length()-1) != '.' &&
                            trimLine.toString().charAt(trimLine.toString().length()-1) != ',') {
                        trimLine.append("?");
                    }
                    stringQueue2.add(trimLine.toString());
                }
            }
            else {
                stringQueue2.add(line);
            }

            //System.out.println(line);

        }

        while (! stringQueue2.isEmpty()) {
            String line = stringQueue2.poll();
            String noQuotesLine = line.replaceAll("\"", "");
            String trimLine = noQuotesLine.trim();
            System.out.println(trimLine);
        }
        /*
        */

    }
	
	/*
	 * Add words

	public static void addWords(String phrase) {

		// put each word into an array
		String[] words = phrase.split(" ");
				
		// Loop through each word, check if it's already added
		// if its added, then get the suffix vector and add the word
		// if it hasn't been added then add the word to the list
		// if its the first or last word then select the _start / _end key
		
		for (int i=0; i<words.length; i++) {
		
			// Add the start and end words to their own
			if (i == 0) {
				
                Vector<String> startWords = markovChain.get("_start");
				startWords.add(words[i]);
				
				Vector<String> suffix = markovChain.get(words[i]);
				if (suffix == null) {
					
                    if ( i+1 < words.length ) {
                        suffix = new Vector<String>();
                        suffix.add(words[i+1]);
                        markovChain.put(words[i], suffix);
                    }
				}
				
			} else if (i == words.length-1) {
				Vector<String> endWords = markovChain.get("_end");
				endWords.add(words[i]);
				
			} else {	
				Vector<String> suffix = markovChain.get(words[i]);
				if (suffix == null) {
					suffix = new Vector<String>();
					suffix.add(words[i+1]);
					markovChain.put(words[i], suffix);
				} else {
					suffix.add(words[i+1]);
					markovChain.put(words[i], suffix);
				}
			}
		}	
	}
	 */
	
	
	/*
	 * Generate a markov phrase
	public static void generateTweet() {
		// Vector to hold the phrase
		Vector<String> newPhrase = new Vector<String>();
		
		// String for the next word
		String nextWord = "";
				
		// Select the first word
		Vector<String> startWords = markovChain.get("_start");
		int startWordsLen = startWords.size();

		nextWord = startWords.get(rnd.nextInt(startWordsLen));
		newPhrase.add(nextWord);
		
		// Keep looping through the words until we've reached the end
		while (nextWord.charAt(nextWord.length()-1) != '.' ) {
			Vector<String> wordSelection = markovChain.get(nextWord);
             
            try {
                int wordSelectionLen = wordSelection.size();
                nextWord = wordSelection.get(rnd.nextInt(wordSelectionLen));
                newPhrase.add(nextWord);
            } 
            catch (Exception e) {
                
            }
		}
		
        String newPhraseString = newPhrase.toString();
        String strToOutput = newPhraseString.substring(1, newPhraseString.length()-1);
        strToOutput = strToOutput.replace(",", "");
		System.out.println(strToOutput);
	}
	 */
}
