package com.demonisles.schedulemanager.service;

import java.util.Map;

import com.demonisles.schedulemanager.domain.Task;

/**
 * 任务执行服务
 *
 * @author shawn
 *
 * @site http://wangxuan.me
 *
 * 2021-07-12
 *
 *
 */
public interface TaskExcService {
	/**
	 * http 任务执行
	 * 
	 * @param task
	 * @return
	 */
	Map<String,String> httpExc(Task task);
	/**
	 * http Get方法 任务执行
	 * 
	 * @param url
	 * @param uriVariables
	 * @return
	 * @throws Exception
	 */
	String httpGet(String url, Map<String, String> uriVariables) throws Exception;
	/**
	 *  http Post方法 任务执行
	 * 
	 * @param url
	 * @param uriVariables
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	String httpPost(String url, Map<String, String> uriVariables, String contentType) throws Exception;
	/**
	 * shell 任务执行
	 * 
	 * @param task
	 * @return
	 */
	Map<String, String> shellExc(Task task);
	/**
	 * sql 任务执行
	 * 
	 * @param task
	 * @return
	 */
	Map<String, String> sqlExc(Task task);
}
