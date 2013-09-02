package com.bfz.zhbao.activity;

import com.bfz.zhbao.R;
import android.os.Bundle;

import com.bfz.zhbao.Base.BaseActivity;

public class ActMessage extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		onCreateMainView();
		
	}
	@Override
	public void onBackPressed() {
      super.onBackPressed();
	}
	@Override
	protected void onCreateMainView() {
		this.setContentView(R.layout.act_message);
		
	}
}
