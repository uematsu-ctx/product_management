package com.example.demo.model;

import lombok.Data;

@Data
public class DeleteProductForm {
	private int id;//ID
	private String productName;//商品名
	private int categoryId;//カテゴリーID
	private String categoryName;//カテゴリー名
	private String companyName;//会社名
	private int count;//現在数量
}
