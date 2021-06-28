package com.demonisles.schedulemanager.repository;

import org.springframework.data.repository.CrudRepository;

import com.demonisles.schedulemanager.domain.TaskLog;

public interface TaskLogRepository extends CrudRepository<TaskLog, Long> {

}
