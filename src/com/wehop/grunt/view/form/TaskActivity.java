package com.wehop.grunt.view.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.slfuture.carrie.base.etc.Serial;
import com.slfuture.pluto.communication.Networking;
import com.slfuture.pluto.view.annotation.ResourceView;
import com.slfuture.pluto.view.component.FragmentEx;
import com.wehop.grunt.R;
import com.wehop.grunt.business.Logic;
import com.wehop.grunt.framework.Storage;
import com.wehop.grunt.view.control.WebViewEx;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

/**
 * 任务页
 */
@ResourceView(id = R.layout.activity_task)
public class TaskActivity extends FragmentEx {
	/**
	 * 引导对象
	 */
	@ResourceView(id = R.id.task_image_load)
	public ImageView load = null;
	/**
	 * 浏览器对象
	 */
	@ResourceView(id = R.id.task_browser)
	public WebViewEx browser = null;
	/**
	 * 上传消息
	 */
	public ValueCallback<Uri> uploadMessage;
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
		this.url = Networking.fetchURL("TaskPage", Logic.user.token);
	}

	/**
	 * 准备浏览器
	 */
	public void prepareBrowser() {
		browser.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
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
//	                // browser.resumeTimers();
//	                return true;
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

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
				this.openFileChooser(uploadMsg);
			}

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
				this.openFileChooser(uploadMsg);
			}

			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				uploadMessage = uploadMsg;
				pickFile();
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

	public void pickFile() {
		Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
		chooserIntent.setType("image/*");
		startActivityForResult(chooserIntent, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (0 == requestCode) {
			if (null == uploadMessage){
				return;
			}
			Uri result = intent == null || resultCode != -1 ? null : intent.getData();
			if(null != result) {
				result = Uri.parse("file://" + compressImage(BitmapFactory.decodeFile(getRealPathFromURI(result))));
			}
			uploadMessage.onReceiveValue(result);
			uploadMessage = null;
		}
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

	public Bitmap compressBitmap(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;
    }
	
	/**
	 * 压缩图片
	 * 
	 * @param path 图片路径
	 */
	public String compressImage(Bitmap image) {
		File template = new File(Storage.imagePath("temp" + Serial.makeSerialString() + ".png"));
		try {
			FileOutputStream out = new FileOutputStream(template, true);
			ratio(image, 500, 500).compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		}
		catch(Exception ex) {
			return null;
		}
		return template.getAbsolutePath();
	}
	
	public String getRealPathFromURI(Uri contentUri) {
		if(null != this.getActivity()) {
			return getImageAbsolutePath(this.getActivity(), contentUri);
		}
		Cursor cursor = this.getActivity().getContentResolver().query(contentUri, null, null, null, null);
		if(null == cursor) {
			// file:///storage/emulated/0/DCIM/Camera/IMG_20151224_193837.jpg
			return contentUri.getPath().replace("file://", "");
		}
		else {
			String res = null;
			if(cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				res = cursor.getString(column_index);
			}
			cursor.close();
			return res;
		}
	}
	
	
	/**
	 * Compress image by size, this will modify image width/height. 
	 * Used to get thumbnail
	 * 
	 * @param image
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @return
	 */
	public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
	Bitmap bitmap = null;
	try {
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	    image.compress(Bitmap.CompressFormat.JPEG, 100, os);
	    if( os.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
	        os.reset();//重置baos即清空baos  
	        image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中  
	    }  
	    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());  
	    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
	    newOpts.inJustDecodeBounds = true;
	    newOpts.inPreferredConfig = Config.RGB_565;
	    bitmap = BitmapFactory.decodeStream(is, null, newOpts);  
	    newOpts.inJustDecodeBounds = false;  
	    int w = newOpts.outWidth;  
	    int h = newOpts.outHeight;  
	    float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
	    float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
	    int be = 1;//be=1表示不缩放  
	    if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outWidth / ww);  
	    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outHeight / hh);  
	    }  
	    if (be <= 0) be = 1;  
	    newOpts.inSampleSize = be;//设置缩放比例  
	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
	    is = new ByteArrayInputStream(os.toByteArray());  
	    bitmap = BitmapFactory.decodeStream(is, null, newOpts);
	    //压缩好比例大小后再进行质量压缩
	    //return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
	} catch (Exception e) {
	// TODO: handle exception
	e.printStackTrace();
	Log.e("--ImageFactory.ratio--", e.toString());
	}
	    return bitmap;
	}
	
	
	
	public String getImageAbsolutePath(Context context, Uri imageUri) {  
	    if (context == null || imageUri == null)  
	        return null;  
	    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {  
	        if (isExternalStorageDocument(imageUri)) {  
	            String docId = DocumentsContract.getDocumentId(imageUri);  
	            String[] split = docId.split(":");  
	            String type = split[0];  
	            if ("primary".equalsIgnoreCase(type)) {  
	                return Environment.getExternalStorageDirectory() + "/" + split[1];  
	            }  
	        } else if (isDownloadsDocument(imageUri)) {  
	            String id = DocumentsContract.getDocumentId(imageUri);  
	            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
	            return getDataColumn(context, contentUri, null, null);  
	        } else if (isMediaDocument(imageUri)) {  
	            String docId = DocumentsContract.getDocumentId(imageUri);  
	            String[] split = docId.split(":");  
	            String type = split[0];  
	            Uri contentUri = null;  
	            if ("image".equals(type)) {  
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("video".equals(type)) {  
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("audio".equals(type)) {  
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
	            }  
	            String selection = MediaStore.Images.Media._ID + "=?";  
	            String[] selectionArgs = new String[] { split[1] };  
	            return getDataColumn(context, contentUri, selection, selectionArgs);  
	        }  
	    } // MediaStore (and general)  
	    else if ("content".equalsIgnoreCase(imageUri.getScheme())) {  
	        // Return the remote address  
	        if (isGooglePhotosUri(imageUri))  
	            return imageUri.getLastPathSegment();  
	        return getDataColumn(context, imageUri, null, null);  
	    }  
	    // File  
	    else if ("file".equalsIgnoreCase(imageUri.getScheme())) {  
	        return imageUri.getPath();  
	    }  
	    return null;  
	}  
	  
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {  
	    Cursor cursor = null;  
	    String column = MediaStore.Images.Media.DATA;  
	    String[] projection = { column };  
	    try {  
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);  
	        if (cursor != null && cursor.moveToFirst()) {  
	            int index = cursor.getColumnIndexOrThrow(column);  
	            return cursor.getString(index);  
	        }  
	    } finally {  
	        if (cursor != null)  
	            cursor.close();  
	    }  
	    return null;  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is ExternalStorageProvider. 
	 */  
	public static boolean isExternalStorageDocument(Uri uri) {  
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {  
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {  
	    return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is Google Photos. 
	 */  
	public static boolean isGooglePhotosUri(Uri uri) {  
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());  
	}
}
