// Dorothy Carter	Jan. 2016

package twitter;

import java.net.*;
import java.util.Scanner;
import java.io.*;

import oauth.signpost.*;
import oauth.signpost.basic.*;
import oauth.signpost.http.HttpParameters;

public class PostStatus {
	protected static URL goHere;
	protected static OAuthConsumer consumer;
	protected static OAuthProvider provider;
	
	public static void main(String[] args) throws IOException {
		consumer = new DefaultOAuthConsumer(Keys.consumerKey, Keys.consumerSecret);
		
		getAccessToken();
		
		GenerateTweet tweet = new GenerateTweet();
		
		String params = "status=" + OAuth.percentEncode(tweet.status);
		HttpParameters encodedParams = new HttpParameters();
		
		encodedParams.put("status", OAuth.percentEncode(tweet.status));	
		consumer.setAdditionalParameters(encodedParams);
		
		goHere = new URL("https://api.twitter.com/1.1/statuses/update.json");
		
		try {
			HttpURLConnection cnx = (HttpURLConnection) goHere.openConnection();
			
			cnx.setRequestMethod("POST");
			cnx.setDoOutput(true);
			
			consumer.sign(cnx);
			
			OutputStream os = cnx.getOutputStream();
			os.write(params.getBytes());
			os.flush(); os.close();
			
			//cnx.connect();
			
			if (cnx.getResponseCode() == 200)
				System.out.println("Status posted");
			else
				System.err.println("Status not posted. Response code: " + cnx.getResponseCode());

		} catch (Exception e) {e.printStackTrace(System.err);}
	}
	
	public static void getAccessToken() {
		Scanner scanPin = new Scanner(System.in);
		provider = new DefaultOAuthProvider(
                "https://api.twitter.com/oauth/request_token",
                "https://api.twitter.com/oauth/access_token",
                "https://api.twitter.com/oauth/authorize");
		try {
			String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
			
			System.out.println("Visit:\n" + authUrl + "\n... and grant this app authorization");
			System.out.println("Enter the PIN code and hit ENTER when you're done:");
			String pin = scanPin.nextLine();
			
			provider.retrieveAccessToken(consumer, pin);
		} catch (Exception e) {e.printStackTrace(System.err);}
	}
}
