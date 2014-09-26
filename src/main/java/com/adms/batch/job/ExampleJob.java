package com.adms.batch.job;

import java.util.Calendar;

import org.springframework.stereotype.Component;

@Component
public class ExampleJob {

	public void printCurrentDate() {
		System.out.println("Now: " + Calendar.getInstance().getTime());
	}
	
}
