package com.coolweather.app.util;

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
					//存入数据库city表
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}

		return false;
	}
	/**
	 * 处理县数据
	 * @param response 格式为“代号|县名，代号|县名”
	 * @param coolWeatherDB
	 * @param cityId
	 * @return
	 */
	public static boolean handCountiesResponse(String response,
			CoolWeatherDB coolWeatherDB, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties!=null && allCounties.length>0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//存入数据库county表
					coolWeatherDB.saveCounty(county);
				}
			}
			return true;
		}
		return false;
	}
}
