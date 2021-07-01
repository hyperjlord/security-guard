package website.qingxu.security.account.service;

import org.springframework.data.jpa.repository.JpaRepository;
import website.qingxu.security.account.entity.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByPhone(String phone);
}
