package com.bfz.zhbao.Base;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;

import com.bfz.zhbao.xmppManager.XmppConnection;


public final class BaseFactory {

	private static ChatManager chatmanager;

	private static FileTransferManager fileManager;
	
	private static Roster roster;
	
	 public static ChatManager getChatManager() {
	        if (chatmanager == null) {
	        	chatmanager = XmppConnection.getConnection().getChatManager();
	        }
	        return chatmanager;
	    }
	 
	 public static FileTransferManager getFileTransferManager() {
	        if (fileManager == null) {
	        	fileManager = new FileTransferManager(XmppConnection.getConnection());
	        }
	        return fileManager;
	    }
	 public static Roster getRoster() {
	        if (roster == null) {
	        	roster = XmppConnection.getConnection().getRoster();
	        }
	        return roster;
	    }
}
