package edu.sjsu.cmpe275.aop.tweet.aspect;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.aspectj.lang.annotation.Around;

@Aspect
@Order(1)
public class RetryAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     * @throws Throwable 
     */

	@Around("execution(public * edu.sjsu.cmpe275.aop.tweet.TweetService.*(..))")
	public Object retryConnect(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println("==> Stats Retry Before");
		System.out.printf("Prior to the executuion of the metohd %s\n", joinPoint.getSignature().getName());
		Object result = null;
		int totalTries = 0;
		try {
		while(true) {
			try {
				//throw new IOException();
				result = joinPoint.proceed();
//				if(Math.random()<0.01) {
//					throw new IOException();
//				}
				System.out.printf("Finished the executuion of the metohd %s with result %s\n", joinPoint.getSignature().getName(), result);
				System.out.println("==> Stats Retry After");
				return result;
			} catch(IOException e) {
					e.printStackTrace();
					totalTries++;
					if(totalTries<4) {
						System.out.println("Connection to network failed. Retrying for "+ totalTries + " out of 3 times.");
					}else {
						System.out.printf("Aborted the executuion of the metohd %s\n", joinPoint.getSignature().getName());
						throw e;
					}
				} 
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
