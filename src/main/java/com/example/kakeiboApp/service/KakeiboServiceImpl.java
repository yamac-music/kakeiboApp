package com.example.kakeiboApp.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kakeiboApp.entity.Kakeibo;
import com.example.kakeiboApp.entity.PriceTotal;
import com.example.kakeiboApp.repository.KakeiboRepository;

@Service
@Transactional
public class KakeiboServiceImpl implements KakeiboService {
	@Autowired
	KakeiboRepository repository;

	@Override
	public Iterable<Kakeibo> getKakeiboByYearMonth(int year, int month) {
		// TODO 自動生成されたメソッド・スタブ
		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.plusMonths(1).minusDays(1);

		return repository.findByDateBetweenOrderByDateAsc(startDate, endDate);
	}
	
	@Override
	public Optional<Kakeibo> selectOneByID(Integer id) {
		// TODO 自動生成されたメソッド・スタブ
		return repository.findById(id);
	}

	@Override
	public void insertKakeibo(Kakeibo kakeibo) {
		// TODO 自動生成されたメソッド・スタブ
		repository.save(kakeibo);
	}

	@Override
	public void updateKakeibo(Kakeibo kakeibo) {
		// TODO 自動生成されたメソッド・スタブ
		repository.save(kakeibo);
	}

	@Override
	public void deleteOneByID(Integer id) {
		// TODO 自動生成されたメソッド・スタブ
		//一旦は物理削除を実装
		repository.deleteById(id);
	}

	@Override
	public Integer calcTotalPriceCurrentMonth() {
		// TODO 自動生成されたメソッド・スタブ
		return repository.calcTotalPriceCurrentMonth();

	}

	@Override
	public Iterable<PriceTotal> calcPersonTotal() {
		// TODO 自動生成されたメソッド・スタブ
		Iterable<PriceTotal>  priceList = repository.calcPersonTotalPriceCurrentMonth(); 
		return priceList;
	}


}
