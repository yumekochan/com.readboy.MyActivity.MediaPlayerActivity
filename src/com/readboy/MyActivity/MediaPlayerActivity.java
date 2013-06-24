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
	//==========控件==========	
	private Button random = null; //随机模式
	private Button singleCircular = null; //单曲循环模式
	private Button circular = null; //循环模式
	private Button seriate = null; //顺序模式
	private Button listShow = null; //列表显示按钮
	
	private TextView title = null; //歌曲名称
	private TextView artist = null; //歌曲演唱者
	private TextView songInfo = null; //歌曲信息
	private TextView lyricFirst = null; //歌词
	private TextView lyricCurrent = null;
	private TextView lyricLast = null;
	
	private TextView currentTimer = null; //当前播放时间
	private SeekBar seekBar = null; //进度控制条
	private TextView totalTimer = null; //歌曲总时间
	
	private Button previous = null; //前一首
	private Button pause = null; //暂停
	private Button play = null; //播放
	private Button next = null; //下一首
	
	private Button rock = null; //摇滚模式
	private Button live = null; //现场模式
	private Button pop = null; //流行模式
	private Button jazz = null; //爵士模式
	private Button classical = null; //经典模式
	private Button standard = null; //标准模式
	
	private FrameLayout songListLayout = null; //歌曲列表布局
	private Button closeList = null; //关闭列表
	private ListView songList = null; //歌曲列表
	private Button delete = null; //删除歌曲
	private Button refresh = null; //刷新歌曲列表
	
	private Button  volumnEnable;//音量开启
	private Button  volumnSilent;//静音
	private SeekBar volumnSeekBar;//音量控制条
	//==========控件结束==========
	//==========全局变量==========
	private MediaPlayerApplication mpApp = null; //Application对象
	
	private int playOrder = 0; //播放顺序
	private int playMode = 0; //播放模式
	
	private MediaPlayerReceiver activityReceiver = null; //广播接收对象
	private MediaPlayerReceiver activityReceiverNew = null; //广播接收对象
	
	private List<Map<String, String>> songInfoList = null; //歌曲信息列表
	private SongAdapter songAdapter = null; //列表适配器
	
	private boolean seekBarProgressTimerShow = true; //手动控制SeekBar时,Timer不再控制SeekBar显示
	private int seekBarMax = 0; //SeekBar最大值和进度
	private int seekBarProgress = 0;
	private boolean isSeekBarProgressChange = false; //手动改变SeekBar的位置后,音乐要播放到相应位置
	
	private boolean playButtonShow = true; //当前显示播放按钮
	
	private long lrcKey = (-1L); //歌词显示位置标志
	
	private Toast toast = null; //提示语
	
	private String externalPath = null; //外部调用路径
	private boolean isRestart = false;
	
	private boolean finishFlag = false; //关机操作标志
	
	private int volumeSeekBarProgress;
	private boolean volumeSeekBarProgressTimerShow = true; //手动控制SeekBar时,Timer不再控制SeekBar显示
	private int volumeSeekBarMax; //SeekBar最大值和进度
	private int presentVolumeValue; //静音之前的音量大小
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        System.out.println("mymp3 MediaPlayerActivity onCreate() begin!");
    	
        /****************************去掉标题栏，全屏窗口*********** ***********************/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(0x80000000, 0x80000000);
        
        /*****布局*****/
        setContentView(R.layout.activity_layout);
        
        /*****获取控件ID*****/
    	volumnEnable = (Button)findViewById(R.id.activity_volumnenable);
    	volumnSilent = (Button)findViewById(R.id.activity_volumnsilent);
    	volumnSeekBar = (SeekBar)findViewById(R.id.activity_volumnSeekBar);//音量控制条
        
        random = (Button)findViewById(R.id.activity_randomMode);//随机模式
    	singleCircular = (Button)findViewById(R.id.activity_singleCircularMode); //单曲循环模式
    	circular = (Button)findViewById(R.id.activity_circularMode); //循环模式
    	seriate = (Button)findViewById(R.id.activity_seriateMode); //顺序模式
    	listShow = (Button)findViewById(R.id.activity_listShow); //列表显示按钮
    	
    	title = (MarqueeForeverTextView)findViewById(R.id.activity_title); //歌曲名称
    	artist = (TextView)findViewById(R.id.activity_artist); //歌曲演唱者
    	songInfo = (TextView)findViewById(R.id.activity_songInfo); //歌曲信息
    	lyricFirst = (TextView)findViewById(R.id.activity_lyricFirst); //歌词
    	lyricCurrent = (MarqueeForeverTextView)findViewById(R.id.activity_lyricCurrent);
    	lyricLast = (TextView)findViewById(R.id.activity_lyricLast);

    	currentTimer = (TextView)findViewById(R.id.activity_currentTimer); //当前播放时间
    	seekBar = (SeekBar)findViewById(R.id.activity_seekBar);//进度控制条
    	totalTimer = (TextView)findViewById(R.id.activity_totalTimer); //歌曲总时间
    	
    	previous = (Button)findViewById(R.id.activity_previous); //前一首
    	pause = (Button)findViewById(R.id.activity_pause); //暂停
    	play = (Button)findViewById(R.id.activity_play); //播放
    	next = (Button)findViewById(R.id.activity_next); //下一首

    	rock = (Button)findViewById(R.id.activity_rockMode); //摇滚模式
    	live = (Button)findViewById(R.id.activity_liveMode); //现场模式
    	pop = (Button)findViewById(R.id.activity_popMode); //流行模式
    	jazz = (Button)findViewById(R.id.activity_jazzMode); //爵士模式
    	classical = (Button)findViewById(R.id.activity_classicalMode); //经典模式
    	standard = (Button)findViewById(R.id.activity_standardMode); //标准模式
    	
    	songListLayout = (FrameLayout)findViewById(R.id.activity_songListLayout); //歌曲列表布局
    	closeList = (Button)findViewById(R.id.activity_closeList); //关闭列表
    	songList = (ListView)findViewById(R.id.activity_songList); //歌曲列表
    	delete = (Button)findViewById(R.id.activity_delete); //删除歌曲
    	refresh = (Button)findViewById(R.id.activity_refresh); //刷新歌曲列表
    	
    	/*****设置按钮侦听*****/    	
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
    	
    	mpApp = ((MediaPlayerApplication)getApplication()); //Application对象初始化
    	
    	playOrder = mpApp.getPlayOrder(); //获取播放顺序
    	playMode = mpApp.getPlayMode(); //获取播放模式
    	
    	//创建自定义广播接收器MediaPlayerActivityReceiver
    	activityReceiver = new MediaPlayerReceiver();
    	activityReceiver.setOnReceiverBroadcastListener(this);
    	//创建过滤器IntentFilter
    	IntentFilter intentFilter = new IntentFilter();
    	//指定监听的Action
    	intentFilter.addAction(MediaPlayerApplication.ACTION_TIMER);
    	intentFilter.addAction(MediaPlayerApplication.ACTION_PLAYCOMPLETION);
    	intentFilter.addAction(MediaPlayerApplication.ACTION_DYNAMICLOADSHOW);
    	intentFilter.addAction(MediaPlayerApplication.ACTION_DELETEDEAL);
    	intentFilter.addAction(MediaPlayerApplication.ACTION_REFRESHDEAL);
    	//注册BroadcastReceiver
    	registerReceiver(activityReceiver, intentFilter);
    	
    	//创建自定义广播接收器MediaPlayerActivityReceiver
    	activityReceiverNew = new MediaPlayerReceiver();
    	activityReceiverNew.setOnReceiverBroadcastListener(this);
    	//创建过滤器IntentFilter
    	IntentFilter intentFilterNew = new IntentFilter();
    	//指定监听的Action
    	intentFilterNew.addAction(MediaPlayerApplication.ACTION_BATTERY_LOW);
    	intentFilterNew.addAction(MediaPlayerApplication.ACTION_MEDIA_REMOVED);
    	intentFilterNew.addAction(MediaPlayerApplication.ACTION_MEDIA_EJECT);
    	intentFilterNew.addDataScheme("file");
    	//注册BroadcastReceiver
    	registerReceiver(activityReceiverNew, intentFilterNew);
    	
    	//SeekBar位置改变侦听
    	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { //滑块滑动时调用
				progressChangedDeal(seekBar, progress, fromUser);
			}
			public void onStartTrackingTouch(SeekBar seekBar) { //滑块开始滑动时
				startTrackDeal(seekBar);
			}
			public void onStopTrackingTouch(SeekBar seekBar) { //滑块停止滑动时
				stopTrackDeal(seekBar);
			}
    	});
    	
    	volumnSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { //滑块滑动时调用
				if(fromUser) { //用户控制
					if(mpApp.setValume(progress)) {
						volumeSeekBarProgress = progress; //记录当前音量
						volumeSeekbarSetSilentDeal(); //静音处理
					}
				}
			}
			public void onStartTrackingTouch(SeekBar seekBar) { //滑块开始滑动时
				volumeSeekBarProgressTimerShow = false;
			}
			public void onStopTrackingTouch(SeekBar seekBar) { //滑块停止滑动时
				volumeSeekBarProgressTimerShow = true;
			}
    	});
    	
    	//音量显示
    	volumeSeekBarMax = mpApp.getMaxValume(); //获取最大音量
    	volumeSeekBarProgress = mpApp.getValume(); //获取当前音量
    	volumnSeekBar.setMax(volumeSeekBarMax); //设置最大音量
    	volumnSeekBar.setProgress(volumeSeekBarProgress); //设置当前音量
    	volumeSeekbarSetSilentDeal();
    	
    	//歌曲信息列表
    	songInfoList = new ArrayList<Map<String, String>>();
    	//列表适配器
    	songAdapter = new SongAdapter(this, songInfoList, R.layout.activity_listviewitem, new String[]{"songName", "songTimer"}, new int[]{R.id.activity_songName, R.id.activity_songTimer});
    	//列表关联适配器
    	songList.setAdapter(songAdapter);
    	songList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				songListClickDeal(parent, view, position, id); //点击处理
			}
    	});
    	
