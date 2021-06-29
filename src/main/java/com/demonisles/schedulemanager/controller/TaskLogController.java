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

@Controller
public class TaskLogController {

	public static final int pageSize = 5 ;
	
	@Autowired
	private TaskLogService taskLogService;
	
	@GetMapping("/listLogs")
	public String listTasks(@RequestParam(name = "taskId", required = false ) Long taskId,
			@RequestParam(name = "pageIndex", required = true, defaultValue = "1") int pageIndex, Model model) {
		Sort sort = Sort.by(Direction.DESC,"logId");
		Pageable page = PageRequest.of(pageIndex - 1, pageSize,sort);
		Page<TaskLog> logs = taskLogService.listTask(taskId, page);
		model.addAttribute("logs", logs);
		return "logs";
	}
}
