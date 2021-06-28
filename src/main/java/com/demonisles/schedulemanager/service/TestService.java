package com.demonisles.schedulemanager.service;

import java.util.Map;

import com.demonisles.schedulemanager.domain.Task;

public interface TestService {
	
	String httpTest(Task task);
	
	String httpGetTest(String url, Map<String, String> uriVariables);
	
	String httpPostTest(String url, Map<String, String> uriVariables, String contentType);

}
