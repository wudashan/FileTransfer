package com.scut.filetransfer.application;
import com.scut.filetransfer.adapter.AdapterManager;
import com.scut.filetransfer.entity.TouchObject;

import android.app.Application;

public class FileTransferApplication extends Application {
	/**
	 * Application实例--单例模式
	 */
	private static FileTransferApplication application;
	
	/**
	 * AdapterManager实例
	 */
	private AdapterManager mAdapterManager;
	
	/**
	 * 当前操作的对象实例
	 */
	private TouchObject mTouchObject;

	@Override
	public void onCreate() {
		super.onCreate();
		if(application == null){
			application = this;
		}
		mTouchObject = new TouchObject();
		mAdapterManager = new AdapterManager(getApplicationContext());
	}
	
	/**
	 * 获取Application实例
	 */
	public static FileTransferApplication getInstance(){
		return application;
	}

	public AdapterManager getAdapterManager() {
		return mAdapterManager;
	}

	public void setAdapterManager(AdapterManager adapterManager) {
		this.mAdapterManager = adapterManager;
	}

	public TouchObject getTouchObject() {
		return mTouchObject;
	}

	public void setTouchObject(TouchObject touchObject) {
		this.mTouchObject = touchObject;
	}

}