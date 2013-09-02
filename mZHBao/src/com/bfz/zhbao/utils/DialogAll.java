package com.bfz.zhbao.utils;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bfz.zhbao.R;

public abstract class DialogAll {
	protected View view = null;
	private View Show;
	private Button sureBtn;
	private Button cancleBtn;
	public Boolean isSureChecked=false;
	private EditText inputText;
	private String titleString;
	private View location;
	private TextView contentText;  
	private int chekedID;
	private RadioGroup checkGroup;
	public enum Type{
		INPUT,PRESENT,CHECKBOX
	}
	public Type dialogType;
	
	
	public DialogAll(View locationFrom,String title){
		
		
//		if(i==0||i==1){
//			
//			if(i==0){
//				dialogType=Type.INPUT;
//				inputText=(EditText)view.findViewById(R.id.inputText);
//			}else{
//				dialogType=Type.PRESENT;
//				
//			}
//		}else{
//			dialogType=Type.CHECKBOX;
//			checkGroup=(RadioGroup)view.findViewById(R.id.CheckGroup);
//		}
//		
//		this.view=view;
		setTitle(title);
		setLocation(locationFrom);
	}
	
	
	public void setTitle(String title){
		this.titleString=title;
	}
	public void setInput(String input){
		this.inputText.setText(input);
	}
    public String getText(){
		return inputText.getText().toString();
	}
    public int getCheckedId(){
    	return chekedID;
    }
    private void setCheckedId(int id){
    	chekedID=id;
    }
    private void initInputDialog(){
    	inputText=(EditText)view.findViewById(R.id.inputText);
    	
    }
    private void initPresentDialog(){
    	contentText=(TextView)view.findViewById(R.id.contentText);
    	
    }
    private void initCheckDialog(){
    	checkGroup=(RadioGroup)view.findViewById(R.id.CheckGroup);
    	
    }
	protected void setLocation(View id){
		this.location=id;
	}
	public void setView(View id){
		this.view=id;
	}
	public void setShow(View show){
		Show=show;
	}
	public View getShow(){
		return Show;
	}
	protected abstract void responseCheck(int checkedId);
	protected abstract void responseClick(Boolean isSureChecked);
	protected abstract void defaultOperate(TextView contentText,Button sure,Button cancle);
	
	
	public void openDialog(Type diaType,View view){
		dialogType=diaType;
		setView(view);
		TextView titleText=(TextView)view.findViewById(R.id.titleText);
		titleText.setText(titleString);

		final PopupWindow mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.showAtLocation(location, Gravity.CENTER, 0, 0);
		mPopupWindow.setAnimationStyle(R.style.animationmsg);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();

		if(!dialogType.equals(Type.CHECKBOX)){
			sureBtn= (Button)view.findViewById(R.id.sure);
			cancleBtn=(Button)view.findViewById(R.id.cancle);
			if(dialogType.equals(Type.PRESENT)){
				initPresentDialog();
				defaultOperate(contentText,sureBtn,cancleBtn);
			}else{
				initInputDialog();
			}
			
			sureBtn.setOnClickListener(new OnClickListener() {	
				public void onClick(View v) {
					isSureChecked=true;
					responseClick(isSureChecked);
					mPopupWindow.dismiss();
				}
			});
			cancleBtn.setOnClickListener(new OnClickListener() {			
				public void onClick(View v) {
					isSureChecked=false;
					responseClick(isSureChecked);
					mPopupWindow.dismiss();
				}
			});
		 }else{
			 initCheckDialog();
			 checkGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
			          
		        public void onCheckedChanged(RadioGroup group, int checkedId) {  
		        	setCheckedId(checkedId); 
		        	responseCheck(checkedId);
		        	mPopupWindow.dismiss();
		        }  
			});
		}
		
		
	}
	
}
