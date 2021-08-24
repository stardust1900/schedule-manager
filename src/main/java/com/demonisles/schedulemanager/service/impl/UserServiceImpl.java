package com.demonisles.schedulemanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demonisles.schedulemanager.domain.User;
import com.demonisles.schedulemanager.repository.UserRepository;
import com.demonisles.schedulemanager.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public User findUserByName(String userName) {
		return userRepo.findByUserName(userName);
	}

	@Override
	public void save(User user) {
		userRepo.save(user);
	}

}