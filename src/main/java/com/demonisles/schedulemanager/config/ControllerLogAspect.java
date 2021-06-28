package com.demonisles.schedulemanager.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 控制器日志切面
 *
 * @author shawn
 *
 * @site http://wangxuan.me
 *
 */
@Aspect
@Component
public class ControllerLogAspect {
	private static final Logger log = LoggerFactory.getLogger(ControllerLogAspect.class);
	
	@Around("execution(* com.demonisles.schedulemanager.controller.*.*(..))")
	public Object logAround(ProceedingJoinPoint joinPoint) {
		String classMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
		log.info("logAround开始:[{}] 参数为:{}",
				classMethod,
				joinPoint.getArgs()); // 方法执行前的代理处理
		try {
			long startTime = System.currentTimeMillis();
			Object obj = joinPoint.proceed(joinPoint.getArgs());
			long usedTime = System.currentTimeMillis() - startTime;
			log.info("logAround结束[{}]: 处理用时:{}ms -> 返回为:{}", classMethod, usedTime, obj); // 方法执行后的代理处理
			return obj;
		} catch (Throwable e) {
			log.error("system error ",e);
			return e.getMessage();
		}
	}
}
