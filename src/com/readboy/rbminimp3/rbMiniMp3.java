package com.readboy.rbminimp3;

import com.readboy.MyWindow.FloatMp3Window;
import com.readboy.rbminimp3.rbWindow.OnDismissListener;
import com.readboy.rbpopupservice.IrbPopupManager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class rbMiniMp3 extends Service{
	static final String TAG = "rbMiniMp3";
	
	private IrbPopupManager managerService;
	private FloatMp3Window mMiniMp3 = null;
	
	private ServiceConnection conn = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			managerService = null;
			Log.i(TAG, "onServiceDisconnected: managerService="+managerService);
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			managerService = IrbPopupManager.Stub.asInterface(service);
			Log.i(TAG, "onServiceConnected: managerService="+managerService);
			
			showFloatMp3Window();
		}
	};
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		//int ret = super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "rbMiniMp3 is running");
		
		if(managerService==null){
			Intent in = new Intent("com.readboy.rbpopupservice.rbPopupManager");
			Log.i(TAG, Boolean.toString(
    			bindService(in, conn, Service.BIND_AUTO_CREATE)));
		}else{
			showFloatMp3Window();
		}
		
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy(){
		if(managerService!=null){
			unbindService(conn);
		}
		Log.i(TAG, "onDestroy");
	}
	
	
	public void showFloatMp3Window(){
    	if(mMiniMp3==null){
    		mMiniMp3 = new FloatMp3Window(getApplicationContext(), managerService);
    		mMiniMp3.setOnDismissListener(new OnDismissListener() {

				public void onDismiss(rbWindow win) {
					mMiniMp3 = null;
				}
			});
		}else{
			Log.w(TAG, "mini FloatMp3 is already existed");
		}
    }
	
}
