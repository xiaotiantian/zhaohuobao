package com.bfz.zhbao.service;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseFactory;
import com.bfz.zhbao.activity.ActApply;
import com.bfz.zhbao.activity.ActChat;
import com.bfz.zhbao.activity.ActMain;
import com.bfz.zhbao.db.DBTools;
import com.bfz.zhbao.entity.ChatMessage;
import com.bfz.zhbao.utils.Utils;
import com.bfz.zhbao.xmppManager.XmppConnection;
/**
 * 基础的service
 * @author yangfan
 *
 */
public class MsgService extends Service implements ChatStateListener{
	// 通知管理器
	private NotificationManager notiManager;

	private Roster roster;
	
	private IncomingFileTransfer incomefile;
	private File file;
	private DBTools tools;
    public static  String username;
    public static String password;
    
	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		MsgService.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		MsgService.password = password;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("Serivce is on create " + getPassword() + getUsername());
		 initialization();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		xmppInit();
		xmppListeners();
		tools = new DBTools(MsgService.this);
		super.onStart(intent, startId);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}



	/**
	 * 初始化后台服务
	 */
	private void xmppInit() {

			if(login(username,password)){
				System.out.println("登录成功");
			}else{
				System.out.println("登录失败，注册并且登录");
				registered(username,password);
			}
	}

	/**
	 * 初始化消息监听和文件的监听； 含 聊天消息监听、文件传输监听、好友状态监听。
	 */
	private void xmppListeners() {

		BaseFactory.getChatManager().addChatListener(new ChatManagerListener() {

						public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener(){

					public void processMessage(Chat chat, Message message) {

						String body = message.getBody();
						
						String from = message.getFrom();
					
						String to = message.getTo();
			
						ChatMessage receiveTextMsg = new ChatMessage();
						
						receiveTextMsg.setMsgBody(body);
						receiveTextMsg.setMsgFrom(Utils.cutSentence(from));
						receiveTextMsg.setMsgTo(Utils.cutSentence(to));
						
						receiveTextMsg.setMsgType("0");
						receiveTextMsg.setMsgState("IN");
						android.os.Message fontMsg = new android.os.Message();
						fontMsg.what = 0;
						fontMsg.obj = receiveTextMsg;
						
						if(checkChatActivity(Utils.cutSentence(from),Utils.cutSentence(to))){
							ActChat.handler.sendMessage(fontMsg);
				    	}else{
				    		tools.insert(tools.EarlyInsert(receiveTextMsg));
				    		setNotiType(R.drawable.logo,Utils.cutSentence(from),"有新消息",Utils.cutSentence(from),Utils.cutSentence(to));
				    	}
						
					}
					
				});
			}
		});

		BaseFactory.getFileTransferManager().addFileTransferListener(new FileTransferListener() {

			public void fileTransferRequest(FileTransferRequest request) {

				String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +request.getFileName();
				String Description = request.getDescription();
				String[] str = Description.split("@");
				String type = str[0];
				String requestFrom = str[1];
				String requestTo = str[2];
				incomefile = request.accept();
				file = new File(filename);
				try {
					incomefile.recieveFile(file);
					Log.d("file", "文件接收成功");
				} catch (XMPPException e) {
		
					e.printStackTrace();
				}
				
				ChatMessage VPMsg = new ChatMessage();
				VPMsg.setMsgFrom(requestFrom);
				VPMsg.setMsgTo(requestTo);
				if("voice".equals(type)){	
					VPMsg.setMsgType("1");
				}else{
					VPMsg.setMsgType("2");
				}
				VPMsg.setMsgPath(filename);
				VPMsg.setMsgState("IN");
				
				android.os.Message fileMsg = new android.os.Message();
				if(VPMsg.getMsgType().equals("1")){
					
					fileMsg.what=1;
					
				}else{
					fileMsg.what=2;
				}
				fileMsg.obj = VPMsg;
				if(checkChatActivity(requestFrom,requestTo)){
					
					ActChat.handler.sendMessage(fileMsg);
					
				}else{
					tools.insert(tools.EarlyInsert(VPMsg));
					setNotiType(R.drawable.logo,requestFrom,"有新消息",requestFrom,requestTo);
				}
				
			}
		});
		
		//好友状态监听
		
		  roster.addRosterListener(  
             new RosterListener() {   
            //监听好友申请消息
            public void entriesAdded(Collection<String> invites) {  
            	String[] fromUserJid = new String[invites.size()];
            	int i=0;
				for (Iterator<String> iter = invites.iterator(); iter.hasNext()&i<invites.size();i++) {
					fromUserJid[i] = (String)iter.next();
					
					RosterEntry rosterEntry = roster.getEntry(fromUserJid[i]);
					String itemType=rosterEntry.getType().toString();
					if(itemType.equals("from")){
						//通知栏显示申请消息，然后打开对话框activity
	           		  	applyNotification(getUsername(),fromUserJid[i]);
					}
         	   }     
            }    
             
            //监听好友更新列表的消息
            public void entriesUpdated(Collection<String> invites) {  
            	   String[] toUserJid = new String[invites.size()];
            	   
            	    int i=0;
					for (Iterator<String> iter = invites.iterator(); iter.hasNext()&i<invites.size();i++) {
						toUserJid[i]= (String)iter.next();
						
						Presence presence=roster.getPresence(toUserJid[i]);
						String type=presence.getType().toString();
						
						RosterEntry rosterEntry = roster.getEntry(toUserJid[i]);
						String itemType=rosterEntry.getType().toString();
						
						
						if(itemType.equals("to")){
						
						}
						else if(itemType.equals("both")){
							if(type.equals("unavailable")){
								
							}
							else if(type.equals("available")){
								if(rosterEntry.getGroups().isEmpty()){
									
								}else{
								
									acceptNotification(toUserJid[i]);
								}
							}
						}
        	    }               	    
            } 

			//监听好友删除消息(不提示)
            public void entriesDeleted(Collection<String> delFriends) {  
            
            	String[] delFriName = new String[delFriends.size()];
            	String usrName=getUsername();
            	int i=0;
            	for (Iterator<String> iter = delFriends.iterator(); iter.hasNext()&i<delFriends.size();i++) {
            		delFriName[i]= (String)iter.next();
        	    }  
            } 
          
            //监听好友状态改变消息
            public void presenceChanged(Presence presence) {  
        	   // friendMood = presence.getStatus();

            }    
	    }); 
		
	}
		//好友申请的通知栏
		protected void applyNotification(String pUSERID, String fromUser) {
			NotificationManager mNotificationManager= (NotificationManager) this.getSystemService(Service.NOTIFICATION_SERVICE);
			Intent intent = new Intent(MsgService.this,ActApply.class);
			intent.putExtra("pUSERID", pUSERID);
			intent.putExtra("fromUserJid", fromUser);
			PendingIntent appIntent = PendingIntent.getActivity(this, 0, intent, 0);
			
			Notification myNoti = new Notification();
			myNoti.icon =R.drawable.ic_launcher;
			myNoti.defaults = Notification.DEFAULT_SOUND;
			myNoti.flags = Notification.FLAG_AUTO_CANCEL;
			myNoti.setLatestEventInfo(this, "好友申请消息", fromUser+"申请成为您的好友", appIntent);
			mNotificationManager.notify(0, myNoti);
		}
		//好友同意的通知栏
		protected void acceptNotification(String string) {
			NotificationManager mNotificationManager= (NotificationManager) this.getSystemService(Service.NOTIFICATION_SERVICE);
			PendingIntent appIntent = PendingIntent.getActivity(this, 0, null, 0);
			
			Notification myNoti = new Notification();
			myNoti.icon =R.drawable.ic_launcher;
			myNoti.defaults = Notification.DEFAULT_SOUND;
			myNoti.flags = Notification.FLAG_AUTO_CANCEL;
			myNoti.setLatestEventInfo(this, "申请同意消息", string+"通过了您的好友申请", appIntent);
			mNotificationManager.notify(0, myNoti);	
		}

	/**
	 * 用户注册并登录
	 * @param accounts
	 * @param password
	 */
	private void registered(String accounts, String password) {
	
		String nickname = "";
		String email = "";
		registered(accounts, password, nickname, email);
	}
	/**
	 * 用户注册并登录
	 * 
	 * @param accounts
	 * @param password
	 * @param name
	 * @param email
	 */
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
	
		collector.cancel();

		if (result == null) {

			Toast.makeText(getApplicationContext(), "服务器没有返回结果", Toast.LENGTH_SHORT).show();

		} else if (result.getType() == IQ.Type.ERROR) {

			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				
				System.out.println("用户名:" + accounts + "密码:" + password + ";账号已经存在");
				
			} else {
				
				System.out.println("用户名:" + accounts + "密码:" + password + ";注册失败");
				
			}
		} else if (result.getType() == IQ.Type.RESULT) {
			try {
				XmppConnection.getConnection().login(accounts, password);
				Presence presence = new Presence(Presence.Type.available);
				XmppConnection.getConnection().sendPacket(presence);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 用户登录（提交账号密码信息到服务器）
	 */
	private boolean login(String accounts, String password) {
			try {
					XmppConnection.getConnection().login(accounts, password);
					Presence presence = new Presence(Presence.Type.available);
					XmppConnection.getConnection().sendPacket(presence);
					Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
					goMainAvtivvity();
					return true;
			} catch (XMPPException e) {
				XmppConnection.closeConnection();
				Log.i("login", "登录异常！" + e.toString());
				return false;
			}
		}

	
	
	protected void setNotiType(int iconId, String title, String msg,String from,String to) {
		Intent intent = new Intent();
		intent.setClass(this, ActChat.class);
		ActChat.setMsgFrom(Utils.cutSentence(from));
		ActChat.setMsgTo(Utils.cutSentence(to));
		
		intent.putExtra("userphone", to);
		intent.putExtra("opposite_jid", from+"@langbao/Smack");
		intent.putExtra("opposite_phone", from);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent appIntent = PendingIntent.getActivity(this, 0, intent, 0);

		Notification myNoti = new Notification();
		myNoti.icon = iconId;
		myNoti.tickerText = msg;
		myNoti.defaults = Notification.DEFAULT_SOUND;
		myNoti.flags |= Notification.FLAG_AUTO_CANCEL;
		myNoti.setLatestEventInfo(this, title, msg, appIntent);
		notiManager.notify(0, myNoti);
	}

	public void processMessage(Chat arg0, Message arg1) {
	
	
	}
	
	/**  
	 *  初始化操作
	 */
	private void  initialization(){
		notiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		 roster = BaseFactory.getRoster();
	}


	private void goMainAvtivvity(){
		Intent intent = new Intent();
		intent.putExtra("userphone", username);
		intent.setClass(this, ActMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
		this.startActivity(intent);
	}
	
	private boolean checkChatActivity(String from,String to){
		if( (from.equals(ActChat.getMsgFrom())) && (to.equals(ActChat.getMsgTo())) ){
			return true;
		}else{
			return false;
		}
	}

	public void stateChanged(Chat arg0, ChatState arg1) {
		
		
	}

}
