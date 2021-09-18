package com.example.demo.repository.jdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LoadingAndUnloadingHistory;
import com.example.demo.model.ProductList;
import com.example.demo.repository.ProductDao;

@Repository
public class ProductDaoJdbcImpl implements ProductDao {
	@Autowired
	JdbcTemplate jdbc;

	//product_listテーブルに新たな商品を1件登録
	@Override
	public int insertProduct(ProductList insert) throws DataAccessException {

		//1件登録
		int rowNumber = jdbc.update("INSERT INTO product_list(product_name,category_id,company_name,count)VALUES(?,?,?,?)",
				insert.getProduct_name(), insert.getCategory_id(),insert.getCompany_name(), insert.getCount());

		return rowNumber;
	}

	//product_listテーブルにデータを1件入庫登録
	@Override
	public int insertOne(ProductList insert) throws DataAccessException {
		//1件入庫
		int rowNumber = jdbc.update("UPDATE product_list SET count = count + ? WHERE id = ?", insert.getCount(),
				insert.getId());

		return rowNumber;
	}

	//product_listテーブルにデータを1件出庫登録
	@Override
	public int exitOne(ProductList exit) throws DataAccessException {
		//1件出庫
		int rowNumber = jdbc.update("UPDATE product_list SET count = count - ? WHERE id = ?", exit.getCount(),
				exit.getId());

		return rowNumber;
	}

	//loading_and_unloading_historyテーブルにデータを1件履歴に入庫、出庫確認
	@Override
	public int insertOne1(LoadingAndUnloadingHistory insert1) throws DataAccessException {
		//1件履歴に反映
		int rowNumber = jdbc.update(
				"INSERT INTO loading_and_unloading_history(date_time,product_name_id,company_name_id,count_before,count_l,person_id) VALUES(?,?,?,?,?,?)",
				insert1.getDate_time(), insert1.getId(),insert1.getId(), insert1.getCount_before(), insert1.getCount_l(),
				insert1.getPerson_id());

		return rowNumber;
	}

	//product_listテーブルから1件データを取得
	@Override
	public ProductList selectOne(int id) {

		//1件取得
		String sql = "SELECT product_list.id, product_list.product_name,product_list.company_name,category_list.category_name,product_list.count FROM product_list inner JOIN  category_list ON product_list.category_id=category_list.id WHERE product_list.id=?";

		//RowMapperの生成
		RowMapper<ProductList> rowMapper = new BeanPropertyRowMapper<ProductList>(ProductList.class);

		//SQL実行
		return jdbc.queryForObject(sql, rowMapper, id);
	}

	//product_listテーブルを全件取得
	@Override
	public List<ProductList> selectMany() throws DataAccessException {
		//product_listテーブルのcategory_idをcategory_listテーブルのidを一致させて、category_listテーブルのcategory_nameを取得する
		String sql = "SELECT pl.id,category_name,product_name,company_name,count,delete_flug FROM product_list as pl, category_list as cl WHERE pl.category_id=cl.id AND delete_flug='0'";

		//RowMapperの生成
		RowMapper<ProductList> rowMapper = new BeanPropertyRowMapper<ProductList>(ProductList.class);

		//SQL実行
		return jdbc.query(sql, rowMapper);

	}

	//product_listテーブルを検索取得
	@Override
	public List<ProductList> selectSearch1() {
		//商品一覧のデータを検索
		String sql = " SELECT category_name,product_name,company_name,count FROM product_list as pl, category_list as cl WHERE pl.category_id=cl.id"
				+
				"AND (category_name like '%%' OR product_name like '%%')";

		//RowMapperの生成
		RowMapper<ProductList> rowMapper = new BeanPropertyRowMapper<ProductList>(ProductList.class);

		//SQL実行
		return jdbc.query(sql, rowMapper);
	}

	//product_listテーブルの件数を取得
	@Override
	public int count() throws DataAccessException {
		//全件取得してカウント
		int count = jdbc.queryForObject("SELECT COUNT(*)FROM product_list WHERE delete_flug='0'", Integer.class);

		return count;
	}

