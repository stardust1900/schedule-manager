package com.demonisles.schedulemanager;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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

import com.demonisles.schedulemanager.domain.Role;
import com.demonisles.schedulemanager.domain.User;
import com.demonisles.schedulemanager.repository.RoleRepository;
import com.demonisles.schedulemanager.repository.UserRepository;

@SpringBootApplication
@EnableScheduling
public class ScheduleManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduleManagerApplication.class, args);
	}
	
	@Bean
	  public CommandLineRunner demo(UserRepository userRepo,RoleRepository roleRepo) {
		return (args)->{
//			userRepo.deleteAll();
//			roleRepo.deleteAll();
			Iterable<User> users = userRepo.findAll();
			if(users == null || !users.iterator().hasNext()) {
				PasswordEncoder pe = passwordEncoder();
				User user = new User();
				user.setUserName("user");
				user.setPassword(pe.encode("password"));
				
				Role role = new Role();
				role.setRoleKey("default");
				role.setRoleName("default");
				roleRepo.save(role);
				
				Set<Role> roles = new HashSet<Role>();
				roles.add(role);
				user.setRoles(roles);
				
				userRepo.save(user);
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
