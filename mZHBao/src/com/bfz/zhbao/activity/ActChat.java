package com.bfz.zhbao.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.Base.BaseFactory;
import com.bfz.zhbao.db.DBTools;
import com.bfz.zhbao.entity.ChatMessage;
import com.bfz.zhbao.service.MsgService;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.utils.RecordButton;
import com.bfz.zhbao.utils.RecordButton.OnFinishedRecordListener;

public class ActChat extends BaseActivity implements OnClickListener{

	private static final int IMAGE_CODE = 1;
	private static final String CHATLOG = "ChatLog";
	private static final String STATE = "OUT";
	private String userphone;
	private String opposite_jid;
	private String opposite_phone;
	private Button backBTN, sendBTN, picBTN;
	private TextView friendView;
	private RecordButton mRecordButton;
	private EditText msgText;
	private ChatMessage message;
	private Chat newchat;
	private Message smackMsg;
	private File file;
	private ListView listview;
	private DBTools tools;
	private List<ChatMessage> mMsgList;
	private Actadapter adapter;
	public static mHandler handler;
	private Presence mPrencese;
	private String PicPath;
	private static String MsgFrom;
	private static String MsgTo;

	/**
	 * 设置消息的来源
	 * @return
	 */
	public static String getMsgFrom() {
		
		return MsgFrom;
	}

	public static void setMsgFrom(String msgFrom) {

		MsgFrom = msgFrom;

	}

	public static String getMsgTo() {
		return MsgTo;
	}

