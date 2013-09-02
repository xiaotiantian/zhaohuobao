package com.bfz.zhbao.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bfz.zhbao.R;
import com.bfz.zhbao.Base.BaseActivity;
import com.bfz.zhbao.utils.MsgInfoParser;
import com.bfz.zhbao.utils.MyActivityManagerApplication;
import com.bfz.zhbao.utils.SysConfig;

@SuppressLint({ "SimpleDateFormat", "UseSparseArrays" })
public class ActGoodsInfo extends BaseActivity {

	ArrayList<MsgInfoParser.Msg> itemList = new ArrayList<MsgInfoParser.Msg>();
	private Handler handler = null;
	private String uImsi = "0";
	private int uLevel = 0;
	private String sSheng = null;
	private String sShi = null;
	private String toReg = null;
	private String toCity = "全部";

	private Button btPrice = null;
	private Button btTime = null;
	private Button btCity = null;

	private ListView lv = null;
	private View loadMoreView = null;
	private Button loadMoreButton = null;

	private MyAdapter mSimpleAdapter = null;
	private ProgressDialog mpDialog = null;
	private int addList = 0;

	Runnable runnableUi = new Runnable() {
		public void run() {
			// 更新界面
			if (addList == 0)
				Toast.makeText(ActGoodsInfo.this, "目前没有到" + toReg + "的信息。", Toast.LENGTH_SHORT).show();

			if (mpDialog != null)
				mpDialog.dismiss();

			mSimpleAdapter = new MyAdapter();
			lv.setAdapter(mSimpleAdapter);
		}
	};
	Runnable runnable = new Runnable() {
		public void run() {
			addItemList();
			mSimpleAdapter.notifyDataSetChanged(); // 数据集变化后,通知adapter
			if (addList == 0) {
				Toast.makeText(ActGoodsInfo.this, "暂无新信息更新。", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ActGoodsInfo.this, "更新 " + addList + "新信息！", Toast.LENGTH_SHORT).show();
			}
			// lv.setSelection(mSimpleAdapter.getCount()-1); //设置选中项
			loadMoreButton.setText("获取更多到" + toReg + "的信息"); // 恢复按钮文字
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// 取消 消息提示
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(R.string.msg_in_notify);
		// 获取 从service 传来的数据
		final String msg = intent.getStringExtra("msg");

		if (msg != null) {
			Thread threadSend = new Thread(new Runnable() {
				public void run() {
					setInfo(msg);
					handler.post(runnableUi);
				}
			});
			mpDialog = new ProgressDialog(ActGoodsInfo.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			mpDialog.setTitle("提示");// 设置标题
			mpDialog.setMessage("正在查找到" + toReg + "货源信息...");
			mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
			mpDialog.show();
			threadSend.start();
		}

	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();


	}

	/**
	 * 
	 * 初始化listview的数据，
	 * 
	 * @param str
	 *            字符串
	 */
	private void setInfo(String str) {

		List<MsgInfoParser.Msg> infos = MsgInfoParser.Parse(str);
		lv.removeFooterView(loadMoreView);
		addList = 0;
		for (Iterator<MsgInfoParser.Msg> iterator = infos.iterator(); iterator.hasNext();) {
			MsgInfoParser.Msg goods = (MsgInfoParser.Msg) iterator.next();

			boolean flag = true;
			for (Iterator<MsgInfoParser.Msg> it = infos.iterator(); iterator.hasNext();) {
				if (goods.getId().toString().equals((String) it.next().getId())) {
					flag = false;
					break;
				}
			}

			if (flag) {
				addList++;
				itemList.add(goods);
			}
		}

		lv.addFooterView(loadMoreView);
	}

	class moreListener implements OnClickListener {
		public void onClick(View v) {

			if (uLevel == 1) {
				// 设置按钮文字loading
				loadMoreButton.setText("获取中...");
				handler.postDelayed(runnable, 2000);
			} else {
				// 试用客户
				Dialog dialog = new AlertDialog.Builder(ActGoodsInfo.this).setTitle("试用提醒").setMessage(R.string.regMsg)
						.setIcon(android.R.drawable.ic_dialog_info).setPositiveButton("确认", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								Uri smsToUri = Uri.parse("smsto:" + SysConfig.spCode);
								Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
								intent.putExtra("sms_body", getString(R.string.sms_body));
								startActivity(intent);
								dialog.cancel();
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

								dialog.cancel();
							}
						}).create();

				dialog.show();

			}

		}
	}

