package com.demonisles.schedulemanager;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.demonisles.schedulemanager.controller.TaskController;
import com.demonisles.schedulemanager.domain.Role;
import com.demonisles.schedulemanager.domain.Task;
import com.demonisles.schedulemanager.domain.User;
import com.demonisles.schedulemanager.repository.RoleRepository;
import com.demonisles.schedulemanager.repository.TaskRepository;
import com.demonisles.schedulemanager.repository.UserRepository;
import com.demonisles.schedulemanager.service.ScheduleService;

@SpringBootApplication
@EnableScheduling
public class ScheduleManagerApplication {
	private static final Logger log = LoggerFactory.getLogger(TaskController.class);
	public static void main(String[] args) {
		SpringApplication.run(ScheduleManagerApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner demo(UserRepository userRepo,RoleRepository roleRepo,
			TaskRepository taskRepo, ScheduleService scheduleService) {
		
		return (args)->{
			
//			userRepo.deleteAll();
//			roleRepo.deleteAll();
			Iterable<User> users = userRepo.findAll();
			//初始化用户
			if(users == null || !users.iterator().hasNext()) {
				PasswordEncoder pe = passwordEncoder();
				PasswordGenerator generator = new PasswordGenerator();
				List<CharacterRule> rules = Arrays.asList(
						  // at least one upper-case character
						  new CharacterRule(EnglishCharacterData.UpperCase, 1),
						  // at least one lower-case character
						  new CharacterRule(EnglishCharacterData.LowerCase, 1),
						  // at least one digit character
						  new CharacterRule(EnglishCharacterData.Digit, 1));
				String password = generator.generatePassword(8, rules);
				User user = new User();
				user.setUserName("admin");
				log.warn("\n ###########\n 初始用户名:admin 密码:{}\n ###########\n",password);
				user.setPassword(pe.encode(password));
				
				Role role = new Role();
				role.setRoleKey("default");
				role.setRoleName("default");
				roleRepo.save(role);
				
				Set<Role> roles = new HashSet<Role>();
				roles.add(role);
				user.setRoles(roles);
				
				userRepo.save(user);
			}
			//启动任务
			Iterable<Task> tasks = taskRepo.findAllNotDel();
			for(Task t : tasks) {
				scheduleService.scheduleCronTask(t);
			}
			
		};
		
	}
	
	/**
     * 装载BCrypt密码编码器
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
	public RestTemplate restTemplate(){
		 HttpComponentsClientHttpRequestFactory factory = null;
		//忽略ssl证书
		try {
			TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
			SSLConnectionSocketFactory connectionSocketFactory =
	                new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

	        HttpClientBuilder httpClientBuilder = HttpClients.custom();
	        httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
	        CloseableHttpClient httpClient = httpClientBuilder.build();
	        factory = new HttpComponentsClientHttpRequestFactory();
	        factory.setHttpClient(httpClient);
	        factory.setConnectTimeout(10 * 1000);
	        factory.setReadTimeout(30 * 1000);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
        
	    RestTemplate restTemplate = new RestTemplate(factory);
	    //返回结果中文乱码问题
	    //restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
	    List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
	    converterList.stream().forEach(httpMessageConverter -> {
	    	if(httpMessageConverter instanceof StringHttpMessageConverter) {
	    		StringHttpMessageConverter converter = (StringHttpMessageConverter)httpMessageConverter;
	    		converter.setDefaultCharset(Charset.forName("UTF-8"));
	    	}
	    });
	    
	    /*converterList.stream().forEach(new Consumer<HttpMessageConverter<?>>(){
			@Override
			public void accept(HttpMessageConverter<?> httpMessageConverter) {
				if(httpMessageConverter instanceof StringHttpMessageConverter) {
		    		StringHttpMessageConverter converter = (StringHttpMessageConverter)httpMessageConverter;
		    		converter.setDefaultCharset(Charset.forName("UTF-8"));
		    	}
			}});*/
	    return restTemplate;
	}
}
