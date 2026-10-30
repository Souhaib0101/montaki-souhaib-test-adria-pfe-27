package net.montaki.transactionservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import net.montaki.transactionservice.clients.AccountRestClient;
import net.montaki.transactionservice.model.Account;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountResilienceService {
    private final AccountRestClient accountRestClient;

    @CircuitBreaker(name = "accountService", fallbackMethod = "getDefaultAccount")
    public Account fetchAccount(UUID id) {
        return accountRestClient.getAccountById(id);
    }

    public Account getDefaultAccount(UUID id, Exception e) {
        return new Account(id, "Default customer", "UNAVAILABLE", "N/A", 0);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "updateBalanceFallback")
    public Account pushBalance(UUID id, double newBalance) {
        return accountRestClient.updateBalance(id, new AccountRestClient.BalanceRequest(newBalance));
    }

    public Account updateBalanceFallback(UUID id, double newBalance, Exception e) {
        throw new BusinessException("Account-service indisponible : solde non mis à jour");
    }
}