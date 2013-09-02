package com.bfz.zhbao.db;

import java.util.ArrayList;
import java.util.List;

import com.bfz.zhbao.entity.ChatMessage;
import com.bfz.zhbao.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class DBTools {
	
	private static DBTools instance = null;
	private SqliteHelper sh;
	private SQLiteDatabase dbase;
	private Cursor cur1;
	public DBTools(Context context){
		
		sh = new SqliteHelper(context);
		dbase = sh.getWritableDatabase();
	}
	
	public static DBTools getinstance(Context context){
		if(instance == null){
			instance = new DBTools(context);
		}
		return instance;
	}
	
	
	public class SqliteHelper extends SQLiteOpenHelper {
		
		private static final String ChatDbName="ChatDatabase";
		private static final int version=1;
		public SqliteHelper(Context context) {
			super(context,ChatDbName, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql="create table chatTable(MsgFrom varchar,MsgTo varchar,MsgBody varchar," +
					"MsgPath varchar,MsgType varchar,MsgState varchar,ifRead varchar,MsgTime TimeStamp NOT NULL DEFAULT(datetime('now','localtime')))";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			

		}
	}
	
	//插入数据
	public boolean insert(ContentValues value){
		
		try {
			System.out.println("准备插入");
			dbase.insert("chatTable", null, value);
			System.out.println("插入完毕");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 插入之前对数据的处理
	 * @param chatMsg
	 */
	public ContentValues EarlyInsert(com.bfz.zhbao.entity.ChatMessage chatMsg){
		String msgFrom=chatMsg.getMsgFrom();
		String msgTo=chatMsg.getMsgTo();
		String msgBody=chatMsg.getMsgBody();
		String msgPath=chatMsg.getMsgPath();
		String msgType=chatMsg.getMsgType();
		String msgState=chatMsg.getMsgState();
		String msgifRead=chatMsg.getIfRead();
		ContentValues value=new ContentValues();
		String mFrom = Utils.cutSentence(msgFrom);
		String mTO = Utils.cutSentence(msgTo);
		value.put("MsgFrom",mFrom);
		value.put("MsgTo",mTO);
		value.put("MsgBody",msgBody);
		value.put("MsgPath",msgPath);
		value.put("MsgType",msgType);
		value.put("MsgState",msgState);
		value.put("ifRead",msgifRead);	
		return value;
	}
	//查询数据
	public Cursor queryDb(String from,String to){

		try {
		    cur1=dbase.query("chatTable",null,"MsgFrom=?"+"and MsgTo=?"+"or MsgFrom=?"+"and MsgTo=?", new String[]{from,to,to,from},null,null,"MsgTime");
			return cur1;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	

	}
	
	public List<ChatMessage> queryAfter(Cursor cur1){
		List<ChatMessage> list=new ArrayList<ChatMessage>();

		String tmpFrom=null;
		String tmpTo=null;
		String tmpBody=null;
		String tmpPath=null;
		String tmpType=null;
		String tmpState=null;
		String tmpifRead=null;
		String tmpMsgTime=null;
		
		
    if(cur1==null){
			Log.i("DataBase", "数据查询出错");
		}else{
			while(cur1.moveToNext()){
				
				tmpFrom=cur1.getString(0);
				tmpTo=cur1.getString(1);
				tmpBody=cur1.getString(2);
				tmpPath=cur1.getString(3);
				tmpType=cur1.getString(4);
				tmpState=cur1.getString(5);
				tmpifRead=cur1.getString(6);
				tmpMsgTime=cur1.getString(7);
				ChatMessage cm=new ChatMessage(tmpFrom,tmpTo,tmpBody,tmpPath,tmpType,tmpState,tmpifRead,tmpMsgTime);
				list.add(cm);
			}
		}
    return list;
		
	}
	/**
	 * 关闭数据库
	 */
	public  void closeDb(){
		if(dbase != null && dbase.isOpen()){
			dbase.close();
			cur1.close();
			}
		sh.close();
	}
	
}
