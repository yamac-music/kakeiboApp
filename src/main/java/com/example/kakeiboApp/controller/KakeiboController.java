package com.example.kakeiboApp.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
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
import com.example.kakeiboApp.entity.PriceTotal;
import com.example.kakeiboApp.form.KakeiboForm;
import com.example.kakeiboApp.service.KakeiboService;

@Controller
@RequestMapping("/")
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
	public String showList(Model model,@RequestParam(required = false) Integer year, 
			@RequestParam(required = false) Integer month){

		//priceList取得
		Iterable<PriceTotal> priceList = service.calcPersonTotal();
		
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
        //テスト表示（年月指定のフォームの値を表示）
        //System.out.println("指定年："+targetYear);
        //System.out.println("指定月："+targetMonth);
        
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
		
	    // Mapの内容をテストで出力
	    //for (Map.Entry<String, Integer> entry : personTotalMap.entrySet()) {
	    //  String person = entry.getKey();
	    //  int totalAmount = entry.getValue();
	    //  System.out.println(person + "の合計金額：" + totalAmount);
	    //}
	    //System.out.println("-------");
	    
		//Modelに格納
		model.addAttribute("priceList", priceList);
		model.addAttribute("title", "家計簿　登録用フォーム");
		
		//支払額を計算した結果のmapをModelに格納
		Map<String, Integer> differencesMap = calculateExpenseDifference(personTotalMap, targetDateTotalPrice);
		model.addAttribute("differencesMap", differencesMap);
	    System.out.println("-------");
		System.out.println("personTotalMap: "+personTotalMap);
		System.out.println("Total"+targetDateTotalPrice);
		System.out.println(differencesMap);
		
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
        // 差額を格納するMapを初期化
        Map<String, Integer> differences = new HashMap<>();

        // payments Mapのサイズ(人数)を取得
        int numPeople = payments.size();

        // 人数が2人でない場合は例外をスロー
        if (numPeople != 2) {
            throw new IllegalArgumentException("This method only works for two people.");
        }

        // payments Mapのエントリーセットをイテレータに変換
        Iterator<Map.Entry<String, Integer>> iterator = payments.entrySet().iterator();

        // 最初のエントリー(人とその支払い額)を取得
        Map.Entry<String, Integer> entry1 = iterator.next();
        // 2番目のエントリー(人とその支払い額)を取得
        Map.Entry<String, Integer> entry2 = iterator.next();

        // 最初の人の名前を取得
        String person1 = entry1.getKey();
        // 最初の人の支払い額を取得
        int payment1 = entry1.getValue();

        // 2番目の人の名前を取得
        String person2 = entry2.getKey();
        // 2番目の人の支払い額を取得
        int payment2 = entry2.getValue();

        // 2人の支払い額の差を計算
        int totalDifference = payment1 - payment2;

        // 差額を2で割り、片方には正の値、もう片方には負の値を割り当てる
        differences.put(person1, totalDifference / 2);
        differences.put(person2, -totalDifference / 2);

        // 調整後の差額を返す
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


