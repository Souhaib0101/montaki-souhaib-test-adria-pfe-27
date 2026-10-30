package net.montaki.transactionservice;

import net.montaki.transactionservice.clients.AccountRestClient;
import net.montaki.transactionservice.model.Account;
import net.montaki.transactionservice.service.TransactionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
public class TransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedTransactions(TransactionService transactionService,
                                       AccountRestClient accountRestClient) {
        return args -> {
            if (!transactionService.findAll().isEmpty()) return;
            try {
                List<Account> accounts = accountRestClient.getAccounts();
                String[] categories = {"FOOD", "TRAVEL", "SHOPPING", "FUEL", "HEALTH"};
                String[] countries = {"MA", "FR", "ES", "US", "DE"};
                int i = 0;
                for (Account account : accounts) {
                    transactionService.create(account.getId(),
                            BigDecimal.valueOf(50 + i * 10),
                            account.getCurrency(),
                            categories[i % categories.length],
                            countries[i % countries.length]);
                    i++;
                }
            } catch (Exception e) {
                System.out.println("Seed ignoré : account-service indisponible");
            }
        };
    }
}