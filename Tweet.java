// Dorothy Carter	Jan. 2016

package twitter;

import com.google.gson.*;

public class Tweet {
	protected String text;
	protected int id;
	
	public Tweet(String created, int id, String id_str, String text) {
		this.text = text;
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	public int getID() {
		return id;
	}
	
	public String toString() {
		return "Tweet: " + id + ": " + text;
	}
	
	public static Tweet[] parseData(String data) {
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		
		JsonArray array = parser.parse(data).getAsJsonArray();
		Tweet[] tweets = new Tweet[GrabTweets.COUNT+1];
		int index = 0;
		for (JsonElement thing : array) {
			tweets[index] = gson.fromJson(thing, Tweet.class);
			index++;
		}
		
		return tweets;
	}
	
	public static String grabTextFromData(String data) {
		String res = "";
		
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		
		JsonArray array = parser.parse(data).getAsJsonArray();
		Tweet tw;
		for (JsonElement thing : array) {
			tw = gson.fromJson(thing, Tweet.class);
			res += tw.getText() + " \n";
		}
		
		return res != null ? res : "";
		
	}
}
