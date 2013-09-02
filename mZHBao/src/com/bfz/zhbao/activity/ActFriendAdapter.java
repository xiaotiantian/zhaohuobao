package com.bfz.zhbao.activity;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfz.zhbao.R;
import com.bfz.zhbao.entity.FriendInfo;
import com.bfz.zhbao.entity.GroupInfo;

public class ActFriendAdapter  extends BaseExpandableListAdapter {
	Context context;
	private List<GroupInfo> groList;
	private LayoutInflater mChildInflater;
	public ActFriendAdapter(Context context){
		mChildInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	class FriendHolder{
		TextView name;
		TextView mood;
		ImageView iv;
	}
	public void setList(List<GroupInfo> list){
		this.groList=list;
	}
	public List<GroupInfo> getList(){
		return this.groList;
	}
	
	public int getGroupCount() {
		return groList.size();
	}

	public int getChildrenCount(int groupPosition) {
		return groList.get(groupPosition).getFriendInfoList().size();
	}

	public GroupInfo getGroup(int groupPosition) {
		return groList.get(groupPosition);
	}
	public GroupInfo getGroup(String groupName) {
		GroupInfo groupInfo = null;
		if(getGroupCount() > 0){
			for(int i = 0,j = getGroupCount();i< j;i++){
				GroupInfo holder = (GroupInfo) getGroup(i);
				if(TextUtils.isEmpty(holder.getGroupName())){
					groList.remove(holder);
				}else{
					if(holder.getGroupName().equals(groupInfo)){
						groupInfo = holder;
					}
				}
			}
		}
		return groupInfo;
	}

	public FriendInfo getChild(int groupPosition, int childPosition) {
		return groList.get(groupPosition).getFriendInfoList().get(childPosition);
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public boolean hasStableIds() {
		return false;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,	View convertView, ViewGroup parent) {
			FriendHolder holder;
			if(convertView == null){
				holder = new FriendHolder();					
				convertView = mChildInflater.inflate(R.layout.friend_group_item,null);
				holder.name =  (TextView) convertView.findViewById(R.id.friend_group_list_name);
				holder.iv = (ImageView) convertView.findViewById(R.id.friend_group_list_icon);
				convertView.setTag(holder);
			}else{
				holder = (FriendHolder) convertView.getTag();
			}
			String groupname = groList.get(groupPosition).getGroupName();
			holder.name.setText(groupname);
			if(isExpanded){
				holder.iv.setBackgroundResource(R.drawable.sc_group_expand);
			}else{
				holder.iv.setBackgroundResource(R.drawable.sc_group_unexpand);		
			}		
			return convertView;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		    FriendHolder holder;
			if(convertView == null){
				holder = new FriendHolder();
				convertView = mChildInflater.inflate(R.layout.friend_child_item,null);
				holder.name =  (TextView) convertView.findViewById(R.id.friend_nickname);
				holder.mood =  (TextView) convertView.findViewById(R.id.friend_mood);
				convertView.setTag(holder);
			}else{
				holder = (FriendHolder) convertView.getTag();
			}
			FriendInfo groupname = groList.get(groupPosition).getFriendInfoList().get(childPosition);
			String nick=groupname.getNickname();
			if(nick!=null){
				holder.name.setText(groupname.getUsername()+"("+nick+")");
			}else{
				holder.name.setText(groupname.getUsername());
			}
			
			holder.mood.setText(groupname.getMood());
			/*if(isLastChild){
				lvFriendsList.setItemChecked(groupPosition, true);
			}*/
			return convertView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

}
