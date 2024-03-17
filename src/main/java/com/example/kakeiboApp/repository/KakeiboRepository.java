package com.example.kakeiboApp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.kakeiboApp.entity.Kakeibo;

//Kakeiboテーブル:RepositoryImpl
public interface KakeiboRepository extends CrudRepository<Kakeibo, Integer> {
	
	//startdateからendDateまでを指定し、取得（ASC）
	public Iterable<Kakeibo> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);
	
	//
	@Query("SELECT DISTINCT person FROM Kakeibo")
	public List<String> findDistinctPeople();
	
}