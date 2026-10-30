package net.montaki.accountservice.repository;

import net.montaki.accountservice.domain.Account;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {}