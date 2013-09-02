package com.bfz.zhbao.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
//import android.telephony.TelephonyManager;

/**
 * 
 * ����Ӧ�õ�ȫ�ֲ����� �ڲ����ǿ��԰Ѳ��������ļ�zb.properties �ŵ�sdcard�ĸ�Ŀ¼�� server.port��server.ipΪ�ͻ���������Ϣ�������� ip ��port
 * 
 * @author XueHaiFeng
 * 
 */
public class SysConfig extends Properties {
	
	public static final String PREFS_NAME = "com.bfz.zhbao";
	public static String updateUrl = "http://zhaohuobao.cn:8080/Down/VerUpdate.xml";
	public static String updateUrlTest = "http://zhaohuobao.cn:8080/Down/VerUpdateTest.xml";
	//public static String ActionUrl = "http://apps.zhaohuobao.cn/Actions.aspx?";
	public static String ActionUrl = "http://192.168.1.101/actions.aspx";
	public static String asyncMsgsNumUtl = "http://apps.zhaohuobao.cn/GetCityList.aspx";
	
	public static String spCode = "10669588";

	public static String msgServerIp = "zhaohuobao.cn";// ��Ϣ���ͷ�����ip
	public static int msgServerPort = 5222;
	private static final long serialVersionUID = 1L;
	private static SysConfig config;
	private static String fileName = "zb.properties";
	private static boolean isDebug;

	
//	public static String GetImsi(Context context){
//		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		return tm.getSubscriberId();
//	}
	/**
	 * ����ģʽ ����һ��SysConfigʵ��
	 * 
	 * @return SysConfig
	 */
	public static SysConfig getInstace() {
		if (config == null) {
			config = new SysConfig();
			isDebug = false;
			try {
				boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				if (sdCardExist) {
					File dir = Environment.getExternalStorageDirectory();
					String path = dir.getPath() + "/";
					config.load(new FileInputStream(new File(path + fileName)));
					isDebug = true;
				}
			} catch (FileNotFoundException e) {
				isDebug = false;
			} catch (IOException e) {
				isDebug = false;
			}

		}
		return config;
	}

	/**
	 * 
	 * ��ȡ������ip
	 * 
	 * @return ������ip
	 */
	public String getMsgServerIp() {
		if (config.getProperty("server.ip") != null && isDebug)
			return config.getProperty("server.ip");
		return msgServerIp;
	}

	/**
	 * 
	 * ��ȡ�������˿�
	 * 
	 * @return �������˿�
	 */
	public int getMsgServerPort() {
		if (config.getProperty("server.port") != null && isDebug) {
			try {
				return Integer.parseInt(config.getProperty("server.port"));
			} catch (Exception e) {
				return msgServerPort;
				
			}

		}
		return msgServerPort;
	}

	public static String PostMsg(final String imsi, final String even, final String eved) {
		String strResult = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost ���Ӷ���
			HttpPost httpRequest = new HttpPost(SysConfig.ActionUrl);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("imsi", imsi));
			params.add(new BasicNameValuePair("even", even));
			params.add(new BasicNameValuePair("eved", eved));
			// �����ַ���
			HttpEntity httpentity = new UrlEncodedFormEntity(params, "utf8");
		  
