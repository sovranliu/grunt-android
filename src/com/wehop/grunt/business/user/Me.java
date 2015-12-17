package com.wehop.grunt.business.user;

/**
 * 当前登陆用户
 */
public class Me extends User {
	/**
	 * 手机号码
	 */
	public String username;
	/**
	 * 登录密码
	 */
	public String password;
	/**
	 * 姓名
	 */
	public String name;
	/**
	 * 操作口令
	 */
	public String token;


	/**
	 * 构造函数
	 * 
     * @param id 用户ID
	 * @param phone 手机号码
	 * @param password 密码
	 * @param name 姓名
	 * @param token 口令
	 */
	public Me() { }
	public Me(int id, String username, String password, String name, String token) {
		super(id);
		this.username = username;
		this.password = password;
		this.name = name;
		this.token = token;
	}
}
