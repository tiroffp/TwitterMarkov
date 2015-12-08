import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Test{

    //if something goes wrong, we might see a TwitterException
    public static void main(String... args) throws TwitterException{

        //access the twitter API using your twitter4j.properties file
        Twitter twitter = TwitterFactory.getSingleton();

        //send a tweet
        Status status = twitter.updateStatus("Initializing");

        //print a message so we know when it finishes
        System.out.println("Done.");
    }
}