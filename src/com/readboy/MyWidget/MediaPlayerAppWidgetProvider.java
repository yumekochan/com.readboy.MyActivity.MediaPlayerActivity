package com.readboy.MyWidget;

import com.readboy.MyActivity.MediaPlayerActivity;
import com.readboy.MyMp3.MediaPlayerApplication;
import com.readboy.MyMp3.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.RemoteViews;

public class MediaPlayerAppWidgetProvider extends AppWidgetProvider{
	
	private static MediaPlayerApplication mpApp = null; //MediaPlayerApplication对象
	
	private static int playButtonShow = (-1); //当前显示播放按钮
	
	private static int pbMax = 0; //进度条最大值和当前进度
	private static int pbPos = 0;
	
	private static long lrcKey = (-1L); //歌词显示位置标志

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		System.out.println("mymp3 AppWidgetProvider onEnabled called!");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("mymp3 AppWidgetProvider onUpdate called!");
		
//		if(mpApp == null) { //未初始化
// 			mpApp = ((MediaPlayerApplication)context.getApplicationContext()); //Application对象初始化
// 		}
//		
//		//上一曲按钮
//		Intent intentPrevious = new Intent(MediaPlayerApplication.ACTION_PREVIOUS);
//		PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, 0);
//		//播放按钮
//		Intent intentPlay = new Intent(MediaPlayerApplication.ACTION_PLAY);
//		PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, 0);
//		//暂停按钮
//		Intent intentPause = new Intent(MediaPlayerApplication.ACTION_PAUSE);
//		PendingIntent pendingIntentPause = PendingIntent.getBroadcast(context, 0, intentPause, 0);
//		//下一曲按钮
//		Intent intentNext = new Intent(MediaPlayerApplication.ACTION_NEXT);			
//		PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, 0);
//		//背景处理(点击背景进入大图播放界面)
//		Intent intentBackground = new Intent(context, MediaPlayerActivity.class);
//		PendingIntent pendingIntentBackground = PendingIntent.getActivity(context, 0, intentBackground, 0);
//		//获取布局
//		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
//		//添加侦听
//		remoteViews.setOnClickPendingIntent(R.id.widgetPrevious, pendingIntentPrevious);
//		remoteViews.setOnClickPendingIntent(R.id.widgetPlay, pendingIntentPlay);
//		remoteViews.setOnClickPendingIntent(R.id.widgetPause, pendingIntentPause);
//		remoteViews.setOnClickPendingIntent(R.id.widgetNext, pendingIntentNext);
//		remoteViews.setOnClickPendingIntent(R.id.bk, pendingIntentBackground);
//		//更新部件
//		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
//		
//		showAppWidget(context); //显示部件信息
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if(mpApp == null) { //未初始化
 			mpApp = ((MediaPlayerApplication)context.getApplicationContext()); //Application对象初始化
 		}
		
