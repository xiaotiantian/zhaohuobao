package com.bfz.zhbao.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class UpdateManager {

	private static Context mContext;
	private static String mUpdateUrl;
	private final static String TAG = "Update：";
	private int apkVer = 0;
	private static String apkUrl = null;
	private static String apkMsg = null;
	private static Boolean hasUpdate = false;

	/* 下载包安装路径 */
	private static final String savePath = Environment.getExternalStorageDirectory().getPath() + "/";
	private static final String saveFileName = "update.apk";

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated
			// method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(mContext, "无法连接到网络，请检查网络设置！", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				if (hasUpdate)
					Toast.makeText(mContext, "已是最新版本，无需更新！", Toast.LENGTH_SHORT).show();
				break;
			case 2:
			// 对话框通知用户升级程序
			{
				AlertDialog.Builder builer = new Builder(mContext).setTitle("版本升级").setMessage(apkMsg);
				// 当点确定按钮时从服务器上下载 新的apk 然后安装
				builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Log.i(TAG, "下载apk,更新");
						downLoadFile();
					}
				});
				// 当点取消按钮时进行登录
				builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				});
				AlertDialog dialog = builer.create();
				dialog.show();
			}
				break;
			case 3:
				// sdcard不可用
				Toast.makeText(mContext, "SD卡不可用", Toast.LENGTH_LONG).show();
				break;
			case 4: // 下载apk失败
				Toast.makeText(mContext, "新版本下载失败", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	public UpdateManager(Context context) {
		mContext = context;
		mUpdateUrl = SysConfig.updateUrl;
		hasUpdate = false;
	}

	public UpdateManager(Context context, String updateUrl) {
		mContext = context;
		mUpdateUrl = updateUrl;
		hasUpdate = true;
	}

	// 外部接口让主Activity调用
	public void checkUpdate() {
		if (SysConfig.NetWorkStatus(mContext)) {

			new Thread() {
				@Override
				public void run() {
					try {
						int locVer = mContext.getPackageManager().getPackageInfo(SysConfig.PREFS_NAME, 0).versionCode;
						SAXParse(mUpdateUrl);
						if (apkVer <= locVer) {
							Log.i(TAG, "版本号相同,无需升级");
							handler.sendEmptyMessage(1);
						} else {
							Log.i(TAG, "版本号不同 ,提示升级 ");
							handler.sendEmptyMessage(2);
						}
					} catch (Exception e) {
						Log.e(TAG, e.getMessage());
					}
				}
			}.start();
		} else {
			handler.sendEmptyMessage(0);
		}
	}

	private void SAXParse(String updateUrl) {

		RootElement root = new RootElement("info");
		// Element item = root.getChild("NODE");
		root.getChild("version").setEndTextElementListener(new EndTextElementListener() {

			public void end(String body) {

				apkVer = Integer.parseInt(body);
			}
		});
		root.getChild("url").setEndTextElementListener(new EndTextElementListener() {

			public void end(String body) {

				// TODO Auto-generated method stub
				apkUrl = body;
			}
		});
		root.getChild("description").setEndTextElementListener(new EndTextElementListener() {

			public void end(String body) {

				// TODO Auto-generated method stub
				apkMsg = body;
			}
		});
		try {
			// 从资源文件获取服务器地址包装成url的对象
			URL url = new URL(updateUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			InputStream inputStream = conn.getInputStream();
			// 对xml进行解释
			Xml.parse(inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
			inputStream.close();
		} catch (Exception e) {
			// e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadFile() {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			handler.sendEmptyMessage(3);
		} else {

			final ProgressDialog pDialog; // 进度条对话框
			pDialog = new ProgressDialog(mContext);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setTitle("版本升级");
			pDialog.setMessage("正在下载更新");
			pDialog.setCancelable(false);// 禁止取消
			pDialog.show();

			new Thread() {
				@Override
				public void run() {
					try {
						File file = downFileProgress(apkUrl, pDialog);
						sleep(500);
						installApk(file);
						pDialog.dismiss(); // 结束掉进度条对话框
					} catch (Exception e) {
						handler.sendEmptyMessage(4);
					}
				}
			}.start();
		}
	}

	/**
	 * 从服务器下载apk
	 * 
	 * @param apkUpl
	 * @param pDialog
	 * @return
	 * @throws Exception
	 */
	private static File downFileProgress(String apkUpl, ProgressDialog pDialog) throws Exception {

		URL url = new URL(apkUpl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		// 获取到文件的大小
		pDialog.setMax(conn.getContentLength());
		InputStream is = conn.getInputStream();
		
		File file = new File(savePath, saveFileName);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] buffer = new byte[1024];
		int len;
		int total = 0;
		while ((len = bis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
			total += len;
			// 获取当前下载量
			pDialog.setProgress(total);
		}
		fos.close();
		bis.close();
		is.close();
		return file;
	}

	/**
	 * 安装apk
	 */
	protected static void installApk(File file) {

		((Activity) mContext).finish();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW); // 执行动作
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		mContext.startActivity(intent);

		// Uri packageURI = Uri.parse("package:com.hger.zhbao");
		// Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		// mContext.startActivity(uninstallIntent);
	}

}