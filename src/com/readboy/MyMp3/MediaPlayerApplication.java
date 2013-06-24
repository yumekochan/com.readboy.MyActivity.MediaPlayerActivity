/*
 * 全局变量
 */
package com.readboy.MyMp3;

import java.util.Random;
import java.util.Set;
import com.readboy.MyMp3.MediaInformation.OnDynamicCalledListener;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MediaPlayerApplication extends Application implements OnDynamicCalledListener, Runnable{
	public static final String ACTION_UPDATE_FOR_WIDGET = "com.readboy.MyMp3.ACTION_APPWIDGET_UPDATE";
	public static final String ACTION_PREVIOUS = "com.readboy.MyMp3.ACTION_PREVIOUS";
	public static final String ACTION_PLAY = "com.readboy.MyMp3.ACTION_PLAY";
	public static final String ACTION_PAUSE = "com.readboy.MyMp3.ACTION_PAUSE";
	public static final String ACTION_NEXT = "com.readboy.MyMp3.ACTION_NEXT";
	public static final String ACTION_TIMER = "com.readboy.MyMp3.ACTION_TIMER";
	public static final String ACTION_PLAYCOMPLETION = "com.readboy.MyMp3.ACTION_PLAYCOMPLETION";
	public static final String ACTION_DYNAMICLOADSHOW = "com.readboy.MyMp3.ACTION_DYNAMICLOADSHOW";
	public static final String ACTION_DELETEDEAL = "com.readboy.MyMp3.ACTION_DELETEDEAL";
	public static final String ACTION_REFRESHDEAL = "com.readboy.MyMp3.ACTION_REFRESHDEAL";
	public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
	public static final String ACTION_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
	public static final String ACTION_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED";
	public static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";
	
	private boolean playInfoChange = false; //播放曲目状态模式改变
	private boolean songListChange = false; //播放列表改变

	public static final int MEDIAPLAYER_PLAYSTATE_STOP = 0; //停止状态
	public static final int MEDIAPLAYER_PLAYSTATE_PLAY = 1; //播放状态
	public static final int MEDIAPLAYER_PLAYSTATE_PAUSE = 2; //暂停状态
	private int playState = MEDIAPLAYER_PLAYSTATE_STOP; //初始状态
	
	private SharedPreferences sp = null; //SharedPreferences对象,用于少量数据保存
	private Editor editor = null; //Editor对象,存储数据时用到
	
	public static final int MEDIAPLAYER_PLAYORDER_SERIATE = 0; //顺序
	public static final int MEDIAPLAYER_PLAYORDER_CIRCULAR = 1; //循环
	public static final int MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR = 2; //单曲循环
	public static final int MEDIAPLAYER_PLAYORDER_RANDOM = 3; //随机
	private int playOrder = MEDIAPLAYER_PLAYORDER_SERIATE; //初始顺序为顺序播放
	
	public static final int MEDIAPLAYER_PLAYMODE_STANDARD = 0; //标准模式
	public static final int MEDIAPLAYER_PLAYMODE_CLASSICAL = 1; //经典模式
	public static final int MEDIAPLAYER_PLAYMODE_JAZZ = 2; //爵士模式
	public static final int MEDIAPLAYER_PLAYMODE_POP = 3; //流行模式
	public static final int MEDIAPLAYER_PLAYMODE_LIVE = 4; //现场模式
	public static final int MEDIAPLAYER_PLAYMODE_ROCK = 5; //摇滚模式
	private int playMode = MEDIAPLAYER_PLAYMODE_STANDARD; //初始模式为标准模式
	
	private int currentSongIndex = 0; //当前播放歌曲index
	private MediaInformation mediaInfo = null; //MediaInfomation对象
	
	private MediaPlayer mediaPlayer = null; //MediaPlayer对象
	private LyricInformation lrcInfo = null; //歌词信息
	private AudioManager audioManager = null; //AudioManager对象
	private EqualizerControler ec = null; //音效模式控制器
	
//	private Timer timer = null; //Timer
//	private Timer timerForUpdate = null; //专为更新widget而使用的Timer
	
	private int prepareCount = 0; //调用MediaPlayer的prepare次数
	
	@Override
	public void onCreate() {
		System.out.println("mymp3 MediaPlayerApplication onCreate() called! begin");
		
		super.onCreate();
		
		//获取各存储信息
		sp = getSharedPreferences("MediaInfo", MODE_PRIVATE); //获取SharedPreferences对象
		playOrder = sp.getInt("MediaInfo_PlayOrder", MEDIAPLAYER_PLAYORDER_SERIATE); //获取播放顺序类型
		playMode = sp.getInt("MediaInfo_PlayMode", MEDIAPLAYER_PLAYMODE_STANDARD); //获取播放模式
		currentSongIndex = sp.getInt("MediaInfo_CurrentSongIndex", 0); //获取当前播放歌曲index
		
		//创建媒体信息对象并初始化媒体信息
		mediaInfo = new MediaInformation(getApplicationContext()); //创建媒体信息对象
		mediaInfo.setOnDynamicCalledListener(this);
		mediaInfo.getInfoFromDatabase(); //读取数据库信息
		int totalNum = mediaInfo.getTotalNum(); //获取总歌曲数
		if(totalNum <= 0 || (currentSongIndex < 0 || currentSongIndex >= totalNum)) { //无音频或无效index
			currentSongIndex = 0; //设置当前播放曲目为0
		}
		
		//媒体播放对象
		mediaPlayer = new MediaPlayer(); //创建MediaPlayer对象
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() { //播放完成侦听
			public void onCompletion(MediaPlayer mp) {
				System.out.println("mymp3 onCompletion called! 1 prepareCount = "+prepareCount);
				if(prepareCount > 0) {
					prepareCount --;
					playByOrder(true, false); //下一首
					sendCompletionBroadcast(); //发送播放完成的广播
				}
				System.out.println("mymp3 onCompletion called! 2 prepareCount = "+prepareCount);
			}
    	});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				System.out.println("mymp3 onErrorListener called! prepareCount = 0");
				prepareCount = 0;
				mediaPlayer.reset();
				stopButtonDeal(); //停止处理
				return true;
			}
		});
		//获取AudioManager对象
		audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
		//获取均衡控制器
		ec = new EqualizerControler(getApplicationContext(), mediaPlayer);
		//设置均衡器
		ec.setEqualizer(playMode);
