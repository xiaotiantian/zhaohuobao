package com.bfz.zhbao.activity;


import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.entity.ChatMessage;




public class Actadapter extends BaseAdapter {
	private Context cxt;
	private LayoutInflater inflater;
	private List<ChatMessage> listMsg;
	private MediaPlayer mediaPlayer = null;
	public Actadapter(Context formClient) {
		cxt = formClient;
	}
	public List<ChatMessage> getListMsg() {
		return listMsg;
	}
	public void setListMsg(List<ChatMessage> listMsg) {
		this.listMsg = listMsg;
	}
	public int getCount() {
		return listMsg.size();
	}
	public Object getItem(int position) {
	
		return listMsg.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
    public View getView(int position, View convertView, ViewGroup parent) {
		this.inflater = (LayoutInflater) this.cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int state = Integer.parseInt(listMsg.get(position).getMsgType());	
			switch(state){
			case 0:
				if(listMsg.get(position).getMsgState().equals("IN")){
					convertView = this.inflater.inflate(R.layout.act_chat_in, null);	
				}else{
					convertView = this.inflater.inflate(R.layout.act_chat_out, null);
				}
				TextView msgView = (TextView) convertView.findViewById(R.id.formclient_row_msg);
				msgView.setText(listMsg.get(position).getMsgBody());
				break;
			case 1:
				 final String path = listMsg.get(position).getMsgPath();
				if(listMsg.get(position).getMsgState().equals("IN")){
					convertView = this.inflater.inflate(R.layout.act_voice_in, null);
				}else{
					convertView = this.inflater.inflate(R.layout.act_voice_out, null);
				}
				ImageView button = (ImageView)convertView.findViewById(R.id.formclient_row_voice_in);
				
				button.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						Toast.makeText(cxt, "¿ªÊ¼²¥·ÅÂ¼Òô"+path,Toast.LENGTH_SHORT).show();
						try {
							mediaPlayer = new MediaPlayer();
							mediaPlayer.setDataSource(path);
							mediaPlayer.prepare();
						} catch (Exception e) {
							e.printStackTrace();
						}
						mediaPlayer.start();
					}
					
				});
				break;
			case 2:
				if(listMsg.get(position).getMsgState().equals("IN")){
					
					final String PicPath = listMsg.get(position).getMsgPath();
					convertView = this.inflater.inflate(R.layout.act_picture_in, null);		
			        Bitmap inbitmap = BitmapFactory.decodeFile(PicPath,options());
					ImageView iView = (ImageView)convertView.findViewById(R.id.formclient_row_photo_in); 
					iView.setImageBitmap(inbitmap);
					
					break;
				}else{
					final String PicPath = listMsg.get(position).getMsgPath();
					convertView = this.inflater.inflate(R.layout.act_picture_out, null);
					Bitmap outbitmap = BitmapFactory.decodeFile(PicPath,options());
					ImageView iView = (ImageView)convertView.findViewById(R.id.formclient_row_photo_out); 
				    iView.setImageBitmap(outbitmap);
				   
				    break;
				}	
			   default:
			        break;
			}	 	
		return convertView;
	}
    
    public BitmapFactory.Options options(){
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inSampleSize = 2;
    	return options; 
    }
}
