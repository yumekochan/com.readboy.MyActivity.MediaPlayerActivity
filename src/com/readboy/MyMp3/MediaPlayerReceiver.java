package com.readboy.MyMp3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaPlayerReceiver extends BroadcastReceiver{
	
	//接口对象
	private OnReceiverBroadcastListener mReceiverListener = null;
	
	@Override //重写 onReceive,处理接收到的消息
	public void onReceive(Context context, Intent intent) {
		if(mReceiverListener != null){
			mReceiverListener.onReceiverDeal(context, intent);
		}
	}
	
	//设置侦听
	public void setOnReceiverBroadcastListener(OnReceiverBroadcastListener l){
		mReceiverListener = l;
	}
	
	//接口
	public interface OnReceiverBroadcastListener {
		void onReceiverDeal(Context context, Intent intent); //接收消息处理
	}
}