			httpRequest.setEntity(httpentity);
		
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}

		} catch (Exception e) {
			 e.printStackTrace();
		  
			System.out.println("PostMsg Ex��" + e.getMessage());
			strResult = null;
		}
		return strResult;

	}
	
	// �򵥵���
	public static String regList[][] = { { "����" }, { "���" }, { "�ӱ�", "ʯ��ׯ", "����", "����", "�е�", "����", "��ˮ", "�ȷ�", "�ػʵ�", "��ɽ", "��̨", "�żҿ�" },
			{ "���ɹ�", "���ͺ���", "������", "�����׶�", "��ͷ", "���", "���ױ���", "ͨ��", "�ں�", "�����첼", "���ֹ���", "�˰�", "������", "������˹" },
			{ "ɽ��", "̫ԭ", "��ͬ", "����", "����", "�ٷ�", "����", "˷��", "����", "��Ȫ", "�˳�", "����" },
			{ "ɽ��", "����", "����", "����", "��Ӫ", "����", "����", "����", "�ĳ�", "����", "�ൺ", "����", "̩��", "����", "Ϋ��", "��̨", "��ׯ", "�Ͳ�" },

			{ "�Ϻ�" }, { "����", "�Ϸ�", "����", "����", "����", "����", "����", "����", "����", "����", "��ɽ", "����", "��ɽ", "ͭ��", "�ߺ�", "����", "����" },
			{ "�㽭", "����", "����", "����", "��", "��ˮ", "����", "����", "����", "̨��", "����", "��ɽ" },
			{ "����", "�Ͼ�", "����", "����", "���Ƹ�", "��ͨ", "����", "̩��", "����", "��Ǩ", "����", "�γ�", "����", "��" },

			{ "�㶫", "����", "����", "��ɽ", "��Դ", "����", "����", "����", "ï��", "÷��", "��Զ", "��ͷ", "��β", "�ع�", "����", "����", "�Ƹ�", "տ��", "����", "�麣" },
			{ "����", "����", "����", "��ƽ", "����", "����", "Ȫ��", "����", "����", "����" }, { "����", "����", "����", "����", "��ɳ" },
			{ "����", "����", "��ɫ", "����", "����", "���Ǹ�", "���", "����", "�ӳ�", "����", "����", "����", "����", "����", "����" },

			{ "����", "�人", "����", "��ʩ", "����", "�Ƹ�", "��ʯ", "����", "����", "ʮ��", "����", "�差", "Т��", "�˲�" },
			{ "����", "��ɳ", "����", "����", "����", "����", "¦��", "����", "��̶", "����", "����", "����", "����", "�żҽ�", "����" },
			{ "����", "֣��", "����", "�ױ�", "��Դ", "����", "����", "����", "���", "����", "ƽ��ɽ", "���", "����Ͽ", "����", "����", "����", "���", "�ܿ�", "פ���" },
			{ "����", "�ϲ�", "����", "����", "����", "������", "�Ž�", "Ƽ��", "����", "����", "�˴�", "ӥ̶" },

			{ "����", "����", "��ɽ", "��Ϫ", "����", "����", "����", "��˳", "����", "��«��", "����", "����", "�̽�", "����", "Ӫ��" },
			{ "������", "������", "����", "���˰���", "�׸�", "�ں�", "����", "��ľ˹", "ĵ����", "��̨��", "�������", "˫Ѽɽ", "�绯", "����" },
			{ "����", "����", "�׳�", "��ɽ", "����", "��Դ", "��ƽ", "��ԭ", "ͨ��", "�ӱ�" },

			{ "����", "����", "����", "����", "����", "����", "ͭ��", "μ��", "����", "�Ӱ�", "����" },
			{ "����", "����", "����", "����", "����", "������", "���", "��Ȫ", "����", "¤��", "ƽ��", "����", "��ˮ", "����", "��Ҵ" }, { "����", "����", "��ԭ", "ʯ��ɽ", "����", "����" },
			{ "�ຣ", "����", "����", "����", "����", "����", "����", "����", "����" },
			{ "�½�", "��³ľ��", "������", "����̩", "��������", "��������", "����", "����", "����", "��ʲ", "��������", "�������տ¶�����", "����", "��³��", "����" },

			{ "����" }, { "�Ĵ�", "�ɶ�", "����", "����", "�ﴨ", "����", "����", "�㰲", "��Ԫ", "��ɽ", "��ɽ", "����", "üɽ", "����", "�ϳ�", "�ڽ�", "��֦��", "����", "�Ű�", "�˱�", "����", "�Թ�" },
			{ "����", "����", "��ɽ", "����", "����", "�º�", "����", "���", "����", "�ٲ�", "ŭ��", "����", "˼é", "��ɽ", "��˫����", "��Ϫ", "��ͨ" },
			{ "����", "����", "��˳", "�Ͻ�", "����ˮ", "ǭ����", "ǭ��", "ǭ����", "ͭ��", "����" }, { "����", "����", "����", "����", "��֥", "����", "�տ���", "ɽ��" } };
	// ��׼����
	public static String googleList[][] = {
			{ "������" },
			{ "�����" },
			{ "�ӱ�ʡ", "ʯ��ׯ��", "������", "������", "�е���", "������", "��ˮ��", "�ȷ���", "�ػʵ���", "��ɽ��", "��̨��", "�żҿ���" },
			{ "���ɹ�������", "���ͺ�����", "��������", "�����׶���", "��ͷ��", "�����", "���ױ�����", "ͨ����", "�ں���", "�����첼��", "���ֹ�����", "�˰���", "��������", "������˹��" },
			{ "ɽ��ʡ", "̫ԭ��", "��ͬ��", "������", "������", "�ٷ���", "������", "˷����", "������", "��Ȫ��", "�˳���", "������" },
			{ "ɽ��ʡ", "������", "������", "������", "��Ӫ��", "������", "������", "������", "�ĳ���", "������", "�ൺ��", "������", "̩����", "������", "Ϋ����", "��̨��", "��ׯ��", "�Ͳ���" },

			{ "�Ϻ���" },
			{ "����ʡ", "�Ϸ���", "������", "������", "������", "������", "������", "������", "������", "������", "��ɽ��", "������", "��ɽ��", "ͭ����", "�ߺ���", "������", "������" },
			{ "�㽭ʡ", "������", "������", "������", "����", "��ˮ��", "������", "������", "������", "̨����", "������", "��ɽ��" },
			{ "����ʡ", "�Ͼ���", "������", "������", "���Ƹ���", "��ͨ��", "������", "̩����", "������", "��Ǩ��", "������", "�γ���", "������", "����" },

			{ "�㶫ʡ", "������", "��ɽ��", "������", "��Դ��", "������", "������", "������", "ï����", "÷����", "��Զ��", "��ͷ��", "��β��", "�ع���", "������", "������", "�Ƹ���", "տ����", "������", "�麣��" },
			{ "����ʡ", "������", "������", "��ƽ��", "������", "������", "Ȫ����", "������", "������", "������" },
			{ "����ʡ", "������", "������", "������", "��ɳ��" },
			{ "����׳��������", "������", "��ɫ��", "������", "������", "���Ǹ���", "�����", "������", "�ӳ���", "������", "������", "������", "������", "������", "������" },

			{ "����ʡ", "�人��", "������", "��ʩ����������������", "������", "�Ƹ���", "��ʯ��", "������", "������", "ʮ����", "������", "�差��", "Т����", "�˲���" },
			{ "����ʡ", "��ɳ��", "������", "������", "������", "������", "¦����", "������", "��̶��", "��������������������", "������", "������", "������", "�żҽ���", "������" },
			{ "����ʡ", "֣����", "������", "�ױ���", "��Դ��", "������", "������", "������", "�����", "������", "ƽ��ɽ��", "�����", "����Ͽ��", "������", "������", "������", "�����", "�ܿ���", "פ�����" },
			{ "����ʡ", "�ϲ���", "������", "������", "������", "��������", "�Ž���", "Ƽ����", "������", "������", "�˴���", "ӥ̶��" },

			{ "����ʡ", "������", "��ɽ��", "��Ϫ��", "������", "������", "������", "��˳��", "������", "��«����", "������", "������", "�̽���", "������", "Ӫ����" },
			{ "������ʡ", "��������", "������", "���˰������", "�׸���", "�ں���", "������", "��ľ˹��", "ĵ������", "��̨����", "���������", "˫Ѽɽ��", "�绯��", "������" },
			{ "����ʡ", "������", "�׳���", "��ɽ��", "������", "��Դ��", "��ƽ��", "��ԭ��", "ͨ����", "�ӱ���" },

			{ "����ʡ", "������", "������", "������", "������", "������", "ͭ����", "μ����", "������", "�Ӱ���", "������" },
			{ "����ʡ", "������", "������", "������", "���ϲ���������", "��������", "�����", "��Ȫ��", "���Ļ���������", "¤����", "ƽ����", "������", "��ˮ��", "������", "��Ҵ��" },
			{ "���Ļ���������", "������", "��ԭ��", "ʯ��ɽ��", "������", "������" },
			{ "�ຣʡ", "������", "�������������", "��������������", "��������", "���ϲ���������", "�����ɹ������������", "���ϲ���������", "��������������" },
			{ "�½�ά���������", "��³ľ����", "�����յ���", "����̩����", "���������ɹ�������", "���������ɹ�������", "��������������", "���ܵ���", "�������", "��ʲ����", "����������", "�������տ¶�����������", "���ǵ���", "��³������",
					"���������������" },

			{ "������" },
			{ "�Ĵ�ʡ", "�ɶ���", "���Ӳ���Ǽ��������", "������", "�ﴨ��", "������", "���β���������", "�㰲��", "��Ԫ��", "��ɽ��", "��ɽ����������", "������", "üɽ��", "������", "�ϳ���", "�ڽ���", "��֦����", "������",
					"�Ű���", "�˱���", "������", "�Թ���" },
			{ "����ʡ", "������", "��ɽ��", "��������������", "�������������", "�º���徰����������", "�������������", "��ӹ���������������", "������", "�ٲ���", "ŭ��������������", "������", "˼é��", "��ɽ׳������������",
					"��˫���ɴ���������", "��Ϫ��", "��ͨ��" }, { "����ʡ", "������", "��˳��", "�Ͻڵ���", "����ˮ��", "ǭ�������嶱��������", "ǭ�ϲ���������������", "ǭ���ϲ���������������", "ͭ�ʵ���", "������" },
			{ "����������", "������", "�������", "��������", "��֥����", "��������", "�տ������", "ɽ�ϵ���" } };

	// timeToString
	public static String CalculationTime(String date) {

		// date="2012-4-22 10:42:59"; //����ʱ��
		int Year, Month, Day, Hour, Minute, Seconds;
		if (date.indexOf("-") == -1 || date.indexOf(" ") == -1 || date.indexOf(":") == -1) {
			return ("�����ʽ����");
		}
		try {
			Year = Integer.parseInt(date.substring(0, date.indexOf("-")));
			Month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
			Day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, date.indexOf(" ")));
			Hour = Integer.parseInt(date.substring(date.indexOf(" ") + 1, date.indexOf(":")));
			Minute = Integer.parseInt(date.substring(date.indexOf(":") + 1, date.lastIndexOf(":")));
			Seconds = Integer.parseInt(date.substring(date.lastIndexOf(":") + 1));
		} catch (Exception e) {
			return ("�ַ���ת��Ϊ����ʧ��");
		}

		Calendar MyCalendar = Calendar.getInstance();
		MyCalendar.set(Year, Month - 1, Day, Hour, Minute, Seconds);

		long EndTime = MyCalendar.getTimeInMillis(); // �Ѵ�����ַ���ת������
		long StartTime = System.currentTimeMillis(); // �ѵ�ǰ��ʱ��ת������
		long DuringSeconds;
		DuringSeconds = (StartTime - EndTime) / 1000; // ����ʱ���

		long ResultHour = DuringSeconds / 3600;
		long ResultDay = ResultHour / 24;
		ResultHour = ResultHour % 24;

		DuringSeconds = DuringSeconds % 3600;
		long ResultMinute = DuringSeconds / 60;

		if (ResultDay > 1) {
			return Integer.toString((int) ResultDay) + "��ǰ";
		}
		if (ResultHour > 1) {
			return Integer.toString((int) ResultHour) + "Сʱǰ";
		}
		return Integer.toString((int) ResultMinute) + "����ǰ";
	}

	public static boolean NetWorkStatus(Context context) {

		boolean netSataus = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager != null) {
			NetworkInfo[] info = cwjManager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
						return true;
			}

		}

		NetworkInfo activeNetInfo = cwjManager.getActiveNetworkInfo();

		if (activeNetInfo != null) {
			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		return netSataus;
	}

	public static class cfgMsg {
		public String imsi;
		public String fromReg;
		public String fromCity;
		public String toReg;
		public String toCity;
		public double fromLng;
		public double fromLat;
		
	}
}
