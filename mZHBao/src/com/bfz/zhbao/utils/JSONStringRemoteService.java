package com.bfz.zhbao.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONStringRemoteService extends StringRemoteService {

	  @Override
	    public Object onExecute() throws Exception {
	        String jsonString = (String) super.onExecute();

	        Object json = null;

	        try {
	            json = new JSONArray(jsonString);
	        } catch (JSONException e) {
	            try {
	                json = new JSONObject(jsonString);
	            } catch (JSONException e1) {
	                throw new Exception("Convert json object fault", e1);
	            }
	        }

	        return json;
	    }

}
