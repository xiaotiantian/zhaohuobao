package com.bfz.zhbao.activity;



import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.utils.MyActivityManagerApplication;

public class ActPersonnal extends BaseActivity{



	@Override
	protected void onCreateMainView() {
		setContentView(R.layout.act_personnal);
		 
		MyActivityManagerApplication.getInstance().addActivity(this);
	}
	
}
