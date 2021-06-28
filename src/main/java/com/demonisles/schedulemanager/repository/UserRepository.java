package com.demonisles.schedulemanager.repository;

import org.springframework.data.repository.CrudRepository;

import com.demonisles.schedulemanager.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByUserName(String userName);

}
