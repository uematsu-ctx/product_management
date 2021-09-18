package com.example.demo.model;

import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class ExitForm {
	private int id;
	private String productName;//商品名
	@Min(value = 1, message = "{min_check}", groups = ValidGroup2.class)
	private int countMinus;//数量を出庫

	private int count;//現在数量
	private int countBefore;//(差分の元を表す数値)
	private String userName;//担当者
	private int productNameId;//商品ID
	private int categoryId;//カテゴリーID
	private int personId;//担当者ID
	private int countL;//出庫数量
	private String companyName;//会社名
	private int companyNameId;//会社名ID
}
