package com.bfz.zhbao.entity;

/**
 * 注册时和服务器数据的交互类
 * @author yangfan
 *
 */
public class IJsonClass {
	
	/**
	 * 手机的imsi码
	 */
	public String imsi;
	/**
	 * 手机号码
	 */
	public String acc;
	/**
	 * 中间插件
	 */
	public String sp;
	/**
	 *服务器返回的密码
	 */
	public String pwd;
	
	@Override
	public String toString() {
		return "IJsonClass [imsi=" + imsi + ", acc=" + acc + ", sp=" + sp + ", pwd=" + pwd + "]";
	}

	
}
