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
 * ����Ŀ�ʼ����
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
	 * �ֻ���Ϊ�գ���ת��ע��ҳ��
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
	 * �ֻ����벻Ϊ�ջ�ȡ�ֻ��ź�������service�н���ע�Ტ�ҵ�¼
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
	 * ��activity�ĳ�ʼ������
	 */
	protected void initialization() {
		// �����ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splashscreen);
		MyActivityManagerApplication.getInstance().addActivity(this);
		tvStart = (TextView) findViewById(R.id.tvStart);
		tvStart.setText("����������.");
		jclass = new IJsonClass();
		TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// ��ȡ�ֻ���imsi��
		imsi = tm.getSubscriberId();

	}

	/**
	 * ��handler������Ϣ�Ĵ��� Ϊ0���������⡣Ϊ3����ע���¼��Ϊ4��ת��ע��ҳ���Ƚ���ע��
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(ActSplash.this, "�޷����ӵ����磬�����������ã�", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				tvStart.setText("����������");
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
					System.out.println("����ʧ��");
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
				Toast.makeText(ActSplash.this, "��������������ʧ��", Toast.LENGTH_SHORT).show();
				
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
