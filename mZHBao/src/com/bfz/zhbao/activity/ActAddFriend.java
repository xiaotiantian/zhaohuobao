package com.bfz.zhbao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.utils.DialogAll;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.xmppManager.XmppConnection;
import com.bfz.zhbao.xmppManager.XmppService;


@SuppressWarnings("all")
public class ActAddFriend extends Activity{
	
	private String pUSERID;//当前用户
	private Button search_button;
	private Button goback_button;
	private String nameResult="";
	private String friName;
	private ListView list;
	DialogAll addFridialog;
	View dialogView;
	Iterator<Row> it=null;
	Roster roster = XmppConnection.getConnection().getRoster();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_add_friend);
		MyActivityManagerApplication.getInstance().addActivity(this);
		pUSERID = getIntent().getStringExtra("USERID");
		list = (ListView) findViewById(R.id.searchlist);
		
		search_button = (Button) findViewById(R.id.search_button);
		search_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				  searchFriend();
				}
		});
		//返回按钮
		goback_button = (Button) findViewById(R.id.goback_button);
		goback_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					
				finish();
			}			
		});
		
		dialogView = View.inflate(this,R.layout.dialog_present, null);
		addFridialog=new DialogAll((RelativeLayout)findViewById(R.id.actAddFriHead),"加为好友") {
			
			@Override
			protected void defaultOperate(TextView contentText,Button sure, Button cancle) {
				contentText.setText("您确定要将【 "+friName+" 】\n加为好友吗？");
				sure.setText("添加");
				cancle.setText("取消");
			}

			@Override
			protected void responseClick(Boolean isSureChecked) {
				// TODO Auto-generated method stub
				if(isSureChecked){
					 Roster roster = XmppConnection.getConnection().getRoster();
		         	 String userJid = friName+"@"+XmppConnection.getConnection().getServiceName();
		         	 //默认添加到【我的好友】分组
		         	 String groupName = "我的好友";
		         	 XmppService.addUsers(roster, userJid, friName, groupName);
		         	 Presence subscription = new Presence(Presence.Type.subscribe);
		             subscription.setTo(userJid);
		             subscription.setFrom(pUSERID);
		             XmppConnection.getConnection().sendPacket(subscription);
		     	     
		     	     Intent intent = new Intent();
		     		 intent.putExtra("USERID", pUSERID);
		     		 intent.putExtra("GROUPNAME", groupName);
		     		 ActMain.tabHost.setCurrentTab(2);
		     		 intent.setClass(ActAddFriend.this, ActMain.class);
		     		 startActivity(intent);	
				}
			
			}

			@Override
			protected void responseCheck(int checkedId) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	//查询好友
	public void searchFriend() {	
		String search_text = ((EditText) findViewById(R.id.search_text)).getText().toString();
		if (search_text.equals("")) {
			Toast.makeText(ActAddFriend.this, "输入信息不能为空！", Toast.LENGTH_SHORT).show();
		} else {
			try{
				//建立连接
				XMPPConnection connection = XmppConnection.getConnection();
				//声明查询管理器
				UserSearchManager search = new UserSearchManager(connection);
				//此处一定要加上 search.
				//得到查询表格+connection.getServiceName()
				Form searchForm = search.getSearchForm("search." + connection.getServiceName());
				//得到查询表格AnswerForm
				Form answerForm = searchForm.createAnswerForm();
				//给AnswerForm表格添加查询项
				answerForm.setAnswer("Username", true);
				answerForm.setAnswer("search", search_text.toString().trim());
				//提交查询表格，并返回ReportedData结果
				ReportedData data = search.getSearchResults(answerForm,"search."+connection.getServiceName());					
				it = data.getRows();
				
			}catch(Exception e){
				Toast.makeText(ActAddFriend.this,e.getMessage()+" "+e.getClass().toString(), Toast.LENGTH_SHORT).show();
			}
			
			if(it.hasNext()){
				// 生成动态数组，加入数据
				ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
				while(it.hasNext()){
				    HashMap<String, Object> map = new HashMap<String, Object>();	     
				    map.put("name", it.next().getValues("Username").next().toString()); //会员昵称
					listItem.add(map);
				}
				// 生成适配器的Item和动态数组对应的元素
				SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
						R.layout.add_friend_listitem,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
						new String[] { "name"/*,"address","button"*/ },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.usrName/*,R.id.addressInfo,R.id.addAsFriend*/});
				// 添加并且显示查询好友列表
				list.setAdapter(listItemAdapter);
				// 添加短点击事件
				list.setOnItemClickListener(new OnItemClickListener(){
					public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
						HashMap<String, String> map = (HashMap<String, String>) list.getItemAtPosition(position);
						friName = map.get("name");
						
						addFridialog.openDialog(DialogAll.Type.PRESENT,dialogView);
					}
				});
				}else{
				  Toast.makeText(ActAddFriend.this, "此用户不存在，请确保输入的信息正确！", Toast.LENGTH_SHORT).show();
			  }
		}
	}
	
}
