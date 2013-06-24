package com.readboy.MyMp3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MediaInfoDatabaseManager {
	
	private SQLiteDatabase db = null; //���ݿ�
	
	public MediaInfoDatabaseManager(Context context) {
		String databaseFilePath = context.getFilesDir()+"/MediaInfo.db";
		db = SQLiteDatabase.openOrCreateDatabase(databaseFilePath, null); //�������ݿ��ļ�
		//������MediaInfo.db
//		db.execSQL("CREATE TABLE IF NOT EXISTS MediaInfo(id TEXT PRIMARY KEY, data TEXT, title TEXT, mineType TEXT, album TEXT, artist TEXT, duration TEXT, state TEXT)");
		createTable();
	}
	
	//������
	public void createTable() {
		//������MediaInfo.db
		db.execSQL("CREATE TABLE IF NOT EXISTS MediaInfo(id TEXT PRIMARY KEY, data TEXT, title TEXT, mineType TEXT, album TEXT, artist TEXT, duration TEXT, state TEXT)");
	}
	
	//����һ����Ϣ,�ɹ�����0,ʧ�ܷ���(-1)
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
		if(rowId == (-1)) { //����ʧ��,������������
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
	
	//ɾ��һ����Ϣ,�ɹ�����0,ʧ�ܷ���(-1)
	public int deleteInfo(String id) {
		if(1 != db.delete("MediaInfo", "id = "+id, null)) {
			return (-1);
		}
		return 0;
	}
	
	//��Ϣ��ѯ
	public Cursor queryInfo() {
		return db.rawQuery("SELECT * FROM MediaInfo", null);
	}
	
	//�����Ϣ
	public void clearInfo() {
		db.execSQL("DROP TABLE IF EXISTS MediaInfo");
	}
	
	//�ر����ݿ�
	public void closeDatabase() {
		db.close();
		db = null;
	}
}
