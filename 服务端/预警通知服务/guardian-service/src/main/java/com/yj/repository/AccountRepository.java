package com.yj.repository;

import com.yj.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.Id;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