		String action = intent.getAction(); //获取广播信息
		if(!action.equals(MediaPlayerApplication.ACTION_TIMER) && !action.equals(MediaPlayerApplication.ACTION_UPDATE_FOR_WIDGET)) {
			System.out.println("mymp3 AppWidgetProvider onReceive called! action = "+action);
		}
		if(action.equals(MediaPlayerApplication.ACTION_TIMER)) { //timer
			showProgressBarByTimer(context); //进度条显示
			showPlayButton(context); //播放按钮显示
		} else if(action.equals(MediaPlayerApplication.ACTION_UPDATE_FOR_WIDGET)) { //update
			updateDeal(context); //update
		} else if(action.equals(MediaPlayerApplication.ACTION_PREVIOUS)) { //上一曲
			mpApp.previousButtonDeal(); //上一曲处理
			showAppWidget(context); //显示部件信息
		} else if(action.equals(MediaPlayerApplication.ACTION_PLAY)) { //播放
			mpApp.playButtonDeal(); //播放处理
			showAppWidget(context); //显示部件信息
		} else if(action.equals(MediaPlayerApplication.ACTION_PAUSE)) { //暂停
			mpApp.pauseButtonDeal(); //暂停处理
			showAppWidget(context); //显示部件信息
		} else if(action.equals(MediaPlayerApplication.ACTION_NEXT)) { //下一曲
			mpApp.nextButtonDeal(); //下一曲处理
			showAppWidget(context); //显示部件信息
		} else if(action.equals(MediaPlayerApplication.ACTION_PLAYCOMPLETION)) { //播放完成
			showAppWidget(context); //显示部件信息
		} else if(action.equals(MediaPlayerApplication.ACTION_SHUTDOWN)) { //关机
			mpApp.stopButtonDeal(); //停止播放
			mpApp.saveInfo(); //保存信息
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
 		super.onDeleted(context, appWidgetIds);
 		System.out.println("mymp3 AppWidgetProvider onDeleted called!");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		System.out.println("mymp3 AppWidgetProvider onDisabled called!");
		
		if(mpApp == null) { //未初始化
 			mpApp = ((MediaPlayerApplication)context.getApplicationContext()); //Application对象初始化
 			mpApp.stopButtonDeal(); //停止播放
 			mpApp.saveInfo(); //保存信息
 		}
	}
	
