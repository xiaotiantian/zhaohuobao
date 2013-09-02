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
	private final static String TAG = "Update��";
	private int apkVer = 0;
	private static String apkUrl = null;
	private static String apkMsg = null;
	private static Boolean hasUpdate = false;

	/* ���ذ���װ·�� */
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
				Toast.makeText(mContext, "�޷����ӵ����磬�����������ã�", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				if (hasUpdate)
					Toast.makeText(mContext, "�������°汾��������£�", Toast.LENGTH_SHORT).show();
				break;
			case 2:
			// �Ի���֪ͨ�û���������
			{
				AlertDialog.Builder builer = new Builder(mContext).setTitle("�汾����").setMessage(apkMsg);
				// ����ȷ����ťʱ�ӷ����������� �µ�apk Ȼ��װ
				builer.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Log.i(TAG, "����apk,����");
						downLoadFile();
					}
				});
				// ����ȡ����ťʱ���е�¼
				builer.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
				// sdcard������
				Toast.makeText(mContext, "SD��������", Toast.LENGTH_LONG).show();
				break;
			case 4: // ����apkʧ��
				Toast.makeText(mContext, "�°汾����ʧ��", Toast.LENGTH_LONG).show();
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

	// �ⲿ�ӿ�����Activity����
	public void checkUpdate() {
		if (SysConfig.NetWorkStatus(mContext)) {

			new Thread() {
				@Override
				public void run() {
					try {
						int locVer = mContext.getPackageManager().getPackageInfo(SysConfig.PREFS_NAME, 0).versionCode;
						SAXParse(mUpdateUrl);
						if (apkVer <= locVer) {
							Log.i(TAG, "�汾����ͬ,��������");
							handler.sendEmptyMessage(1);
						} else {
							Log.i(TAG, "�汾�Ų�ͬ ,��ʾ���� ");
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
			// ����Դ�ļ���ȡ��������ַ��װ��url�Ķ���
			URL url = new URL(updateUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			InputStream inputStream = conn.getInputStream();
			// ��xml���н���
			Xml.parse(inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
			inputStream.close();
		} catch (Exception e) {
			// e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}

	/*
	 * �ӷ�����������APK
	 */
	protected void downLoadFile() {

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			handler.sendEmptyMessage(3);
		} else {

			final ProgressDialog pDialog; // �������Ի���
			pDialog = new ProgressDialog(mContext);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setTitle("�汾����");
			pDialog.setMessage("�������ظ���");
			pDialog.setCancelable(false);// ��ֹȡ��
			pDialog.show();

			new Thread() {
				@Override
				public void run() {
					try {
						File file = downFileProgress(apkUrl, pDialog);
						sleep(500);
						installApk(file);
						pDialog.dismiss(); // �������������Ի���
					} catch (Exception e) {
						handler.sendEmptyMessage(4);
					}
				}
			}.start();
		}
	}

	/**
	 * �ӷ���������apk
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
		// ��ȡ���ļ��Ĵ�С
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
			// ��ȡ��ǰ������
			pDialog.setProgress(total);
		}
		fos.close();
		bis.close();
		is.close();
		return file;
	}

	/**
	 * ��װapk
	 */
	protected static void installApk(File file) {

		((Activity) mContext).finish();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW); // ִ�ж���
		// ִ�е���������
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		mContext.startActivity(intent);

		// Uri packageURI = Uri.parse("package:com.hger.zhbao");
		// Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		// mContext.startActivity(uninstallIntent);
	}

}