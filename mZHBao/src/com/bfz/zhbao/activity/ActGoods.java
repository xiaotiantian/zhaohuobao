package com.bfz.zhbao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.utils.AMapLocManager;
import com.bfz.zhbao.utils.CityInfoParser;
import com.bfz.zhbao.utils.CityInfoParser.CityInfo;
import com.bfz.zhbao.utils.HttpClientUtil;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.utils.SysConfig;
import com.bfz.zhbao.utils.SysConfig.cfgMsg;

@SuppressLint("HandlerLeak")
public class ActGoods extends BaseActivity{

	private Button[] btRegion = null;
	private TextView tvCity = null;
	private Map<String, Integer> regNumMap = new HashMap<String, Integer>();
   


	@Override
	protected void onStart() {
		super.onStart();
		handle.sendEmptyMessage(2);
		SharedPreferences preferences = getSharedPreferences(SysConfig.PREFS_NAME, Activity.MODE_PRIVATE);
		tvCity.setText(preferences.getString("sShiStr", "[切换城市]"));

		// tvCity.setText(fromCity);
		if (!tvCity.getText().equals("[切换城市]"))
			getRegNumTask().execute(SysConfig.asyncMsgsNumUtl);

	}

	private Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: 
			{
				final cfgMsg mMsg = (cfgMsg) msg.obj;

				if (mMsg.fromCity == null) {
					break;
				}
				if (!tvCity.getText().equals(mMsg.fromCity.replace("市", ""))) {
					Builder builder = new AlertDialog.Builder(ActGoods.this).setTitle("定位提示").setMessage("您已进入:" + mMsg.fromCity + "\n是否切换到当前城市!");
					builder.setPositiveButton("切换", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							// 城市切换
							SharedPreferences preferences = getSharedPreferences(SysConfig.PREFS_NAME, Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString("sShengStr", mMsg.fromReg.replace("省", ""));
							editor.putString("sShiStr", mMsg.fromCity.replace("市", ""));
							editor.commit();
							tvCity.setText(mMsg.fromCity.replace("市", ""));
							if (!tvCity.getText().equals("[切换城市]"))
								getRegNumTask().execute(SysConfig.asyncMsgsNumUtl);
							dialog.cancel();
						}
					}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					}).show();
				}

				break;
			}
			case 1: 
			{
				CityInfoParser parser = new CityInfoParser((String) msg.obj);
				ArrayList<CityInfo> list = parser.parse();
				for (CityInfo ci : list) {
					if (ci.ADDRESSEND.equals("黑龙")) {
						ci.ADDRESSEND = "黑龙江";
					}
					if (ci.ADDRESSEND.equals("内蒙")) {
						ci.ADDRESSEND = "内蒙古";
					}
					if (regNumMap.containsKey(ci.ADDRESSEND)) {
						((TextView) findViewById(regNumMap.get(ci.ADDRESSEND))).setText(ci.ADDRESSCOUNT);
					}
				}
				break;
			}
			case 2:
			{
				for (int j = 0; j < btRegion.length; j++) {
					((TextView) findViewById(regNumMap.get(btRegion[j].getText().toString()))).setText("");
				}
				break;
			}
			
			}
		}

	};

	private AsyncTask<String, Void, String> getRegNumTask() {

		AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = result;
					handle.sendMessage(msg);
				}
			}

			@Override
			protected String doInBackground(String... params) {
				Map<String, Object> httpParams = new HashMap<String, Object>();
				String str = null;
				try {
					httpParams.put("cityName", tvCity.getText());
					str = HttpClientUtil.getStringByGet(params[0], httpParams, 1000 * 5);

				} catch (Exception e) {
					
					e.printStackTrace();
				}
				return str;
			}
		};
		return asyncTask;
	}

	class onClickListener implements View.OnClickListener {
		public void onClick(View view) {

			if(view.getId() == R.id.tvCity)
			{
				Intent intent = new Intent(ActGoods.this, ActSheng.class);
				startActivity(intent);
			} else
			{
				SharedPreferences preferences = getSharedPreferences(SysConfig.PREFS_NAME, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("toReg", ((Button) view).getText().toString());
				editor.putString("toCity", "全部");
				editor.commit();
	
				Intent intent = new Intent(ActGoods.this, ActGoodsInfo.class);
				startActivity(intent);
			}
		}
	}

	@Override
	protected void onCreateMainView() {
		
		setContentView(R.layout.act_goods);
	
		MyActivityManagerApplication.getInstance().addActivity(this);
		int[] btRegionId = {
				// btHuabei
				R.id.btBeijing, R.id.btTianjin, R.id.btHebei, R.id.btShanxi, R.id.btSandong, R.id.btNeimeng,
				// btHuadong
				R.id.btShanghai, R.id.btAnhui, R.id.btZhejiang, R.id.btJiangsu,
				// btHuanan
				R.id.btGuangdong, R.id.btFujian, R.id.btHainan, R.id.btGuangxi,
				// btHuazhong
				R.id.btHubei, R.id.btHunan, R.id.btHenan, R.id.btJiangxi,
				// btDongbei
				R.id.btLiaoning, R.id.btHeilongjiang, R.id.btJiling,
				// btXibei
				R.id.btShannxi, R.id.btGansu, R.id.btNingxia, R.id.btQinghai, R.id.btXinjiang,
				// btXinan
				R.id.btChongqing, R.id.btSichuan, R.id.btYunnan, R.id.btGuizhou, R.id.btXizang };

		int[] btRegionNumId = {
				// btHuabei
				R.id.btBeijing_num, R.id.btTianjin_num, R.id.btHebei_num, R.id.btShanxi_num, R.id.btSandong_num, R.id.btNeimeng_num,
				// btHuadong
				R.id.btShanghai_num, R.id.btAnhui_num, R.id.btZhejiang_num, R.id.btJiangsu_num,
				// btHuanan
				R.id.btGuangdong_num, R.id.btFujian_num, R.id.btHainan_num, R.id.btGuangxi_num,
				// btHuazhong
				R.id.btHubei_num, R.id.btHunan_num, R.id.btHenan_num, R.id.btJiangxi_num,
				// btDongbei
				R.id.btLiaoning_num, R.id.btHeilongjiang_num, R.id.btJiling_num,
				// btXibei
				R.id.btShannxi_num, R.id.btGansu_num, R.id.btNingxia_num, R.id.btQinghai_num, R.id.btXinjiang_num,
				// btXinan
				R.id.btChongqing_num, R.id.btSichuan_num, R.id.btYunnan_num, R.id.btGuizhou_num, R.id.btXizang_num };

		btRegion = new Button[btRegionId.length];
		for (int j = 0; j < btRegionId.length; j++) {
			btRegion[j] = (Button) findViewById(btRegionId[j]);
			btRegion[j].setOnClickListener(new onClickListener());

			regNumMap.put(btRegion[j].getText().toString(), btRegionNumId[j]);
		}

		tvCity = (TextView) findViewById(R.id.tvCity);
		tvCity.setOnClickListener(new onClickListener());
		//高德定位
		new AMapLocManager(this,handle);
		
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
}