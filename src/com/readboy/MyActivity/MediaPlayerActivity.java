package com.readboy.MyActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.readboy.MyMp3.MarqueeForeverTextView;
import com.readboy.MyMp3.MediaPlayerReceiver;
import com.readboy.MyMp3.MediaPlayerApplication;
import com.readboy.MyMp3.R;
import com.readboy.MyMp3.MediaPlayerReceiver.OnReceiverBroadcastListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MediaPlayerActivity extends Activity implements OnReceiverBroadcastListener, OnClickListener{
    /** Called when the activity is first created. */
	//==========�ؼ�==========	
	private Button random = null; //���ģʽ
	private Button singleCircular = null; //����ѭ��ģʽ
	private Button circular = null; //ѭ��ģʽ
	private Button seriate = null; //˳��ģʽ
	private Button listShow = null; //�б���ʾ��ť
	
	private TextView title = null; //��������
	private TextView artist = null; //�����ݳ���
	private TextView songInfo = null; //������Ϣ
	private TextView lyricFirst = null; //���
	private TextView lyricCurrent = null;
	private TextView lyricLast = null;
	
	private TextView currentTimer = null; //��ǰ����ʱ��
	private SeekBar seekBar = null; //���ȿ�����
	private TextView totalTimer = null; //������ʱ��
	
	private Button previous = null; //ǰһ��
	private Button pause = null; //��ͣ
	private Button play = null; //����
	private Button next = null; //��һ��
	
	private Button rock = null; //ҡ��ģʽ
	private Button live = null; //�ֳ�ģʽ
	private Button pop = null; //����ģʽ
	private Button jazz = null; //��ʿģʽ
	private Button classical = null; //����ģʽ
	private Button standard = null; //��׼ģʽ
	
	private FrameLayout songListLayout = null; //�����б���
	private Button closeList = null; //�ر��б�
	private ListView songList = null; //�����б�
	private Button delete = null; //ɾ������
	private Button refresh = null; //ˢ�¸����б�
	
	private Button  volumnEnable;//��������
	private Button  volumnSilent;//����
	private SeekBar volumnSeekBar;//����������
	//==========�ؼ�����==========
	//==========ȫ�ֱ���==========
	private MediaPlayerApplication mpApp = null; //Application����
	
	private int playOrder = 0; //����˳��
	private int playMode = 0; //����ģʽ
	
	private MediaPlayerReceiver activityReceiver = null; //�㲥���ն���
	private MediaPlayerReceiver activityReceiverNew = null; //�㲥���ն���
	
	private List<Map<String, String>> songInfoList = null; //������Ϣ�б�
	private SongAdapter songAdapter = null; //�б�������
	
	private boolean seekBarProgressTimerShow = true; //�ֶ�����SeekBarʱ,Timer���ٿ���SeekBar��ʾ
	private int seekBarMax = 0; //SeekBar���ֵ�ͽ���
	private int seekBarProgress = 0;
	private boolean isSeekBarProgressChange = false; //�ֶ��ı�SeekBar��λ�ú�,����Ҫ���ŵ���Ӧλ��
	
	private boolean playButtonShow = true; //��ǰ��ʾ���Ű�ť
	
	private long lrcKey = (-1L); //�����ʾλ�ñ�־
	
	private Toast toast = null; //��ʾ��
	
	private String externalPath = null; //�ⲿ����·��
	private boolean isRestart = false;
	
	private boolean finishFlag = false; //�ػ�������־
	
	private int volumeSeekBarProgress;
	private boolean volumeSeekBarProgressTimerShow = true; //�ֶ�����SeekBarʱ,Timer���ٿ���SeekBar��ʾ
	private int volumeSeekBarMax; //SeekBar���ֵ�ͽ���
	private int presentVolumeValue; //����֮ǰ��������С
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        System.out.println("mymp3 MediaPlayerActivity onCreate() begin!");
    	
        /****************************ȥ����������ȫ������*********** ***********************/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(0x80000000, 0x80000000);
        
        /*****����*****/
        setContentView(R.layout.activity_layout);
        
        /*****��ȡ�ؼ�ID*****/
    	volumnEnable = (Button)findViewById(R.id.activity_volumnenable);
    	volumnSilent = (Button)findViewById(R.id.activity_volumnsilent);
    	volumnSeekBar = (SeekBar)findViewById(R.id.activity_volumnSeekBar);//����������
        
        random = (Button)findViewById(R.id.activity_randomMode);//���ģʽ
    	singleCircular = (Button)findViewById(R.id.activity_singleCircularMode); //����ѭ��ģʽ
    	circular = (Button)findViewById(R.id.activity_circularMode); //ѭ��ģʽ
    	seriate = (Button)findViewById(R.id.activity_seriateMode); //˳��ģʽ
    	listShow = (Button)findViewById(R.id.activity_listShow); //�б���ʾ��ť
    	
    	title = (MarqueeForeverTextView)findViewById(R.id.activity_title); //��������
    	artist = (TextView)findViewById(R.id.activity_artist); //�����ݳ���
    	songInfo = (TextView)findViewById(R.id.activity_songInfo); //������Ϣ
    	lyricFirst = (TextView)findViewById(R.id.activity_lyricFirst); //���
    	lyricCurrent = (MarqueeForeverTextView)findViewById(R.id.activity_lyricCurrent);
    	lyricLast = (TextView)findViewById(R.id.activity_lyricLast);

    	currentTimer = (TextView)findViewById(R.id.activity_currentTimer); //��ǰ����ʱ��
    	seekBar = (SeekBar)findViewById(R.id.activity_seekBar);//���ȿ�����
    	totalTimer = (TextView)findViewById(R.id.activity_totalTimer); //������ʱ��
    	
    	previous = (Button)findViewById(R.id.activity_previous); //ǰһ��
    	pause = (Button)findViewById(R.id.activity_pause); //��ͣ
    	play = (Button)findViewById(R.id.activity_play); //����
    	next = (Button)findViewById(R.id.activity_next); //��һ��

    	rock = (Button)findViewById(R.id.activity_rockMode); //ҡ��ģʽ
    	live = (Button)findViewById(R.id.activity_liveMode); //�ֳ�ģʽ
    	pop = (Button)findViewById(R.id.activity_popMode); //����ģʽ
    	jazz = (Button)findViewById(R.id.activity_jazzMode); //��ʿģʽ
    	classical = (Button)findViewById(R.id.activity_classicalMode); //����ģʽ
    	standard = (Button)findViewById(R.id.activity_standardMode); //��׼ģʽ
    	
    	songListLayout = (FrameLayout)findViewById(R.id.activity_songListLayout); //�����б���
    	closeList = (Button)findViewById(R.id.activity_closeList); //�ر��б�
    	songList = (ListView)findViewById(R.id.activity_songList); //�����б�
    	delete = (Button)findViewById(R.id.activity_delete); //ɾ������
    	refresh = (Button)findViewById(R.id.activity_refresh); //ˢ�¸����б�
    	
    	/*****���ð�ť����*****/    	
/*    	random.setOnClickListener(this);
    	singleCircular.setOnClickListener(this);
    	circular.setOnClickListener(this);
    	seriate.setOnClickListener(this);
    	listShow.setOnClickListener(this);
  	
    	previous.setOnClickListener(this);
    	pause.setOnClickListener(this);
    	play.setOnClickListener(this);
    	next.setOnClickListener(this);
    	
    	rock.setOnClickListener(this);
    	live.setOnClickListener(this);
    	pop.setOnClickListener(this);
    	jazz.setOnClickListener(this);
    	classical.setOnClickListener(this);
    	standard.setOnClickListener(this);

    	closeList.setOnClickListener(this);
    	delete.setOnClickListener(this);
    	refresh.setOnClickListener(this);*/
    	
    	songListLayout.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
    	});
    	
    	mpApp = ((MediaPlayerApplication)getApplication()); //Application�����ʼ��
    	
    	playOrder = mpApp.getPlayOrder(); //��ȡ����˳��
    	playMode = mpApp.getPlayMode(); //��ȡ����ģʽ
    	
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
    	registerReceiver(activityReceiver, intentFilter);
    	
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
    	registerReceiver(activityReceiverNew, intentFilterNew);
    	
    	//SeekBarλ�øı�����
    	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
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
    	
    	volumnSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { //���黬��ʱ����
				if(fromUser) { //�û�����
					if(mpApp.setValume(progress)) {
						volumeSeekBarProgress = progress; //��¼��ǰ����
						volumeSeekbarSetSilentDeal(); //��������
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
    	
    	//������ʾ
    	volumeSeekBarMax = mpApp.getMaxValume(); //��ȡ�������
    	volumeSeekBarProgress = mpApp.getValume(); //��ȡ��ǰ����
    	volumnSeekBar.setMax(volumeSeekBarMax); //�����������
    	volumnSeekBar.setProgress(volumeSeekBarProgress); //���õ�ǰ����
    	volumeSeekbarSetSilentDeal();
    	
    	//������Ϣ�б�
    	songInfoList = new ArrayList<Map<String, String>>();
    	//�б�������
    	songAdapter = new SongAdapter(this, songInfoList, R.layout.activity_listviewitem, new String[]{"songName", "songTimer"}, new int[]{R.id.activity_songName, R.id.activity_songTimer});
    	//�б����������
    	songList.setAdapter(songAdapter);
    	songList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				songListClickDeal(parent, view, position, id); //�������
			}
    	});
    	
