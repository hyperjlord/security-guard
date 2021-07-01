package com.yj.repository;

import com.yj.entity.Guard;
import com.yj.entity.pk.GuardPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuardRepository extends JpaRepository<Guard, GuardPK> {
    List<Guard> findAllByGuardPKGuardian(Long guardian);
    List<Guard> findAllByGuardPKWard(Long ward);
}
