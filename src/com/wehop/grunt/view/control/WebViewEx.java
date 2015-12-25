package com.wehop.grunt.view.control;

import java.util.List;

import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.pluto.js.BridgeHandler;
import com.slfuture.pluto.js.CallBackFunction;
import com.slfuture.pluto.sensor.Location;
import com.slfuture.pluto.sensor.LocationSensor;
import com.slfuture.pluto.sensor.core.ILocationListener;
import com.wehop.grunt.base.Logger;
import com.wehop.grunt.framework.Utility;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

/**
 * 拓展WebView
 */
public class WebViewEx extends com.slfuture.pluto.js.BridgeWebView {
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
		registerHandler("capture", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				Logger.i("capture(" + data + ") execute");
				final CallBackFunction fFunction = function;
				Utility.capture((Activity) WebViewEx.this.getContext(), new IEventable<String>() {
					@Override
					public void on(String data) {
						fFunction.onCallBack(data);
					}
				});
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
				final CallBackFunction callbackFunction = function;
				LocationSensor.fetchCurrentLocation(new ILocationListener() {
					@Override
					public void onListen(Location location) {
						if(null == location) {
							callbackFunction.onCallBack("{\"latitude\":0.0, \"longitude\":0.0}");
							return;
						}
						String result = "{\"latitude\":\"" + location.latitude + "\", \"longitude\":\"" + location.longitude + "\"}";
						callbackFunction.onCallBack(result);
					}
				}, 2000);
				Logger.i("gps(" + data + ") execute");
            }
		});
	}
}
