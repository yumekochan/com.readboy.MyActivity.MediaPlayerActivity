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
	
	private static MediaPlayerApplication mpApp = null; //MediaPlayerApplication����
	
	private static int playButtonShow = (-1); //��ǰ��ʾ���Ű�ť
	
	private static int pbMax = 0; //���������ֵ�͵�ǰ����
	private static int pbPos = 0;
	
	private static long lrcKey = (-1L); //�����ʾλ�ñ�־

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		System.out.println("mymp3 AppWidgetProvider onEnabled called!");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("mymp3 AppWidgetProvider onUpdate called!");
		
//		if(mpApp == null) { //δ��ʼ��
// 			mpApp = ((MediaPlayerApplication)context.getApplicationContext()); //Application�����ʼ��
// 		}
//		
//		//��һ����ť
//		Intent intentPrevious = new Intent(MediaPlayerApplication.ACTION_PREVIOUS);
//		PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, 0);
//		//���Ű�ť
//		Intent intentPlay = new Intent(MediaPlayerApplication.ACTION_PLAY);
//		PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, 0);
//		//��ͣ��ť
//		Intent intentPause = new Intent(MediaPlayerApplication.ACTION_PAUSE);
//		PendingIntent pendingIntentPause = PendingIntent.getBroadcast(context, 0, intentPause, 0);
//		//��һ����ť
//		Intent intentNext = new Intent(MediaPlayerApplication.ACTION_NEXT);			
//		PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, 0);
//		//��������(������������ͼ���Ž���)
//		Intent intentBackground = new Intent(context, MediaPlayerActivity.class);
//		PendingIntent pendingIntentBackground = PendingIntent.getActivity(context, 0, intentBackground, 0);
//		//��ȡ����
//		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
//		//�������
//		remoteViews.setOnClickPendingIntent(R.id.widgetPrevious, pendingIntentPrevious);
//		remoteViews.setOnClickPendingIntent(R.id.widgetPlay, pendingIntentPlay);
//		remoteViews.setOnClickPendingIntent(R.id.widgetPause, pendingIntentPause);
//		remoteViews.setOnClickPendingIntent(R.id.widgetNext, pendingIntentNext);
//		remoteViews.setOnClickPendingIntent(R.id.bk, pendingIntentBackground);
//		//���²���
//		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
//		
//		showAppWidget(context); //��ʾ������Ϣ
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if(mpApp == null) { //δ��ʼ��
 			mpApp = ((MediaPlayerApplication)context.getApplicationContext()); //Application�����ʼ��
 		}
		
		String action = intent.getAction(); //��ȡ�㲥��Ϣ
		if(!action.equals(MediaPlayerApplication.ACTION_TIMER) && !action.equals(MediaPlayerApplication.ACTION_UPDATE_FOR_WIDGET)) {
			System.out.println("mymp3 AppWidgetProvider onReceive called! action = "+action);
		}
		if(action.equals(MediaPlayerApplication.ACTION_TIMER)) { //timer
			showProgressBarByTimer(context); //��������ʾ
			showPlayButton(context); //���Ű�ť��ʾ
		} else if(action.equals(MediaPlayerApplication.ACTION_UPDATE_FOR_WIDGET)) { //update
			updateDeal(context); //update
		} else if(action.equals(MediaPlayerApplication.ACTION_PREVIOUS)) { //��һ��
			mpApp.previousButtonDeal(); //��һ������
			showAppWidget(context); //��ʾ������Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_PLAY)) { //����
			mpApp.playButtonDeal(); //���Ŵ���
			showAppWidget(context); //��ʾ������Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_PAUSE)) { //��ͣ
			mpApp.pauseButtonDeal(); //��ͣ����
			showAppWidget(context); //��ʾ������Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_NEXT)) { //��һ��
			mpApp.nextButtonDeal(); //��һ������
			showAppWidget(context); //��ʾ������Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_PLAYCOMPLETION)) { //�������
			showAppWidget(context); //��ʾ������Ϣ
		} else if(action.equals(MediaPlayerApplication.ACTION_SHUTDOWN)) { //�ػ�
			mpApp.stopButtonDeal(); //ֹͣ����
			mpApp.saveInfo(); //������Ϣ
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
		
		if(mpApp == null) { //δ��ʼ��
 			mpApp = ((MediaPlayerApplication)context.getApplicationContext()); //Application�����ʼ��
 			mpApp.stopButtonDeal(); //ֹͣ����
 			mpApp.saveInfo(); //������Ϣ
 		}
	}
	
	//update��Ϣ����
	private void updateDeal(Context context) {
		//��һ����ť
		Intent intentPrevious = new Intent(MediaPlayerApplication.ACTION_PREVIOUS);
		PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, 0);
		//���Ű�ť
		Intent intentPlay = new Intent(MediaPlayerApplication.ACTION_PLAY);
		PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, 0);
		//��ͣ��ť
		Intent intentPause = new Intent(MediaPlayerApplication.ACTION_PAUSE);
		PendingIntent pendingIntentPause = PendingIntent.getBroadcast(context, 0, intentPause, 0);
		//��һ����ť
		Intent intentNext = new Intent(MediaPlayerApplication.ACTION_NEXT);			
		PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, 0);
		//��������(������������ͼ���Ž���)
		Intent intentBackground = new Intent(context, MediaPlayerActivity.class);
		PendingIntent pendingIntentBackground = PendingIntent.getActivity(context, 0, intentBackground, 0);
		//��ȡ����
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
		//�������
		remoteViews.setOnClickPendingIntent(R.id.widget_Previous, pendingIntentPrevious);
		remoteViews.setOnClickPendingIntent(R.id.widget_Play, pendingIntentPlay);
		remoteViews.setOnClickPendingIntent(R.id.widget_Pause, pendingIntentPause);
		remoteViews.setOnClickPendingIntent(R.id.widget_Next, pendingIntentNext);
		remoteViews.setOnClickPendingIntent(R.id.widget_bk, pendingIntentBackground);
		//���²���
		ComponentName appWidgetId = new ComponentName(context, MediaPlayerAppWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
		
		//������ʼ��
		playButtonShow = (-1); //��ǰ��ʾ���Ű�ť
		lrcKey = (-1L); //�����ʾλ�ñ�־
		//��ʾ������Ϣ
		showAppWidget(context); 
		
	}
	
	private void showAppWidget(Context context) {
		//��ȡ����
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
		
		//��ʾ����
		showSongName(remoteViews);
		//��ʾ����
		showProgressBar(remoteViews);
		//��ʾ�޸��
		showNoLyric(remoteViews);
		
		//���²���
		ComponentName appWidgetId = new ComponentName(context, MediaPlayerAppWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
	}
	
	//��ʾ����
	private void showSongName(RemoteViews remoteViews) {
		if(mpApp.getTotalNum() < 1) { //�޸���
			remoteViews.setCharSequence(R.id.widget_SongName, "setText", "����Ӹ���");
		} else { 
			remoteViews.setCharSequence(R.id.widget_SongName, "setText", mpApp.getTitleById(true, 0));
		}
	}
	
	//��ʾ����
	private void showProgressBar(RemoteViews remoteViews) {
		if(mpApp.getTotalNum() < 1) { //�޸���
			pbMax = 100; //Ĭ�Ͻ��������ֵΪ100��ǰλ��Ϊ0
			pbPos = 0;
		} else { //���ݲ���״̬�������ֵ�͵�ǰλ��
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
		//��ʾ�������͸��
		remoteViews.setProgressBar(R.id.widget_ProgressBar, pbMax, pbPos, false);
		showLyric(remoteViews); //�����ʾ
	}
	
	//timer���������ʾ
	private void showProgressBarByTimer(Context context) {
		if(mpApp.getTotalNum() < 1) { //�޸���
			return ;
		} 
		
		//��ȡ����
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
		
		//���ݲ���״̬��ʾ���
		int pbPosTmp = pbPos; //��¼��ǰλ��
		switch(mpApp.getPlayState()) {
		case MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_STOP: //����ֹͣ״̬
			if(pbPos != 0) { //���ȿ�������Ϊ0����Ϊ0
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
				showLyric(remoteViews); //�����ʾ
			}
			break;
		}
		
		//���²���
		ComponentName appWidgetId = new ComponentName(context, MediaPlayerAppWidgetProvider.class);
		AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
	}
	
	//�����ʾ����
	private void showLyric(RemoteViews remoteViews) {		
		if(mpApp.getLyricSize() < 1) { //�޸����Ϣ
			return ;
		}
		
		long temp = 0L;
		for(long tmp : mpApp.getLyricKeySet()) { //��ѯʱ��
			if(tmp > pbPos) { //�ѵ���ǰ��ʾ�е���һ��
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
	
	//��ʾ�޸��
	private void showNoLyric(RemoteViews remoteViews) {
		if(mpApp.getLyricSize() < 1) { //�޸��
			if(lrcKey == (-1)) {
				remoteViews.setCharSequence(R.id.widget_lyric, "setText", "��ǰ�޸��");
			}
		}
	}
	
	//��ʾ��ͣ���Ű�ť
	private void showPlayButton(Context context) {
		int newPlayButtonShow = 1; //Ĭ��Ϊ��ʾ���Ű�ť
		if(mpApp.getPlayState() == MediaPlayerApplication.MEDIAPLAYER_PLAYSTATE_PLAY) {
			newPlayButtonShow = 0; //����ʾ���Ű�ť
		}
		if(playButtonShow != newPlayButtonShow) { //��ǰ��ʾ����״̬��ͬ
			//��ȡ����
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_layout);
			
			//�ı�״̬
			playButtonShow = newPlayButtonShow;
			if(playButtonShow == 1) { //��ʾ���Ű�ť
				remoteViews.setViewVisibility(R.id.widget_Play, Button.VISIBLE);
				remoteViews.setViewVisibility(R.id.widget_Pause, Button.GONE);
			} else if(playButtonShow == 0) { //���ز��Ű�ť
				remoteViews.setViewVisibility(R.id.widget_Play, Button.GONE);
				remoteViews.setViewVisibility(R.id.widget_Pause, Button.VISIBLE);
			}
			
			//���²���
			ComponentName appWidgetId = new ComponentName(context, MediaPlayerAppWidgetProvider.class);
			AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
		}
	}
}
