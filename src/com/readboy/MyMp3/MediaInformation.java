/*
 * 功能:Android中歌曲信息读取
 */
package com.readboy.MyMp3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.readboy.MyMp3.DynamicLoader.OnDynamicLoaderListener;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.MediaStore;

public class MediaInformation implements OnDynamicLoaderListener {
	private Context context = null;
	private String calledFilePath = null; //外部调用文件全路径
	private List<MediaInfo> mediaInfoList = null; //列表
	
	public static final int DYNAMICLOADER_READY = 0; //准备
	public static final int DYNAMICLOADER_DOING = 1; //进行中
	public static final int DYNAMICLOADER_DONE = 2; //结束
	private int dynLdDone = DYNAMICLOADER_READY; //动态加载标志,0表示未开始,1表示进行中,2表示结束
	
	private OnDynamicCalledListener mDynCld = null; //动态加载过程中显示处理
	
	public MediaInformation(Context context) { //构造函数
		this.context = context;
		mediaInfoList = new ArrayList<MediaInfo>(); //创建列表
	}
	
	//动态加载处理显示接口
	public interface OnDynamicCalledListener {
		void dynamicShow();
	}
	
	//设置侦听
	public void setOnDynamicCalledListener(OnDynamicCalledListener l) {
		mDynCld = l;
	}
	
	public void setCalledFilePath(String calledFilePathTmp) {
		calledFilePath = calledFilePathTmp;
	}
	
	//搜索目标文件夹中的文件处理
	@SuppressWarnings("unused")
	private Searcher searchDeal() {
		Searcher srch = null;
		
		if(calledFilePath != null && !calledFilePath.isEmpty()) { //文件管理调用
			System.out.println("mymp3 calledFilePath = "+calledFilePath);
			int index = calledFilePath.lastIndexOf("/"); //得到最后/位置
			if(index > 0) {
				String searchPath = calledFilePath.substring(0, index); //获取文件所在目录
				calledFilePath = null;
				System.out.println("mymp3 searchPath = "+searchPath);
				srch = new Searcher(searchPath, ".mp3", false); //初始化搜索对象
				return srch;
			}
		}
		
		try { //获取SDCard路径
	    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	    		File file = Environment.getExternalStorageDirectory();
	    		String sdCardDir = file.getCanonicalPath()+"/Music"; //路径处理
	    		System.out.println("mymp3 sdCard Path is "+sdCardDir);
	    		srch = new Searcher(sdCardDir, ".mp3", false); //初始化搜索对象
	    	} else {
	    		System.out.println("mymp3 sdCard mounter error!");
	    	}
	    } catch(Throwable e) { //异常
	    	System.out.println("mymp3 Open sdCard Path Error!");
	    	e.printStackTrace();
	    }
		
