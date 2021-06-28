package com.demonisles.schedulemanager.service.impl;

import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.demonisles.schedulemanager.domain.Task;
import com.demonisles.schedulemanager.repository.TaskRepository;
import com.demonisles.schedulemanager.service.ScheduleService;
import com.demonisles.schedulemanager.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Override
	public Page<Task> listTask(String taskName, String taskType, Pageable page) {
		
		Page<Task> tasks = taskRepository.findAll(new Specification<Task>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				ArrayList<Predicate> list = new ArrayList<>();
				if(StringUtils.hasText(taskName)) {
					Predicate name = criteriaBuilder.like(root.get("taskName").as(String.class), "%" + taskName + "%");
					list.add(name);
				}
				
				if(StringUtils.hasText(taskType)) {
					Predicate type = criteriaBuilder.like(root.get("taskType").as(String.class), taskType);
	                list.add(type);
				}
                Predicate[] array = new Predicate[list.size()];
                Predicate[] predicates = list.toArray(array);
                
                return criteriaBuilder.and(predicates);
			}}, page);
//		Iterable<Task> tasks = taskRepository.findAllByTaskNameContainingAndTaskType(taskName, taskType, page);
//		List<Task> result = new ArrayList<Task>();
//		
//		for(Task t: tasks) {
//			result.add(t);
//		}
		return tasks;
	}

	@Override
	public void saveTask(Task task) {
		taskRepository.save(task);
		scheduleService.scheduleCronTask(task);
	}

	@Override
	public void removeTask(Long taskId) {
		scheduleService.removeTask(taskId);
		taskRepository.deleteById(taskId);
	}

	@Override
	public Task getTaskById(Long taskId) {
		return taskRepository.findById(taskId).get();
	}

}
