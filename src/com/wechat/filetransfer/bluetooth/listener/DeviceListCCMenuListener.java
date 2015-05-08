package com.wechat.filetransfer.bluetooth.listener;


import com.wechat.filetransfer.bluetooth.application.BluetoothApplication;
import com.wechat.filetransfer.bluetooth.entity.MyMenuItem;
import com.wechat.filetransfer.bluetooth.entity.TouchObject;

import android.bluetooth.BluetoothDevice;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * ListView元素长按事件监听器
 * @author 210001001427
 *
 */
public class DeviceListCCMenuListener implements OnCreateContextMenuListener {
	private TouchObject mTouchObject;
	private ListView mDeviceListView;
	
	public DeviceListCCMenuListener(ListView deviceListView){
		this.mDeviceListView = deviceListView;
		mTouchObject = BluetoothApplication.getInstance().getTouchObject();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		mTouchObject.clickDeviceItemId = info.position;
		mTouchObject.bluetoothDevice = (BluetoothDevice) mDeviceListView.getAdapter().getItem(info.position);
		menu.setHeaderTitle("请选择操作");
		menu.add(MyMenuItem.MENU_GROUP_DEVICE, 
				 MyMenuItem.MENU_ITEM_PAIR_ID, 
				 MyMenuItem.MENU_ITEM_PAIR_ORDER, 
				 MyMenuItem.MENU_ITEM_PAIR_TITLE);
//		menu.add(MyMenuItem.MENU_GROUP_DEVICE, 
//				 MyMenuItem.MENU_ITEM_UNPAIR_ID, 
//				 MyMenuItem.MENU_ITEM_UNPAIR_ORDER, 
//				 MyMenuItem.MENU_ITEM_UNPAIR_TITLE);
		menu.add(MyMenuItem.MENU_GROUP_DEVICE, 
				 MyMenuItem.MENU_ITEM_SEND_ID, 
				 MyMenuItem.MENU_ITEM_SEND_ORDER, 
				 MyMenuItem.MENU_ITEM_SEND_TITLE);
	}

}
