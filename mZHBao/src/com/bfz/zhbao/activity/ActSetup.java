package com.bfz.zhbao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.utils.DialogAll;
import com.bfz.zhbao.utils.MyActivityManagerApplication;

public class ActSetup extends BaseActivity implements OnClickListener{

	static TextView tvSetup = null;
	ImageButton vehicleStatusButton;
	View inputView;
	RelativeLayout location;
	DialogAll inputDialog;
	String titleText;
	String contentText;
	TextView phoneShow,nameShow,vehicleCardShow,statusShow;
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
			case R.id.nameEdit:
				titleText="±‡º≠–’√˚";
				inputDialog.setTitle(titleText);
				inputDialog.setShow(nameShow);
				inputDialog.openDialog(DialogAll.Type.INPUT,inputView);
				
				break;
			case R.id.phoneEdit:
				titleText="±‡º≠∫≈¬Î";
				inputDialog.setTitle(titleText);
				inputDialog.setShow(phoneShow);
				inputDialog.openDialog(DialogAll.Type.INPUT,inputView);
				
				break;
			case R.id.usrInfo:
				Intent intentPerson=new Intent(this,ActPersonnal.class);
				startActivity(intentPerson);
				break;
			case R.id.vehicleCardEdit:
				titleText="±‡º≠≥µ≈∆∫≈";
				inputDialog.setTitle(titleText);
				inputDialog.setShow(vehicleCardShow);
				inputDialog.openDialog(DialogAll.Type.INPUT,inputView);
				
				break;
			case R.id.vehicleStatusEdit:
				titleText="—°‘Ò‘ÿ÷ÿ◊¥Ã¨";
				View checkView=View.inflate(this,R.layout.dialog_check_box, null);
				inputDialog.setTitle(titleText);
				inputDialog.setShow(statusShow);
				inputDialog.openDialog(DialogAll.Type.CHECKBOX,checkView);
				
				break;
			case R.id.vehicleInfo:
				Intent intentVehicle=new Intent(this,ActVehicle.class);
				startActivity(intentVehicle);
				break;
			default:
				break;
		};
		
		
	}

	@Override
	protected void onCreateMainView() {
		setContentView(R.layout.act_setup);
		MyActivityManagerApplication.getInstance().addActivity(this);
		RelativeLayout vehicleStatus=(RelativeLayout)findViewById(R.id.vehicleStatusEdit);
		RelativeLayout nameEdit=(RelativeLayout)findViewById(R.id.nameEdit);
		RelativeLayout phoneEdit=(RelativeLayout)findViewById(R.id.phoneEdit);
		phoneShow=(TextView)findViewById(R.id.phoneShow);
		nameShow=(TextView)findViewById(R.id.nameShow);
		vehicleCardShow=(TextView)findViewById(R.id.vehicleCardShow);
		statusShow=(TextView)findViewById(R.id.vStatusShow);
		RelativeLayout vehicleInfo=(RelativeLayout)findViewById(R.id.vehicleInfo);
		RelativeLayout usrInfo=(RelativeLayout)findViewById(R.id.usrInfo);
		RelativeLayout vehicleCardEdit=(RelativeLayout)findViewById(R.id.vehicleCardEdit);
		
		nameEdit.setOnClickListener(this);
		phoneEdit.setOnClickListener(this);
		usrInfo.setOnClickListener(this);
		vehicleStatus.setOnClickListener(this);
		vehicleInfo.setOnClickListener(this);
		vehicleCardEdit.setOnClickListener(this);
		
		inputView = View.inflate(this,R.layout.dialog_input, null);
		
		location=(RelativeLayout)findViewById(R.id.actSetupHead);
		inputDialog=new DialogAll(location,"") {
			@Override
			protected void defaultOperate(TextView contentText,Button sure, Button cancle) {
				
			}

			@Override
			protected void responseClick(Boolean isSureChecked) {
				// TODO Auto-generated method stub
				if(isSureChecked){
					contentText=this.getText();
					((TextView) this.getShow()).setText(contentText);
					this.setInput("");
				}
			}

			@Override
			protected void responseCheck(int checkedId) {
				// TODO Auto-generated method stub
				switch(inputDialog.getCheckedId()){
				case R.id.emptyBtn:
					((TextView) this.getShow()).setText("ø’‘ÿ");
					break;
				case R.id.unfullBtn:
					((TextView) this.getShow()).setText("∞Î‘ÿ");
					break;
				case R.id.fullBtn:
					((TextView) this.getShow()).setText("¬˙‘ÿ");
					break;
				default:
					break;
			}
			}
		};
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
}