	//update消息处理
	private void updateDeal(Context context) {
		//上一曲按钮
		Intent intentPrevious = new Intent(MediaPlayerApplication.ACTION_PREVIOUS);
		PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, 0);
		//播放按钮
		Intent intentPlay = new Intent(MediaPlayerApplication.ACTION_PLAY);
		PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, 0);
		//暂停按钮
		Intent intentPause = new Intent(MediaPlayerApplication.ACTION_PAUSE);
		PendingIntent pendingIntentPause = PendingIntent.getBroadcast(context, 0, intentPause, 0);
		//下一曲按钮
		Intent intentNext = new Intent(MediaPlayerApplication.ACTION_NEXT);			
		PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, 0);
		//背景处理(点击背景进入大图播放界面)
		Intent intentBackground = new Intent(context, MediaPlayerActivity.class);
		PendingIntent pendingIntentBackground = PendingIntent.getActivity(context, 0, intentBackground, 0);
		//获取布局
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
		//添加侦听
		remoteViews.setOnClickPendingIntent(R.id.widget_Previous, pendingIntentPrevious);
		remoteViews.setOnClickPendingIntent(R.id.widget_Play, pendingIntentPlay);
		remoteViews.setOnClickPendingIntent(R.id.widget_Pause, pendingIntentPause);
		remoteViews.setOnClickPendingIntent(R.id.widget_Next, pendingIntentNext);
		remoteViews.setOnClickPendingIntent(R.id.widget_bk, pendingIntentBackground);
		//更新部件
		ComponentName appWidgetId = new ComponentName(context, MediaPlayerAppWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
		
		//变量初始化
		playButtonShow = (-1); //当前显示播放按钮
		lrcKey = (-1L); //歌词显示位置标志
		//显示部件信息
		showAppWidget(context); 
		
	}
	
	private void showAppWidget(Context context) {
		//获取布局
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
		
		//显示歌名
		showSongName(remoteViews);
		//显示进度
		showProgressBar(remoteViews);
		//显示无歌词
		showNoLyric(remoteViews);
		
		//更新部件
		ComponentName appWidgetId = new ComponentName(context, MediaPlayerAppWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
	}
	
	//显示歌名
	private void showSongName(RemoteViews remoteViews) {
		if(mpApp.getTotalNum() < 1) { //无歌曲
			remoteViews.setCharSequence(R.id.widget_SongName, "setText", "请添加歌曲");
		} else { 
			remoteViews.setCharSequence(R.id.widget_SongName, "setText", mpApp.getTitleById(true, 0));
		}
	}
	
	//显示进度
	private void showProgressBar(RemoteViews remoteViews) {
		if(mpApp.getTotalNum() < 1) { //无歌曲
			pbMax = 100; //默认进度条最大值为100当前位置为0
			pbPos = 0;
		} else { //根据播放状态设置最大值和当前位置
			pbMax = Integer.parseInt(mpApp.getDurationById(true, 0));
			switch(mpApp.getPlayState()) {
	    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP:
	    		pbPos = 0;
	    		break;
	    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
	    	case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
	    		pbPos = mpApp.getMediaPlayerCurrentPosition();
	    		break;
	    	}
		}
		//显示进度条和歌词
		remoteViews.setProgressBar(R.id.widget_ProgressBar, pbMax, pbPos, false);
		showLyric(remoteViews); //歌词显示
	}
	
	//timer处理进度显示
	private void showProgressBarByTimer(Context context) {
		if(mpApp.getTotalNum() < 1) { //无歌曲
			return ;
		} 
		
		//获取布局
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
		
		//根据播放状态显示歌词
		int pbPosTmp = pbPos; //记录当前位置
		switch(mpApp.getPlayState()) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP: //音乐停止状态
			if(pbPos != 0) { //进度控制条不为0设置为0
				pbPos = 0;
				remoteViews.setInt(R.id.widget_ProgressBar, "setProgress", pbPos);
			}
			break;
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY:
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PAUSE:
			int currentTimerTmp = mpApp.getMediaPlayerCurrentPosition();
			if(currentTimerTmp < pbPos) {
				pbPos = pbMax;
			} else {
				pbPos = currentTimerTmp;
			}
			if(pbPos != pbPosTmp) {
				remoteViews.setInt(R.id.widget_ProgressBar, "setProgress", pbPos);
				showLyric(remoteViews); //歌词显示
			}
			break;
		}
		
		//更新部件
		ComponentName appWidgetId = new ComponentName(context, MediaPlayerAppWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
	}
	
	//歌词显示处理
	private void showLyric(RemoteViews remoteViews) {		
		if(mpApp.getLyricSize() < 1) { //无歌词信息
			return ;
		}
		
		long temp = 0L;
		for(long tmp : mpApp.getLyricKeySet()) { //查询时间
			if(tmp > pbPos) { //已到当前显示行的下一行
				if(lrcKey != temp) {
					lrcKey = temp;
					if(lrcKey == 0L) {
						remoteViews.setCharSequence(R.id.widget_lyric, "setText", mpApp.getLyricTitle());
					} else {
						remoteViews.setCharSequence(R.id.widget_lyric, "setText", mpApp.getLyricContentByKey(lrcKey));
					}
				}
				break;
			}
			temp = tmp;
		}
	}	
	
	//显示无歌词
	private void showNoLyric(RemoteViews remoteViews) {
		if(mpApp.getLyricSize() < 1) { //无歌词
			if(lrcKey == (-1)) {
				remoteViews.setCharSequence(R.id.widget_lyric, "setText", "当前无歌词");
			}
		}
	}
	
	//显示暂停播放按钮
	private void showPlayButton(Context context) {
		int newPlayButtonShow = 1; //默认为显示播放按钮
		if(mpApp.getPlayState() == MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY) {
			newPlayButtonShow = 0; //不显示播放按钮
		}
		if(playButtonShow != newPlayButtonShow) { //当前显示与新状态不同
			//获取布局
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
			
			//改变状态
			playButtonShow = newPlayButtonShow;
			if(playButtonShow == 1) { //显示播放按钮
				remoteViews.setViewVisibility(R.id.widget_Play, Button.VISIBLE);
				remoteViews.setViewVisibility(R.id.widget_Pause, Button.GONE);
			} else if(playButtonShow == 0) { //隐藏播放按钮
				remoteViews.setViewVisibility(R.id.widget_Play, Button.GONE);
				remoteViews.setViewVisibility(R.id.widget_Pause, Button.VISIBLE);
			}
			
			//更新部件
			ComponentName appWidgetId = new ComponentName(context, MediaPlayerAppWidgetProvider.class);
			AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
		}
	}
}
