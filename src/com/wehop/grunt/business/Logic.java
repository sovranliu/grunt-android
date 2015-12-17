package com.wehop.grunt.business;

import com.wehop.grunt.business.user.Me;
import com.wehop.grunt.framework.Storage;

/**
 * 当前运行时
 */
public class Logic {
	/**
	 * 当前登录用户
	 */
	public static Me user = null;


	/**
	 * 初始化
	 */
	public static boolean initialize() {
		String token = Storage.data("me.token", String.class);
		if(null != token) {
			int id = Storage.data("me.id", Integer.class);
			String phone = Storage.data("me.phone", String.class);
			String name = Storage.data("me.name", String.class);
			//
			Logic.user = new Me(id, phone, null, name, token);
		}
		return true;
	}

	/**
	 * 终结
	 */
	public static void terminate() {
		user = null;
	}

	/**
	 * 登录
	 * 
	 * @param id 用户ID
	 * @param username 用户名
	 * @param password 密码
	 * @param name 用户姓名
	 * @param token 访问口令
	 */
	public static void login(int id, String username, String password, String name, String token) {
		Me user = new Me(id, username, password, name, token);
		Logic.user = user;
		saveUser();
	}

	/**
	 * 保存用户信息
	 */
	public static void saveUser() {
		if(null == user) {
			Storage.clear();
			return;
		}
		Storage.setData("me.id", user.id);
		Storage.setData("me.phone", user.username);
		Storage.setData("me.name", user.name);
		Storage.setData("me.token", user.token);
	}
}