//    	//初始化列表
//    	int songListInit = (-1);
//    	if(callDeal() == true) { //处部调用
//    		songListInit = 0;
//    	} else {
//    		songListInit = songListInit(false);
//    	}
//
//    	//=============================以下部分为回复上次播放状态=======================
//    	showPlayOrderButton(); //显示当前播放顺序
//    	showPlayModeButton(); //显示播放模式
//    	
//    	if(0 == songListInit) { //歌曲列表初始化正常
//    		showMediaInfo(true); //刷新各信息
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
    	
    	//初始化列表
    	int songListInit = (-1);
    	if(callDeal()) { //处部调用
    		songListInit = 0;
    	} else {
    		songListInit = songListInit(false);
    	}

    	//=============================以下部分为回复上次播放状态=======================
    	showPlayOrderButton(); //显示当前播放顺序
    	showPlayModeButton(); //显示播放模式
    	
    	if(0 == songListInit) { //歌曲列表初始化正常
    		showMediaInfo(true); //刷新各信息
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
    	
    	if(activityReceiver != null) { //注销广播接收器
    		unregisterReceiver(activityReceiver);
    		activityReceiver = null;
    	}
    	
    	if(activityReceiverNew != null) { //注销广播接收器
    		unregisterReceiver(activityReceiverNew);
    		activityReceiverNew = null;
    	}
    	
    	mpApp.saveInfo(); //保存信息
    	
    	super.onDestroy();
    	
    	System.out.println("mymp3 MediaPlayerActivity onDestroy() end!");
    }
    
	public void onReceiverDeal(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(MediaPlayerApplication.ACTION_PLAYCOMPLETION)) {
			playCompletionDeal(); //播放结束处理
		} else if(action.equals(MediaPlayerApplication.ACTION_DYNAMICLOADSHOW)) {
			dynmicLoadShow(true); //动态加载显示
		} else if(action.equals(MediaPlayerApplication.ACTION_DELETEDEAL)) { //删除处理
			songAdapter.setSelectPosition((-1)); //设置无选中歌曲
			songListInit(false); //歌曲列表初始化
        	showMediaInfo(true); //刷新各信息
		} else if(action.equals(MediaPlayerApplication.ACTION_REFRESHDEAL)) { //删除处理
			songAdapter.setSelectPosition(-1); //设置无选中歌曲
			songAdapter.setPlayPosition(-1); //设置无播放歌曲
			songInfoList.clear(); //清除列表
			showMediaInfo(true); //刷新各信息
		} else if(action.equals(MediaPlayerApplication.ACTION_TIMER)) {
			timerDeal(); //时间处理
		} else if(action.equals(MediaPlayerApplication.ACTION_SHUTDOWN)) {
			mpApp.stopButtonDeal(); //停止播放
			mpApp.saveInfo(); //保存信息
		} else if(action.equals(MediaPlayerApplication.ACTION_MEDIA_REMOVED) ||
			(action.equals(MediaPlayerApplication.ACTION_MEDIA_EJECT))) {
			System.out.println("mymp3 card removed! finishFlag = "+finishFlag);
			String mPath = mpApp.getFullPathById(true, 0); 
			if (mPath != null && (mPath.startsWith("/mnt/extsd/") || mPath.startsWith("mnt/extsd/") ||
					mPath.startsWith("mnt/sdcard-ext/") || mPath.startsWith("/mnt/sdcard-ext/"))) {
				if (!finishFlag) {
					finishFlag = true;
					mpApp.stopButtonDeal(); //停止播放
					mpApp.saveInfo(); //保存信息
					finish();
				}
			}
		} else if(action.equals(MediaPlayerApplication.ACTION_BATTERY_LOW)) {
			System.out.println("mymp3 battery low! finishFlag = "+finishFlag);
			if(!finishFlag) {
				finishFlag = true;
				mpApp.stopButtonDeal(); //停止播放
				mpApp.saveInfo(); //保存信息
				finish();
			}
		}
	}
    
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.activity_randomMode: //随机模式
			playOrderButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_SERIATE, "顺序模式："); //顺序切换
			break;
		case R.id.activity_singleCircularMode: //单曲循环模式
			playOrderButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_RANDOM, "随机模式："); //顺序切换
			break;
		case R.id.activity_circularMode: //循环模式
			playOrderButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_SINGLECIRCULAR, "单曲模式："); //顺序切换
			break;
		case R.id.activity_seriateMode: //顺序模式
			playOrderButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYORDER_CIRCULAR, "循环模式："); //顺序切换
			break;
		case R.id.activity_listShow: //显示歌曲列表
			songListLayout.setVisibility(FrameLayout.VISIBLE);
			break;
		case R.id.activity_previous: //上一曲
			previousButtonDeal();
			break;
		case R.id.activity_pause: //暂停
			pauseButtonDeal();
			break;
		case R.id.activity_play: //播放
			playButtonDeal();
			break;
		case R.id.activity_next: //下一曲
			nextButtonDeal();
			break;
		case R.id.activity_rockMode: //摇滚模式
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_STANDARD); //模式转换
			break;
		case R.id.activity_liveMode: //现场模式
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_ROCK); //模式转换
			break;
		case R.id.activity_popMode: //流行模式
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_LIVE); //模式转换
			break;
		case R.id.activity_jazzMode: //爵士模式
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_POP); //模式转换
			break;
		case R.id.activity_classicalMode: //经典模式
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_JAZZ); //模式转换
			break;
		case R.id.activity_standardMode: //标准模式
			playModeButtonSwitchDeal(MediaPlayerApplication.MEDIAPLAYER_PLAYMODE_CLASSICAL); //模式转换
			break;
		case R.id.activity_closeList: //关闭歌曲列表
			songListLayout.setVisibility(FrameLayout.GONE);
			break;
		case R.id.activity_delete: //删除歌曲条目
			deleteButtonDeal();
			break;
    	case R.id.activity_refresh: //列表刷新
    		mpApp.stopButtonDeal(); //停止播放
        	songListInit(true); //歌曲列表初始化
        	showMediaInfo(true); //刷新各信息
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
	
	//静音按钮
	private void volumeSilentDeal(boolean state){
		if(!state &&  mpApp.getValume()==0){
			return;
		}
		mpApp.setMute(state);
		volumeSilentButtonModeSwitch(state);
	}
	
	//静音图标变化
	private void volumeSilentButtonModeSwitch(boolean state){
		if(state){
			volumnEnable.setVisibility(View.INVISIBLE);
			volumnSilent.setVisibility(View.VISIBLE);
		}else{
			volumnEnable.setVisibility(View.VISIBLE);
			volumnSilent.setVisibility(View.INVISIBLE);
		}
	}
	
	
	//静音处理
	private void volumeSeekbarSetSilentDeal() {
		if(volumeSeekBarProgress > 0) {
			mpApp.setMute(false);
			volumeSilentButtonModeSwitch(false);
		} else {
			mpApp.setMute(true);
			volumeSilentButtonModeSwitch(true);
		}
	}
	
	
	//文件关联处理
	private boolean callDeal() {
		if(isRestart) {
			isRestart = false;
			return false;
		}
		
		Intent intent = getIntent(); //获取Intent对象
		if(intent == null) {
			System.out.println("MediaPlayerActivity callDeal NULL INTENT");
			return false;
		}
		
		String mPath = intent.getStringExtra("file"); //获取传入路径
		
		if(mPath == null) { //文件
			System.out.println("MediaPlayerActivity callDeal NULL FILE");
			Uri uri = intent.getData(); //获取uri
			if(uri != null) {
				mPath = uri.getPath(); //获取传入路径
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
			songListInit(true); //歌曲列表初始化
//			for(int i = 0; i < mpApp.getTotalNum(); i ++) {
//				if(mpApp.getFullPathById(false, i).equals(mPath)) {
//					playMusicDeal(i); //播放歌曲处理
					return true;
//				}
//			}
		}else{
			System.out.println("MediaPlayerActivity callDeal NULL URI");
		}
		return false;
	}

	//歌曲播放完处理
	private void playCompletionDeal() {
		showMediaInfo(true); //刷新各信息
	}
	
	//Timer处理
	private void timerDeal() {
		showPlayButton(); //根据播放状态显示播放按钮
		showSeekBarPerTimer(); //进度条显示
	}
	
	//播放按钮显示
	private void showPlayButton() {
		switch(mpApp.getPlayState()) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP:
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
			if(playButtonShow == false) {
				playButtonShow = true; //改变标志
				play.setVisibility(Button.VISIBLE);
				pause.setVisibility(Button.GONE);
			}
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
			if(playButtonShow == true) {
				playButtonShow = false; //改变标志
				play.setVisibility(Button.GONE);
				pause.setVisibility(Button.VISIBLE);
			}
			break;
		}
	}
	
	//时间转化,毫秒转化成分秒
	private String timerTransform(String millisecond) {
		if(millisecond == null || millisecond.isEmpty()) {
			return "";
		}
		String timer = "";
		int hour = 0, minute = 0, second = 0;
		//string转int
		int millisecondTmp = Integer.parseInt(millisecond); 
		
		//计算分钟
		while(millisecondTmp >= 60000) {
			millisecondTmp -= 60000;
			minute ++;
			if(minute >= 60) {
				minute -= 60;
				hour ++;
			}
		}
		//计算秒钟
		while(millisecondTmp >= 1000) {
			millisecondTmp -= 1000;
			second ++;
		}
		//处理秒钟,四舍五入
		if(millisecondTmp >= 500) {
			//second ++; //全舍不入了
		}
		
		//int转string
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
		
	//根据timer显示进度控制条
	private void showSeekBarPerTimer() {
		if(seekBarProgressTimerShow == false) {
			return ; //非时间控制显示
		}
		
		if(mpApp.getTotalNum() < 1) {
			if(seekBar.getProgress() != 0) { //进度控制条不为0设置为0
				seekBar.setProgress(0);
			}
			return ; //无歌曲
		}

		switch(mpApp.getPlayState()) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP: //音乐停止状态
			if(seekBar.getProgress() != 0) { //进度控制条不为0设置为0
				seekBarProgress = 0;
				seekBar.setProgress(0);
				currentTimer.setText("0:00"); //当前播放时间
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
				currentTimer.setText(timerTransform(Integer.toString(seekBarProgress))); //当前播放时间
				showLyric(); //歌词显示
			}
			break;
		}
	}
	
	//歌词显示处理
	private void showLyric() {
		int i = 0, N = mpApp.getLyricSize();
		
		if(N < 1) { //无歌词信息
			return ;
		}
		
		Long time[] = new Long[N];
		for(long tmp : mpApp.getLyricKeySet()) { //查询时间
			time[i] = tmp;
			if(tmp > seekBarProgress) { //已到当前显示行的下一行
				if(i == 0) { //起始时,中间显示题目
					if(lrcKey != tmp) {
						lrcKey = tmp;
						lyricFirst.setText(""); 
						lyricCurrent.setText(mpApp.getTitleById(true, 0)); //显示歌名
						lyricLast.setText(mpApp.getLyricContentByKey(time[i]));
					}
				} else if(i == 1) { //第一句显示题目
					if(lrcKey != tmp) {
						lrcKey = tmp;
						lyricFirst.setText(mpApp.getTitleById(true, 0)); //显示歌名
						lyricCurrent.setText(mpApp.getLyricContentByKey(time[i-1]));
						lyricLast.setText(mpApp.getLyricContentByKey(time[i]));
					}
				} else if(i == (N-1)) { //最后一行,第三句显示为空
					if(lrcKey != tmp) {
						lrcKey = tmp;
						lyricFirst.setText(mpApp.getLyricContentByKey(time[N-2])); 
						lyricCurrent.setText(mpApp.getLyricContentByKey(time[N-1])); //显示最后一句歌词
						lyricLast.setText("");
					}
				} else if(lrcKey != tmp) {
					lrcKey = tmp;
					lyricFirst.setText(mpApp.getLyricContentByKey(time[i-2])); //显示歌词
					lyricCurrent.setText(mpApp.getLyricContentByKey(time[i-1]));
					lyricLast.setText(mpApp.getLyricContentByKey(time[i]));
				}
				break;
			}
			i ++;
		}
	}
	
	//SeekBar处理
	private void progressChangedDeal(SeekBar seekBar, int progress, boolean fromUser) {
		if(fromUser == true) { //用户控制
			switch(mpApp.getPlayState()) {
			case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP:
				break;
			case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
			case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
				if(progress != seekBarProgress) {
					seekBarProgress = progress; //改变位置
					isSeekBarProgressChange = true; //标志位置改变有效
				}
				break;
			}
		}
	}
	private void startTrackDeal(SeekBar seekBar) {
		seekBarProgressTimerShow = false; //用户控制进度过程中Timer不显示
	}
	private void stopTrackDeal(SeekBar seekBar) {
		seekBarProgressTimerShow = true; //用户控制释放,Timer显示
		if(isSeekBarProgressChange == true) { //位置改变有效
			isSeekBarProgressChange = false;
			mpApp.seekTo(seekBarProgress); //查找歌曲对应位置并播放
		}
	}
	
	//播放顺序按钮交换显示处理
	private void playOrderButtonSwitchDeal(int playOrderTmp, String tip) {
		if(playOrder != playOrderTmp) {
			playOrder = playOrderTmp;
			mpApp.setPlayOrder(playOrder); //设置顺序
			showPlayOrderButton(); //显示顺序按钮
			showOneLineTip(tip, 730, 20);
		} 
	}
	
	//上一曲按钮处理
	private void previousButtonDeal() {
		mpApp.previousButtonDeal(); //播放上一首
		showMediaInfo(true); //刷新各信息
	}
	
	//暂停按钮处理
	private void pauseButtonDeal() {
		mpApp.pauseButtonDeal();
	}
	
	//播放按钮处理
	private void playButtonDeal() {
		mpApp.playButtonDeal();
		showMediaInfo(false); //刷新各信息
	}
	
	//下一曲按钮处理
	private void nextButtonDeal() {
		mpApp.nextButtonDeal(); //播放下一首
		showMediaInfo(true); //刷新各信息
	}
	
	//播放模式按钮交换显示处理
	private void playModeButtonSwitchDeal(int playModeTmp) {
		if(playMode != playModeTmp) {
			playMode = playModeTmp;
			mpApp.setPlayMode(playMode); //设置模式
			showPlayModeButton(); //显示模式按钮
		} 
	}
	
	//删除按钮处理
	private void deleteButtonDeal() {
		int selectPosition = songAdapter.getSelectPosition();
		int totalNum = songAdapter.getCount();
		
		if(totalNum < 1 || selectPosition < 0 || selectPosition > (totalNum-1)) {
			return ; //无歌曲或无选中项不处理
		}
		if(0 == mpApp.deleteSongByIndex(selectPosition)) { //从信息中删除歌曲
			songInfoList.remove(selectPosition); //删除列表中的信息
			songAdapter.setSelectPosition((-1)); //设置无选中歌曲
			songAdapter.setPlayPosition(mpApp.getCurrentSongIndex()); //设置播放歌曲index
			showMediaInfo(true); //刷新各信息
		} else {
			showOneLineTip("搜索中，删除失败，请别频繁刷新，等搜索完成再执行删除操作！", (-1), (-1));
		}
	}
	
	//歌曲列表初始化,返回1表示未搜到MP3,返回0表示正常
	private int songListInit(boolean isRefresh) { //0表示正常,1表示未搜索到歌曲,(-1)表示出错
		//清空列表
		songInfoList.clear();
		//刷新歌曲信息
		mpApp.refreshMediaInfo(isRefresh);
		
		if(isRefresh == true) { //刷新采用动态加载的所以不执行以下操作
			return 0;
		}
		
		//获取歌曲总数
		int totalNum = mpApp.getTotalNum();
    	if(totalNum < 1) { //未搜索到歌曲
    		if(isRefresh == true) {
    			System.out.println("mymp3 MediaPlayerActivity search no mp3 file!");
    			showOneLineTip("末搜索到mp3文件！", (-1), (-1));
    		}
    		return 1;
    	}
    	for(int i = 0; i < totalNum; i ++) {
    		String title = mpApp.getTitleById(false, i); //获取歌名
    		
    		String duration = mpApp.getDurationById(false, i); //获取总时间,单位是ms
    		duration = timerTransform(duration); //时间处理,转化成分:秒的形式
    		
    		Map<String, String> tmp = new HashMap<String, String>(); //添加新条目
    		tmp.put("songName", title);
    		tmp.put("songTimer", duration);
    		songInfoList.add(tmp);
    	}
    	songAdapter.setSelectPosition(-1);
    	songAdapter.notifyDataSetChanged(); //更新列表
    	
    	return 0;
	}
	
	//动态加载显示
	private void dynmicLoadShow(boolean isRefresh) {
		//获取歌曲总数
		int totalNum = mpApp.getTotalNum();
    	if(totalNum < 1) { //未搜索到歌曲
    		if(isRefresh == true) {
    			System.out.println("mymp3 MediaPlayerActivity search no mp3 file!");
    			showOneLineTip("末搜索到mp3文件！", (-1), (-1));
    		}
    		return ;
    	}
    	for(int i = songInfoList.size(); i < totalNum; i ++) {
    		if(i < 2) {
    			songAdapter.setSelectPosition(-1);
    			showMediaInfo(true); //刷新各信息
    		}
    		
    		String title = mpApp.getTitleById(false, i); //获取歌名
    		
    		String duration = mpApp.getDurationById(false, i); //获取总时间,单位是ms
    		duration = timerTransform(duration); //时间处理,转化成分:秒的形式
    		
    		Map<String, String> tmp = new HashMap<String, String>(); //添加新条目
    		tmp.put("songName", title);
    		tmp.put("songTimer", duration);
    		songInfoList.add(tmp);
    	}
    	songAdapter.notifyDataSetChanged(); //更新列表
    	
    	if (externalPath != null && !externalPath.isEmpty()) {
    		playMusicDeal(0);
    		externalPath = null;
    	}
	}
	
	//歌曲列表点击处理
	private void songListClickDeal(AdapterView<?> parent, View view, int position, long id) {
		boolean isReplay = false; //重新播放标志
		
		int playPosition = songAdapter.getPlayPosition(); //播放歌曲position
		int selectPosition = songAdapter.getSelectPosition(); //选中歌曲position
				
		if(playPosition == position){ //当前点击position和当前播放position是同一个 
			if(playPosition == selectPosition) {//当前播放position和选中position是同一个
				isReplay = true;
			} else {
				songAdapter.setSelectPosition(position);
			}
		} else if(selectPosition == position) { //当前点击position和选中position是同一个
			songAdapter.setPlayPosition(position);
			isReplay = true;
		} else { //当前点击了一个新的position
			songAdapter.setSelectPosition(position);
		}
		
		if(isReplay == true) { //重新播放歌曲
			playMusicDeal(position); //播放歌曲处理
		}
		
		songAdapter.notifyDataSetChanged(); //刷新列表
	}
	
	//播放歌曲处理
	private void playMusicDeal(int position) {
		mpApp.setCurrentSongIndex(position); //设置新曲目
		showMediaInfo(false); //刷新各信息
	}
	
	//显示播放顺序按钮
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
	
	//显示播放模式按钮
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
	
	//根据播放状态显示各种信息
	private void showMediaInfo(boolean refreshSongList) {
		if(refreshSongList) { //根据需要刷新歌曲列表
			songAdapter.setPlayPosition(mpApp.getCurrentSongIndex()); //设置播放曲目
	    	songAdapter.notifyDataSetChanged(); //更新列表
		}
		showNoSongInfo(); //显示无歌曲信息
		showSongInfo(); //显示歌曲信息
    	showNoLyric(); //根据条件显示无歌词
    	showSeekBar(); //进度控制条显示,包含歌词显示
	}
	
	//无歌曲信息
	private void showNoSongInfo() {
		title.setText(""); //显示歌曲名称
		artist.setText(""); //显示歌手
		songInfo.setText(""); //显示歌曲信息
	}
	
	//显示歌曲信息
	private void showSongInfo() {
		if(mpApp.getTotalNum() > 0) { //有歌曲
			title.setText(mpApp.getTitleById(true, 0)); //显示歌曲名称
			artist.setText(mpApp.getArtistById(true, 0)); //显示歌手
			songInfo.setText(mpApp.getAlbumById(true, 0)); //显示歌曲信息
		}
	}
	
	//根据条件显示当前无歌词
	private void showNoLyric() {
		lyricFirst.setText(""); //显示无歌词
		lyricCurrent.setText("");
		lyricLast.setText("");
	}

	//进度控制条显示
	private void showSeekBar() {
		if(mpApp.getTotalNum() < 1) { //无歌曲
			seekBarMax = 100; //最大值默认为100
			seekBarProgress = 0; //当前位置默认为0
			seekBar.setMax(seekBarMax); //设置进度控制条最大值
			seekBar.setProgress(seekBarProgress); //设置进度
			totalTimer.setText(""); //总时间
			currentTimer.setText(""); //当前播放时间
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
			seekBarMax = Integer.parseInt(duration); //记录最大值和进度
			seekBar.setMax(seekBarMax); //设置进度控制条最大值
		} else {
			seekBarMax = 100000000; //记录最大值和进度
			seekBar.setMax(seekBarMax); //设置进度控制条最大值
		}
		if (seekBarProgress > seekBarMax) {
			seekBarProgress = 0;
		}
		seekBar.setProgress(seekBarProgress); //设置进度
		if (duration == null || duration.isEmpty()) {
			totalTimer.setText("error"); //总时间
		} else {
			totalTimer.setText(timerTransform(Integer.toString(seekBarMax))); //总时间
		}
		currentTimer.setText(timerTransform(Integer.toString(seekBarProgress))); //当前播放时间
    	showSeekBarPerTimer(); //进度控制条显示,包含歌词显示
		showLyric(); //歌词显示
	}
	
	//显示单行提示语
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