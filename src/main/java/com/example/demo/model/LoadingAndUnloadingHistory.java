package com.example.demo.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoadingAndUnloadingHistory {
	private int id;//ID
	private String category_name;//分類名
	private String product_name;//商品名
	private String company_name;//会社名
	private String user_name;//担当者
	private LocalDateTime date_time;//日時
	private int count_before;//変更前の数量
	private String count_l;//入出庫数
	private int person_id;//担当者ID
	private int product_name_id;//商品名ID
	private int company_name_id;//会社名ID
}
