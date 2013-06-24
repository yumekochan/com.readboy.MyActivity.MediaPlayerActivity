package com.readboy.MyWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.readboy.MyMp3.MediaPlayerApplication;
import com.readboy.MyMp3.MediaPlayerReceiver;
import com.readboy.MyMp3.MediaPlayerReceiver.OnReceiverBroadcastListener;
import com.readboy.MyMp3.R;
import com.readboy.rbminimp3.rbWindow;
import com.readboy.rbminimp3.rbWindow.OnDismissListener;
import com.readboy.rbpopupservice.IrbPopupManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class FloatMp3Window implements OnClickListener, OnReceiverBroadcastListener{
	
	private Context context; //Context����
	
	private MediaPlayerApplication mpApp = null; //Application����
	
	private Toast toast = null; //��ʾ��
	
	rbWindow pw = null;
	IrbPopupManager mManager;
	
	private Button volumeSilent = null; //������ť
	private Button volumeEnable = null; //������ť
	private SeekBar volumeSeekBar = null; //����������
	private boolean volumeSeekBarProgressTimerShow = true; //�ֶ�����SeekBarʱ,Timer���ٿ���SeekBar��ʾ
	private int volumeSeekBarMax = 0; //SeekBar���ֵ�ͽ���
	private int volumeSeekBarProgress = 0;
	
	private ListView songList = null; //�����б�
	private List<Map<String, String>> songInfoList = null; //������Ϣ�б�
	private SongAdapter songAdapter = null; //�б�������	
	
	private SeekBar playSeekBar = null; //���Ž��ȿ�����
	private boolean playSeekBarProgressTimerShow = true; //�ֶ�����SeekBarʱ,Timer���ٿ���SeekBar��ʾ
	private int playSeekBarMax = 0; //SeekBar���ֵ�ͽ���
	private int playSeekBarProgress = 0;
	private boolean isPlaySeekBarProgressChange = false; //�ֶ��ı�SeekBar��λ�ú�,����Ҫ���ŵ���Ӧλ��
	
	private TextView currentTime = null; //��ǰʱ���ı�
	private TextView totalTime = null; //��ʱ���ı�
	
	private Button previous = null; //��һ��
	private Button pause = null; //��ͣ
	private Button play = null; //����
	private Button next = null; //��һ��
	
	private MediaPlayerReceiver activityReceiver = null; //�㲥���ն���
	private MediaPlayerReceiver activityReceiverNew = null; //�㲥���ն���
	
	private boolean playButtonShow = true; //��ǰ��ʾ���Ű�ť
	
	private boolean closeBtnClick = false; //�رհ�ťClick��־
	
	private boolean finishFlag = false; //�ػ�������־
	
	//���캯��
	public FloatMp3Window(Context context, IrbPopupManager manager) {
		this.context = context;
		mManager = manager;
		mpApp = (MediaPlayerApplication)context; //Application�����ʼ��
		init(); //��ʼ��
	}
	
	public void setOnDismissListener(OnDismissListener listener){
		if(pw!=null){
			pw.setOnDismissListener(listener);
		}
	}
	
	private void init() {
		System.out.println("mymp3 FloatMp3Window init() called! begin");
		
		//��ȡ����
		LayoutInflater layoutInflaterTitle = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout frameLayoutTitle = (FrameLayout)layoutInflaterTitle.inflate(R.layout.window_titlelayout, null);
		
		//�����ͼ���水ť
		Button entry = (Button)frameLayoutTitle.findViewById(R.id.window_entry);
		entry.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (closeBtnClick == false) {
					closeBtnClick = true;
					System.out.println("mymp3 FloatMp3Window init() entry button click!");
					//�����ͼ����Activity
					Intent intent = new Intent(context, com.readboy.MyActivity.MediaPlayerActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					//�˳����׽��洰��
					closeWindowDeal();
				}
			}
		});
		
		//ˢ�°�ť
		Button refresh = (Button)frameLayoutTitle.findViewById(R.id.window_refresh);
		refresh.setOnClickListener(this);
		//ɾ����ť
		Button delete = (Button)frameLayoutTitle.findViewById(R.id.window_delete);
		delete.setOnClickListener(this);
		//�رհ�ť
		Button close = (Button)frameLayoutTitle.findViewById(R.id.window_close);
		close.setOnClickListener(this);

		//��ȡ����
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout frameLayout = (FrameLayout)layoutInflater.inflate(R.layout.window_floatwindow, null);
		
		//������ť
		volumeSilent = (Button)frameLayout.findViewById(R.id.window_volumeSilent);
		volumeSilent.setOnClickListener(this);
		//������ť
		volumeEnable = (Button)frameLayout.findViewById(R.id.window_volumeEnable);
		volumeEnable.setOnClickListener(this);
		//����������
		volumeSeekBar = (SeekBar)frameLayout.findViewById(R.id.window_volumnSeekBar);
		//SeekBarλ�øı�����
		
		volumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { //���黬��ʱ����
				if(fromUser) { //�û�����
					if(mpApp.setValume(progress)) {
						volumeSeekBarProgress = progress; //��¼��ǰ����
						volumeSilentDeal(); //��������
					}
				}
			}
			public void onStartTrackingTouch(SeekBar seekBar) { //���鿪ʼ����ʱ
				volumeSeekBarProgressTimerShow = false;
			}
			public void onStopTrackingTouch(SeekBar seekBar) { //����ֹͣ����ʱ
				volumeSeekBarProgressTimerShow = true;
			}
    	});
		
		//�����б�
		songList = (ListView)frameLayout.findViewById(R.id.window_songList); 
		//������Ϣ�б�
    	songInfoList = new ArrayList<Map<String, String>>();
		//�б�������
    	songAdapter = new SongAdapter(context, songInfoList, R.layout.window_listviewitem, new String[]{"songName", "songArtist", "songTimer"}, new int[]{R.id.window_songName, R.id.window_songArtist, R.id.window_songTimer});
    	//�б����������
    	songList.setAdapter(songAdapter);
    	songList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				songListClickDeal(parent, view, position, id); //�������
			}
    	});
    	//��ʼ���б�
    	int songListInit = songListInit(false); 
    	
    	//���Ž��ȿ�����
		playSeekBar = (SeekBar)frameLayout.findViewById(R.id.window_playSeekBar);
		//SeekBarλ�øı�����
		playSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { //���黬��ʱ����
				progressChangedDeal(seekBar, progress, fromUser);
			}
			public void onStartTrackingTouch(SeekBar seekBar) { //���鿪ʼ����ʱ
				startTrackDeal(seekBar);
			}
			public void onStopTrackingTouch(SeekBar seekBar) { //����ֹͣ����ʱ
				stopTrackDeal(seekBar);
			}
    	});
		
		//��ǰʱ���ı�
		currentTime = (TextView)frameLayout.findViewById(R.id.window_currentTime);
		//��ʱ���ı�
		totalTime = (TextView)frameLayout.findViewById(R.id.window_totalTime); 
		
		//��һ��
		previous = (Button)frameLayout.findViewById(R.id.window_previous);
		previous.setOnClickListener(this);
		//��ͣ
		pause = (Button)frameLayout.findViewById(R.id.window_pause);
		pause.setOnClickListener(this); 
		//����
		play = (Button)frameLayout.findViewById(R.id.window_play);
		play.setOnClickListener(this);
		//��һ��
		next = (Button)frameLayout.findViewById(R.id.window_next);
		next.setOnClickListener(this);
		
		//�����Զ���㲥������MediaPlayerActivityReceiver
    	activityReceiver = new MediaPlayerReceiver();
    	activityReceiver.setOnReceiverBroadcastListener(this);
    	//����������IntentFilter
    	IntentFilter intentFilter = new IntentFilter();
    	//ָ��������Action
    	intentFilter.addAction(MediaPlayerApplication.ACTION_TIMER);
    	intentFilter.addAction(MediaPlayerApplication.ACTION_PLAYCOMPLETION);
    	intentFilter.addAction(MediaPlayerApplication.ACTION_DYNAMICLOADSHOW);
    	intentFilter.addAction(MediaPlayerApplication.ACTION_DELETEDEAL);
    	intentFilter.addAction(MediaPlayerApplication.ACTION_REFRESHDEAL);
    	//ע��BroadcastReceiver
    	context.registerReceiver(activityReceiver, intentFilter);
    	
    	//�����Զ���㲥������MediaPlayerActivityReceiver
    	activityReceiverNew = new MediaPlayerReceiver();
    	activityReceiverNew.setOnReceiverBroadcastListener(this);
    	//����������IntentFilter
    	IntentFilter intentFilterNew = new IntentFilter();
    	//ָ��������Action
    	intentFilterNew.addAction(MediaPlayerApplication.ACTION_BATTERY_LOW);
    	intentFilterNew.addAction(MediaPlayerApplication.ACTION_MEDIA_REMOVED);
    	intentFilterNew.addAction(MediaPlayerApplication.ACTION_MEDIA_EJECT);
    	intentFilterNew.addDataScheme("file");
    	//ע��BroadcastReceiver
    	context.registerReceiver(activityReceiverNew, intentFilterNew);
    	
    	//��ʾ��Ϣ
    	if(songListInit == 0) {
    		showMediaInfo(true);
    	} else {
    		showMediaInfo(false);
    	}
    	
    	//������ʾ
    	volumeSeekBarMax = mpApp.getMaxValume(); //��ȡ�������
    	volumeSeekBarProgress = mpApp.getValume(); //��ȡ��ǰ����
    	volumeSeekBar.setMax(volumeSeekBarMax); //�����������
    	volumeSeekBar.setProgress(volumeSeekBarProgress); //���õ�ǰ����
		
		//������������
