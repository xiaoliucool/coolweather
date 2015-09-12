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
 * @date 2015年9月11日 下午2:46:30
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class Utility {
	/**
	 * 处理province数据 格式为“代号|城市，代号|城市”
	 * 
	 * @param response
	 *            服务器响应的字符串数据
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
				// 存入province表中
				coolWeatherDB.saveProvince(province);
			}
			return true;
		}
		return false;
	}

	/**
	 * 处理city信息
	 * 
	 * @param response
	 *            格式为“代号|城市名，代号|城市名”
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
					// 存入数据库city表
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}

		return false;
	}

	/**
	 * 处理县数据
	 * 
	 * @param response
	 *            格式为“代号|县名，代号|县名”
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
					// 存入数据库county表
					coolWeatherDB.saveCounty(county);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 解析服务器返回的json天气信息，并存储到本地 {"weatherinfo":
	 * {"city":"昆山","cityid":"101190404","temp1":"21℃","temp2":"9℃",
	 * "weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"} }
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
		//定义日期的显示格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", "今天"+publishTime+"发布");
		editor.putString("current_time", sdf.format(new Date()));
		editor.commit();
	}
}
