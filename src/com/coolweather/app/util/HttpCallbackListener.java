package com.coolweather.app.util;
/**
 * 回调接口，用来处理子线程中httpResoponse响应的内容
 * @author Administrator
 *
 */
public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
