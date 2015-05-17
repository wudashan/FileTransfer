package com.scut.filetransfer.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.util.InetAddressUtils;

import com.scut.filetransfer.R;
import com.scut.filetransfer.util.WifiUtil;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * 管理连接的类
 * 
 * @author ccz
 * 
 */
public class ConnectionManager {

	private static ConnectionManager connectionManager;
	public static LocationManager locationManager;
	private static String ipAddress;

	/**
	 * 线程池
	 */
	private static ScheduledExecutorService scheduledThreadPool;
	/**
	 * 线程池最大数量
	 */
	private final static int Max_Thread_Num = 5;

	public static ConnectionManager getInstance(Context context) {
		if (connectionManager == null)
			connectionManager = new ConnectionManager(context);
		scheduledThreadPool = Executors.newScheduledThreadPool(Max_Thread_Num);
		ipAddress = getLocalAddress();
		return connectionManager;
	}

	@SuppressWarnings("static-access")
	private ConnectionManager(Context context) {
		this.context = context;
		locationManager = new LocationManager(context);
	}

	public static int SERVERPORT = 8878;
	private String preIP;
	private static Context context;

	public static void sendMsg(String ip, String msg) {
		Socket socket = null;
		try {
			socket = new Socket(ip, SERVERPORT);
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			os.println(msg);
			os.flush();
		} catch (Exception unknownHost) {

		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void scan() {
		preIP = getLocAddrIndex();
		if (preIP == null || preIP.equals("")) {
			Toast.makeText(context, context.getString(R.string.no_wifi),
					Toast.LENGTH_SHORT).show();
			((Activity) context).finish();
			return;
		}

		for (int i = 0; i < 256; i++) {
			final int index = i;
			if (scheduledThreadPool.isShutdown()) {
				scheduledThreadPool = Executors
						.newScheduledThreadPool(Max_Thread_Num);
			}
			scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					try {
						String current_ip = preIP + index;
						if (!current_ip.equals(ipAddress))
							ConnectionManager.sendMsg(
									current_ip,
									ConnectionManager.ipAddress
											+ ","
											+ android.os.Build.MODEL
											+ ","
											+ ConnectionManager.locationManager
													.getLatitude()
											+ ","
											+ ConnectionManager.locationManager
													.getLontitude() + ",scan");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 1, 3, TimeUnit.SECONDS);
		}
	}

	public static void closeThreadPool() {
		scheduledThreadPool.shutdownNow();
	}

	public static String getLocalAddress() {
		String miui = getSystemProperty("ro.miui.ui.version.name");
		WifiUtil wifiUtil = new WifiUtil(context);
		List<String> ipList = getLocalAddressList();
		int index = ipList.size() - 1;
		if (miui != null && !miui.equals("")) {
			index = index - 1;
			if (wifiUtil.isWifiApEnabled() && wifiUtil.isMobileConnected())
				index = index - 1;
		} else if (wifiUtil.isWifiApEnabled() && wifiUtil.isMobileConnected()) {
			index = index - 1;
		}
		return ipList.get(index);
	}

	private static List<String> getLocalAddressList() {
		List<String> ipList = new ArrayList<String>();
		String ipaddress = "";

		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			InetAddress ip = null;
			while (en.hasMoreElements()) {
				NetworkInterface networks = en.nextElement();
				Enumeration<InetAddress> address = networks.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ip
									.getHostAddress())) {
						ipaddress = ip.getHostAddress();
						ipList.add(ipaddress);
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ipList;
	}

	public static String getIpAddress() {
		return ipAddress;
	}

	public static void setIpAddress(String ipAddress) {
		ConnectionManager.ipAddress = ipAddress;
	}

	public String getLocAddrIndex() {
		String str = getLocalAddress();
		if (!str.equals("")) {
			return str.substring(0, str.lastIndexOf(".") + 1);
		}
		return null;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	@SuppressWarnings("static-access")
	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	/**
	 * 判断是不是MIUI系统
	 * 
	 * @param propName
	 * @return
	 */
	private static String getSystemProperty(String propName) {
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {

				}
			}
		}
		return line;
	}

}