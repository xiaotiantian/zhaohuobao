package com.bfz.zhbao.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonToObjRemoteService<T> extends JSONStringRemoteService {

	private T t;
	private String url;

	public JsonToObjRemoteService(String url) {
		this.url = url;
	}

	public T JsonToObj(Object obj, Class cls) {
		if (t == null) {  
			try {
				t = (T) cls.newInstance();
				if(!(obj instanceof JSONArray)&&obj instanceof JSONObject){
					JSONObject json = (JSONObject) obj;
					toObj(t, json);
				}
			} catch (IllegalAccessException e) {				
				e.printStackTrace();
			} catch (InstantiationException e) {			
				e.printStackTrace();
			}
		}
		return t;
	}
	
	public T JsonToObj(Object obj, Class cls,String[] keys) {
		if (t == null) {
			try {
				t = (T) cls.newInstance();
				JSONArray json = (JSONArray) obj;
				toArray(t,json,keys);
			} catch (IllegalAccessException e) {				
				e.printStackTrace();
			} catch (InstantiationException e) {			
				e.printStackTrace();
			}
		}

		return t;
	}
	
	private void toArray(Object cl, JSONArray json,String[] keys)
	{
		
	  for(String key :keys){
			Field f = null;
			try {
				f = cl.getClass().getField(key);
			} catch (SecurityException e2) {
				e2.printStackTrace();
			} catch (NoSuchFieldException e2) {			
				e2.printStackTrace();
			}
			
			JSONArray array = (JSONArray) json;
			int i = array.length();
			Object newArray = Array.newInstance(f.getType()
					.getComponentType(), i);

			for (int l = 0; l < i; l++) {

				try {
					Object tmp = f.getType().getComponentType()
							.newInstance();
					toObj(tmp, (JSONObject) array.get(l));
					Array.set(newArray, l, tmp);
				} catch (IllegalAccessException e1) {
					
					e1.printStackTrace();
				} catch (InstantiationException e1) {
				
					e1.printStackTrace();
				} catch (JSONException e) {
					
					e.printStackTrace();
				}

			}
			try {
				// System.arraycopy(tmps, 0, newArray, 0, i);
				f.set(cl, newArray);
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
				
			} catch (IllegalAccessException e) {
			
				e.printStackTrace();
				
			}
	  }
		
	}
	
	private boolean checkType(Object cl)
	{
		if (cl instanceof String) {
			return true;
		} else {
			if (cl instanceof Number) {
				return true;
			
			}
		}
		return false;
		
	}
	
	private Object checkType(Object cl, Object json)
	{
		if (cl instanceof String && json != null) {
			try {
				cl = json.toString();
				return cl;
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();

			}
		} else {
			if (cl instanceof Number && json != null) {
				try {
					cl = new BigDecimal(json.toString());
					return cl;
				} catch (IllegalArgumentException e) {	
					e.printStackTrace();
				}
			}
		}
		return cl;
		
	}

	private void toObj(Object cl, Object json) {

		Field[] fields = cl.getClass().getFields();
		Log.e("cl", cl.getClass().getName());
		Log.e("toObJ", json.toString());

		Boolean flag = false;
		
		
		for (Field f : fields) {
			Object obj = null;
			try {
				obj = ((JSONObject)json).get(f.getName());
				flag = true;
			} catch (JSONException e1) {
				e1.printStackTrace();
				flag = false;
			}

			if (obj instanceof JSONArray && obj != null) {

				JSONArray array = (JSONArray) obj;
				int i = array.length();
				Object newArray = Array.newInstance(f.getType()
						.getComponentType(), i);

				for (int l = 0; l < i; l++) {

					try {
						Object tmp = f.getType().getComponentType()
								.newInstance();
						if(checkType(tmp))
							tmp =  checkType(tmp,array.get(l));
						else
							toObj(tmp,  array.get(l));
						
						Array.set(newArray, l, tmp);
					} catch (IllegalAccessException e1) {
						
						e1.printStackTrace();
					} catch (InstantiationException e1) {
					
						e1.printStackTrace();
					} catch (JSONException e) {
					
						e.printStackTrace();
					}

				}
				try {
					// System.arraycopy(tmps, 0, newArray, 0, i);
					f.set(cl, newArray);
				} catch (IllegalArgumentException e) {
				
					e.printStackTrace();
					flag = false;
				} catch (IllegalAccessException e) {
					
					e.printStackTrace();
					flag = false;
				}

			} else {
				if (obj instanceof String && obj != null) {
					try {
						f.set(cl, obj);
					} catch (IllegalArgumentException e) {
					
						e.printStackTrace();
						flag = false;
					} catch (IllegalAccessException e) {
					
						e.printStackTrace();
						flag = false;
					}
				} else {
					if (obj instanceof Number && obj != null) {
						try {
							f.set(cl, obj);
						} catch (IllegalArgumentException e) {
						
							e.printStackTrace();
							flag = false;
						} catch (IllegalAccessException e) {
							
							e.printStackTrace();
							flag = false;
						}
					} else {
						if (obj instanceof JSONObject) {
							Object tmp;
							try {
								tmp = f.getType().newInstance();
								toObj(tmp, (JSONObject) obj);
								f.set(cl, tmp);
							} catch (IllegalAccessException e) {
							
								e.printStackTrace();
							} catch (InstantiationException e) {
								
								e.printStackTrace();
							}
						}
					}
				}
			}
		}	
	}

	@Override
	protected Map<String, Object> getParams() {
        
		return params;
	}
	
	public void setParams(Map<String, Object> p)
	{
		this.params = p;
	}
	
	public void setFormFiles(List<HttpClientUtil.FormFile> ff)
	{
		this.formFiles = ff;
	}

	@Override
	protected String getURL() {

		return url;
	}

     
}
