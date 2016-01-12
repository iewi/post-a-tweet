// Dorothy Carter	Jan. 2016

package twitter;

import java.net.*;

import java.io.*;

import oauth.signpost.*;
import oauth.signpost.basic.*;
import oauth.signpost.http.HttpParameters;

public class PostStatus {
	protected static URL goHere;
	protected static OAuthConsumer consumer;
	public static void main(String[] args) throws IOException {
		consumer = new DefaultOAuthConsumer("",""); // get your own keys
		consumer.setTokenWithSecret("","");
		
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
			
			cnx.connect();
			
			if (cnx.getResponseCode() == 200)
				System.out.println("Status posted");
			else
				System.err.println("Status not posted. Response code: " + cnx.getResponseCode());

		} catch (Exception e) {e.printStackTrace(System.err);}
	}
}
