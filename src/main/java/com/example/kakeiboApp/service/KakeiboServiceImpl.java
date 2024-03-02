package com.example.kakeiboApp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kakeiboApp.entity.Kakeibo;
import com.example.kakeiboApp.repository.KakeiboRepository;

@Service
@Transactional
public class KakeiboServiceImpl implements KakeiboService {
	@Autowired
	KakeiboRepository repository;

	@Override
	public Iterable<Kakeibo> selectAll() {
		// TODO 自動生成されたメソッド・スタブ
		return repository.findCurrentMonth();
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
	public List<Integer> calcPersonTotal() {
		// TODO 自動生成されたメソッド・スタブ
		List<Integer>  priceList = repository.calcPersonTotalPriceCurrentMonth(); 
		int larger = Math.max(priceList.get(0), priceList.get(1));
		int smaller = Math.min(priceList.get(0), priceList.get(1));
		int diff = larger - smaller; 
		
		priceList.add(diff);
		
		return priceList;
	}

}
