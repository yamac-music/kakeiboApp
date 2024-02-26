package com.example.kakeiboApp.entity;

import java.sql.Date;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Kakeibo {
	//ID
	@Id
	private Integer id;
	
	//年月日
	private Date date;
	
	//場所
	private String place;
	
	//カテゴリ
	private String category;
	
	//人
	private String person;
	
	//金額
	private Integer price;
	
}
