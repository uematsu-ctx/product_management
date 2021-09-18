package com.example.demo.repository.jdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.CategoryList;
import com.example.demo.repository.UserCategoryDao;

@Repository
public class CategoryDaoJdbcImpl implements UserCategoryDao {
	@Autowired
	JdbcTemplate jdbc;

	//category_listテーブルから全カテゴリー名を取得
	@Override
	public List<CategoryList> selectCategory() {

		//category_listテーブルからcategory_nameを取得
		String sql = "SELECT id,category_name FROM category_list";

		//RowMapperの生成
		RowMapper<CategoryList> rowMapper = new BeanPropertyRowMapper<CategoryList>(CategoryList.class);

		return jdbc.query(sql, rowMapper);
	}


}