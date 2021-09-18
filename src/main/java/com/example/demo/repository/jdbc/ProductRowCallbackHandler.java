package com.example.demo.repository.jdbc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowCallbackHandler;

public class ProductRowCallbackHandler implements RowCallbackHandler {

	@Override
	public void processRow(ResultSet rs) throws SQLException {
		try {

			//ファイル書き込みの準備
			File file = new File("product_list.csv");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			//取得件数分loop
			do {

				//ResultSetから値を取得してStringにセット
				String str = rs.getString("id") + "," + rs.getString("category_name") + ","
						+ rs.getString("product_name") + "," + rs.getString("company_name") + ","
						+ rs.getString("count");

				//ファイルに書き込み＆改行
				bw.write(str);
				bw.newLine();
			} while (rs.next());

			//強制的に書き込み＆ファイルクローズ
			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
	}
}
