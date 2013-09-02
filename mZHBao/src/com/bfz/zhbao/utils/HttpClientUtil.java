package com.bfz.zhbao.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * Http ����post get ��,����http����
 */
public class HttpClientUtil {

	private static DefaultHttpClient client;
	static {
		
		client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
	}

	public static String getStringByPostMultipart(String actionUrl, Map<String, Object> params, List<FormFile> files, HttpClientPostCallbackHandler handler,
			int timeout) throws IOException {

		InputStream inStream = getStreamByPostMultipart(actionUrl, params, files, handler, timeout);
		DataInputStream resultInStream = new DataInputStream(inStream);
		ByteArrayOutputStream resultOutStream = new ByteArrayOutputStream();

		byte[] resultBuf = new byte[1024];
		int resutReadLen = 0;
		while ((resutReadLen = resultInStream.read(resultBuf)) != -1) {
			resultOutStream.write(resultBuf, 0, resutReadLen);
			resultOutStream.flush();
		}

		String result = new String(resultOutStream.toByteArray());
		resultOutStream.close();
		resultInStream.close();

		return result;
	}

	public static InputStream getStreamByPostMultipart(String actionUrl, Map<String, Object> params, List<FormFile> files,
			HttpClientPostCallbackHandler handler, int timeout) throws IOException {

		final String BOUNDARY = java.util.UUID.randomUUID().toString();
		final String PREFIX = "--";
		final String LINEND = "\r\n";
		final String MULTIPART_FROM_DATA = "multipart/form-data";
		final String CHARSET = "UTF-8";
		// final String TRANSFER_ENCODING = "chunked";
		URL uri = new URL(actionUrl);

		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", CHARSET);
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
		// conn.setRequestProperty("Transfer-Encoding", TRANSFER_ENCODING);
		conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible)");
		conn.setRequestProperty("Accept", "*/*");

		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.connect();

		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		// OutputStream outStream = conn.getOutputStream();

		// 首先组拼文本类型的参�?
		StringBuilder textParam = new StringBuilder();

		// 这个地方使用了Map循环 map循环的方式需要注意一下了
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			textParam.append(PREFIX);
			textParam.append(BOUNDARY);
			textParam.append(LINEND);
			textParam.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
			textParam.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			textParam.append("Content-Transfer-Encoding: 8bit" + LINEND);
			textParam.append(LINEND);
			textParam.append(entry.getValue());
			textParam.append(LINEND);
		}

		outStream.write(textParam.toString().getBytes());
		outStream.flush();

		for (FormFile file : files) {

			StringBuilder sb1 = new StringBuilder();
			sb1.append(PREFIX);
			sb1.append(BOUNDARY);
			sb1.append(LINEND);
			sb1.append("Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename=\"" + file.getFile().getName() + "\"" + LINEND);
			sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
			sb1.append(LINEND);
			outStream.write(sb1.toString().getBytes());
			outStream.flush();

			FileInputStream inStream = new FileInputStream(file.getFile());
			byte[] buffer = new byte[1024 * 8];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
				outStream.flush();
				if (handler != null) {
					handler.onPostData(buffer.length);
				}
			}

			inStream.close();
			outStream.write(LINEND.getBytes());
			outStream.flush();
		}

		// 请求结束标志
		byte[] endData = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(endData);
		outStream.flush();
		outStream.close();

		int responseCode = conn.getResponseCode();

		if (responseCode != 200) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			String line = null;
			StringBuffer errorMsg = new StringBuffer();
			if ((line = reader.readLine()) != null) {
				errorMsg.append(line).append("\n");
			}
			reader.close();

			throw new IOException(errorMsg.toString());
		}

		return conn.getInputStream();
	}

	public static HttpEntity getEntityByGet(String url, Map<String, Object> httpParams, int timeout) throws Exception {
		// String responseStr;
		String requestUrl;

		List<NameValuePair> getParams = new ArrayList<NameValuePair>();
		String getParamsStr = "";
		if (httpParams != null) {
			for (Object key : httpParams.keySet()) {
				Object value = httpParams.get(key);

				getParams.add(new BasicNameValuePair(key.toString(), value.toString()));
			}

			getParamsStr = URLEncodedUtils.format(getParams, "UTF-8");
		}
		if (getParamsStr == null || "".equals(getParamsStr.trim())) {
			requestUrl = url;
		} else {
			requestUrl = url + "?" + getParamsStr;
		}

		HttpGet request = new HttpGet(requestUrl);

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
		HttpConnectionParams.setSoTimeout(httpParameters, timeout);

		client.setParams(httpParameters);

		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();

		return entity;

	}

	public static String getStringByGet(String url, Map<String, Object> httpParams, int timeout) throws Exception {

		HttpEntity entity = getEntityByGet(url, httpParams, timeout);
		String responseStr = EntityUtils.toString(entity, "UTF-8");
		return responseStr;

	}

	public static InputStream getStreamByGet(String url, Map<String, Object> httpParams, int timeout) throws Exception {
		HttpEntity entity = getEntityByGet(url, httpParams, timeout);
		return entity.getContent();
	}

	public static HttpEntity getEntityByPost(String url, Map<String, Object> params, int timeout) throws Exception {

		// String result = null;
		// StringBuilder sb = new StringBuilder();

		HttpPost request = new HttpPost(url);

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
		HttpConnectionParams.setSoTimeout(httpParameters, timeout);

		client.setParams(httpParameters);

		if (params != null) {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			for (Object key : params.keySet()) {
				Object value = params.get(key);

				postParams.add(new BasicNameValuePair(key.toString(), value.toString()));
			}

			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParams, "UTF-8");
			request.setEntity(formEntity);
		}

		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		return entity;
	}

	public static String getStringByPost(String url, Map<String, Object> params, int timeout) throws Exception {
		HttpEntity entity = getEntityByPost(url, params, timeout);
		String result = EntityUtils.toString(entity, "UTF-8");
		return result;
	}

	public static InputStream getStreamByPost(String url, Map<String, Object> params, int timeout) throws Exception {
		HttpEntity entity = getEntityByPost(url, params, timeout);
		return entity.getContent();
	}

	public static class FormFile {
		private File file;
		private String name;

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public interface HttpClientPostCallbackHandler {
		public void onPostData(int postLength);
	}
}
