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
	private String userphone;//当前登录用户
	private String pGROUPNAME;//当前组
	private Builder usrLongClickDialog ;//长按用户菜单
	private Builder groupLongClickDialog ;//长按分组菜单
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
		//加载好友列表
		try {	
			loadFriend();
		} catch (Exception e) {		
			e.printStackTrace();
			Toast.makeText(this, "出错啦",0).show();
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
	//要聊天的人将两者传送至聊天界面进行接受
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
	 * 加载好友
	 */
	public void loadFriend() {
		try {
			Collection<RosterGroup> groups = roster.getGroups();
			groupList = new ArrayList<GroupInfo>();
			for (RosterGroup group : groups) {
				System.out.println(" 分组有：" + group.getName());
				groupInfo = new GroupInfo();
				friendList = new ArrayList<FriendInfo>();
				groupInfo.setGroupName(group.getName());
				Collection<RosterEntry> entries = group.getEntries();
				for (RosterEntry entry : entries) {
					if("both".equals(entry.getType().name())){//只添加双边好友 
						friendInfo = new FriendInfo();
						friendInfo.setUsername(Utils.getJidToUsername(entry.getUser()));
						//System.out.println("我的好友心情是："+entry.getStatus().fromString(entry.getUser()));
						if(friendMood == null){
							friendMood ="Q我吧，静待你的来信！";
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
			groupInfo.setGroupName("我的好友");
			groupInfo.setFriendInfoList(new ArrayList<FriendInfo>());
			groupList.add(groupInfo);
			groupInfo = null;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	//长按管理好友与分组
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(menuInfo instanceof ExpandableListView.ExpandableListContextMenuInfo){
			
			final ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
			final int type = ExpandableListView.getPackedPositionType(info.packedPosition);//选中ITEM的类型
			int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
			int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			final GroupInfo  selectedGroup = groupList.get(groupPos);
			
			if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {		//CHILD TYPE
				final FriendInfo selectedFriend = groupList.get(groupPos).getFriendInfoList().get(childPos);
				usrLongClickDialog=new AlertDialog.Builder(this);
				usrLongClickDialog.setIcon(R.drawable.ic_launcher);
				//设置对话框的标题
				usrLongClickDialog.setTitle("好友管理");
				//为对话框设置多个列表
				usrLongClickDialog.setItems(
						new String[] {"好友备注","删除好友" , "移动好友到" , "清空聊天记录","查看资料"}
						, new DialogInterface.OnClickListener() 
						{
						//该方法的which参数代表用户单击了那个列表项
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
										//长按获取Jid，列表对话框获取groupName,沿用connection
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
				//设置对话框的标题
				groupLongClickDialog.setTitle("分组管理");
				//为对话框设置多个列表
				groupLongClickDialog.setItems(
						new String[] {"小组群发","分组备注","删除分组" }
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
		Toast.makeText(this, "清空聊天记录",Toast.LENGTH_SHORT).show();
	}

	/**
	 * 小组群发 （没实现）
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
		System.out.println("小组群聊成功");
	}
	/**
	 * 删除分组
	 * @param info
	 */
	protected void deleteGroup(final GroupInfo  gInfo) {
		LayoutInflater layoutInflater= LayoutInflater.from(this);
        View delFriendView = layoutInflater.inflate(R.layout.dialog_del_group, null);
        TextView delname = (TextView)delFriendView.findViewById(R.id.delgroupName);
        delname.setText(gInfo.getGroupName());
        Dialog dialog =new AlertDialog.Builder(this)
        .setIcon(R.drawable.default_head)
        .setTitle("删除分组")
        .setView(delFriendView)
        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface dialog, int which){
            	//若是有好友则自动分到“我的好友”
            	List<FriendInfo> friendL=gInfo.getFriendInfoList();
            	Iterator<FriendInfo> FriendIt=friendL.iterator();
            	String currentName=gInfo.getGroupName();
            	List<GroupInfo> list=adapter.getList();
            	Iterator<GroupInfo> groupIt =list.iterator();
        		List<FriendInfo> defaultFriList=new ArrayList<FriendInfo>();
        		GroupInfo MyFriendGroOld=null;
        		
        		
        		while(groupIt.hasNext()){
            		MyFriendGroOld=groupIt.next();
            		//如果分组不为空，获取“我的好友”的好友列表
            		if (friendL!=null&MyFriendGroOld.getGroupName().equals("我的好友")){
            			defaultFriList=MyFriendGroOld.getFriendInfoList();//获取我的好友中的默认好友列表	
            			groupIt.remove();//删除“我的好友”分组
            		}
            		//删除目标分组
            		if(MyFriendGroOld.getGroupName().equals(currentName)){
            			groupIt.remove();
            		}
            	}
        		//若分组不为空，将好友添加到默认好友列表
        		if(friendL!=null){
        			while(FriendIt.hasNext()){
    	    			FriendInfo nextFri=FriendIt.next();
    	    			defaultFriList.add(nextFri);//将好友添加到默认好友列表
    	    			XmppService.addUsers(roster,nextFri.getJid(),nextFri.getUsername() ,"我的好友");//添加好友到“我的好友”
            		}
            		GroupInfo MyFriendGroNew=new GroupInfo();
            		MyFriendGroNew.setGroupName("我的好友");
            		MyFriendGroNew.setFriendInfoList(defaultFriList);//set我的好友列表
            		list.add(MyFriendGroNew);//添加“我的好友”分组
        		}
        		
            	//更新adapter
            	adapter.setList(list);
            	adapter.notifyDataSetChanged();
            }
        })
        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                	dialog.cancel();
                }
        })
        .create();
        dialog.show();
		
		
	}
	
	/**
	 * 修改备注名称
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
					Toast.makeText(ActFriends.this, "名称不能为空!", Toast.LENGTH_SHORT).show();
				} else if(type==ExpandableListView.PACKED_POSITION_TYPE_CHILD){
					//若是点击的好友
					dInfo.setNickname(name);
				}
				else{
					//若是点击的分组
					String currentName=gInfo.getGroupName();
					List<GroupInfo> list=adapter.getList();
					//更新服务器
					roster.getGroup(currentName).setName(name);
					
					int i=0;
					while(true){
						if(list.get(i).getGroupName().equals(currentName)){
							list.get(i).setGroupName(name);
							break;
						}
						i++;
					}
					//刷新适配器
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
	 * 删除好友的方法
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
            .setTitle("删除好友")
            .setView(delFriendView)
            .setPositiveButton("确定", new DialogInterface.OnClickListener(){
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
							System.out.println("删除成功");
						} catch (XMPPException e) {
							System.out.println("删人异常啊！！");
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
                	 //刷新适配器
                	 adapter.setList(list);
                	 adapter.notifyDataSetChanged();
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
	                	dialog.cancel();
	                }
            })
            .create();
            dialog.show();
	}
	
	/**
	 * 移动好友到
	 * @param userJid
	 * @param connection
	 * 
	 * 迭代出原组和目标组，修改两个的friendList,然后notifyDataSetChanged()
	 */
	public boolean moveFriendTo(final GroupInfo fromGroup,final FriendInfo selectFriend){
		//从原组的好友列表中去掉此好友
		List<FriendInfo> fromFriendL=fromGroup.getFriendInfoList();
		Iterator<FriendInfo> friendIt=fromFriendL.iterator();
		while(friendIt.hasNext()){
			if(friendIt.next().getUsername().equals(selectFriend.getUsername())){
				friendIt.remove();
				break;
			}
		}
		//弹出分组列表，点击后获取分组名称toGroupName
		final List<GroupInfo> list=adapter.getList();
		groNames=new String[list.size()];
		Iterator<GroupInfo> groupIt=list.iterator();
		for(int i=0;groupIt.hasNext();i++){
			GroupInfo ros=groupIt.next();
			groNames[i]=ros.getGroupName();
			//将原组的好友列表更新
			if(groNames[i]==fromGroup.getGroupName()){
				list.get(i).setFriendInfoList(fromFriendL);
			}
		}
		
		Builder groupListDialog=new AlertDialog.Builder(this);
		groupListDialog.setIcon(R.drawable.ic_launcher);
		//设置对话框的标题
		groupListDialog.setTitle("请选择分组");
		//为对话框设置多个列表
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
	 * 查看资料（没实现）
	 * @param userJid
	 */
	public void checkInfo(String userJid){
		// TODO Auto-generated method stub
		//跳转到个人资料页的Activity
		Intent intent=new Intent(ActFriends.this,ActPersonnal.class);
		System.out.println("查看资料成功");
		startActivity(intent);
		
	}
	/**
	 * Activity的初始化
	 */
	private void  initialization(){
		MyActivityManagerApplication.getInstance().addActivity(this);
		//获取从loginactivity传来的参数为当前登录用户
		this.userphone = getIntent().getStringExtra("userphone");
		this.pGROUPNAME = getIntent().getStringExtra("GROUPNAME");
		
		//头部显示
		tvFriends = (TextView)findViewById(R.id.tvFriends);
		tvFriends.setText(userphone);
		
		lvFriendsList = (ExpandableListView) findViewById(R.id.lvFriendsList);
		registerForContextMenu(lvFriendsList);
		
		//右上角添加按钮
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
