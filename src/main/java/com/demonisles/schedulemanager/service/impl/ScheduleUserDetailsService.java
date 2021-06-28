package com.demonisles.schedulemanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.demonisles.schedulemanager.domain.ScheduleUserDetails;
import com.demonisles.schedulemanager.domain.User;
import com.demonisles.schedulemanager.repository.UserRepository;

@Component
public class ScheduleUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("No userDetail found with username '%s'.", username));
		}
		return new ScheduleUserDetails(user);
	}

}
