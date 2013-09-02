package com.bfz.zhbao.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class RetInfoParser {

	public static class RetInfo {

		public String ret;

		public RetInfo copy() {

			RetInfo tmp = new RetInfo();
			tmp.ret = this.ret;

			return tmp;
		}
	}

	private InputStream in;

	/**
	 * 
	 * @param xmlStr
	 *            字符串
	 */

	public RetInfoParser(String xmlStr) {

		in = new ByteArrayInputStream(xmlStr.getBytes());
	}

	/**
	 * 
	 * 字符串转化成 java类
	 * 
	 * @return ArrayList<RetInfo> 返回list对象
	 */
	public ArrayList<RetInfo> parse() {

		final RetInfo currentMessage = new RetInfo();

		RootElement root = new RootElement("ROOT");

		final ArrayList<RetInfo> messages = new ArrayList<RetInfo>();
		// Element item = root.getChild("NODE");

		root.setEndElementListener(new EndElementListener() {
			public void end() {
				messages.add(currentMessage.copy());
			}
		});

		root.getChild("ADDRESSEND").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.ret = body;
			}
		});

		try {
			Xml.parse(in, Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (Exception e) {
			return messages;
		}
		return messages;
	}

}
