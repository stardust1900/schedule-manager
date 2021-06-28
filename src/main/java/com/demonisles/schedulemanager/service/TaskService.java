package com.demonisles.schedulemanager.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demonisles.schedulemanager.domain.Task;

public interface TaskService {

	Page<Task> listTask(String taskName, String taskType, Pageable page);
	
	void addTask(Task task);
	
	void modifyTask(Task task);
	
	void removeTask(Long taskId);
	
	Task getTaskById(Long taskId);
}
