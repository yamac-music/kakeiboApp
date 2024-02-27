package com.example.kakeiboApp.service;

import java.util.Optional;

import com.example.kakeiboApp.entity.Kakeibo;

//Kakeiboサービス処理
public interface KakeiboService {
	//全件取得
	Iterable<Kakeibo> selectAll();

	
	//IDをキーに1件取得
	Optional<Kakeibo> selectOneByID(Integer id);
	
	//家計簿を登録
	void insertKakeibo(Kakeibo kakeibo);
	
	//家計簿を編集
	void updateKakeibo(Kakeibo kakeibo);
	
	//家計簿を削除
	void deleteOneByID(Integer id);
}
