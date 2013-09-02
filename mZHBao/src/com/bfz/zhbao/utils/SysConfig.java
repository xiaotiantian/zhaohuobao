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
 * 配置应用的全局参数， 在测试是可以把测试配置文件zb.properties 放到sdcard的根目录里 server.port、server.ip为客户端连接消息服务器的 ip 和port
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

	public static String msgServerIp = "zhaohuobao.cn";// 消息推送服务器ip
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
	 * 单例模式 返回一个SysConfig实体
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
	 * 获取服务器ip
	 * 
	 * @return 服务器ip
	 */
	public String getMsgServerIp() {
		if (config.getProperty("server.ip") != null && isDebug)
			return config.getProperty("server.ip");
		return msgServerIp;
	}

	/**
	 * 
	 * 获取服务器端口
	 * 
	 * @return 服务器端口
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
			// HttpPost 连接对象
			HttpPost httpRequest = new HttpPost(SysConfig.ActionUrl);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("imsi", imsi));
			params.add(new BasicNameValuePair("even", even));
			params.add(new BasicNameValuePair("eved", eved));
			// 设置字符集
			HttpEntity httpentity = new UrlEncodedFormEntity(params, "utf8");
		  
			httpRequest.setEntity(httpentity);
		
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}

		} catch (Exception e) {
			 e.printStackTrace();
		  
			System.out.println("PostMsg Ex：" + e.getMessage());
			strResult = null;
		}
		return strResult;

	}
	
	// 简单地名
	public static String regList[][] = { { "北京" }, { "天津" }, { "河北", "石家庄", "保定", "沧州", "承德", "邯郸", "衡水", "廊坊", "秦皇岛", "唐山", "邢台", "张家口" },
			{ "内蒙古", "呼和浩特", "阿拉善", "巴彦淖尔", "包头", "赤峰", "呼伦贝尔", "通辽", "乌海", "乌兰察布", "锡林郭勒", "兴安", "伊克昭", "鄂尔多斯" },
			{ "山西", "太原", "大同", "晋城", "晋中", "临汾", "吕梁", "朔州", "忻州", "阳泉", "运城", "长治" },
			{ "山东", "济南", "滨州", "德州", "东营", "菏泽", "济宁", "莱芜", "聊城", "临沂", "青岛", "日照", "泰安", "威海", "潍坊", "烟台", "枣庄", "淄博" },

			{ "上海" }, { "安徽", "合肥", "安庆", "蚌埠", "巢湖", "池州", "滁州", "阜阳", "淮北", "淮南", "黄山", "六安", "马鞍山", "铜陵", "芜湖", "宿州", "宣城" },
			{ "浙江", "杭州", "湖州", "嘉兴", "金华", "丽水", "宁波", "衢州", "绍兴", "台州", "温州", "舟山" },
			{ "江苏", "南京", "常州", "淮阴", "连云港", "南通", "苏州", "泰州", "无锡", "宿迁", "徐州", "盐城", "扬州", "镇江" },

			{ "广东", "广州", "潮州", "佛山", "河源", "惠州", "江门", "揭阳", "茂名", "梅州", "清远", "汕头", "汕尾", "韶关", "深圳", "阳江", "云浮", "湛江", "肇庆", "珠海" },
			{ "福建", "福州", "龙岩", "南平", "宁德", "莆田", "泉州", "三明", "厦门", "漳州" }, { "海南", "海口", "海南", "三亚", "三沙" },
			{ "广西", "南宁", "百色", "北海", "崇左", "防城港", "贵港", "桂林", "河池", "贺州", "来宾", "柳州", "钦州", "梧州", "玉林" },

			{ "湖北", "武汉", "鄂州", "恩施", "湖北", "黄冈", "黄石", "荆门", "荆州", "十堰", "咸宁", "襄樊", "孝感", "宜昌" },
			{ "湖南", "长沙", "常德", "郴州", "衡阳", "怀化", "娄底", "邵阳", "湘潭", "湘西", "益阳", "永州", "岳阳", "张家界", "株洲" },
			{ "河南", "郑州", "安阳", "鹤壁", "济源", "焦作", "开封", "洛阳", "漯河", "南阳", "平顶山", "濮阳", "三门峡", "商丘", "新乡", "信阳", "许昌", "周口", "驻马店" },
			{ "江西", "南昌", "抚州", "赣州", "吉安", "景德镇", "九江", "萍乡", "上饶", "新余", "宜春", "鹰潭" },

			{ "辽宁", "沈阳", "鞍山", "本溪", "朝阳", "大连", "丹东", "抚顺", "阜新", "葫芦岛", "锦州", "辽阳", "盘锦", "铁岭", "营口" },
			{ "黑龙江", "哈尔滨", "大庆", "大兴安岭", "鹤岗", "黑河", "鸡西", "佳木斯", "牡丹江", "七台河", "齐齐哈尔", "双鸭山", "绥化", "伊春" },
			{ "吉林", "长春", "白城", "白山", "吉林", "辽源", "四平", "松原", "通化", "延边" },

			{ "陕西", "西安", "安康", "宝鸡", "汉中", "商洛", "铜川", "渭南", "咸阳", "延安", "榆林" },
			{ "甘肃", "兰州", "白银", "定西", "甘南", "嘉峪关", "金昌", "酒泉", "临夏", "陇南", "平凉", "庆阳", "天水", "武威", "张掖" }, { "宁夏", "银川", "固原", "石嘴山", "吴忠", "中卫" },
			{ "青海", "西宁", "果洛", "海北", "海东", "海南", "海西", "黄南", "玉树" },
			{ "新疆", "乌鲁木齐", "阿克苏", "阿勒泰", "巴音郭楞", "博尔塔拉", "昌吉", "哈密", "和田", "喀什", "克拉玛依", "克孜勒苏柯尔克孜", "塔城", "吐鲁番", "伊犁" },

			{ "重庆" }, { "四川", "成都", "阿坝", "巴中", "达川", "德阳", "甘孜", "广安", "广元", "乐山", "凉山", "泸州", "眉山", "绵阳", "南充", "内江", "攀枝花", "遂宁", "雅安", "宜宾", "资阳", "自贡" },
			{ "云南", "昆明", "保山", "楚雄", "大理", "德宏", "迪庆", "红河", "丽江", "临沧", "怒江", "曲靖", "思茅", "文山", "西双版纳", "玉溪", "昭通" },
			{ "贵州", "贵阳", "安顺", "毕节", "六盘水", "黔东南", "黔南", "黔西南", "铜仁", "遵义" }, { "西藏", "拉萨", "阿里", "昌都", "林芝", "那曲", "日喀则", "山南" } };
	// 标准地名
	public static String googleList[][] = {
			{ "北京市" },
			{ "天津市" },
			{ "河北省", "石家庄市", "保定市", "沧州市", "承德市", "邯郸市", "衡水市", "廊坊市", "秦皇岛市", "唐山市", "邢台市", "张家口市" },
			{ "内蒙古自治区", "呼和浩特市", "阿拉善盟", "巴彦淖尔市", "包头市", "赤峰市", "呼伦贝尔市", "通辽市", "乌海市", "乌兰察布市", "锡林郭勒盟", "兴安盟", "伊克昭盟", "鄂尔多斯市" },
			{ "山西省", "太原市", "大同市", "晋城市", "晋中市", "临汾市", "吕梁市", "朔州市", "忻州市", "阳泉市", "运城市", "长治市" },
			{ "山东省", "济南市", "滨州市", "德州市", "东营市", "菏泽市", "济宁市", "莱芜市", "聊城市", "临沂市", "青岛市", "日照市", "泰安市", "威海市", "潍坊市", "烟台市", "枣庄市", "淄博市" },

			{ "上海市" },
			{ "安徽省", "合肥市", "安庆市", "蚌埠市", "巢湖市", "池州市", "滁州市", "阜阳市", "淮北市", "淮南市", "黄山市", "六安市", "马鞍山市", "铜陵市", "芜湖市", "宿州市", "宣城市" },
			{ "浙江省", "杭州市", "湖州市", "嘉兴市", "金华市", "丽水市", "宁波市", "衢州市", "绍兴市", "台州市", "温州市", "舟山市" },
			{ "江苏省", "南京市", "常州市", "淮阴市", "连云港市", "南通市", "苏州市", "泰州市", "无锡市", "宿迁市", "徐州市", "盐城市", "扬州市", "镇江市" },

			{ "广东省", "潮州市", "佛山市", "广州市", "河源市", "惠州市", "江门市", "揭阳市", "茂名市", "梅州市", "清远市", "汕头市", "汕尾市", "韶关市", "深圳市", "阳江市", "云浮市", "湛江市", "肇庆市", "珠海市" },
			{ "福建省", "福州市", "龙岩市", "南平市", "宁德市", "莆田市", "泉州市", "三明市", "厦门市", "漳州市" },
			{ "海南省", "海口市", "海南市", "三亚市", "三沙市" },
			{ "广西壮族自治区", "南宁市", "百色市", "北海市", "崇左市", "防城港市", "贵港市", "桂林市", "河池市", "贺州市", "来宾市", "柳州市", "钦州市", "梧州市", "玉林市" },

			{ "湖北省", "武汉市", "鄂州市", "恩施土家族苗族自治州", "湖北市", "黄冈市", "黄石市", "荆门市", "荆州市", "十堰市", "咸宁市", "襄樊市", "孝感市", "宜昌市" },
			{ "湖南省", "长沙市", "常德市", "郴州市", "衡阳市", "怀化市", "娄底市", "邵阳市", "湘潭市", "湘西土家族苗族自治州", "益阳市", "永州市", "岳阳市", "张家界市", "株洲市" },
			{ "河南省", "郑州市", "安阳市", "鹤壁市", "济源市", "焦作市", "开封市", "洛阳市", "漯河市", "南阳市", "平顶山市", "濮阳市", "三门峡市", "商丘市", "新乡市", "信阳市", "许昌市", "周口市", "驻马店市" },
			{ "江西省", "南昌市", "抚州市", "赣州市", "吉安市", "景德镇市", "九江市", "萍乡市", "上饶市", "新余市", "宜春市", "鹰潭市" },

			{ "辽宁省", "沈阳市", "鞍山市", "本溪市", "朝阳市", "大连市", "丹东市", "抚顺市", "阜新市", "葫芦岛市", "锦州市", "辽阳市", "盘锦市", "铁岭市", "营口市" },
			{ "黑龙江省", "哈尔滨市", "大庆市", "大兴安岭地区", "鹤岗市", "黑河市", "鸡西市", "佳木斯市", "牡丹江市", "七台河市", "齐齐哈尔市", "双鸭山市", "绥化市", "伊春市" },
			{ "吉林省", "长春市", "白城市", "白山市", "吉林市", "辽源市", "四平市", "松原市", "通化市", "延边市" },

			{ "陕西省", "西安市", "安康市", "宝鸡市", "汉中市", "商洛市", "铜川市", "渭南市", "咸阳市", "延安市", "榆林市" },
			{ "甘肃省", "兰州市", "白银市", "定西市", "甘南藏族自治州", "嘉峪关市", "金昌市", "酒泉市", "临夏回族自治州", "陇南市", "平凉市", "庆阳市", "天水市", "武威市", "张掖市" },
			{ "宁夏回族自治区", "银川市", "固原市", "石嘴山市", "吴忠市", "中卫市" },
			{ "青海省", "西宁市", "果洛藏族自治州", "海北藏族自治州", "海东地区", "海南藏族自治州", "海西蒙古族藏族自治州", "黄南藏族自治州", "玉树藏族自治州" },
			{ "新疆维吾尔自治区", "乌鲁木齐市", "阿克苏地区", "阿勒泰地区", "巴音郭楞蒙古自治州", "博尔塔拉蒙古自治州", "昌吉回族自治州", "哈密地区", "和田地区", "喀什地区", "克拉玛依市", "克孜勒苏柯尔克孜自治州", "塔城地区", "吐鲁番地区",
					"伊犁哈萨克自治州" },

			{ "重庆市" },
			{ "四川省", "成都市", "阿坝藏族羌族自治州", "巴中市", "达川市", "德阳市", "甘孜藏族自治州", "广安市", "广元市", "乐山市", "凉山彝族自治州", "泸州市", "眉山市", "绵阳市", "南充市", "内江市", "攀枝花市", "遂宁市",
					"雅安市", "宜宾市", "资阳市", "自贡市" },
			{ "云南省", "昆明市", "保山市", "楚雄彝族自治州", "大理白族自治州", "德宏傣族景颇族自治州", "迪庆藏族自治州", "红河哈尼族彝族自治州", "丽江市", "临沧市", "怒江傈僳族自治州", "曲靖市", "思茅区", "文山壮族苗族自治州",
					"西双版纳傣族自治州", "玉溪市", "昭通市" }, { "贵州省", "贵阳市", "安顺市", "毕节地区", "六盘水市", "黔东南苗族侗族自治州", "黔南布依族苗族自治州", "黔西南布依族苗族自治州", "铜仁地区", "遵义市" },
			{ "西藏自治区", "拉萨市", "阿里地区", "昌都地区", "林芝地区", "那曲地区", "日喀则地区", "山南地区" } };

	// timeToString
	public static String CalculationTime(String date) {

		// date="2012-4-22 10:42:59"; //测试时间
		int Year, Month, Day, Hour, Minute, Seconds;
		if (date.indexOf("-") == -1 || date.indexOf(" ") == -1 || date.indexOf(":") == -1) {
			return ("传入格式错误");
		}
		try {
			Year = Integer.parseInt(date.substring(0, date.indexOf("-")));
			Month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
			Day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, date.indexOf(" ")));
			Hour = Integer.parseInt(date.substring(date.indexOf(" ") + 1, date.indexOf(":")));
			Minute = Integer.parseInt(date.substring(date.indexOf(":") + 1, date.lastIndexOf(":")));
			Seconds = Integer.parseInt(date.substring(date.lastIndexOf(":") + 1));
		} catch (Exception e) {
			return ("字符串转换为整型失败");
		}

		Calendar MyCalendar = Calendar.getInstance();
		MyCalendar.set(Year, Month - 1, Day, Hour, Minute, Seconds);

		long EndTime = MyCalendar.getTimeInMillis(); // 把传入的字符串转换成秒
		long StartTime = System.currentTimeMillis(); // 把当前的时间转换成秒
		long DuringSeconds;
		DuringSeconds = (StartTime - EndTime) / 1000; // 计算时间差

		long ResultHour = DuringSeconds / 3600;
		long ResultDay = ResultHour / 24;
		ResultHour = ResultHour % 24;

		DuringSeconds = DuringSeconds % 3600;
		long ResultMinute = DuringSeconds / 60;

		if (ResultDay > 1) {
			return Integer.toString((int) ResultDay) + "天前";
		}
		if (ResultHour > 1) {
			return Integer.toString((int) ResultHour) + "小时前";
		}
		return Integer.toString((int) ResultMinute) + "分钟前";
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
