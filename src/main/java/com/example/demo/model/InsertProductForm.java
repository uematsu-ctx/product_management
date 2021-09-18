package com.example.demo.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class InsertProductForm {
	private int id;//ID
	@NotBlank(message = "{require_check}",groups = ValidGroup1.class)
	private String productName;//商品名
	private int categoryId;//カテゴリーID
	@NotBlank(message="{require_check}",groups=ValidGroup1.class)
	private String companyName;//会社名
	private int count;//現在数量

}
