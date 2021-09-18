package com.example.demo.model;

import java.util.Date;

import lombok.Data;

@Data
public class UserList {

	private int id;//id
	private String user_id;//ユーザーID
	private String user_name;//ユーザー名
	private String password;//パスワード
	private Date birthday;//誕生日
	private String role;//雇用形態
	private boolean retirement;//退職者
}
