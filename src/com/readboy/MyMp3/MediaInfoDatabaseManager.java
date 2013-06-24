package com.readboy.MyMp3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MediaInfoDatabaseManager {
	
	private SQLiteDatabase db = null; //数据库
	
	public MediaInfoDatabaseManager(Context context) {
		String databaseFilePath = context.getFilesDir()+"/MediaInfo.db";
		db = SQLiteDatabase.openOrCreateDatabase(databaseFilePath, null); //创建数据库文件
		//创建表MediaInfo.db
//		db.execSQL("CREATE TABLE IF NOT EXISTS MediaInfo(id TEXT PRIMARY KEY, data TEXT, title TEXT, mineType TEXT, album TEXT, artist TEXT, duration TEXT, state TEXT)");
		createTable();
	}
	
	//创建表
	public void createTable() {
		//创建表MediaInfo.db
		db.execSQL("CREATE TABLE IF NOT EXISTS MediaInfo(id TEXT PRIMARY KEY, data TEXT, title TEXT, mineType TEXT, album TEXT, artist TEXT, duration TEXT, state TEXT)");
	}
	
	//插入一条信息,成功返回0,失败返回(-1)
	public int insertInfo(String id, String data, String title, String mineType, String album, String artist, String duration, String state) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("id", id);
		contentValues.put("data", data);
		contentValues.put("title", title);
		contentValues.put("mineType", mineType);
		contentValues.put("album", album);
		contentValues.put("artist", artist);
		contentValues.put("duration", duration);
		contentValues.put("state", state);
		long rowId = db.insert("MediaInfo", "MediaInfo", contentValues);
		if(rowId == (-1)) { //插入失败,创建表再重试
			System.out.println("mymp3 MediaInfoDatabaseManager create database table MediaInfo!");
//			db.execSQL("CREATE TABLE IF NOT EXISTS MediaInfo(id TEXT PRIMARY KEY, data TEXT, title TEXT, mineType TEXT, album TEXT, artist TEXT, duration TEXT, state TEXT)");
			createTable();
			rowId = db.insert("MediaInfo", "MediaInfo", contentValues);
			if(rowId == (-1)) {
				return (-1);
			}
		}
		return 0;
	}
	
	//删除一条信息,成功返回0,失败返回(-1)
	public int deleteInfo(String id) {
		if(1 != db.delete("MediaInfo", "id = "+id, null)) {
			return (-1);
		}
		return 0;
	}
	
	//信息查询
	public Cursor queryInfo() {
		return db.rawQuery("SELECT * FROM MediaInfo", null);
	}
	
	//清除信息
	public void clearInfo() {
		db.execSQL("DROP TABLE IF EXISTS MediaInfo");
	}
	
	//关闭数据库
	public void closeDatabase() {
		db.close();
		db = null;
	}
}
