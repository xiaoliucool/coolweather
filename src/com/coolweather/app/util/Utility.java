package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * @author xiaoliucool
 * @date 2015��9��11�� ����2:46:30
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class Utility {
	/**
	 * ����province���� ��ʽΪ������|���У�����|���С�
	 * 
	 * @param response
	 *            ��������Ӧ���ַ�������
	 * @param coolWeatherDB
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(String response,
			CoolWeatherDB coolWeatherDB) {

		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			for (String p : allProvinces) {
				String[] array = p.split("\\|");
				Province province = new Province();
				province.setProvinceCode(array[0]);
				province.setProvinceName(array[1]);
				// ����province����
				coolWeatherDB.saveProvince(province);
			}
			return true;
		}
		return false;
	}

	/**
	 * ����city��Ϣ
	 * 
	 * @param response
	 *            ��ʽΪ������|������������|��������
	 * @param coolWeatherDB
	 * @param provinceId
	 * @return
	 */
	public static boolean handleCitiesResponse(String response,
			CoolWeatherDB coolWeatherDB, int provinceId) {

		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// �������ݿ�city��
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}

		return false;
	}

	/**
	 * ����������
	 * 
	 * @param response
	 *            ��ʽΪ������|����������|������
	 * @param coolWeatherDB
	 * @param cityId
	 * @return
	 */
	public static boolean handleCountiesResponse(String response,
			CoolWeatherDB coolWeatherDB, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// �������ݿ�county��
					coolWeatherDB.saveCounty(county);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * �������������ص�json������Ϣ�����洢������ {"weatherinfo":
	 * {"city":"��ɽ","cityid":"101190404","temp1":"21��","temp2":"9��",
	 * "weather":"����תС��","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"} }
	 * 
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		//�������ڵ���ʾ��ʽ
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", "����"+publishTime+"����");
		editor.putString("current_time", sdf.format(new Date()));
		editor.commit();
	}
}
