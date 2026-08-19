package com.jms.assignment1.repository;

import com.jms.assignment1.user.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);
}
