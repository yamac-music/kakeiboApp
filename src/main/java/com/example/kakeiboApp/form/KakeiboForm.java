package com.example.kakeiboApp.form;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Form
@Data
@NoArgsConstructor
@AllArgsConstructor

public class KakeiboForm {
	
	//ID
	private Integer id;
	
	//日付
	@NotNull
	private Date date;
	
	//場所
	@NotBlank(message = "利用場所を入力してください")
	private String place;
	
	//カテゴリー
	@NotBlank
	private String category;
	
	//払った人
	@NotBlank
	private String person;
	
	//金額
	@NotNull(message = "金額を入力してください")
	private Integer price;

	
	/** 「登録」or「変更」判定用 */
	private Boolean newKakeibo;	
}
