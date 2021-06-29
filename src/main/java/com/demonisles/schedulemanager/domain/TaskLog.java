package com.demonisles.schedulemanager.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 任务执行日志
 *
 * @author shawn
 *
 * @site http://wangxuan.me
 *
 * 2021-06-17
 *
 *
 */
@Entity
@Table(name = "task_log")
public class TaskLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "log_id")
	private long logId;
	
//	@Column(name = "task_id")
//	private long taskId;
	
	@ManyToOne
	@JoinColumn(name = "task_id")
	private Task task;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "task_begin")
	private Date taskBegin;
	
	@Column(name = "task_end")
	private Date taskEnd;
	
	@Column(name = "request_params",columnDefinition="TEXT")
	private String requestParams;
	
	@Column(name = "response_params",columnDefinition="TEXT")
	private String responseParams;
	
	@Column(name = "remark")
	private String remark;

	public long getLogId() {
		return logId;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}

//	public long getTaskId() {
//		return taskId;
//	}
//
//	public void setTaskId(long taskId) {
//		this.taskId = taskId;
//	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTaskBegin() {
		return taskBegin;
	}

	public void setTaskBegin(Date taskBegin) {
		this.taskBegin = taskBegin;
	}

	public Date getTaskEnd() {
		return taskEnd;
	}

	public void setTaskEnd(Date taskEnd) {
		this.taskEnd = taskEnd;
	}

	public String getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}

	public String getResponseParams() {
		return responseParams;
	}

	public void setResponseParams(String responseParams) {
		this.responseParams = responseParams;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
