package com.wehop.grunt.view.form;

import com.slfuture.carrie.base.json.JSONVisitor;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.communication.response.JSONResponse;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentActivityEx;
import com.wehop.grunt.R;
import com.wehop.grunt.base.Logger;
import com.wehop.grunt.business.Logic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

/**
 * 主界面
 */
@ResourceView(id = R.layout.activity_main)
public class MainActivity extends FragmentActivityEx {
	/**
	 * 选项卡个数
	 */
    public final static int TAB_COUNT = 3;
	
    
	/**
     * 选项卡对象
     */
    @ResourceView(id = R.id.main_tabhost)
    public TabHost tabhost = null;
    @ResourceView(id = R.id.main_tab_home)
    public RadioButton btnHome = null;
    @ResourceView(id = R.id.main_tab_task)
    public RadioButton btnTask = null;
    @ResourceView(id = R.id.main_tab_user)
    public RadioButton btnUser = null;

    /**
     * 界面创建
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("call MainActivity.onCreate()");
        //
        tabhost.setup();
        tabhost.addTab(tabhost.newTabSpec("main_tab_home").setIndicator("").setContent(R.id.main_fragment_home));
        tabhost.addTab(tabhost.newTabSpec("main_tab_task").setIndicator("").setContent(R.id.main_fragment_task));
        tabhost.addTab(tabhost.newTabSpec("main_tab_user").setIndicator("").setContent(R.id.main_fragment_user));
        RadioGroup group = (RadioGroup)findViewById(R.id.main_tab);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                TabHost tabhost = (TabHost)findViewById(R.id.main_tabhost);
                switch(checkedId) {
                    case R.id.main_tab_home:
                        tabhost.setCurrentTabByTag("main_tab_home");
                        btnHome.setTextColor(getResources().getColor(R.color.white));
                        btnTask.setTextColor(getResources().getColor(R.color.green));
                        btnUser.setTextColor(getResources().getColor(R.color.green));
                        break;
                    case R.id.main_tab_task:
                        tabhost.setCurrentTabByTag("main_tab_task");
                        btnHome.setTextColor(getResources().getColor(R.color.green));
                        btnTask.setTextColor(getResources().getColor(R.color.white));
                        btnUser.setTextColor(getResources().getColor(R.color.green));
                        break;
                    case R.id.main_tab_user:
                        tabhost.setCurrentTabByTag("main_tab_user");
                        btnHome.setTextColor(getResources().getColor(R.color.green));
                        btnTask.setTextColor(getResources().getColor(R.color.green));
                        btnUser.setTextColor(getResources().getColor(R.color.white));
                        break;
                }
            }
        });
        group.check(R.id.main_tab_home);
        btnHome.setTextColor(getResources().getColor(R.color.white));
        btnTask.setTextColor(getResources().getColor(R.color.green));
        btnUser.setTextColor(getResources().getColor(R.color.green));
        tabhost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
		        for(int i = 0; i < TAB_COUNT; i++) {
					if("main_tab_home".equals(tabId)) {
			        	final TextView txtTitle = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			        	txtTitle.setText("ceshi");
			        	txtTitle.setTextColor(Color.GREEN);
					}
		        }
			}
        });
        check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 检查用户合法性
     */
    public void check() {
    	Networking.doCommand("check", new JSONResponse(MainActivity.this) {
			@Override
			public void onFinished(JSONVisitor content) {
				if(null == content) {
					return;
				}
				if(1 == content.getInteger("code", 0)) {
					return;
				}
				Logic.logout();
				// 切入登录页
				MainActivity.this.finish();
				MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
			}
    	}, Logic.user.username, Logic.user.token);
    }
}
