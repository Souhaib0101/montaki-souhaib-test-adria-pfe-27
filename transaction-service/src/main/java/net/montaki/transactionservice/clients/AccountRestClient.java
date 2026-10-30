package net.montaki.transactionservice.clients;

import net.montaki.transactionservice.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${clients.account-service.url:}")
public interface AccountRestClient {
    @GetMapping("/accounts/{id}")
    Account getAccountById(@PathVariable UUID id);

    @PutMapping("/accounts/{id}/balance")
    Account updateBalance(@PathVariable UUID id, @RequestBody BalanceRequest request);

    record BalanceRequest(double balance) {}
}