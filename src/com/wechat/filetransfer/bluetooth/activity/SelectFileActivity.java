package com.wechat.filetransfer.bluetooth.activity;

import java.io.File;

import com.wechat.filetransfer.R;
import com.wechat.filetransfer.activity.PageBlueTooth;
import com.wechat.filetransfer.bluetooth.adapter.AdapterManager;
import com.wechat.filetransfer.bluetooth.adapter.FileListAdapter;
import com.wechat.filetransfer.bluetooth.application.BluetoothApplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class SelectFileActivity extends Activity {
	ListView mFileListView;
	FileListAdapter mFileListAdapter;
	AdapterManager mAdapterManager;
	
	private Handler mOtherHandler;
	private Runnable updateFileListRunnable;
	
	private File file;   //当前操作文件 或 文件夹
	
	private String sdcardPath; //sd卡路径
	private String path;    //当前文件父目录
	
	Button mBackBtn;  //返回按钮
	Button mEnsureBtn;   //确定按钮
	Button mCancelBtn;   //取消按钮

	TextView mLastClickView;   //最后一次点击的文件 --文件名
	TextView mNowClickView;   //现在点击的文件 -- 文件名
	private boolean isSelected = false;   //是否选择了文件   (非文件夹)
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_file);
		
		mFileListView = (ListView) findViewById(R.id.fileListView);
		mBackBtn = (Button) findViewById(R.id.selectFileBackBtn);
		mEnsureBtn = (Button) findViewById(R.id.selectFileEnsureBtn);
		mCancelBtn = (Button) findViewById(R.id.selectFileCancelBtn);
		
		//取得sd卡目录
		sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		path = sdcardPath;
		
		mAdapterManager = BluetoothApplication.getInstance().getAdapterManager();
		mFileListView.setAdapter(mAdapterManager.getFileListAdapter());
		//首先显示sd卡下所有文件及文件夹
		mAdapterManager.updateFileListAdapter(path);
		
		mFileListView.setOnItemClickListener(mFileListOnItemClickListener);
		mBackBtn.setOnClickListener(mBackBtnClickListener);
		mEnsureBtn.setOnClickListener(mEnsureBtnClickListener);
		mCancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SelectFileActivity.this.finish();
			}
		});
		
	}
	
	/**
	 * 
	 */
	private OnItemClickListener mFileListOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			//当前操作文件 或 文件夹
			file = (File) mFileListView.getAdapter().getItem(position);
			if(file.isFile()){
				//如果是文件， 则选中 --- 文件名变色 
				if(null != mLastClickView){
					//若之前选中了文件， 则取消之前选择  -- 恢复颜色
					mLastClickView.setTextColor(Color.WHITE);
				}
				//改变文件名颜色, 选中文件
				mNowClickView = (TextView) view.findViewById(R.id.fileNameTV);
				mNowClickView.setTextColor(Color.BLUE);
				isSelected = true;
				//设置为最后一次点击的文件
				mLastClickView = mNowClickView;
			}else {
				//如果是文件夹， 则显示该文件夹下所有文件 及 文件夹
				path = file.getAbsolutePath();
				updateFileList();
			}							
		}

	};
	
	private OnClickListener mBackBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(path.equals(sdcardPath)){
				//当前文件父目录为 sd卡， 不做任何操作
				return ;
			}
			//返回上一级目录
			path = path.substring(0, path.lastIndexOf("/"));
			updateFileList();
		}
	};
	
	private OnClickListener mEnsureBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!isSelected){
				//没有选择文件
				Toast.makeText(SelectFileActivity.this, "请选择文件!", Toast.LENGTH_LONG).show();
				return ;
			}
			//将选择的文件的全路径 返回
			Intent intent = new Intent();
			intent.putExtra(PageBlueTooth.SEND_FILE_NAME, file.getAbsolutePath());
			Log.e("SelectFileActivity", file.getAbsolutePath());
			SelectFileActivity.this.setResult(PageBlueTooth.RESULT_CODE, intent);
			SelectFileActivity.this.finish();
		}
	};
	
	/**
	 * 根据父目录path显示path下所有文件及文件夹
	 */
	private void updateFileList() {
		if(null != mLastClickView){
			//进入另一文件夹，则取消之前的选择
			mLastClickView.setTextColor(Color.WHITE);
			mLastClickView = null;
			isSelected = false;
		}
		if(null == updateFileListRunnable){
			updateFileListRunnable = new Runnable() {
							
				@Override
				public void run() {
					
					mAdapterManager.updateFileListAdapter(path);
				}
			};
		}
		if(null == mOtherHandler){
			HandlerThread handlerThread = new HandlerThread("other_thread");
			handlerThread.start();
			mOtherHandler = new Handler(handlerThread.getLooper());
		}
		mOtherHandler.post(updateFileListRunnable);
	}
}
