package com.example.demo.model;

import lombok.Data;

@Data
public class ProductList {
	private int id;//ID
	private int category_id;//カテゴリーID
	private String category_name;//カテゴリー名
	private String product_name;//商品名
	private String company_name;//会社名
	private int person_id;//担当者
	private int count;//数量
}