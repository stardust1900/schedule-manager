package com.demonisles.schedulemanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import com.demonisles.schedulemanager.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService, SchedulingConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		log.info("taskRegistrar:{}",taskRegistrar);
		TaskScheduler taskScheduler = taskRegistrar.getScheduler();
		log.info("TaskScheduler:{}",taskScheduler);
//		CronTask cronTask = new CronTask(null,"");
//		taskRegistrar.addCronTask(cronTask);
		
//		cronTask.getTrigger().nextExecutionTime(triggerContext)
		//taskRegistrar.addCronTask(null, "");
		//taskScheduler.
	}

}
