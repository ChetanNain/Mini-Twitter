package edu.sjsu.cmpe275.aop.tweet.aspect;

import java.security.AccessControlException;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;



@SuppressWarnings("removal")
@Aspect
@Order(0)
public class AccessControlAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     * @throws Throwable 
     */
	
	@Autowired ValidationAspect vasp;
	
	@Around("execution(public int edu.sjsu.cmpe275.aop.tweet.TweetService.*(..))")
	public int dummyAdviceOne(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.printf("Prior to the executuion of the metohd %s\n", joinPoint.getSignature().getName());
		Integer result = null;
		try {
			result = (Integer) joinPoint.proceed();
			System.out.printf("Finished the executuion of the metohd %s with result %s\n", joinPoint.getSignature().getName(), result);
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.printf("Aborted the executuion of the metohd %s\n", joinPoint.getSignature().getName());
			throw e;
		}
		return result.intValue();
	}
	
	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.tweet(..))")
	public void beforeTweet(JoinPoint pjp) throws Throwable {
		String user =(String) pjp.getArgs()[0];
		String message = (String)pjp.getArgs()[1];
		
		if(message.length()>140 || user==null || message==null || user=="" || message=="") {
			throw new IllegalArgumentException("The message is more than 140 characters as measured by string length, or any parameter is null or empty.");
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.reply(..))")
	public void aroundReply(JoinPoint joinPoint)  throws Throwable{
		String user =(String) joinPoint.getArgs()[0];
		UUID uuid = (UUID) joinPoint.getArgs()[1];
		String message = (String) joinPoint.getArgs()[2];
		boolean validTweet = vasp.isTweetValid(uuid);
		
		
		if(validTweet==false) {
			throw new IllegalArgumentException("Any parameter is null or empty,  the UUID is invalid, or  a user attempts to directly reply to a message by themselves.");
		}
		
		String tweetBy = vasp.getUserFromUUID(uuid);
		

		if(user==null || uuid==null || message==null || user=="" || message==""  || tweetBy==user ) {
			throw new IllegalArgumentException("Any parameter is null or empty,  the UUID is invalid, or  a user attempts to directly reply to a message by themselves.");
		}
		
		if(message.length()>140) {
			throw new IllegalArgumentException("Message length exceeds 140 characters.");
		}
		
		if(!vasp.tweetShared.get(uuid).contains(user)) {
			throw new AccessControlException("the current user has not been shared with the original message or the current user has blocked the original sender.");
		}
	}
	
	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.block(..))")
	public void beforeBlockUser(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		String toBlock = (String) joinPoint.getArgs()[1];
		
		if(user==null || toBlock==null || user=="" || toBlock=="" || user.equalsIgnoreCase(toBlock)) {
			throw new IllegalArgumentException("Either parameter is null or empty, or  when a user attempts to block himself.");
		}
	}
	
	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.follow(..))")
	public void followUser(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		String toFollow = (String) joinPoint.getArgs()[1];
		
		if(user==null || toFollow==null || user=="" || toFollow=="" || user.equalsIgnoreCase(toFollow)) {
			throw new IllegalArgumentException("either parameter is null or empty, or  when a user attempts to follow himself.");
		}
	}
	
	@SuppressWarnings("deprecation")
	@Before("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.like(..))")
	public void likeMessage(JoinPoint joinPoint) {
		String user = (String) joinPoint.getArgs()[0];
		UUID messageID = (UUID) joinPoint.getArgs()[1];
		
		String checkUser = vasp.getUserFromUUID(messageID);
		
		boolean isShared =vasp.isTweetShared(user, messageID);
		
		boolean validTweet = vasp.isTweetValid(messageID);
		
		boolean isLiked = vasp.isAlreadyLiked(user,messageID);
		if(checkUser==user || isShared==false || validTweet==false || isLiked) {
			throw new AccessControlException("the given user is not following the sender of the given message, or the sender has blocked the given user, the given message does not exist, someone tries to like his own messages  or when the message with the given ID is already  successfully liked by the same user.");
		}
		
	}
}
