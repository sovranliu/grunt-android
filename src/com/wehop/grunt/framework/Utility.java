package com.wehop.grunt.framework;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.carrie.base.model.core.IEventable;
import com.slfuture.carrie.base.time.DateTime;
import com.slfuture.carrie.base.time.Duration;
import com.slfuture.pluto.etc.Controller;
import com.wehop.grunt.Program;
import com.wehop.grunt.R;
import com.wehop.grunt.framework.qcode.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 工具类
 */
public class Utility {
	/**
	 * 请求手机磁盘
	 */
	public static final int INTENT_REQUEST_PHONE = 11;
	/**
	 * 请求手机
	 */
	public static final int INTENT_REQUEST_CAMERA = 12;
	
	
	/**
	 * 隐藏构造函数
	 */
	private Utility() { }

	/**
	 * 生成圆形的图片
	 * 
	 * @param bitmap 图片对象
	 * @param width 目标宽度
	 * @param height 目标高度
	 * @return 打圆的图片
	 */
	public static Bitmap makeCycleImage(Bitmap bitmap, int width, int height) {
		float radius = 0;
		if(width > height) {
			radius = height;
		}
		else {
			radius = width;
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		RectF rect = new RectF(0, 0, radius, radius);
		canvas.drawRoundRect(rect, radius/2, radius/2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		return result;
	}

	/**
	 * 给位图增加环
	 * 
	 * @param bitmap 位图对象
	 * @param stroke 位图对象
	 */
	public static Bitmap makeImageRing(Bitmap bitmap, int strokeWidth) {
		float radius = bitmap.getWidth();
		if(radius > bitmap.getHeight()) {
			radius = bitmap.getHeight();
		}
		radius = radius / 2 - strokeWidth + 1;
		Bitmap result = bitmap;
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Program.application.getResources().getColor(R.color.white));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, radius, paint);
		return result;
	}

	/**
	 * 拷贝文件
	 * 
	 * @param stream 源
	 * @param target 目标文件
	 */
	public static void copyFile(InputStream stream, File target) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
        	bufferedInputStream = new BufferedInputStream(stream);
        	bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(target));
            byte[] b = new byte[1024 * 10];
            int len;
            while ((len = bufferedInputStream.read(b)) != -1) {
            	bufferedOutputStream.write(b, 0, len);
            }
            bufferedOutputStream.flush();
        }
        finally {
            if (bufferedInputStream != null) {
            	bufferedInputStream.close();
            }
            bufferedInputStream = null;
            if (bufferedOutputStream != null) {
            	bufferedOutputStream.close();
            }
            bufferedOutputStream = null;
        }
    }

	/**
	 * 图片压缩
	 * 
	 * @param bitmap 位图
	 * @param side 边长
	 * @return 压缩后的位图
	 */
	public static Bitmap compress(Bitmap bitmap, int side) {
		Matrix matrix = new Matrix();
		// matrix.setRotate(90);
		matrix.postScale((float) (side / bitmap.getWidth()), (float) (side / bitmap.getHeight()));
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * 选择图片
	 * 
	 * @param activity 上下文
	 */
	public static void selectImage(final Activity activity) {
		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setGravity(Gravity.BOTTOM);
		window.setAttributes(layoutParams);
		window.setContentView(R.layout.dialog_imageselect);
		TextView txtCancel = (TextView) window.findViewById(R.id.imageselect_cancel);
		txtCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.cancel();
			}
		});
		TextView txtPhone = (TextView) window.findViewById(R.id.imageselect_phone);
		txtPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				activity.startActivityForResult(intent, INTENT_REQUEST_PHONE);
				alertDialog.hide();
			}
		});
		TextView txtCamera = (TextView) window.findViewById(R.id.imageselect_camera);
		txtCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String status = Environment.getExternalStorageState();
				if (!status.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(activity, "SD卡不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				activity.startActivityForResult(intent, INTENT_REQUEST_CAMERA);
				alertDialog.hide();
			}
		});
	}

	/**
	 * 生成时间的描述文字
	 * 
	 * @param time 时间
	 * @return 描述文字
	 */
	public static String makeTimeDescription(DateTime time) {
		if(null == time) {
			return "";
		}
		if(DateTime.now().getDate().toInteger() - 2 > time.getDate().toInteger()) {
			return time.getDate().toString();
		}
		else if(DateTime.now().getDate().toInteger() - 1 > time.getDate().toInteger()) {
			return "前天";
		}
		else if(DateTime.now().getDate().toInteger() > time.getDate().toInteger()) {
			return "昨天";
		}
		else if(DateTime.now().toLong() - Duration.HOUR_MILLIS > time.toLong()) {
			return "今天";
		}
		else if(DateTime.now().toLong() - Duration.HOUR_MILLIS / 2 > time.toLong()) {
			return "1小时内";
		}
		else if(DateTime.now().toLong() - Duration.MINUTE_MILLIS * 10 > time.toLong()) {
			return "半小时内";
		}
		else if(DateTime.now().toLong() - Duration.MINUTE_MILLIS * 2 > time.toLong()) {
			return "10分钟内";
		}
		else if(DateTime.now().getDate().toInteger() == time.getDate().toInteger()) {
			return "今天";
		}
		else if(DateTime.now().getDate().toInteger() == time.getDate().toInteger() - 1) {
			return "明天";
		}
		else if(DateTime.now().getDate().toInteger() == time.getDate().toInteger() - 2) {
			return "后天";
		}
		if(time.getDate().year() == DateTime.now().year()) {
			return time.getDate().toString("MM-dd");
		}
		else {
			return time.getDate().toString();
		}
	}
	
	/**
	 * 扫描WIFI
	 * 
	 * @return 可用 WIFI列表
	 */
	public static List<String> scanWIFI() {
		WifiManager manager = (WifiManager) Program.application.getSystemService(Context.WIFI_SERVICE);  
		manager.startScan();
		List<ScanResult> list = manager.getScanResults();
		Collections.sort(list,new Comparator<ScanResult>(){
            public int compare(ScanResult arg0, ScanResult arg1) {
                return arg0.level - arg1.level;
            }
        });
		LinkedList<String> result = new LinkedList<String>();
		for(ScanResult scanResult : list) {
			result.add(scanResult.SSID);
		}
		return result;
	}

	/**
	 * 扫描二维码
	 * 
	 * @param context 上下文
	 * @param callback 回调
	 */
	public static void capture(Activity context, IEventable<String> callback) {
		Intent intent = new Intent(context, CaptureActivity.class);
		int commandId = Serial.makeLoopInteger();
		intent.putExtra("commandId", commandId);
		Controller.<String>doJoin(commandId, callback);
		context.startActivity(intent);
	}
}
