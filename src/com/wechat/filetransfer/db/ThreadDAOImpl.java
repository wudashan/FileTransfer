package com.wechat.filetransfer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wechat.filetransfer.bean.ThreadInfo;

public class ThreadDAOImpl implements ThreadDAO{
	
	private DBHelper dbHelper = null;
	
	public ThreadDAOImpl(Context context) {
		dbHelper = new DBHelper(context);
	}
	

	@Override
	public boolean isExists(String IP, int Port, int thread_id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from thread_info where ip =? and port = ? and thread_id = ?",
				new String[]{IP,Port+"",thread_id+""});
		boolean exists = cursor.moveToNext();
		db.close();
		cursor.close();
		return exists;
	}

	@Override
	public void insertThread(ThreadInfo threadInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("insert into thread_info(thread_id,ip,port,start,end,finished) values(?,?,?,?,?,?)",
				new Object[]{threadInfo.getId(),threadInfo.getIP(),threadInfo.getPort(),threadInfo.getStart(),threadInfo.getEnd(),threadInfo.getFinished()});
		db.close();
	}

	@Override
	public void deleteThread(String IP, int Port, int thread_id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("delete from thread_info where ip =? and port = ? and thread_id = ? ",
				new Object[]{IP,Port,thread_id});
		db.close();
	}

	@Override
	public void updateThread(String IP, int Port, int thread_id,int finished) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("update  thread_info set finished = ? where ip =? and port = ? and thread_id = ? ",
				new Object[]{finished,IP,Port,thread_id});
		db.close();
	}

	@Override
	public List<ThreadInfo> getThreads(String IP, int Port) {
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from thread_info where ip =? and port = ? ",
				new String[]{IP,Port+""});
		while (cursor.moveToNext()) {
			ThreadInfo thread = new ThreadInfo();
			thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			thread.setIP(cursor.getString(cursor.getColumnIndex("ip")));
			thread.setPort(cursor.getInt(cursor.getColumnIndex("port")));
			thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			list.add(thread);
		}
		db.close();
		cursor.close();
		return list;
	}

}
