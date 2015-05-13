package com.scut.filetransfer.service;
import com.scut.filetransfer.bean.FileInfo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UploadService extends Service{


	public static final String ACTION_START  = "ACTION_UPLOAD_START";
	public static final String ACTION_STOP  = "ACTION_STOP";
	public static final String ACTION_UPDATE  = "ACTION_UPDATE";
	private static int index = 0;
	private String filePath;
	private static final int MAX_NUM=100;
	private UploadTask[] uploadTasks = new UploadTask[MAX_NUM];
	private FileInfo fileInfo = null;
	

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		for (int i = 0; i<MAX_NUM; i++) {
			if (uploadTasks[i] != null) {
				uploadTasks[i].uploadThread.onDestroySocketConnection();
			}
		}
		super.onDestroy();
	}

	//启动Service
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//当被进程杀死时，会自动调用onStartCommand方法,防止空指针异常
		if (intent != null) {
			//若传过来的动作是START
			if (ACTION_START.equals(intent.getAction())) {
				//fileID与数组下标相同
				filePath = intent.getStringExtra("filePath");
				fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				//开始上传
				System.out.println("UploadService start:"+fileInfo);
				//启动上传线程进行下载
				uploadTasks[index] = new UploadTask(UploadService.this, fileInfo,filePath);
				uploadTasks[index].upload();
				index ++;
			}
		}else {
			
		}
		return super.onStartCommand(intent, flags, startId);
	}
}