import twitter4j.*;
import twitter4j.api.TrendsResources;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
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
        //get the trending topics for boston via the twitter api.
        try {
            List<String> tweets = getTweets();
            for(String tweet : tweets) {
                addWords(tweet);
            }
            String tweet = generateTweet();
            System.out.println(tweet);
            twitter.updateStatus(tweet);
        }
        catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }

    }
    //Finds a suitable trending topic to generate text from. Tweets must be from a trending hashtag that has the maximum
    //number of popular tweets (15) returned from the query
    private static List<String> getTweets() throws TwitterException, NoSuchElementException{
        //Request trends for boston (using boston's geoID of 2367105)
        Trend[] topTrends = twitter.getPlaceTrends(2367105).getTrends();
        Query query = new Query();
        query.setLang("en");
        query.setCount(100);
        query.setResultType(Query.ResultType.mixed);
        for(Trend t : topTrends) {
            query.setQuery(t.getQuery());
            List<Status> results = twitter.search(query).getTweets();
            if (results.size() >= 15) {
                System.out.println(query.getQuery());
                List<String> tweetText = new ArrayList<>();
                for(Status s: results) {
                    tweetText.add(s.getText());
                    System.out.println(s.getText());
                }
                return tweetText;
            }
        }
        throw new NoSuchElementException("no topic fit for analysis :(");
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
        strToOutput = strToOutput.replace("@", "#"); //just to prevent pinging people by accident
        return strToOutput;
    }
}
