package com.demonisles.schedulemanager.domain;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.StringUtils;
/**
 * 定时任务
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
@Table(name = "task")
public class Task {
	/**
	 * 任务id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "task_id")
	private long taskId;
	/**
	 * 任务名称
	 */
	@Column(name = "task_name")
	private String taskName;
	/**
	 * 任务描述
	 */
	@Column(name = "task_desc")
	private String taskDesc;
	/**
	 * 任务类型，http, shell, sql
	 */
	@Column(name = "task_type")
	private String taskType;
	/**
	 * cron表达式
	 */
	@Column(name = "cron")
	private String cron;
	/**
	 * 任务执行参数
	 */
	@Column(name = "params")
	private String params;
	/**
	 * 任务状态 0:暂停 1:启动  默认启动
	 */
	@Column(name = "task_state")
	private String taskState = "1";
	/**
	 * 创建人
	 */
	@Column(name = "created_by")
	private String createdBy;
	/**
	 * 创建时间
	 */
	@Column(name = "created_time")
	private Date createdTime;
	/**
	 * 更新人
	 */
	@Column(name = "updated_by")
	private String updatedBy;
	/**
	 * 更新时间
	 */
	@Column(name = "updated_time")
	private Date updatedTime;
	/**
	 * 上次执行时间
	 */
	@Column(name = "last_exe_time")
	private Date lastExeTime;
	/**
	 * 上次执行状态 0：失败 1:成功
	 */
	@Column(name = "last_exe_status")
	private String lastExeStatus;
	
	/**
	 * 删除标记 0:未删除 1:已删除
	 */
	@Column(name ="del_flag",columnDefinition="tinyint default 0")
	private int delFlag = 0;
	
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskDesc() {
		return taskDesc;
	}
	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getTaskState() {
		return taskState;
	}
	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	public Date getLastExeTime() {
		return lastExeTime;
	}
	public void setLastExeTime(Date lastExeTime) {
		this.lastExeTime = lastExeTime;
	}
	public String getLastExeStatus() {
		return lastExeStatus;
	}
	public void setLastExeStatus(String lastExeStatus) {
		this.lastExeStatus = lastExeStatus;
	}
	
	public int getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}
	public String getNextExcTime() {
		if(StringUtils.hasLength(cron)) {
			try {
				CronExpression ce = CronExpression.parse(cron);
				ZonedDateTime next = ce.next(ZonedDateTime.now());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.format(Date.from(next.toInstant()));
			}catch(Exception e) {
				return null;
			}
		}
		return null;
	}
	@Override
	public String toString() {
		return "Task [taskId=" + taskId + ", taskName=" + taskName + ", taskDesc=" + taskDesc + ", taskType=" + taskType
				+ ", cron=" + cron + ", params=" + params + ", taskState=" + taskState + "]";
	}
}
