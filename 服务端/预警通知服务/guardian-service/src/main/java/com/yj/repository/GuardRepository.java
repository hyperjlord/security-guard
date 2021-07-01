package com.yj.repository;

import com.yj.entity.Guard;
import com.yj.entity.pk.GuardPK;
import com.yj.vo.GuardianInfoVo;
import com.yj.vo.WardInfoVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface GuardRepository extends JpaRepository<Guard, GuardPK> {
    List<Guard> findAllByGuardPKGuardian(Long guardian);
    List<Guard> findAllByGuardPKWard(Long ward);
}
