package com.bfz.zhbao.Base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public abstract class BaseActivity extends Activity{
	
	protected Context mContext;
	protected Activity mActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// 初始化基本字段
				initBasic();
		// 创建主试图
				createMainView();
	}
	
	 private void initBasic() {
			mContext = this;
			mActivity = this;
		}
	 protected void createMainView() {
			onCreateMainView();	
		}
	 protected abstract void onCreateMainView();
	 @Override
		public void onBackPressed() {

			Intent i = new Intent(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
		}
}
