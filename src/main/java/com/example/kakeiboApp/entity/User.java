package com.example.kakeiboApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Repository;

import lombok.Data;

@Repository
@Data

public class User {
	@Id
	private String id;
	
	private String email;
	
	private String password;

}
