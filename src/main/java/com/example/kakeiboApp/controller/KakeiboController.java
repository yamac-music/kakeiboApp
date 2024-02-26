package com.example.kakeiboApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kakeiboApp.entity.Kakeibo;
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
		
		//Modelに格納
		model.addAttribute("list", list);
		model.addAttribute("title", "家計簿　登録用フォーム");
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
		System.out.println(kakeiboForm.getDate());
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
}