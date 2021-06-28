package com.demonisles.schedulemanager.service;

import com.demonisles.schedulemanager.domain.Task;

public interface ScheduleService {

	void scheduleCronTask(Task task);
	
	void removeTask(Long taskId);
}
