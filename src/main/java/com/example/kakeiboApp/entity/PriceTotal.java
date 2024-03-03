package com.example.kakeiboApp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceTotal {
	//人
	private String person;
	
	//当月の金額合計
	private Integer totalprice;
}
