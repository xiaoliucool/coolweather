package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.R.color;
import android.app.Activity;
import android.app.DownloadManager.Query;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xiaoliucool
 * @date 2015��9��11�� ����3:19:53
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class ChooseAreaActivity extends Activity {

	// ���Ʋ㼶�ĳ���
	private final static int LEVEL_PROVINCE = 0;
	private final static int LEVEL_CITY = 1;
	private final static int LEVEL_COUNTY = 2;

	// ��ǰ�Ĳ㼶��Ĭ�ϳ�ʼ��Ϊ0
	private int currentLevel;

	// �����������е�����
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();

	// ��Ӧlayout�еĿؼ�
	private TextView titleView;
	private ListView listView;

	// ���ݿ⣬����ģʽ
	private CoolWeatherDB coolWeatherDB;

	// ���ݿ��ж�Ӧ��������ṹ
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;

	// ѡ�е�ʡ���С���
	private Province selectedProvince;
	private City selectedCity;
	private County selectedCounty;

	// ��ѯ������ʱ�����ʾ����
	private ProgressDialog progressDialog;

	// ��־���ж��Ƿ��Ǵ�weatherActivity��ת����
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"isfromWeatherActivity", false);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sp.getBoolean("city_selected", false)&&!isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		titleView = (TextView) findViewById(R.id.title_text);
		listView = (ListView) findViewById(R.id.list_view);

		coolWeatherDB = CoolWeatherDB.getInstance(this);
		// ��Ϊ������adapter�������������ʹ��adapter���������ã�����query������Ϊʹ�õ���adapter���Ի���ֿ�ָ��
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("countyCode", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		Log.i("coolweather", "��ʼ��ѯprovince����");
		queryProvinces();
	}

	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		Log.i("coolweather", "����province���ݳɹ�");
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			Log.i("coolweather", "datalist��ʼ�����");
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer("", "province");
		}
	}

	/**
	 * ��ѯ��������
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * ��ѯ��������
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * ����������ݿ��������ݣ��ʹӷ�������ѯ������Ļ����ַ�������
	 * 
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}

		showProgressDialog();

		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(response,
							coolWeatherDB);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(response,
							coolWeatherDB, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(response,
							coolWeatherDB, selectedCity.getId());
				}
				if (result) {
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
