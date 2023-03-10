package com.demonisles.schedulemanager.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.demonisles.schedulemanager.domain.Task;
import com.demonisles.schedulemanager.service.TaskExcService;
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
	public String httpPost(String url, Map<String, String> uriVariables, String contentType) throws Exception {
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

			String jsonParameter = mapper.writeValueAsString(uriVariables);
			HttpEntity<String> entity = new HttpEntity<String>(jsonParameter, headers);
			return restTemplate.postForObject(url, entity, String.class);

		} else {
			return "unsupported contentType";
		}
	}

	@Override
	public Map<String, String> httpExc(Task task) {
		Map<String, String> result = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode params = mapper.readTree(task.getParams());
			JsonNode methodNode = params.get("method");

			String method = methodNode.asText();
			String url = params.get("url").asText();
			String contentType = params.get("contentType").asText();
			String strArgs = params.get("args").asText();
			Map<String, String> uriVariables = new HashMap<String, String>();
			if (StringUtils.hasLength(strArgs)) {
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
				String getResult = httpGet(url, uriVariables);
				result.put("code", "success");
				result.put("msg", getResult);
				return result;
			} else if ("post".equalsIgnoreCase(method)) {
				String postResult = httpPost(url, uriVariables, contentType);
				result.put("code", "success");
				result.put("msg", postResult);
				return result;
			} else {
				result.put("code", "fail");
				result.put("msg", "unsupported method");
				return result;
			}
		} catch (Exception e) {
			log.error("httpExc error", e);
			result.put("code", "fail");
			result.put("msg", e.getMessage());
			return result;
		}

	}

	@Override
	public Map<String, String> shellExc(Task task) {
		Map<String, String> result = new HashMap<String, String>();
		if (null == task || !StringUtils.hasLength(task.getParams())) {
			result.put("code", "fail");
			result.put("msg", "params is null");
			return result;
		}
		String[] strArray = task.getParams().split(" ");
		List<String> commands = new ArrayList<String>();
		for (String s : strArray) {
			if (StringUtils.hasLength(s)) {
				commands.add(s);
			}
		}
		log.info("commands:{}", commands);
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.redirectErrorStream(true);// 将错误流中的数据合并到输入流
		BufferedReader br = null;
		BufferedReader errorBr = null;
		try {
			Process p = pb.start();
			if (p.isAlive()) {
				p.waitFor();
			}
			br = new BufferedReader(new InputStreamReader(p.getInputStream(), "utf-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			result.put("code", "success");
			result.put("msg", sb.toString());

		} catch (IOException e) {
			log.error("shellExc error", e);
			result.put("code", "fail");
			result.put("msg", e.getMessage());
		} catch (InterruptedException e) {
			log.error("shellExc error", e);
			result.put("code", "fail");
			result.put("msg", e.getMessage());
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(errorBr);
		}

		return result;
	}
	
	@Override
	public Map<String, String> sqlExc(Task task) {
		Map<String, String> result = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode params = mapper.readTree(task.getParams());
			String jdbcUrl = params.get("jdbcUrl").asText();
			String username = params.get("username").asText();
			String password = params.get("password").asText();
			String sql = params.get("sql").asText();
			DriverManagerDataSource dataSource = new DriverManagerDataSource(jdbcUrl,username,password);
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.execute(sql);
			result.put("code", "success");
			result.put("msg", "finish");
		} catch (Exception e) {
			log.error("sqlExc error", e);
			result.put("code", "fail");
			result.put("msg", e.getMessage());
		}
		return result;
	}

}
