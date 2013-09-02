package com.bfz.zhbao.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

public class ActPutVerify extends BaseActivity implements OnClickListener{

	private static String imsi;
	private static String phone;
	private static String password;
	private EditText et ;
	private IJsonClass jclass;
	
	public static void setImsi(String imsi) {
		ActPutVerify.imsi = imsi;
	}

	public static void setPhone(String phone) {
		ActPutVerify.phone = phone;
	}

	public static void setPassword(String password) {
		ActPutVerify.password = password;
	}
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnok:
			
			if(password.equals(et.getText().toString())){
				handlerserver();  
				if(!("null".equals(jclass.acc)) && !("null".equals(jclass.pwd))){
					MsgService.setUsername(phone);
					MsgService.setPassword(password);
				    this.startActivity(new Intent(this,ActMain.class));
				  }else{
					  Toast.makeText(ActPutVerify.this, "账号注册出错", Toast.LENGTH_SHORT).show();
				  }
			break;
					
			}else{
				
				Toast.makeText(ActPutVerify.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
				break;
			}
			
		case R.id.btncancel:
			Intent intent = new Intent(this, ActGoods.class);
			startActivity(intent);
			finish();
			break;
		}
		
	}



	@Override
	protected void onCreateMainView() {
		  this.setContentView(R.layout.act_verify);
		  MyActivityManagerApplication.getInstance().addActivity(this);
		  et = (EditText)findViewById(R.id.verfy);
		  Button btnok,btncancel;
		  btnok = (Button)findViewById(R.id.btnok);
		  btnok.setOnClickListener(this);
		  btncancel = (Button)findViewById(R.id.btncancel);
		  btncancel.setOnClickListener(this);
		  jclass = new IJsonClass();
	}

	public void handlerserver(){

		 final JsonToObjRemoteService<IJsonClass> serv = new JsonToObjRemoteService<IJsonClass>(SysConfig.ActionUrl);
			serv.setOnFaultHandler(new OnFaultHandler(){
				
				public void onFault(Exception ex) {
					Toast.makeText(ActPutVerify.this, "服务器返回数据失败", Toast.LENGTH_SHORT).show();		
				        }
				     });
			serv.setOnSuccessHandler(new OnSuccessHandler(){
				public void onSuccess(Object result) {
					Log.i("setr", "onSuccess");
					jclass = serv.JsonToObj(result,IJsonClass.class);	
				}
	     });
			Map<String ,Object> map = new HashMap<String ,Object>();
			map.put("imsi", imsi);
			map.put("even", "UserRegister");
			map.put("eved", phone+","+et.getText().toString());
			
			serv.setParams(map);
			serv.setRequestMethod(RemoteService.REQUEST_METHOD_POST);
			try {
				serv.syncExecute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	   }	
}
