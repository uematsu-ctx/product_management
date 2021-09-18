package com.example.demo.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateUser {

	public static void main(String[] args) throws IOException {
		//パスワードを暗号化して、データベースに登録。
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("パスワードを入力してください");
		String password = br.readLine();
		String pass = passwordEncoder.encode(password);
		System.out.println(pass);
	}
}
