package net.montaki.accountservice.mcp;

import lombok.RequiredArgsConstructor;
import net.montaki.accountservice.domain.Account;
import net.montaki.accountservice.service.AccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountMcpTools {
    private final AccountService accountService;

    @Tool(description = "Obtenir un compte bancaire par son id")
    public Account getAccount(String id) { return accountService.findById(UUID.fromString(id)); }

    @Tool(description = "Lister tous les comptes bancaires")
    public List<Account> listAccounts() { return accountService.findAll(); }

    @Tool(description = "Créer un compte bancaire")
    public Account createAccount(String customerReference, String currency, double balance) {
        return accountService.create(customerReference, currency, balance);
    }
}