package com.demonisles.schedulemanager.config;

import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController("/home").setViewName("home");
//		registry.addViewController("/").setViewName("home");
		//registry.addViewController("/hello").setViewName("hello");
		registry.addViewController("/login").setViewName("login");
		//registry.addViewController("/demo").setViewName("demo");
		//registry.addViewController("/toAdd").setViewName("add");
	}
	
//	@Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//            registry.addResourceHandler("/resources/**")
//                    .addResourceLocations("/public", "classpath:/static/")
//                    .setCachePeriod(31556926);
//    }
}
