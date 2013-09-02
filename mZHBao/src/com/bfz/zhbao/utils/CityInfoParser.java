
package com.bfz.zhbao.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

/**
 * ��ȡ������Ϣ�б��Լ���Ϣ��
 * 
 * @author 
 * 
 */

public class CityInfoParser {
    
    public static class CityInfo {
        
        public String ADDRESSEND;
        public String ADDRESSCOUNT;
        
        public CityInfo copy() {

            CityInfo tmp = new CityInfo();
            tmp.ADDRESSCOUNT = this.ADDRESSCOUNT;
            tmp.ADDRESSEND = this.ADDRESSEND;
            
            return tmp;
        }
    }
    
    private InputStream in;
    
    /**
     * 
     * @param xmlStr
     *            �ַ���
     */
    
    public CityInfoParser(String xmlStr) {

        in = new ByteArrayInputStream(xmlStr.getBytes());
    }
    
    /**
     * 
     * �ַ���ת���� java��
     * 
     * @return ArrayList<CityInfo> ����list����
     */
    public ArrayList<CityInfo> parse() {

        final CityInfo currentMessage = new CityInfo();
        final ArrayList<CityInfo> messages = new ArrayList<CityInfo>();
        
        RootElement root = new RootElement("ROOT");
        Element item = root.getChild("NODE");

        item.setEndElementListener(new EndElementListener() {
            
            public void end() {

                messages.add(currentMessage.copy());
            }
        });
        
        item.getChild("ADDRESSEND").setEndTextElementListener(new EndTextElementListener() {
            
            public void end(String body) {

                currentMessage.ADDRESSEND = body;
                
            }
        });
        item.getChild("ADDRESSCOUNT").setEndTextElementListener(new EndTextElementListener() {
            
            public void end(String body) {

                // currentMessage.ADDRESSCOUNT = body;
                
                /**
                 * add 2013-02-19
                 */
                currentMessage.ADDRESSCOUNT = body;
                
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