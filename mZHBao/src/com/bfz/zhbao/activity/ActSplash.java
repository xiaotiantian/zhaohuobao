package com.bfz.zhbao.activity;

import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.entity.IJsonClass;
import com.bfz.zhbao.service.MsgService;
import com.bfz.zhbao.utils.JsonToObjRemoteService;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.utils.RemoteService;
import com.bfz.zhbao.utils.Service.OnFaultHandler;
import com.bfz.zhbao.utils.Service.OnSuccessHandler;
import com.bfz.zhbao.utils.SysConfig;

/**
 * 程序的开始界面
 * @author yangfan
 *
 */
@SuppressLint("HandlerLeak")
public class ActSplash extends BaseActivity {

	private static TextView tvStart = null;
	private String imsi;
	private IJsonClass jclass;
	private Intent intentRegister;

	@Override
	protected void onCreateMainView() {
		initialization();
	   if (!SysConfig.NetWorkStatus(ActSplash.this)) {
			handler.sendEmptyMessage(0);
		}
		handlerserver();
	}
	
	
	/**
	 * 手机号为空，跳转至注册页面
	 * 
	 * @param imsi
	 * @param sp
	 * @param password
	 */
	public void imsiIsNotIn() {
		intentRegister = new Intent();
		intentRegister.setClass(this, ActLogin.class);
		intentRegister.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intentRegister.putExtra("imsi", jclass.imsi);
		intentRegister.putExtra("sp", jclass.sp);
		intentRegister.putExtra("pwd", jclass.pwd);
		this.startActivity(intentRegister);
	}

	/**
	 * 手机号码不为空获取手机号和密码在service中进行注册并且登录
	 * 
	 * @return
	 */
	public boolean setAndStartMainActivity() {
		String userphone = jclass.acc;
		String password = jclass.pwd;
		MsgService.setUsername(userphone);
		MsgService.setPassword(password);
		if (null != MsgService.getUsername() && null != MsgService.getPassword()) {
			startActivity(new Intent(ActSplash.this, ActMain.class).putExtra("userphone", userphone));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 该activity的初始化操作
	 */
	protected void initialization() {
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splashscreen);
		MyActivityManagerApplication.getInstance().addActivity(this);
		tvStart = (TextView) findViewById(R.id.tvStart);
		tvStart.setText("正在启动中.");
		jclass = new IJsonClass();
		TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// 获取手机的imsi码
		imsi = tm.getSubscriberId();

	}

	/**
	 * 用handler进行消息的处理 为0网络有问题。为3进行注册登录。为4跳转至注册页面先进行注册
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(ActSplash.this, "无法连接到网络，请检查网络设置！", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				tvStart.setText("正在启动中");
				break;
			case 2:
				tvStart.setText(tvStart.getText() + ".");
				break;

			case 3: {

				imsiIsNotIn();
				ActSplash.this.finish();
				break;
			}
			case 4: {
				if (setAndStartMainActivity()) {
					
					ActSplash.this.finish();
					
				} else {
					System.out.println("启动失败");
				}

				break;
			}

			}
		}
	};

public void handlerserver(){
	
	 final JsonToObjRemoteService<IJsonClass> serv = new JsonToObjRemoteService<IJsonClass>(SysConfig.ActionUrl);
		
		serv.setOnFaultHandler(new OnFaultHandler(){
			 
			public void onFault(Exception ex) {
				Toast.makeText(ActSplash.this, "服务器返回数据失败", Toast.LENGTH_SHORT).show();
				
			}});
		serv.setOnSuccessHandler(new OnSuccessHandler(){
	
			public void onSuccess(Object result) {
				Log.i("setr", "onSuccess");
				jclass = serv.JsonToObj(result,IJsonClass.class);
				
				if ("null".equals(jclass.acc)) {

					handler.sendEmptyMessage(3);

				} else {

					handler.sendEmptyMessage(4);
				}
		
			}});
		Map<String ,Object> map = new HashMap<String ,Object>();
	
		map.put("imsi", imsi);
		map.put("even", "LoginProve");
		map.put("eved", imsi);
		serv.setParams(map);
		serv.setRequestMethod(RemoteService.REQUEST_METHOD_POST);
		serv.asyncExecute();
		
    }	
}
