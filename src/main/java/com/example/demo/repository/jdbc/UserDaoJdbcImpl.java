package com.example.demo.repository.jdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.model.UserList;
import com.example.demo.repository.UserDao;

@Repository
public class UserDaoJdbcImpl implements UserDao {
	@Autowired
	JdbcTemplate jdbc;

	//user_listテーブルのデータを1件登録
	@Override
	public int insertOne(UserList insert) throws DataAccessException {

		// 1件登録
		int rowNumber = jdbc.update("INSERT INTO user_list(user_id,user_name,password,birthday,role)VALUES(?,?,?,?,?)",
				insert.getUser_id(), insert.getUser_name(), insert.getPassword(), insert.getBirthday(),
				insert.getRole());

		//SQL実行
		return rowNumber;
	}

	//user_listテーブルのログインユーザーのパスワードを更新
	@Override
	public int passUpdate(UserList update) throws DataAccessException {

		//パスワード更新
		int rowNumber = jdbc.update("UPDATE user_list SET password=? WHERE user_id=?", update.getPassword(),
				update.getUser_id());

		//SQL実行
		return rowNumber;
	}

	//user_listテーブルのデータを全件取得
	@Override
	public List<UserList> selectUserListMany() throws DataAccessException {

		// 全件取得
		String sql = "SELECT * FROM user_list WHERE retirement='0'";

		//RowMapperの生成
		RowMapper<UserList> rowMapper = new BeanPropertyRowMapper<UserList>(UserList.class);

		//SQL実行
		return jdbc.query(sql, rowMapper);
	}

	//user_listテーブルの担当者を全件取得(削除済みの担当者は除く)
	@Override
	public List<UserList> selectUserList() throws DataAccessException {

		//全件取得
		String sql = "SELECT id, user_name FROM user_list WHERE retirement='0'";

		//RowMapperの生成
		RowMapper<UserList> rowMapper = new BeanPropertyRowMapper<UserList>(UserList.class);

		//SQL実行
		return jdbc.query(sql, rowMapper);
	}

	//user_listテーブルのユーザーIDの重複を防ぐ
	@Override
	public int countByUserId(String userId) throws DataAccessException {

		// ユーザーIDの確認
		int rowNumber = jdbc.queryForObject("SELECT COUNT(id) FROM user_list WHERE user_id=?", Integer.class, userId);

		//SQL実行
		return rowNumber;
	}

	//user_listテーブルのデータを1件削除
	@Override
	public int deleteOne(String userId) throws DataAccessException {

		//1件削除
		int rowNumber = jdbc.update("UPDATE user_list SET retirement='1' WHERE user_id=?", userId);

		//SQL実行
		return rowNumber;
	}

	//user_listテーブルのデータを1件取得
	@Override
	public UserList selectOne(int id) throws DataAccessException {
		//1件取得
		String sql = "SELECT * FROM user_list WHERE id=?";

		//RowMapperの生成
		RowMapper<UserList> rowMapper = new BeanPropertyRowMapper<UserList>(UserList.class);

		//SQL実行
		return jdbc.queryForObject(sql, rowMapper, id);
	}

	//user_listテーブルのデータを1件更新
	@Override
	public int userUpdate(UserList update1) throws DataAccessException {

		//1件更新
		int rowNumber = jdbc.update("UPDATE user_list SET role=? WHERE user_id=?", update1.getRole(),
				update1.getUser_id());

		//SQL実行
		return rowNumber;
	}

	//user_listテーブルの件数を取得
	@Override
	public int count() throws DataAccessException {

		//全件取得してカウント
		int count = jdbc.queryForObject("SELECT COUNT(*)FROM user_list WHERE retirement='0'", Integer.class);

		//SQL実行
		return count;
	}

	//user_listテーブルのデータをCSVに出力する
	@Override
	public void userCsvOut() throws DataAccessException {
		//user_listテーブルのデータを取得
		String sql = "SELECT user_id,user_name,birthday,role ,retirement FROM user_list";

		//UserRowCallbackHandlerの生成
		UserRowCallbackHandler handler = new UserRowCallbackHandler();

		//SQL実行
		jdbc.query(sql, handler);
	}

	//ログインユーザーの名前を取得
	@Override
	public UserList loginU(String userId) throws DataAccessException {
		String sql = "SELECT user_name FROM user_list WHERE user_id=?";

		//RowMapperの生成
		RowMapper<UserList> rowMapper = new BeanPropertyRowMapper<UserList>(UserList.class);

		//SQL実行
		return jdbc.queryForObject(sql, rowMapper, userId);
	}
}
