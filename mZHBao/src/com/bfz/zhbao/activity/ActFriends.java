/**
 * 
 */
package com.bfz.zhbao.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.entity.FriendInfo;
import com.bfz.zhbao.entity.GroupInfo;
import com.bfz.zhbao.service.MsgService;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.utils.Utils;
import com.bfz.zhbao.xmppManager.XmppConnection;
import com.bfz.zhbao.xmppManager.XmppService;

/**
 * @author HaiFeng 
 * @date 2013-7-18
 */
public class ActFriends extends BaseActivity implements OnGroupClickListener, OnChildClickListener{

	static TextView tvFriends = null;
	public static final String CHECK = null;
	private String userphone;//��ǰ��¼�û�
	private String pGROUPNAME;//��ǰ��
	private Builder usrLongClickDialog ;//�����û��˵�
	private Builder groupLongClickDialog ;//��������˵�
	private List<GroupInfo> groupList;
	private List<FriendInfo> friendList;
	XMPPConnection connection = XmppConnection.getConnection();
	private Button addButton=null;
	FriendInfo friendInfo;
	GroupInfo groupInfo;
	private String[] groNames;
	
	private ExpandableListView lvFriendsList;
	public ActFriendAdapter adapter;
	public Roster roster = connection.getRoster();
	private String friendMood = null;

	
	@Override
	protected void onStart() {
		super.onStart();
		//���غ����б�
		try {	
			loadFriend();
		} catch (Exception e) {		
			e.printStackTrace();
			Toast.makeText(this, "������",0).show();
		}
		
		adapter =new ActFriendAdapter(this);
		adapter.setList(groupList);
		lvFriendsList.setAdapter(adapter);
		lvFriendsList.setOnGroupClickListener(this);
		lvFriendsList.setOnChildClickListener(this);
		
		 
	}

	@Override
	protected void onResume() {

		super.onResume();
	}
	//Ҫ������˽����ߴ��������������н���
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		FriendInfo info = groupList.get(groupPosition).getFriendInfoList().get(childPosition);
		Intent intent = new Intent(this,ActChat.class);
		intent.putExtra("opposite_jid", info.getJid());
		intent.putExtra("opposite_phone", info.getUsername());
		intent.putExtra("userphone", userphone);
		ActChat.setMsgFrom(info.getUsername());
		ActChat.setMsgTo(userphone);

		startActivity(intent);	
		
