package com.wechat.filetransfer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wechat.filetransfer.bean.FileInfo;

public class FileInfoDAOImpl implements FileInfoDAO {

	private DBHelper dbHelper = null;
	
	
	public FileInfoDAOImpl(Context context){
		dbHelper = new DBHelper(context);
	}
	
	@Override
	public boolean isExists(String IP, int Port, int fileInfoID) {
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from file_info where ip = ? and port = ? and file_id = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{IP,Port+"",fileInfoID+""});
		boolean exists = cursor.moveToNext();
		cursor.close();
		db.close();
		return exists;
	}
	
	@Override
	public boolean isExists(String IP, int Port, String fileName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from file_info where ip = ? and port = ? and file_name = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{IP,Port+"",fileName});
		boolean exists = cursor.moveToNext();
		cursor.close();
		db.close();
		return exists;
	}
	
	
	
	
	

	@Override
	public void deleteFileInfo(String IP, int Port, int fileInfoID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "delete from file_info where ip = ? and port = ? and file_id = ? ";
	    db.execSQL(sql,new Object[]{IP,Port,fileInfoID});
	    db.close();
	}

	@Override
	public void updateFileInfo(String IP, int Port, int fileInfoID, int finished,int start,int length,String fileName,String status) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "update file_info set finished = ? , start = ? , length = ? , file_name = ? , status = ? where ip =? and port = ? and file_id = ?";
		db.execSQL(sql, new Object[]{finished,start,length,fileName,status,IP,Port,fileInfoID});
		db.close();
	}
	
	@Override
	public void updateFileInfo(String IP, int Port, String fileName,String status) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "update file_info set status = ? where ip =? and port = ? and file_name = ?";
		db.execSQL(sql, new Object[]{status,IP,Port,fileName});
		db.close();
	}
	

	@Override
	public void insertFileInfo(FileInfo fileInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("insert into file_info(file_id,ip,port,start,length,finished,file_name,status) values(?,?,?,?,?,?,?,?)",
				new Object[]{fileInfo.getId(),fileInfo.getIP(),fileInfo.getPort(),
				fileInfo.getStart(),fileInfo.getLength(),fileInfo.getFinished(),fileInfo.getFileName(),fileInfo.getStatus()});
		db.close();	
	}


	@Override
	public List<FileInfo> getAllFileInfos() {
		List<FileInfo> list = new ArrayList<FileInfo>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from file_info ";
		Cursor cursor = db.rawQuery(sql,null);
		while(cursor.moveToNext()){
			FileInfo file = new FileInfo();
			file.setId(cursor.getInt(cursor.getColumnIndex("file_id")));
			file.setIP(cursor.getString(cursor.getColumnIndex("ip")));
			file.setPort(cursor.getInt(cursor.getColumnIndex("port")));
			file.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			file.setLength(cursor.getInt(cursor.getColumnIndex("length")));
			file.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			file.setFileName(cursor.getString(cursor.getColumnIndex("file_name")));
			file.setStatus(cursor.getString(cursor.getColumnIndex("status")));
			list.add(file);
		}
		db.close();
		cursor.close();
		return list;
	}

	@Override
	public FileInfo getFileInfo(String IP, int Port, String fileName) {
		FileInfo file = new FileInfo();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from file_info where ip =? and port = ? and file_name = ?";
		Cursor cursor = db.rawQuery(sql,new String[]{IP,Port+"",fileName});
		while(cursor.moveToNext()){
			file.setId(cursor.getInt(cursor.getColumnIndex("file_id")));
			file.setIP(cursor.getString(cursor.getColumnIndex("ip")));
			file.setPort(cursor.getInt(cursor.getColumnIndex("port")));
			file.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			file.setLength(cursor.getInt(cursor.getColumnIndex("length")));
			file.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			file.setFileName(cursor.getString(cursor.getColumnIndex("file_name")));
			file.setStatus(cursor.getString(cursor.getColumnIndex("status")));
		}
		db.close();
		cursor.close();
		return file;
	}
	
	@Override
	public void deleteAllFileInfo() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "delete  from file_info";
	    db.execSQL(sql);
	    db.close();		
	}


	

	

	


}
