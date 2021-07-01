package com.demonisles.schedulemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demonisles.schedulemanager.domain.TaskLog;
import com.demonisles.schedulemanager.service.TaskLogService;
import com.demonisles.schedulemanager.service.TaskService;

@Controller
public class TaskLogController {

	public static final int pageSize = 5 ;
	
	@Autowired
	private TaskLogService taskLogService;
	
	@Autowired
	private TaskService taskService;
	
	@GetMapping("/listLogs")
	public String listTasks(@RequestParam(name = "taskId", required = false ) Long taskId,
			@RequestParam(name = "pageIndex", required = true, defaultValue = "1") int pageIndex, 
			@RequestParam(name = "taskDate", required = false) String taskDate, Model model) {
		Sort sort = Sort.by(Direction.DESC,"logId");
		Pageable page = PageRequest.of(pageIndex - 1, pageSize,sort);
		Page<TaskLog> logs = taskLogService.listTask(taskId,taskDate, page);
		
		model.addAttribute("logs", logs);
		int totalPages = logs.getTotalPages();
		if(pageIndex > totalPages) {
			model.addAttribute("pageIndex", totalPages==0 ? 1 : totalPages);
		}else {
			model.addAttribute("pageIndex", pageIndex);
		}
		
		model.addAttribute("taskId", taskId);
		model.addAttribute("taskDate", taskDate);
		model.addAttribute("tasks", taskService.findAll());
		return "logs";
	}
}
