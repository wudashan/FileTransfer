package com.wechat.filetransfer.bluetooth.listener;


import com.wechat.filetransfer.activity.PageBlueTooth;
import com.wechat.filetransfer.bluetooth.activity.SelectFileActivity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 选择文件按钮监听器
 * 
 *
 */
public class SelectFileBtnClickListener implements OnClickListener {
	private PageBlueTooth mPageBlueTooth;
	
	public SelectFileBtnClickListener(Fragment fragment){
		this.mPageBlueTooth = (PageBlueTooth) fragment;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(mPageBlueTooth.getActivity(), SelectFileActivity.class);
		mPageBlueTooth.startActivityForResult(intent, PageBlueTooth.REQUEST_CODE);
	}

}
