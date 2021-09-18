package com.example.demo.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.CategoryList;
import com.example.demo.model.DeleteProductForm;
import com.example.demo.model.EntryForm;
import com.example.demo.model.ExitForm;
import com.example.demo.model.GroupOrder;
import com.example.demo.model.InsertProductForm;
import com.example.demo.model.LoadingAndUnloadingHistory;
import com.example.demo.model.ProductList;
import com.example.demo.model.UserList;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;

@Controller
class ProductController {

	@Autowired
	ProductService productService;
	@Autowired
	UserService userService;

	//ログアウト用メソッド
	@PostMapping("/logout")
	public String postLogout() {

		//ログイン画面にリダイレクト
		return "redirect:/login";
	}

	//商品一覧画面のGET用メソッド
	@GetMapping("/productList")
	public String getProductList(Model model) {
		model.addAttribute("contents", "task/productList::productList_contents");

		//1年分だけ履歴の入出庫履歴データを保存
		productService.deleteMonth();

		//データの全件取得を実行
		List<ProductList> many = productService.selectProductListMany();
		model.addAttribute("ma", many);

		//データの件数を取得
		int count = productService.count();
		model.addAttribute("productListCount", count);

		//商品一覧リストにログインユーザーの名前を表示
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		UserList ul = userService.loginU(name);
		model.addAttribute("lu", ul.getUser_name());

		//商品一覧画面にリダイレクト
		return "task/taskLayout";
	}

	//商品登録画面のGET用メソッド
	@GetMapping("/insertProduct")
	public String getProductInsert(@ModelAttribute InsertProductForm form, Model model) {
		model.addAttribute("contents", "task/insertProduct::insertProduct_contents");

		//カテゴリー名を取得
		List<CategoryList> category = productService.selectCategory();
		model.addAttribute("selectItems", category);

		//登録画面に遷移
		return "task/taskLayout";
	}

	//商品登録画面のPOST用メソッド
	@PostMapping("/insertProduct")
	public String postProductInsert(@ModelAttribute @Validated(GroupOrder.class) InsertProductForm form,
			BindingResult bindingResult,
			Model model) {

		//入力チェックに引っかかった場合、商品登録画面に戻る
		if (bindingResult.hasErrors()) {
			//GET用のリクエストのメソッドを呼び出して、商品登録画面に戻る

			return getProductInsert(form, model);
		}
		//登録確認画面に遷移
		return getProductCheck(form, model);
	}

	//商品登録確認画面のGET用メソッド
	@GetMapping("/insertProductCheck")
	public String getProductCheck(@ModelAttribute InsertProductForm form, Model model) {
		model.addAttribute("contents", "task/insertProductCheck::insertProductCheck_contents");

		//カテゴリー名を取得
		List<CategoryList> category = productService.selectCategory();
		model.addAttribute("selectItems", category);

		//登録確認画面に遷移
		return "task/taskLayout";
	}

