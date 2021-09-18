package com.example.demo.model;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class SignUpForm {

	private int id;//id

	//必須入力、メールアドレス形式
	@NotBlank(message = "{require_check}",groups = ValidGroup1.class)
	@Email(message = "{email_check}",groups = ValidGroup2.class)
	private String userId;//ユーザーID

	//必須入力、長さ2文字から60文字まで
	@NotBlank(message = "{require_check}",groups = ValidGroup1.class)
	@Length(min = 2,max = 60,groups = ValidGroup2.class)
	private String userName;//ユーザー名

	//必須入力、長さ8桁から100桁まで、半角英数字のみ
	@NotBlank(message = "{require_check}",groups = ValidGroup1.class)
	@Length(min = 8,max = 100,message = "{length_check}",groups = ValidGroup2.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$",message= "{pattern_check}",groups = ValidGroup2.class)
	private String password;//パスワード

	//必須入力、誕生日をyyyy/MM/dd形式
	@NotNull(message = "{require_check}",groups = ValidGroup1.class)
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date birthday;//誕生日

	@NotNull(message = "{require_check}",groups = ValidGroup1.class)
	private String role;//雇用形態
}
