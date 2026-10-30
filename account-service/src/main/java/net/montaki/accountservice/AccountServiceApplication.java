package net.montaki.accountservice;

import net.montaki.accountservice.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner seedAccounts(AccountService accountService) {
		return args -> {
			if (!accountService.findAll().isEmpty()) return;
			for (int i = 1; i <= 10; i++) {
				accountService.create(String.format("CUST-%03d", i), "EUR", i * 1000.0);
			}
		};
	}


}
