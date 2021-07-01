package website.qingxu.security.account.service;

import org.springframework.data.jpa.repository.JpaRepository;
import website.qingxu.security.account.entity.Guard;
import website.qingxu.security.account.entity.GuardPK;

import java.util.List;

public interface GuardRepository extends JpaRepository<Guard, GuardPK> {
    List<Guard> findAllByGuardian(long guardian);
    List<Guard> findAllByWard(long ward);
}
