package com.bfz.zhbao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bfz.zhbao.R;
import com.bfz.zhbao.utils.DialogAll;


public class ActVehicle extends Activity implements OnClickListener{
	private String contentText;
	private String titleText;
	private Spinner tuogua;
	private Spinner huoxiang;
	private RelativeLayout vNumberEdit;
	private TextView vNumberShow;
	private RelativeLayout vLengthEdit;
	private TextView vLengthShow;
	private RelativeLayout vWeightEdit;
	private TextView vWeightShow;
	private RelativeLayout vPathEdit;
	private TextView vPathShow;
	private ImageButton vehiclePhoto1;
	private ImageButton vehiclePhoto2;
	private ImageButton vehiclePhoto3;
	DialogAll inputDialog;
	private View inputView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_vehicle);
		initialization();
		SetSpinner();
		
		inputView=View.inflate(this, R.layout.dialog_input, null);
		RelativeLayout location=(RelativeLayout)findViewById(R.id.LayoutOfHead);
		inputDialog=new DialogAll(location,"") {
			
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
				
			}
			
			@Override
			protected void defaultOperate(TextView contentText, Button sure, Button cancle) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	
	private void SetSpinner(){
		ArrayAdapter<CharSequence> tadapter = ArrayAdapter.createFromResource(this, R.array.tuogualunzhou, android.R.layout.simple_spinner_item);
		tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		tuogua.setAdapter(tadapter);
		
		ArrayAdapter<CharSequence> hadapter = ArrayAdapter.createFromResource(this, R.array.huoxiangjiegou, android.R.layout.simple_spinner_item);
		hadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		huoxiang.setAdapter(hadapter);
	}
	
	private void initialization(){
		tuogua = (Spinner)findViewById(R.id.tuogua);
		huoxiang = (Spinner)findViewById(R.id.huoxiang); 
		vNumberEdit=(RelativeLayout)findViewById(R.id.vNumberEdit);
		vLengthEdit=(RelativeLayout)findViewById(R.id.vLengthEdit);
		vWeightEdit=(RelativeLayout)findViewById(R.id.vWeightEdit);
		vPathEdit=(RelativeLayout)findViewById(R.id.vPathEdit);
		vehiclePhoto1=(ImageButton)findViewById(R.id.vehiclePhoto1);
		vehiclePhoto2=(ImageButton)findViewById(R.id.vehiclePhoto2);
		vehiclePhoto3=(ImageButton)findViewById(R.id.vehiclePhoto3);
		vNumberShow=(TextView)findViewById(R.id.vNumberShow);
		vLengthShow=(TextView)findViewById(R.id.vLengthShow);
		vWeightShow=(TextView)findViewById(R.id.vWeightShow);
		vPathShow=(TextView)findViewById(R.id.vPathShow);
		
		vNumberEdit.setOnClickListener(this);
		vLengthEdit.setOnClickListener(this);
		vWeightEdit.setOnClickListener(this);
		vPathEdit.setOnClickListener(this);
		vehiclePhoto1.setOnClickListener(this);
		vehiclePhoto2.setOnClickListener(this);
		vehiclePhoto3.setOnClickListener(this);
		
		
	}
	
	 public void show_click(View v){
	    	startActivity(new Intent(this,ImageShower.class));
	 }


	public void onClick(View v) {
		switch (v.getId()){
		
		case R.id.vNumberEdit :
			titleText="±à¼­³µÅÆ";
			inputDialog.setTitle(titleText);
			inputDialog.setShow(vNumberShow);
			inputDialog.openDialog(DialogAll.Type.INPUT,inputView);
			
			break;
		case R.id.vLengthEdit :
			titleText="±à¼­³µ³¤";
			inputDialog.setTitle(titleText);
			inputDialog.setShow(vLengthShow);
			inputDialog.openDialog(DialogAll.Type.INPUT,inputView);
			
			break;
		case R.id.vWeightEdit :
			titleText="±à¼­¶ÖÎ»";
			inputDialog.setTitle(titleText);
			inputDialog.setShow(vWeightShow);
			inputDialog.openDialog(DialogAll.Type.INPUT,inputView);
			
			break;
		case R.id.vPathEdit :
			titleText="±à¼­ÏßÂ·";
			inputDialog.setTitle(titleText);
			inputDialog.setShow(vPathShow);
			inputDialog.openDialog(DialogAll.Type.INPUT,inputView);
			
			break;
		
		
		}
		
	}
	
	
}
