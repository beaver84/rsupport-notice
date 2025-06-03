package com.example.rsupportnotice.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingAspect.class);

    // com.example.rsupportnotice.service 패키지의 모든 public 메서드에 적용
    // 필요한 서비스에만 적용 가능 - 예) @Around("execution(* com.example.rsupportnotice.service..createNotice(..))")
    @Around("execution(public * com.example.rsupportnotice.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        logger.info("[PERF] {} executed in {} ms", joinPoint.getSignature(), executionTime);

        return result;
    }
}
