package com.demonisles.schedulemanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/h2-console/**","/testTask").permitAll()
		.anyRequest()
		.authenticated()
				.and().formLogin().loginPage("/login").permitAll()
				.and().logout().permitAll();
		http.csrf().ignoringAntMatchers("/h2-console/**"); // 禁用 H2 控制台的 CSRF 防护
		http.headers().frameOptions().sameOrigin(); // 允许来自同一来源的H2 控制台的请求
	}

//	@Bean
//	@Override
//	public UserDetailsService userDetailsService() {
//		UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER")
//				.build();
//
//		return new InMemoryUserDetailsManager(user);
//	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/js/**", "/css/**","/images/**");
	}
}
