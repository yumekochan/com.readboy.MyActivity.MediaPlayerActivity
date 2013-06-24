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
	
	private Context context; //Context对象
	
	private MediaPlayerApplication mpApp = null; //Application对象
	
	private Toast toast = null; //提示语
	
	rbWindow pw = null;
	IrbPopupManager mManager;
	
	private Button volumeSilent = null; //静音按钮
	private Button volumeEnable = null; //声音按钮
	private SeekBar volumeSeekBar = null; //音量控制条
	private boolean volumeSeekBarProgressTimerShow = true; //手动控制SeekBar时,Timer不再控制SeekBar显示
	private int volumeSeekBarMax = 0; //SeekBar最大值和进度
	private int volumeSeekBarProgress = 0;
	
	private ListView songList = null; //歌曲列表
	private List<Map<String, String>> songInfoList = null; //歌曲信息列表
	private SongAdapter songAdapter = null; //列表适配器	
	
	private SeekBar playSeekBar = null; //播放进度控制条
	private boolean playSeekBarProgressTimerShow = true; //手动控制SeekBar时,Timer不再控制SeekBar显示
	private int playSeekBarMax = 0; //SeekBar最大值和进度
	private int playSeekBarProgress = 0;
	private boolean isPlaySeekBarProgressChange = false; //手动改变SeekBar的位置后,音乐要播放到相应位置
	
	private TextView currentTime = null; //当前时间文本
	private TextView totalTime = null; //总时间文本
	
	private Button previous = null; //上一曲
	private Button pause = null; //暂停
	private Button play = null; //播放
	private Button next = null; //下一曲
	
	private MediaPlayerReceiver activityReceiver = null; //广播接收对象
	private MediaPlayerReceiver activityReceiverNew = null; //广播接收对象
	
	private boolean playButtonShow = true; //当前显示播放按钮
	
	private boolean closeBtnClick = false; //关闭按钮Click标志
	
	private boolean finishFlag = false; //关机操作标志
	
	//构造函数
	public FloatMp3Window(Context context, IrbPopupManager manager) {
		this.context = context;
		mManager = manager;
		mpApp = (MediaPlayerApplication)context; //Application对象初始化
		init(); //初始化
	}
	
	public void setOnDismissListener(OnDismissListener listener){
		if(pw!=null){
			pw.setOnDismissListener(listener);
		}
	}
	
	private void init() {
		System.out.println("mymp3 FloatMp3Window init() called! begin");
		
		//获取布局
		LayoutInflater layoutInflaterTitle = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout frameLayoutTitle = (FrameLayout)layoutInflaterTitle.inflate(R.layout.window_titlelayout, null);
		
		//进入大图界面按钮
		Button entry = (Button)frameLayoutTitle.findViewById(R.id.window_entry);
		entry.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (closeBtnClick == false) {
					closeBtnClick = true;
					System.out.println("mymp3 FloatMp3Window init() entry button click!");
					//进入大图界面Activity
					Intent intent = new Intent(context, com.readboy.MyActivity.MediaPlayerActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					//退出简易界面窗口
					closeWindowDeal();
				}
			}
		});
		
		//刷新按钮
		Button refresh = (Button)frameLayoutTitle.findViewById(R.id.window_refresh);
		refresh.setOnClickListener(this);
		//删除按钮
		Button delete = (Button)frameLayoutTitle.findViewById(R.id.window_delete);
		delete.setOnClickListener(this);
		//关闭按钮
		Button close = (Button)frameLayoutTitle.findViewById(R.id.window_close);
		close.setOnClickListener(this);

		//获取布局
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout frameLayout = (FrameLayout)layoutInflater.inflate(R.layout.window_floatwindow, null);
		
		//静音按钮
		volumeSilent = (Button)frameLayout.findViewById(R.id.window_volumeSilent);
		volumeSilent.setOnClickListener(this);
		//声音按钮
		volumeEnable = (Button)frameLayout.findViewById(R.id.window_volumeEnable);
		volumeEnable.setOnClickListener(this);
		//音量控制条
		volumeSeekBar = (SeekBar)frameLayout.findViewById(R.id.window_volumnSeekBar);
		//SeekBar位置改变侦听
		
		volumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { //滑块滑动时调用
				if(fromUser) { //用户控制
					if(mpApp.setValume(progress)) {
						volumeSeekBarProgress = progress; //记录当前音量
						volumeSilentDeal(); //静音处理
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
		
		//歌曲列表
		songList = (ListView)frameLayout.findViewById(R.id.window_songList); 
		//歌曲信息列表
    	songInfoList = new ArrayList<Map<String, String>>();
		//列表适配器
    	songAdapter = new SongAdapter(context, songInfoList, R.layout.window_listviewitem, new String[]{"songName", "songArtist", "songTimer"}, new int[]{R.id.window_songName, R.id.window_songArtist, R.id.window_songTimer});
    	//列表关联适配器
    	songList.setAdapter(songAdapter);
    	songList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				songListClickDeal(parent, view, position, id); //点击处理
			}
    	});
    	//初始化列表
    	int songListInit = songListInit(false); 
    	
    	//播放进度控制条
		playSeekBar = (SeekBar)frameLayout.findViewById(R.id.window_playSeekBar);
		//SeekBar位置改变侦听
		playSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
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
		
		//当前时间文本
		currentTime = (TextView)frameLayout.findViewById(R.id.window_currentTime);
		//总时间文本
		totalTime = (TextView)frameLayout.findViewById(R.id.window_totalTime); 
		
		//上一曲
		previous = (Button)frameLayout.findViewById(R.id.window_previous);
		previous.setOnClickListener(this);
		//暂停
		pause = (Button)frameLayout.findViewById(R.id.window_pause);
		pause.setOnClickListener(this); 
		//播放
		play = (Button)frameLayout.findViewById(R.id.window_play);
		play.setOnClickListener(this);
		//下一曲
		next = (Button)frameLayout.findViewById(R.id.window_next);
		next.setOnClickListener(this);
		
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
    	context.registerReceiver(activityReceiver, intentFilter);
    	
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
    	context.registerReceiver(activityReceiverNew, intentFilterNew);
    	
    	//显示信息
    	if(songListInit == 0) {
    		showMediaInfo(true);
    	} else {
    		showMediaInfo(false);
    	}
    	
    	//音量显示
    	volumeSeekBarMax = mpApp.getMaxValume(); //获取最大音量
    	volumeSeekBarProgress = mpApp.getValume(); //获取当前音量
    	volumeSeekBar.setMax(volumeSeekBarMax); //设置最大音量
    	volumeSeekBar.setProgress(volumeSeekBarProgress); //设置当前音量
		
		//设置悬浮窗口
