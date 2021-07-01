package com.yj.repository;

import com.yj.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting,Long> {
}
