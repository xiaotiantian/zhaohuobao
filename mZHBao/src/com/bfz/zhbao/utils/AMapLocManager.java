package com.bfz.zhbao.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.bfz.zhbao.utils.SysConfig.cfgMsg;

public class AMapLocManager implements AMapLocationListener{

	LocationManagerProxy mAMapLocManager = null;
	Context mContext;
	Handler mHandler;
	
	public AMapLocManager(Context context,Handler handler){
		this.mContext = context;
		this.mHandler = handler;

		/*
		 * Location API��λ����GPS�������϶�λ��ʽ��ʱ�������5000����
		 * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true
		 * mAMapLocManager.setGpsEnable(false);
		 * 
		 */
		mAMapLocManager = LocationManagerProxy.getInstance(mContext);
		mAMapLocManager.setGpsEnable(false);
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 1000 * 5, 1000, this);

	}
	
	/**
	 * �˷����Ѿ�����
	 */
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub

		if (location != null) {
			cfgMsg cmsg = new cfgMsg();
			cmsg.imsi = "";
			cmsg.fromReg = location.getProvince();
			cmsg.fromCity = location.getCity();
			cmsg.fromLat =  location.getLatitude();
			cmsg.fromLng = location.getLongitude();
			
			if (mAMapLocManager != null) {
				mAMapLocManager.removeUpdates(this);
				mAMapLocManager.destory();
			}
			mAMapLocManager = null;
			
//			if (ClientUtil.getConnection().isConnected()) {
//				CliSendMsgIQ sendPack = new CliSendMsgIQ();
//				sendPack.setBody("<![CDATA[{\"msg\":{\"cellId\":\"" + cmsg.imsi + "\",\"lng\":\"" + cmsg.fromLng + "\",\"lat\":\"" + cmsg.fromLat + "\"}}]]>");
//				ClientUtil.getConnection().sendPacket(sendPack);
//				
//			}
			
			Message msg = new Message();
			msg.what = 0;
			msg.obj = cmsg;
			if (mHandler != null) {
				mHandler.sendMessage(msg);
			}
		}
		
	}

}
