/*
 * ȫ�ֱ���
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
	
	private boolean playInfoChange = false; //������Ŀ״̬ģʽ�ı�
	private boolean songListChange = false; //�����б�ı�

	public static final int MEDIAPLAYER_PLAYSTATE_STOP = 0; //ֹͣ״̬
	public static final int MEDIAPLAYER_PLAYSTATE_PLAY = 1; //����״̬
	public static final int MEDIAPLAYER_PLAYSTATE_PAUSE = 2; //��ͣ״̬
	private int playState = MEDIAPLAYER_PLAYSTATE_STOP; //��ʼ״̬
	
	private SharedPreferences sp = null; //SharedPreferences����,�����������ݱ���
	private Editor editor = null; //Editor����,�洢����ʱ�õ�
	
	public static final int MEDIAPLAYER_PLAYORDER_SERIATE = 0; //˳��
	public static final int MEDIAPLAYER_PLAYORDER_CIRCULAR = 1; //ѭ��
	public static final int MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR = 2; //����ѭ��
	public static final int MEDIAPLAYER_PLAYORDER_RANDOM = 3; //���
	private int playOrder = MEDIAPLAYER_PLAYORDER_SERIATE; //��ʼ˳��Ϊ˳�򲥷�
	
	public static final int MEDIAPLAYER_PLAYMODE_STANDARD = 0; //��׼ģʽ
	public static final int MEDIAPLAYER_PLAYMODE_CLASSICAL = 1; //����ģʽ
	public static final int MEDIAPLAYER_PLAYMODE_JAZZ = 2; //��ʿģʽ
	public static final int MEDIAPLAYER_PLAYMODE_POP = 3; //����ģʽ
	public static final int MEDIAPLAYER_PLAYMODE_LIVE = 4; //�ֳ�ģʽ
	public static final int MEDIAPLAYER_PLAYMODE_ROCK = 5; //ҡ��ģʽ
	private int playMode = MEDIAPLAYER_PLAYMODE_STANDARD; //��ʼģʽΪ��׼ģʽ
	
	private int currentSongIndex = 0; //��ǰ���Ÿ���index
	private MediaInformation mediaInfo = null; //MediaInfomation����
	
	private MediaPlayer mediaPlayer = null; //MediaPlayer����
	private LyricInformation lrcInfo = null; //�����Ϣ
	private AudioManager audioManager = null; //AudioManager����
	private EqualizerControler ec = null; //��Чģʽ������
	
//	private Timer timer = null; //Timer
//	private Timer timerForUpdate = null; //רΪ����widget��ʹ�õ�Timer
	
	private int prepareCount = 0; //����MediaPlayer��prepare����
	
	@Override
	public void onCreate() {
		System.out.println("mymp3 MediaPlayerApplication onCreate() called! begin");
		
		super.onCreate();
		
		//��ȡ���洢��Ϣ
		sp = getSharedPreferences("MediaInfo", MODE_PRIVATE); //��ȡSharedPreferences����
		playOrder = sp.getInt("MediaInfo_PlayOrder", MEDIAPLAYER_PLAYORDER_SERIATE); //��ȡ����˳������
		playMode = sp.getInt("MediaInfo_PlayMode", MEDIAPLAYER_PLAYMODE_STANDARD); //��ȡ����ģʽ
		currentSongIndex = sp.getInt("MediaInfo_CurrentSongIndex", 0); //��ȡ��ǰ���Ÿ���index
		
		//����ý����Ϣ���󲢳�ʼ��ý����Ϣ
		mediaInfo = new MediaInformation(getApplicationContext()); //����ý����Ϣ����
		mediaInfo.setOnDynamicCalledListener(this);
		mediaInfo.getInfoFromDatabase(); //��ȡ���ݿ���Ϣ
		int totalNum = mediaInfo.getTotalNum(); //��ȡ�ܸ�����
		if(totalNum <= 0 || (currentSongIndex < 0 || currentSongIndex >= totalNum)) { //����Ƶ����Чindex
			currentSongIndex = 0; //���õ�ǰ������ĿΪ0
		}
		
		//ý�岥�Ŷ���
		mediaPlayer = new MediaPlayer(); //����MediaPlayer����
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() { //�����������
			public void onCompletion(MediaPlayer mp) {
				System.out.println("mymp3 onCompletion called! 1 prepareCount = "+prepareCount);
				if(prepareCount > 0) {
					prepareCount --;
					playByOrder(true, false); //��һ��
					sendCompletionBroadcast(); //���Ͳ�����ɵĹ㲥
				}
				System.out.println("mymp3 onCompletion called! 2 prepareCount = "+prepareCount);
			}
    	});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				System.out.println("mymp3 onErrorListener called! prepareCount = 0");
				prepareCount = 0;
				mediaPlayer.reset();
				stopButtonDeal(); //ֹͣ����
				return true;
			}
		});
		//��ȡAudioManager����
		audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
		//��ȡ���������
		ec = new EqualizerControler(getApplicationContext(), mediaPlayer);
		//���þ�����
		ec.setEqualizer(playMode);
/*		
		//����Timer
		timer = new Timer(); 
		timer.schedule(new TimerTask() {
			public void run() {
				Intent intent = new Intent(ACTION_TIMER); //����Intent��������Intent��action����
				sendBroadcast(intent); //���͹㲥
			}
		}, 0, 100);
		
		//Ϊwidget��update����Timer
		timerForUpdate = new Timer(); 
		timerForUpdate.schedule(new TimerTask() {
			public void run() {
				Intent intent = new Intent(ACTION_UPDATE_FOR_WIDGET); //����Intent��������Intent��action����
				sendBroadcast(intent); //���͹㲥
			}
		}, 0, 1000);
*/		
		//Timer����ĳ��̴߳���
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
			Intent intent1 = new Intent(ACTION_TIMER); //����Intent��������Intent��action����
			sendBroadcast(intent1); //���͹㲥
			Intent intent2 = new Intent(ACTION_UPDATE_FOR_WIDGET); //����Intent��������Intent��action����
			sendBroadcast(intent2); //���͹㲥
			try {
				Thread.sleep(500);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	//���Ͳ�����ɵĹ㲥
	private void sendCompletionBroadcast() {
		Intent intent = new Intent(ACTION_PLAYCOMPLETION); //����Intent��������Intent��action����
		sendBroadcast(intent); //���͹㲥
	}
	
	//��Ϣ����,�˳�ʱ������
	public void saveInfo() {
		System.out.println("mymp3 MediaPlayerApplication saveInfo() called! begin");
		
		//��־�ı�
		if(playInfoChange == true) {
			editor = sp.edit(); //��ȡEditor����
			editor.putInt("MediaInfo_PlayOrder", playOrder); //���沥��˳��
			editor.putInt("MediaInfo_PlayMode", playMode); //���沥��ģʽ
			editor.putInt("MediaInfo_CurrentSongIndex", currentSongIndex); //���浱ǰ���Ÿ���index
			editor.commit(); //���ݸ���
			playInfoChange = false;
		}
		
		//��־�ı�
		if(songListChange) {
			mediaInfo.saveMediaInfo(); //��������б�
			songListChange = false; //��ʼ����־
		}
		
		System.out.println("mymp3 MediaPlayerApplication saveInfo() called! end");
	}
	
	//��ȡ����״̬,�˽ӿڱ������ȱ�����,Щ�ӿڰ�����ʼ������
	public int getPlayState() {
		return playState;
	}
	
	//��ȡ����˳������
	public int getPlayOrder() {
		return playOrder;
	}
	
	//���ò���˳������
	public void setPlayOrder(int newPlayOrder) {
		switch(newPlayOrder) {
		case MEDIAPLAYER_PLAYORDER_SERIATE: //˳��
		case MEDIAPLAYER_PLAYORDER_CIRCULAR: //ѭ��
		case MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR: //����ѭ��
		case MEDIAPLAYER_PLAYORDER_RANDOM: //���
			if(playOrder != newPlayOrder) { //������˳������
				playOrder = newPlayOrder;
				playInfoChange = true; //��־�ı�
			}
			break;
		}
	}
	
	//��ȡ����˳������
	public int getPlayMode() {
		return playMode;
	}
	
	//���ò���ģʽ
	public void setPlayMode(int newPlayMode) {
		switch(newPlayMode) {
		case MEDIAPLAYER_PLAYMODE_STANDARD: //��׼ģʽ
		case MEDIAPLAYER_PLAYMODE_CLASSICAL: //����ģʽ
		case MEDIAPLAYER_PLAYMODE_JAZZ: //��ʿģʽ
		case MEDIAPLAYER_PLAYMODE_POP: //����ģʽ
		case MEDIAPLAYER_PLAYMODE_LIVE: //�ֳ�ģʽ
		case MEDIAPLAYER_PLAYMODE_ROCK: //ҡ��ģʽ
			if(playMode != newPlayMode) { //�����²���ģʽ
				playMode = newPlayMode;
				setEqualizer(playMode);
				playInfoChange = true; //��־�ı�
			}
			break;
		}
	}
	
	//������Ч
	private void setEqualizer(int playMode) {
		ec.setEqualizer(playMode);
	}
	
	//��ȡ��ǰ���Ÿ���index
	public int getCurrentSongIndex() {
		return currentSongIndex;
	}
	
	//���õ�ǰ���Ÿ���index
	public void setCurrentSongIndex(int newCurrentSongIndex) {
		if(newCurrentSongIndex < 0 || newCurrentSongIndex >= mediaInfo.getTotalNum()) {
			System.out.println("mymp3 MediaPlayerApplication setCurrentSongIndex() failed! newCurrentSongIndex = "+newCurrentSongIndex);
			return ;
		}
		currentSongIndex = newCurrentSongIndex; //�ı䵱ǰ��Ŀ
		if((-1) == playMusic()) { //���ŵ�ǰ��������
			playState = MEDIAPLAYER_PLAYSTATE_STOP; //�ı䲥��״̬
		} else {
			playState = MEDIAPLAYER_PLAYSTATE_PLAY; //�ı䲥��״̬
		}
		sendCompletionBroadcast(); //���Ͳ�����ɵĹ㲥
		playInfoChange = true; //��־�ı�
	}
	
	//�����ⲿ����·��
	public void setCalledFilePath(String calledFilePathTmp) {
		mediaInfo.setCalledFilePath(calledFilePathTmp);
	}
	
	//��̬������ʾ����
	public void dynamicShow() {
		Intent intent = new Intent(ACTION_DYNAMICLOADSHOW); //����Intent��������Intent��action����
		sendBroadcast(intent); //���͹㲥
	}
	
	//��ʼ��ý����Ϣ
	public void refreshMediaInfo(boolean isRefresh) {
		if(isRefresh) { //ˢ��
			currentSongIndex = 0; //��ǰ������Ŀ��־����
			Intent intent = new Intent(ACTION_REFRESHDEAL); //����Intent��������Intent��action����
			sendBroadcast(intent); //���͹㲥
			mediaInfo.init(); //��ʼ��
			if(mediaPlayer.isPlaying()) {
				mediaPlayer.stop(); //ֹͣ����
			}
			playState = MEDIAPLAYER_PLAYSTATE_STOP; //����Ϊֹͣ״̬
			//��־�ı�
			songListChange = true;
		}
	}
	
	//��ȡ��������
	public int getTotalNum() {
		return mediaInfo.getTotalNum();
	}
	
	//��ȡ��ǰ���Ÿ�����ȫ·��
	public String getFullPathById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return null; //�޸������ؿ�
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
	
	//��ȡ������
	public String getTitleById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return null; //�޸������ؿ�
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
	
	//��ȡ��ǰ���Ÿ���������
	public String getTypeById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //�޸������ؿ�
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
	
	//��ȡ��ǰ���Ÿ�����ר��
	public String getAlbumById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //�޸������ؿ�
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
	
	//��ȡ��ǰ���Ÿ����ĸ�����
	public String getArtistById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //�޸������ؿ�
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
	
	//��ȡ������ʱ��
	public String getDurationById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //�޸������ؿ�
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
	
	//��ȡ����״̬
	public String getStateById(boolean isCurrentSong, int position) {
		if(mediaInfo.getTotalNum() < 1) {
			return ""; //�޸������ؿ�
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
	
	//�ж��Ƿ�̬������
	public boolean isDynamicLoading() {
		if(mediaInfo.getDynLdState() == MediaInformation.DYNAMICLOADER_DOING) {
			return true; //��̬������
		}
		return false; //�Ƕ�̬������
	}
	
	//ɾ������,��������0,�޸�������1,ʧ�ܷ���(-1)
	public int deleteSongByIndex(int index) {
		if(mediaInfo.getTotalNum() < 1) {
			return 1; //�޸������ؿ�
		}
		
		if(isDynamicLoading() == true) {
			return (-1); //��̬������,��ִ��ɾ������
		}

		if(index == currentSongIndex) { //�뵱ǰ������ͬid
			if(playState != MEDIAPLAYER_PLAYSTATE_STOP) {
				if(mediaPlayer.isPlaying()) {
					mediaPlayer.stop(); //ֹͣ����
				}
				playState = MEDIAPLAYER_PLAYSTATE_STOP; //�ı䲥��״̬
			}
		}
		mediaInfo.deleteMediaInfo(index); //ɾ��������Ϣ
		if(currentSongIndex > index) {
			currentSongIndex --;
		}
		if(currentSongIndex >= (mediaInfo.getTotalNum()-1)) { //����ǰ��Ŀ
			currentSongIndex = mediaInfo.getTotalNum()-1;
			if(currentSongIndex < 0) {
				currentSongIndex = 0;
			}
		}
		
		//��־�ı�
		songListChange = true;
		
		//����ɾ����Ϣ
		Intent intent = new Intent(ACTION_DELETEDEAL);
		sendBroadcast(intent);
		
		return 0;
	}
	
	//���ŵ�ǰ����,���ų�����(-1),��������0
	private int playMusic() { 
		if((-1) == prepareMediaPlayer()) {
			return (-1);
		}
		mediaPlayer.start();
		return 0;
	}

	//׼����ǰ������Դ,��������0,ʧ�ܷ���(-1)
	private int prepareMediaPlayer() {
		if(mediaInfo.getTotalNum() < 1) {
			return (-1); //�޸�������ʧ��
		}
		try {
			lyricParase(mediaInfo.getFullPath(currentSongIndex)); //�������
			System.out.println("mymp3 prepareMediaPlayer 1 prepareCount = "+prepareCount);
			prepareCount = 1;
			System.out.println("mymp3 prepareMediaPlayer 2 prepareCount = "+prepareCount);
			mediaPlayer.reset();
			mediaPlayer.setDataSource(mediaInfo.getFullPath(currentSongIndex));
			mediaPlayer.prepare();
			mediaInfo.setState(currentSongIndex, true); //��������
		} catch (Throwable e) {
			System.out.println("mymp3 MediaPlayerApplication prepareMediaPlayer() failed! setState(false)");
			e.printStackTrace();
			mediaInfo.setState(currentSongIndex, false); //����������
			System.out.println("mymp3 prepareMediaPlayer 3 prepareCount = "+prepareCount);
			prepareCount --;
			System.out.println("mymp3 prepareMediaPlayer 4 prepareCount = "+prepareCount);
			return (-1);
		}
		return 0;
	}
	
	//��ȡ��ǰ����λ��
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	//��ȡ��ǰ����λ��
	public int getMediaPlayerCurrentPosition() {
		return mediaPlayer.getCurrentPosition();
	}
	
	//�ı䲥��λ��
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
	
	//��ʽ���,�ɹ�����0,ʧ�ܷ���(-1)
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
	
	//��ȡ���size,ʧ�ܷ���(-1),�ɹ�����ʵ��size
	public int getLyricSize() {
		if(lrcInfo == null) {
			//System.out.println("mymp3 MediaPlayerApplication getLyricSize() failed! lrcInfo = "+lrcInfo);
			return (-1);
		}
		return lrcInfo.getInfos().size();
	}
	
	//��ȡ��ʹؼ��ּ���
	public Set<Long> getLyricKeySet() {
		if(lrcInfo == null) {
			//System.out.println("mymp3 MediaPlayerApplication getLyricSize() failed! lrcInfo = "+lrcInfo);
			return null;
		}
		return lrcInfo.getInfos().keySet();
	}
	
	//��ȡ���title
	public String getLyricTitle() {
		if(lrcInfo == null) {
			//System.out.println("mymp3 MediaPlayerApplication getLyricTitle() failed! lrcInfo = "+lrcInfo);
			return null;
		}
		return lrcInfo.getTitle();
	}
	
	//���ݹؼ��ֻ�ȡ���
	public String getLyricContentByKey(long key) {
		if(lrcInfo == null) {
			//System.out.println("mymp3 MediaPlayerApplication getLyricContentByKey() failed! lrcInfo = "+lrcInfo);
			return null;
		}
		return lrcInfo.getInfos().get(key);
	}
	
	//��ȡ�������
	public int getMaxValume() {
		return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	//��ȡ����
	public int getValume() {
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	
	//��������
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
	
	//���þ���
	public void setMute(boolean state) {
		audioManager.setRingerMode(2);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, state);
		
		
	}
	
	//���Ű�ť����
	public void playButtonDeal() {
		if(mediaInfo.getTotalNum() < 1) { //�޸���
			return ; //������
		}
		
		int playTmp = 0; //���ű�־
		//���ݲ���״̬�������
		switch(playState) { 
		case MEDIAPLAYER_PLAYSTATE_STOP:	
			playTmp = playMusic(); //���ŵ�ǰ����
			break;
		case MEDIAPLAYER_PLAYSTATE_PAUSE:
			if(mediaPlayer.isPlaying() == false) {
				mediaPlayer.start(); //��������
				System.out.println("mymp3 playButtonDeal 1 prepareCount = "+prepareCount);
				prepareCount ++;
				System.out.println("mymp3 playButtonDeal 2 prepareCount = "+prepareCount);
			}
			break;
		case MEDIAPLAYER_PLAYSTATE_PLAY:
			break;
		}
		if(playTmp != (-1)) {
			playState = MEDIAPLAYER_PLAYSTATE_PLAY; //�ı䲥��״̬
		}
		sendCompletionBroadcast(); //���Ͳ�����ɵĹ㲥
	}
	
	//��ͣ��ť����
	public void pauseButtonDeal() {
		if(mediaInfo.getTotalNum() < 1) { //�޸���
			return ; //������
		}
		
		//���ݲ���״̬�������
		switch(playState) {
		case MEDIAPLAYER_PLAYSTATE_STOP:
		case MEDIAPLAYER_PLAYSTATE_PAUSE:
			break;
		case MEDIAPLAYER_PLAYSTATE_PLAY:
			if(mediaPlayer.isPlaying() == true) {
				mediaPlayer.pause(); //��ͣ
				System.out.println("mymp3 pauseButtonDeal 1 prepareCount = "+prepareCount);
				prepareCount --;
				System.out.println("mymp3 pauseButtonDeal 2 prepareCount = "+prepareCount);
			}
			playState = MEDIAPLAYER_PLAYSTATE_PAUSE; //�ı䲥��״̬
			break;
		}
	}
	
	//��һ����ť����
	public void previousButtonDeal() {
		playByOrder(false, true);
	}
	
	//��һ����ť����
	public void nextButtonDeal() {
		playByOrder(true, true);
	}
	
	//����˳���־����������
	private void playByOrder(boolean isNext, boolean fromUser) {
		int totalNum = mediaInfo.getTotalNum(); //��ȡ�ܸ�����
		if(totalNum <= 0) { //�޸���,ֱ�ӷ���
			return ;
		}
		
		//��ǰ����δ����״̬,�򲥷ŵ�ǰ��
		if(playState == MEDIAPLAYER_PLAYSTATE_STOP) {
			if(playMusic() != (-1)) {
				playState = MEDIAPLAYER_PLAYSTATE_PLAY; //�ı䲥��״̬
				sendCompletionBroadcast(); //���Ͳ�����ɵĹ㲥
				return ;
			}
		}
		
		boolean isStop = false;
		
		switch(playOrder) { //����˳���־����
		case MEDIAPLAYER_PLAYORDER_SERIATE:
			if(isNext == true) {
				if(currentSongIndex < (totalNum-1)) { //�������һ��
					currentSongIndex ++;
				} else {
					currentSongIndex = totalNum -1;
					if(!fromUser) {
						isStop = true;
					}
				}
			} else if(currentSongIndex > 0) { //���ǵ�һ��
				currentSongIndex --;
			}
			break;
		case MEDIAPLAYER_PLAYORDER_CIRCULAR:
			if(isNext == true) {
				if(currentSongIndex >= (totalNum-1)) { //���һ��
					currentSongIndex = 0;
				} else {
					currentSongIndex ++;
				}
			} else if(currentSongIndex <= 0) { //���һ��
				currentSongIndex = totalNum-1;
			} else {
				currentSongIndex --;
			}
			break;
		case MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR:
			if(fromUser) {
				if(isNext == true) {
					if(currentSongIndex < (totalNum-1)) { //�������һ��
						currentSongIndex ++;
					} else {
						currentSongIndex = totalNum -1;
					}
				} else if(currentSongIndex > 0) { //���ǵ�һ��
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
		
		if(isStop == true || (-1) == playMusic()) { //���ŵ�ǰ��������
			if(mediaPlayer.isPlaying()) { //���ڲ�����ֹͣ
				mediaPlayer.stop();
			}
			playState = MEDIAPLAYER_PLAYSTATE_STOP; //�ı䲥��״̬
		} else {
			playState = MEDIAPLAYER_PLAYSTATE_PLAY; //�ı䲥��״̬
		}
		
		sendCompletionBroadcast(); //���Ͳ�����ɵĹ㲥
		
		playInfoChange = true; //��־�ı�
		
		return ;
	}
	
	//ֹͣ����
	public void stopButtonDeal() {
		if(mediaPlayer.isPlaying()) { //���ڲ�����ֹͣ
			mediaPlayer.stop();
			System.out.println("mymp3 stopButtonDeal 1 prepareCount = "+prepareCount);
			prepareCount --;
			System.out.println("mymp3 stopButtonDeal 2 prepareCount = "+prepareCount);
		}
		playState = MEDIAPLAYER_PLAYSTATE_STOP; //�ı䲥��״̬
		sendCompletionBroadcast(); //���Ͳ�����ɵĹ㲥
	}
}
