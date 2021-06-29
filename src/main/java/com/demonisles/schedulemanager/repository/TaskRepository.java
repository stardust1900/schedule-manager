package com.demonisles.schedulemanager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.demonisles.schedulemanager.domain.Task;

public interface TaskRepository extends PagingAndSortingRepository<Task, Long> ,JpaSpecificationExecutor<Task> {

	Page<Task> findAllByTaskNameContainingAndTaskType(String taskName, String taskType, Pageable page);
	
	@Query("select t from Task t where t.delFlag = 0 ")
	List<Task> findAllNotDel();
}
