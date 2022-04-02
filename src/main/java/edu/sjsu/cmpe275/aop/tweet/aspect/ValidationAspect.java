package edu.sjsu.cmpe275.aop.tweet.aspect;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.tweet.TweetStatsServiceImpl;

@Aspect
@Order(3)
public class ValidationAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */

	
	//
	//TweetStatsServiceImpl 
	
	public HashMap<String, HashSet<String>> blockList = new HashMap<>();
	public HashMap<String, HashSet<String>> followList = new HashMap<>();
	public HashMap<UUID, HashSet<String>> likeList = new HashMap<>();
	public HashMap<String, HashMap<UUID,String>> tweetList= new HashMap<>();
	public HashMap<UUID, HashSet<String>> tweetShared = new HashMap<>();
	public HashMap<UUID,List<UUID>> replyMap = new HashMap<>();
	public HashMap<String, HashMap<UUID,String>> replyList= new HashMap<>();
	
	@Before("execution(public int edu.sjsu.cmpe275.aop.tweet.TweetService.retweet(..))")
	public void dummyBeforeAdvice(JoinPoint joinPoint) {
		System.out.println("==> Validation Aspect ");
		System.out.printf("Permission check before the executuion of the metohd %s\n", joinPoint.getSignature().getName());
	}
	
	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.tweet(..))")
	public UUID aroundTweet(ProceedingJoinPoint pjp) throws Throwable {
		String user =(String) pjp.getArgs()[0];
		String message = (String)pjp.getArgs()[1];
		
		UUID uuid = (UUID) pjp.proceed();
		List<UUID> lst = new ArrayList<>();
		lst.add(uuid);
		replyMap.put(uuid, lst);
		
		HashMap<UUID,String> map;
		if(tweetList.isEmpty()) {
			map = new HashMap<UUID,String>();
		} else {
			map =tweetList.get(user);
			if(map==null) {
				map=new HashMap<UUID,String>();
			}
		}
		map.put(uuid, message);
		tweetList.put(user, map);
		
		HashSet<String> sharedToUsers = new HashSet<>();
		HashSet<String> userFollowers = followList.get(user);
		
		
		
		if(userFollowers!=null) {
			Iterator<String> it = userFollowers.iterator();
        	System.out.println(userFollowers);
        	while (it.hasNext()) {
        		String val = it.next();
        		if(!isBlocked(val,user)) {
        			sharedToUsers.add(val);
        		}
        	}
		}
        tweetShared.put(uuid, sharedToUsers);
		
//		System.out.println("============TWEET LIST AFTER RETURNING TWEET METHOD=========");
//		System.out.println(tweetList);
//		System.out.println("      ");
//		System.out.println(tweetShared);
//		System.out.println("      ");
//		System.out.println(followList);
//		System.out.println("==============================================================");

		return uuid;
	}
	

	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.reply(..))")
	public UUID aroundReply(ProceedingJoinPoint joinPoint)  throws Throwable{
		String user =(String) joinPoint.getArgs()[0];
		UUID uuid = (UUID) joinPoint.getArgs()[1];
		String message = (String) joinPoint.getArgs()[2];
		String tweetBy = getUserFromUUID(uuid);
		
		
		UUID uuidReturned = (UUID)joinPoint.proceed();
		
		
		if(uuidReturned==null) {
			uuidReturned= UUID.randomUUID();
		}
		
		List<UUID> lst = replyMap.get(uuid);
		List<UUID> copy = new ArrayList<>(lst);
		copy.add(uuidReturned);
		
		replyMap.put(uuidReturned, copy);
		

		HashMap<UUID,String> map;

		if(tweetList.isEmpty()) {
			map = new HashMap<UUID,String>();
			map.put(uuidReturned, message);
		} else {
			map =tweetList.get(user);
			if(map==null) {
				map = new HashMap<UUID,String>();
			}
			map.put(uuidReturned, message);
		}
		tweetList.put(user, map);
		replyList.put(user, map);
		
		//check if tweetby is in follow list and not in block list
		HashSet<String> sharedToUsers = new HashSet<>();	

		if(isBlocked(tweetBy,user)) {
			throw new AccessControlException("User has blocked the person he is trying to reply to.");
		}
		
		if(!isBlocked(tweetBy,user)) {
			System.out.println("--------------------------------------");
			System.out.println(blockList);
			System.out.println(tweetBy);
			System.out.println(user);
			sharedToUsers.add(tweetBy);
		}
//		if(isBlocked(user,tweetBy))
//		{
//			throw new AccessControlException("User Blocked");
//		}


		HashSet<String> userFollowers = followList.get(user);
		if(userFollowers!=null) {
			Iterator<String> it = userFollowers.iterator();
        	//System.out.println(userFollowers);
        	while (it.hasNext()) {
        		String val = it.next();
        		if(!isBlocked(val,user)) {
        			sharedToUsers.add(val);
        		}
        	}
		}
//		userFollowers = followList.get(tweetBy);
//		if(userFollowers!=null) {
//			Iterator<String> it = userFollowers.iterator();
//        	//System.out.println(userFollowers);
//        	while (it.hasNext()) {
//        		String val = it.next();
//        		if(!isBlocked(val,user)) {
//        			sharedToUsers.add(val);
//        		}
//        	}
//		}
		
        tweetShared.put(uuidReturned, sharedToUsers);
		
		
//		System.out.println("==========TWEET LIST AFTER RETURNING REPLY=============");
////		System.out.println(message);
//		System.out.println(tweetList);
//		System.out.println(tweetShared);
//
//		System.out.println("========================================================");
		
		return uuidReturned;

	}

	@After("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.block(..))")
	public void blockUser(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		String toBlock = (String) joinPoint.getArgs()[1];
	
		HashSet<String> userBlockList;
		if(blockList.isEmpty()){
			userBlockList =  new HashSet<>();
		}else {
			userBlockList = blockList.get(user);
			if(userBlockList ==null) {
				userBlockList = new HashSet<String>();
			}
		}
		
		userBlockList.add(toBlock);
		blockList.put(user, userBlockList);
//		System.out.println("==========BLOCK LIST BEFORE EXECUTING BLOCK=============");
//		System.out.println(blockList);
//		System.out.println("=========================================================");
	}
	
	//Use @After or @AfterReturning
	
	@After("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.follow(..))")
	public void followUser(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		String toFollow = (String) joinPoint.getArgs()[1];

		HashSet<String> userFollowList;
		
//		HashSet<String>userBlockList =  blockList.get(toFollow);
		
//		if(userBlockList!=null) {
//			boolean contains = userBlockList.contains(user);
//			if(!contains) {
//				if(followList.isEmpty()){
//					userFollowList =  new HashSet<>();
//				}else {
//					userFollowList = followList.get(toFollow);
//					if(userFollowList ==null) {
//						userFollowList = new HashSet<String>();
//					}
//				}
//				userFollowList.add(user);
//				followList.put(toFollow, userFollowList);
//			}
//		}else {
			if(followList.isEmpty()){
				userFollowList =  new HashSet<>();
			}else {
				userFollowList = followList.get(toFollow);
				if(userFollowList==null) {
					userFollowList =  new HashSet<>();
				}
			}
			userFollowList.add(user);
			followList.put(toFollow, userFollowList);
//		}

//		System.out.println("==========FOLLOW LIST AFTER EXECUTING FOLLOW=============");
//		System.out.println(followList);
//		System.out.println("=========================================================");
	}
	
	
	@After("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.like(..))")
	public void likeMessage(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		UUID messageID = (UUID) joinPoint.getArgs()[1];

		
		HashSet<String> userLikeList;
		if(likeList.isEmpty()){
			userLikeList =  new HashSet<>();
		}else {
			userLikeList = likeList.get(messageID);
			if(userLikeList ==null) {
				userLikeList = new HashSet<String>();
			}
		}
		
		userLikeList.add(user);
		likeList.put(messageID, userLikeList);
//		System.out.println("==========LIKE LIST AFTER EXECUTING LIKE=============");
//		System.out.println(likeList);
//		System.out.println("=========================================================");
	}
	
	private boolean isBlocked(String follower, String followee) {
		return blockList==null ? false : blockList.get(followee) ==null?false : blockList.get(followee).contains(follower);	
	}

	public boolean isFollower(String follower, String followee ) {
		return followList==null ? false : followList.get(followee) ==null?false : followList.get(followee).contains(follower);	
	}

	public  String getUserFromUUID(UUID uuid) {
		for(Entry<String, HashMap<UUID, String>> entry : tweetList.entrySet()) {
			for(Entry<UUID,String> userTweetList : entry.getValue().entrySet()) {
				if(userTweetList.getKey()==uuid) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public String getMessageFromUUID(UUID uuid) {
		for(Entry<String, HashMap<UUID, String>> entry : tweetList.entrySet()) {
			for(Entry<UUID,String> userTweetList : entry.getValue().entrySet()) {
				if(userTweetList.getKey()==uuid) {
					return userTweetList.getValue();
				}
			}
		}
		return null;
	}
	
	public boolean isTweetValid(UUID uuid) {
		for(Entry<String, HashMap<UUID, String>> entry : tweetList.entrySet()) {
			for(Entry<UUID,String> userTweetList : entry.getValue().entrySet()) {
				if(userTweetList.getKey()==uuid) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isTweetShared(String user, UUID uuid) {
		for(Entry<UUID, HashSet<String>> entry : tweetShared.entrySet()) {
			if(entry.getKey()==uuid) {
				return entry.getValue().contains(user);
			}
		}
		return false;
	}

	public boolean isAlreadyLiked(String user, UUID uuid) {
		for(Entry<UUID, HashSet<String>> entry : likeList.entrySet()) {
			if(entry.getKey()==uuid) {
				return entry.getValue().contains(user);
			}
		}
		return false;
	}
	
	
}


