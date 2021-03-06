// Dorothy Carter	Jan. 2016
// grabs celebrity tweets from Twitter's streaming api and writes them to markovChains.json

package twitter;

import com.google.gson.*;

import oauth.signpost.OAuth;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.http.HttpParameters;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.net.ssl.SSLException;

public class GrabTweets {
	public static final int COUNT = 1000;
	protected Gson gson;
	protected File target;
	
	public GrabTweets() throws IOException {
		String choice;
		Scanner grabChoice = new Scanner(System.in);
		System.out.print("Are you sure you want to erase your current file? Enter Y or N: ");
		choice = grabChoice.nextLine();
		grabChoice.close();
		if (choice.equalsIgnoreCase("y")) {		
			gson = new Gson();
			target = new File("markovChains.json");
			PrintWriter out = new PrintWriter(target);
			try {
				System.out.println("Setting up");
				DefaultOAuthConsumer consumer = new DefaultOAuthConsumer(Keys.consumerKey, Keys.consumerSecret);
				
				consumer.setTokenWithSecret(Keys.accessKey, Keys.accessSecret);
				
				System.out.println("Connecting");
				HttpParameters encodedParams = new HttpParameters();
				
				// by default, it follows a bunch of celebrities
				String followUsers = "25365536,23617610,17919972,24929621,14230524,27195114,"+
									 "157140968,166739404,23669909,44409004,15485441,"+
									 "34507480,23083404,184910040,169686021,32959253,115485051,"+
									 "25521487,101928415";
				String params = "language=en&follow="+OAuth.percentEncode(followUsers);
				encodedParams.put("language", "en");
				encodedParams.put("follow", OAuth.percentEncode(followUsers));
				consumer.setAdditionalParameters(encodedParams);

				URL goHere = new URL("https://stream.twitter.com/1.1/statuses/filter.json");
				
				
				HttpURLConnection tweets = (HttpURLConnection) goHere.openConnection();
				
				tweets.setRequestMethod("POST");
				tweets.setDoOutput(true);				
				consumer.sign(tweets);
				
				OutputStream os = tweets.getOutputStream();
				os.write(params.getBytes());
				os.flush(); os.close();
				
				tweets.connect();
				
				for (String str : readData(tweets).split("\n")) {
					out.println(str);
				}
			} catch (Exception e ) {
				e.printStackTrace(System.err);
			}
			finally {
				out.close();
				System.out.println("Finished");
			}
		}

	}
	
	public String readData(URLConnection cnx) throws IOException {
		System.out.println("Reading data");
		String temp = "", fin = "";
		int count=0;
		BufferedReader read = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
		try {
			while (temp != null && count<COUNT) {
				temp = read.readLine();
				temp = temp.split(",\"source\"")[0];
				temp = temp.split(",\"text\":")[1];				
				if (count % 10 == 0)
					fin += temp + "]\n[";
				else
					fin += temp + ",";
				
				count++;
				if (count % 30 == 0)
					System.out.println(count + " ");
				else
					System.out.print(count + " ");
			}
		} catch (SSLException e) {
			System.err.println("\n" + e.getMessage());
		}
		fin = "[" + fin.substring(0, fin.length()-1)
				       .replace("},}", "}").replace("},}", "}")+ "]";
		System.out.println("\nPrinting data");
		return fin;
	}
	
	public static void main(String[] args) throws IOException {
		new GrabTweets();
	}

}
