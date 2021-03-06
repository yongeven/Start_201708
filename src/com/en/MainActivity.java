﻿package com.en.sharedpreference;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import net.sf.json.JSONObject;

public class MainActivity extends Activity implements OnClickListener{

	/**
	 *����⼪�׵���ʾ�� 
	 *���߽ӿ��ĵ���http://www.juhe.cn/docs/166
	 **/

	private Button button;
	private EditText editText;
	private Editable in;
	public static final String DEF_CHATEST = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 30000;
	public static final int DEF_READ_TIMEOUT = 30000;
	public static final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
	public static final String APPKEY="9eaeef7698c0d064173177b84eace00a";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void upView() {

	}

	private void initData() {
		String result = null;
		String url = "http://japi.juhe.cn/qqevaluate/qq";//����ӿڵ�ַ
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("key", APPKEY);
		params.put("qq", in);
		try {
			result = net(url,params,"GET");
			Log.i("md", "result:"+result);
			JSONObject object = JSONObject.fromObject(result);
			if (object.getInt("error_code")==0) {
				System.out.println(object.get("result"));
			}else{
				System.out.println(object.get("error_code")+":"+object.get("reason"));
			}
		} catch (Exception e) {
		}
	}
	public static void main(String[] args) {

	}
	private void initView() {
		button=(Button) findViewById(R.id.bt);
		button.setOnClickListener(this);
		editText=(EditText) findViewById(R.id.et);
	}

	@Override
	public void onClick(View v) {
		if (v.getId()==R.id.bt) {
			in=null;
			in=editText.getText();
			if (editText.getText().toString().trim().equals("")) {
				showToast("����������");
			}else{
				new Thread(){
					public void run() {
						new AnotherTask().execute("JSON");
					};
				}.start();
			}
		}
	}

	Toast toast;

	private void showToast(String s){
		if (toast==null) {
			toast=Toast.makeText(this, s, 0);
		}else{
			toast.setText(s);
		}
		toast.show();
	}
	
	private class AnotherTask extends AsyncTask<String, Void, String>{  
		@Override  
		protected void onPostExecute(String result) {  
			//��UI����ĸ��²���  
		}  
		@Override  
		protected String doInBackground(String... params) {
			try {
				initData();
			} catch (Exception e) {
			}
			return params[0];  
		}  
	} 
	
	/*
	 * @param strUrl �����ַ
	 * @param params �������
	 * @param method ���󷽷�
	 * @return  ���������ַ���
	 * @throws Exception 
	 */
	public static String net(String strUrl,Map<String, Object> params,String method) throws Exception{
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			StringBuffer sb=new StringBuffer();
			if (method==null||method.equals("GET")) {
				strUrl=strUrl+"?"+urlencode(params);
			}
			URL url=new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (method==null||method.equals("GET")) {
				conn.setRequestMethod("GET");
			}else{
				conn.setRequestMethod("POST");
				conn.setDoInput(true);
			}
			conn.setRequestProperty("User-agent", userAgent);
			conn.setUseCaches(false);
			conn.setConnectTimeout(DEF_CONN_TIMEOUT);
			conn.setReadTimeout(DEF_READ_TIMEOUT);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params!=null&&method.equals("POST")) {
				try {
					DataOutputStream outputStream=new DataOutputStream(conn.getOutputStream());
					outputStream.writeBytes(urlencode(params));
				} catch (Exception e) {
				}
			}
			InputStream is=conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, DEF_CHATEST));
			String strRead = null;
			while((strRead = reader.readLine())!=null){
				sb.append(strRead);
			}
			rs=sb.toString();
		} catch (Exception e) {
		}finally{
			if (reader!=null) {
				reader.close();
			}
			if (conn!=null) {
				conn.disconnect();
			}
		}
		return rs;
	}


	/*
	 * ��MAP��תΪ�������
	 */
	public static String urlencode(Map<String,Object>data){
		StringBuffer sb=new StringBuffer();
		for(Map.Entry i:data.entrySet()){
			try {
				sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
