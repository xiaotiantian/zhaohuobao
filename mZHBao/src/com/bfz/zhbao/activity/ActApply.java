package com.bfz.zhbao.activity;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.xmppManager.XmppConnection;
import com.bfz.zhbao.xmppManager.XmppService;

public class ActApply extends BaseActivity{

	private String pGROUPNAME;//当前组
	XMPPConnection connection = XmppConnection.getConnection();
	public Roster roster = connection.getRoster();
	

	@Override
	protected void onCreateMainView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		setContentView(R.layout.act_apply);
		 
		MyActivityManagerApplication.getInstance().addActivity(this);
		TextView content=(TextView)findViewById(R.id.applyContent);
		Button agree=(Button)findViewById(R.id.applyAgree);
		Button reject=(Button)findViewById(R.id.applyReject);
		
		Intent intent=getIntent();
		final String fromUserJid=intent.getStringExtra("fromUserJid");
		final String pUSERID=intent.getStringExtra("pUSERID");
	
		
		content.setText("【"+fromUserJid+"】向你发来好友申请，是否添加对方为好友?");
		agree.setOnClickListener(new OnClickListener() {//同意添加
			
			public void onClick(View v) {
				  System.out.println("pGROUPNAME是："+pGROUPNAME);
                  if(pGROUPNAME == null){
                 	 pGROUPNAME = "我的好友";
                  }
                  XmppService.addUsers(roster, fromUserJid,null, pGROUPNAME);
                //被邀请人发送一个邀请请求，将subscription改为both
         	      Presence subscription = new Presence(Presence.Type.subscribed);
                  subscription.setTo(fromUserJid);
                  subscription.setFrom(pUSERID);
                  XmppConnection.getConnection().sendPacket(subscription);
                  finish();
			}
		});
		
		reject.setOnClickListener(new OnClickListener() {//拒绝添加
			public void onClick(View v) {
				XmppService.removeUser(roster, fromUserJid);
				finish();
			}
		});
		
	}
	
}
