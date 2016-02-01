# post-a-tweet
A Java program to generate a tweet using a markov chain and post it to twitter

You'll need both Gson and OAuth Signpost to run it. You'll also need to create the files markovChains.json and markovMaps.json.

There are 3 separate programs here: GrabTweets, which grabs a bunch of tweets and writes them to a file; GenerateTweet, which parses the data in the file to make a Markov chain (which it writes to a file so it doesn't necessarily have to parse it again) and generates a tweet using the Markov chain; and PostStatus, which uses GenerateTweet to generate a tweet and then posts it to Twitter.

~~I'm working on phasing out the Tweet class~~ (finished that, woohoo),and also putting in an authentication thing in PostStatus so anyone can post a status. It might take a while, though. UPDATE: I'm most of the way done with the authentication thing, but I need to deal with a bug with the Scanner class.
