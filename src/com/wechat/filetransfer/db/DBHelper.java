package com.wechat.filetransfer.db;
/**
 * Êý¾Ý¿â°ïÖúÀà
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "download.db";
	private static final int VERSION = 1;
	private static final String SQL_CREATE = "create table file_info(_id integer primary key autoincrement,"
			+ "file_id integer,ip text,port integer,start integer,length integer,finished integer,file_name text,status text)";
	private static final String SQL_DROP = "drop table if exists file_info";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL(SQL_DROP);
		db.execSQL(SQL_CREATE);
	}

}
