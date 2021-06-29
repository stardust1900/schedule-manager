package com.demonisles.schedulemanager.service;

import java.util.Map;

import com.demonisles.schedulemanager.domain.Task;

public interface TaskExcService {
	
	Map<String,String> httpExc(Task task);
	
	String httpGet(String url, Map<String, String> uriVariables) throws Exception;
	
	String httpPost(String url, Map<String, String> uriVariables, String contentType) throws Exception;
}
