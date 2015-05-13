package com.scut.filetransfer.activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.scut.filetransfer.R;
import com.scut.filetransfer.adapter.AdapterManager;
import com.scut.filetransfer.application.BluetoothApplication;
import com.scut.filetransfer.entity.MyMenuItem;
import com.scut.filetransfer.entity.TouchObject;
import com.scut.filetransfer.listener.DeviceListCCMenuListener;
import com.scut.filetransfer.listener.SearchDeviceBtnClickListener;
import com.scut.filetransfer.listener.SelectFileBtnClickListener;
import com.scut.filetransfer.listener.SetVisibleBtnClickListener;
import com.scut.filetransfer.receiver.PairStateChangeReceiver;

public class PageBlueTooth extends Fragment {
	
	public static final String SEND_FILE_NAME = "sendFileName";
	public static final int RESULT_CODE = 100; // 选择文件 返回码
	public static final int REQUEST_CODE = 101; // 选择文件 请求码
	public static final int REQUEST_ENABLE = 10000; // 打开蓝牙 请求码
	
	
	
	private BluetoothApplication mApplication;
	private AdapterManager mAdapterManager; // Adapter管理器
	private TouchObject mTouchObject; // 当前操作对象
	
	private PairStateChangeReceiver mPairStateChangeReceiver; // 配对状态改变广播接收器
	private BluetoothSocket socket; // 蓝牙连接socket
	private Handler mOthHandler; // 其它线程Handler
	private SearchDeviceBtnClickListener mSearchDeviceBtnClickListener; // 搜索设备按钮监听器

	
	
	ListView mDeviceListView;
	TextView mSendFileNameTV;
	TextView mSearchBtnText;
	LinearLayout mSetVisibleBtn;
	LinearLayout mSearchDeviceBtn;
	LinearLayout mSelectFileBtn;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.page_bluetooth, container, false);
		
		mDeviceListView = (ListView) view.findViewById(R.id.deviceListView);
		mSetVisibleBtn = (LinearLayout) view.findViewById(R.id.setDeviceVisibleBtn);
		mSearchDeviceBtn = (LinearLayout) view.findViewById(R.id.searchDeviceBtn);
		mSelectFileBtn = (LinearLayout) view.findViewById(R.id.cancelSearchBtn);
		mSendFileNameTV = (TextView) view.findViewById(R.id.sendFileTV);
		mSearchBtnText = (TextView) view.findViewById(R.id.searchBtnText);
		
		mApplication = BluetoothApplication.getInstance();
		mTouchObject = mApplication.getTouchObject();
		// 实例化Adapter管理器并设置到Application
		mAdapterManager = new AdapterManager(getActivity());
		mApplication.setAdapterManager(mAdapterManager);
		mDeviceListView.setAdapter(mAdapterManager.getDeviceListAdapter());
		mSearchDeviceBtnClickListener = new SearchDeviceBtnClickListener(this);
		
		mDeviceListView.setOnCreateContextMenuListener(new DeviceListCCMenuListener(mDeviceListView));
		mSetVisibleBtn.setOnClickListener(new SetVisibleBtnClickListener(this));
		mSearchDeviceBtn.setOnClickListener(mSearchDeviceBtnClickListener);
		mSelectFileBtn.setOnClickListener(new SelectFileBtnClickListener(this));
		
		return view;
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("PageBlueTooth", requestCode+" "+resultCode);
		if (requestCode == REQUEST_ENABLE) {
			// 请求为 "打开蓝牙"
			if (resultCode == Activity.RESULT_OK) {
				// 打开蓝牙成功
				mSearchDeviceBtnClickListener.beginDiscovery();
			} else {
				// 打开蓝牙失败
				Toast.makeText(getActivity(), "打开蓝牙失败！", Toast.LENGTH_LONG).show();
			}
		} else if (resultCode == RESULT_CODE) {
			// 请求为 "选择文件"
				// 取得选择的文件名
				String sendFileName = data.getStringExtra(SEND_FILE_NAME);
				Log.e("PageBlueTooth", sendFileName);
				mSendFileNameTV.setText(sendFileName);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == MyMenuItem.MENU_GROUP_DEVICE) {
			switch (item.getItemId()) {
			case MyMenuItem.MENU_ITEM_PAIR_ID: // 配对
				doPair();
				break;

			case MyMenuItem.MENU_ITEM_SEND_ID: // 发送文件
				doSendFileByBluetooth();
				break;

			default:
				break;
			}
		}
		return true;
	}
	
	/**
	 * 配对
	 */
	private void doPair() {
		if (mTouchObject.bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
			// 未与该设备配对
			if (null == mPairStateChangeReceiver) {
				mPairStateChangeReceiver = new PairStateChangeReceiver(getActivity());
			}
			// 注册设备配对状态改变监听器
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			getActivity().registerReceiver(mPairStateChangeReceiver, intentFilter);
			if (null == mOthHandler) {
				HandlerThread handlerThread = new HandlerThread("other_thread");
				handlerThread.start();
				mOthHandler = new Handler(handlerThread.getLooper());
			}
			mOthHandler.post(new Runnable() {

				@Override
				public void run() {
					initSocket(); // 取得socket
					try {
						socket.connect(); // 请求配对
						// mAdapterManager.updateDeviceAdapter();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			// 已经与该设备配对
			Toast.makeText(getActivity(), "该设备已配对，无需重复操作！",
					Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 通过蓝牙发送文件
	 */
	private void doSendFileByBluetooth() {
		// 取得文件全路径
		String filePath = mSendFileNameTV.getText().toString().trim();
		if (!filePath.equals("null")) {
			if (null == mOthHandler) {
				HandlerThread handlerThread = new HandlerThread("other_thread");
				handlerThread.start();
				mOthHandler = new Handler(handlerThread.getLooper());
			}
			mOthHandler.post(new Runnable() {

				@Override
				public void run() {
					//安卓4.0以后的用法
					 Intent intent = new Intent(Intent.ACTION_SEND);
					 intent.setType("image/*");
					 intent.setType("video/*");
					 intent.setType("audio/*");
					 intent.setType("text/*");
					 intent.setType("application/*");
					 intent.setType("message/*");
					 intent.setType("x-world/*");
					 intent.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
					 String filePath = mSendFileNameTV.getText().toString().trim();
					 intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
					 startActivity(intent);
					 
					try {
						if (null != socket) {
							socket.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		} else {
			Toast.makeText(getActivity(), "请选择要发送的文件!",
					Toast.LENGTH_LONG).show();
		}
	}
	
	
	/**
	 * 取得BluetoothSocket
	 */
	private void initSocket() {
		BluetoothSocket temp = null;
		try {
			Method m = mTouchObject.bluetoothDevice.getClass().getMethod(
					"createRfcommSocket", new Class[] { int.class });
			temp = (BluetoothSocket) m.invoke(mTouchObject.bluetoothDevice, 1);
			// 怪异错误： 直接赋值给socket,对socket操作可能出现异常， 要通过中间变量temp赋值给socket
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		socket = temp;
	}
	
	/**
	 * 改变按钮显示文字
	 */
	public void changeSearchBtnText() {
		mSearchBtnText.setText("重新搜索");
	}
	
}
