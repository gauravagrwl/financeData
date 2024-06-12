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
@OpenAPIDefinition(info = @Info(title = "Personal Finance Data API"))
public class FinanceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceAppApplication.class, args);
    }

    //TODO: 1. Add validation for ENUMS in Rest controller REQUEST.
    //TODO: 2. Decide where the duplicate indicator should be. (In transaction statement or in Statement Model.
    //TODO: 3. NEed to store the amount as negative from transactions. But at time of inserting into Statement make it to ABS. NEed to this to handle the transactions.

}
