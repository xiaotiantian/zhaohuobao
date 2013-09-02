package com.bfz.zhbao.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.jivesoftware.smack.util.Base64;

import com.bfz.zhbao.xmppManager.XmppConnection;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * 工具类
 */
public class Utils {
	/**
	 * 根据jid获取用户名
	 */
	public static String getJidToUsername(String jid){
		return jid.split("@")[0];
	}
	
	public static String getUserNameToJid(String username){
		return username + "@" + XmppConnection.SERVER_NAME;
	}
	
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density;
		} catch (Exception ex) {

		}
		return 1.0f;
	}
	
	/**
	 * <p>将文件转成base64 字符串</p>
	 * @param path 文件路径
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64File(String path) throws Exception
    {
		Log.v("录音文件路径", "123");
        File file = new File(path);
        InputStream inStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1)
        {
            outStream.write(buffer, 0, len);
            Log.v("录音文件路径", "789");
        }
        byte[] data = outStream.toByteArray();
        String mImage = new String(Base64.encodeBytes(data));
        
        outStream.close();
        inStream.close();
        Log.v("录音文件路径", "456");
        return mImage;
    }
	
	/**
	 * <p>将base64字符解码保存文件</p>
	 * @param base64Code
	 * @param path
	 * @throws Exception
	 */
	public static void decoderBase64File(String base64Code,String path) throws Exception {
		byte[] buffer = Base64.decode(base64Code);
		FileOutputStream out = new FileOutputStream(path);
		out.write(buffer);
		out.close();
	}
	
	public static byte[] decord(String base64Code,String path)throws Exception {
		byte[] buffer = Base64.decode(base64Code);
	    return buffer;	
	}
	public static String cutSentence(String string){
		String str[] = string.split("@");
		return str[0];
	}
}
