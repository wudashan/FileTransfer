package com.scut.filetransfer.listener;
import com.scut.filetransfer.R;
import com.scut.filetransfer.activity.PageBlueTooth;
import com.scut.filetransfer.application.FileTransferApplication;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 设置蓝牙可见按钮监听器
 * 
 * 
 */
public class SetVisibleBtnClickListener implements OnClickListener {
	private static final String[] arr = { "60", "120", "180", "240", "300" };

	private PageBlueTooth mPageBlueTooth;

	private String visibleTime; // 蓝牙可见时间
	private Spinner mSelectTimeSpinner;
	private Button mSelectTimeEnsureBtn;
	private Button mSelectTimeCancelBtn;
	private Context mContext;
	private AlertDialog mSelectTimeDialog; // 选择时间dialog

	public SetVisibleBtnClickListener(Fragment fragment) {
		this.mPageBlueTooth = (PageBlueTooth) fragment;
		this.mContext = FileTransferApplication.getInstance().getApplicationContext();
	}

	@Override
	public void onClick(View v) {
		if (null == mSelectTimeDialog) {
			View view = LayoutInflater.from(mPageBlueTooth.getActivity()).inflate(R.layout.select_time_dialog, null);
			mSelectTimeSpinner = (Spinner) view.findViewById(R.id.selectTimeSpinner);
			mSelectTimeEnsureBtn = (Button) view.findViewById(R.id.selectTimeEnsureBtn);
			mSelectTimeCancelBtn = (Button) view.findViewById(R.id.selectTimeCancelBtn);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mPageBlueTooth.getActivity(),android.R.layout.simple_spinner_item, arr);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSelectTimeSpinner.setAdapter(adapter);
			mSelectTimeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							visibleTime = arr[position];
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

						}
					});

			mSelectTimeEnsureBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 发送请求，设置蓝牙可见时间
					Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,Integer.parseInt(visibleTime));
					mPageBlueTooth.getActivity().startActivity(intent);
					mSelectTimeDialog.dismiss();
				}
			});
			mSelectTimeCancelBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mSelectTimeDialog.dismiss();
				}
			});
			mSelectTimeDialog = new AlertDialog.Builder(mPageBlueTooth.getActivity()).create();
			mSelectTimeDialog.setTitle(mContext.getString(R.string.set_bluetooth_see));
			mSelectTimeDialog.setView(view);
		}
		mSelectTimeDialog.show();
	}

}