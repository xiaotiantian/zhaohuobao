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
		 * Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
		 * mAMapLocManager.setGpsEnable(false);
		 * 
		 */
		mAMapLocManager = LocationManagerProxy.getInstance(mContext);
		mAMapLocManager.setGpsEnable(false);
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 1000 * 5, 1000, this);

	}
	
	/**
	 * 此方法已经废弃
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
