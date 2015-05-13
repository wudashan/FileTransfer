package com.scut.filetransfer.util;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	/**
	 * 根据给定的路径，取得path下所有文件 及文件夹 
	 * @param path
	 * @return
	 */
	public static List<File> getFileList(String path){
		List<File> fileList = new ArrayList<File>();
		File[] files = new File(path).listFiles();
		if(files.length > 0){
			List<File> allFolder = new ArrayList<File>();
			List<File> allFile = new ArrayList<File>();
			for(File file : files){
				if(file.isFile()){
					allFile.add(file);
				}else {
					allFolder.add(file);
				}
			}
			fileList.addAll(allFolder);
			fileList.addAll(allFile);
		}
		
		return fileList;
	}
}