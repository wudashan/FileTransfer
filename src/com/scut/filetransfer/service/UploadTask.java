package com.scut.filetransfer.service;

import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.scut.filetransfer.activity.PageSend;
import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.util.AESUtil;
import com.scut.filetransfer.util.PortUtil;

/**
 * 上传任务类
 * 
 * @author Wise
 * 
 */
public class UploadTask {

	private Context context;
	private FileInfo fileInfo;
	private int finished;
	private String filePath;
	private boolean isPause;
	private AESUtil aesUtil;
	public UploadThread uploadThread;
	//所有线程公用一个ServerSocket
	public static ServerSocket socketServer ;
	
	static{
		try {
			socketServer = new ServerSocket(PortUtil.PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Pause() {
		isPause = true;
	}

	public void Resume() {
		isPause = false;
	}

	public UploadTask(Context context, FileInfo fileInfo, String filePath) {
		super();
		this.context = context;
		this.fileInfo = fileInfo;
		this.filePath = filePath;
		aesUtil = AESUtil.getInstance();
	}

	public void upload() {
		// 创建下载进程
		uploadThread = new UploadThread();
		uploadThread.start();
	}

	/**
	 * 上传线程
	 * 
	 * @author Wise
	 * 
	 */
	class UploadThread extends Thread {


		public void run() {
			
			File file = new File(filePath);
			ServerSocket ss = null;
			Socket socket = null;
			DataOutputStream dos = null;
			DataInputStream disClient = null;
			FileInputStream fis = null;
			DataInputStream dis = null;
			try {
				ss = socketServer;
				socket = ss.accept();
				dos = new DataOutputStream(socket.getOutputStream());
				disClient = new DataInputStream(socket.getInputStream());
				int buffferSize = 1024;
				byte[] bufArray = new byte[buffferSize];
				dos.writeUTF(file.getName());
				dos.flush();
				dos.writeInt((int) file.length());
				dos.flush();
				int start = disClient.readInt();
				int len = -1;
				// 从指定位置开始发送数据
				fis = new FileInputStream(filePath);
				fis.skip((long) start);
				dis = new DataInputStream(fis);
				byte[] result = null ;
				while ((len = dis.read(bufArray)) != -1) {
					try {
						//result = aesUtil.encrypt(bufArray);
						//Log.i("UploadTask", result.length+"");
						result = aesUtil.encrypt(bufArray);
						//System.err.println("长度：" + result.length);
						//System.err.println("密钥：" + aesUtil.getSeed());
						//Log.i("UploadTask", result.length+"");
						dos.writeInt(result.length);
						dos.write(result, 0, result.length);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//dos.write(result, 0, result.length);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
//					if (ss != null) {
//						ss.close();
//					}
					if (socket != null) {
						socket.close();
					}
					if (dos != null) {
						dos.close();
					}
					if (dis !=null) {
						dis.close();
					}
					if (disClient !=null) {
						disClient.close();
					}
					if (fis !=null) {
						fis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
