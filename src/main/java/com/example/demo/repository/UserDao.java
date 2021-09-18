package com.example.demo.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.demo.model.UserList;

public interface UserDao {

	//user_listテーブルに1件登録
	public int insertOne(UserList insert) throws DataAccessException;

	//user_listテーブルのログインユーザーのパスワードを更新
	public int passUpdate(UserList update) throws DataAccessException;

	//user_listテーブルのデータを1件更新
	public int userUpdate(UserList update1) throws DataAccessException;

	//user_listテーブルのデータを1件削除
	public int deleteOne(String userId) throws DataAccessException;

	//user_listテーブルのデータを1件取得
	public UserList selectOne(int id) throws DataAccessException;

	//user_listテーブルの全データを取得
	public List<UserList> selectUserListMany() throws DataAccessException;

	//user_listテーブルの件数を取得
	public int count() throws DataAccessException;

	//SQL取得結果をサーバーにCSVで保存する
	public void userCsvOut() throws DataAccessException;

	//ログインユーザーのユーザーIDの重複を防ぐ
	public int countByUserId(String userId) throws DataAccessException;

	//ログインユーザーの名前を取得
	public UserList loginU(String userId) throws DataAccessException;

	//user_listテーブルの担当者を全件取得(削除済みのユーザーを除く)
	public List<UserList> selectUserList() throws DataAccessException;

}
