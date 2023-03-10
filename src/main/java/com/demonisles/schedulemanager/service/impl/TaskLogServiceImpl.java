package com.demonisles.schedulemanager.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

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
	public Page<TaskLog> listTaskLog(Long taskId, String taskDate, Pageable page) {
		log.info("entityManager:{}", entityManager);

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
//					Expression<String> taskBegin = criteriaBuilder.function("FORMATDATETIME", String.class,
//							taskLog.get("taskBegin"),
//							new LiteralExpression<String>((CriteriaBuilderImpl) criteriaBuilder, "yyyy-MM-dd"));
//					Predicate taskBeginPre = criteriaBuilder.equal(taskBegin, taskDate);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date beginDate = sdf.parse(taskDate);
						Calendar end = Calendar.getInstance();
						end.setTime(beginDate);
						end.add(Calendar.DAY_OF_MONTH, 1);
						end.add(Calendar.SECOND, -1);
						Predicate taskBeginPre = criteriaBuilder.between(taskLog.get("taskBegin"), beginDate, end.getTime());
						list.add(taskBeginPre);
					} catch (ParseException e) {
						log.error("listTaskLog error",e);
					}
					
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

	@Override
	@Transactional
	public void clearLogsBefore(String clearDate) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaDelete<TaskLog> taskLogCriteriaDelete = cb.createCriteriaDelete(TaskLog.class);
		Root<TaskLog> root = taskLogCriteriaDelete.from(TaskLog.class);
		
		//使用函数
//		Expression<String> taskBegin = cb.function("FORMATDATETIME", String.class,
//				root.get("taskBegin"),
//				new LiteralExpression<String>((CriteriaBuilderImpl) cb, "yyyy-MM-dd"));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date clsDate = sdf.parse(clearDate);
			//开始日期小于或等于参数
			Predicate taskBeginPre = cb.lessThanOrEqualTo(root.get("taskBegin"), clsDate);
			entityManager.createQuery(taskLogCriteriaDelete.where(taskBeginPre))
					.executeUpdate();
		} catch (ParseException e) {
			log.error("clearLogsBefore error",e);
		}
		
	}

}
