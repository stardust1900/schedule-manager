package com.demonisles.schedulemanager.service.impl;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.demonisles.schedulemanager.domain.Task;
import com.demonisles.schedulemanager.domain.TaskLog;
import com.demonisles.schedulemanager.repository.TaskLogRepository;
import com.demonisles.schedulemanager.service.TaskLogService;

@Service
public class TaskLogServiceImpl implements TaskLogService {

	private static final Logger log = LoggerFactory.getLogger(TaskLogServiceImpl.class);
			
	@Autowired
	private TaskLogRepository taskLogRepo;

	@Autowired
	private EntityManager entityManager;
	 
	@Override
	public Page<TaskLog> listTask(Long taskId, String taskDate, Pageable page) {
		log.info("entityManager:{}",entityManager);
		
		return taskLogRepo.findAll(new Specification<TaskLog>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<TaskLog> taskLog, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				ArrayList<Predicate> list = new ArrayList<>();
				if (taskId != null) {
					Predicate flag = criteriaBuilder.equal(taskLog.get("task").get("taskId"), taskId);
					list.add(flag);
				}
				if (StringUtils.hasLength(taskDate)) {
					// criteriaBuilder.equal(x, y);
					Expression<String> taskBegin = criteriaBuilder.function("FORMATDATETIME", String.class,
							taskLog.get("taskBegin"), new LiteralExpression<String>((CriteriaBuilderImpl) criteriaBuilder,"yyyy-MM-dd"));
					//criteriaBuilder.parameter(String.class, "yyyy-MM-dd");
					Predicate taskBeginPre = criteriaBuilder.equal(taskBegin, taskDate);
					list.add(taskBeginPre);
				}
				Predicate[] array = new Predicate[list.size()];
				Predicate[] predicates = list.toArray(array);

				return criteriaBuilder.and(predicates);
			}
		}, page);
	}

	public Page<TaskLog> listTask1(Long taskId, Pageable page) {
		if (null == taskId) {
			return taskLogRepo.findAll(page);
		}
		return taskLogRepo.findAllByTaskTaskId(taskId, page);
	}

}
