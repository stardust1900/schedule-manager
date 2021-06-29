package com.demonisles.schedulemanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.demonisles.schedulemanager.domain.TaskLog;

public interface TaskLogRepository
		extends PagingAndSortingRepository<TaskLog, Long>, JpaSpecificationExecutor<TaskLog> {

	Page<TaskLog> findAllByTaskTaskId(Long taskId,Pageable page);
}
