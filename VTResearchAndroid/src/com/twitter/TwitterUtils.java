package com.twitter;

import oauth.signpost.OAuth;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import android.content.SharedPreferences;

public class TwitterUtils {

	public static boolean isAuthenticated(SharedPreferences prefs) {

		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

		AccessToken a = new AccessToken(token, secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
				Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);

		try {
			twitter.getAccountSettings();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void sendTweet(SharedPreferences prefs, String msg)
			throws Exception {
		String token = prefs.getString(OAuth.OAUTH_TOKEN,
				"1300274934-OTdgoJplMhziQxjk8Vamwjp7ykbLTTf9GbcFIbk");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET,
				"1J8Vd2B2N2IVKzqiYBXozb4HUCoSbxPNGKbYPFRMyZA");

		AccessToken a = new AccessToken(token, secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
				Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		twitter.updateStatus(msg);
	}
	
	public static QueryResult getTweet(SharedPreferences prefs, Query query) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN,
				"1300274934-OTdgoJplMhziQxjk8Vamwjp7ykbLTTf9GbcFIbk");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET,
				"1J8Vd2B2N2IVKzqiYBXozb4HUCoSbxPNGKbYPFRMyZA");

		AccessToken a = new AccessToken(token, secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
				Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		
		 try {
			return twitter.search(query);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		 return null;
	}
}
