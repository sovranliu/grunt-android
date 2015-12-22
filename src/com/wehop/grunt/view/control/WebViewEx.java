package com.wehop.grunt.view.control;

import java.util.LinkedList;
import java.util.List;
import com.slfuture.pluto.js.BridgeHandler;
import com.slfuture.pluto.js.CallBackFunction;
import com.slfuture.pluto.sensor.Position;
import com.wehop.grunt.base.Logger;
import com.wehop.grunt.framework.Utility;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

/**
 * 拓展WebView
 */
public class WebViewEx extends com.slfuture.pluto.js.BridgeWebView {
	/**
	 * 
	 */
	public final static int MESSAGE_ID_CAPTURE = 999;
	/**
	 * 消息与回调映射
	 */
	private List<CallBackFunction> callbacks = new LinkedList<CallBackFunction>();
	
	
	public WebViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WebViewEx(Context context) {
		super(context);
	}

	public void prepare() {
		Position.initialize(this.getContext());
		registerHandler("capture", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				Logger.i("capture(" + data + ") execute");
				callbacks.add(function);
				Utility.capture((Activity) WebViewEx.this.getContext(), MESSAGE_ID_CAPTURE);
            }
		});
		registerHandler("wifi", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				Logger.i("wifi(" + data + ") execute");
				List<String> list = Utility.scanWIFI();
				StringBuilder builder = null;
				for(String item : list) {
					if(null == builder) {
						builder = new StringBuilder();
						builder.append("{");
					}
					else {
						builder.append(",");
					}
					builder.append("\"");
					builder.append(item);
					builder.append("\"");
				}
				if(null == builder) {
					builder = new StringBuilder();
					builder.append("{");
				}
				builder.append("}");
				function.onCallBack(builder.toString());
            }
		});
		registerHandler("gps", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				Logger.i("gps(" + data + ") execute");
				String result = "{\"latitude\":\"" + Position.currentLatitude + "\", \"longitude\":\"" + Position.currentLongitude + "\"}";
				function.onCallBack(result);
            }
		});
	}

	/**
	 * 回调处理
	 * 
	 * @param data 数据
	 */
	public void callback(String data) {
		for(CallBackFunction function : callbacks) {
			function.onCallBack(data);
		}
		callbacks.clear();
	}
}
