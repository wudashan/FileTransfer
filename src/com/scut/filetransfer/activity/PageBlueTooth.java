package com.scut.filetransfer.activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.ViewGroup;
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
import com.scut.filetransfer.application.FileTransferApplication;
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
	
	
	
	private FileTransferApplication mApplication;
	private Context mContext;
	private AdapterManager mAdapterManager; // Adapter管理器
	private TouchObject mTouchObject; // 当前操作对象
	private PairStateChangeReceiver mPairStateChangeReceiver; // 配对状态改变广播接收器
	private BluetoothSocket socket; // 蓝牙连接socket
	private Handler mOthHandler; // 其它线程Handler
	private SearchDeviceBtnClickListener mSearchDeviceBtnClickListener; // 搜索设备按钮监听器
	private DeviceListCCMenuListener mDeviceListCCMenuListener;
	private SetVisibleBtnClickListener mSetVisibleBtnClickListener;
	private SelectFileBtnClickListener mSelectFileBtnClickListener;

	
	
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
		
		//布局加载
		mDeviceListView = (ListView) view.findViewById(R.id.deviceListView);
		mSetVisibleBtn = (LinearLayout) view.findViewById(R.id.setDeviceVisibleBtn);
		mSearchDeviceBtn = (LinearLayout) view.findViewById(R.id.searchDeviceBtn);
		mSelectFileBtn = (LinearLayout) view.findViewById(R.id.cancelSearchBtn);
		mSendFileNameTV = (TextView) view.findViewById(R.id.sendFileTV);
		mSearchBtnText = (TextView) view.findViewById(R.id.searchBtnText);
		
		mApplication = FileTransferApplication.getInstance();
		mContext = mApplication.getApplicationContext();
		mTouchObject = mApplication.getTouchObject();
		mAdapterManager = mApplication.getAdapterManager();
		
		mDeviceListView.setAdapter(mAdapterManager.getDeviceListAdapter());
		mDeviceListCCMenuListener = new DeviceListCCMenuListener(mDeviceListView);
		mDeviceListView.setOnCreateContextMenuListener(mDeviceListCCMenuListener);
		
		mSearchDeviceBtnClickListener = new SearchDeviceBtnClickListener(this);
		mSearchDeviceBtn.setOnClickListener(mSearchDeviceBtnClickListener);
		
		
		mSetVisibleBtnClickListener = new SetVisibleBtnClickListener(this);
		mSetVisibleBtn.setOnClickListener(mSetVisibleBtnClickListener);
		
		mSelectFileBtnClickListener = new SelectFileBtnClickListener(this);
		mSelectFileBtn.setOnClickListener(mSelectFileBtnClickListener);
		
		return view;
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE) {
			// 请求为 "打开蓝牙"
			if (resultCode == Activity.RESULT_OK) {
				// 打开蓝牙成功，开始搜索附近的蓝牙设备
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
	
	/**
	 * 长按的操作
	 */
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
			//标记
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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			// 已经与该设备配对
			Toast.makeText(getActivity(), mContext.getString(R.string.has_already_attach),Toast.LENGTH_LONG).show();
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
			Toast.makeText(getActivity(), mContext.getString(R.string.please_select_file),Toast.LENGTH_LONG).show();
		}
	}
	
	
	/**
	 * 取得BluetoothSocket，通过反射
	 */
	private void initSocket() {
		try {
			Method m = mTouchObject.bluetoothDevice.getClass().getMethod(
					"createRfcommSocket", new Class[] { int.class });
			socket = (BluetoothSocket) m.invoke(mTouchObject.bluetoothDevice, 1);
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
	}
	
	/**
	 * 改变按钮显示文字
	 */
	public void changeSearchBtnText() {
		mSearchBtnText.setText("重新搜索");
	}
	
}
