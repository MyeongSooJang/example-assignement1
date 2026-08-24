package com.jms.assignment1.infrastructure.repository;

import com.jms.assignment1.repository.UserRepository;
import com.jms.assignment1.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(entity -> entity.toDomain());
    }
}