	//loading_and_unloading_historyテーブルのデータを今月取得
	@Override
	public List<LoadingAndUnloadingHistory> selectThisMonth() {
		//今月取得
		String sql = "SELECT lh.date_time,cl.category_name,pl.product_name,pl.company_name,lh.count_before,lh.count_l,usel.user_name FROM loading_and_unloading_history as lh JOIN product_list as pl ON lh.product_name_id=pl.id JOIN category_List as cl ON pl.category_id=cl.id JOIN user_list as usel ON lh.person_id = usel.id AND lh.company_name_id=pl.id WHERE DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%y%m%d') ORDER BY date_time ASC ";

		//RowMapperの生成
		RowMapper<LoadingAndUnloadingHistory> rowMapper = new BeanPropertyRowMapper<LoadingAndUnloadingHistory>(
				LoadingAndUnloadingHistory.class);

		//SQL実行
		return jdbc.query(sql, rowMapper);
	}

	//loading_and_unloading_historyテーブルを全件取得
	@Override
	public List<LoadingAndUnloadingHistory> selectMany1() throws DataAccessException {
		//全件取得
		String sql = "SELECT lh.date_time,cl.category_name,pl.product_name,pl.company_name,lh.count_before,lh.count_l,usel.user_name FROM loading_and_unloading_history as lh JOIN product_list as pl ON lh.product_name_id=pl.id JOIN category_List as cl ON pl.category_id=cl.id JOIN user_list as usel ON lh.person_id = usel.id AND lh.company_name_id=pl.id ORDER BY date_time ASC";

		//RowMapperの生成
		RowMapper<LoadingAndUnloadingHistory> rowMapper = new BeanPropertyRowMapper<LoadingAndUnloadingHistory>(
				LoadingAndUnloadingHistory.class);

		//SQL実行
		return jdbc.query(sql, rowMapper);
	}

	//loading_and_unloading_historyテーブルを検索取得
	@Override
	public List<LoadingAndUnloadingHistory> selectSearch(int year, int month, int day) {

		//0埋めをして文字列に変換 例)2021年->21,8月->08,1日->01
		String mo = String.format("%02d%02d%02d", year, month, day);

		//検索取得
		String sql = "SELECT lh.date_time,DATE_FORMAT(lh.date_time, '%y%m%d'),cl.category_name,pl.product_name,pl.company_name,lh.count_before,lh.count_l,usel.user_name FROM loading_and_unloading_history as lh JOIN product_list as pl ON lh.product_name_id=pl.id JOIN category_List as cl ON pl.category_id=cl.id JOIN user_list as usel ON lh.person_id = usel.id AND lh.company_name_id=pl.id WHERE DATE_FORMAT(lh.date_time, '%y%m%d')=?;";

		//RowMapperの生成
		RowMapper<LoadingAndUnloadingHistory> rowMapper = new BeanPropertyRowMapper<LoadingAndUnloadingHistory>(
				LoadingAndUnloadingHistory.class);
		//SQL実行
		return jdbc.query(sql, rowMapper, mo);
	}

	//loading_and_unloading_historyテーブルのデータを1年分だけ保存
	@Override
	public int deleteMonth() throws DataAccessException {
		//アプリを立ち上げた日時から2年前の入出庫履歴データを削除
		String sql = "DELETE FROM loading_and_unloading_history WHERE(date_time < DATE_SUB(CURDATE(), INTERVAL 2 YEAR))";

		//SQL実行
		return jdbc.update(sql);
	}

	//product_listテーブルのデータを1件削除
	@Override
	public int deteleOne(int id)throws DataAccessException{
		//1件削除
		int rowNumber=jdbc.update("UPDATE product_list SET delete_flug='1' WHERE id=?",id);

		//SQL実行
		return rowNumber;
	}
	//product_listテーブルの全データをCSVに出力する
	@Override
	public void productCsvOut() throws DataAccessException {

		//商品一覧画面のデータを取得する
		String sql = "SELECT pl.id,category_name,product_name,company_name,count FROM product_list as pl, category_list as cl WHERE pl.category_id=cl.id;";

		//ProductRowCallbackHandler生成する
		ProductRowCallbackHandler handler = new ProductRowCallbackHandler();

		//SQL実行
		jdbc.query(sql, handler);
	}

}