package com.example.kakeiboApp.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.kakeiboApp.entity.Kakeibo;
import com.example.kakeiboApp.entity.PriceTotal;

//Kakeiboテーブル:RepositoryImpl
public interface KakeiboRepository extends CrudRepository<Kakeibo, Integer> {
	public Iterable<Kakeibo> findAllByOrderByDateDesc();
	
	//当月のリスト表示
	@Query("SELECT * FROM kakeibo "
			+ "WHERE date BETWEEN DATE_TRUNC('month',now()) AND "
			+ "DATE_TRUNC('month',now()) + '1 month' + '-1 day'")
	public Iterable<Kakeibo> findCurrentMonth();
	
	//当月の金額合計を表示
	@Query("SELECT sum(price) FROM kakeibo "
			+ "WHERE date BETWEEN DATE_TRUNC('month',now()) AND "
			+ "DATE_TRUNC('month',now()) + '1 month' + '-1 day'")
	public Integer calcTotalPriceCurrentMonth();

	//翔太郎と更のそれぞれの当月金額合計を表示
	@Query("SELECT person, sum(price) AS totalprice FROM kakeibo "
			+ "WHERE date BETWEEN DATE_TRUNC('month',now()) AND "
			+ "DATE_TRUNC('month',now()) + '1 month' + '-1 day'"
			+ "GROUP BY person;")
	public Iterable<PriceTotal> calcPersonTotalPriceCurrentMonth();


	//先月のリスト取得
	@Query("SELECT * FROM kakeibo "
			+ "WHERE date BETWEEN DATE_TRUNC('month',now()) + '-1 month' AND "
			+ "DATE_TRUNC('month',now()) + '-1 day'")
	public Iterable<Kakeibo> findLastMonth();
	
	
}