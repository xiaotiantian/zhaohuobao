package com.bfz.zhbao.entity;

import java.io.Serializable;
/**
 * ��Ϣʵ����
 * @author yangfan
 *
 */
@SuppressWarnings("serial")
public class ChatMessage implements Serializable{

	/**
	 * ��Ϣ��������
	 */
	private String MsgFrom;
	/**
	 * ��Ϣȥ������
	 */
	private String MsgTo;
	/**
	 * ��Ϣ��Body
	 */
	private String MsgBody;
	/**
	 * ��Ϣ��·��������ļ��ķ��ͣ�
	 */
	private String MsgPath;
	/**
	 * ��Ϣ�����ͣ�����"0"������"1"��ͼƬ"2"���ļ�"3"��
	 */
	private String MsgType;
	/**
	 * ��Ϣ�ķ��ͻ��߽��ܣ�IN  ����   OUT��
	 */
	private String MsgState;
	/**
	 * �Ƿ��ȡ�����������Ϣ���ļ���
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
