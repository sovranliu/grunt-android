package com.wehop.grunt.view.form;

import com.wehop.grunt.R;
import com.wehop.grunt.base.Logger;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

/**
 * 主界面
 */
public class MainActivity extends FragmentActivity {
	/**
	 * 选项卡个数
	 */
    public final static int TAB_COUNT = 3;
	
    
	/**
     * 选项卡对象
     */
    protected TabHost tabhost = null;


    /**
     * 界面创建
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("call MainActivity.onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //
        tabhost = (TabHost)findViewById(R.id.main_tabhost);
        tabhost.setup();
        tabhost.addTab(tabhost.newTabSpec("main_tab_home").setIndicator("").setContent(R.id.main_fragment_home));
        tabhost.addTab(tabhost.newTabSpec("main_tab_task").setIndicator("").setContent(R.id.main_fragment_task));
        tabhost.addTab(tabhost.newTabSpec("main_tab_user").setIndicator("").setContent(R.id.main_fragment_user));
        RadioGroup group = (RadioGroup)findViewById(R.id.main_tab);
        group.check(R.id.main_tab_home);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                TabHost tabhost = (TabHost)findViewById(R.id.main_tabhost);
                switch(checkedId) {
                    case R.id.main_tab_home:
                        tabhost.setCurrentTabByTag("main_tab_home");
                        break;
                    case R.id.main_tab_task:
                        tabhost.setCurrentTabByTag("main_tab_task");
                        break;
                    case R.id.main_tab_user:
                        tabhost.setCurrentTabByTag("main_tab_user");
                        break;
                }
            }
        });
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
