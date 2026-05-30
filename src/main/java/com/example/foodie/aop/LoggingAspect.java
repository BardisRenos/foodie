package com.example.foodie.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Controllers
    @Around("execution(* com.example.foodie..controller.*.*(..))")
    public Object logController(ProceedingJoinPoint jp) throws Throwable {
        return log(jp, "CONTROLLER");
    }

    // Services
    @Around("execution(* com.example.foodie..service.*.*(..))")
    public Object logService(ProceedingJoinPoint jp) throws Throwable {
        return log(jp, "SERVICE");
    }

    // Event Listeners
    @Around("execution(* com.example.foodie..service.*Listener.*(..))")
    public Object logListener(ProceedingJoinPoint jp) throws Throwable {
        return log(jp, "LISTENER");
    }

    private Object log(ProceedingJoinPoint jp, String layer) throws Throwable {
        String method = jp.getSignature().toShortString();
        log.info("[{}] --> {}", layer, method);
        long start = System.currentTimeMillis();
        try {
            Object result = jp.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[{}] <-- {} completed in {}ms", layer, method, elapsed);
            return result;
        } catch (Exception e) {
            log.error("[{}] <-- {} threw: {}", layer, method, e.getMessage());
            throw e;
        }
    }
}