package com.example.demo.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.demo.model.LoadingAndUnloadingHistory;
import com.example.demo.model.ProductList;

public interface ProductDao {

	//product_listテーブルに商品を登録
	public int insertProduct(ProductList insert) throws DataAccessException;

	//product_listテーブルのデータを1件入庫
	public int insertOne(ProductList insert) throws DataAccessException;

	//loading_and_unloading_historyテーブルのデータを1件入庫
	public int insertOne1(LoadingAndUnloadingHistory insert1) throws DataAccessException;

	//product_listテーブルのデータを1件出庫する際の計算
	public int exitOne(ProductList exit) throws DataAccessException;

	//product_listテーブルのデータを1件取得
	public ProductList selectOne(int id) throws DataAccessException;

	//product_listテーブルの全データを取得
	public List<ProductList> selectMany() throws DataAccessException;

	//product_listテーブルのデータを検索取得
	public List<ProductList> selectSearch1() throws DataAccessException;

	//loading_and_unloadingテーブルの全履歴を取得
	public List<LoadingAndUnloadingHistory> selectMany1() throws DataAccessException;

	//loading_and_unloadingテーブルの履歴を検索取得
	public List<LoadingAndUnloadingHistory> selectSearch(int y, int m, int d) throws DataAccessException;

	//loading_and_unloadingテーブルの4ヶ月前からのデータを削除
	public int deleteMonth() throws DataAccessException;

	//SQL取得結果をサーバーにCSVで保存する
	public void productCsvOut() throws DataAccessException;

	//product_listテーブルの件数を取得
	public int count() throws DataAccessException;

	//loading_and_unloadingテーブルのデータを今月取得
	public List<LoadingAndUnloadingHistory> selectThisMonth();

	//product_listテーブルのデータを1件削除
	public int deteleOne(int id) throws DataAccessException;

}