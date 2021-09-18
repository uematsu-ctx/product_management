package com.example.demo.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.GroupOrder;
import com.example.demo.model.PasswordUpForm;
import com.example.demo.model.SignUpForm;
import com.example.demo.model.UserList;
import com.example.demo.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService userService;
	@Autowired
	LoginController login;
	@Autowired
	ProductController pro;
	//パスワードを暗号化して、データベースに登録。
	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	//ラジオボタンの実装
	private Map<String, String> radioRole;

	//ラジオボタンの初期化メソッド
	private Map<String, String> initRadioRole() {
		Map<String, String> radio = new LinkedHashMap<>();

		radio.put("管理者", "ROLE_K");
		radio.put("正社員", "ROLE_S");
		radio.put("パート従業員", "ROLE_P");

		return radio;
	}

	//ユーザー登録画面のメソッド
	@GetMapping("/signUp")
	public String getSignUp(@ModelAttribute SignUpForm form, Model model) {
		model.addAttribute("contents", "task/signUp::signUp_contents");
		//ラジオボタンの初期化メソッド呼び出し
		radioRole = initRadioRole();

		//ラジオボタン用のMapをModelに登録
		model.addAttribute("radioRole", radioRole);

		//ユーザー登録画面に遷移
		return "task/taskLayout";
	}

	//ユーザー登録画面POST用のメソッド
	@PostMapping("/signUp")
	public String postSignUp(@ModelAttribute @Validated(GroupOrder.class) SignUpForm form, BindingResult bindingResult,
			Model model) {

		//入力チェックに引っかかった場合、ユーザー登録画面に戻る
		if (bindingResult.hasErrors()) {
			//GET用のリクエストのメソッドを呼び出して、ユーザー登録画面に戻る

			return getSignUp(form, model);
		}
		//ユーザーIDの重複の確認
		boolean result = userService.isDuplicated(form.getUserId());

		//ユーザーIDの重複結果の判定
		if (result == true) {
			//バリデーションエラーメッセージを追加
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "userId", "同一ユーザーが存在しています");
			bindingResult.addError(fieldError);

			//GET用のリクエストのメソッドを呼び出して、ユーザー登録画面に戻る
			return getSignUp(form, model);
		}
		//登録用変数
		UserList user = new UserList();

		//formクラスをUserListクラスに変換
		user.setUser_id(form.getUserId());//ユーザーID
		user.setUser_name(form.getUserName());//ユーザー名
		user.setPassword(form.getPassword());//パスワード
		user.setBirthday(form.getBirthday());//誕生日
		user.setRole(form.getRole());//雇用形態

		//パスワードを暗号化してuser_listテーブルに登録する。
		String digest = passwordEncoder.encode(form.getPassword());
		user.setPassword(digest);

		//ユーザー確認画面に遷移
		return getSignUpCheck(form, model);
	}

	//ユーザー登録確認画面のGET用メソッド
	@GetMapping("signUpCheck")
	public String getSignUpCheck(@ModelAttribute SignUpForm form, Model model) {
		model.addAttribute("contents", "task/signUpCheck::signUpCheck_contents");

		//ラジオボタンの初期化メソッドの呼び出し
		radioRole = initRadioRole();

		//ラジオボタン用のMapをModelに登録
		model.addAttribute("radioRole", radioRole);

		//ユーザー登録確認画面に遷移
		return "task/taskLayout";
	}

	//ユーザー登録確認画面のPOST用メソッド
	@PostMapping("/signUpCheck")
	public String postSignUpCheck(@ModelAttribute SignUpForm form, Model model) {

		//UserListインスタンス生成
		UserList user = new UserList();

		//formクラスをUserListクラスに変換
		user.setUser_id(form.getUserId());//ユーザーID
		user.setUser_name(form.getUserName());//ユーザー名
		user.setPassword(form.getPassword());//パスワード
		user.setBirthday(form.getBirthday());//誕生日
		user.setRole(form.getRole());//雇用形態

		String digest = passwordEncoder.encode(form.getPassword());

		user.setPassword(digest);

		try {
			//ユーザー登録処理
			boolean result1 = userService.insert(user);
			if (result1 == true) {
				model.addAttribute("result", "登録成功");
			} else {
				model.addAttribute("result", "登録失敗");
			}
		} catch (DataAccessException e) {
			model.addAttribute("result", "登録失敗(トランザクション)");
		}
		//ユーザー一覧画面に遷移
		return getUserList(model);
	}

	//ログインユーザーのパスワード更新画面のGET用メソッド
	@GetMapping("/passwordUpdate")
	public String getPasswordUpdate(@ModelAttribute PasswordUpForm form, Model model) {
		model.addAttribute("contents", "task/passwordUpdate::passwordUpdate_contents");

		//ログインユーザーIDを取得
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		model.addAttribute("uid", name);
		form.setUserId(name);

		//ログインユーザーのパスワード更新画面に遷移
		return "task/taskLayout";
	}

	//ログインユーザーのパスワード更新画面のPOST用メソッド
	@PostMapping("/passwordUpdate")
	public String postPasswordUpdate(@ModelAttribute @Validated(GroupOrder.class) PasswordUpForm form,
			BindingResult bindingResult,
			Model model) {

		//入力チェックに引っかかった場合、パスワード更新画面に戻る
		if (bindingResult.hasErrors()) {
			//GET用のリクエストのメソッドを呼び出して、パスワード更新画面に戻る
			return getPasswordUpdate(form, model);
		}

		//update用変数
		UserList ul = new UserList();
		ul.setUser_id(form.getUserId());

		//パスワードを暗号化してuser_listテーブルを更新する
		String digest = passwordEncoder.encode(form.getPassword());
		ul.setPassword(digest);

		try {
			//ログインユーザー自身の更新
			boolean result = userService.updatePassword(ul);
			if (result == true) {
				model.addAttribute("result", "パスワード更新成功");
			} else {
				model.addAttribute("result", "パスワード更新失敗");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
			model.addAttribute("result", "更新失敗(トランザクション)");
		}
		//ユーザー一覧画面に遷移
		return getUserList(model);
	}

	//ユーザー一覧画面のGET用メソッド(管理者、正社員専用)
	@GetMapping("/userList")
	public String getUserList(Model model) {
		model.addAttribute("contents", "task/userList::userList_contents");

		//ユーザー一覧の生成
		List<UserList> userList = userService.selectMany();

		//ModelにuserListを登録
		model.addAttribute("userList", userList);

		//データの件数を取得
		int count = userService.count();
		model.addAttribute("userListCount", count);

		//ユーザー一覧画面に遷移
		return "task/taskLayout";
	}

	//社員の更新・削除するGET用メソッド
	@GetMapping("/userUpdateDelete/{id}")
	public String getUserUpdateDelete(@ModelAttribute SignUpForm form, Model model, @PathVariable("id") String userId) {
		model.addAttribute("contents", "task/userUpdateDelete::userUpdateDelete_contents");
		//ラジオボタンの初期化メソッドの呼び出し
		radioRole = initRadioRole();

		//ラジオボタン用のMapをModelに登録
		model.addAttribute("radioRole", radioRole);

		//ユーザー情報を取得
		UserList user = userService.selectOne(form.getId());

		//UserListクラスをformクラスに変換

		form.setUserId(user.getUser_id());//ユーザーID
		form.setUserName(user.getUser_name());//ユーザー名
		form.setPassword(user.getPassword());//パスワード
		form.setBirthday(user.getBirthday());//誕生日
		form.setRole(user.getRole());//雇用形態

		//更新・削除画面に遷移
		return "task/taskLayout";
	}

	//ユーザー更新POST用のメソッド
	@PostMapping(value = "/userUpdateDelete", params = "up")
	public String postUpdate(@ModelAttribute SignUpForm form, Model model) {

		//UserListインスタンス生成
		UserList user = new UserList();

		//formクラスをUserListクラスに変換
		user.setUser_id(form.getUserId());//ユーザーID
		user.setUser_name(form.getUserName());//ユーザー名
		user.setPassword(form.getPassword());//パスワード
		user.setBirthday(form.getBirthday());//誕生日
		user.setRole(form.getRole());//雇用形態

		//更新確認画面に遷移
		return getUpdateCheck(form, model);
	}

	//ユーザー更新確認画面のGET用メソッド
	@GetMapping("/updateUserCheck")
	public String getUpdateCheck(@ModelAttribute SignUpForm form, Model model) {
		model.addAttribute("contents", "task/updateUserCheck::updateUserCheck_contents");

		//ラジオボタンの初期化メソッドの呼び出し
		radioRole = initRadioRole();

		//ラジオボタン用のMapをModelに登録
		model.addAttribute("radioRole", radioRole);

		//更新確認画面に遷移
		return "task/taskLayout";
	}

	//ユーザー更新確認画面のPOST用メソッド
	@PostMapping("/updateUserCheck")
	public String postUpdateCheck(@ModelAttribute SignUpForm form, Model model) {

		//UserListインスタンス生成
		UserList user = new UserList();

		//formクラスをUserListクラスに変換
		user.setUser_id(form.getUserId());//ユーザーID
		user.setUser_name(form.getUserName());//ユーザー名
		user.setPassword(form.getPassword());//パスワード
		user.setBirthday(form.getBirthday());//誕生日
		user.setRole(form.getRole());//雇用形態

		try {
			//ユーザーの更新
			boolean result = userService.updateOne(user);

			//更新の判定
			if (result == true) {
				model.addAttribute("result", "更新成功");
			} else {
				model.addAttribute("result", "更新失敗");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
			model.addAttribute("result", "更新失敗(トランザクション)");
		}

		//ユーザー一覧の画面に遷移
		return getUserList(model);
	}

	//ユーザー削除POST用メソッド
	@PostMapping(value = "/userUpdateDelete", params = "de")
	public String postDelete(@ModelAttribute SignUpForm form, Model model) {

		//UserListインスタンス生成
		UserList user = new UserList();

		//formクラスをUserListクラスに変換
		user.setUser_id(form.getUserId());//ユーザーID
		user.setUser_name(form.getUserName());//ユーザー名
		user.setPassword(form.getPassword());//パスワード
		user.setBirthday(form.getBirthday());//誕生日
		user.setRole(form.getRole());//雇用形態

		//削除確認画面に遷移
		return getDeleteCheck(form, model);
	}

	//ユーザー削除確認画面のGET用メソッド
	@GetMapping("/deleteUserCheck")
	public String getDeleteCheck(@ModelAttribute SignUpForm form, Model model) {
		model.addAttribute("contents", "task/deleteUserCheck::deleteUserCheck_contents");

		//ラジオボタンの初期化メソッドの呼び出し
		radioRole = initRadioRole();

		//ラジオボタン用のMapをModelに登録
		model.addAttribute("radioRole", radioRole);

		//削除確認画面に遷移
		return "task/taskLayout";
	}

	//ユーザー削除確認画面のPOST用メソッド
	@PostMapping("/deleteUserCheck")
	public String postDeleteCheck(@ModelAttribute SignUpForm form, Model model) {

		try {
			//ユーザーの削除
			boolean result = userService.deleteOne(form.getUserId());

			if (result == true) {
				model.addAttribute("result", "削除成功");
			} else {
				model.addAttribute("result", "削除失敗");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
			model.addAttribute("result", "削除失敗(トランザクション)");
		}
		//ユーザー一覧画面に遷移
		return getUserList(model);
	}

	//ユーザー一覧のCSV出力用メソッド
	@GetMapping("/userList/csv")
	public ResponseEntity<byte[]> getUserListCSV(Model model) {

		//ユーザーリストを全件取得して、CSVをサーバーに保存する
		userService.userCsvOut();

		byte[] bytes = null;
		try {
			//サーバーに保存されているuser_list.csvファイルをbyteで取得する
			bytes = userService.getFile("user_list.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//HTTPヘッダーの設定
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "text/csv;charset=UTF-8");
		header.setContentDispositionFormData("filename", "user_list.csv");

		//user_list.csvを戻す
		return new ResponseEntity<>(bytes, header, HttpStatus.OK);
	}
}