		return srch;
	}
	
	//动态加载处理
	public void onDynLdDeal() {
		dynLdDone = DYNAMICLOADER_DOING; //动态加载进行中
		MediaInfoDatabaseManager mediaInfoDb = new MediaInfoDatabaseManager(context); //创建数据库
		mediaInfoList.clear(); //清空列表
		mediaInfoDb.clearInfo(); //清空数据库
		mediaInfoDb.createTable(); //创建表
		
		//文件管理调用
		if (calledFilePath != null && !calledFilePath.isEmpty()) {
			MediaInfo mediaInfo = new MediaInfo();
			mediaInfo.id = "20121221";
			mediaInfo.data = calledFilePath;
			calledFilePath = null;
			String lower = mediaInfo.data.toLowerCase();
			if(lower.endsWith(".mp3") == true) {
				MediaMetadataRetriever mmrTmp = new MediaMetadataRetriever();
				mediaInfo.title = mediaInfo.data.substring(mediaInfo.data.lastIndexOf("/")+1, mediaInfo.data.lastIndexOf("."));
				try {
					mmrTmp.setDataSource(mediaInfo.data); //资源解析
					mediaInfo.mineType = mmrTmp.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
					mediaInfo.album = mmrTmp.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
					mediaInfo.artist = mmrTmp.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
					mediaInfo.duration = mmrTmp.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
					mediaInfo.state = "validity"; //有效
					mediaInfoList.add(mediaInfo);
					mediaInfoDb.insertInfo(mediaInfo.id, mediaInfo.data, mediaInfo.title, mediaInfo.mineType, mediaInfo.album, mediaInfo.artist, mediaInfo.duration, mediaInfo.state);
					if(mDynCld != null) {
						mDynCld.dynamicShow();
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} else {
			//检索所有音频文件(读表)
			Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				if(cursor.moveToFirst() == true) { //读取每一项
					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
					do {
						if(dynLdDone != DYNAMICLOADER_DOING) { //不在进行动态加载则退出
							break;
						}
						MediaInfo mediaInfo = new MediaInfo();
						mediaInfo.id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
						mediaInfo.data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
						String lower = mediaInfo.data.toLowerCase();
						if(lower.endsWith(".mp3") == false) {
							continue;
						}
						mediaInfo.title = mediaInfo.data.substring(mediaInfo.data.lastIndexOf("/")+1, mediaInfo.data.lastIndexOf("."));
						try {
							mmr.setDataSource(mediaInfo.data); //资源解析
						} catch (Throwable e) {
							continue;
						}
						mediaInfo.mineType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
						mediaInfo.album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
						mediaInfo.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
						mediaInfo.duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		/*
						mediaInfo.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
						mediaInfo.mineType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
						mediaInfo.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
						mediaInfo.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
						mediaInfo.duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
		*/
						mediaInfo.state = "validity"; //有效
						mediaInfoList.add(mediaInfo);
						mediaInfoDb.insertInfo(mediaInfo.id, mediaInfo.data, mediaInfo.title, mediaInfo.mineType, mediaInfo.album, mediaInfo.artist, mediaInfo.duration, mediaInfo.state);
						if(mDynCld != null) {
							mDynCld.dynamicShow();
						}
					}while(cursor.moveToNext() == true);
				}
				cursor.close(); //关闭游标
			}
		}
		//提示无歌曲
		if (mediaInfoList.size() < 1 && mDynCld != null) {
			mDynCld.dynamicShow();
		}
		
		mediaInfoDb.closeDatabase(); //关闭数据库
		
		dynLdDone = DYNAMICLOADER_DONE; //动态加载完成
	}
	
	//从系统信息中读取media信息
	public int init() {	
		if(dynLdDone != DYNAMICLOADER_DOING) { //动态加载进行中
			DynamicLoader dynLdr = new DynamicLoader(); //创建动态加载对象
			dynLdr.setOnDynamicLoadListener(this);
		}
		return 0;
	}
/*	
	//从系统信息中读取media信息
	public int init() {	
		MediaInfoDatabaseManager mediaInfoDb = new MediaInfoDatabaseManager(context); //创建数据库
		
		mediaInfoList.clear(); //清空列表
		mediaInfoDb.clearInfo(); //清空数据库
		mediaInfoDb.createTable(); //创建表

		//检索所有音频文件(读表)
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null); 
		if(cursor.moveToFirst() == true) { //读取每一项
			do {
				MediaInfo mediaInfo = new MediaInfo();
				mediaInfo.id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
				mediaInfo.data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				mediaInfo.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
				mediaInfo.mineType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
				mediaInfo.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
				mediaInfo.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
				mediaInfo.duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
				mediaInfo.state = "validity"; //有效
				mediaInfoList.add(mediaInfo);
				mediaInfoDb.insertInfo(mediaInfo.id, mediaInfo.data, mediaInfo.title, mediaInfo.mineType, mediaInfo.album, mediaInfo.artist, mediaInfo.duration, mediaInfo.state);
			}while(cursor.moveToNext() == true);
		}
		cursor.close(); //关闭游标
		
		Searcher srch = searchDeal(); //搜索mp3
		if(srch != null && srch.init() == 0) { //搜索成功
			int totalNum = srch.getTotalNum();
			if(totalNum > 0) { //有歌曲
				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
				for(int i = 0; i < totalNum; i ++) {
					MediaInfo mediaInfo = new MediaInfo();
					mediaInfo.id = String.valueOf(i);
					mediaInfo.data = srch.getPathByIndex(i);
					System.out.println("mymp3 "+i+" data = "+mediaInfo.data);
					mediaInfo.title = mediaInfo.data.substring(mediaInfo.data.lastIndexOf("/")+1, mediaInfo.data.lastIndexOf("."));
					System.out.println("mymp3 "+i+" title = "+mediaInfo.title);
					mmr.setDataSource(mediaInfo.data); //资源解析
					mediaInfo.mineType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
					System.out.println("mymp3 "+i+" mineType = "+mediaInfo.mineType);
					mediaInfo.album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
					System.out.println("mymp3 "+i+" album = "+mediaInfo.album);
					mediaInfo.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
					System.out.println("mymp3 "+i+" artist = "+mediaInfo.artist);
					mediaInfo.duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
					System.out.println("mymp3 "+i+" duration = "+mediaInfo.duration);
					mediaInfo.state = "validity"; //有效
					mediaInfoList.add(mediaInfo);
					mediaInfoDb.insertInfo(mediaInfo.id, mediaInfo.data, mediaInfo.title, mediaInfo.mineType, mediaInfo.album, mediaInfo.artist, mediaInfo.duration, mediaInfo.state);
				}
			}
		}
		
		mediaInfoDb.closeDatabase(); //关闭数据库

		return 0;
	}
*/	
	
	//从数据库中读取media信息,返回0表示正常,1表示无内容
	public int getInfoFromDatabase() {	
		MediaInfoDatabaseManager mediaInfoDb = new MediaInfoDatabaseManager(context); //创建数据库
		
		mediaInfoList.clear(); //清空列表
		
		Cursor cursorTmp = mediaInfoDb.queryInfo(); //查询数据库
		if(cursorTmp.getCount() == 0) { //无内容
			cursorTmp.close(); //关闭游标
			mediaInfoDb.closeDatabase(); //关闭数据库
			return 1;
		}
		
		//从数据库中读取media信息
		for(cursorTmp.moveToFirst(); !cursorTmp.isAfterLast(); cursorTmp.moveToNext()) {
			MediaInfo mediaInfo = new MediaInfo();
			mediaInfo.id = cursorTmp.getString(0);
			mediaInfo.data = cursorTmp.getString(1);
			mediaInfo.title = cursorTmp.getString(2);
			mediaInfo.mineType = cursorTmp.getString(3);
			mediaInfo.album = cursorTmp.getString(4);
			mediaInfo.artist = cursorTmp.getString(5);
			mediaInfo.duration = cursorTmp.getString(6);
			mediaInfo.state = cursorTmp.getString(7);
			mediaInfoList.add(mediaInfo);
		}
		cursorTmp.close(); //关闭游标
		
		mediaInfoDb.closeDatabase(); //关闭数据库
		
		return 0;
	}
	
	//判断是否搜索中
	public int getDynLdState() {
		return dynLdDone;
	}
	
	//获取歌曲总数
	public int getTotalNum() {
		return mediaInfoList.size();
	}
	
	//获取歌曲全路径
	public String getFullPath(int position) {
		if (mediaInfoList.get(position) == null) {
			return "";
		}
		return mediaInfoList.get(position).data;
	}
	
	//获取歌曲名
	public String getTitle(int position) {
		if (mediaInfoList.get(position) == null) {
			return "";
		}
		return mediaInfoList.get(position).title;
	}
	
	//获取歌曲类型
	public String getType(int position) {
		if (mediaInfoList.get(position) == null) {
			return "";
		}
		return mediaInfoList.get(position).mineType;
	}
	
	//获取歌曲所在专辑
	public String getAlbum(int position) {
		if (mediaInfoList.get(position) == null) {
			return "";
		}
		return mediaInfoList.get(position).album;
	}
	
	//获取歌手名
	public String getArtist(int position) {
		if (mediaInfoList.get(position) == null) {
			return "";
		}
		return mediaInfoList.get(position).artist;
	}
	
	//获取歌曲总时间
	public String getDuration(int position) {
		if (mediaInfoList.get(position) == null) {
			return "";
		}
		return mediaInfoList.get(position).duration;
	}
	
	//获取状态,从而判断歌曲有效与否
	public String getState(int position) {
		if (mediaInfoList.get(position) == null) {
			return "";
		}
		return mediaInfoList.get(position).state;
	}
	
	//设置状态,从而改变歌曲的有效状态
	public void setState(int position, boolean isValidity) {
		if(position >= mediaInfoList.size()) {
			return ;
		}
		if(isValidity) {
			mediaInfoList.get(position).state = "validity";
		} else {
			mediaInfoList.get(position).state = "invalidity";
		}
	}
	
	//删除歌曲信息
	public void deleteMediaInfo(int position) {
		MediaInfoDatabaseManager mediaInfoDb = new MediaInfoDatabaseManager(context); //创建数据库
		mediaInfoDb.deleteInfo(mediaInfoList.get(position).id);
		mediaInfoDb.closeDatabase(); //关闭数据库
		mediaInfoList.remove(position);
		return ;
	}
	
	//保存歌曲列表
	public void saveMediaInfo() {
//		MediaInfoDatabaseManager mediaInfoDb = new MediaInfoDatabaseManager(context); //创建数据库
//		
//		mediaInfoDb.clearInfo(); //清除信息
//		mediaInfoDb.createTable(); //创建表
//		
//		int N = mediaInfoList.size();
//		for(int i = 0; i < N; i ++) { //逐条保存信息
//			MediaInfo mediaInfo = mediaInfoList.get(i);
//			mediaInfoDb.insertInfo(mediaInfo.id, mediaInfo.data, mediaInfo.title, mediaInfo.mineType, mediaInfo.album, mediaInfo.artist, mediaInfo.duration, mediaInfo.state);
//		}
//		
//		mediaInfoDb.closeDatabase(); //关闭数据库
	}
	
	//歌曲信息内部类
	class MediaInfo {
		String id = null; //独一无二标识ID
		String data = null; //全路径
		String title = null; //歌名
		String mineType = null; //类型
		String album = null; //专辑
		String artist = null; //艺术家		
		String duration = null; //总时间
		String state = null; //状态
	}
}
