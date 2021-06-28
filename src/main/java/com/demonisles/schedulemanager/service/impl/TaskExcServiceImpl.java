package com.demonisles.schedulemanager.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import com.demonisles.schedulemanager.domain.Task;
import com.demonisles.schedulemanager.service.TaskExcService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskExcServiceImpl implements TaskExcService {

	private static final Logger log = LoggerFactory.getLogger(TaskExcServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String httpGet(String url, Map<String, String> uriVariables) {
		StringBuffer gurl = new StringBuffer(url);

		if (!CollectionUtils.isEmpty(uriVariables)) {
			gurl.append("?");
			for (Entry<String, String> entry : uriVariables.entrySet()) {
				gurl.append(entry.getKey()).append("={").append(entry.getKey()).append("}&");
			}
			gurl.deleteCharAt(gurl.length() - 1);// 删除最后一个&
		}

		log.info("gurl:{}", gurl);
		return restTemplate.getForObject(gurl.toString(), String.class, uriVariables);
	}

	@Override
	public String httpPost(String url, Map<String, String> uriVariables, String contentType) {
		if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType)) {
			MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
			for (Entry<String, String> entry : uriVariables.entrySet()) {
				paramMap.add(entry.getKey(), entry.getValue());
			}
			return restTemplate.postForObject(url, paramMap, String.class);
		} else if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-type", "application/json");
			headers.add("Connection", "Keep-Alive");
			headers.add("Charset", "UTF-8");
			headers.add("Content-Length", "0");
			ObjectMapper mapper = new ObjectMapper();
			try {
				String jsonParameter = mapper.writeValueAsString(uriVariables);
				HttpEntity<String> entity = new HttpEntity<String>(jsonParameter, headers);
				return restTemplate.postForObject(url, entity, String.class);
			} catch (JsonProcessingException e) {
				return e.getMessage();
			}
			
		} else {
			return "unsupported contentType";
		}
	}

	@Override
	public String httpExc(Task task) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode params = mapper.readTree(task.getParams());
			JsonNode methodNode = params.get("method");

			String method = methodNode.asText();
			String url = params.get("url").asText();
			String contentType = params.get("contentType").asText();
			String strArgs = params.get("args").asText();
			Map<String, String> uriVariables = new HashMap<String, String>();
			if (!StringUtils.isEmpty(strArgs)) {
				JsonNode args = mapper.readTree(strArgs);
				log.info("method:{} url:{} args:{}", method, url, args);
				if (args != null && args.fieldNames().hasNext()) {
					for (Iterator<String> it = args.fieldNames(); it.hasNext();) {
						String field = it.next();
						uriVariables.put(field, args.get(field).asText());
					}
				}
			}
			if ("get".equalsIgnoreCase(method)) {
				return httpGet(url, uriVariables);
			} else if ("post".equalsIgnoreCase(method)) {
				return httpPost(url, uriVariables, contentType);
			} else {
				return "unsupported method";
			}
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException", e);
			return e.getMessage();
		}

	}

}
