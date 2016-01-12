// Dorothy Carter	Jan. 2016

package twitter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class GenerateTweet {
	Map<String, ArrayList<String>> words;
	Random gen;
	String status;
	
	public GenerateTweet() {
		// this method generates a tweet based on the tweets written in the markovChains.json file
		words = new HashMap<String, ArrayList<String>>();
		gen = new Random();
		
		try {
			grabFromMap();
			
			Scanner scan = new Scanner(System.in);
			System.out.print("Do you want to parse your data again? Enter y or n: ");
			if (scan.next().equalsIgnoreCase("y")) {
				parseData();
			}
			writeMap();
			scan.close();
			
			
			String current, tweet = "";
			
			current = nextWord();
			
			// while the tweet is less than 140 characters and current is not the empty string,
			// grab the next word and update the tweet string
			do {
				current = nextWord(current);
				if (current.length() + tweet.length() < 139)
					tweet += current + " ";
				else
					break;
			} while (!current.equals("") && tweet.length() < 140);
			
			System.out.println(tweet);
			status = tweet;
			
		} catch(FileNotFoundException e) {System.err.println("File not found");}
		catch(IOException e) {System.err.println("IO exception");}
	}
	
	public String nextWord() {
		int num = gen.nextInt(words.get("").size());	
		return words.get("").get(num);
	}
	
	public String nextWord(String thisWord) {		
		int num = gen.nextInt(words.get(thisWord).size());	
		return words.get(thisWord).get(num);
		
	}
	
	public void parseData() throws FileNotFoundException {
		// this method parses the json file markovChains.json into a 2-dimensional array
		// associates the words with the words which follow

		Scanner scan = new Scanner(new File("markovChains.json"));
		String[] tmp = new String[(GrabTweets.COUNT/5)+1];
		int current = 0;
		
		// populating tmp with data from the file
		while (scan.hasNextLine() && current < tmp.length) {
			try {
				tmp[current] = Tweet.grabTextFromData(scan.nextLine());
				current++;
			} catch (NullPointerException e) {break;} // it's probably hit the end of the file
		}
		scan.close();
		
		while(current < tmp.length) {tmp[current] = ""; current++;} // finish up populating tmp
		
		// separating tweets into words
		String[][] raw_data = new String[tmp.length][1];
		for (int i=0; i<tmp.length;i++) {
			raw_data[i] = tmp[i].split(" ");
		}
		
		// strip of punctuation, change all cases to lowercase except I
		// should make separate method for this, because of words like I'm, I'd, there's, etc
		for (int i=0; i < raw_data.length; i++) {
			formatTweet(raw_data[i]);
		}
		
		associate(raw_data);
	}
		
	public void associate(String[][] data) {
		ArrayList<String> initial = new ArrayList<String>();
		// iterate through each array
		for (String[] array : data) {
			// go through each word until a non-trivial word is found
			int c = 0;
			try {
				while (array[c].contains("http") || array[c].contains("@") || array[c].equals("rt")) {
					c++;
				}
				
				// if initial doesn't already contain the word, add the empty string
				// and associate it with the next non-trivial word
				initial.add(array[c]);
				//assert words != null;
				words.put("", initial);
			}
			catch (IndexOutOfBoundsException e) {} // if there are no valid words in this tweet, then don't do anything.
			
			// iterate through all the words in the array
			for (int num=0; num<array.length; num++) {
				// clear the collection so as to be able to populate a new one for each word
				ArrayList<String> collec;
				
				String word = array[num];
				
				if (word.contains("http") || word.contains("@") || word.equals("rt")) {} // these define trivial words
				else {
					// if the word is not a key in the map, it'll have to be assigned
					if (!words.containsKey(word))
						collec = new ArrayList<String>();
					// the word is already in the map
					else
						collec = words.get(word); // since the word already exists, it has a collection associated with it
					
					try {
						// go to the next word until a word is found that is a non-trivial word
						c = 1;
						while (array[num+c].contains("http") || array[num+c].contains("@") || array[num+c].equals("rt")) {
							c++;
						}
						collec.add(array[num+c]);
						
					} catch (IndexOutOfBoundsException e) {
						// this means we've come to the end of the array and there's no next word
						// so put an empty string into the collection
						collec.add("");
					}
						
					words.put(word, collec);
				}
			}
		}		
	}
	
	public void formatTweet(String[] tweet) {
		for (int i=0; i<tweet.length; i++) {
			for (String key : replaceWith().keySet()) {
				tweet[i] = tweet[i].replace(key, replaceWith().get(key));
			}
	
			if (!whiteList().contains(tweet[i]))
				tweet[i] = tweet[i].replace("\'", "").toLowerCase();
		}				
	}
	
	private Set<String> whiteList() {
		Set<String> okay = new HashSet<String>();
		okay.add("I'd"); okay.add("you'd"); okay.add("he'd");
		okay.add("she'd"); okay.add("we'd"); okay.add("they'd");
		okay.add("I'm"); okay.add("you're"); okay.add("he's");
		okay.add("she's"); okay.add("we're"); okay.add("they're");
		okay.add("there's"); okay.add("don't"); okay.add("shouldn't");
		okay.add("can't"); okay.add("wouldn't"); okay.add("couldn't");
		okay.add("I've"); okay.add("you've"); okay.add("we've");
		okay.add("they've"); okay.add("I"); okay.add("what's");
		okay.add("where's"); okay.add("who's"); okay.add("what're");
		okay.add("where're"); okay.add("haven't");
		
		return okay;
	}
	
	private Map<String, String> replaceWith() {
		Map<String, String> repl = new HashMap<String, String>();
		repl.put(".", ""); repl.put("!", ""); repl.put("?", "");
		repl.put("&amp;", "&"); repl.put("\n", ""); repl.put("-", "");
		repl.put(";", ""); repl.put(".", ""); repl.put(",", "");
		repl.put(":", ""); repl.put("\"", ""); repl.put("(", "");
		repl.put(")", ""); repl.put("{", ""); repl.put("}", "");
		repl.put("[", ""); repl.put("]", ""); repl.put("~", "");
		repl.put("<", ""); repl.put(">", "");
				
		return repl;
	}
	
	public void writeMap() throws FileNotFoundException, IOException {
		PrintWriter out = new PrintWriter(new File("markovMaps.json"));
		Gson gson = new Gson();
		int count=0;
		
		out.print("{");
		for (String key : words.keySet()) {
			out.print("\"" + key + "\":" + gson.toJson(words.get(key)));
			count++;
			if (!(count==words.keySet().size()))
				out.println(",");
			else
				out.print("}");
		}
		
		out.close();	
	}
	
	public void grabFromMap() throws FileNotFoundException {
		Scanner scan = new Scanner(new File("markovMaps.json"));
		String json = "";
		while (scan.hasNextLine()) {
			json += scan.nextLine();
		}
		scan.close();
		
		Gson gson = new Gson();
		
		Type mapType = new TypeToken<Map<String, ArrayList<String>>>(){}.getType();
		if (gson.fromJson(json, mapType) != null)
			words = gson.fromJson(json, mapType);
		
	}
	
	public static void main(String[] args) {
		new GenerateTweet();
	}
}
