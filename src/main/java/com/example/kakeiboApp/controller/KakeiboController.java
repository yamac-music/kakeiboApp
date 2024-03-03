package com.example.kakeiboApp.controller;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kakeiboApp.entity.Kakeibo;
import com.example.kakeiboApp.entity.PriceTotal;
import com.example.kakeiboApp.form.KakeiboForm;
import com.example.kakeiboApp.service.KakeiboService;

@Controller
@RequestMapping("/home")
public class KakeiboController {
	//DI対象
	@Autowired
	KakeiboService service;
	
	//form-backing beanの初期化
	@ModelAttribute
	public KakeiboForm setupForm() {
		KakeiboForm form = new KakeiboForm();
		return form;
	}
	
	//家計簿一覧表示
	@GetMapping
	public String showList(KakeiboForm kakeiboForm, Model model) {
		//新規登録
		kakeiboForm.setNewKakeibo(true);
		
		//一覧取得
		Iterable<Kakeibo> list = service.selectAll();
		Integer currentMonthTotal = service.calcTotalPriceCurrentMonth();
		
		//priceList取得
		Iterable<PriceTotal> priceList = service.calcPersonTotal();
		
		//折半の場合，誰が誰にどれだけお金を渡すか計算
		
		
		//Modelに格納
		model.addAttribute("list", list);
		model.addAttribute("title", "家計簿　登録用フォーム");
		model.addAttribute("currentMonthTotal", currentMonthTotal);
		model.addAttribute("priceList", priceList);

		return "home";
	}
	
	@PostMapping("/insert")
	public String insert(@Validated @ModelAttribute KakeiboForm kakeiboForm, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		
		Kakeibo kakeibo = new Kakeibo();
		
		kakeibo.setDate(kakeiboForm.getDate());
		kakeibo.setPlace(kakeiboForm.getPlace());
		kakeibo.setCategory(kakeiboForm.getCategory());
		kakeibo.setPerson(kakeiboForm.getPerson());
		kakeibo.setPrice(kakeiboForm.getPrice());
		
		
		System.out.println("----------------");
		System.out.println(kakeiboForm.getDate() instanceof Date);
		System.out.println(kakeiboForm.getPlace());
		System.out.println(kakeiboForm.getCategory());
		System.out.println(kakeiboForm.getPerson());
		System.out.println(kakeiboForm.getPrice());
		System.out.println("----------------");

		if (bindingResult.hasErrors()) {
			return showList(kakeiboForm, model);
		}else {
			service.insertKakeibo(kakeibo);
			redirectAttributes.addFlashAttribute("complete", "入力が完了しました");
			return "redirect:/home";
		}
	}
	
	//key : id でデータ削除
	@PostMapping("/delete")
	public String delete(@RequestParam("id") String id, Model model, RedirectAttributes redirectAttributes) {
		//1件削除
		service.deleteOneByID(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("delcomplete", "削除が完了しました");
		return "redirect:/home";
	}
	
}
