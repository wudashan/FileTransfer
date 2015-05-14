package com.scut.filetransfer.listener;

import com.scut.filetransfer.R;
import com.scut.filetransfer.activity.PageBlueTooth;
import com.scut.filetransfer.adapter.AdapterManager;
import com.scut.filetransfer.application.FileTransferApplication;
import com.scut.filetransfer.receiver.ScanBluetoothReceiver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 搜索设备按钮监听器
 * 
 * 
 */
public class SearchDeviceBtnClickListener implements OnClickListener {
	private Context mContext;
	private PageBlueTooth mPageBlueTooth;
	private AdapterManager mAdapterManager;

	private BluetoothAdapter mBluetoothAdapter;
	private ScanBluetoothReceiver mScanBluetoothReceiver; // 蓝牙扫描监听器
	private AlertDialog mAlertDialog; // 确定打开蓝牙 dialog
	private ProgressDialog mProgressDialog;

	public SearchDeviceBtnClickListener(Fragment fragment) {
		this.mPageBlueTooth = (PageBlueTooth) fragment;
		this.mAdapterManager = FileTransferApplication.getInstance().getAdapterManager();
		this.mContext = FileTransferApplication.getInstance().getApplicationContext();
	}

	@Override
	public void onClick(View v) {
		// 清空蓝牙设备列表
		mAdapterManager.clearDevice();
		mAdapterManager.updateDeviceAdapter();
		if (null == mBluetoothAdapter) {
			// 取得系统蓝牙适配器
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		if (!mBluetoothAdapter.isEnabled()) {
			// 蓝牙未打开, 打开蓝牙提示框
			if (null == mAlertDialog) {
				mAlertDialog = new AlertDialog.Builder(mPageBlueTooth.getActivity())
						.setPositiveButton(mContext.getString(R.string.ensure), new Dialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 发送请求，打开蓝牙
								Intent startBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
								mPageBlueTooth.startActivityForResult(startBluetoothIntent,PageBlueTooth.REQUEST_ENABLE);
							}

						}).setNeutralButton(mContext.getString(R.string.cancel), new Dialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,int which) {
								mAlertDialog.dismiss();
							}

						}).create();
			}
			mAlertDialog.setTitle(mContext.getString(R.string.open_bluetooth));
			mAlertDialog.setMessage(mContext.getString(R.string.is_open_bluetooth));
			mAlertDialog.show();
		} else {
			// 蓝牙已打开， 开始搜索设备
			Log.i("SearchDeviceBtnClickListener", "beginDiscovery()");
			beginDiscovery();
		}
	}

	/**
	 * 开始搜索设备...
	 */
	public void beginDiscovery() {
		if (null == mProgressDialog) {
			mProgressDialog = new ProgressDialog(mPageBlueTooth.getActivity());
			mProgressDialog.setMessage(mContext.getString(R.string.searching_device));
		}
		mProgressDialog.show();
		// 注册蓝牙扫描监听器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		if (null == mScanBluetoothReceiver) {
			mScanBluetoothReceiver = new ScanBluetoothReceiver(mContext,mPageBlueTooth,mAdapterManager, mProgressDialog);
		}
		mPageBlueTooth.getActivity().registerReceiver(mScanBluetoothReceiver,intentFilter);
		//开始查找
		mBluetoothAdapter.startDiscovery();
	}

}