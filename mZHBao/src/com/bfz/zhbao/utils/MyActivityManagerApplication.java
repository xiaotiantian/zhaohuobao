package com.bfz.zhbao.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Parcelable;

import com.bfz.zhbao.R;
import com.bfz.zhbao.activity.ActSplash;


public class MyActivityManagerApplication extends Application {

	 private List<Activity> activityList = new LinkedList<Activity>();
	 
	 private static MyActivityManagerApplication instance;
	    @Override
		public void onCreate() {
			super.onCreate();
		}

		public synchronized static MyActivityManagerApplication getInstance() {

	        if (null == instance) {
	            instance = new MyActivityManagerApplication();
	        }

	        return instance;

	    }

	    public void addActivity(Activity activity) {

	        activityList.add(activity);
	       
	    }
	    public void exit() {
	        for (Activity activity : activityList) {
	            activity.finish();
	        }
	         
	        System.exit(0);
	    }
}
