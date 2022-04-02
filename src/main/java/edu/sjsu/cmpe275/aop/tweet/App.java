package edu.sjsu.cmpe275.aop.tweet;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.sjsu.cmpe275.aop.tweet.aspect.ValidationAspect;

public class App {
	public static void main(String[] args) {
		/***
		 * Following is a dummy implementation of App to demonstrate bean creation with
		 * Application context. You may make changes to suit your need, but this file is
		 * NOT part of the submission.
		 */


		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
		TweetService tweeter = (TweetService) ctx.getBean("tweetService");
		TweetStatsService stats = (TweetStatsService) ctx.getBean("tweetStatsService");

		try {
			


//			tweeter.follow("bob", "alice");
//			tweeter.follow("james", "alice");
//			//tweeter.follow("james", "alice");
//			//tweeter.block("alice", "james");
//			UUID msg =tweeter.tweet("alice", "Hi");
//			//tweeter.follow("james", "bob");
//			UUID reply1= tweeter.reply("bob", msg, "Hello ALice");
//			UUID reply2 = tweeter.reply("alice", reply1, "How are you doing?");
//			tweeter.block("alice", "bob");
//			UUID reply3 = tweeter.reply("bob", msg, "After alice blocked Bob. hahaha");
//			tweeter.like("bob", msg);
//			UUID reply4 = tweeter.reply("bob", reply2, "no comments!");
//			//UUID reply5 = tweeter.reply("bob", reply3, "this message shouldn't reach!");
//			UUID reply5 = tweeter.reply("bob",msg,"First Messag Comment");
//			UUID reply6 = tweeter.reply("alice", reply5, "Testing");
			
				tweeter.follow("bob", "alice");
	           tweeter.follow("divyaraj", "raj");
	           tweeter.follow("bob", "raj");
	           tweeter.follow("raj", "bob");
	           tweeter.follow("dummy", "raj");
	           tweeter.follow("dummy1", "raj");

	            //should throw IllegalArgumentException
	            //tweeter.follow("", "raj");

//	           UUID msg1 = tweeter.tweet("bob", "first tweet");
//	           UUID reply_msg2 = tweeter.reply("raj", msg1, "second tweet");
//	           UUID reply_reply2 = tweeter.reply("bob", reply_msg2, "bob reply to reply of raj");
//	           tweeter.block("raj", "bob");
//	           
//	           System.out.println("AFTER BLOCKING");
//	           System.out.println("=============================================");
//	           System.out.println("=============================================");
//	           UUID reply_first = tweeter.reply("bob", reply_msg2, "reply after block by blocked");
//	           //should throw exception
//	           UUID reply_second = tweeter.reply("raj", reply_first, "trying to reply after blocking to new message");
	          // should throw exception or not?
	           //UUID reply_third = tweeter.reply("raj", reply_reply2, "sending reply to old message sent before blocking");
			
			  tweeter.follow("bob", "alice");
	           tweeter.follow("divyaraj", "raj");
	           tweeter.follow("bob", "raj");
	           tweeter.follow("dummy", "raj");
	           tweeter.follow("dummy1", "raj");

	           // should throw IllegalArgumentException
//	            tweeter.follow("", "raj");
	           
	          // throw new IOException();
	           
	           UUID msg1 = tweeter.tweet("alice", "first tweet");
	           UUID msg2 = tweeter.tweet("alice", "second tweet");

	           tweeter.block("raj", "bob");
	           UUID msg3 = tweeter.tweet("raj", "third tweet hello world");

	           UUID msg4 = tweeter.tweet("divyaraj", "divyaraj tweet hello world");
	           UUID msg5 = tweeter.tweet("bob", "bob tweets hello world!!");

	           // should through exception
//	            UUID reply_msg1 = tweeter.reply("bob", msg3, "reply tweet");

	           UUID reply_msg2 = tweeter.reply("divyaraj", msg3, "reply tweet1");
	           UUID reply_msg3 = tweeter.reply("divyaraj", msg3, "reply tweet2");
	           UUID reply_msg4 = tweeter.reply("dummy", msg3, "reply tweet3");

	           // should through exception
//	            UUID reply_msg5 = tweeter.reply("dummy2", msg3, "reply tweet3");
	           tweeter.follow("dummy2", "raj");
	           tweeter.follow("dummy2", "alice");
	           tweeter.follow("dummy2", "divyaraj");

	           tweeter.block("raj","alice");
	           tweeter.follow("alice","raj");

	           UUID msg6 = tweeter.tweet("raj", "Raj Fourth tweet hello world");
	           // should throw Exception
//            UUID reply_msg6 = tweeter.reply("alice", msg6, "reply tweet3");
	           // should throw Exception - name cannot be null
//	            tweeter.block("","raj");

	           tweeter.like("divyaraj",msg3);
	           tweeter.like("dummy1",msg3);
	           tweeter.like("dummy",msg3);
	           tweeter.like("raj",reply_msg2);
	           // should throw exception
//	            tweeter.like("divyaraj",reply_msg2);

	           //should throw exception
//	            tweeter.like("raj", msg3);
//	            tweeter.like("dummy3",msg3);

	           	
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Most productive user: " + stats.getMostProductiveReplier());
		System.out.println("Most popular user: " + stats.getMostFollowedUser());
		System.out.println("Length of the longest tweet: " + stats.getLengthOfLongestTweet());
		System.out.println("Most popular message: " + stats.getMostPopularMessage());
		System.out.println("Most liked message: " + stats.getMostLikedMessage());
		
		// NEED TO CHECK FOR THIS System.out.println("Most most message: " + stats.getMostPopularMessage());
		
		System.out.println("Most unpopular follower: " + stats.getMostUnpopularFollower());
		System.out.println("Longest message thread: " + stats.getLongestMessageThread());
		ctx.close();
	}
}
