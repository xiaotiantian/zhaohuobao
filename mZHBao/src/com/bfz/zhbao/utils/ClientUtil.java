//package com.bfz.zhbao.utils;
//
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//
//
///**
// * xmpp连接的工具类，是一个单例模式
// * @author
// *
// */
//
//public class ClientUtil {
//
//private static XMPPConnection con = null;
//	/**
//	 * 打开xmpp的连接
//	 */
//	private static void openConnection() {
//		try {
//			ConnectionConfiguration connConfig = new ConnectionConfiguration(SysConfig.getInstace().getMsgServerIp(), SysConfig.getInstace().getMsgServerPort());
//			connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//			connConfig.setSelfSignedCertificateEnabled(false);
//			con = new XMPPConnection(connConfig);
//			con.connect();
//		} catch (XMPPException xe) {
//			xe.printStackTrace();
//			try {
//				Thread.sleep(1000*60);
//				openConnection();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//   /**
//    * 获取 一个xmpp客户端的连接
//    * @return XMPPConnection 
//    */
//	public static XMPPConnection getConnection() {
//		if (con == null)
//		{
//			openConnection();
//		}
//		if(!con.isConnected())
//		{
//			openConnection();
//		}
//		return con;
//	}
//    /**
//     * 关闭xmpp的连接
//     */
//	public static void closeConnection() {
//		con.disconnect();
//		con = null;
//	}
//	/**
//	 * 获取当前xmpp是否连接 true为已连接，false没有连接
//	 * @return boolean
//	 */
//	public static boolean isConnected()
//	{
//		
//		if(con==null)
//			return false;
//		return con.isConnected();
//	}
//
//}
//