	class sortClickListener implements OnClickListener {
		public void onClick(View v) {
			handler.postDelayed(new Runnable() {
				public void run() {
					
					for (int i = 0; i < itemList.size(); i++) {
						for (int j = i; j < itemList.size(); j++) {
							MsgInfoParser.Msg temp = new MsgInfoParser.Msg();
							String str1 = (String) itemList.get(i).getInfo();
							String str2 = (String) itemList.get(j).getInfo();

							str1 = str1.trim();
							str2 = str2.trim();
							// IF A > B
							if (strToInt(str1) < strToInt(str2)) {
								temp = itemList.get(i);
								itemList.set(i, itemList.get(j));
								itemList.set(j, temp);
							}
						}
					}

					// 数据集变化后,通知adapter
					lv.setAdapter(new MyAdapter());
					mSimpleAdapter.notifyDataSetChanged();
					mpDialog.dismiss();
				}
			}, 2000);

			mpDialog = new ProgressDialog(ActGoodsInfo.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			mpDialog.setTitle("价格最高");// 设置标题
			mpDialog.setMessage("按价格由高到低排序..");
			mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
			mpDialog.show();
			// mpDialog.dismiss();
		}
	}

	class timeClickListener implements OnClickListener {
		public void onClick(View v) {
			handler.postDelayed(new Runnable() {
				public void run() {
					for (int i = 0; i < itemList.size(); i++) {
						for (int j = i; j < itemList.size(); j++) {

							MsgInfoParser.Msg temp = new MsgInfoParser.Msg();
							String str1 = (String) itemList.get(i).getCreateDate();
							String str2 = (String) itemList.get(j).getCreateDate();
							Date date1 = null, date2 = null;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							try {
								date1 = sdf.parse(str1);
								date2 = sdf.parse(str2);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							// IF A > B
							if (date1.compareTo(date2) == -1) {
								temp = itemList.get(i);
								itemList.set(i, itemList.get(j));
								itemList.set(j, temp);
							}
						}
					}
					// 数据集变化后,通知adapter
					lv.setAdapter(new MyAdapter());
					mSimpleAdapter.notifyDataSetChanged();
					mpDialog.dismiss();
				}
			}, 2000);

			mpDialog = new ProgressDialog(ActGoodsInfo.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			mpDialog.setTitle("最新发布");// 设置标题
			mpDialog.setMessage("按发布时间先后排序..");
			mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
			mpDialog.show();
			// mpDialog.dismiss();
		}
	}

	class cityClickListener implements OnClickListener {
		public void onClick(View v) {
			handler.postDelayed(new Runnable() {
				public void run() {
					// 排序
					Collections.sort(itemList, new Comparator<MsgInfoParser.Msg>() {
						public int compare(MsgInfoParser.Msg o1, MsgInfoParser.Msg o2) {
							// return (o2.getValue() - o1.getValue());
							return (o1.getInfoEnd().toString().compareTo(o2.getInfoEnd().toString()));
						}
					});

					// 数据集变化后,通知adapter
					lv.setAdapter(new MyAdapter());
					mSimpleAdapter.notifyDataSetChanged();
					mpDialog.dismiss();
				}
			}, 2000);

			mpDialog = new ProgressDialog(ActGoodsInfo.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			mpDialog.setTitle("城市排序");// 设置标题
			mpDialog.setMessage("按城市先后排序..");
			mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
			mpDialog.show();
			// mpDialog.dismiss();
		}
	}

	private void addItemList() {

		System.out.println("Line:" + uImsi + ",GetInfo:" + sSheng + "," + sShi + "," + toReg + "," + toCity);
		String rsGetInfo = SysConfig.PostMsg(uImsi, "GetInfo", sSheng + "," + sShi + "," + toReg + "," + toCity);
		// System.out.println("GetInfo Result:" + rsGetInfo);

		try {
			List<MsgInfoParser.Msg> infos = new ArrayList<MsgInfoParser.Msg>();
			infos = MsgInfoParser.Parse(rsGetInfo);
			lv.removeFooterView(loadMoreView);
			addList = 0;
			for (Iterator<MsgInfoParser.Msg> iterator = infos.iterator(); iterator.hasNext();) {
				MsgInfoParser.Msg goods = (MsgInfoParser.Msg) iterator.next();
				boolean flag = true;
				for (int i = 0; i < itemList.size(); i++) {
					if (goods.getId().equals(itemList.get(i).getId())) {
						flag = false;
						break;
					}
				}
				if (flag) {
					addList++;
					itemList.add(goods);
				}
			}
			lv.addFooterView(loadMoreView);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int strToInt(String str) {
		Pattern pattern = Pattern.compile("\\d");
		Matcher ma = pattern.matcher(str);
		String temp = "";
		while (ma.find())
			temp += ma.group();
		temp = temp.trim();
		if (temp.equals(""))
			return 0;
		else
			return Integer.parseInt(temp);
	}

	// 以下add 2013-02-26 by 杨星辉
	private class MyAdapter extends BaseAdapter {

		HashMap<Integer, View> map = new HashMap<Integer, View>();

		public int getCount() {
			return itemList.size();
		}

		public Object getItem(int position) {
			return itemList.get(position);
		}

		public boolean isEnabled(int position) {
			return super.isEnabled(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			Result holder = null;
			View view1 = convertView;
			if (map.get(position) == null) {

				view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.goods_info_item, null);

				holder = new Result();
				holder.depname = (TextView) view1.findViewById(R.id.depname);
				holder.datatime = (TextView) view1.findViewById(R.id.datatime);
				holder.msginfo = (TextView) view1.findViewById(R.id.msginfo);
				holder.infoNbr1 = (TextView) view1.findViewById(R.id.infoNbr1);
				holder.infoNbr2 = (TextView) view1.findViewById(R.id.infoNbr2);
				holder.infoNbr3 = (TextView) view1.findViewById(R.id.infoNbr3);
				holder.sharenum = (TextView) view1.findViewById(R.id.sharenum);
				holder.fenxiang = (TextView) view1.findViewById(R.id.fenxiang);
				TextPaint paint = holder.fenxiang.getPaint();
				paint.setFakeBoldText(true);

				final String msgid = itemList.get(position).getId();
				final String phoneNum1 = itemList.get(position).getInfoNbr1();
				final String phoneNum2 = itemList.get(position).getInfoNbr2();
				final String phoneNum3 = itemList.get(position).getInfoNbr3();
				final String msginfo = itemList.get(position).getInfo();

				holder.depname.setText(itemList.get(position).getName());
				holder.datatime.setText(SysConfig.CalculationTime(itemList.get(position).getCreateDate()));
				holder.msginfo.setText(msginfo);
				holder.infoNbr1.setText(phoneNum1);
				holder.infoNbr2.setText(phoneNum2);
				holder.infoNbr3.setText(phoneNum3);
				int num = 0 - Integer.parseInt(itemList.get(position).getNum());
				holder.sharenum.setText("已被" + num + "人阅读，");
				holder.infoNbr1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				holder.infoNbr2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				holder.infoNbr3.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

				holder.infoNbr1.setOnClickListener(new OnClickListener() {// 拨号1

							public void onClick(View paramView) {
								new Thread() {
									public void run() {
										String result = SysConfig.PostMsg(uImsi, "CallPhone", phoneNum1 + "," + msgid).trim();
										System.out.println("CallPhone1:" + result);

									}
								}.start();
								Uri uri = Uri.parse("tel:" + phoneNum1);
								Intent it = new Intent(Intent.ACTION_DIAL, uri);
								startActivity(it);
							}
						});
				holder.infoNbr2.setOnClickListener(new OnClickListener() {// 拨号2

							public void onClick(View paramView) {
								new Thread() {
									public void run() {
										String result = SysConfig.PostMsg(uImsi, "CallPhone", phoneNum2 + "," + msgid).trim();
										System.out.println("CallPhone2:" + result);
									}
								}.start();

								Uri uri = Uri.parse("tel:" + phoneNum2);
								Intent it = new Intent(Intent.ACTION_DIAL, uri);
								startActivity(it);
							}
						});
				holder.infoNbr3.setOnClickListener(new OnClickListener() {// 拨号3

							public void onClick(View paramView) {
								new Thread() {
									public void run() {
										String result = SysConfig.PostMsg(uImsi, "CallPhone", phoneNum3 + "," + msgid).trim();
										System.out.println("CallPhone3:" + result);
									}
								}.start();
								Uri uri = Uri.parse("tel:" + phoneNum3);
								Intent it = new Intent(Intent.ACTION_DIAL, uri);
								startActivity(it);
							}
						});
				holder.fenxiang.setOnClickListener(new OnClickListener() {// 分享

							public void onClick(View paramView) {
								// 调用短信程序
								Uri smsToUri = Uri.parse("smsto:");
								Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
								intent.putExtra("sms_body", msginfo + " " + phoneNum1 + " " + phoneNum2 + " " + phoneNum3 + " " + getString(R.string.fengXiangxx));
								startActivity(intent);

							}
						});

				view1.setTag(holder);
				map.put(position, view1);
			} else {
				view1 = map.get(position);
				holder = (Result) view1.getTag();
			}

			return view1;
		}
	}

	private class Result {
		TextView depname;
		TextView datatime;
		TextView msginfo;
		TextView infoNbr1;
		TextView infoNbr2;
		TextView infoNbr3;
		TextView sharenum;
		TextView fenxiang;
	}

	@Override
	protected void onCreateMainView() {
		
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(R.string.msg_in_notify);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.showinfo);
		 
		MyActivityManagerApplication.getInstance().addActivity(this);
		// 以下 add 2013-02-25 by 杨星辉
		Button bt_back = (Button) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActGoodsInfo.this.finish();
			}
		});

		Button bt_fenxiang = (Button) findViewById(R.id.bt_fenxiang);
		bt_fenxiang.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 调用短信程序
				Uri smsToUri = Uri.parse("smsto:");
				Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
				intent.putExtra("sms_body", getString(R.string.fengXiang));
				startActivity(intent);
			}
		});
		// 以上 add 2013-02-25 by 杨星辉

		TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		uImsi = tm.getSubscriberId();

		btPrice = (Button) findViewById(R.id.btPrice);
		btPrice.setOnClickListener(new sortClickListener());
		btTime = (Button) findViewById(R.id.btTime);
		btTime.setOnClickListener(new timeClickListener());
		btCity = (Button) findViewById(R.id.btCity);
		btCity.setOnClickListener(new cityClickListener());

		lv = (ListView) findViewById(R.id.myListView);
		handler = new Handler();

		loadMoreView = getLayoutInflater().inflate(R.layout.footer, null);
		loadMoreButton = (Button) loadMoreView.findViewById(R.id.loadMoreButton);
		loadMoreButton.setOnClickListener(new moreListener());

		new Thread(new Runnable() {
			public void run() {
				String rsLevel = SysConfig.PostMsg(uImsi, "Level", ",,");
				// System.out.println("rsLevel Result:" + rsLevel);
				if (rsLevel != null) {
					uLevel =  Integer.parseInt(rsLevel.split(",")[1].toString().trim());
				}

			}
		}).start();
		
		
		
		SharedPreferences preferences = getSharedPreferences(SysConfig.PREFS_NAME, Activity.MODE_PRIVATE);
		sSheng = preferences.getString("sShengStr", "陕西");
		sShi = preferences.getString("sShiStr", "榆林");

		toReg = preferences.getString("toReg", "陕西");
		toCity = preferences.getString("toCity", "榆林");

		loadMoreButton.setText("获取更多到" + toReg + "的信息");
		Thread thread = new Thread(new Runnable() {
			public void run() {
				addItemList();
				handler.post(runnableUi);
			}
		});
		mpDialog = new ProgressDialog(ActGoodsInfo.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		mpDialog.setTitle("提示");// 设置标题
		mpDialog.setMessage("正在查找到" + toReg + "货源信息....");
		mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
		mpDialog.show();
		thread.start();
		// threadSend = null;

		if (this.getIntent() != null) {
			final String msg = this.getIntent().getStringExtra("msg");

			if (msg != null) {
				Thread threadSend = new Thread(new Runnable() {
					public void run() {
						setInfo(msg);
						handler.post(runnableUi);
					}
				});
				mpDialog = new ProgressDialog(ActGoodsInfo.this);
				mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
				mpDialog.setTitle("提示");// 设置标题
				mpDialog.setMessage("正在查找到" + toReg + "货源信息...");
				mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
				mpDialog.show();
				threadSend.start();
			}
		}
		
	}

}
