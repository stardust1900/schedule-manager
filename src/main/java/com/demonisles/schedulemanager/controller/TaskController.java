package com.demonisles.schedulemanager.controller;


import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demonisles.schedulemanager.domain.Task;
import com.demonisles.schedulemanager.service.TaskService;
import com.demonisles.schedulemanager.service.TaskExcService;

@Controller
public class TaskController {
	
	public static final Integer defaultPageSize = 5 ;
	
	private static final Logger log = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private TaskExcService taskExcService;

	@GetMapping("/")
	public String listTasks(@RequestParam(name = "taskName", required = false, defaultValue = "") String taskName,
			@RequestParam(name = "taskType", required = false, defaultValue = "") String taskType,
			@RequestParam(name = "pageIndex", required = true, defaultValue = "1") int pageIndex, 
			@RequestParam(name = "pageSize", required = false) Integer pageSize,
			Model model,
			HttpSession session) {

//        Sort sort = Sort.by("taskId");
		if(pageSize == null) {
			if(session.getAttribute("pageSize") == null) {
				session.setAttribute("pageSize", defaultPageSize);
			}
			pageSize = (Integer) session.getAttribute("pageSize");
		}else {
			session.setAttribute("pageSize", pageSize);
		}
		Pageable page = PageRequest.of(pageIndex - 1, pageSize);
		
		Page<Task> tasks = taskService.listTask(taskName,taskType,page);
		model.addAttribute("tasks", tasks);
		model.addAttribute("taskName", taskName);
		model.addAttribute("taskType", taskType);
		model.addAttribute("pageIndex", pageIndex);
		model.addAttribute("pageSize", pageSize);
		return "home";
	}

	@PostMapping("/saveTask")
	public String saveTask(Task task) {
		log.info("saveTask :{}",task);
		UserDetails user = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(task.getTaskId() == 0) {
			task.setCreatedBy(user.getUsername());
			task.setCreatedTime(new Date());
			taskService.saveTask(task);
		}else {
			Task oldTask = taskService.getTaskById(task.getTaskId());
			
			oldTask.setTaskName(task.getTaskName());
			oldTask.setTaskDesc(task.getTaskDesc());
			oldTask.setTaskType(task.getTaskType());
			oldTask.setCron(task.getCron());
			oldTask.setParams(task.getParams());
			
			oldTask.setUpdatedBy(user.getUsername());
			oldTask.setUpdatedTime(new Date());
			
			taskService.saveTask(oldTask);
		}
		
		return "redirect:/";
	}
	
	@RequestMapping(value="/testTask",method= {RequestMethod.POST})
	@ResponseBody
	public String testTask(Task task) {
		if("http".equals(task.getTaskType())) {
			return taskExcService.httpExc(task).get("msg");
		}else if("shell".equals(task.getTaskType())) {
			return taskExcService.shellExc(task).get("msg");
		}else {
			return null;
		}
		
	}
	
	@PostMapping("/pauseTask")
	@ResponseBody
	public String pauseTask(Long taskId) {
		Task task = taskService.getTaskById(taskId);
		if(task != null) {
			task.setTaskState("0");
			taskService.saveTask(task);
			return "success";
		}else {
			return "fail";
		}
		
	}
	
	@PostMapping("/startTask")
	@ResponseBody
	public String startTask(Long taskId) {
		Task task = taskService.getTaskById(taskId);
		if(task != null) {
			task.setTaskState("1");
			taskService.saveTask(task);
			return "success";
		}else {
			return "fail";
		}
		
	}
	
	@GetMapping("/toAdd")
	public String toAdd(Model model) {
		model.addAttribute("task", new Task());
		return "add";
	}
	
	@GetMapping("/editTask")
	public String editTask(Long taskId, Model model) {
		Task task = taskService.getTaskById(taskId);
		model.addAttribute("task", task);
		return "add";
	}
	
	@PostMapping("/removeTask")
	@ResponseBody
	public String removeTask(Long taskId) {
		taskService.removeTask(taskId);
		return "success";
	}
	
	@PostMapping("/excTask")
	@ResponseBody
	public String excTask(Long taskId) {
		Task task = taskService.getTaskById(taskId);
		task.setLastExeTime(new Date());
		Map<String,String> result = null;
		if("http".equals(task.getTaskType())) {
			result = taskExcService.httpExc(task);
			log.info("httpExc result:{}",result);
		}else if("shell".equals(task.getTaskType())) {
			result = taskExcService.shellExc(task);
			log.info("shellExc result:{}",result);
		}
		
		String retCode = result==null ? "0" : result.get("code");
		if("success".equals(retCode)) {
			task.setLastExeStatus("1");//成功
		}else {
			task.setLastExeStatus("0");//失败
		}
		taskService.saveTask(task);
		return result.get("code");
	}
	
	@GetMapping("/nextExcTimes")
	@ResponseBody
	public Object nextExcTimes(String cron,Model model) {
		//model.addAttribute("task", new Task());
		List<String> result = new ArrayList<String>();
		if(StringUtils.hasLength(cron)) {
			try {
				CronExpression ce = CronExpression.parse(cron);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				ZonedDateTime archTime = ZonedDateTime.now();
				int count = 0;
				while(count < 5) {
					ZonedDateTime next = ce.next(archTime);
					result.add(sdf.format(Date.from(next.toInstant())));
					archTime = next;
					count++; 
				}
				
			}catch(Exception e) {
				result.add(e.getMessage());
			}
		}
		return result;
	}
	
}
