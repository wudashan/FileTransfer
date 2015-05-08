package com.wechat.filetransfer.bean;

import java.io.Serializable;


/**
 * 文件实体类
 * @author Wise
 *
 */
public class FileInfo implements Serializable{
	private int id;
	private String IP;
	private int Port;
	private int finished;
	private int start;
	private int length;
	private String fileName;
	private String status;

	
	
	public FileInfo() {
	}
	

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", IP=" + IP + ", Port=" + Port
				+ ", finished=" + finished + ", start=" + start + ", length="
				+ length + ", fileName=" + fileName + ", status=" + status
				+ "]";
	}



	public FileInfo(String iP, int port) {
		super();
		IP = iP;
		Port = port;
		status = "开始下载";
	}


	
	
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public String getIP() {
		return IP;
	}




	public void setIP(String iP) {
		IP = iP;
	}




	public int getPort() {
		return Port;
	}




	public void setPort(int port) {
		Port = port;
	}




	public int getFinished() {
		return finished;
	}




	public void setFinished(int finished) {
		this.finished = finished;
	}




	public int getStart() {
		return start;
	}




	public void setStart(int start) {
		this.start = start;
	}




	public int getLength() {
		return length;
	}




	public void setLength(int length) {
		this.length = length;
	}




	public String getFileName() {
		return fileName;
	}




	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	
	
	
	
	
	
}
