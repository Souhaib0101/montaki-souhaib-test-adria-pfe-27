package net.montaki.transactionservice.service;

import jakarta.transaction.Transactional;
import lombok.Value;
import net.montaki.transactionservice.domain.Transaction;
import net.montaki.transactionservice.model.Account;
import net.montaki.transactionservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountResilienceService accountService;
    private final BigDecimal maxAmount;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountResilienceService accountService,
                              @Value("${bank.transaction.max-amount}") BigDecimal maxAmount) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.maxAmount = maxAmount;
    }

    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0)
            throw new BusinessException("Montant doit être strictement positif");
        if (amount.compareTo(maxAmount) > 0)
            throw new BusinessException("Montant supérieur au plafond : " + maxAmount);
    }

    private Account loadActiveAccount(UUID id) {
        Account account = accountService.fetchAccount(id);
        if ("UNAVAILABLE".equals(account.getStatus()))
            throw new BusinessException("Account-service indisponible");
        if (!"ACTIVE".equals(account.getStatus()))
            throw new BusinessException("Compte non actif");
        return account;
    }

    public Transaction create(UUID accountId, BigDecimal amount, String currency,
                              String merchantCategory, String country) {
        checkAmount(amount);
        Account account = loadActiveAccount(accountId);
        if (!account.getCurrency().equals(currency))
            throw new BusinessException("Devise différente de celle du compte");
        if (account.getBalance() < amount.doubleValue())
            throw new BusinessException("Solde insuffisant");

        Transaction saved = transactionRepository.save(Transaction.builder()
                .accountId(accountId).amount(amount).currency(currency)
                .merchantCategory(merchantCategory).country(country)
                .createdAt(Instant.now()).build());

        accountService.pushBalance(accountId, account.getBalance() - amount.doubleValue());
        return saved;
    }

    @Transactional
    public void transfer(UUID fromId, UUID toId, BigDecimal amount) {
        checkAmount(amount);
        if (fromId.equals(toId)) throw new BusinessException("Comptes identiques");
        Account from = loadActiveAccount(fromId);
        Account to = loadActiveAccount(toId);
        if (!from.getCurrency().equals(to.getCurrency()))
            throw new BusinessException("Devises différentes");
        if (from.getBalance() < amount.doubleValue())
            throw new BusinessException("Solde insuffisant");

        accountService.pushBalance(fromId, from.getBalance() - amount.doubleValue());
        try {
            accountService.pushBalance(toId, to.getBalance() + amount.doubleValue());
        } catch (RuntimeException e) {
            accountService.pushBalance(fromId, from.getBalance()); // compensation
            throw new BusinessException("Virement annulé : crédit impossible");
        }
        Instant now = Instant.now();
        transactionRepository.save(Transaction.builder().accountId(fromId)
                .amount(amount.negate()).currency(from.getCurrency()).createdAt(now).build());
        transactionRepository.save(Transaction.builder().accountId(toId)
                .amount(amount).currency(to.getCurrency()).createdAt(now).build());
    }

    public List<Transaction> findAll() { return transactionRepository.findAll(); }
    public List<Transaction> findByAccount(UUID accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
    public Transaction findById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Transaction introuvable"));
    }
}