package net.montaki.accountservice.web.rest;

import lombok.RequiredArgsConstructor;
import net.montaki.accountservice.domain.Account;
import net.montaki.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountRestController {
    private final AccountService accountService;

    public record CreateAccountRequest(String customerReference, String currency, double balance) {}
    public record BalanceRequest(double balance) {}

    @PostMapping
    public Account create(@RequestBody CreateAccountRequest r) {
        return accountService.create(r.customerReference(), r.currency(), r.balance());
    }
    @GetMapping
    public List<Account> findAll() { return accountService.findAll(); }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable UUID id) { return accountService.findById(id); }

    @PutMapping("/{id}/balance")
    public Account updateBalance(@PathVariable UUID id, @RequestBody BalanceRequest r) {
        return accountService.updateBalance(id, r.balance());
    }
}