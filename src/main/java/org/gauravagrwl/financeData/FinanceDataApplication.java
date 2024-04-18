package org.gauravagrwl.financeData;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories("org.gauravagrwl.financeData.model")
@EnableAsync
@OpenAPIDefinition(info = @Info(title = "Market Data API"))
public class FinanceDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceDataApplication.class, args);
    }

    // TODO: Naming Conventions
    // TODO: Java Docs
    // TODO: Add API Docs
    // TODO: Set user from SecurityContext
    // TODO: Set Security Context (JWT or LDAP or SAML or OAuth)
    // TODO: Exception Handling
    // TODO: Set validations
    // TODO: Add Swagger Documentations
    // TODO: Use UUID
    // TODO: Add hard stop no update once reconciled. do not allow duplicate record insert after that. Any update only after that date and recalculate / update from date date.
    //TODO: Current name is confusing need to rename- methods and attributes


}