//		floatWnd = new FloatWindow<FrameLayout>(frameLayout, 458, 549);
    	pw = new rbWindow(context, mManager, "rbminimp3", 
    			frameLayout, frameLayoutTitle, 430, 529);
    	
    	System.out.println("mymp3 FloatMp3Window init() called! end");
	}

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.window_refresh: //ˢ��
			mpApp.stopButtonDeal(); //ֹͣ����
			songListInit(true); //�����б��ʼ��
        	showMediaInfo(true); //ˢ�¸���Ϣ
			break;
		case R.id.window_delete: //ɾ��
			deleteButtonDeal();
			break;
		case R.id.window_close: //�ر�
			if (closeBtnClick == false) {
				closeBtnClick = true;
				closeWindowDeal();
			}
			break;
		case R.id.window_volumeSilent: //����
			mpApp.setMute(false);
			break;
		case R.id.window_volumeEnable: //����
			mpApp.setMute(true);
			break;
		case R.id.window_previous: //��һ��
			previousButtonDeal();
			break;
		case R.id.window_pause: //��ͣ
			pauseButtonDeal();
			break;
		case R.id.window_play: //����
			playButtonDeal();
			break;
		case R.id.window_next: //��һ��
			nextButtonDeal();
			break;
		}
	}

	public void onReceiverDeal(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(MediaPlayerApplication.ACTION_PLAYCOMPLETION)) {
			playCompletionDeal(); //���Ž�������
		} else if(action.equals(MediaPlayerApplication.ACTION_DYNAMICLOADSHOW)) {
			dynmicLoadShow(true); //��̬������ʾ
		} else if(action.equals(MediaPlayerApplication.ACTION_DELETEDEAL)) { //ɾ������
			songAdapter.setSelectPosition((-1)); //������ѡ�и���
			songListInit(false); //�����б��ʼ��
        	showMediaInfo(true); //ˢ�¸���Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_REFRESHDEAL)) { //ɾ������
			songAdapter.setSelectPosition((-1)); //������ѡ�и���
			songAdapter.setPlayPosition(-1); //���ò��޷Ÿ���
			songInfoList.clear(); //����б�
			showMediaInfo(true); //ˢ�¸���Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_TIMER)) {
			timerDeal(); //ʱ�䴦��
			if(volumeSeekBarProgressTimerShow) { //ʱ�����������ʾ
				if(volumeSeekBarProgress != mpApp.getValume()) {
					volumeSeekBarProgress = mpApp.getValume(); //��¼�����ı�
					volumeSeekBar.setProgress(volumeSeekBarProgress); //��ʾ�ı�
				}
			}
			volumeButtonShow(); //��ʾ������ť
		} else if(action.equals(MediaPlayerApplication.ACTION_SHUTDOWN)) {
			mpApp.stopButtonDeal(); //ֹͣ����
			mpApp.saveInfo(); //������Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_MEDIA_REMOVED) ||
			(action.equals(MediaPlayerApplication.ACTION_MEDIA_EJECT))) {
			System.out.println("mymp3 card removed! finishFlag = "+finishFlag);
			String mPath = mpApp.getFullPathById(true, 0); 
			if (mPath != null && (mPath.startsWith("/mnt/extsd/") || mPath.startsWith("mnt/extsd/") ||
					mPath.startsWith("mnt/sdcard-ext/") || mPath.startsWith("/mnt/sdcard-ext/"))) {
				if (!finishFlag) {
					finishFlag = true;
					mpApp.stopButtonDeal(); //ֹͣ����
					mpApp.saveInfo(); //������Ϣ
					//�˳����׽��洰��
					closeWindowDeal();
				}
			}
		} else if(action.equals(MediaPlayerApplication.ACTION_BATTERY_LOW)) {
			System.out.println("mymp3 battery low! finishFlag = "+finishFlag);
			if(!finishFlag) {
				finishFlag = true;
				mpApp.stopButtonDeal(); //ֹͣ����
				mpApp.saveInfo(); //������Ϣ
				//�˳����׽��洰��
				closeWindowDeal();
			}
		}
	}
	
	//��������
	private void volumeSilentDeal() {
		if(volumeSeekBarProgress > 0) {
			mpApp.setMute(false);
		} else {
			mpApp.setMute(true);
		}
	}
	
	//������ť��ʾ
	private void volumeButtonShow() {
		if(volumeSeekBarProgress <= 0) { //����
			volumeEnable.setVisibility(Button.GONE);
			volumeSilent.setVisibility(Button.VISIBLE);
		} else { //��������
			volumeEnable.setVisibility(Button.VISIBLE);
			volumeSilent.setVisibility(Button.GONE);
		}
	}
	
	//ʱ��ת��,����ת���ɷ���
	private String timerTransform(String millisecond) {
		if(millisecond == null || millisecond.isEmpty()) {
			return "";
		}
		String timer = null;
		int hour = 0, minute = 0, second = 0;
		//stringתint
		int millisecondTmp = Integer.parseInt(millisecond); 
		
		//�������
		while(millisecondTmp >= 60000) {
			millisecondTmp -= 60000;
			minute ++;
			if(minute >= 60) {
				minute -= 60;
				hour ++;
			}
		}
		//��������
		while(millisecondTmp >= 1000) {
			millisecondTmp -= 1000;
			second ++;
		}
		//��������,��������
		if(millisecondTmp >= 500) {
			//second ++; //ȫ�᲻����
		}
		
		//intתstring
		if(hour > 0) {
			timer = hour+":";
			if(minute < 10) {
				timer = timer+"0"+minute+":";
			} else {
				timer = timer+minute+":";
			}
		} else {
			timer = minute+":";
		}
		if(second < 10) {
			timer = timer+"0"+second;
		} else {
			timer = timer+second;
		}
		
		return timer;
	}
		
	//�����б��ʼ��,����1��ʾδ�ѵ�MP3,����0��ʾ����
	private int songListInit(boolean isRefresh) { //0��ʾ����,1��ʾδ����������,(-1)��ʾ����
		//����б�
		songInfoList.clear();
		//ˢ�¸�����Ϣ
		mpApp.refreshMediaInfo(isRefresh);
		
		if(isRefresh == true) { //ˢ�²��ö�̬���ص����Բ�ִ�����²���
			return 0;
		}
		
		//��ȡ��������
		int totalNum = mpApp.getTotalNum();
    	if(totalNum < 1) { //δ����������
    		if(isRefresh == true) {
    			System.out.println("mymp3 FloatMp3Window search no mp3 file!");
    			showOneLineTip("ĩ������mp3�ļ���", (-1), (-1));
    		}
    		return 1;
    	}
    	for(int i = 0; i < totalNum; i ++) {
    		String title = mpApp.getTitleById(false, i); //��ȡ����
    		String artist = mpApp.getArtistById(false, i); //��ȡ����
    		String duration = mpApp.getDurationById(false, i); //��ȡ��ʱ��,��λ��ms
    		duration = timerTransform(duration); //ʱ�䴦��,ת���ɷ�:�����ʽ
    		
    		Map<String, String> tmp = new HashMap<String, String>(); //�������Ŀ
    		tmp.put("songName", title);
    		tmp.put("songArtist", artist);
    		tmp.put("songTimer", duration);
    		songInfoList.add(tmp);
    	}
    	songAdapter.notifyDataSetChanged(); //�����б�
    	
    	return 0;
	}
	
	//��̬������ʾ
	private void dynmicLoadShow(boolean isRefresh) {
		//��ȡ��������
		int totalNum = mpApp.getTotalNum();
    	if(totalNum < 1) { //δ����������
    		if(isRefresh == true) {
    			System.out.println("mymp3 MediaPlayerActivity search no mp3 file!");
    			showOneLineTip("ĩ������mp3�ļ���", (-1), (-1));
    		}
    		return ;
    	}
    	
    	for(int i = songInfoList.size(); i < totalNum; i ++) {
    		if(i < 2) {
    			songAdapter.setSelectPosition(-1);
    			showMediaInfo(true); //ˢ�¸���Ϣ
    		}
    		
    		String title = mpApp.getTitleById(false, i); //��ȡ����
    		
    		String duration = mpApp.getDurationById(false, i); //��ȡ��ʱ��,��λ��ms
    		duration = timerTransform(duration); //ʱ�䴦��,ת���ɷ�:�����ʽ
    		
    		Map<String, String> tmp = new HashMap<String, String>(); //�������Ŀ
    		tmp.put("songName", title);
    		tmp.put("songTimer", duration);
    		songInfoList.add(tmp);
    	}
    	songAdapter.notifyDataSetChanged(); //�����б�
	}
	
	//�����б�������
	private void songListClickDeal(AdapterView<?> parent, View view, int position, long id) {
		boolean isReplay = false; //���²��ű�־
		
		int playPosition = songAdapter.getPlayPosition(); //���Ÿ���position
		int selectPosition = songAdapter.getSelectPosition(); //ѡ�и���position
				
		if(playPosition == position){ //��ǰ���position�͵�ǰ����position��ͬһ�� 
			if(playPosition == selectPosition) {//��ǰ����position��ѡ��position��ͬһ��
				isReplay = true;
			} else {
				songAdapter.setSelectPosition(position);
			}
		} else if(selectPosition == position) { //��ǰ���position��ѡ��position��ͬһ��
			songAdapter.setPlayPosition(position);
			isReplay = true;
		} else { //��ǰ�����һ���µ�position
			songAdapter.setSelectPosition(position);
		}
		
		if(isReplay == true) { //���²��Ÿ���
			playMusicDeal(position); //���Ÿ�������
		}
		
		songAdapter.notifyDataSetChanged(); //ˢ���б�
	}
	
	//���Ÿ�������
	private void playMusicDeal(int position) {
		mpApp.setCurrentSongIndex(position); //��������Ŀ
		showMediaInfo(false); //ˢ�¸���Ϣ
	}
	
	//ɾ����ť����
	private void deleteButtonDeal() {
		int selectPosition = songAdapter.getSelectPosition();
		int totalNum = songAdapter.getCount();
		
		if(totalNum < 1 || selectPosition < 0 || selectPosition > (totalNum-1)) {
			return ; //�޸�������ѡ�������
		}
		
		if(0 == mpApp.deleteSongByIndex(songAdapter.getSelectPosition())) { //����Ϣ��ɾ������
			songInfoList.remove(songAdapter.getSelectPosition()); //ɾ���б��е���Ϣ
			songAdapter.setSelectPosition((-1)); //������ѡ�и���
			showMediaInfo(true); //ˢ�¸���Ϣ
		} else {
			showOneLineTip("�����У�ɾ��ʧ�ܣ����Ƶ��ˢ�£������������ִ��ɾ��������", (-1), (-1));
		}
	}
	
	//��һ����ť����
	private void previousButtonDeal() {
		mpApp.previousButtonDeal(); //������һ��
		showMediaInfo(true); //ˢ�¸���Ϣ
	}
	
	//��ͣ��ť����
	private void pauseButtonDeal() {
		mpApp.pauseButtonDeal();
	}
	
	//���Ű�ť����
	private void playButtonDeal() {
		mpApp.playButtonDeal();
		showMediaInfo(false); //ˢ�¸���Ϣ
	}
	
	//��һ����ť����
	private void nextButtonDeal() {
		mpApp.nextButtonDeal(); //������һ��
		showMediaInfo(true); //ˢ�¸���Ϣ
	}
	
	//Timer����
	private void timerDeal() {
		showPlayButton(); //���ݲ���״̬��ʾ���Ű�ť
		showSeekBarPerTimer(); //��������ʾ
	}
	
	//���Ű�ť��ʾ
	private void showPlayButton() {
		switch(mpApp.getPlayState()) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP:
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
			if(playButtonShow == false) {
				playButtonShow = true; //�ı��־
				play.setVisibility(Button.VISIBLE);
				pause.setVisibility(Button.GONE);
			}
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
			if(playButtonShow == true) {
				playButtonShow = false; //�ı��־
				play.setVisibility(Button.GONE);
				pause.setVisibility(Button.VISIBLE);
			}
			break;
		}
	}
	
	//����timer��ʾ���ȿ�����
	private void showSeekBarPerTimer() {
		if(playSeekBarProgressTimerShow == false) {
			return ; //��ʱ�������ʾ���޸���
		}

		if(mpApp.getTotalNum() < 1) {
			if(playSeekBar.getProgress() != 0) { //���ȿ�������Ϊ0����Ϊ0
				playSeekBar.setProgress(0);
			}
			return ; //�޸���
		}
		
		switch(mpApp.getPlayState()) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP: //����ֹͣ״̬
			if(playSeekBar.getProgress() != 0) { //���ȿ�������Ϊ0����Ϊ0
				playSeekBarProgress = 0;
				playSeekBar.setProgress(0);
				currentTime.setText("0:00"); //��ǰ����ʱ��
			}
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
			int currentTimerTmp = mpApp.getMediaPlayerCurrentPosition();
			if(currentTimerTmp < playSeekBarProgress) {
				playSeekBarProgress = playSeekBarMax;
				if(mpApp.isPlaying()) {
					playSeekBarProgress = currentTimerTmp;
				}
			} else {
				playSeekBarProgress = currentTimerTmp;
			}
			
			if (playSeekBarProgress > playSeekBarMax) {
				playSeekBarProgress = 0;
			}
			if(playSeekBar.getProgress() != playSeekBarProgress) {
				playSeekBar.setProgress(playSeekBarProgress);
				currentTime.setText(timerTransform(Integer.toString(playSeekBarProgress))); //��ǰ����ʱ��
			}
			break;
		}
	}
	
	//SeekBar����
	private void progressChangedDeal(SeekBar seekBar, int progress, boolean fromUser) {
		if(fromUser == true) { //�û�����
			switch(mpApp.getPlayState()) {
			case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP:
				break;
			case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
			case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
				if(progress != playSeekBarProgress) {
					playSeekBarProgress = progress; //�ı�λ��
					isPlaySeekBarProgressChange = true; //��־λ�øı���Ч
				}
				break;
			}
		}
	}
	private void startTrackDeal(SeekBar seekBar) {
		playSeekBarProgressTimerShow = false; //�û����ƽ��ȹ�����Timer����ʾ
	}
	private void stopTrackDeal(SeekBar seekBar) {
		playSeekBarProgressTimerShow = true; //�û������ͷ�,Timer��ʾ
		if(isPlaySeekBarProgressChange == true) { //λ�øı���Ч
			isPlaySeekBarProgressChange = false;
			mpApp.seekTo(playSeekBarProgress); //���Ҹ�����Ӧλ�ò�����
		}
	}
	
	//���������괦��
	private void playCompletionDeal() {
		showMediaInfo(true); //ˢ�¸���Ϣ
	}
	
	//���ݲ���״̬��ʾ������Ϣ
	private void showMediaInfo(boolean refreshSongList) {
		if(refreshSongList) { //������Ҫˢ�¸����б�
			songAdapter.setPlayPosition(mpApp.getCurrentSongIndex()); //���ò�����Ŀ
	    	songAdapter.notifyDataSetChanged(); //�����б�
		}
    	showSeekBar(); //���ȿ�������ʾ,���������ʾ
	}

	//���ȿ�������ʾ
	private void showSeekBar() {
		if(mpApp.getTotalNum() < 1) { //�޸���
			playSeekBarMax = 100; //���ֵĬ��Ϊ100
			playSeekBarProgress = 0; //��ǰλ��Ĭ��Ϊ0
			playSeekBar.setMax(playSeekBarMax); //���ý��ȿ��������ֵ
			playSeekBar.setProgress(playSeekBarProgress); //���ý���
			totalTime.setText(""); //��ʱ��
			currentTime.setText(""); //��ǰ����ʱ��
			return ;
		}
		
		switch(mpApp.getPlayState()) {
    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP:
			playSeekBarProgress = 0;
    		break;
    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
			playSeekBarProgress = mpApp.getMediaPlayerCurrentPosition();
    		break;
    	}
		String duration = mpApp.getDurationById(true, 0);
		if (duration != null && !duration.isEmpty()) {
			playSeekBarMax = Integer.parseInt(duration); //��¼���ֵ�ͽ���
			playSeekBar.setMax(playSeekBarMax); //���ý��ȿ��������ֵ
		} else {
			playSeekBarMax = 100000000; //��¼���ֵ�ͽ���
			playSeekBar.setMax(playSeekBarMax); //���ý��ȿ��������ֵ
		}
		
		if (playSeekBarProgress > playSeekBarMax) {
			playSeekBarProgress = 0;
		}
		playSeekBar.setProgress(playSeekBarProgress); //���ý���
		if (duration == null || duration.isEmpty()) {
			totalTime.setText("error"); //��ʱ��
		} else {
			totalTime.setText(timerTransform(Integer.toString(playSeekBarMax))); //��ʱ��
		}
		currentTime.setText(timerTransform(Integer.toString(playSeekBarProgress))); //��ǰ����ʱ��
		showSeekBarPerTimer(); //���ȿ�������ʾ,���������ʾ
	}
	
	//�رմ��ڴ���
	private void closeWindowDeal() {
		System.out.println("mymp3 FloatMp3Window close button click! begin");
		
		//ע���㲥������
		if(activityReceiver != null) { 
    		context.unregisterReceiver(activityReceiver);
    		activityReceiver = null;
    	}
    	
		if(activityReceiverNew != null) { //ע���㲥������
			context.unregisterReceiver(activityReceiverNew);
    		activityReceiverNew = null;
    	}
		
		//������Ϣ
    	mpApp.saveInfo(); 
    	
    	pw.dismiss();
		
		System.out.println("mymp3 FloatMp3Window close button click! end");
	}
	
	//��ʾ������ʾ��
	private void showOneLineTip(String str, int x, int y) {
		int gravity = Gravity.TOP|Gravity.LEFT;
		if(toast == null) {
			toast = Toast.makeText(context.getApplicationContext(), str, Toast.LENGTH_SHORT);
		} else {
			toast.setText(str);
		}
		if(x == (-1)) {
			x = 0;
			gravity = Gravity.CENTER_HORIZONTAL;
			if(y == (-1)) {
				y = 0;
				gravity = Gravity.CENTER;
			}
		} else if(y == (-1)) {
			y = 0;
			gravity = Gravity.CENTER_VERTICAL;
		}
		toast.setGravity(gravity, x, y);
		toast.show();
	}
}
