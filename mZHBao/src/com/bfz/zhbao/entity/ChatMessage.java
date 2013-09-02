package com.bfz.zhbao.entity;

import java.io.Serializable;
/**
 * 消息实体类
 * @author yangfan
 *
 */
@SuppressWarnings("serial")
public class ChatMessage implements Serializable{

	/**
	 * 消息来自哪里
	 */
	private String MsgFrom;
	/**
	 * 消息去向哪里
	 */
	private String MsgTo;
	/**
	 * 消息的Body
	 */
	private String MsgBody;
	/**
	 * 消息的路径（针对文件的发送）
	 */
	private String MsgPath;
	/**
	 * 消息的类型（文字"0"，语音"1"，图片"2"，文件"3"）
	 */
	private String MsgType;
	/**
	 * 消息的发送或者接受（IN  或者   OUT）
	 */
	private String MsgState;
	/**
	 * 是否读取（针对离线消息和文件）
	 */
	private String ifRead;
	
	private String MsgTime;
	
	public ChatMessage() {
		
	}

	public ChatMessage(String msgFrom, String msgTo, String msgBody, String msgPath, String msgType, String msgState,
			String ifRead, String msgTime) {
		super();
		MsgFrom = msgFrom;
		MsgTo = msgTo;
		MsgBody = msgBody;
		MsgPath = msgPath;
		MsgType = msgType;
		MsgState = msgState;
		this.ifRead = ifRead;
		MsgTime = msgTime;
	}



	public String getMsgFrom() {
		return MsgFrom;
	}

	public void setMsgFrom(String msgFrom) {
		MsgFrom = msgFrom;
	}

	public String getMsgTo() {
		return MsgTo;
	}

	public void setMsgTo(String msgTo) {
		MsgTo = msgTo;
	}

	public String getMsgBody() {
		return MsgBody;
	}

	public void setMsgBody(String msgBody) {
		MsgBody = msgBody;
	}

	public String getMsgPath() {
		return MsgPath;
	}

	public void setMsgPath(String msgPath) {
		MsgPath = msgPath;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getMsgState() {
		return MsgState;
	}

	public void setMsgState(String msgState) {
		MsgState = msgState;
	}

	public String getIfRead() {
		return ifRead;
	}

	public void setIfRead(String ifRead) {
		this.ifRead = ifRead;
	}

	public String getMsgTime() {
		return MsgTime;
	}



	public void setMsgTime(String msgTime) {
		MsgTime = msgTime;
	}



	@Override
	public String toString() {
		return "ChatMessage [MsgFrom=" + MsgFrom + ", MsgTo=" + MsgTo
				+ ", MsgBody=" + MsgBody + ", MsgPath=" + MsgPath
				+ ", MsgType=" + MsgType + ", MsgState=" + MsgState
				+ ", ifRead=" + ifRead + "]";
	}
	
}