//    	//��ʼ���б�
//    	int songListInit = (-1);
//    	if(callDeal() == true) { //��������
//    		songListInit = 0;
//    	} else {
//    		songListInit = songListInit(false);
//    	}
//
//    	//=============================���²���Ϊ�ظ��ϴβ���״̬=======================
//    	showPlayOrderButton(); //��ʾ��ǰ����˳��
//    	showPlayModeButton(); //��ʾ����ģʽ
//    	
//    	if(0 == songListInit) { //�����б��ʼ������
//    		showMediaInfo(true); //ˢ�¸���Ϣ
//    	}
    	
    	System.out.println("mymp3 MediaPlayerActivity onCreate() end!");
    }
    
    @Override
    public void onNewIntent(Intent intent){
    	
    	System.out.println("mymp3 MediaPlayerActivity onNewIntent() begin!");
    	
    	super.onNewIntent(intent);
    	
    	setIntent(intent);
    	
    	System.out.println("mymp3 MediaPlayerActivity onNewIntent() end!");
    }
    
    @Override
    public void onRestart() {
    	System.out.println("mymp3 MediaPlayerActivity onRestart begin! externalPath = "+externalPath);

    	super.onRestart();
    	
    	isRestart = true;
    	
    	System.out.println("mymp3 MediaPlayerActivity onRestart end! externalPath = "+externalPath);    	
    }
    
    @Override
    public void onStart() {
    	System.out.println("mymp3 MediaPlayerActivity onStart begin!");

    	super.onStart();
    	
    	System.out.println("mymp3 MediaPlayerActivity onStart end!");
    }
    
    @Override
    public void onResume() {
    	
    	System.out.println("mymp3 MediaPlayerActivity onResume() begin!");
    	
    	super.onResume();
    	
    	//��ʼ���б�
    	int songListInit = (-1);
    	if(callDeal()) { //��������
    		songListInit = 0;
    	} else {
    		songListInit = songListInit(false);
    	}

    	//=============================���²���Ϊ�ظ��ϴβ���״̬=======================
    	showPlayOrderButton(); //��ʾ��ǰ����˳��
    	showPlayModeButton(); //��ʾ����ģʽ
    	
    	if(0 == songListInit) { //�����б��ʼ������
    		showMediaInfo(true); //ˢ�¸���Ϣ
    	}
    	
    	System.out.println("mymp3 MediaPlayerActivity onResume() end!");
    }
    
    @Override
    public void onPause() {
    	System.out.println("mymp3 MediaPlayerActivity onPause begin!");

    	super.onPause();
    	
    	System.out.println("mymp3 MediaPlayerActivity onPause end!");     	
    }
    
    @Override
    public void onStop() {
    	System.out.println("mymp3 MediaPlayerActivity onStop begin!");

    	super.onStop();
    	
    	System.out.println("mymp3 MediaPlayerActivity onStop end!");     	
    }
    
    @Override
    public void onDestroy() {
    	System.out.println("mymp3 MediaPlayerActivity onDestroy() begin!");
    	
    	if(activityReceiver != null) { //ע���㲥������
    		unregisterReceiver(activityReceiver);
    		activityReceiver = null;
    	}
    	
    	if(activityReceiverNew != null) { //ע���㲥������
    		unregisterReceiver(activityReceiverNew);
    		activityReceiverNew = null;
    	}
    	
    	mpApp.saveInfo(); //������Ϣ
    	
    	super.onDestroy();
    	
    	System.out.println("mymp3 MediaPlayerActivity onDestroy() end!");
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
			songAdapter.setSelectPosition(-1); //������ѡ�и���
			songAdapter.setPlayPosition(-1); //�����޲��Ÿ���
			songInfoList.clear(); //����б�
			showMediaInfo(true); //ˢ�¸���Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_TIMER)) {
			timerDeal(); //ʱ�䴦��
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
					finish();
				}
			}
		} else if(action.equals(MediaPlayerApplication.ACTION_BATTERY_LOW)) {
			System.out.println("mymp3 battery low! finishFlag = "+finishFlag);
			if(!finishFlag) {
				finishFlag = true;
				mpApp.stopButtonDeal(); //ֹͣ����
				mpApp.saveInfo(); //������Ϣ
				finish();
			}
		}
	}
    
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.activity_randomMode: //���ģʽ
			playOrderButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_SERIATE, "˳��ģʽ��"); //˳���л�
			break;
		case R.id.activity_singleCircularMode: //����ѭ��ģʽ
			playOrderButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_RANDOM, "���ģʽ��"); //˳���л�
			break;
		case R.id.activity_circularMode: //ѭ��ģʽ
			playOrderButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR, "����ģʽ��"); //˳���л�
			break;
		case R.id.activity_seriateMode: //˳��ģʽ
			playOrderButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_CIRCULAR, "ѭ��ģʽ��"); //˳���л�
			break;
		case R.id.activity_listShow: //��ʾ�����б�
			songListLayout.setVisibility(FrameLayout.VISIBLE);
			break;
		case R.id.activity_previous: //��һ��
			previousButtonDeal();
			break;
		case R.id.activity_pause: //��ͣ
			pauseButtonDeal();
			break;
		case R.id.activity_play: //����
			playButtonDeal();
			break;
		case R.id.activity_next: //��һ��
			nextButtonDeal();
			break;
		case R.id.activity_rockMode: //ҡ��ģʽ
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_STANDARD); //ģʽת��
			break;
		case R.id.activity_liveMode: //�ֳ�ģʽ
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_ROCK); //ģʽת��
			break;
		case R.id.activity_popMode: //����ģʽ
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_LIVE); //ģʽת��
			break;
		case R.id.activity_jazzMode: //��ʿģʽ
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_POP); //ģʽת��
			break;
		case R.id.activity_classicalMode: //����ģʽ
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_JAZZ); //ģʽת��
			break;
		case R.id.activity_standardMode: //��׼ģʽ
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_CLASSICAL); //ģʽת��
			break;
		case R.id.activity_closeList: //�رո����б�
			songListLayout.setVisibility(FrameLayout.GONE);
			break;
		case R.id.activity_delete: //ɾ��������Ŀ
			deleteButtonDeal();
			break;
    	case R.id.activity_refresh: //�б�ˢ��
    		mpApp.stopButtonDeal(); //ֹͣ����
        	songListInit(true); //�����б��ʼ��
        	showMediaInfo(true); //ˢ�¸���Ϣ
    		break;
    	case R.id.acitivy_finish:
    		finish();
    		break;
    	case R.id.activity_volumnenable:
    		volumeSilentDeal(true);
    		break;
    	case R.id.activity_volumnsilent:
    		volumeSilentDeal(false);
    		break;
		}
	}
	
	//������ť
	private void volumeSilentDeal(boolean state){
		if(!state &&  mpApp.getValume()==0){
			return;
		}
		mpApp.setMute(state);
		volumeSilentButtonModeSwitch(state);
	}
	
	//����ͼ��仯
	private void volumeSilentButtonModeSwitch(boolean state){
		if(state){
			volumnEnable.setVisibility(View.INVISIBLE);
			volumnSilent.setVisibility(View.VISIBLE);
		}else{
			volumnEnable.setVisibility(View.VISIBLE);
			volumnSilent.setVisibility(View.INVISIBLE);
		}
	}
	
	
	//��������
	private void volumeSeekbarSetSilentDeal() {
		if(volumeSeekBarProgress > 0) {
			mpApp.setMute(false);
			volumeSilentButtonModeSwitch(false);
		} else {
			mpApp.setMute(true);
			volumeSilentButtonModeSwitch(true);
		}
	}
	
	
	//�ļ���������
	private boolean callDeal() {
		if(isRestart) {
			isRestart = false;
			return false;
		}
		
		Intent intent = getIntent(); //��ȡIntent����
		if(intent == null) {
			System.out.println("MediaPlayerActivity callDeal NULL INTENT");
			return false;
		}
		
		String mPath = intent.getStringExtra("file"); //��ȡ����·��
		
		if(mPath == null) { //�ļ�
			System.out.println("MediaPlayerActivity callDeal NULL FILE");
			Uri uri = intent.getData(); //��ȡuri
			if(uri != null) {
				mPath = uri.getPath(); //��ȡ����·��
			}
		}
		externalPath = null;
		if(mPath != null) {
			if(!mPath.startsWith("/")) {
				mPath = "/"+mPath;
			}
			System.out.println("mymp3 callDeal() mPath = "+mPath);
			String lower = mPath.toLowerCase();
			if (!lower.endsWith(".mp3")) {
				return false;
			}
			externalPath = mPath;
			mpApp.setCalledFilePath(mPath);
			songListInit(true); //�����б��ʼ��
//			for(int i = 0; i < mpApp.getTotalNum(); i ++) {
//				if(mpApp.getFullPathById(false, i).equals(mPath)) {
//					playMusicDeal(i); //���Ÿ�������
					return true;
//				}
//			}
		}else{
			System.out.println("MediaPlayerActivity callDeal NULL URI");
		}
		return false;
	}

	//���������괦��
	private void playCompletionDeal() {
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
	
	//ʱ��ת��,����ת���ɷ���
	private String timerTransform(String millisecond) {
		if(millisecond == null || millisecond.isEmpty()) {
			return "";
		}
		String timer = "";
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
		
	//����timer��ʾ���ȿ�����
	private void showSeekBarPerTimer() {
		if(seekBarProgressTimerShow == false) {
			return ; //��ʱ�������ʾ
		}
		
		if(mpApp.getTotalNum() < 1) {
			if(seekBar.getProgress() != 0) { //���ȿ�������Ϊ0����Ϊ0
				seekBar.setProgress(0);
			}
			return ; //�޸���
		}

		switch(mpApp.getPlayState()) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP: //����ֹͣ״̬
			if(seekBar.getProgress() != 0) { //���ȿ�������Ϊ0����Ϊ0
				seekBarProgress = 0;
				seekBar.setProgress(0);
				currentTimer.setText("0:00"); //��ǰ����ʱ��
			}
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
			int currentTimerTmp = mpApp.getMediaPlayerCurrentPosition();
			if(currentTimerTmp < seekBarProgress) {
				seekBarProgress = seekBarMax;
				if(mpApp.isPlaying()) {
					seekBarProgress = currentTimerTmp;
				}
			} else {
				seekBarProgress = currentTimerTmp;
			}
			if (seekBarProgress > seekBarMax) {
				seekBarProgress = 0;
			}
			if(seekBar.getProgress() != seekBarProgress) {
				seekBar.setProgress(seekBarProgress);
				currentTimer.setText(timerTransform(Integer.toString(seekBarProgress))); //��ǰ����ʱ��
				showLyric(); //�����ʾ
			}
			break;
		}
	}
	
	//�����ʾ����
	private void showLyric() {
		int i = 0, N = mpApp.getLyricSize();
		
		if(N < 1) { //�޸����Ϣ
			return ;
		}
		
		Long time[] = new Long[N];
		for(long tmp : mpApp.getLyricKeySet()) { //��ѯʱ��
			time[i] = tmp;
			if(tmp > seekBarProgress) { //�ѵ���ǰ��ʾ�е���һ��
				if(i == 0) { //��ʼʱ,�м���ʾ��Ŀ
					if(lrcKey != tmp) {
						lrcKey = tmp;
						lyricFirst.setText(""); 
						lyricCurrent.setText(mpApp.getTitleById(true, 0)); //��ʾ����
						lyricLast.setText(mpApp.getLyricContentByKey(time[i]));
					}
				} else if(i == 1) { //��һ����ʾ��Ŀ
					if(lrcKey != tmp) {
						lrcKey = tmp;
						lyricFirst.setText(mpApp.getTitleById(true, 0)); //��ʾ����
						lyricCurrent.setText(mpApp.getLyricContentByKey(time[i-1]));
						lyricLast.setText(mpApp.getLyricContentByKey(time[i]));
					}
				} else if(i == (N-1)) { //���һ��,��������ʾΪ��
					if(lrcKey != tmp) {
						lrcKey = tmp;
						lyricFirst.setText(mpApp.getLyricContentByKey(time[N-2])); 
						lyricCurrent.setText(mpApp.getLyricContentByKey(time[N-1])); //��ʾ���һ����
						lyricLast.setText("");
					}
				} else if(lrcKey != tmp) {
					lrcKey = tmp;
					lyricFirst.setText(mpApp.getLyricContentByKey(time[i-2])); //��ʾ���
					lyricCurrent.setText(mpApp.getLyricContentByKey(time[i-1]));
					lyricLast.setText(mpApp.getLyricContentByKey(time[i]));
				}
				break;
			}
			i ++;
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
				if(progress != seekBarProgress) {
					seekBarProgress = progress; //�ı�λ��
					isSeekBarProgressChange = true; //��־λ�øı���Ч
				}
				break;
			}
		}
	}
	private void startTrackDeal(SeekBar seekBar) {
		seekBarProgressTimerShow = false; //�û����ƽ��ȹ�����Timer����ʾ
	}
	private void stopTrackDeal(SeekBar seekBar) {
		seekBarProgressTimerShow = true; //�û������ͷ�,Timer��ʾ
		if(isSeekBarProgressChange == true) { //λ�øı���Ч
			isSeekBarProgressChange = false;
			mpApp.seekTo(seekBarProgress); //���Ҹ�����Ӧλ�ò�����
		}
	}
	
	//����˳��ť������ʾ����
	private void playOrderButtonSwitchDeal(int playOrderTmp, String tip) {
		if(playOrder != playOrderTmp) {
			playOrder = playOrderTmp;
			mpApp.setPlayOrder(playOrder); //����˳��
			showPlayOrderButton(); //��ʾ˳��ť
			showOneLineTip(tip, 730, 20);
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
	
	//����ģʽ��ť������ʾ����
	private void playModeButtonSwitchDeal(int playModeTmp) {
		if(playMode != playModeTmp) {
			playMode = playModeTmp;
			mpApp.setPlayMode(playMode); //����ģʽ
			showPlayModeButton(); //��ʾģʽ��ť
		} 
	}
	
	//ɾ����ť����
	private void deleteButtonDeal() {
		int selectPosition = songAdapter.getSelectPosition();
		int totalNum = songAdapter.getCount();
		
		if(totalNum < 1 || selectPosition < 0 || selectPosition > (totalNum-1)) {
			return ; //�޸�������ѡ�������
		}
		if(0 == mpApp.deleteSongByIndex(selectPosition)) { //����Ϣ��ɾ������
			songInfoList.remove(selectPosition); //ɾ���б��е���Ϣ
			songAdapter.setSelectPosition((-1)); //������ѡ�и���
			songAdapter.setPlayPosition(mpApp.getCurrentSongIndex()); //���ò��Ÿ���index
			showMediaInfo(true); //ˢ�¸���Ϣ
		} else {
			showOneLineTip("�����У�ɾ��ʧ�ܣ����Ƶ��ˢ�£������������ִ��ɾ��������", (-1), (-1));
		}
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
    			System.out.println("mymp3 MediaPlayerActivity search no mp3 file!");
    			showOneLineTip("ĩ������mp3�ļ���", (-1), (-1));
    		}
    		return 1;
    	}
    	for(int i = 0; i < totalNum; i ++) {
    		String title = mpApp.getTitleById(false, i); //��ȡ����
    		
    		String duration = mpApp.getDurationById(false, i); //��ȡ��ʱ��,��λ��ms
    		duration = timerTransform(duration); //ʱ�䴦��,ת���ɷ�:�����ʽ
    		
    		Map<String, String> tmp = new HashMap<String, String>(); //�������Ŀ
    		tmp.put("songName", title);
    		tmp.put("songTimer", duration);
    		songInfoList.add(tmp);
    	}
    	songAdapter.setSelectPosition(-1);
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
    	
    	if (externalPath != null && !externalPath.isEmpty()) {
    		playMusicDeal(0);
    		externalPath = null;
    	}
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
	
	//��ʾ����˳��ť
	private void showPlayOrderButton() {
		int seriateVisible = Button.GONE;
		int circularVisible = Button.GONE;
		int singleCircularVisible = Button.GONE;
		int randomVisible = Button.GONE;

		switch(playOrder) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_SERIATE:
			seriateVisible = Button.VISIBLE;
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_CIRCULAR:
			circularVisible = Button.VISIBLE;
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR:
			singleCircularVisible = Button.VISIBLE;
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_RANDOM:
			randomVisible = Button.VISIBLE;
			break;
		}
	
		seriate.setVisibility(seriateVisible);
		circular.setVisibility(circularVisible);
		singleCircular.setVisibility(singleCircularVisible);
		random.setVisibility(randomVisible);
	}
	
	//��ʾ����ģʽ��ť
	private void showPlayModeButton() {
		int standardVisible = Button.GONE;
		int classicalVisible = Button.GONE;
		int jazzVisible = Button.GONE;
		int popVisible = Button.GONE;
		int liveVisible = Button.GONE;
		int rockVisible = Button.GONE;

		switch(playMode) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_STANDARD:
			standardVisible = Button.VISIBLE;
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_CLASSICAL:
			classicalVisible = Button.VISIBLE;
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_JAZZ:
			jazzVisible = Button.VISIBLE;
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_POP:
			popVisible = Button.VISIBLE;
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_LIVE:
			liveVisible = Button.VISIBLE;
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_ROCK:
			rockVisible = Button.VISIBLE;
			break;
		}
	
		standard.setVisibility(standardVisible);
		classical.setVisibility(classicalVisible);
		jazz.setVisibility(jazzVisible);
		pop.setVisibility(popVisible);
		live.setVisibility(liveVisible);
		rock.setVisibility(rockVisible);
	}
	
	//���ݲ���״̬��ʾ������Ϣ
	private void showMediaInfo(boolean refreshSongList) {
		if(refreshSongList) { //������Ҫˢ�¸����б�
			songAdapter.setPlayPosition(mpApp.getCurrentSongIndex()); //���ò�����Ŀ
	    	songAdapter.notifyDataSetChanged(); //�����б�
		}
		showNoSongInfo(); //��ʾ�޸�����Ϣ
		showSongInfo(); //��ʾ������Ϣ
    	showNoLyric(); //����������ʾ�޸��
    	showSeekBar(); //���ȿ�������ʾ,���������ʾ
	}
	
	//�޸�����Ϣ
	private void showNoSongInfo() {
		title.setText(""); //��ʾ��������
		artist.setText(""); //��ʾ����
		songInfo.setText(""); //��ʾ������Ϣ
	}
	
	//��ʾ������Ϣ
	private void showSongInfo() {
		if(mpApp.getTotalNum() > 0) { //�и���
			title.setText(mpApp.getTitleById(true, 0)); //��ʾ��������
			artist.setText(mpApp.getArtistById(true, 0)); //��ʾ����
			songInfo.setText(mpApp.getAlbumById(true, 0)); //��ʾ������Ϣ
		}
	}
	
	//����������ʾ��ǰ�޸��
	private void showNoLyric() {
		lyricFirst.setText(""); //��ʾ�޸��
		lyricCurrent.setText("");
		lyricLast.setText("");
	}

	//���ȿ�������ʾ
	private void showSeekBar() {
		if(mpApp.getTotalNum() < 1) { //�޸���
			seekBarMax = 100; //���ֵĬ��Ϊ100
			seekBarProgress = 0; //��ǰλ��Ĭ��Ϊ0
			seekBar.setMax(seekBarMax); //���ý��ȿ��������ֵ
			seekBar.setProgress(seekBarProgress); //���ý���
			totalTimer.setText(""); //��ʱ��
			currentTimer.setText(""); //��ǰ����ʱ��
			return ;
		}
		
		switch(mpApp.getPlayState()) {
    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP:
			seekBarProgress = 0;
    		break;
    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
			seekBarProgress = mpApp.getMediaPlayerCurrentPosition();
    		break;
    	}
		String duration = mpApp.getDurationById(true, 0);
		if (duration != null && !duration.isEmpty()) {
			seekBarMax = Integer.parseInt(duration); //��¼���ֵ�ͽ���
			seekBar.setMax(seekBarMax); //���ý��ȿ��������ֵ
		} else {
			seekBarMax = 100000000; //��¼���ֵ�ͽ���
			seekBar.setMax(seekBarMax); //���ý��ȿ��������ֵ
		}
		if (seekBarProgress > seekBarMax) {
			seekBarProgress = 0;
		}
		seekBar.setProgress(seekBarProgress); //���ý���
		if (duration == null || duration.isEmpty()) {
			totalTimer.setText("error"); //��ʱ��
		} else {
			totalTimer.setText(timerTransform(Integer.toString(seekBarMax))); //��ʱ��
		}
		currentTimer.setText(timerTransform(Integer.toString(seekBarProgress))); //��ǰ����ʱ��
    	showSeekBarPerTimer(); //���ȿ�������ʾ,���������ʾ
		showLyric(); //�����ʾ
	}
	
	//��ʾ������ʾ��
	private void showOneLineTip(String str, int x, int y) {
		int gravity = Gravity.TOP|Gravity.LEFT;
		if(toast == null) {
			toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
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