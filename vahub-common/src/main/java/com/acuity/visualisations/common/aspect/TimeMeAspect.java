package com.acuity.visualisations.common.aspect;

import static com.google.common.collect.Lists.newArrayList;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * This can time spring beans methods.  
 * 
 * @author ksnd199
 */
@Aspect
@Component
@Order(20) // after caching at 10.  Dont want to log cached times 
public class TimeMeAspect extends TimeMeLog {

    @Around("within(@com.acuity.visualisations.common.aspect.TimeMe *)")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        String classAndMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
        logExecutionTime(classAndMethod, stopWatch, newArrayList(joinPoint.getArgs())); // log the method call

        return result;
    }
}
