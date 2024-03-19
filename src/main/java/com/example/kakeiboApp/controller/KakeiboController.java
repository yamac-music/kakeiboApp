package com.example.kakeiboApp.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.example.kakeiboApp.form.KakeiboForm;
import com.example.kakeiboApp.repository.KakeiboRepository;
import com.example.kakeiboApp.service.KakeiboService;

@Controller
@RequestMapping("/")
public class KakeiboController {
	//DI対象
	@Autowired
	KakeiboService service;
	
	@Autowired
	KakeiboRepository repository;
	
	//form-backing beanの初期化
	@ModelAttribute
	public KakeiboForm setupForm() {
		KakeiboForm form = new KakeiboForm();
		return form;
	}
	
	//家計簿一覧表示
	@GetMapping
	public String showList(Model model,@RequestParam(required = false) Integer year, 
			@RequestParam(required = false) Integer month){
		
		//現在の年月を設定
        LocalDate targetDate = getTargetDate(model, year, month);
        int targetYear = targetDate.getYear();
        int targetMonth = targetDate.getMonthValue();
        
        //Iterable<Kakeibo> listに指定年月のデータを格納
        Iterable<Kakeibo> list = service.getKakeiboByYearMonth(targetYear, targetMonth);
        
        //modelにListと年月の情報を格納
		model.addAttribute("list", list);       
        model.addAttribute("year", targetYear);
        model.addAttribute("month", targetMonth);
        
        //指定した月の合計金額 targetDateTotalPriceを計算
        int targetDateTotalPrice = 0;
        for (Kakeibo kakeibo : list) {
			targetDateTotalPrice += kakeibo.getPrice();
		}
        //System.out.println("total："+targetDateTotalPrice);
		model.addAttribute("targetDateTotalPrice", targetDateTotalPrice);
		
		//それぞれの金額を集計
		//Mapを作成
		Map<String, Integer> personTotalMap = new HashMap<>();
		
		//listをループ処理する
		for (Kakeibo kakeibo : list) {
			String personString = kakeibo.getPerson();
			Integer priceInteger = kakeibo.getPrice();
			
			// personがMapに存在しない場合
		    if (!personTotalMap.containsKey(personString)) {
		    	personTotalMap.put(personString, priceInteger);
		    }else {
		    // personがMapに存在する場合
		    Integer totalAmount = personTotalMap.get(personString);
		    personTotalMap.put(personString, totalAmount + priceInteger);
		    }
		}
		
		//MapをModelに格納
		model.addAttribute("personTotalMap",personTotalMap);
	    
		//Modelに格納
		model.addAttribute("title", "家計簿　登録用フォーム");
		
		//支払額を計算した結果のmapをModelに格納
		Map<String, Integer> differencesMap = calculateExpenseDifference(personTotalMap, targetDateTotalPrice);
		model.addAttribute("differencesMap", differencesMap);
		
		//人の一覧を取得する
		List<String> people = repository.findDistinctPeople();
		model.addAttribute("people", people);
		return "home";
	}
	
    private LocalDate getTargetDate(Model model, Integer year, Integer month) {
    	//year、monthがnull、monthが1~12月出ない場合、当月を返す
        if (year != null && month != null && month >= 1 && month <= 12) {
            return LocalDate.of(year, month, 1);
        }
        //そうでないなら、modelから取ってきた値を返す
        return getTargetDateFromModel(model);
    }
    
    private LocalDate getTargetDateFromModel(Model model) {
        int year = getIntParameter(model, "year", LocalDate.now().getYear());
        int month = getIntParameter(model, "month", LocalDate.now().getMonthValue());
        if (month < 1 || month > 12) {
            return LocalDate.now();
        }
        return LocalDate.of(year, month, 1);
    }

    private int getIntParameter(Model model, String paramName, int defaultValue) {
        String paramValue = model.asMap().containsKey(paramName) ? model.asMap().get(paramName).toString() : null;
        return paramValue != null ? Integer.parseInt(paramValue) : defaultValue;
    }
	
    
    public static Map<String, Integer> calculateExpenseDifference(Map<String, Integer> payments, int totalAmount) {
        Map<String, Integer> differences = new HashMap<>();
        int numPeople = payments.size();
        int averagePayment = totalAmount / numPeople;

        // 各人の支払い額と平均支払い額の差を計算
        for (Map.Entry<String, Integer> entry : payments.entrySet()) {
            String person = entry.getKey();
            int payment = entry.getValue();
            differences.put(person, payment - averagePayment);
        }

        return differences;
    }
    
    
	//以下、データを登録する処理を実装
	@PostMapping("/insert")
	public String insert(
			@RequestParam(required = false) Integer year, 
			@RequestParam(required = false) Integer month, 
			@Validated @ModelAttribute KakeiboForm kakeiboForm, 
			BindingResult bindingResult, 
			Model model,
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
			return showList(model, year, month);
		}else {
			service.insertKakeibo(kakeibo);
			redirectAttributes.addFlashAttribute("complete", "入力が完了しました");
			return "redirect:/";
		}
	}
	
	//key : id でデータ削除
	@PostMapping("/delete")
	public String delete(@RequestParam("id") String id, Model model, RedirectAttributes redirectAttributes) {
		//1件削除
		service.deleteOneByID(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("delcomplete", "削除が完了しました");
		return "redirect:/";
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
			return "redirect:/";
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
	
	@RequestMapping("/analyse")
	public String analyse() {
		return "analyse";
	}
	
}


