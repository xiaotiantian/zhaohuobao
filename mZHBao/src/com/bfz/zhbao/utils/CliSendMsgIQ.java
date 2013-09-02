//package com.bfz.zhbao.utils;
//
//import org.jivesoftware.smack.packet.IQ;
//
//public class CliSendMsgIQ extends IQ {
//
//	private String body;
//
//	public String getElementName() {
//
//		return "c";
//	}
//
//	public String getNamespace() {
//
//		return "com.msg.cli";
//	}
//
//	public void setBody(String body) {
//
//		this.body = body;
//	}
//
//	public String getBody() {
//
//		return body;
//	}
//
//	@Override
//	public String getChildElementXML() {
//
//		if (getBody() == null) {
//			throw new RuntimeException("OnLine body is empty");
//		}
//		StringBuilder sb = new StringBuilder();
//		sb.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">").append(getBody()).append("</")
//				.append(getElementName()).append(">");
//		return sb.toString();
//	}
//}