	//商品登録確認画面のPOST用メソッド
	@PostMapping("/insertProductCheck")
	public String postProductCheck(@ModelAttribute InsertProductForm form, Model model) {
		model.addAttribute("contents", "task/insertProductCheck::insertProductCheck_contents");

		//ProductListインスタンス生成
		ProductList product = new ProductList();

		//formクラスをProductListに変換
		product.setProduct_name(form.getProductName());//商品名
		product.setCategory_id(form.getCategoryId());//分類名
		product.setCompany_name(form.getCompanyName());//会社名
		product.setCount(form.getCount());//数量

		try {
			//ユーザー登録処理
			boolean result = productService.insert(product);
			if (result == true) {
				model.addAttribute("result", "登録成功");
			} else {
				model.addAttribute("result", "登録失敗");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
			model.addAttribute("result", "登録失敗(トランザクション)");
		}
		//商品一覧画面に遷移
		return getProductList(model);
	}

	//商品入庫画面のGET用メソッド
	@GetMapping("/productEntry/{id}")
	public String getProductEntry(@ModelAttribute EntryForm form, Model model, @PathVariable("id") int id) {
		model.addAttribute("contents", "task/productEntry::productEntry_contents");

		//担当者名を取得
		List<UserList> person = userService.selectList();
		model.addAttribute("selectItems0", person);

		//商品一覧から一つの行を選択
		ProductList pdl = productService.selectOne(form.getId());

		form.setId(pdl.getId());//ID
		form.setProductName(pdl.getProduct_name());//商品名
		form.setCategoryId(pdl.getCategory_id());//カテゴリー名
		form.setCompanyName(pdl.getCompany_name());//会社名
		form.setCount(pdl.getCount());//数量

		//入庫画面に遷移
		return "task/taskLayout";
	}

	//商品入庫画面のPOST用メソッド
	@PostMapping("/productEntry")
	public String postProductEntry(@ModelAttribute @Validated(GroupOrder.class) EntryForm form,
			BindingResult bindingResult,
			Model model) {

		//入庫チェックに引っかかった場合、入庫画面に戻る
		if (bindingResult.hasErrors()) {
			return getProductEntry(form, model, form.getId());
		}

		//入庫用変数
		ProductList pro = new ProductList();

		//フォームクラスをProductListに変換する
		pro.setId(form.getId());//ID
		pro.setProduct_name(form.getProductName());//商品名
		pro.setCategory_id(form.getCategoryId());//カテゴリーID
		pro.setCompany_name(form.getCompanyName());//会社名
		pro.setCount(form.getCountPrus());//入庫数量

		//登録用変数
		LoadingAndUnloadingHistory la = new LoadingAndUnloadingHistory();

		//フォームクラスをLoadingAndUnloadingHistoryに変換する
		la.setId(form.getId());//ID
		la.setDate_time(LocalDateTime.now());//日付
		la.setProduct_name_id(form.getProductNameId());//商品ID
		la.setCompany_name_id(form.getCompanyNameId());//会社名ID
		la.setPerson_id(form.getPersonId());//担当者ID
		la.setCount_before(form.getCount());//在庫数量を入出庫履歴の画面に遷移

		//符号付の文字列に変換
		Integer l = Integer.valueOf(form.getCountPrus());
		la.setCount_l("+" + l.toString());//入庫した数量

		try {
			boolean result = productService.insert(pro, la);
			if (result == true) {
				model.addAttribute("result", "入庫完了");
			} else {
				model.addAttribute("result", "入庫未完了");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
			model.addAttribute("result", "入庫未完了(トランザクション)");

		}
		//商品一覧画面に遷移
		return getProductList(model);
	}

	//商品出庫画面のGET用メソッド
	@GetMapping("/productExit/{id}")
	public String getProductExit(@ModelAttribute ExitForm form, Model model, @PathVariable("id") int id) {
		model.addAttribute("contents", "task/productExit::productExit_contents");

		//担当者名を取得
		List<UserList> per = userService.selectList();
		model.addAttribute("selectItems0", per);

		//商品一覧から一つの行を選択
		ProductList pdl = productService.selectOne(form.getId());

		form.setId(pdl.getId());//ID
		form.setProductName(pdl.getProduct_name());//商品名
		form.setCategoryId(pdl.getCategory_id());//カテゴリー名
		form.setCompanyName(pdl.getCompany_name());//会社名
		form.setCount(pdl.getCount());//現在数量

		//出庫画面に遷移
		return "task/taskLayout";
	}

	//商品出庫画面のPOST用メソッド
	@PostMapping("/productExit")
	public String postProductExit(@ModelAttribute @Validated(GroupOrder.class) ExitForm form,
			BindingResult bindingResult,
			Model model) {

		//入力チェックに引っかかった場合、出庫画面に戻る
		if (bindingResult.hasErrors()) {
			return getProductExit(form, model, form.getId());
		}
		//入力用変数
		ProductList pro = new ProductList();

		//フォームクラスをProductListに変換する
		pro.setId(form.getId());//ID
		pro.setCategory_id(form.getCategoryId());//カテゴリーID
		pro.setProduct_name(form.getProductName());//商品名
		pro.setCompany_name(form.getCompanyName());//会社名
		pro.setCount(form.getCountMinus());//数量

		//登録用変数
		LoadingAndUnloadingHistory la = new LoadingAndUnloadingHistory();

		//フォームクラスをLoadingAndUnloadingHistoryに変換する
		la.setId(form.getId());//ID
		la.setDate_time(LocalDateTime.now());//日付
		la.setProduct_name_id(form.getProductNameId());//商品ID
		la.setCompany_name_id(form.getCompanyNameId());//会社名ID
		la.setPerson_id(form.getPersonId());//担当者ID
		la.setCount_before(form.getCount());//在庫数量を入出庫履歴の画面に遷移

		System.out.println(form.getPersonId());
		//符号付きの文字列に変換
		Integer l = Integer.valueOf(form.getCountMinus());
		la.setCount_l("-" + l.toString());//入庫した数量

		try {
			boolean result = productService.updateOne(pro, la);
			if (result == true) {
				model.addAttribute("result", "出庫完了");
			} else {
				model.addAttribute("result", "出庫未完了");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
			model.addAttribute("result", "出庫未完了(トランザクション)");
		}
		//商品一覧画面に遷移
		return getProductList(model);
	}

	//商品入出庫履歴画面のGET用メソッド
	@GetMapping("/loadingAndUnloading")
	public String getTaskHistory(Model model) {
		model.addAttribute("contents", "task/loadingAndUnloading::loadingAndUnloading_contents");

		//データの今月分取得を実行
		List<LoadingAndUnloadingHistory> many = productService.selectThisMonth();
		model.addAttribute("ma1", many);

		//入出庫履歴画面に遷移
		return "task/taskLayout";
	}

	//商品入出庫履歴の検索結果画面のGET用メソッド
	@GetMapping("/lauSearch")
	public String getLauSearch(@RequestParam("number") String x, Model model) {
		model.addAttribute("contents", "task/lauSearch::lauSearch_contents");

		//(例)2021-08-08を21,08,08の文字列として切り取る
		String y = x.substring(2, 4);
		String m = x.substring(5, 7);
		String d = x.substring(8, 10);

		//上記の文字列を数値に変える
		int ye = Integer.parseInt(y);
		int mo = Integer.parseInt(m);
		int da = Integer.parseInt(d);

		//データの検索取得を実行
		List<LoadingAndUnloadingHistory> search = productService.selectTaskHistorySearch(ye, mo, da);
		model.addAttribute("se", search);
		return "task/taskLayout";
	}

	//商品削除画面のGET用メソッド
	@GetMapping("/deleteProduct/{id}")
	public String getDeleteProduct(@ModelAttribute DeleteProductForm form, Model model, @PathVariable("id") int id) {
		model.addAttribute("contents", "task/deleteProduct::deleteProduct_contents");

		//全商品一覧から一つの商品を取得
		ProductList prod = productService.selectOne(form.getId());

		form.setId(prod.getId());//ID
		form.setProductName(prod.getProduct_name());//商品名
		form.setCompanyName(prod.getCompany_name());//会社名
		form.setCount(prod.getCount());//数量
		form.setCategoryName(prod.getCategory_name());//分類名

		//商品削除画面に遷移
		return "task/taskLayout";
	}

	//商品削除画面のPOST用メソッド
	@PostMapping("/deleteProduct")
	public String postDeleteProduct(@ModelAttribute DeleteProductForm form, Model model) {

		try {
			boolean result = productService.deleteOne(form.getId());
			if (result == true) {
				model.addAttribute("result", "削除成功");
			} else {
				model.addAttribute("result", "削除失敗");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
			model.addAttribute("result", "削除失敗(トランザクション)");
		}
		return getProductList(model);
	}

	//商品一覧画面の出力用メソッド
	@GetMapping("/productList/csv")
	public ResponseEntity<byte[]> getProductListCSV(Model model) {

		//商品一覧リストを全件取得して、CSVをサーバーに保存する
		productService.proCsvOut();

		byte[] bytes = null;
		try {
			//サーバーに保存されているproduct_listファイルをbyteで取得する
			bytes = productService.getFile("product_list.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//HTTPヘッダーの設定
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "text/csv;charset=UTF-8");
		header.setContentDispositionFormData("filename", "product_list.csv");
		return new ResponseEntity<>(bytes, header, HttpStatus.OK);
	}

}