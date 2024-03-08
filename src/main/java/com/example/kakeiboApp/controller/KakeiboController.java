package com.example.kakeiboApp.controller;

import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	//データ1件取得して、フォームに表示
	@GetMapping("/{id}")
	public String edit(@PathVariable Integer id, Model model, KakeiboForm kakeiboForm) {
		
		//指定IDの家計簿を取得
		Optional<Kakeibo> kakeiboOptional = service.selectOneByID(id);
		
		//kakeiboFormにkakeiboの中身を入れる
		Optional<KakeiboForm> kakeiboFormOptional = kakeiboOptional.map(t -> makeKakeiboForm(t));
		//もしKakeiboFormが存在するなら中身を取り出す
		if (kakeiboFormOptional.isPresent()) {
			kakeiboForm = kakeiboFormOptional.get();
		}
		//更新用Model作成
		makeUpdateModel(kakeiboForm, model);
		return "edit";
	}
	//更新用Model作成
	private void makeUpdateModel(KakeiboForm kakeiboForm, Model model) {
		model.addAttribute("id", kakeiboForm.getId());
		kakeiboForm.setNewKakeibo(false);
		model.addAttribute("kakeiboForm", kakeiboForm);
		model.addAttribute("title", "家計簿　編集用フォーム");
	}
	
	//idをキーとしてデータ更新
	@PostMapping("/update")
	public String update(@Validated KakeiboForm kakeiboForm, BindingResult bindingResult, 
						Model model, RedirectAttributes redirectAttributes) {
		
		//kakeiboにkakeiboFormの中身を入れる
		Kakeibo kakeibo = makeKakeibo(kakeiboForm);
		
		//入力チェック
		if (bindingResult.hasErrors()) {
			makeUpdateModel(kakeiboForm, model);
			return "edit";
		}else {
			service.updateKakeibo(kakeibo);
			redirectAttributes.addFlashAttribute("complete", "編集が完了しました");
			return "redirect:/home";
		}
	}

	
	//kakeiboFormをkakeiboに入れて返す
	private Kakeibo makeKakeibo(KakeiboForm kakeiboForm) {
		Kakeibo kakeibo = new Kakeibo();
		kakeibo.setId(kakeiboForm.getId());
		kakeibo.setDate(kakeiboForm.getDate());
		kakeibo.setPlace(kakeiboForm.getPlace());
		kakeibo.setCategory(kakeiboForm.getCategory());
		kakeibo.setPerson(kakeiboForm.getPerson());
		kakeibo.setPrice(kakeiboForm.getPrice());
		
		return kakeibo;
	}
	
	//kakeiboをkakeiboFormに入れて返す
	private KakeiboForm makeKakeiboForm(Kakeibo kakeibo) {
		KakeiboForm kakeiboForm = new KakeiboForm();
		
		kakeiboForm.setId(kakeibo.getId());
		kakeiboForm.setDate(kakeibo.getDate());
		kakeiboForm.setPlace(kakeibo.getPlace());
		kakeiboForm.setCategory(kakeibo.getCategory());
		kakeiboForm.setPerson(kakeibo.getPerson());
		kakeiboForm.setPrice(kakeibo.getPrice());
		kakeiboForm.setNewKakeibo(false);
		
		return kakeiboForm;
	}

}
