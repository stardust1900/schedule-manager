package com.demonisles.schedulemanager.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger log = LoggerFactory.getLogger(TaskLogController.class);
	public static final Integer defaultPageSize = 5 ;
	
	@Autowired
	private TaskLogService taskLogService;
	
	@Autowired
	private TaskService taskService;
	
	private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/listLogs")
	public String listLogs(@RequestParam(name = "taskId", required = false ) Long taskId,
			@RequestParam(name = "pageIndex", required = true, defaultValue = "1") int pageIndex, 
			@RequestParam(name = "pageSize", required = false) Integer pageSize,
			@RequestParam(name = "taskDate", required = false) String taskDate, Model model, HttpSession session) {
		if (!StringUtils.hasLength(taskDate)) {
			taskDate = SDF.format(new Date());
		}
		if(pageSize == null) {
			if(session.getAttribute("pageSize") == null) {
				session.setAttribute("pageSize", defaultPageSize);
			}
			pageSize = (Integer) session.getAttribute("pageSize");
		}else {
			session.setAttribute("pageSize", pageSize);
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
		List<Integer> visibalePage = new ArrayList<>();
		if(totalPages<=7) {
			for(int i = 1;i<=totalPages;i++) {
				visibalePage.add(i);
			}
		}else {
			visibalePage.add(1);
			if(pageIndex -4 > 0) {
				visibalePage.add(-1);
				if(pageIndex + 4 <= totalPages) {
					visibalePage.add(pageIndex-1);
				}
			}else {
				visibalePage.add(2);
				visibalePage.add(3);
			}
			
			if(pageIndex + 4 <= totalPages) {
				visibalePage.add(visibalePage.get(2)+1);
				visibalePage.add(visibalePage.get(2)+2);
				visibalePage.add(-1);
				visibalePage.add(totalPages);
			}else {
				visibalePage.add(totalPages-4);
				visibalePage.add(totalPages-3);
				visibalePage.add(totalPages-2);
				visibalePage.add(totalPages-1);
				visibalePage.add(totalPages);
			}
		}
		log.debug("visibalePage:{}",visibalePage);
		
		
		
		
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("taskId", taskId);
		model.addAttribute("taskDate", taskDate);
		model.addAttribute("tasks", taskService.findAll());
		model.addAttribute("visibalePage", visibalePage);
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
