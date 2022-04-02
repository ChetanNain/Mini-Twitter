package edu.sjsu.cmpe275.aop.tweet.aspect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;


@Aspect
@Order(2)
public class StatsAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */


	@Autowired ValidationAspect vasp;

	@After("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.*(..))")
	public void dummyAfterAdvice(JoinPoint joinPoint) {
//		System.out.println("==> Stats Aspect After");
		System.out.printf("After the executuion of the metohd %s\n", joinPoint.getSignature().getName());
		System.out.printf("After the executuion of the metohd %s\n", joinPoint.getSignature().getName());
		//stats.resetStats();
	}
	
//	@Before("execution(public String edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getMostFollowedUser())")
//	public void dummyBeforeAdvice(JoinPoint joinPoint) {
//		System.out.println("==> Stats Aspect Before");
//		System.out.printf("Before the executuion of the metohd %s\n", joinPoint.getSignature().getName());
//	}
	
	@Before("execution(public void edu.sjsu.cmpe275.aop.tweet.TweetService.resetStatsAndSystem(..))")
	public void resetValues() {
		vasp.blockList.clear();
		vasp.followList.clear();
		vasp.likeList.clear();
		vasp.replyMap.clear();
		vasp.tweetList.clear();
		vasp.tweetShared.clear();

	}
//	edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getMostFollowedUser()
	
	
	
	//Change it to after returning
	@Around("execution(public String edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getMostFollowedUser(..))")
	public String mostFollowedUser(ProceedingJoinPoint pjp) throws Throwable {
		double followers =0;
		String user=(String) pjp.proceed();
		//String user="";
		for(Entry<String, HashSet<String>> entry : vasp.followList.entrySet()) {
				if(entry.getValue().size()>followers) {
					followers = entry.getValue().size();
					user = entry.getKey();
				}else if(entry.getValue().size()==followers) {
					int x = entry.getKey().compareTo(user);
					if(x<0) {
						user=entry.getKey();
					}
				}
		}
		//System.out.println("Most Followed User :" + user + " with " + followers + " followers.");
		return user;
	}
	
	@Around("execution(public int edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getLengthOfLongestTweet(..))")
	public int longestTweet(ProceedingJoinPoint pjp ) throws Throwable {
		int tweetLength = (int) pjp.proceed();
		for(Entry<String, HashMap<UUID, String>> entry : vasp.tweetList.entrySet()) {
			for(Entry<UUID,String> userTweetList : entry.getValue().entrySet()) {
				if(userTweetList.getValue().length()>tweetLength){
					tweetLength=userTweetList.getValue().length();
				}
			}
		}
		return tweetLength;
	}
	
	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getMostLikedMessage(..))")
	public UUID mostLikedMessage(ProceedingJoinPoint pjp ) throws Throwable {
		UUID likedMessage = (UUID) pjp.proceed();
		int mostLikes=0;
		//System.out.print(vasp.likeList);
		for(Entry<UUID, HashSet<String>> entry : vasp.likeList.entrySet()) {
//			System.out.println(entry.getValue());
//			System.out.println(entry.getKey());
				if(entry.getValue().size()>mostLikes){
					mostLikes = entry.getValue().size();
					likedMessage=entry.getKey();
			}else if( mostLikes == entry.getValue().size()) {
				int x = entry.getKey().compareTo(likedMessage);
				if(x<0) {
					likedMessage=entry.getKey();
				}
			}
		}
		return likedMessage;
	}
	
	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getMostUnpopularFollower(..))")
	public String mostUnpopularFollower(ProceedingJoinPoint pjp ) throws Throwable {
		String mostBlockedUser = (String) pjp.proceed();
		int mostBlocks=0;
		HashMap<String,Integer> map = new HashMap<>();
		//System.out.print(vasp.likeList);
		for(Entry<String, HashSet<String>> entry : vasp.blockList.entrySet()) {
//			System.out.println(entry.getValue());
//			System.out.println(entry.getKey());
			HashSet<String> blockedUsers = entry.getValue();
			Iterator<String> it = blockedUsers.iterator();

			while(it.hasNext()) {
				String user = it.next();
				int val = map.getOrDefault(user,0);
				val +=1;
				if(val>mostBlocks) {
					mostBlockedUser = user;
				}else if (val==mostBlocks && mostBlocks>0)  {
					int x = user.compareTo(mostBlockedUser);
					if(x<0) {
						mostBlockedUser=user;
					}
				}
				map.put(user, val);
			}
		}
		return mostBlockedUser;
	}
	
	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getMostPopularMessage(..))")
	public UUID mostPopularMessage(ProceedingJoinPoint pjp) throws Throwable {
		UUID uuid = (UUID) pjp.proceed();
		int sharedWith=0;
		for(Entry<UUID, HashSet<String>> entry : vasp.tweetShared.entrySet()) {
			if(sharedWith<entry.getValue().size()) {
				sharedWith=entry.getValue().size();
				uuid = entry.getKey();
			}else if( sharedWith == entry.getValue().size() && sharedWith>0) {
				int x = entry.getKey().compareTo(uuid );
				if(x<0) {
					uuid=entry.getKey();
				}
			}
		}
		return uuid;
	}
	
	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getMostProductiveReplier(..))")
	public String mostProductiveReplier(ProceedingJoinPoint pjp) throws Throwable {
		String userName = (String) pjp.proceed();
		int maxWordCount=0;
		for(Entry<String, HashMap<UUID, String>> entry : vasp.replyList.entrySet()) {
			int wordCount=0;
			for(Entry<UUID,String> userTweetList : entry.getValue().entrySet()) {
				wordCount += userTweetList.getValue().length();
			}
			if(wordCount>maxWordCount) {
				maxWordCount=wordCount;
				userName = entry.getKey();
			}else if(wordCount==maxWordCount && maxWordCount>0) {
				int x = entry.getKey().compareTo(userName );
				if(x<0) {
					userName=entry.getKey();
				}
			}
		}
		return userName;
	}
	
	
	//Latest ID
	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetStatsService.getLongestMessageThread(..))")
	public UUID longestMessageThread(ProceedingJoinPoint pjp) throws Throwable {
		UUID uuid = (UUID)pjp.proceed();
		int maxSize=0;
		for(Entry<UUID, List<UUID>> entry : vasp.replyMap.entrySet()) {
			int listSize = entry.getValue().size();
			if(listSize>maxSize) {
				maxSize = listSize;
				uuid=entry.getKey();
			}else if(listSize==maxSize && maxSize>0) {
				int x = entry.getKey().compareTo(uuid);
				if(x<0) {
					uuid = entry.getKey();
				}
			}
		}
		//System.out.println(vasp.tweetList);
		return uuid;
	}
	
}
