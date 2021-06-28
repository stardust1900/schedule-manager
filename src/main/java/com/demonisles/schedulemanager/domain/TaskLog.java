package com.demonisles.schedulemanager.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	@Column(name = "task_id")
	private long taskId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "task_begin")
	private Date taskBegin;
	
	@Column(name = "task_end")
	private Date taskEnd;
	
	@Column(name = "request_params")
	private String requestParams;
	
	@Column(name = "response_params")
	private String responseParams;
	
	@Column(name = "remark")
	private String remark;
	
}
