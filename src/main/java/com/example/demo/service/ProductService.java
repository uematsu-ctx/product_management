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

import com.example.demo.model.CategoryList;
import com.example.demo.model.LoadingAndUnloadingHistory;
import com.example.demo.model.ProductList;
import com.example.demo.repository.ProductDao;
import com.example.demo.repository.UserCategoryDao;

@Transactional
@Service
public class ProductService {

	@Autowired
	ProductDao pd;
	@Autowired
	UserCategoryDao cp;

	//登録用メソッド
	public boolean insert(ProductList insertOne) {

		//1件登録
		int product = pd.insertProduct(insertOne);

		//判定用変数
		boolean result = false;

		if (product > 0) {
			result = true;
		}
		return result;
	}

	//入庫用メソッド
	public boolean insert(ProductList in, LoadingAndUnloadingHistory ins) {

		//1件入庫
		int product = pd.insertOne(in);
		int lounh = pd.insertOne1(ins);

		//判定用変数
		boolean result = false;

		if (product > 0) {
			result = true;
		}
		if (lounh > 0) {
			result = true;
		}
		return result;
	}

	//出庫用メソッド
	public boolean updateOne(ProductList up, LoadingAndUnloadingHistory upd) {

		//1件出庫
		int rowNumber = pd.exitOne(up);
		int update = pd.insertOne1(upd);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//更新成功
			result = true;
		}
		if (update > 0) {

			//更新成功
			result = true;
		}

		return result;
	}

	//1件削除メソッド
	public boolean deleteOne(int id) {
		//1件削除
		int rowNumber = pd.deteleOne(id);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//削除成功
			result = true;
		}
		return result;
	}

	//1件取得メソッド
	public ProductList selectOne(int id) {

		//1件取得
		ProductList pl = pd.selectOne(id);
		return pl;
	}

	//商品一覧の全件取得メソッド
	public List<ProductList> selectProductListMany() {

		//商品一覧リストの全件取得実行
		return pd.selectMany();
	}

	//入出庫履歴の今月分取得メソッド
	public List<LoadingAndUnloadingHistory> selectThisMonth() {

		//今月分取得
		return pd.selectThisMonth();
	}

	//入出庫履歴の全件取得メソッド
	public List<LoadingAndUnloadingHistory> selectTaskHistoryMany() {

		//全件取得実行
		return pd.selectMany1();
	}

	//商品一覧リストの件数を取得
	public int count() {

		//件数取得
		return pd.count();
	}

	//入出庫履歴の検索メソッド
	public List<LoadingAndUnloadingHistory> selectTaskHistorySearch(int y, int m, int d) {

		//年月日による検索
		List<LoadingAndUnloadingHistory> result = pd.selectSearch(y, m, d);

		//検索実行
		return result;
	}

	//カテゴリー名の取得メソッド
	public List<CategoryList> selectCategory() {

		//全件取得実行
		return cp.selectCategory();
	}

	//アプリを立ち上げた日時から2年前のデータを削除
	public boolean deleteMonth() {

		//1件削除
		int mon = pd.deleteMonth();

		//判定用変数
		boolean result = false;

		if (mon > 0) {

			//削除成功
			result = true;
		}
		return result;
	}

	//商品一覧画面をCSV出力する
	public void proCsvOut() throws DataAccessException {

		//CSV出力
		pd.productCsvOut();
	}

	//サーバーに保存されているファイルを取得して、byte配列に変換する
	public byte[] getFile(String fileName) throws IOException {

		//ファイルシステム(デフォルト)の取得
		FileSystem fs = FileSystems.getDefault();

		//ファイル取得
		Path pa = fs.getPath(fileName);

		//ファイルをbyte配列に変換
		byte[] bytes = Files.readAllBytes(pa);

		return bytes;
	}
}