package com.demonisles.schedulemanager.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/listLogs")
	public String listLogs(@RequestParam(name = "taskId", required = false ) Long taskId,
			@RequestParam(name = "pageIndex", required = true, defaultValue = "1") int pageIndex, 
			@RequestParam(name = "taskDate", required = false) String taskDate, Model model) {
		if (!StringUtils.hasLength(taskDate)) {
			taskDate = SDF.format(new Date());
		}
		Sort sort = Sort.by(Direction.DESC,"logId");
		Pageable page = PageRequest.of(pageIndex - 1, pageSize,sort);
		Page<TaskLog> logs = taskLogService.listTaskLog(taskId,taskDate, page);
		
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
	
	@PostMapping("/clearLogs")
	public String clearLogs(String clearDate) {
		if(StringUtils.hasLength(clearDate)) {
			taskLogService.clearLogsBefore(clearDate);
		}
		return "redirect:/listLogs";
	}
}
