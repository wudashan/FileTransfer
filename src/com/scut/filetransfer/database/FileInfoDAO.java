package com.scut.filetransfer.database;

import java.util.List;

import com.scut.filetransfer.bean.FileInfo;

public interface FileInfoDAO {
	
		public boolean isExists(String IP,int Port,int fileInfoID);
		public boolean isExists(String IP,int Port,String fileName);
		public void insertFileInfo(FileInfo fileInfo);
		public void deleteFileInfo(String IP,int Port,int fileInfoID);
		public void deleteAllFileInfo();
		public void updateFileInfo(String IP,int Port,int fileInfoID,int finished,int start,int length,String fileName,String status);
		public void updateFileInfo(String IP,int Port,String fileName,String status);
		public List<FileInfo> getAllFileInfos();
		public FileInfo getFileInfo(String IP,int Port,String fileName);
		
}