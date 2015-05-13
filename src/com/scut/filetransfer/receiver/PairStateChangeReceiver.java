package com.scut.filetransfer.receiver;
import com.scut.filetransfer.adapter.AdapterManager;
import com.scut.filetransfer.application.BluetoothApplication;
import com.scut.filetransfer.entity.TouchObject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 配对状态改变监听器
 * 
 *
 */
public class PairStateChangeReceiver extends BroadcastReceiver {
	private BluetoothApplication mApplication;
	private Activity mActivity;
	private AdapterManager mAdapterManager;
	private TouchObject mTouchObject;
	
	public PairStateChangeReceiver(Activity activity){
		this.mApplication = BluetoothApplication.getInstance();
		this.mActivity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
			//状态改变
			if(null == mAdapterManager){
				mAdapterManager = mApplication.getAdapterManager();
			}
			if(null == mTouchObject){
				mTouchObject = mApplication.getTouchObject();
			}
			//取得状态改变的设备，更新设备列表信息 （配对状态）
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mAdapterManager.changeDevice(mTouchObject.clickDeviceItemId, device);
			mAdapterManager.updateDeviceAdapter();
			mActivity.unregisterReceiver(this);
		}
	}

}
