//package com.bfz.zhbao.utils;
//
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//
//
///**
// * xmpp���ӵĹ����࣬��һ������ģʽ
// * @author
// *
// */
//
//public class ClientUtil {
//
//private static XMPPConnection con = null;
//	/**
//	 * ��xmpp������
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
//    * ��ȡ һ��xmpp�ͻ��˵�����
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
//     * �ر�xmpp������
//     */
//	public static void closeConnection() {
//		con.disconnect();
//		con = null;
//	}
//	/**
//	 * ��ȡ��ǰxmpp�Ƿ����� trueΪ�����ӣ�falseû������
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
