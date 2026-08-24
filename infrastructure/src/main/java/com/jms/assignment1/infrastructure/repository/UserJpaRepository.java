package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
