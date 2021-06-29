package com.demonisles.schedulemanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.demonisles.schedulemanager.domain.TaskLog;
import com.demonisles.schedulemanager.repository.TaskLogRepository;
import com.demonisles.schedulemanager.service.TaskLogService;

@Service
public class TaskLogServiceImpl implements TaskLogService {

	@Autowired
	private TaskLogRepository taskLogRepo;
	
	@Override
	public Page<TaskLog> listTask(Long taskId, Pageable page) {
		if(null == taskId) {
			return taskLogRepo.findAll(page);
		}
		return taskLogRepo.findAllByTaskTaskId(taskId,page);
	}

}
