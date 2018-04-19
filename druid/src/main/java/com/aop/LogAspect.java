package com.aop;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 切面日志,主要保存log注解标注的接口日志
 */
@Aspect
@Component
public class LogAspect {

    //获取log主键标示,用于更新日志结果
    private String logId;

    @Pointcut("@annotation(com.annotation.Log)")
    public void logPointCut() {

    }

    /* 请求接口之前 */
    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取请求方法名和类名
        logId = UUID.randomUUID().toString().replace("-", "");
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        System.out.println("=========请求前======" + className + "======" + methodName);
        System.out.println("=========当前uuid======" + logId);
    }

    /* 接口执行成功后通知 */
    @AfterReturning(value = "logPointCut()", returning = "rvt")
    public void doAfterReturning(JoinPoint joinPoint, Object rvt) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取请求方法名和类名
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getName();
        System.out.println("=========执行成功后======" + className + "======" + methodName);
        System.out.println("=========当前uuid======" + logId);
        System.out.println("=========当前uuid======" + JSON.toJSONString(rvt));

    }

    /* 接口抛出异常后 */
    @AfterThrowing(pointcut = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取请求方法名和类名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        System.out.println("=========执行失败后======" + className + "======" + methodName);
        System.out.println("=========当前uuid======" + logId);
        System.out.println("=========当前uuid======" + e.getMessage());
    }
}
