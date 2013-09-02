package com.bfz.zhbao.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.utils.SysConfig;

public class ActShi extends BaseActivity {

	private Button btcTitle = null;
	private Button[] btCity = null;


	class Listener implements android.view.View.OnClickListener {
		public void onClick(View view) {
			SharedPreferences preferences = getSharedPreferences(SysConfig.PREFS_NAME, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("sShiStr", ((Button) view).getText().toString());
			editor.commit();
			ActShi.this.finish();

		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下的如果是BACK，同时没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Toast.makeText(this, "必须选择一个出发地城市.", Toast.LENGTH_SHORT).show();
//			Intent intent = new Intent(ActivityShi.this, ActivitySheng.class);
//			startActivity(intent);
//			
//			ActivityShi.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
	@Override
	protected void onCreateMainView() {
		setContentView(R.layout.shi);
		SharedPreferences preferences = getSharedPreferences(SysConfig.PREFS_NAME, Activity.MODE_PRIVATE);
		String sheng = preferences.getString("sShengStr", null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		 MyActivityManagerApplication.getInstance().addActivity(this);
		btcTitle = (Button) findViewById(R.id.btcTitle);
		btcTitle.setText("请选择出发地城市：");

		int[] btCityId = { R.id.btCity1, R.id.btCity2, R.id.btCity3, R.id.btCity4, R.id.btCity5, R.id.btCity6, R.id.btCity7, R.id.btCity8, R.id.btCity9,
				R.id.btCity10, R.id.btCity11, R.id.btCity12, R.id.btCity13, R.id.btCity14, R.id.btCity15, R.id.btCity16, R.id.btCity17, R.id.btCity18,
				R.id.btCity19, R.id.btCity20, R.id.btCity21, R.id.btCity22, R.id.btCity23, R.id.btCity24 };

		int i = 0;
		while (!sheng.equals(SysConfig.regList[i][0]))
			i++;
		btCity = new Button[SysConfig.regList[i].length - 1];
		for (int j = 1; j < SysConfig.regList[i].length; j++) {
			btCity[j - 1] = (Button) findViewById(btCityId[j - 1]);
			btCity[j - 1].setOnClickListener(new Listener());
			btCity[j - 1].setText(SysConfig.regList[i][j]);
		}
		
	}
}
