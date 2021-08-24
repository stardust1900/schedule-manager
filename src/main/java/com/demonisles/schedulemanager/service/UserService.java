package com.demonisles.schedulemanager.service;

import com.demonisles.schedulemanager.domain.User;

public interface UserService {
	User findUserByName(String userName);

	void save(User user);
}