//		floatWnd = new FloatWindow<FrameLayout>(frameLayout, 458, 549);
    	pw = new rbWindow(context, mManager, "rbminimp3", 
    			frameLayout, frameLayoutTitle, 430, 529);
    	
    	System.out.println("mymp3 FloatMp3Window init() called! end");
	}

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.window_refresh: //刷新
			mpApp.stopButtonDeal(); //停止播放
			songListInit(true); //歌曲列表初始化
        	showMediaInfo(true); //刷新各信息
			break;
		case R.id.window_delete: //删除
			deleteButtonDeal();
			break;
		case R.id.window_close: //关闭
			if (closeBtnClick == false) {
				closeBtnClick = true;
				closeWindowDeal();
			}
			break;
		case R.id.window_volumeSilent: //静音
			mpApp.setMute(false);
			break;
		case R.id.window_volumeEnable: //音量
			mpApp.setMute(true);
			break;
		case R.id.window_previous: //上一曲
			previousButtonDeal();
			break;
		case R.id.window_pause: //暂停
			pauseButtonDeal();
			break;
		case R.id.window_play: //播放
			playButtonDeal();
			break;
		case R.id.window_next: //下一曲
			nextButtonDeal();
			break;
		}
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
			songAdapter.setSelectPosition((-1)); //设置无选中歌曲
			songAdapter.setPlayPosition(-1); //设置播无放歌曲
			songInfoList.clear(); //清除列表
			showMediaInfo(true); //刷新各信息
		} else if(action.equals(MediaPlayerApplication.ACTION_TIMER)) {
			timerDeal(); //时间处理
			if(volumeSeekBarProgressTimerShow) { //时间控制音量显示
				if(volumeSeekBarProgress != mpApp.getValume()) {
					volumeSeekBarProgress = mpApp.getValume(); //记录音量改变
					volumeSeekBar.setProgress(volumeSeekBarProgress); //显示改变
				}
			}
			volumeButtonShow(); //显示音量按钮
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
					//退出简易界面窗口
					closeWindowDeal();
				}
			}
		} else if(action.equals(MediaPlayerApplication.ACTION_BATTERY_LOW)) {
			System.out.println("mymp3 battery low! finishFlag = "+finishFlag);
			if(!finishFlag) {
				finishFlag = true;
				mpApp.stopButtonDeal(); //停止播放
				mpApp.saveInfo(); //保存信息
				//退出简易界面窗口
				closeWindowDeal();
			}
		}
	}
	
	//静音处理
	private void volumeSilentDeal() {
		if(volumeSeekBarProgress > 0) {
			mpApp.setMute(false);
		} else {
			mpApp.setMute(true);
		}
	}
	
	//音量按钮显示
	private void volumeButtonShow() {
		if(volumeSeekBarProgress <= 0) { //静音
			volumeEnable.setVisibility(Button.GONE);
			volumeSilent.setVisibility(Button.VISIBLE);
		} else { //正常音量
			volumeEnable.setVisibility(Button.VISIBLE);
			volumeSilent.setVisibility(Button.GONE);
		}
	}
	
	//时间转化,毫秒转化成分秒
	private String timerTransform(String millisecond) {
		if(millisecond == null || millisecond.isEmpty()) {
			return "";
		}
		String timer = null;
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
    			System.out.println("mymp3 FloatMp3Window search no mp3 file!");
    			showOneLineTip("末搜索到mp3文件！", (-1), (-1));
    		}
    		return 1;
    	}
    	for(int i = 0; i < totalNum; i ++) {
    		String title = mpApp.getTitleById(false, i); //获取歌名
    		String artist = mpApp.getArtistById(false, i); //获取歌手
    		String duration = mpApp.getDurationById(false, i); //获取总时间,单位是ms
    		duration = timerTransform(duration); //时间处理,转化成分:秒的形式
    		
    		Map<String, String> tmp = new HashMap<String, String>(); //添加新条目
    		tmp.put("songName", title);
    		tmp.put("songArtist", artist);
    		tmp.put("songTimer", duration);
    		songInfoList.add(tmp);
    	}
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
	
	//删除按钮处理
	private void deleteButtonDeal() {
		int selectPosition = songAdapter.getSelectPosition();
		int totalNum = songAdapter.getCount();
		
		if(totalNum < 1 || selectPosition < 0 || selectPosition > (totalNum-1)) {
			return ; //无歌曲或无选中项不处理
		}
		
		if(0 == mpApp.deleteSongByIndex(songAdapter.getSelectPosition())) { //从信息中删除歌曲
			songInfoList.remove(songAdapter.getSelectPosition()); //删除列表中的信息
			songAdapter.setSelectPosition((-1)); //设置无选中歌曲
			showMediaInfo(true); //刷新各信息
		} else {
			showOneLineTip("搜索中，删除失败，请别频繁刷新，等搜索完成再执行删除操作！", (-1), (-1));
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
	
	//根据timer显示进度控制条
	private void showSeekBarPerTimer() {
		if(playSeekBarProgressTimerShow == false) {
			return ; //非时间控制显示或无歌曲
		}

		if(mpApp.getTotalNum() < 1) {
			if(playSeekBar.getProgress() != 0) { //进度控制条不为0设置为0
				playSeekBar.setProgress(0);
			}
			return ; //无歌曲
		}
		
		switch(mpApp.getPlayState()) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP: //音乐停止状态
			if(playSeekBar.getProgress() != 0) { //进度控制条不为0设置为0
				playSeekBarProgress = 0;
				playSeekBar.setProgress(0);
				currentTime.setText("0:00"); //当前播放时间
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
				currentTime.setText(timerTransform(Integer.toString(playSeekBarProgress))); //当前播放时间
			}
			break;
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
				if(progress != playSeekBarProgress) {
					playSeekBarProgress = progress; //改变位置
					isPlaySeekBarProgressChange = true; //标志位置改变有效
				}
				break;
			}
		}
	}
	private void startTrackDeal(SeekBar seekBar) {
		playSeekBarProgressTimerShow = false; //用户控制进度过程中Timer不显示
	}
	private void stopTrackDeal(SeekBar seekBar) {
		playSeekBarProgressTimerShow = true; //用户控制释放,Timer显示
		if(isPlaySeekBarProgressChange == true) { //位置改变有效
			isPlaySeekBarProgressChange = false;
			mpApp.seekTo(playSeekBarProgress); //查找歌曲对应位置并播放
		}
	}
	
	//歌曲播放完处理
	private void playCompletionDeal() {
		showMediaInfo(true); //刷新各信息
	}
	
	//根据播放状态显示各种信息
	private void showMediaInfo(boolean refreshSongList) {
		if(refreshSongList) { //根据需要刷新歌曲列表
			songAdapter.setPlayPosition(mpApp.getCurrentSongIndex()); //设置播放曲目
	    	songAdapter.notifyDataSetChanged(); //更新列表
		}
    	showSeekBar(); //进度控制条显示,包含歌词显示
	}

	//进度控制条显示
	private void showSeekBar() {
		if(mpApp.getTotalNum() < 1) { //无歌曲
			playSeekBarMax = 100; //最大值默认为100
			playSeekBarProgress = 0; //当前位置默认为0
			playSeekBar.setMax(playSeekBarMax); //设置进度控制条最大值
			playSeekBar.setProgress(playSeekBarProgress); //设置进度
			totalTime.setText(""); //总时间
			currentTime.setText(""); //当前播放时间
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
			playSeekBarMax = Integer.parseInt(duration); //记录最大值和进度
			playSeekBar.setMax(playSeekBarMax); //设置进度控制条最大值
		} else {
			playSeekBarMax = 100000000; //记录最大值和进度
			playSeekBar.setMax(playSeekBarMax); //设置进度控制条最大值
		}
		
		if (playSeekBarProgress > playSeekBarMax) {
			playSeekBarProgress = 0;
		}
		playSeekBar.setProgress(playSeekBarProgress); //设置进度
		if (duration == null || duration.isEmpty()) {
			totalTime.setText("error"); //总时间
		} else {
			totalTime.setText(timerTransform(Integer.toString(playSeekBarMax))); //总时间
		}
		currentTime.setText(timerTransform(Integer.toString(playSeekBarProgress))); //当前播放时间
		showSeekBarPerTimer(); //进度控制条显示,包含歌词显示
	}
	
	//关闭窗口处理
	private void closeWindowDeal() {
		System.out.println("mymp3 FloatMp3Window close button click! begin");
		
		//注销广播接收器
		if(activityReceiver != null) { 
    		context.unregisterReceiver(activityReceiver);
    		activityReceiver = null;
    	}
    	
		if(activityReceiverNew != null) { //注销广播接收器
			context.unregisterReceiver(activityReceiverNew);
    		activityReceiverNew = null;
    	}
		
		//保存信息
    	mpApp.saveInfo(); 
    	
    	pw.dismiss();
		
		System.out.println("mymp3 FloatMp3Window close button click! end");
	}
	
	//显示单行提示语
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
