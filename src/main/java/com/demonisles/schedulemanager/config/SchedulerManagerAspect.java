package com.demonisles.schedulemanager.config;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demonisles.schedulemanager.domain.Task;
import com.demonisles.schedulemanager.domain.TaskLog;
import com.demonisles.schedulemanager.repository.TaskLogRepository;

/**
 * 任务管理切面
 *
 * @author shawn
 *
 * @site http://wangxuan.me
 *
 */
@Aspect
@Component
public class SchedulerManagerAspect {
	private static final Logger log = LoggerFactory.getLogger(SchedulerManagerAspect.class);

	@Autowired
	private TaskLogRepository taskLogRepo;

	private ExecutorService pool = Executors.newFixedThreadPool(3);

	@Around("execution(* com.demonisles.schedulemanager.controller.*.*(..))")
	public Object logAround(ProceedingJoinPoint joinPoint) {
		String classMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
		log.info("logAround开始:[{}] 参数为:{}", classMethod, joinPoint.getArgs()); // 方法执行前的代理处理
		try {
			long startTime = System.currentTimeMillis();
			Object obj = joinPoint.proceed(joinPoint.getArgs());
			long usedTime = System.currentTimeMillis() - startTime;
			log.info("logAround结束[{}]: 处理用时:{}ms -> 返回为:{}", classMethod, usedTime, obj); // 方法执行后的代理处理
			return obj;
		} catch (Throwable e) {
			log.error("system error ", e);
			return e.getMessage();
		}
	}

	@Around("execution(* com.demonisles.schedulemanager.service.impl.TaskExcServiceImpl.httpExc(..)) || execution(* com.demonisles.schedulemanager.service.impl.TaskExcServiceImpl.shellExc(..))")
	public Object httpExcAround(ProceedingJoinPoint joinPoint) {
		String classMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		log.info("logAround开始:[{}] 参数为:{}", classMethod, args); // 方法执行前的代理处理
		try {
			Date startTime = new Date();
			Object obj = joinPoint.proceed(joinPoint.getArgs());
			Date endTime = new Date();
			long usedTime = endTime.getTime() - startTime.getTime();
			Task task = (Task) args[0];
			if (task != null && task.getTaskId() != 0) {
				@SuppressWarnings("unchecked")
				Map<String, String> result = (Map<String, String>) obj;
				pool.execute(new TaskLogWorker(task, result, startTime, endTime));
			}
			log.info("logAround结束[{}]: 处理用时:{}ms -> 返回为:{}", classMethod, usedTime, obj); // 方法执行后的代理处理
			return obj;
		} catch (Throwable e) {
			log.error("system error ", e);
			return e.getMessage();
		}
	}

	class TaskLogWorker implements Runnable {

		private Task task;

		private Map<String, String> result;

		private Date startTime;

		private Date endTime;

		public TaskLogWorker(Task task, Map<String, String> result, Date startTime, Date endTime) {
			this.task = task;
			this.result = result;
			this.startTime = startTime;
			this.endTime = endTime;
		}

		@Override
		public void run() {
			TaskLog log = new TaskLog();
			log.setRequestParams(task.getParams());
			log.setResponseParams(result.get("msg"));
			log.setTaskBegin(startTime);
			log.setTaskEnd(endTime);
			log.setTask(task);
			if ("success".equals(result.get("code"))) {
				log.setStatus("1");
			} else {
				log.setStatus("0");
			}

			taskLogRepo.save(log);

		}

	}
}
