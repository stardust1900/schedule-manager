package com.demonisles.schedulemanager.service;

import java.util.Map;

import com.demonisles.schedulemanager.domain.Task;

public interface TaskExcService {
	
	String httpExc(Task task);
	
	String httpGet(String url, Map<String, String> uriVariables);
	
	String httpPost(String url, Map<String, String> uriVariables, String contentType);

}
