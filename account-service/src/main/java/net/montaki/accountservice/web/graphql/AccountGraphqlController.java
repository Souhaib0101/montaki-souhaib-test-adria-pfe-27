package net.montaki.accountservice.web.graphql;

import lombok.RequiredArgsConstructor;
import net.montaki.accountservice.domain.Account;
import net.montaki.accountservice.service.AccountService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphqlController {
    private final AccountService accountService;

    @QueryMapping
    public List<Account> accounts() { return accountService.findAll(); }

    @QueryMapping
    public Account accountById(@Argument UUID id) { return accountService.findById(id); }

    @MutationMapping
    public Account createAccount(@Argument String customerReference,
                                 @Argument String currency,
                                 @Argument double balance) {
        return accountService.create(customerReference, currency, balance);
    }
}