package net.montaki.transactionservice.web.rest;

import lombok.RequiredArgsConstructor;
import net.montaki.transactionservice.domain.Transaction;
import net.montaki.transactionservice.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionRestController {
    private final TransactionService transactionService;

    public record CreateRequest(UUID accountId, BigDecimal amount, String currency,
                                String merchantCategory, String country) {}
    public record TransferRequest(UUID fromId, UUID toId, BigDecimal amount) {}

    @PostMapping
    public Transaction create(@RequestBody CreateRequest r) {
        return transactionService.create(r.accountId(), r.amount(), r.currency(),
                r.merchantCategory(), r.country());
    }
    @GetMapping
    public List<Transaction> findAll() { return transactionService.findAll(); }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable UUID id) { return transactionService.findById(id); }

    @GetMapping("/account/{accountId}")
    public List<Transaction> findByAccount(@PathVariable UUID accountId) {
        return transactionService.findByAccount(accountId);
    }
    @PostMapping("/transfer")
    public Map<String, String> transfer(@RequestBody TransferRequest r) {
        transactionService.transfer(r.fromId(), r.toId(), r.amount());
        return Map.of("status", "OK");
    }
}