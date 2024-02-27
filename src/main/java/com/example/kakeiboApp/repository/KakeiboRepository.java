package com.example.kakeiboApp.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.kakeiboApp.entity.Kakeibo;

//Kakeiboテーブル:RepositoryImpl
public interface KakeiboRepository extends CrudRepository<Kakeibo, Integer> {

}