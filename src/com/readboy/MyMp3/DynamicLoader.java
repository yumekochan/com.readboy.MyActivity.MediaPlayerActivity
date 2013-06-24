package com.readboy.MyMp3;

public class DynamicLoader implements Runnable{
	
	private Thread thread = null;
	private OnDynamicLoaderListener mDynLdLsnr = null;
	
	public DynamicLoader() {
		thread = new Thread(this);  //
		thread.start();              //
	}
	
	public interface OnDynamicLoaderListener {
		void onDynLdDeal();
	}
	
	public void setOnDynamicLoadListener(OnDynamicLoaderListener l) {
		mDynLdLsnr = l;
	}
	
	public void run() {
		if(mDynLdLsnr != null) {
			mDynLdLsnr.onDynLdDeal();
		}
	}
}
