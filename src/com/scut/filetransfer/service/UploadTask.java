package com.scut.filetransfer.service;
import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.util.AESUtil;

/**
 * 上传任务类
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
	
	public void Pause(){
		isPause = true;
	}
	
	public void Resume(){
		isPause = false;
	}
	
	public UploadTask(Context context, FileInfo fileInfo,String filePath) {
		super();
		this.context = context;
		this.fileInfo = fileInfo;
		this.filePath = filePath;
		aesUtil = AESUtil.getInstance();
	}
	
	public void upload(){
		//创建下载进程
		uploadThread = new UploadThread();
		uploadThread.start();
	}
	
	
	/**
	 * 上传线程
	 * @author Wise
	 *
	 */
	 class UploadThread extends Thread{
		
		//SOCKET连接需要的类
		Socket socket = null;
		DataOutputStream dos=null;
		FileInputStream fis = null;
		DataInputStream dis = null;
		DataInputStream disClient = null;
		RandomAccessFile raf = null;
		ServerSocket ss=null;
		
		/**
		 * 当Service被强制关闭时，启动该方法，结束所有连接
		 */
		public void onDestroySocketConnection(){
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (disClient != null) {
				try {
					disClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public UploadThread() {
			super();
		}
		
		public void run(){
			try {
				File file=new File(filePath);
				ss=new ServerSocket(fileInfo.getPort());
				socket=ss.accept();
				dos=new DataOutputStream(socket.getOutputStream());
				disClient = new DataInputStream(socket.getInputStream());
				int buffferSize=1024;
				byte[]bufArray=new byte[buffferSize];
				dos.writeUTF(file.getName()); 
				dos.flush(); 
				dos.writeInt((int) file.length()); 
				dos.flush(); 
				int start = disClient.readInt();
				int len = -1;
				//从指定位置开始发送数据
				fis = new FileInputStream(filePath);
				fis.skip((long)start);
				dis = new DataInputStream(fis);
				//raf = new RandomAccessFile(file, "r");
				//raf.seek(start);
				
				while ((len = dis.read(bufArray)) != -1) { 
					try {
						aesUtil.encrypt(bufArray);
						dos.write(bufArray, 0, len); 
					} catch (Exception e) {
						e.printStackTrace();
					}
				} 
				dos.flush();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					socket.close();
					dis.close();
					disClient.close();
					dos.close();
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
