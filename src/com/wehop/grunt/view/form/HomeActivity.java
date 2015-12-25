package com.wehop.grunt.view.form;

import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.wehop.grunt.R;
import com.wehop.grunt.business.Logic;
import com.wehop.grunt.view.control.WebViewEx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

/**
 * 首页
 */
@ResourceView(id = R.layout.activity_home)
public class HomeActivity extends FragmentEx {
	/**
	 * 入口URL
	 */
	public final static String URL = "http://cdn.oss.wehop-resources-beta.wehop.cn/sales/app/sites/v-1/index.html";
	/**
	 * 引导对象
	 */
	@ResourceView(id = R.id.home_image_load)
	public ImageView load = null;
	/**
	 * 浏览器对象
	 */
	@ResourceView(id = R.id.home_browser)
	public WebViewEx browser = null;
	/**
	 * 加载的URL
	 */
	public String url = null;


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//
		prepare();
		load();
	}

	/**
	 * 准备
	 */
	public void prepare() {
		prepareData();
		prepareBrowser();
	}

	/**
	 * 准备数据
	 */
	public void prepareData() {
		this.url = URL + "?token=" + Logic.user.token;
	}

	/**
	 * 准备浏览器
	 */
	public void prepareBrowser() {
		browser.getSettings().setJavaScriptEnabled(true);
		browser.requestFocus();
		browser.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//		browser.setWebViewClient(new WebViewClient() {
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				if(url.startsWith("mailto:") || url.startsWith("geo:") ||url.startsWith("tel:")) {
//					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//	                startActivity(intent);
//	                browser.pauseTimers();
//	                return false;
//	            }
//				browser.loadUrl(url);
//	            return true;
//			}
//		});
		browser.setDefaultHandler(new com.slfuture.pluto.js.DefaultHandler());
		browser.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(100 == newProgress) {
					view.setVisibility(View.VISIBLE);
					load.setVisibility(View.GONE);
					load.clearAnimation();
				}
			}
		});
		browser.setDownloadListener(new DownloadListener() {
			@Override  
	        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {  
	            Uri uri = Uri.parse(url);  
	            Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	            startActivity(intent);  
	        }
		});
	}

	/**
	 * 加载
	 */
	public void load() {
		if(null == url) {
			return;
		}
		Animation animation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.ratote);  
		animation.setInterpolator(new LinearInterpolator());
		load.setVisibility(View.VISIBLE);
		load.startAnimation(animation);
		//
		browser.setVisibility(View.INVISIBLE);
		browser.loadUrl(url);
		//
		browser.prepare();
	}
}
