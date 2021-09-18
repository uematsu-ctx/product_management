package com.example.demo.service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.UserList;
import com.example.demo.repository.UserDao;

@Transactional
@Service
public class UserService {
	@Autowired
	UserDao ud;

	//1件登録用メソッド
	public boolean insert(UserList insert) {

		//登録実行
		int rowNumber = ud.insertOne(insert);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			//登録成功
			result = true;
		}
		return result;
	}

	//ユーザーの更新メソッド
	public boolean updateOne(UserList user) {

		//1件更新
		int rowNumber = ud.userUpdate(user);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//更新成功
			result = true;
		}
		return result;
	}

	//カウント用メソッド
	public int count() {
		//件数を取得
		return ud.count();
	}

	//1件取得用メソッド
	public UserList selectOne(int id) {

		//1件取得
		return ud.selectOne(id);
	}

	//全件取得用メソッド
	public List<UserList> selectMany() {

		//全件取得
		return ud.selectUserListMany();
	}

	//担当者取得用メソッド(削除済みのユーザーは除く)
	public List<UserList>selectList(){

		//全件取得
		return ud.selectUserList();
	}

	//1件削除メソッド
	public boolean deleteOne(String userId) {

		//1件削除
		int rowNumber = ud.deleteOne(userId);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//削除成功
			result = true;
		}
		return result;
	}

	//ユーザー一覧をCSV出力する
	public void userCsvOut() throws DataAccessException {

		//CSV出力
		ud.userCsvOut();
	}

	//ログインユーザーのパスワードを更新
	public boolean updatePassword(UserList pass) {

		//1件更新
		int rowNumber = ud.passUpdate(pass);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//更新成功
			result = true;
		}
		return result;
	}

	//サーバーに保存されているファイルを取得して、byte配列に変換する
	public byte[] getFile(String fileName) throws IOException {

		//ファイルシステム(デフォルト)の取得
		FileSystem fs = FileSystems.getDefault();

		//ファイル取得
		Path p = fs.getPath(fileName);

		//ファイルをbyte配列に変換
		byte[] bytes = Files.readAllBytes(p);

		return bytes;
	}

	//ログインユーザーの名前を取得
	public UserList loginU(String userId) {

		//名前を取得
		return ud.loginU(userId);
	}

	//ユーザーIDの重複の確認のメソッド
	public boolean isDuplicated(String userId) {

		//1件確認
		int rowNumber = ud.countByUserId(userId);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//登録可能
			result = true;
		}
		return result;
	}

}
