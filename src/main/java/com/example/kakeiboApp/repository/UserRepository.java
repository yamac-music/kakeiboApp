package com.example.kakeiboApp.repository;

import org.springframework.stereotype.Repository;

import com.example.kakeiboApp.entity.User;

@Repository
public interface UserRepository {
	User findUserByUserName(String userName);
}
