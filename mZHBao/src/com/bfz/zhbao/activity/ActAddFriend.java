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
	
	private String pUSERID;//��ǰ�û�
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
		//���ذ�ť
		goback_button = (Button) findViewById(R.id.goback_button);
		goback_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					
				finish();
			}			
		});
		
		dialogView = View.inflate(this,R.layout.dialog_present, null);
		addFridialog=new DialogAll((RelativeLayout)findViewById(R.id.actAddFriHead),"��Ϊ����") {
			
			@Override
			protected void defaultOperate(TextView contentText,Button sure, Button cancle) {
				contentText.setText("��ȷ��Ҫ���� "+friName+" ��\n��Ϊ������");
				sure.setText("���");
				cancle.setText("ȡ��");
			}

			@Override
			protected void responseClick(Boolean isSureChecked) {
				// TODO Auto-generated method stub
				if(isSureChecked){
					 Roster roster = XmppConnection.getConnection().getRoster();
		         	 String userJid = friName+"@"+XmppConnection.getConnection().getServiceName();
		         	 //Ĭ����ӵ����ҵĺ��ѡ�����
		         	 String groupName = "�ҵĺ���";
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
	
	//��ѯ����
	public void searchFriend() {	
		String search_text = ((EditText) findViewById(R.id.search_text)).getText().toString();
		if (search_text.equals("")) {
			Toast.makeText(ActAddFriend.this, "������Ϣ����Ϊ�գ�", Toast.LENGTH_SHORT).show();
		} else {
			try{
				//��������
				XMPPConnection connection = XmppConnection.getConnection();
				//������ѯ������
				UserSearchManager search = new UserSearchManager(connection);
				//�˴�һ��Ҫ���� search.
				//�õ���ѯ���+connection.getServiceName()
				Form searchForm = search.getSearchForm("search." + connection.getServiceName());
				//�õ���ѯ���AnswerForm
				Form answerForm = searchForm.createAnswerForm();
				//��AnswerForm�����Ӳ�ѯ��
				answerForm.setAnswer("Username", true);
				answerForm.setAnswer("search", search_text.toString().trim());
				//�ύ��ѯ��񣬲�����ReportedData���
				ReportedData data = search.getSearchResults(answerForm,"search."+connection.getServiceName());					
				it = data.getRows();
				
			}catch(Exception e){
				Toast.makeText(ActAddFriend.this,e.getMessage()+" "+e.getClass().toString(), Toast.LENGTH_SHORT).show();
			}
			
			if(it.hasNext()){
				// ���ɶ�̬���飬��������
				ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
				while(it.hasNext()){
				    HashMap<String, Object> map = new HashMap<String, Object>();	     
				    map.put("name", it.next().getValues("Username").next().toString()); //��Ա�ǳ�
					listItem.add(map);
				}
				// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
				SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// ����Դ
						R.layout.add_friend_listitem,// ListItem��XMLʵ��
							// ��̬������ImageItem��Ӧ������
						new String[] { "name"/*,"address","button"*/ },
						// ImageItem��XML�ļ������һ��ImageView,����TextView ID
						new int[] { R.id.usrName/*,R.id.addressInfo,R.id.addAsFriend*/});
				// ��Ӳ�����ʾ��ѯ�����б�
				list.setAdapter(listItemAdapter);
				// ��Ӷ̵���¼�
				list.setOnItemClickListener(new OnItemClickListener(){
					public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
						HashMap<String, String> map = (HashMap<String, String>) list.getItemAtPosition(position);
						friName = map.get("name");
						
						addFridialog.openDialog(DialogAll.Type.PRESENT,dialogView);
					}
				});
				}else{
				  Toast.makeText(ActAddFriend.this, "���û������ڣ���ȷ���������Ϣ��ȷ��", Toast.LENGTH_SHORT).show();
			  }
		}
	}
	
}
