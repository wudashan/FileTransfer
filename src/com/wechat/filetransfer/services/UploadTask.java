package com.wechat.filetransfer.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.wechat.filetransfer.bean.FileInfo;
import com.wechat.filetransfer.util.AESUtil;

import android.content.Context;


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
		new UploadThread().start();
	}
	
	
	/**
	 * 上传线程
	 * @author Wise
	 *
	 */
	class UploadThread extends Thread{
		
		public UploadThread() {
			super();
		}
		
		public void run(){
			//SOCKET连接需要的类
			Socket socket = null;
			DataOutputStream dos=null;
			DataInputStream dis = null;
			DataInputStream disClient = null;
			RandomAccessFile raf = null;
			ServerSocket ss=null;
			try {
				File file=new File(filePath);
				ss=new ServerSocket(fileInfo.getPort());
				socket=ss.accept();
				dos=new DataOutputStream(socket.getOutputStream());
				dis = new DataInputStream(new FileInputStream(filePath));
				disClient = new DataInputStream(socket.getInputStream());
				int buffferSize=1024;
				byte[]bufArray=new byte[buffferSize];
				dos.writeUTF(file.getName()); 
				dos.flush(); 
				dos.writeInt((int) file.length()); 
				dos.flush(); 
				int start = disClient.readInt();
				int len = -1;
				raf = new RandomAccessFile(file, "r");
				raf.seek(start);
				
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
