package com.demonisles.schedulemanager.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.demonisles.schedulemanager.domain.Task;
import com.demonisles.schedulemanager.repository.TaskRepository;
import com.demonisles.schedulemanager.service.ScheduleService;
import com.demonisles.schedulemanager.service.TaskExcService;

@Service
public class ScheduleServiceImpl implements ScheduleService, SchedulingConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);
	
	private ScheduledTaskRegistrar taskRegistrar;
	
	private Map<Long,ScheduledTask> taskMap = new ConcurrentHashMap<Long,ScheduledTask>();
	
	@Autowired
	private TaskRepository taskRepo;
	
	@Autowired
	private TaskExcService excService;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		log.info("taskRegistrar:{}",taskRegistrar);
//		TaskScheduler taskScheduler = taskRegistrar.getScheduler();
//		log.info("TaskScheduler:{}",taskScheduler);
		// 创建一个线程池调度器
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		// 设置线程池容量
		scheduler.setPoolSize(10);
		// 线程名前缀
		scheduler.setThreadNamePrefix("task-");
		// 等待时常
		scheduler.setAwaitTerminationSeconds(60);
		// 当调度器shutdown被调用时等待当前被调度的任务完成
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		// 设置当任务被取消的同时从当前调度器移除的策略
		scheduler.setRemoveOnCancelPolicy(true);
		// explicitly call scheduler.initialize() after setting all properties but before returning the scheduler.
		scheduler.initialize();
		//设置任务注册器的调度器
		taskRegistrar.setScheduler(scheduler);
		this.taskRegistrar = taskRegistrar;
		
		
//		CronTask cronTask = new CronTask(null,"");
//		taskRegistrar.addCronTask(cronTask);
		
//		cronTask.getTrigger().nextExecutionTime(triggerContext)
		//taskRegistrar.addCronTask(null, "");
		//taskScheduler.
	}
	@Override
	public void scheduleCronTask(Task task) {
		if(task != null && StringUtils.hasLength(task.getCron()))  {
			if(taskMap.containsKey(task.getTaskId())) {
				taskMap.get(task.getTaskId()).cancel();
				taskMap.remove(task.getTaskId());
			}
			if("1".equals(task.getTaskState())) {
				if("http".equals(task.getTaskType()) ) {
					CronTask  cronTask = new CronTask(new HttpWorker(task),task.getCron());
					ScheduledTask sTask = taskRegistrar.scheduleCronTask(cronTask);
					if(sTask != null) {
						taskMap.put(task.getTaskId(), sTask);
					}else {
						log.error("schedule task failed :{}", task);
					}
				}else if("shell".equals(task.getTaskType())) {
					log.error("schedule task failed :{}", task);
				}else if("sql".equals(task.getTaskType())) {
					log.error("schedule task failed :{}", task);
				}else {
					log.error("schedule task failed :{}", task);
				}
			}
		}else {
			log.error("schedule task failed :{}", task);
		}
	}
	
	class  HttpWorker implements Runnable {

		private Task task;
		
		public HttpWorker(Task task){
			this.task = task;
		}
		
		@Override
		public void run() {
			log.info("task:{}",task);
			Map<String,String> result = excService.httpExc(task);
			if("success".equals(result.get("code"))) {
				task.setLastExeStatus("1");
			}else {
				task.setLastExeStatus("0");
			}
			task.setLastExeTime(new Date());
			taskRepo.save(task);
		}
		
	}

	@Override
	public void removeTask(Long taskId) {
		if(taskMap.containsKey(taskId)) {
			taskMap.get(taskId).cancel();
			taskMap.remove(taskId);
		}
	}

}
