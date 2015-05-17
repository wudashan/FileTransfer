package com.scut.filetransfer.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * wifi工具类，用于判断是否连接到wifi或开启便携式热点
 * 
 * @author ccz
 * 
 */
public class WifiUtil {
	public enum WIFI_AP_STATE {
		WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
	}

	private WifiManager wifiManager;
	private Context context;

	public WifiUtil(Context context) {
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		this.context = context;
	}

	/**
	 * 判断是否开启热点
	 * 
	 * @return
	 */
	public boolean isWifiApEnabled() {
		return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
	}

	/**
	 * 判断是否连接到wifi
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public boolean isWifiConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}

	public boolean isMobileConnected() {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();   
        if (networkINfo != null   
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {   
            return true;   
        }   
        return false;   
    }
	
	private WIFI_AP_STATE getWifiApState() {
		int tmp;
		try {
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			tmp = ((Integer) method.invoke(wifiManager));
			if (tmp > 10) {
				tmp = tmp - 10;
			}
			return WIFI_AP_STATE.class.getEnumConstants()[tmp];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
		}
	}
}
