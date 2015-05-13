package com.scut.filetransfer.receiver;
import com.scut.filetransfer.activity.PageBlueTooth;
import com.scut.filetransfer.adapter.AdapterManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * 蓝牙扫描监听器
 * @author 210001001427
 *
 */
public class ScanBluetoothReceiver extends BroadcastReceiver {
	private PageBlueTooth mPageBlueTooth;
	private AdapterManager mAdapterManager;
	private ProgressDialog mProgressDialog;
	private boolean isFirstSearch = true;
	
	public ScanBluetoothReceiver(Fragment fragment, AdapterManager adapterManager, ProgressDialog progressDialog){
		this.mPageBlueTooth = (PageBlueTooth) fragment;
		this.mAdapterManager = adapterManager;
		this.mProgressDialog = progressDialog;
	}

	@Override
	public void onReceive(Context context, final Intent intent) {
		if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)){
			//扫描到蓝牙
			//取得扫描到的蓝牙，添加到设备列表，更新列表
			BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mAdapterManager.addDevice(bluetoothDevice);
			mAdapterManager.updateDeviceAdapter();
		}else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
			//扫描设备结束
			Log.i("BluetoothDemo", "over");
			mProgressDialog.dismiss();
			if(mAdapterManager.getDeviceList().size() == 0){
				//扫描到的设备数为0
				Toast.makeText(mPageBlueTooth.getActivity(), "没有找到其它蓝牙设备！", Toast.LENGTH_LONG).show();
			}
			if(isFirstSearch){
				//第一次查找后， 设置按钮显示文本为 "重新查找"
				mPageBlueTooth.changeSearchBtnText();
				isFirstSearch = false;
			}
			//取消监听
			mPageBlueTooth.getActivity().unregisterReceiver(this);
		}
	}

}
