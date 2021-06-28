package com.demonisles.schedulemanager;

import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.scheduling.support.CronExpression;

public class CronTest {

	public static void main(String[] args) {
		String cron = "* 1 * ? * 2";
		CronExpression ce = CronExpression.parse(cron);
		
		ZonedDateTime next = ce.next(ZonedDateTime.now());
		System.out.println(Date.from(next.toInstant()));
	}

}
