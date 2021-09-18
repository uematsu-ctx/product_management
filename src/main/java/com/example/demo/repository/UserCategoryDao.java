package com.example.demo.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.demo.model.CategoryList;

public interface UserCategoryDao {

	//カテゴリー名を取得
	public List<CategoryList> selectCategory() throws DataAccessException;

}
