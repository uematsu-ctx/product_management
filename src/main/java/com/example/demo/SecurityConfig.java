package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	//パスワードエンコーダーのBean定義
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private DataSource dataSource;

	private static final String USER_SQL = "SELECT user_id,password,true FROM user_list WHERE retirement='0' and user_id=?";

	private static final String ROLE_SQL = "SELECT user_id,role FROM user_list WHERE user_id=?";

	@Override
	public void configure(WebSecurity web) throws Exception {
		//静的リソースへのアクセスにはセキュリティを適用しない
		web.ignoring().antMatchers("/webjars/**", "/css/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//ログイン不要ページの設定
		http
				.authorizeRequests()
				.antMatchers("/webjars/**").permitAll()//webjarsへアクセス許可
				.antMatchers("/css/**").permitAll()//cssへアクセス許可
				.antMatchers("/login").permitAll()//ログイン画面は直リンクOK
				.antMatchers("/firstSignUp").permitAll()//最初のユーザー登録画面は直リンクOK
				.antMatchers("/userList").hasAnyAuthority("ROLE_K","ROLE_S")//管理者、正社員はユーザーリスト画面に直リンクOK
				.antMatchers("/signUp").hasAnyAuthority("ROLE_K","ROLE_S")//管理者、正社員はユーザー登録画面に直リンクOK
				.antMatchers("/userUpdateDelete/**").hasAuthority("ROLE_K")//管理者はユーザー更新・削除画面は直リンクOK
				.antMatchers("/insertProduct").hasAuthority("ROLE_K")//管理者は商品の登録画面に直リンクOK
				.antMatchers("/deleteProduct").hasAuthority("ROLE_K")//管理者は商品の削除画面に直リンクOK
				.anyRequest().authenticated();//それ以外は直リンク禁止

		//ログイン処理
		http
				.formLogin()
				.loginProcessingUrl("/login")//ログイン処理のパス
				.loginPage("/login")//ログインページの指定
				.failureUrl("/login")//ロスイン失敗時の遷移先
				.usernameParameter("userId")//ログインページのユーザーID
				.passwordParameter("password")//ログインページのパスワード
				.defaultSuccessUrl("/productList", true);//ログイン成功後の遷移先

		//ログアウト処理
		http
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//ログイン処理時のユーザー情報を、DBから取得する
		auth.jdbcAuthentication()
				.dataSource(dataSource)
				.usersByUsernameQuery(USER_SQL)
				.authoritiesByUsernameQuery(ROLE_SQL)
				.passwordEncoder(passwordEncoder());
	}
}
