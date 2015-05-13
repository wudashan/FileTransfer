package com.scut.filetransfer.service;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.Intent;

import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.database.FileInfoDAO;
import com.scut.filetransfer.database.FileInfoDAOImpl;
import com.scut.filetransfer.util.AESUtil;
/**
 * 下载任务类
 * @author Wise
 *
 */
public class DownloadTask {
	private Context context = null;
	private FileInfo fileInfo = null;
	private FileInfoDAO fileInfoDAO = null;
	public boolean isPause = false;
	private AESUtil aesUtil;
	public DownloadThread downloadThread;
	
	public void Pause(){
		isPause = true;
	}
	
	public void Resume(){
		isPause = false;
	}
	
	public DownloadTask(Context context, FileInfo fileInfo) {
		super();
		this.context = context;
		this.fileInfo = fileInfo;
		fileInfoDAO = new FileInfoDAOImpl(context);
		aesUtil = AESUtil.getInstance();
	}
	
	public void download(){
		//创建下载进程
		downloadThread = new DownloadThread();
		downloadThread.start();
	}
	
	
	
	
	/**
	 * 下载线程(先获取文件信息，再下载)
	 * @author Wise
	 *
	 */
	class DownloadThread extends Thread{
		
		//SOCKET连接需要的类
		Socket socket = null;
		RandomAccessFile raf = null;
		DataInputStream dis = null;
		DataOutputStream dos=null;
		
		public DownloadThread() {
			super();
		}
		
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

			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		public void run(){
			
			
			try {
				socket = new Socket(fileInfo.getIP(),fileInfo.getPort());
				dis = new DataInputStream(socket.getInputStream()); 
				dos=new DataOutputStream(socket.getOutputStream());
				//这里从服务器获取文件名和长度
				fileInfo.setFileName(dis.readUTF());
				fileInfo.setLength(dis.readInt());
				//从数据库中查找是否存在该文件，并且未下完（断点续传）
				if (fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getFileName())) {
					fileInfo = fileInfoDAO.getFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getFileName());
				}
				//告诉服务器文件开始传输的位置 
				dos.writeInt(fileInfo.getStart());
				dos.flush(); 
				//设置文件写入位置
				int start = fileInfo.getStart();
				File file = new File(DownloadService.DOWNLOAD_PATH, fileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				//文件断点续传和暂停回复的写入位置
				raf.seek(start);
				//插入数据库
				if (!fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getId())) {
					fileInfoDAO.insertFileInfo(fileInfo);
				}
				//设置buff
				int bufferSize = 1024; 
				byte[] buffer = new byte[bufferSize]; 
				int len = -1;
				long oldProgressBar = 0;
				long progressBar = 0;
				while ((len = dis.read(buffer)) != -1) {
					//若有数据可以读取，写入文件
					try {
						aesUtil.decrypt(buffer);
					} catch (Exception e) {
						e.printStackTrace();
					}
					raf.write(buffer,0,len);
					start += len;
					//long型防止数据溢出
					progressBar = (long)start * 100 / (long)fileInfo.getLength();
					//更新文件进度和传输位置
					fileInfo.setFinished((int)progressBar);
					fileInfo.setStart(start);
					//把下载进度发送广播给Activity
					if (oldProgressBar != progressBar) {
						oldProgressBar = progressBar;
						Intent intent = new Intent(DownloadService.ACTION_UPDATE);
						intent.putExtra("fileInfo", fileInfo);
						System.out.println(fileInfo.getFileName()+"已下载"+progressBar+"%");
						context.sendBroadcast(intent);
					}
					//在点击暂停时，保存下载进度到数据库
					if (isPause) {
						fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getId(), fileInfo.getFinished(), fileInfo.getStart(),fileInfo.getLength(),fileInfo.getFileName(),"停止下载");
						System.out.println("DownloadTask isPause:" + fileInfo);
						//如果是短暂停，则陷入无线循环
						while(isPause){
						}
						//恢复下载，更新数据库
						fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getId(), fileInfo.getFinished(), fileInfo.getStart(),fileInfo.getLength(),fileInfo.getFileName(),"继续下载");
					}
				}
				//下载完成,更新数据库
				fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getId(), 100, fileInfo.getStart(), fileInfo.getLength(), fileInfo.getFileName(),"已完成");
				System.out.println("DownloadTask finished:"+fileInfo);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					socket.close();
					dis.close();
					raf.close();
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