/*		
		//创建Timer
		timer = new Timer(); 
		timer.schedule(new TimerTask() {
			public void run() {
				Intent intent = new Intent(ACTION_TIMER); //创建Intent对象并设置Intent的action属性
				sendBroadcast(intent); //发送广播
			}
		}, 0, 100);
		
		//为widget的update创建Timer
		timerForUpdate = new Timer(); 
		timerForUpdate.schedule(new TimerTask() {
			public void run() {
				Intent intent = new Intent(ACTION_UPDATE_FOR_WIDGET); //创建Intent对象并设置Intent的action属性
				sendBroadcast(intent); //发送广播
			}
		}, 0, 1000);
*/		
		//Timer处理改成线程处理
		Thread thread = new Thread(this);
		thread.start();
		
		System.out.println("mymp3 MediaPlayerApplication onCreate() called! end");
	}
	
	@Override
	public void onTerminate() {
		System.out.println("mymp3 MediaPlayerApplication onTerminate() called! begin");
		
		super.onTerminate();
		
		if(ec != null) {
			ec.closeEqualizer();
			ec = null;
		}
		
		System.out.println("mymp3 MediaPlayerApplication onTerminate() called! end");
	}
	
	public void run() {
		while (true) {
			Intent intent1 = new Intent(ACTION_TIMER); //创建Intent对象并设置Intent的action属性
			sendBroadcast(intent1); //发送广播
			Intent intent2 = new Intent(ACTION_UPDATE_FOR_WIDGET); //创建Intent对象并设置Intent的action属性
			sendBroadcast(intent2); //发送广播
			try {
				Thread.sleep(500);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	//发送播放完成的广播
	private void sendCompletionBroadcast() {
		Intent intent = new Intent(ACTION_PLAYCOMPLETION); //创建Intent对象并设置Intent的action属性
		sendBroadcast(intent); //发送广播
	}
	
	//信息保存,退出时被调用
	public void saveInfo() {
		System.out.println("mymp3 MediaPlayerApplication saveInfo() called! begin");
		
		//标志改变
		if(playInfoChange == true) {
			editor = sp.edit(); //获取Editor对象
			editor.putInt("MediaInfo_PlayOrder", playOrder); //保存播放顺序
			editor.putInt("MediaInfo_PlayMode", playMode); //保存播放模式
			editor.putInt("MediaInfo_CurrentSongIndex", currentSongIndex); //保存当前播放歌曲index
			editor.commit(); //数据更新
			playInfoChange = false;
		}
		
		//标志改变
		if(songListChange) {
			mediaInfo.saveMediaInfo(); //保存歌曲列表
			songListChange = false; //初始化标志
		}
		
		System.out.println("mymp3 MediaPlayerApplication saveInfo() called! end");
	}
	
	//获取播放状态,此接口必须首先被调用,些接口包含初始化功能
	public int getPlayState() {
		return playState;
	}
	
	//获取播放顺序类型
	public int getPlayOrder() {
		return playOrder;
	}
	
	//设置播放顺序类型
	public void setPlayOrder(int newPlayOrder) {
		switch(newPlayOrder) {
		case MEDIAPLAYER_PLAYORDER_SERIATE: //顺序
		case MEDIAPLAYER_PLAYORDER_CIRCULAR: //循环
		case MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR: //单曲循环
		case MEDIAPLAYER_PLAYORDER_RANDOM: //随机
			if(playOrder != newPlayOrder) { //设置新顺序类型
				playOrder = newPlayOrder;
				playInfoChange = true; //标志改变
			}
			break;
		}
	}
	
	//获取播放顺序类型
	public int getPlayMode() {
		return playMode;
	}
	
	//设置播放模式
	public void setPlayMode(int newPlayMode) {
		switch(newPlayMode) {
		case MEDIAPLAYER_PLAYMODE_STANDARD: //标准模式
		case MEDIAPLAYER_PLAYMODE_CLASSICAL: //经典模式
		case MEDIAPLAYER_PLAYMODE_JAZZ: //爵士模式
		case MEDIAPLAYER_PLAYMODE_POP: //流行模式
		case MEDIAPLAYER_PLAYMODE_LIVE: //现场模式
		case MEDIAPLAYER_PLAYMODE_ROCK: //摇滚模式
			if(playMode != newPlayMode) { //设置新播放模式
				playMode = newPlayMode;
				setEqualizer(playMode);
				playInfoChange = true; //标志改变
			}
			break;
		}
	}
	
	//设置音效
	private void setEqualizer(int playMode) {
		ec.setEqualizer(playMode);
	}
	
	//获取当前播放歌曲index
	public int getCurrentSongIndex() {
		return currentSongIndex;
	}
	
	//设置当前播放歌曲index
	public void setCurrentSongIndex(int newCurrentSongIndex) {
		if(newCurrentSongIndex < 0 || newCurrentSongIndex >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication setCurrentSongIndex() failed! newCurrentSongIndex = "+newCurrentSongIndex);
			return ;
		}
		currentSongIndex = newCurrentSongIndex; //改变当前曲目
		if((-1) == playMusic()) { //播放当前歌曲出错
			playState = MEDIAPLAYER_PLAYSTATE_STOP; //改变播放状态
		} else {
			playState = MEDIAPLAYER_PLAYSTATE_PLAY; //改变播放状态
		}
		sendCompletionBroadcast(); //发送播放完成的广播
		playInfoChange = true; //标志改变
	}
	
	//设置外部调用路径
	public void setCalledFilePath(String calledFilePathTmp) {
		mediaInfo.setCalledFilePath(calledFilePathTmp);
	}
	
	//动态加载显示处理
	public void dynamicShow() {
		Intent intent = new Intent(ACTION_DYNAMICLOADSHOW); //创建Intent对象并设置Intent的action属性
		sendBroadcast(intent); //发送广播
	}
	
	//初始化媒体信息
	public void refreshMediaInfo(boolean isRefresh) {
		if(isRefresh) { //刷新
			currentSongIndex = 0; //当前播放曲目标志清零
			Intent intent = new Intent(ACTION_REFRESHDEAL); //创建Intent对象并设置Intent的action属性
			sendBroadcast(intent); //发送广播
			mediaInfo.init(); //初始化
			if(mediaPlayer.isPlaying()) {
				mediaPlayer.stop(); //停止放音
			}
			playState = MEDIAPLAYER_PLAYSTATE_STOP; //设置为停止状态
			//标志改变
			songListChange = true;
		}
	}
	
	//获取歌曲总数
	public int getTotalNum() {
		return mediaInfo.getTotalNum();
	}
	
	//获取当前播放歌曲的全路径
	public String getFullPathById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return null; //无歌曲返回空
		}
		if(isCurrentSong) {
			return mediaInfo.getFullPath(currentSongIndex);
		} else if(position < 0 || position >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication getFullPathById() failed! position = "+position);
			return null;
		} else {
			return mediaInfo.getFullPath(position);
		}
	}
	
	//获取歌曲名
	public String getTitleById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return null; //无歌曲返回空
		}
		if(isCurrentSong) {
			return mediaInfo.getTitle(currentSongIndex);
		} else if(position < 0 || position >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication getTitleById() failed! position = "+position);
			return null;
		} else {
			return mediaInfo.getTitle(position);
		}
	}
	
	//获取当前播放歌曲的类型
	public String getTypeById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //无歌曲返回空
		}
		if(isCurrentSong) {
			return mediaInfo.getType(currentSongIndex);
		} else if(position < 0 || position >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication getTypeById() failed! position = "+position);
			return "";
		} else {
			return mediaInfo.getType(position);
		}
	}
	
	//获取当前播放歌曲的专辑
	public String getAlbumById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //无歌曲返回空
		}
		if(isCurrentSong) {
			return mediaInfo.getAlbum(currentSongIndex);
		} else if(position < 0 || position >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication getAlbumById() failed! position = "+position);
			return "";
		} else {
			return mediaInfo.getAlbum(position);
		}
	}
	
	//获取当前播放歌曲的歌手名
	public String getArtistById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //无歌曲返回空
		}
		if(isCurrentSong) {
			return mediaInfo.getArtist(currentSongIndex);
		} else if(position < 0 || position >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication getArtistById() failed! position = "+position);
			return "";
		} else {
			return mediaInfo.getArtist(position);
		}
	}
	
	//获取歌曲总时长
	public String getDurationById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //无歌曲返回空
		}
		if(isCurrentSong) {
			return mediaInfo.getDuration(currentSongIndex);
		} else if(position < 0 || position >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication getDurationById() failed! position = "+position);
			return "";
		} else {
			return mediaInfo.getDuration(position);
		}
	}
	
	//获取歌曲状态
	public String getStateById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //无歌曲返回空
		}
		if(isCurrentSong) {
			return mediaInfo.getState(currentSongIndex);
		} else if(position < 0 || position >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication getDurationById() failed! position = "+position);
			return "";
		} else {
			return mediaInfo.getState(position);
		}
	}
	
	//判断是否动态加载中
	public boolean isDynamicLoading() {
		if(mediaInfo.getDynLdState() == MediaInformation.DYNAMICLOADER_DOING) {
			return true; //动态加载中
		}
		return false; //非动态加载中
	}
	
	//删除歌曲,正常返回0,无歌曲返回1,失败返回(-1)
	public int deleteSongByIndex(int index) {
		if(mediaInfo.getTotalNum() < 1) {
			return 1; //无歌曲返回空
		}
		
		if(isDynamicLoading() == true) {
			return (-1); //动态加载中,不执行删除操作
		}

		if(index == currentSongIndex) { //与当前歌曲相同id
			if(playState != MEDIAPLAYER_PLAYSTATE_STOP) {
				if(mediaPlayer.isPlaying()) {
					mediaPlayer.stop(); //停止放音
				}
				playState = MEDIAPLAYER_PLAYSTATE_STOP; //改变播放状态
			}
		}
		mediaInfo.deleteMediaInfo(index); //删除歌曲信息
		if(currentSongIndex > index) {
			currentSongIndex --;
		}
		if(currentSongIndex >= (mediaInfo.getTotalNum()-1)) { //处理当前曲目
			currentSongIndex = mediaInfo.getTotalNum()-1;
			if(currentSongIndex < 0) {
				currentSongIndex = 0;
			}
		}
		
		//标志改变
		songListChange = true;
		
		//发送删除消息
		Intent intent = new Intent(ACTION_DELETEDEAL);
		sendBroadcast(intent);
		
		return 0;
	}
	
	//播放当前歌曲,播放出错返回(-1),正常返回0
	private int playMusic() { 
		if((-1) == prepareMediaPlayer()) {
			return (-1);
		}
		mediaPlayer.start();
		return 0;
	}

	//准备当前歌曲资源,正常返回0,失败返回(-1)
	private int prepareMediaPlayer() {
		if(mediaInfo.getTotalNum() < 1) {
			return (-1); //无歌曲返回失败
		}
		try {
			lyricParase(mediaInfo.getFullPath(currentSongIndex)); //解析歌词
			System.out.println("mymp3 prepareMediaPlayer 1 prepareCount = "+prepareCount);
			prepareCount = 1;
			System.out.println("mymp3 prepareMediaPlayer 2 prepareCount = "+prepareCount);
			mediaPlayer.reset();
			mediaPlayer.setDataSource(mediaInfo.getFullPath(currentSongIndex));
			mediaPlayer.prepare();
			mediaInfo.setState(currentSongIndex, true); //歌曲存在
		} catch (Throwable e) {
			System.out.println("mymp3 MediaPlayerApplication prepareMediaPlayer() failed! setState(false)");
			e.printStackTrace();
			mediaInfo.setState(currentSongIndex, false); //歌曲不存在
			System.out.println("mymp3 prepareMediaPlayer 3 prepareCount = "+prepareCount);
			prepareCount --;
			System.out.println("mymp3 prepareMediaPlayer 4 prepareCount = "+prepareCount);
			return (-1);
		}
		return 0;
	}
	
	//获取当前播放位置
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	//获取当前播放位置
	public int getMediaPlayerCurrentPosition() {
		return mediaPlayer.getCurrentPosition();
	}
	
	//改变播放位置
	public void seekTo(int position) {
		mediaPlayer.seekTo(position);
		if(playState != MEDIAPLAYER_PLAYSTATE_PLAY) {
			mediaPlayer.start();
			System.out.println("mymp3 seekTo 1 prepareCount = "+prepareCount);
			prepareCount ++;
			System.out.println("mymp3 seekTo 2 prepareCount = "+prepareCount);
			playState = MEDIAPLAYER_PLAYSTATE_PLAY;
		}
	}
	
	//歌词解析,成功返回0,失败返回(-1)
	private int lyricParase(String mediaPath) {
		String lrcPath = mediaPath.substring(0, mediaPath.lastIndexOf("."))+".lrc";
		LyricParser lp = new LyricParser();
		try {
			lrcInfo = lp.parseLrcInfoByPath(lrcPath);
		} catch (Throwable e) {
			//System.out.println("mymp3 MediaPlayerApplication lyricParase() lyric parse failed!");
			//e.printStackTrace();
			lrcInfo = null;
			return (-1);
		}
		return 0;
	}
	
	//获取歌词size,失败返回(-1),成功返回实际size
	public int getLyricSize() {
		if(lrcInfo == null) {
			//System.out.println("mymp3 MediaPlayerApplication getLyricSize() failed! lrcInfo = "+lrcInfo);
			return (-1);
		}
		return lrcInfo.getInfos().size();
	}
	
	//获取歌词关键字集合
	public Set<Long> getLyricKeySet() {
		if(lrcInfo == null) {
			//System.out.println("mymp3 MediaPlayerApplication getLyricSize() failed! lrcInfo = "+lrcInfo);
			return null;
		}
		return lrcInfo.getInfos().keySet();
	}
	
	//获取歌词title
	public String getLyricTitle() {
		if(lrcInfo == null) {
			//System.out.println("mymp3 MediaPlayerApplication getLyricTitle() failed! lrcInfo = "+lrcInfo);
			return null;
		}
		return lrcInfo.getTitle();
	}
	
	//根据关键字获取歌词
	public String getLyricContentByKey(long key) {
		if(lrcInfo == null) {
			//System.out.println("mymp3 MediaPlayerApplication getLyricContentByKey() failed! lrcInfo = "+lrcInfo);
			return null;
		}
		return lrcInfo.getInfos().get(key);
	}
	
	//获取最大音量
	public int getMaxValume() {
		return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	//获取音量
	public int getValume() {
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	
	//设置音量
	public boolean setValume(int index) {
		if(index < 0 || index > audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
			return false;
		}
		audioManager.setRingerMode(2);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, AudioManager.FLAG_PLAY_SOUND);
		return true;
	}
	
	//设置静音
	public void setMute(boolean state) {
		audioManager.setRingerMode(2);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, state);
		
		
	}
	
	//播放按钮处理
	public void playButtonDeal() {
		if(mediaInfo.getTotalNum() < 1) { //无歌曲
			return ; //不处理
		}
		
		int playTmp = 0; //播放标志
		//根据播放状态处理操作
		switch(playState) { 
		case MEDIAPLAYER_PLAYSTATE_STOP:	
			playTmp = playMusic(); //播放当前歌曲
			break;
		case MEDIAPLAYER_PLAYSTATE_PAUSE:
			if(mediaPlayer.isPlaying() == false) {
				mediaPlayer.start(); //继续播放
				System.out.println("mymp3 playButtonDeal 1 prepareCount = "+prepareCount);
				prepareCount ++;
				System.out.println("mymp3 playButtonDeal 2 prepareCount = "+prepareCount);
			}
			break;
		case MEDIAPLAYER_PLAYSTATE_PLAY:
			break;
		}
		if(playTmp != (-1)) {
			playState = MEDIAPLAYER_PLAYSTATE_PLAY; //改变播放状态
		}
		sendCompletionBroadcast(); //发送播放完成的广播
	}
	
	//暂停按钮处理
	public void pauseButtonDeal() {
		if(mediaInfo.getTotalNum() < 1) { //无歌曲
			return ; //不处理
		}
		
		//根据播放状态处理操作
		switch(playState) {
		case MEDIAPLAYER_PLAYSTATE_STOP:
		case MEDIAPLAYER_PLAYSTATE_PAUSE:
			break;
		case MEDIAPLAYER_PLAYSTATE_PLAY:
			if(mediaPlayer.isPlaying() == true) {
				mediaPlayer.pause(); //暂停
				System.out.println("mymp3 pauseButtonDeal 1 prepareCount = "+prepareCount);
				prepareCount --;
				System.out.println("mymp3 pauseButtonDeal 2 prepareCount = "+prepareCount);
			}
			playState = MEDIAPLAYER_PLAYSTATE_PAUSE; //改变播放状态
			break;
		}
	}
	
	//上一曲按钮处理
	public void previousButtonDeal() {
		playByOrder(false, true);
	}
	
	//下一曲按钮处理
	public void nextButtonDeal() {
		playByOrder(true, true);
	}
	
	//根据顺序标志播放上下首
	private void playByOrder(boolean isNext, boolean fromUser) {
		int totalNum = mediaInfo.getTotalNum(); //获取总歌曲数
		if(totalNum <= 0) { //无歌曲,直接返回
			return ;
		}
		
		//当前处于未播放状态,则播放当前首
		if(playState == MEDIAPLAYER_PLAYSTATE_STOP) {
			if(playMusic() != (-1)) {
				playState = MEDIAPLAYER_PLAYSTATE_PLAY; //改变播放状态
				sendCompletionBroadcast(); //发送播放完成的广播
				return ;
			}
		}
		
		boolean isStop = false;
		
		switch(playOrder) { //根据顺序标志处理
		case MEDIAPLAYER_PLAYORDER_SERIATE:
			if(isNext == true) {
				if(currentSongIndex < (totalNum-1)) { //不是最后一首
					currentSongIndex ++;
				} else {
					currentSongIndex = totalNum -1;
					if(!fromUser) {
						isStop = true;
					}
				}
			} else if(currentSongIndex > 0) { //不是第一首
				currentSongIndex --;
			}
			break;
		case MEDIAPLAYER_PLAYORDER_CIRCULAR:
			if(isNext == true) {
				if(currentSongIndex >= (totalNum-1)) { //最后一首
					currentSongIndex = 0;
				} else {
					currentSongIndex ++;
				}
			} else if(currentSongIndex <= 0) { //最后一首
				currentSongIndex = totalNum-1;
			} else {
				currentSongIndex --;
			}
			break;
		case MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR:
			if(fromUser) {
				if(isNext == true) {
					if(currentSongIndex < (totalNum-1)) { //不是最后一首
						currentSongIndex ++;
					} else {
						currentSongIndex = totalNum -1;
					}
				} else if(currentSongIndex > 0) { //不是第一首
					currentSongIndex --;
				}
			}
			break;
		case MEDIAPLAYER_PLAYORDER_RANDOM:
			Random random = new Random();
			currentSongIndex = random.nextInt()%totalNum;
			if(currentSongIndex < 0) {
				currentSongIndex += totalNum;
			}
			break;
		}
		
		if(isStop == true || (-1) == playMusic()) { //播放当前歌曲出错
			if(mediaPlayer.isPlaying()) { //正在播放则停止
				mediaPlayer.stop();
			}
			playState = MEDIAPLAYER_PLAYSTATE_STOP; //改变播放状态
		} else {
			playState = MEDIAPLAYER_PLAYSTATE_PLAY; //改变播放状态
		}
		
		sendCompletionBroadcast(); //发送播放完成的广播
		
		playInfoChange = true; //标志改变
		
		return ;
	}
	
	//停止放音
	public void stopButtonDeal() {
		if(mediaPlayer.isPlaying()) { //正在播放则停止
			mediaPlayer.stop();
			System.out.println("mymp3 stopButtonDeal 1 prepareCount = "+prepareCount);
			prepareCount --;
			System.out.println("mymp3 stopButtonDeal 2 prepareCount = "+prepareCount);
		}
		playState = MEDIAPLAYER_PLAYSTATE_STOP; //改变播放状态
		sendCompletionBroadcast(); //发送播放完成的广播
	}
}
