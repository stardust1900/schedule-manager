package com.demonisles.schedulemanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.demonisles.schedulemanager.domain.Task;

public interface TaskRepository extends PagingAndSortingRepository<Task, Long> ,JpaSpecificationExecutor<Task> {

	Page<Task> findAllByTaskNameContainingAndTaskType(String taskName, String taskType, Pageable page);
}
