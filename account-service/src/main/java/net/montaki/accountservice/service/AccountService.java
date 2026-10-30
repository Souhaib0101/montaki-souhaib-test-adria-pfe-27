package net.montaki.accountservice.service;

import lombok.RequiredArgsConstructor;
import net.montaki.accountservice.domain.Account;
import net.montaki.accountservice.domain.AccountStatus;
import net.montaki.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account create(String customerReference, String currency, double balance) {
        if (balance < 0) throw new BusinessException("Solde initial négatif");
        return accountRepository.save(Account.builder()
                .customerReference(customerReference)
                .currency(currency)
                .balance(balance)
                .status(AccountStatus.ACTIVE)
                .build());
    }

    @Transactional(readOnly = true)
    public Account findById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Compte introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Account> findAll() { return accountRepository.findAll(); }

    public Account updateBalance(UUID id, double newBalance) {
        Account account = findById(id);
        if (account.getStatus() == AccountStatus.BLOCKED)
            throw new BusinessException("Compte bloqué");
        if (newBalance < 0) throw new BusinessException("Solde négatif interdit");
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
}