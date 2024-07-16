package com.sinaev.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging the execution time of methods annotated with @Loggable.
 * <p>
 * This aspect intercepts the execution of methods annotated with @Loggable
 * and logs the method name and execution time.
 * </p>
 */
@Aspect
@Component
public class LoggableAspect {

    /**
     * Logs the execution time of methods annotated with @Loggable.
     * <p>
     * This advice runs around the execution of methods annotated with @Loggable,
     * logging the method signature and the time taken to execute the method.
     * </p>
     *
     * @param proceedingJoinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if the method execution throws an exception
     */
    @Around("Pointcuts.annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Calling method " + proceedingJoinPoint.getSignature());
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        long exTime = end - start;
        System.out.println("Executed of method " + proceedingJoinPoint.getSignature() + " finished. Executed time is: " + exTime + " ms");
        return result;
    }
}
