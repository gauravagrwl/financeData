package org.gauravagrwl.financeData;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info =@Info(title = "Finance Data API"))
public class FinanceDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceDataApplication.class, args);
	}

}
