package com.bfz.zhbao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.utils.DialogAll;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.xmppManager.XmppConnection;
import com.bfz.zhbao.xmppManager.XmppService;

public class ActAddButton extends Activity {
	private XMPPConnection connection = XmppConnection.getConnection();
	final private Roster roster = connection.getRoster();
	private String pUSERID=connection.getUser();
	private ListView listV=null;
	View view;
	private SimpleAdapter listItemAdapter=null;
	public static final String CHECK = null;
	private String[] itemNames=new String[]{
			"添加好友","添加分组"
	};
	private int[] itemImages=new int[]{
			R.drawable.add_friend,R.drawable.add_group
	};
	private int[] itemTag=new int[]{R.drawable.tag};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_add_button);
		MyActivityManagerApplication.getInstance().addActivity(this);
		
		view = View.inflate(this,R.layout.dialog_input, null);
		final DialogAll addGroupdialog=new DialogAll((RelativeLayout)findViewById(R.id.actAddBtnHead),"添加分组") {
			
			@Override
			protected void defaultOperate(TextView contentText,Button sure, Button cancle) {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void responseClick(Boolean isSureChecked) {
				// TODO Auto-generated method stub
				if(isSureChecked){
					String GroupName=this.getText();
					if (GroupName.equals("")) {
						Toast.makeText(ActAddButton.this, "群组名称不能为空!", Toast.LENGTH_SHORT).show();
					} else {
						boolean result = false;
						result = XmppService.addGroup(roster,GroupName);
						if (result) {
			        	     Intent intent = new Intent();
			        		 intent.putExtra("USERID", pUSERID);
			        		 intent.putExtra("fromUserJid", CHECK);
			        		 ActMain.tabHost.setCurrentTab(2);
			     			 intent.setClass(ActAddButton.this, ActMain.class);
			     			 startActivity(intent);
						} else {
							Toast.makeText(ActAddButton.this, "群组添加失败!", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}

			@Override
			protected void responseCheck(int checkedId) {
				// TODO Auto-generated method stub
				
			}
		};
		
		//列表项的填充与显示
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		for(int i=0;i<itemNames.length;i++){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("image", itemImages[i]);
				map.put("name", itemNames[i]);
				map.put("tag", itemTag[0]);
				list.add(map);
		}	
		
		listItemAdapter = new SimpleAdapter(this, list,// 数据源
			R.layout.add_button_listitem,//listItem的布局
			new String[]{"image","name","tag"},//数据源的键
			new int[]{R.id.add_item_pic,R.id.add_item_name,R.id.add_item_tag//数据值的控件
		});
		listV=(ListView)findViewById(R.id.addList);
		listV.setAdapter(listItemAdapter);
		//列表项监听
		listV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (arg2) {
				case 0://第一项，添加好友
					Intent friIntent=new Intent(ActAddButton.this,ActAddFriend.class);
					friIntent.putExtra("USERID", pUSERID);
					startActivity(friIntent);
					break;
					
				case 1://第二项，添加分组
					addGroupdialog.openDialog(DialogAll.Type.INPUT,view);
					break;
										
				default:
					break;
				} 
			}
		});
		
		//返回按钮监听
		Button returnBtn=(Button)findViewById(R.id.goback_button);
		returnBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
    			finish();
			}
		});
	}
}