package com.bfz.zhbao.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class MsgInfoParser {

	public static class Msg {

		private String _id;
		private String _num;
		private String _nbr;
		private String _name;
		private String _city;
		private String _cDate;
		private String _info;
		private String _infoStart;
		private String _infoEnd;
		private String _infoNbr1;
		private String _infoNbr2;
		private String _infoNbr3;
		private String _infoTruck;
		private String _infoGoods;
		private String _infoNote;
		private String _infoFrom;
		private String _state;
		private String _note;
		private String _lngStart;
		private String _latStart;
		private String _lngEnd;
		private String _latEnd;

		public String getId() {
			return _id;
		}

		public void setId(String id) {
			this._id = id;
		}

		public String getNum() {
			return _num;
		}

		public void setNum(String num) {
			this._num = num;
		}

		public String getNbr() {
			return _nbr;
		}

		public void setNbr(String nbr) {
			this._nbr = nbr;
		}

		public String getName() {
			return _name;
		}

		public void setName(String name) {
			this._name = name;
		}

		public String getCity() {
			return _city;
		}

		public void setCity(String city) {
			this._city = city;
		}

		public String getCreateDate() {
			return _cDate;
		}

		public void setCreateDate(String cDate) {
			this._cDate = cDate;
		}

		public String getInfo() {
			return _info;
		}

		public void setInfo(String info) {
			this._info = info;
		}

		public String getInfoStart() {
			return _infoStart;
		}

		public void setInfoStart(String infoStart) {
			this._infoStart = infoStart;
		}

		public String getInfoEnd() {
			return _infoEnd;
		}

		public void setInfoEnd(String infoEnd) {
			this._infoEnd = infoEnd;
		}

		public String getInfoNbr1() {
			return _infoNbr1;
		}

		public void setInfoNbr1(String infoNbr1) {
			this._infoNbr1 = infoNbr1;
		}

		public String getInfoNbr2() {
			return _infoNbr2;
		}

		public void setInfoNbr2(String infoNbr2) {
			this._infoNbr2 = infoNbr2;
		}

		public String getInfoNbr3() {
			return _infoNbr3;
		}

		public void setInfoNbr3(String infoNbr3) {
			this._infoNbr3 = infoNbr3;
		}

		public String getInfoTruck() {
			return _infoTruck;
		}

		public void setInfoTruck(String infoTruck) {
			this._infoTruck = infoTruck;
		}

		public String getInfoGoods() {
			return _infoGoods;
		}

		public void setInfoGoods(String infoGoods) {
			this._infoGoods = infoGoods;
		}

		public String getInfoNote() {
			return _infoNote;
		}

		public void setInfoNote(String infoNote) {
			this._infoNote = infoNote;
		}

		public String getInfoFrom() {
			return _infoFrom;
		}

		public void setInfoFrom(String infoFrom) {
			this._infoFrom = infoFrom;
		}

		public String getState() {
			return _state;
		}

		public void setState(String state) {
			this._state = state;
		}

		public String getNote() {
			return _note;
		}

		public void setNote(String note) {
			this._note = note;
		}

		public String getLngStart() {
			return _lngStart;
		}

		public void setLngStart(String lngStart) {
			this._lngStart = lngStart;
		}

		public String getLatStart() {
			return _latStart;
		}

		public void setLatStart(String latStart) {
			this._latStart = latStart;
		}

		public String getLngEnd() {
			return _lngEnd;
		}

		public void setLngEnd(String lngEnd) {
			this._lngEnd = lngEnd;
		}

		public String getLatEnd() {
			return _latEnd;
		}

		public void setLatEnd(String latEnd) {
			this._latEnd = latEnd;
		}

		public Msg copy() {
			Msg copy = new Msg();
			copy._id = _id;
			copy._num = _num;
			copy._nbr = _nbr;
			copy._name = _name;
			copy._city = _city;
			copy._cDate = _cDate;
			copy._info = _info;
			copy._infoStart = _infoStart;
			copy._infoEnd = _infoEnd;
			copy._infoNbr1 = _infoNbr1;
			copy._infoNbr2 = _infoNbr2;
			copy._infoNbr3 = _infoNbr3;
			copy._infoTruck = _infoTruck;
			copy._infoGoods = _infoGoods;
			copy._infoNote = _infoNote;
			copy._infoFrom = _infoFrom;
			copy._state = _state;
			copy._note = _note;
			copy._lngStart = _lngStart;
			copy._latStart = _latStart;
			copy._lngEnd = _lngEnd;
			copy._latEnd = _latEnd;
			return copy;
		}
	}

	// 对xml进行解释
	public static List<Msg> Parse(String xmlStr) {

		final List<Msg> msgList = new ArrayList<Msg>();
		final Msg msg = new Msg();
		
		RootElement root = new RootElement("ROOT");
		Element item = root.getChild("NODE");
		item.getChild("ID").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setId(body);
					}
				});
		item.getChild("NUM").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setNum(body != "" ? body : "0");
					}
				});
		item.getChild("NBR").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setNbr(body);
					}
				});
		item.getChild("C_NAME").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setName(body);
					}
				});
		item.getChild("CITY").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setCity(body);
					}
				});
		item.getChild("CREATE_DATE").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setCreateDate(body);
					}
				});
		item.getChild("INFO").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfo(body);
					}
				});
		item.getChild("INFO_START").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoStart(body);
					}
				});
		item.getChild("INFO_END").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoEnd(body);
					}
				});
		item.getChild("INFO_NBR1").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoNbr1(body);
					}
				});
		item.getChild("INFO_NBR2").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoNbr2(body);
					}
				});
		item.getChild("INFO_NBR3").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoNbr3(body);
					}
				});
		item.getChild("INFO_TRUCK").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoTruck(body);
					}
				});
		item.getChild("INFO_GOODS").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoGoods(body);
					}
				});
		item.getChild("INFO_NOTE").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoNote(body);
					}
				});
		item.getChild("INFO_FROM").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setInfoFrom(body);
					}
				});
		item.getChild("STATE").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setState(body != "" ? body : "-1");
					}
				});
		item.getChild("NOTE").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setNote(body);
					}
				});
		item.getChild("LNG_START").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setLngStart(body != "" ? body : "0");
					}
				});
		item.getChild("LAT_START").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setLatStart(body != "" ? body : "0");
					}
				});
		item.getChild("LNG_END").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setLngEnd(body != "" ? body : "0");
					}
				});
		item.getChild("LAT_END").setEndTextElementListener(
				new EndTextElementListener() {

					public void end(String body) {

						// TODO Auto-generated method stub
						msg.setLatEnd(body != "" ? body : "0");
					}
				});

		item.setEndElementListener(new EndElementListener() {

			public void end() {

				// TODO Auto-generated method stub
				msgList.add(msg.copy());
			}
		});
		try {
			Xml.parse(new ByteArrayInputStream(xmlStr.getBytes()),
					Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return msgList;
	}
}
