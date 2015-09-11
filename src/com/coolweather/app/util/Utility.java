package com.coolweather.app.util;

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
					//�������ݿ�city��
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}

		return false;
	}
	/**
	 * ����������
	 * @param response ��ʽΪ������|����������|������
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
					//�������ݿ�county��
					coolWeatherDB.saveCounty(county);
				}
			}
			return true;
		}
		return false;
	}
}
