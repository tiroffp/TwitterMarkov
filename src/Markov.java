import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Random;
import java.nio.charset.Charset;

public class Markov {
    // Hashmap
    //public static Hashtable<String, Vector<String>> markovChain = new Hashtable<String, Vector<String>>();
    public static Hashtable<String, ArrayList<String>> markovChain = new Hashtable<String, ArrayList<String>>();
    public static ArrayList<String> generatedSentences = new ArrayList<String>();
    static Random rnd = new Random();
    static Twitter twitter = TwitterFactory.getSingleton();

    /* 
     * format codes:
     * 0 - no format
     * 1 - simple HTML trailing <br/>
     */
    public static final int FORMATCODE_NONE = 0;
    public static final int FORMATCODE_HTML = 1;

    public static int formatCode = FORMATCODE_NONE;

    public static void main(String[] args) throws IOException, TwitterException {


        int sentences = Integer.parseInt(args[0]);
        formatCode = Integer.parseInt(args[1]);
        int fileArgsStart = 2;

        // Create the first two entries (k:_start, k:_end)
        markovChain.put("_start", new ArrayList<String>());
        markovChain.put("_end", new ArrayList<String>());

        for (int i=fileArgsStart; i<args.length; i++) {

            String filePath0 = args[i];
            FileInputStream file_in0 = new FileInputStream(filePath0);
            BufferedReader reader0  = new BufferedReader(new InputStreamReader( file_in0, Charset.forName("UTF-8")));
            String line = "";

            while ((line = reader0.readLine()) != null) {
                if (line.compareTo("")!=0) {
                    addWords(line);
                }
            }

            reader0.close();
        }

        System.out.println("Files successfully added\nAttempting sentence generation...");

        while (sentences > 0 ) {
            System.out.println("..." + sentences + " left to generate");
            generatedSentences.add( generateTweet() );
            sentences--;
        }

        for (int i=0; i<generatedSentences.size(); i++) {
            //twitter.updateStatus(generatedSentences.get(i));
            System.out.println(generatedSentences.get(i));
        }
    }

    /*
     * Add words
     */
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

                //Vector<String> startWords = markovChain.get("_start");
                ArrayList<String> startWords = markovChain.get("_start");
                startWords.add(words[i]);
                System.out.println("START IS" + words[i]);
                //Vector<String> suffix = markovChain.get(words[i]);
                ArrayList<String> suffix = markovChain.get(words[i]);

                if (suffix == null) {

                    if ( i+1 < words.length ) {

                        //suffix = new Vector<String>();
                        suffix = new ArrayList<String>();

                        suffix.add(words[i+1]);
                        markovChain.put(words[i], suffix);
                    }
                }

            } else if (i == words.length-1) {

                //Vector<String> endWords = markovChain.get("_end");
                ArrayList<String> endWords = markovChain.get("_end");

                endWords.add(words[i]);
                System.out.println("END IS" + words[i]);

            } else {

                //Vector<String> suffix = markovChain.get(words[i]);
                ArrayList<String> suffix = markovChain.get(words[i]);

                if (suffix == null) {

                    //suffix = new Vector<String>();
                    suffix = new ArrayList<String>();

                    suffix.add(words[i+1]);
                    markovChain.put(words[i], suffix);
                } else {
                    suffix.add(words[i+1]);
                    markovChain.put(words[i], suffix);
                }
            }
            System.out.println(words[i]);
        }
    }


    /*
     * Generate a markov phrase
     */
    //public static void generateTweet() {
    public static String generateTweet() {
        // Vector to hold the phrase
        //Vector<String> newPhrase = new Vector<String>();
        ArrayList<String> newPhrase = new ArrayList<String>();

        // String for the next word
        String nextWord = "";

        // Select the first word
        //Vector<String> startWords = markovChain.get("_start");
        ArrayList<String> startWords = markovChain.get("_start");
        int startWordsLen = startWords.size();
        int charsLeft = 0;

        nextWord = startWords.get(rnd.nextInt(startWordsLen));
        newPhrase.add(nextWord);
        charsLeft  = charsLeft + nextWord.length();

        // Keep looping through the words until we've reached the end
        try {
            while (charsLeft < 140) {
                System.out.println(nextWord);
                if ( nextWord.compareTo("") != 0 ) {
                    //Vector<String> wordSelection = markovChain.get(nextWord);
                    ArrayList<String> wordSelection = markovChain.get(nextWord);

                    //try {
                    int wordSelectionLen = wordSelection.size();
                    nextWord = wordSelection.get(rnd.nextInt(wordSelectionLen));

                    if ( nextWord.compareTo("") != 0 ) {
                        newPhrase.add(nextWord);
                        charsLeft  = charsLeft + nextWord.length();
                    }
                    else {
                        if ( wordSelectionLen <= 1 ) {
                            //break;
                        }
                        while ( nextWord.compareTo("")==0 ) {
                            nextWord = wordSelection.get(rnd.nextInt(wordSelectionLen));
                        }

                    }
                    //} 
                    //catch (Exception e) {
                    //}
                }
                else {

                    //break;
                }

                //System.out.println("......checking nextWord.charAt(nextWord.length()-1): [" + nextWord.charAt(nextWord.length()-1) + "]");

            }
        }
        catch (Exception e) {
            //System.out.println("Exception caught: " + e);
            //System.out.println("Next word was: [" + nextWord + "]");
        }

        String newPhraseString = newPhrase.toString();
        String strToOutput = newPhraseString.substring(1, newPhraseString.length()-1);
        strToOutput = strToOutput.replace(",", "");

        //printFormattedString(strToOutput);
        return printFormattedString(strToOutput);
    }


    public static String printFormattedString(String str) {
        String s = str;
        if ( formatCode == FORMATCODE_NONE ) {
        }
        else if ( formatCode == FORMATCODE_HTML ) {
            s = s + "<br/>";
        }
        else {
        }
        return s;
    }
}
