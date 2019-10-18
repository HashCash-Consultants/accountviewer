package com.account.accountviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.account.accountviewer.controller.SimpleCORSFilter;

@SpringBootApplication
public class AccountviewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountviewerApplication.class, args);
	}
}