	public static void setMsgTo(String msgTo) {
	
		MsgTo = msgTo;
	}
	@Override
	protected void onCreateMainView() {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.act_chat);
		 
	}
	@Override
	protected void onStart() {
		initialization();
		mMsgList = tools.queryAfter(tools.queryDb(userphone, opposite_phone));
		listview.setAdapter(adapter);
		adapter.setListMsg(mMsgList);
		adapter.notifyDataSetChanged();
		super.onStart();
	}

	
	
	@Override
	protected void onNewIntent(Intent intent) {
		initialization();
		mMsgList = tools.queryAfter(tools.queryDb(userphone, ActChat.getMsgFrom()));
		listview.setAdapter(adapter);
		adapter.setListMsg(mMsgList);
		adapter.notifyDataSetChanged();
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		finish();
		ActChat.setMsgFrom("");
		ActChat.setMsgTo("");
	
	}

	/**
	 * 初始化各个控件
	 */
	protected void initialization() {
		MyActivityManagerApplication.getInstance().addActivity(this);
		userphone = this.getIntent().getStringExtra("userphone");
		opposite_jid = this.getIntent().getStringExtra("opposite_jid");
		opposite_phone = this.getIntent().getStringExtra("opposite_phone");

		backBTN = (Button) findViewById(R.id.chat_back);
		backBTN.setOnClickListener(this);
		sendBTN = (Button) findViewById(R.id.btn_sendtext);
		sendBTN.setOnClickListener(this);

		picBTN = (Button) findViewById(R.id.btn_sendpic);
		picBTN.setOnClickListener(this);
		friendView = (TextView) findViewById(R.id.chat_name);
		message = new ChatMessage();
		msgText = (EditText) findViewById(R.id.message_text);
		friendView.setText(ActChat.getMsgFrom());
	

		newchat = BaseFactory.getChatManager().createChat(opposite_jid, new MsgService());
	
		handler = new mHandler();
		mMsgList = new ArrayList<ChatMessage>();

		tools = new DBTools(ActChat.this);
		listview = (ListView) findViewById(R.id.formclient_listview);

		listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		adapter = new Actadapter(ActChat.this);
		adapter.setListMsg(mMsgList);
		listview.setAdapter(adapter);

		mPrencese = BaseFactory.getRoster().getPresence(opposite_jid);
		SendVoiceMessage();
	}

	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.chat_back:
			BtnReturn();
			break;
		case R.id.btn_sendtext:
			SendTextMessage();
			break;
		case R.id.btn_sendpic:
			SendPictureMessage();
			
			break;

		}

	}

	public void BtnReturn() {
		ActChat.setMsgFrom("");
		finish();
	}

	public void SendVoiceMessage() {
		mRecordButton = (RecordButton) findViewById(R.id.btn_sendaudio);
		mRecordButton.setOnFinishedRecordListener(new OnFinishedRecordListener() {
			public void onFinishedRecord(String audioPath) {
				file = new File(audioPath);
				if (sendFile(file,"voice")) {
					Log.d(CHATLOG, "发送文件成功"+audioPath);
				} else {
					Log.d(CHATLOG, "发送文件失败");
				}
			}
		});
	}

	public void SendTextMessage() {
		smackMsg = new Message();
		
		smackMsg.setLanguage(STATE);
		smackMsg.setSubject("0");
		smackMsg.setBody(msgText.getText().toString());
		smackMsg.setFrom(userphone);
		smackMsg.setTo(opposite_phone);
	
		if(isOnline()){
			try {
				newchat.sendMessage(smackMsg);
		         System.out.println("发送的消息插入数据库：" + initializationChatMessage().toString());
				tools.insert(tools.EarlyInsert(initializationChatMessage()));
		        System.out.println(userphone+ "****" +opposite_phone +"查询相应的结果");
				mMsgList= tools.queryAfter(tools.queryDb(userphone, opposite_phone));
				listview.setAdapter(adapter);
				adapter.setListMsg(mMsgList);
				adapter.notifyDataSetChanged();
			} catch (XMPPException e) {
				Log.d(CHATLOG, "消息发送失败");
				e.printStackTrace();
			}
			msgText.setText("");
		}else{
			Toast.makeText(this, "对方不在线",Toast.LENGTH_SHORT).show();
		}
	}

	public void SendPictureMessage() {
		// 发送图片
		Intent intentPhoto = new Intent(Intent.ACTION_GET_CONTENT);
		intentPhoto.setType("image/*");
		this.startActivityForResult(intentPhoto,IMAGE_CODE);

	}

	

	public boolean sendFile(File sendfile ,String type) {
		
		if (isOnline()) {
			OutgoingFileTransfer outSend = BaseFactory.getFileTransferManager().createOutgoingFileTransfer(mPrencese.getFrom());
			try {

				initializationFileMessage(sendfile,type);
				StringBuilder Description = new StringBuilder();
				Description.append(type+"@"+userphone+"@"+opposite_phone);
			
				//String Description = type+"@"+userphone+"@"+opposite_phone;
				outSend.sendFile(sendfile, Description.toString());
				tools.insert(tools.EarlyInsert(initializationFileMessage(sendfile,type)));
				mMsgList =  tools.queryAfter(tools.queryDb(userphone, opposite_phone));
				adapter.setListMsg(mMsgList);
				adapter.notifyDataSetChanged();
			} catch (XMPPException e) {

				e.printStackTrace();
			}
			return true;
		} else {
			Toast.makeText(this, "对方不在线",Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/**
	 * 负责处理接收到的消息
	 * @author yangfan
	 *
	 */
	public class mHandler extends Handler{
		@Override
		public void handleMessage(android.os.Message msg) {
		
			switch(msg.what){
			case 0:
				ChatMessage text = (ChatMessage)msg.obj;
					listview.setAdapter(adapter);
					mMsgList.add(text);
					adapter.notifyDataSetChanged();
					tools.insert(tools.EarlyInsert(text));
				break;
			case 1:
				ChatMessage voiceMsg = (ChatMessage)msg.obj;
				listview.setAdapter(adapter);
				mMsgList.add(voiceMsg);
				adapter.notifyDataSetChanged();
				tools.insert(tools.EarlyInsert(voiceMsg));
				break;
			case 2:
				ChatMessage PicMsg = (ChatMessage)msg.obj;
				mMsgList.add(PicMsg);
				adapter.setListMsg(mMsgList);
				adapter.notifyDataSetChanged();
				tools.insert(tools.EarlyInsert(PicMsg));
				break;
			
			}
			
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		 super.onActivityResult(requestCode, resultCode, data);
	    ContentResolver resolver = this.getContentResolver();
		if(requestCode == IMAGE_CODE && data != null){
			  Bitmap bitmap = null;
				Uri originalUri = data.getData();
				BitmapFactory.decodeFile(originalUri.getPath());
				try {
					bitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
				    bitmap.toString();
			    String[] proj = {MediaStore.Images.Media.DATA};      
				Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
	            //按我个人理解 这个是获得用户选择的图片的索引值
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
	            cursor.moveToFirst();
	            //最后根据索引值获取图片路径
	            PicPath = cursor.getString(column_index);
	            File file = new File(PicPath);
	           sendFile(file,"picture");
	          
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	   
	}

	
	public boolean isOnline(){
		if (mPrencese.getType() == Presence.Type.available) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 对发送的文字消息进行封装
	 * 
	 */
	public ChatMessage initializationChatMessage(){
		message.setMsgBody(msgText.getText().toString());
		message.setMsgFrom(userphone);
		message.setMsgTo(opposite_phone);
		message.setMsgState(STATE);
		message.setMsgType("0");
		return message;
	}


	/**
	 * 对发送的文件进行封装
	 * 
	 */

	public ChatMessage initializationFileMessage(File sendfile ,String type){
		
		message.setMsgFrom(userphone);
		message.setMsgTo(opposite_phone);
		message.setMsgState(STATE);
		if("voice".equals(type)){
			message.setMsgType("1");
		}else{
			message.setMsgType("2");
		}
		message.setMsgPath(sendfile.getAbsolutePath());
		return message;
	}

	
} 
