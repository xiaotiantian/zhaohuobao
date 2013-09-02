package com.bfz.zhbao.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;

/**
 * 
 * @author yangfan
 *
 */
public class ImageShower  extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageshower);

		final ImageLoadingDialog dialog = new ImageLoadingDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		// 两秒后关闭后dialog
		new Handler().postDelayed(new Runnable() {
			public void run() {
				dialog.dismiss();
			}
		}, 1000 * 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		finish();
		return true;
	}

	@Override
	protected void onCreateMainView() {
		
	}

	@Override
	public void onBackPressed() {
		finish();
	}
	
}
