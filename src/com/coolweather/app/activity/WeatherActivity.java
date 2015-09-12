package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author xiaoliucool
 * @date 2015��9��12�� ����4:25:55
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class WeatherActivity extends Activity implements OnClickListener {
	// ����xml�еĿؼ�
	private LinearLayout weatherLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;

	private ImageButton switchCity;
	private ImageButton refreshWeather;

	// ��ǰ�صĴ���
	private String countyCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		initComponent();
		countyCode = getIntent().getStringExtra("countyCode");
		Log.i("coolweather", "�ش���" + countyCode);
		if (!TextUtils.isEmpty(countyCode)) {
			Log.i("coolweather", "��һ��ѡ��ͬ����...");
			publishText.setText("ͬ����..");
			weatherLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			Log.i("coolweather", "�Ѿ�ѡ������У�ֱ����ʾ����");
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	/**
	 * ��ʼ�����ֿؼ�
	 */
	private void initComponent() {
		weatherLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (ImageButton) findViewById(R.id.switch_city);
		refreshWeather = (ImageButton) findViewById(R.id.refresh_weather);
		Log.i("coolweather", "�ؼ����»����");
	}

	/**
	 * �����ش���---���Ӳ�ѯ��������������
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		Log.i("coolweather", "��ʼ��ѯ��������");
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * ������������,��ѯ����
	 * 
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(String address, final String code) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				Log.i("coolweather", "��������Ӧ���");
				if ("countyCode".equals(code)) {
					if (!TextUtils.isEmpty(response)) {
						Log.i("coolweather", response);

						String[] array = response.split("\\|");
						Log.i("coolweather", "" + array.length);
						if (array != null && array.length == 2) {
							Log.i("coolweather", "�����ش��ţ���ѯ�����������");
							queryWeatherInfo(array[1]);
						}
					}
				} else if ("weatherCode".equals(code)) {
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					Log.i("coolweather", "ֱ�Ӳ�ѯ�������Ŷ�Ӧ������");
					// ע��Ҫ���ص�UI�߳�
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});

				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});

			}
		});

	}

	/**
	 * ��sharedpreference��ѯ������Ϣ
	 */
	private void showWeather() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(sp.getString("city_name", ""));
		temp1Text.setText(sp.getString("temp1", ""));
		temp2Text.setText(sp.getString("temp2", ""));
		weatherDespText.setText(sp.getString("weather_desp", ""));
		publishText.setText(sp.getString("publish_time", ""));
		currentDateText.setText(sp.getString("current_time", ""));
		weatherLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("isfromWeatherActivity", true);
			startActivity(intent);
			finish();
			break;

		case R.id.refresh_weather:
			publishText.setText("ͬ����...");
			weatherLayout.setVisibility(View.INVISIBLE);
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = sp.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		}

	}
}
