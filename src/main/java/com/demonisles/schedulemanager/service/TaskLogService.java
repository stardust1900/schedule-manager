package com.demonisles.schedulemanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demonisles.schedulemanager.domain.TaskLog;

public interface TaskLogService {
	Page<TaskLog> listTaskLog(Long taskId,String taskDate, Pageable page);
	
	void clearLogsBefore(String clearDate);
}
