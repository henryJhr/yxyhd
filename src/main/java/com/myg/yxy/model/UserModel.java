package com.myg.yxy.model;

import com.hxsoft.hsf.boot.framework.model.BaseModel;
import lombok.Data;

@Data
public class UserModel extends BaseModel {

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 姓名
	 */
	private String userName;
	/**
	 * 编码
	 */
	private String userCode;
	/**
	 * 头像
	 */
	private String userAvatar;
	/**
	 * 性别
	 */
	private Integer sex;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 手机号
	 */
	private String linkMobile;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 密码盐值
	 */
	private String salt;
	/**
	 * 是否锁定
	 */
	private Integer locked;
	/**
	 * 百度配置ID
	 */
	private Long configId;
	/**
	 * openid
	 */
	private String configName;

}
