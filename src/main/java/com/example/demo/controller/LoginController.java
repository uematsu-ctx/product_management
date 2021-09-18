package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.service.UserService;

@Controller
public class LoginController {
	@Autowired
	UserService use;
	//パスワードを暗号化して、データベースに登録。
	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	//ログイン画面GET用メソッド
	@GetMapping("/login")
	public String getLogin(Model model) {

		//ログイン画面に遷移
		return "task/login";
	}

	//ログイン画面POST用メソッド
	@PostMapping("/login")
	public String postLogin(Model model) {

		//商品一覧画面にリダイレクト
		return "redirect:/productList";
	}

}
