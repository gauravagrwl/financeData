package org.gauravagrwl.financeData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
@EnableMongoRepositories("org.gauravagrwl.financeData.model")
@EnableAsync
public class FinanceDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceDataApplication.class, args);
	}

	//TODO: Naming Conventions
	//TODO: Java Docs
	//TODO: Add API Docs
	//TODO: Set user from SecurityContext
	//TODO: Set Security Context (JWT or LDAP or SAML or OAuth)
	//TODO: Exception Handling
	//TODO: Set validations
	//TODO: Add Swagger Documentations

}
