package com.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j // 等同於 private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
public class LoggingAspect {

    // 定義切入點：攔截 com.example.controller 包底下的所有類別的所有方法
    @Pointcut("execution(* com.example.controller..*.*(..))")
    public void controllerPointcut() {
    }

    // 環繞通知：在目標方法執行前後執行
    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        // 1. 前置記錄 (進入方法)
        log.info(">> Invoke [{} -> {}], Parameters: {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            // 執行原本的目標方法
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            // 異常記錄（記錄後往外拋，交給全域例外處理器）
            log.error("!! [{} -> {}] Error: {}", className, methodName, throwable.getMessage());
            throw throwable;
        }

        long executionTime = System.currentTimeMillis() - startTime;

        // 2. 後置記錄 (方法結束)
        log.info("<< Exit [{} -> {}], Return: {}, Duration: {}ms", className, methodName, result, executionTime);

        return result;
    }
}
