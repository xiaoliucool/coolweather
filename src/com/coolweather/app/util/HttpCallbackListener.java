package com.coolweather.app.util;
/**
 * �ص��ӿڣ������������߳���httpResoponse��Ӧ������
 * @author Administrator
 *
 */
public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
