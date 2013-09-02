package com.bfz.zhbao.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.bfz.zhbao.R;
import com.bfz.zhbao.service.MsgService;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.utils.SysConfig;
import com.bfz.zhbao.utils.UpdateManager;

public class ActMain extends TabActivity {
	public static TabHost tabHost;
	private Intent goodsTab;
	private Intent messageTab;
	private Intent friendsTab;
	private Intent setupTab;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);
		startService(new Intent(ActMain.this, MsgService.class));
		initialization();
		//获取TabHost对象
		tabHost = this.getTabHost();

		tabHost.addTab(tabHost.newTabSpec("tab11").setIndicator("消息").setContent(R.id.tab1)
				.setContent(messageTab));
		
		tabHost.addTab(tabHost.newTabSpec("tab12").setIndicator("货源").setContent(R.id.tab2)
				.setContent(goodsTab));
		
		tabHost.addTab(tabHost.newTabSpec("tab13").setIndicator("朋友").setContent(R.id.tab3)
				.setContent(friendsTab));
		
		tabHost.addTab(tabHost.newTabSpec("tab14").setIndicator("设置").setContent(R.id.tab4)
				.setContent(setupTab));
		MyActivityManagerApplication.getInstance().addActivity(this);

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下的如果是BACK，同时没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		
			exitSystem();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	/**
	 *  菜单选项设置
	 */
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(1, 1, 1, R.string.soft_update);
		menu.add(1, 2, 2, R.string.soft_about);
		menu.add(1, 3, 3, R.string.soft_exit);
		return super.onCreateOptionsMenu(menu);
	}



	/**
	 * 对菜单点击事件进行的处理
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			UpdateManager um = new UpdateManager(this, SysConfig.updateUrlTest);
			um.checkUpdate();
			break;
		case 2:
			// 对话框
			Dialog dialog = new AlertDialog.Builder(this).setTitle("关于我们")
					.setMessage("找货宝团队！\nQQ:1554987499").setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
						
							dialog.cancel();
						}
					}).create();
			dialog.show();
			break;
		case 3:
			exitSystem();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 对getTabHost进行设置
	 */
	private void initialization(){
		
		
		String userphone = this.getIntent().getStringExtra("userphone");
		goodsTab = new Intent(this,ActGoods.class);
		messageTab = new Intent(this,ActMessage.class);
		friendsTab = new Intent(this,ActFriends.class);
		friendsTab.putExtra("userphone", userphone);
		setupTab = new Intent(this,ActSetup.class);
	}
	public void exitSystem(){
		
		MyActivityManagerApplication.getInstance().exit();
	}

	@Override
	public void finish() {
		Intent intent = new Intent();
	    intent.setClass(this, MsgService.class);
		this.stopService(intent);
		super.finish();
	}
	
}
