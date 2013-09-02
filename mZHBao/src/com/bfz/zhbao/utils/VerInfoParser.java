package com.bfz.zhbao.utils;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;


/**
 * 解析Xml文件
 * ParseXmlUtils.java
 * @author XueHaifeng
 * 2013-7-8
 */

public class VerInfoParser {

	public static class VerInfo {
		public String version;	//版本号
		public String url;  	//新版本存放url路径
		public String description;   //更新说明信息，比如新增什么功能特性等
	}
	/**
	 * 通过InputStream 解析文件
	 * @param in
	 * @return VerInfo
	 */
	public static VerInfo parseXml(InputStream in){
		final VerInfo zhbVerInfo = new VerInfo();

		RootElement root = new RootElement("info");
		//Element item = root.getChild("info");
		root.getChild("version").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						zhbVerInfo.version = body;
					}
				});
		root.getChild("url").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						zhbVerInfo.url = body;
					}
				});
		root.getChild("description").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						zhbVerInfo.description = body;
					}
				});

	    try {
			
			Xml.parse(in, Xml.Encoding.UTF_8, root.getContentHandler());

		} catch (SAXException e) {
			e.printStackTrace();
		} catch(IOException ex)
		{
			ex.printStackTrace();
		}
	    
		return zhbVerInfo;
	}

	

}
