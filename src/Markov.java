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
    // Hashmap storing each word and its suffixes
    public static Hashtable<String, ArrayList<String>> markovChain = new Hashtable<>();
    static Random rnd = new Random();
    static Twitter twitter = TwitterFactory.getSingleton();
    private static String lastWord;
    private static ArrayList<String> lastWordSuffix;

    public static void main(String[] args) throws IOException, TwitterException {
        // Create the first two entries (k:_start, k:_end)
        markovChain.put("_start", new ArrayList<>());
        markovChain.put("_end", new ArrayList<>());
        String filePath0 = args[0];
        FileInputStream file_in0 = new FileInputStream(filePath0);
        BufferedReader reader0  = new BufferedReader(new InputStreamReader( file_in0, Charset.forName("UTF-8")));
        String line;
        while ((line = reader0.readLine()) != null) {
            if (line.compareTo("")!=0) {
                addWords(line);
            }
        }
        reader0.close();
        String tweet = generateTweet();
        System.out.println(tweet);
//        twitter.updateStatus(tweet);
    }
    /*
     * Add words to markovChain and create suffix lists
     */
    public static void addWords(String phrase) {
        // put each word into an array
        String[] words = phrase.split(" ");
        // Loop through each word, check if it's already added
        // if its added, then get the suffix vector and add the word
        // if it hasn't been added then add the word to the list
        // if its the first or last word then select the _start / _end key
        for (int i=0; i < words.length; i++) {
            // Add the start and end words to their own
            if (i == 0) {
                ArrayList<String> startWords = markovChain.get("_start");
                startWords.add(words[i]);
            } else if (i == words.length-1) {
                ArrayList<String> endWords = markovChain.get("_end");
                endWords.add(words[i]);
            }
            ArrayList<String> suffix = markovChain.get(words[i]);
            if (suffix == null) {
                suffix = new ArrayList<>();
            }
            if (i == words.length - 1) {
                lastWord = words[i];
                lastWordSuffix = suffix;
            } else {
                suffix.add(words[i + 1]);
                markovChain.put(words[i], suffix);
            }
        }
        if (lastWordSuffix != null) {
            lastWordSuffix.add(words[words.length -1]);
            markovChain.put(lastWord, markovChain.get("_start"));
        }
    }
    /*
     * Generate a markov phrase
     */
    public static String generateTweet() {
        // Array to hold the phrase
        ArrayList<String> newPhrase = new ArrayList<>();
        // String for the next word
        String nextWord;
        // Select the first word
        ArrayList<String> startWords = markovChain.get("_start");
        int startWordsLen = startWords.size();
        int charsLeft = 0;
        nextWord = startWords.get(rnd.nextInt(startWordsLen));
//         Keep looping through the words until we've reached the end
        while (charsLeft + nextWord.length() < 140) {
            newPhrase.add(nextWord);
            charsLeft  = charsLeft + nextWord.length() + 1;
            ArrayList<String> wordSelection = markovChain.get(nextWord);
            int wordSelectionLen = wordSelection.size();
            nextWord = wordSelection.get(rnd.nextInt(wordSelectionLen));

        }
        String newPhraseString = newPhrase.toString();
        String strToOutput = newPhraseString.substring(1, newPhraseString.length()-1);
        strToOutput = strToOutput.replace(",", "");
        strToOutput = strToOutput.replace("@", "#");
        System.out.println(strToOutput.length());
        return strToOutput;
    }
}
