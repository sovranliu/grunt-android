package com.wehop.grunt.view.form;

import com.wehop.grunt.R;
import com.wehop.grunt.base.Logger;
import com.wehop.grunt.business.Logic;
import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 登录页
 */
@ResourceView(id = R.layout.activity_login)
public class LoginActivity extends ActivityEx {
	@ResourceView(id = R.id.login_text_username)
	public EditText txtUsername;
	@ResourceView(id = R.id.login_text_password)
	public EditText txtPassword;
	@ResourceView(id = R.id.login_button_login)
	public Button btnLogin;

	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.i("LoginActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		//
		prepare();
		load();
	}

	/**
	 * 界面启动预处理
	 */
	public void prepare() {
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(txtUsername.getText().toString().equals("")) {
					Toast.makeText(LoginActivity.this, "请填写账号", Toast.LENGTH_LONG).show();
					return;
				}
				if(txtPassword.getText().toString().equals("")) {
					Toast.makeText(LoginActivity.this, "请填写密码", Toast.LENGTH_LONG).show();
					return;
				}
				String md5 = Serial.getMD5String(txtPassword.getText().toString()).toLowerCase();
				md5 = md5.substring(8, 24);
				Networking.doCommand("login", new JSONResponse(LoginActivity.this) {
					@Override
					public void onFinished(JSONVisitor content) {
						if(null == content) {
							return;
						}
						if(content.getInteger("code", -1) <= 0) {
							txtPassword.setText("");
							return;
						}
						JSONVisitor dataVisitor = content.getVisitor("data");
						Logic.login(dataVisitor.getInteger("id", 0), dataVisitor.getString("username"), dataVisitor.getString("password"), dataVisitor.getString("name"), dataVisitor.getString("photo"), dataVisitor.getString("token"));
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						LoginActivity.this.startActivity(intent);
						LoginActivity.this.finish();
					}
				}, txtUsername.getText().toString(), md5);
			}
		});
	}

	/**
	 * 加载数据
	 */
	public void load() {
		
	}
}
