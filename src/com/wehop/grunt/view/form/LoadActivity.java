package com.wehop.grunt.view.form;

import java.io.File;

import com.wehop.grunt.Program;
import com.wehop.grunt.R;
import com.wehop.grunt.base.Logger;
import com.wehop.grunt.business.Logic;
import com.wehop.grunt.framework.DynamicConfig;
import com.wehop.grunt.framework.Storage;
import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.etc.Control;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.ActivityEx;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * 引导页
 */
@ResourceView(id = R.layout.activity_load)
public class LoadActivity extends ActivityEx {
	@ResourceView(id = R.id.load_image_logo)
	public ImageView imageLogo;

	/**
	 * 界面创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.i("LoadActivity.onCreate() execute");
		super.onCreate(savedInstanceState);
		//
		prepare();
		load();
	}

	/**
	 * 界面启动预处理
	 */
	public void prepare() {
		// 启动动画
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.gradually);
		imageLogo.startAnimation(animation);
	}

	/**
	 * 加载数据
	 */
	public void load() {
		File loadImage = DynamicConfig.fetchFile("load.png");
		if(null != loadImage) {
			imageLogo.setImageBitmap(Storage.getImage(loadImage));
		}
		Host.doCommand("update", new JSONResponse(this) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content) {
					return;
				}
				if(content.getInteger("code") <= 0) {
					return;
				}
				JSONVisitor data = content.getVisitor("data");
				String version = data.getString("appVersion");
				if(null == version) {
					return;
				}
				String url = data.getString("downloadUrl");
				if(null == url) {
					return;
				}
				int v = Integer.parseInt(version.replace(".", ""));
				if(v <= Integer.valueOf(Program.VERSION.replace(".", ""))) {
					return;
				}
				Intent intent = new Intent(LoadActivity.this, WebActivity.class);
				intent.putExtra("url", url);
				LoadActivity.this.startActivity(intent);
			}
		}, Program.VERSION);
		Control.doDelay(new Runnable() {
			@Override
			public void run() {
		        if(null == Logic.user) {
		        	startActivity(new Intent(LoadActivity.this, LoginActivity.class));
		        }
		        else {
					startActivity(new Intent(LoadActivity.this, MainActivity.class));
		        }
            	LoadActivity.this.finish();
			}
		}, 2000);
	}
}
