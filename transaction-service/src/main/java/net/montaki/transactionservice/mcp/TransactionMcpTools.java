package net.montaki.transactionservice.mcp;

import lombok.RequiredArgsConstructor;
import net.montaki.transactionservice.domain.Transaction;
import net.montaki.transactionservice.service.TransactionService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionMcpTools {
    private final TransactionService transactionService;

    @Tool(description = "Créer une transaction sur un compte")
    public Transaction createTransaction(String accountId, double amount, String currency,
                                         String merchantCategory, String country) {
        return transactionService.create(UUID.fromString(accountId), BigDecimal.valueOf(amount),
                currency, merchantCategory, country);
    }
    @Tool(description = "Lister toutes les transactions")
    public List<Transaction> listTransactions() { return transactionService.findAll(); }

    @Tool(description = "Lister les transactions d'un compte")
    public List<Transaction> getTransactionsByAccount(String accountId) {
        return transactionService.findByAccount(UUID.fromString(accountId));
    }
    @Tool(description = "Virement d'un compte vers un autre")
    public String transfer(String fromId, String toId, double amount) {
        transactionService.transfer(UUID.fromString(fromId), UUID.fromString(toId),
                BigDecimal.valueOf(amount));
        return "Virement effectué";
    }
}