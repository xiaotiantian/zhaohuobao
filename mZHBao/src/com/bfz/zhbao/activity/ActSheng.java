package com.bfz.zhbao.activity;

import com.bfz.zhbao.R;
import com.bfz.zhbao.utils.SysConfig;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActSheng extends Activity {

	
	private Button[] btsSheng = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_sheng);

		int[] btshengId = {
				// btsHuabei
				R.id.btsBeijing, R.id.btsTianjin, R.id.btsHebei, R.id.btsShanxi, R.id.btsSandong, R.id.btsNeimeng,
				// btsHuadong
				R.id.btsShanghai, R.id.btsAnhui, R.id.btsZhejiang, R.id.btsJiangsu,
				// btsHuanan
				R.id.btsGuangdong, R.id.btsFujian, R.id.btsHainan, R.id.btsGuangxi,
				// btsHuazhong
				R.id.btsHubei, R.id.btsHunan, R.id.btsHenan, R.id.btsJiangxi,
				// btsDongbei
				R.id.btsLiaoning, R.id.btsJiling, R.id.btsHeilongjiang,
				// btsXibei
				R.id.btsShannxi, R.id.btsGansu, R.id.btsNingxia, R.id.btsQinghai, R.id.btsXinjiang,
				// btsXinan
				R.id.btsChongqing, R.id.btsSichuan, R.id.btsYunnan, R.id.btsGuizhou, R.id.btsXizang };

		btsSheng = new Button[btshengId.length];
		for (int j = 0; j < btshengId.length; j++) {
			btsSheng[j] = (Button) findViewById(btshengId[j]);
			btsSheng[j].setOnClickListener(new Listener());
		}
	}

	class Listener implements View.OnClickListener {
		public void onClick(View view) {
			SharedPreferences preferences = getSharedPreferences(SysConfig.PREFS_NAME, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();

			String Str = ((Button) view).getText().toString();
			if (Str.equals("北京") || Str.equals("天津") || Str.equals("上海") || Str.equals("重庆")) {
				editor.putString("sShengStr", Str);
				editor.putString("sShiStr", Str);
				editor.commit();
				ActSheng.this.finish();
			} else {
				editor.putString("sShengStr", Str);
				editor.commit();
				Intent intent = new Intent(ActSheng.this, ActShi.class);
				startActivity(intent);
				ActSheng.this.finish();
			}
		}
	}

}
