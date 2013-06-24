package com.readboy.MyMp3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaPlayerReceiver extends BroadcastReceiver{
	
	//�ӿڶ���
	private OnReceiverBroadcastListener mReceiverListener = null;
	
	@Override //��д onReceive,������յ�����Ϣ
	public void onReceive(Context context, Intent intent) {
		if(mReceiverListener != null){
			mReceiverListener.onReceiverDeal(context, intent);
		}
	}
	
	//��������
	public void setOnReceiverBroadcastListener(OnReceiverBroadcastListener l){
		mReceiverListener = l;
	}
	
	//�ӿ�
	public interface OnReceiverBroadcastListener {
		void onReceiverDeal(Context context, Intent intent); //������Ϣ����
	}
}
