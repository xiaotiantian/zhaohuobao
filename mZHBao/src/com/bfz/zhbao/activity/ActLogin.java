package com.bfz.zhbao.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.entity.IJsonClass;
import com.bfz.zhbao.utils.JsonToObjRemoteService;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.utils.RemoteService;
import com.bfz.zhbao.utils.SysConfig;
import com.bfz.zhbao.utils.Service.OnFaultHandler;
import com.bfz.zhbao.utils.Service.OnSuccessHandler;
import com.bfz.zhbao.xmppManager.XmppConnection;

@SuppressLint("HandlerLeak")
public class ActLogin extends BaseActivity implements OnClickListener {

	private EditText etPhone;
	private  String uImsi;
	private  String uPhone;
	private  String uPwd;
	private  String uSp;
	private Dialog mDialog;
	private IJsonClass jclass;
	//public  static SmsRecevier receiver;
	private Thread t1 = new SMSObsrver();
	 


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		etPhone.requestFocus();
		Timer timer = new Timer(); // 设置定时器
		timer.schedule(new TimerTask() {
			@Override
			public void run() { // 弹出软键盘的代码
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(etPhone, InputMethodManager.RESULT_HIDDEN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300); // 设置300毫秒的时长
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.toRegister:
			// etPhone.requestFocus();
			uPhone = etPhone.getText().toString().trim();
			onVerify(uPhone);
			break;

		case R.id.onTrial:
			Intent intent = new Intent(this, ActGoods.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	/*
	 * 验证
	 */
	protected void onVerify(final String uPhone) {
		if (uPhone.length() != 11 || !PhoneNumberUtils.isGlobalPhoneNumber(uPhone)) {
			Toast.makeText(ActLogin.this, "您输入的电话号码不正确！", Toast.LENGTH_SHORT).show();
			return;
		}

		/**
		 * 第二次验证将手机号密码发送至服务器之后开启短信监听
		 */
		handlerserver();
		//SysConfig.PostMsg(uImsi, "PhoneProve", uPhone+","+uPwd);
	
		/*IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");  
		filter.setPriority(Integer.MAX_VALUE);
	
		receiver = new SmsRecevier(); 
		
		registerReceiver(receiver, filter);*/
	


	    t1.start();	 
		showRoundProcessDialog(this,R.layout.progress);
	}

	/*public   void stopListenter(){
		
	    unregisterReceiver(receiver);
	}*/
	public void SkipActivity(){
		Intent intent = new Intent();
		ActPutVerify.setImsi(uImsi);
		ActPutVerify.setPassword(uPwd);
		ActPutVerify.setPhone(uPhone);
	
		intent.setClass(this, ActPutVerify.class);
        this.startActivity(intent);
	}

	public class SMSObsrver extends Thread{

		public void run() {
		
		  try {
			
			this.sleep(5000);
			mDialog.dismiss();
		
		//	stopListenter();
			SkipActivity();
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		  
		}
		
	   }
	@Override
	protected void onDestroy() {
	
		super.onDestroy();
	}
	 public void showRoundProcessDialog(Context mContext, int layout)
	    {
	        OnKeyListener keyListener = new OnKeyListener()
	        {
	            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	            {
	                if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_SEARCH)
	                {
	                    return true;
	                }
	                return false;
	            }
	        };

	        mDialog = new AlertDialog.Builder(mContext).create();
	        mDialog.setOnKeyListener(keyListener);
	        mDialog.show();
	        mDialog.setContentView(layout);
	       
	    }
	
    
/*	public class SmsRecevier extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))  {
				
				System.out.println("接收到了消息。。。。。");

					t1.interrupt();
				
				Object[] pudces = (Object[]) intent.getExtras().get("pdus");

				 for(Object pudc : pudces){
					 byte[] pudcMessage = (byte[]) pudc;

					 SmsMessage sms = SmsMessage.createFromPdu(pudcMessage);
			            //获取短信内容
			            String content = sms.getMessageBody().substring(0, 6);
			            //获取短信发送地址
			           // String phone = sms.getOriginatingAddress();
		            if(content.equals(uPwd)){
							String imsi = jclass.imsi;
							String acc = jclass.acc;
							String pwd = jclass.pwd;
							String sp = jclass.sp;
							if(verfyLoginInformation(imsi,acc,pwd,sp)){
								registered(acc,pwd,"","");
								mDialog.dismiss();
							}else{
								System.out.println("注册失败");
							}
		            }
			  }
				
		  }
		
		}
		
	}
	/**
	 * 用户注册并登录
	 * 
	 * @param accounts
	 * @param password
	 * @param name
	 * @param email
	 *//*
	private void registered(String accounts, String password, String name, String email) {

		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(XmppConnection.getConnection().getServiceName());
		reg.setUsername(accounts);
		reg.setPassword(password);
		reg.addAttribute("name", name);
		reg.addAttribute("email", email);
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = XmppConnection.getConnection().createPacketCollector(filter);
		XmppConnection.getConnection().sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）

		if (result == null) {

			System.out.println("用户名:" + accounts + "密码:" + password + ";注册时,服务器没有返回结果");
			

		} else if (result.getType() == IQ.Type.ERROR) {

			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {

				Toast.makeText(ActLogin.this, "账户已经存在", Toast.LENGTH_SHORT).show();
				
			} else {

				System.out.println("用户名:" + accounts + "密码:" + password + ";注册失败");
				

			}
		} else if (result.getType() == IQ.Type.RESULT) {
			try {
				XmppConnection.getConnection().login(accounts, password);
				Presence presence = new Presence(Presence.Type.available);
				XmppConnection.getConnection().sendPacket(presence);
				Intent intent = new Intent();
				intent.setAction("com.bfz.zhbao.activity.ActMain");
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}

	}
	
private boolean verfyLoginInformation(String imsi,String phone,String pwd,String sp){
	if (imsi.equals(uImsi)&&phone.equals(uPhone)&&pwd.equals(uPwd)&&sp.equals(uSp)){
		return true;
	}
	return false;
}*/

@Override
protected void onCreateMainView() {
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.firststart);
	 
	MyActivityManagerApplication.getInstance().addActivity(this);
	final Button btnRegister = (Button) findViewById(R.id.toRegister);
	btnRegister.setOnClickListener(this);
	final Button btnTrial = (Button) findViewById(R.id.onTrial);
	btnTrial.setOnClickListener(this);

	uImsi = this.getIntent().getStringExtra("imsi");
	uPwd = this.getIntent().getStringExtra("pwd");
	uSp = this.getIntent().getStringExtra("sp");
	
	etPhone = (EditText) findViewById(R.id.uPhone);
	etPhone.clearFocus();
}

public void handlerserver(){
	
	 final JsonToObjRemoteService<IJsonClass> serv = new JsonToObjRemoteService<IJsonClass>(SysConfig.ActionUrl);
		
		serv.setOnFaultHandler(new OnFaultHandler(){
			 
			public void onFault(Exception ex) {
				Toast.makeText(ActLogin.this, "服务器返回数据失败", Toast.LENGTH_SHORT).show();
			}});
		serv.setOnSuccessHandler(new OnSuccessHandler(){
	
			public void onSuccess(Object result) {
				jclass = serv.JsonToObj(result,IJsonClass.class);		
			}});
		Map<String ,Object> map = new HashMap<String ,Object>();
		map.put("imsi", uImsi);
		map.put("even", "PhoneProve");
		map.put("eved", uPhone+","+uPwd);

		serv.setParams(map);
		serv.setRequestMethod(RemoteService.REQUEST_METHOD_POST);
		serv.asyncExecute();
		
   }	
}
