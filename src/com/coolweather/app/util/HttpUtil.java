package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author xiaoliucool
 * @date 2015年9月11日 下午2:30:09
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream is = connection.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					StringBuilder response = new StringBuilder();
					String line = null;
					while((line=br.readLine())!=null){
						response.append(line);
					}
					if(listener!=null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					listener.onError(e);
				} finally {
					if(connection!=null){
						connection.disconnect();
					}
				}
				
			}
		}).start();

	}
}