		return false;
	}
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		return false;
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	/**
	 * ���غ���
	 */
	public void loadFriend() {
		try {
			Collection<RosterGroup> groups = roster.getGroups();
			groupList = new ArrayList<GroupInfo>();
			for (RosterGroup group : groups) {
				System.out.println(" �����У�" + group.getName());
				groupInfo = new GroupInfo();
				friendList = new ArrayList<FriendInfo>();
				groupInfo.setGroupName(group.getName());
				Collection<RosterEntry> entries = group.getEntries();
				for (RosterEntry entry : entries) {
					if("both".equals(entry.getType().name())){//ֻ���˫�ߺ��� 
						friendInfo = new FriendInfo();
						friendInfo.setUsername(Utils.getJidToUsername(entry.getUser()));
						//System.out.println("�ҵĺ��������ǣ�"+entry.getStatus().fromString(entry.getUser()));
						if(friendMood == null){
							friendMood ="Q�Ұɣ�����������ţ�";
						}
						friendInfo.setMood(friendMood);
						friendList.add(friendInfo);
						friendInfo = null;			
					}
				}
				groupInfo.setFriendInfoList(friendList);
				groupList.add(groupInfo);
				groupInfo = null;
			}
		if(groupList.isEmpty()){
			groupInfo = new GroupInfo();
			groupInfo.setGroupName("�ҵĺ���");
			groupInfo.setFriendInfoList(new ArrayList<FriendInfo>());
			groupList.add(groupInfo);
			groupInfo = null;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	//����������������
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(menuInfo instanceof ExpandableListView.ExpandableListContextMenuInfo){
			
			final ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
			final int type = ExpandableListView.getPackedPositionType(info.packedPosition);//ѡ��ITEM������
			int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
			int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			final GroupInfo  selectedGroup = groupList.get(groupPos);
			
			if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {		//CHILD TYPE
				final FriendInfo selectedFriend = groupList.get(groupPos).getFriendInfoList().get(childPos);
				usrLongClickDialog=new AlertDialog.Builder(this);
				usrLongClickDialog.setIcon(R.drawable.ic_launcher);
				//���öԻ���ı���
				usrLongClickDialog.setTitle("���ѹ���");
				//Ϊ�Ի������ö���б�
				usrLongClickDialog.setItems(
						new String[] {"���ѱ�ע","ɾ������" , "�ƶ����ѵ�" , "��������¼","�鿴����"}
						, new DialogInterface.OnClickListener() 
						{
						//�÷�����which���������û��������Ǹ��б���
							public void onClick(DialogInterface dialog, int which) 
							{
								switch(which)
								{
									case 0:
										modifyName(type,null,selectedFriend);
										break;
									case 1:
										deleteFriend(selectedGroup,selectedFriend);
										
										break;
									case 2:
										//������ȡJid���б�Ի����ȡgroupName,����connection
										moveFriendTo(selectedGroup,selectedFriend);
										break;	
									case 3:
										cleanHistory(selectedFriend);
										break;
									case 4:
										checkInfo(selectedFriend.getJid());
										break;
									default:
										break;
								}
							}
				});
				usrLongClickDialog.create().show();
			}else if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP){	
				//GROUP TYPE
				groupLongClickDialog=new AlertDialog.Builder(this);
				groupLongClickDialog.setIcon(R.drawable.ic_launcher);
				//���öԻ���ı���
				groupLongClickDialog.setTitle("�������");
				//Ϊ�Ի������ö���б�
				groupLongClickDialog.setItems(
						new String[] {"С��Ⱥ��","���鱸ע","ɾ������" }
						, new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								switch(which)
								{
									case 0:
										groupChat(selectedGroup);
										break;
									case 1:
										modifyName(type,selectedGroup,null);
										break;
									case 2:
										deleteGroup(selectedGroup);
										break;
									default:
										break;
								}
								
							}
				});
				groupLongClickDialog.create().show();
			}
		}
	}
	protected void cleanHistory(FriendInfo selectedFriend) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "��������¼",Toast.LENGTH_SHORT).show();
	}

	/**
	 * С��Ⱥ�� ��ûʵ�֣�
	 */
	protected void groupChat(GroupInfo  gInfo) {
		// TODO Auto-generated method stub
		List<FriendInfo> friendList=gInfo.getFriendInfoList();
		Iterator<FriendInfo> it=friendList.iterator();
		while(it.hasNext()){
			FriendInfo next=it.next();
			next.getJid(); 
			new MsgService();
		}
		System.out.println("С��Ⱥ�ĳɹ�");
	}
	/**
	 * ɾ������
	 * @param info
	 */
	protected void deleteGroup(final GroupInfo  gInfo) {
		LayoutInflater layoutInflater= LayoutInflater.from(this);
        View delFriendView = layoutInflater.inflate(R.layout.dialog_del_group, null);
        TextView delname = (TextView)delFriendView.findViewById(R.id.delgroupName);
        delname.setText(gInfo.getGroupName());
        Dialog dialog =new AlertDialog.Builder(this)
        .setIcon(R.drawable.default_head)
        .setTitle("ɾ������")
        .setView(delFriendView)
        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface dialog, int which){
            	//�����к������Զ��ֵ����ҵĺ��ѡ�
            	List<FriendInfo> friendL=gInfo.getFriendInfoList();
            	Iterator<FriendInfo> FriendIt=friendL.iterator();
            	String currentName=gInfo.getGroupName();
            	List<GroupInfo> list=adapter.getList();
            	Iterator<GroupInfo> groupIt =list.iterator();
        		List<FriendInfo> defaultFriList=new ArrayList<FriendInfo>();
        		GroupInfo MyFriendGroOld=null;
        		
        		
        		while(groupIt.hasNext()){
            		MyFriendGroOld=groupIt.next();
            		//������鲻Ϊ�գ���ȡ���ҵĺ��ѡ��ĺ����б�
            		if (friendL!=null&MyFriendGroOld.getGroupName().equals("�ҵĺ���")){
            			defaultFriList=MyFriendGroOld.getFriendInfoList();//��ȡ�ҵĺ����е�Ĭ�Ϻ����б�	
            			groupIt.remove();//ɾ�����ҵĺ��ѡ�����
            		}
            		//ɾ��Ŀ�����
            		if(MyFriendGroOld.getGroupName().equals(currentName)){
            			groupIt.remove();
            		}
            	}
        		//�����鲻Ϊ�գ���������ӵ�Ĭ�Ϻ����б�
        		if(friendL!=null){
        			while(FriendIt.hasNext()){
    	    			FriendInfo nextFri=FriendIt.next();
    	    			defaultFriList.add(nextFri);//��������ӵ�Ĭ�Ϻ����б�
    	    			XmppService.addUsers(roster,nextFri.getJid(),nextFri.getUsername() ,"�ҵĺ���");//��Ӻ��ѵ����ҵĺ��ѡ�
            		}
            		GroupInfo MyFriendGroNew=new GroupInfo();
            		MyFriendGroNew.setGroupName("�ҵĺ���");
            		MyFriendGroNew.setFriendInfoList(defaultFriList);//set�ҵĺ����б�
            		list.add(MyFriendGroNew);//��ӡ��ҵĺ��ѡ�����
        		}
        		
            	//����adapter
            	adapter.setList(list);
            	adapter.notifyDataSetChanged();
            }
        })
        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                	dialog.cancel();
                }
        })
        .create();
        dialog.show();
		
		
	}
	
	/**
	 * �޸ı�ע����
	 */
	private void modifyName(final int type,final GroupInfo gInfo,final FriendInfo dInfo) {
		final View view = View.inflate(this,R.layout.dialog_input, null);
		final PopupWindow mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.showAtLocation(findViewById(R.id.LayoutOfHead), Gravity.CENTER, 0, 0);
		mPopupWindow.setAnimationStyle(R.style.animationmsg);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();
		
		Button sure = (Button) view.findViewById(R.id.sure);
		Button cancle = (Button) view.findViewById(R.id.cancle);
		sure.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				EditText modifyName= (EditText) view.findViewById(R.id.inputText);
				String name = modifyName.getText().toString().trim();
				modifyName.setText("");
				if (name.equals("")) {
					Toast.makeText(ActFriends.this, "���Ʋ���Ϊ��!", Toast.LENGTH_SHORT).show();
				} else if(type==ExpandableListView.PACKED_POSITION_TYPE_CHILD){
					//���ǵ���ĺ���
					dInfo.setNickname(name);
				}
				else{
					//���ǵ���ķ���
					String currentName=gInfo.getGroupName();
					List<GroupInfo> list=adapter.getList();
					//���·�����
					roster.getGroup(currentName).setName(name);
					
					int i=0;
					while(true){
						if(list.get(i).getGroupName().equals(currentName)){
							list.get(i).setGroupName(name);
							break;
						}
						i++;
					}
					//ˢ��������
					adapter.setList(list);
					adapter.notifyDataSetChanged();
				}
				mPopupWindow.dismiss();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});
	}
	/**
	 * ɾ�����ѵķ���
	 * @param info
	 */
	public void deleteFriend(final GroupInfo  gInfo,final FriendInfo dInfo){
			LayoutInflater layoutInflater= LayoutInflater.from(this);
            View delFriendView = layoutInflater.inflate(R.layout.dialog_del_friend, null);
            TextView delname = (TextView)delFriendView.findViewById(R.id.delname);
            delname.setText(dInfo.getUsername());
            final CheckBox delCheckBox = (CheckBox)delFriendView.findViewById(R.id.delCheckBox);
            Dialog dialog =new AlertDialog.Builder(this)
            .setIcon(R.drawable.default_head)
            .setTitle("ɾ������")
            .setView(delFriendView)
            .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                	
                	RosterEntry entry = roster.getEntry(dInfo.getJid());
                
               	 try {
               	
               		 roster.getGroup(gInfo.getGroupName()).removeEntry(entry);
               		 
					} catch (XMPPException e1) {
						e1.printStackTrace();
					}
					 if(delCheckBox.isChecked()){
						try {
							roster.removeEntry(entry);
							System.out.println("ɾ���ɹ�");
						} catch (XMPPException e) {
							System.out.println("ɾ���쳣������");
							e.printStackTrace();
						}
					 }
                	
                	 List<FriendInfo> FriendL=gInfo.getFriendInfoList();
                	 Iterator<FriendInfo> FriendIt=FriendL.iterator();
                	 List<GroupInfo> list=adapter.getList();
                	 while(FriendIt.hasNext()){
                		 if(FriendIt.next().getUsername().equals(dInfo.getUsername())){
                			 FriendIt.remove();
                			 break;
                		 }
                	 }
                	 int i=0;
                	 while(true){
                		 if(list.get(i).getGroupName().equals(gInfo.getGroupName())){
                			 list.get(i).setFriendInfoList(FriendL);
                			 break;
                		 }
                		 i++;
                	 }
                	 //ˢ��������
                	 adapter.setList(list);
                	 adapter.notifyDataSetChanged();
                }
            })
            .setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
	                	dialog.cancel();
	                }
            })
            .create();
            dialog.show();
	}
	
	/**
	 * �ƶ����ѵ�
	 * @param userJid
	 * @param connection
	 * 
	 * ������ԭ���Ŀ���飬�޸�������friendList,Ȼ��notifyDataSetChanged()
	 */
	public boolean moveFriendTo(final GroupInfo fromGroup,final FriendInfo selectFriend){
		//��ԭ��ĺ����б���ȥ���˺���
		List<FriendInfo> fromFriendL=fromGroup.getFriendInfoList();
		Iterator<FriendInfo> friendIt=fromFriendL.iterator();
		while(friendIt.hasNext()){
			if(friendIt.next().getUsername().equals(selectFriend.getUsername())){
				friendIt.remove();
				break;
			}
		}
		//���������б�������ȡ��������toGroupName
		final List<GroupInfo> list=adapter.getList();
		groNames=new String[list.size()];
		Iterator<GroupInfo> groupIt=list.iterator();
		for(int i=0;groupIt.hasNext();i++){
			GroupInfo ros=groupIt.next();
			groNames[i]=ros.getGroupName();
			//��ԭ��ĺ����б����
			if(groNames[i]==fromGroup.getGroupName()){
				list.get(i).setFriendInfoList(fromFriendL);
			}
		}
		
		Builder groupListDialog=new AlertDialog.Builder(this);
		groupListDialog.setIcon(R.drawable.ic_launcher);
		//���öԻ���ı���
		groupListDialog.setTitle("��ѡ�����");
		//Ϊ�Ի������ö���б�
		groupListDialog.setItems(
				groNames
				, new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						String toGroupName=groNames[which];
						XmppService.addUsers(roster,selectFriend.getJid(),selectFriend.getNickname() ,toGroupName);
						int i=0;
						while(true){
							if(list.get(i).getGroupName().equals(toGroupName)){
								list.get(i).getFriendInfoList().add(selectFriend);
								break;
							}
							i++;
						}
						adapter.setList(list);
						adapter.notifyDataSetChanged();
					}
		});
		
		groupListDialog.create().show();
		
		return true;
	}
	
	/**
	 * �鿴���ϣ�ûʵ�֣�
	 * @param userJid
	 */
	public void checkInfo(String userJid){
		// TODO Auto-generated method stub
		//��ת����������ҳ��Activity
		Intent intent=new Intent(ActFriends.this,ActPersonnal.class);
		System.out.println("�鿴���ϳɹ�");
		startActivity(intent);
		
	}
	/**
	 * Activity�ĳ�ʼ��
	 */
	private void  initialization(){
		MyActivityManagerApplication.getInstance().addActivity(this);
		//��ȡ��loginactivity�����Ĳ���Ϊ��ǰ��¼�û�
		this.userphone = getIntent().getStringExtra("userphone");
		this.pGROUPNAME = getIntent().getStringExtra("GROUPNAME");
		
		//ͷ����ʾ
		tvFriends = (TextView)findViewById(R.id.tvFriends);
		tvFriends.setText(userphone);
		
		lvFriendsList = (ExpandableListView) findViewById(R.id.lvFriendsList);
		registerForContextMenu(lvFriendsList);
		
		//���Ͻ���Ӱ�ť
		addButton=(Button)findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent addIntent=new Intent(ActFriends.this,ActAddButton.class);
				startActivity(addIntent);
			}
		});
	}

	@Override
	protected void onCreateMainView() {
		 
		setContentView(R.layout.act_friends);
		initialization();
		
	}
}
