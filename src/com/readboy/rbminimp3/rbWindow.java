package com.readboy.rbminimp3;

import java.lang.reflect.Field;

import com.readboy.MyMp3.R;
import com.readboy.rbpopupservice.IrbPopupManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;



public class rbWindow {
	private static final String TAG = "rbWindow";
  
    public static final int RBPOPUP_PADDING_LEFT = 15;
    public static final int RBPOPUP_PADDING_RIGHT = 15;
    public static final int RBPOPUP_PADDING_TOP = 13;
    public static final int RBPOPUP_PADDING_BOTTOM = 13;
    
    public static final int RBPOPUP_TITLE_HEIGHT = 48;

    private Context mContext;
    private WindowManager mWindowManager;
    private Display mDisplay;
    
    private boolean mIsShowing;

    private rbPopupContainer mPopupView;
    private ViewGroup mTitleContainer;
    private View mTitleView;
    private View mContentView;
    
	private int mWidth;
	private int mHeight;
	
	private String mWinTag;
	//private int mId = -1;
	private OnDismissListener mOnDismissListener;

	private IrbPopupManager mPopupManager;
	private rbWindowReceiver mReceiver;
   
    
    public rbWindow(Context context, IrbPopupManager manager, String tag,
    		View contentView, View titleView, 
    		int width, int height) {
    	mContext = context;
    	mWindowManager = (WindowManager) mContext.
    			getSystemService(Context.WINDOW_SERVICE);
    	mDisplay = mWindowManager.getDefaultDisplay();
    	
    	mPopupManager = manager;
    	mWinTag = tag;
    	
        setContentView(contentView);
        setTitleView(titleView);
		mWidth = width+RBPOPUP_PADDING_LEFT+RBPOPUP_PADDING_RIGHT;
		mHeight = height+RBPOPUP_PADDING_TOP+RBPOPUP_PADDING_BOTTOM;
		
		show();
		
		mReceiver = new rbWindowReceiver();
		mContext.registerReceiver(mReceiver, new IntentFilter("rbPopupClient"));
		
		
		try {
			/*
			if(mPopupManager.checkIsLostAllFocus()){
				mPopupManager.enableAllFocus();
			}else{
				mPopupManager.killTopWindowFocus();
			}
			*/
			mPopupManager.killTopWindowFocus();
			
    		//mId = mPopupManager.getAvailableWindowId();
    		//Log.i(TAG, "mId="+mId);
    		mPopupManager.addWindow(mWinTag);
    		
    		WindowManager.LayoutParams p = 
    				(WindowManager.LayoutParams)mPopupView.getLayoutParams();
    		Log.i(TAG, mWinTag+" window pos="+p.x+", "+p.y);
    		mPopupManager.updateWindowPos(mWinTag, p.x, p.y);
    		
		} catch (RemoteException e) {
		}
    	
    }
    
    
    
    public void setContentView(View contentView) {
        if (isShowing()) {
            return;
        }

        mContentView = contentView;
    }
    
    
    
    public void setTitleView(View titleView){
    	if (isShowing()) {
            return;
        }

        mTitleView = titleView;
    }


    public boolean isShowing() {
        return mIsShowing;
    }
    
    

    private WindowManager.LayoutParams createPopupLayout() {
        // generates the layout parameters for the drop down
        // we want a fixed size view located at the bottom left of the anchor
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        // these gravity settings put the view at the top left corner of the
        // screen. The view is then positioned to the appropriate location
        // by setting the x and y offsets to match the anchor's bottom
        // left corner
        p.gravity = Gravity.LEFT | Gravity.TOP;
        p.width = mWidth;
        p.height = mHeight;
        
        p.format = PixelFormat.RGBA_8888;//TRANSLUCENT;
        
        p.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
        		  WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
        		  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        
        p.type = WindowManager.LayoutParams.TYPE_PHONE;
        
        p.windowAnimations = R.style.rbpopup_animation;
        //p.token = token;
        p.setTitle("rbWindow:"+mWinTag/* + Integer.toHexString(hashCode())*/);

        return p;
    }


    private void preparePopup(WindowManager.LayoutParams p) {
        if (mContentView == null || mContext == null || mWindowManager == null) {
            throw new IllegalStateException("You must specify a valid content view by "
                    + "calling setContentView() before attempting to show the popup.");
        }
        
        mPopupView = new rbPopupContainer(mContext);
    	mPopupView.setBackgroundResource(R.drawable.frame);
    	
    	//add title
    	rbPopupContainer.LayoutParams l_title = new rbPopupContainer.LayoutParams(
    			//ViewGroup.LayoutParams.MATCH_PARENT, RBPOPUP_TITLE_HEIGHT);
    			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    	mTitleContainer = new LinearLayout(mContext);
    	//mTitleContainer.setBackgroundColor(0xffff0000);
    	if(mTitleView!=null){
    		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
    				ViewGroup.LayoutParams.MATCH_PARENT, 
    				ViewGroup.LayoutParams.MATCH_PARENT);
    		mTitleContainer.addView(mTitleView, ll);
    	}
    	mPopupView.addChild(mTitleContainer, l_title);
        
    	//add content
        ViewGroup.LayoutParams l_old = mContentView.getLayoutParams();
        int height = ViewGroup.LayoutParams.MATCH_PARENT; 
        if(l_old!=null){
        	height = l_old.height;
        }
        rbPopupContainer.LayoutParams l_content = new rbPopupContainer.LayoutParams(
        		ViewGroup.LayoutParams.MATCH_PARENT, height); 
        //mContentView.setBackgroundColor(0xff00ff00);
        mPopupView.addChild(mContentView, l_content);
        mPopupView.setPadding(RBPOPUP_PADDING_LEFT, RBPOPUP_PADDING_TOP, 
        		RBPOPUP_PADDING_RIGHT, RBPOPUP_PADDING_BOTTOM);

        //make title draggable
        mTitleContainer.setOnTouchListener(new View.OnTouchListener() {
        	float lastX, lastY;
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction();
				
				float x = event.getX();
				float y = event.getY();
				float rx = event.getRawX();
				float ry = event.getRawY();
				
				switch(action){
				case MotionEvent.ACTION_DOWN:{
					lastX = x;
					lastY = y;
					break;
				}
				case MotionEvent.ACTION_MOVE:{
					int nx = (int) (rx - lastX - RBPOPUP_PADDING_LEFT);
					int ny = (int) (ry - lastY - RBPOPUP_PADDING_TOP);
					update(nx, ny);
					break;
				}
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					adjustWindowPos();
					break;
				}
				return true;
			}
		});
    }
    
    public void show() {
        if (isShowing() || mContentView == null) {
            return;
        }

        mIsShowing = true;

        WindowManager.LayoutParams p = createPopupLayout();
        
       
        preparePopup(p);
        
        Point pt = new Point();
        //mDisplay.getSize(pt);
        generateWindowPos(pt);
        p.gravity = Gravity.TOP | Gravity.LEFT;
        p.x = pt.x;
        p.y = pt.y;
        
        Log.i(TAG, mWinTag+" window pos="+p.x+", "+p.y);
        //p.x = (int)(pt.x*Math.random()+0.5f)-mWidth/2;
        //p.y = (int)((pt.y-getStatusBarHeight()-mHeight/2)*Math.random()+0.5f);

        if (mContext != null) {
            p.packageName = mContext.getPackageName();
        }
        mWindowManager.addView(mPopupView, p);
    }

	public void update(int nx, int ny) {
		if (isShowing() && mPopupView != null) {
			WindowManager.LayoutParams params = (WindowManager.LayoutParams)
					mPopupView.getLayoutParams();
			params.x = nx;
			params.y = ny;
			mWindowManager.updateViewLayout(mPopupView, params);
			try{
				mPopupManager.updateWindowPos(mWinTag, nx, ny);
			}catch (RemoteException e) {
			}
		}
	}

    
	public void setWindowFocusable(boolean focusable){
		Log.i(TAG, "setWindowFocusable: mPopupView="+mPopupView
				+", focusable="+focusable);
		if(isShowing() && mPopupView!=null){
			WindowManager.LayoutParams params = (WindowManager.LayoutParams)
					mPopupView.getLayoutParams();
			if(focusable){
				params.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			}else{
				params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			}
			mWindowManager.updateViewLayout(mPopupView, params);
		}
	}
    
    
    public void dismiss() {
        if (isShowing() && mPopupView != null) {
            mIsShowing = false;

            try {
                mWindowManager.removeView(mPopupView);                
            } finally {
                if (mPopupView != mContentView && mPopupView instanceof ViewGroup) {
                    ((ViewGroup) mPopupView).removeView(mContentView);
                }
                mPopupView = null;
                
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(this);
                }
            }
            
            try{
                mPopupManager.removeWindow(mWinTag);
                mPopupManager.setTopWindowFocus();
            }catch(RemoteException e){
            	
            }
            
            mContext.unregisterReceiver(mReceiver);
        }
        Log.i(TAG, "dismissed");
    }

   
    
    

    
    
    

    

    
    private void adjustWindowPos(){
    	if (isShowing() && mPopupView != null) {
    	
	    	WindowManager.LayoutParams p = 
	    			(WindowManager.LayoutParams) mPopupView.getLayoutParams();
	    	
	    	Point pt = new Point();
	    	mDisplay.getSize(pt);
	    	//Log.i(TAG, "w="+pt.x+", h="+pt.y+", x="+p.x+", y="+p.y);
	    	
	    	if(p.x<-mWidth/2){
	    		p.x = -mWidth/2;
	    	}else if(p.x>pt.x-mWidth/2){
	    		p.x = pt.x-mWidth/2;
	    	}
	    	
	    	int statusbar_height = getStatusBarHeight();
	    	//Log.i(TAG, "statusbar_height="+statusbar_height);
	    	if(p.y<-RBPOPUP_PADDING_TOP){
	    		p.y = -RBPOPUP_PADDING_TOP;
	    	}else if(p.y>pt.y-mHeight/3-statusbar_height){
	    		p.y = pt.y-mHeight/3-statusbar_height;
	    	}
	
	    	//Log.i(TAG, "x="+p.x+", y="+p.y);
	    	
	    	mWindowManager.updateViewLayout(mPopupView, p);
	    	try{
	    		mPopupManager.updateWindowPos(mWinTag, p.x, p.y);
	    	}catch (RemoteException e) {
			}
    	}
    }
    
    private void generateWindowPos(Point pos){
    	int x = 0, y = 0;
        mDisplay.getSize(pos);//pos中存放屏幕高宽，包括systembar
        
        Log.i(TAG, " mDisplay.getSize="+pos);
        
        int toppos = -1;
        
        try{
        	toppos = mPopupManager.getTopWindowPos(mWinTag);
        }catch(RemoteException e){
        	
        }

    	int statusbar_height = getStatusBarHeight();
        if(toppos==-1){
        	x = (pos.x-mWidth)/2;
            y = (pos.y-mHeight-statusbar_height)/2;
    		pos.x = x;
    		pos.y = y;
    		return;
    	}

    	Point p = new Point(toppos&0xffff, (toppos>>16)&0xffff);
    	Log.i(TAG, "getTopWindowPos="+p);
    	
    	
    	//check negative num
    	if(p.x>pos.x){
    		p.x = p.x-65536;
    	}
    	if(p.y>pos.y){
    		p.y = p.y-65536;
    	}
    	
    	//adjust range
    	if(p.x<-mWidth/2){
    		p.x = -mWidth/2;
    	}else if(p.x>pos.x-mWidth/2){
    		p.x = pos.x-mWidth/2;
    	}
    	
    	if(p.y<-RBPOPUP_PADDING_TOP){
    		p.y = -RBPOPUP_PADDING_TOP;
    	}else if(p.y>pos.y-mHeight/3-statusbar_height){
    		p.y = pos.y-mHeight/3-statusbar_height;
    	}
    	
    	//int edge_left = -mWidth/2;
    	int edge_top = 0;
    	int edge_right = pos.x-mWidth/2;
    	int edge_bottom = pos.y-statusbar_height-mHeight/3;
    	
    	//boolean toLeft = p.x-RBPOPUP_TITLE_HEIGHT>=edge_left;
    	boolean toTop = p.y-RBPOPUP_TITLE_HEIGHT>=edge_top;
    	boolean toRight = p.x+RBPOPUP_TITLE_HEIGHT<=edge_right;
    	boolean toBottom = p.y+RBPOPUP_TITLE_HEIGHT<=edge_bottom;
    	
    	//默认向右
    	if(toRight){//继续向右
    		x = p.x+RBPOPUP_TITLE_HEIGHT;
    		//默认向下
    		if(toBottom){
    			y = p.y+RBPOPUP_TITLE_HEIGHT;
    		}else{//转向右上
    			y = p.y-RBPOPUP_TITLE_HEIGHT;
    		}
    	}else{//无法向右
    		x = 0;
    		int roll = (int)(Math.random()*2);
    		if(roll == 0){
    			y = 0;
    		}else{
    			y = edge_bottom;
    		}
    	}
    	pos.x = x;
    	pos.y = y;
    }
    
    private int getStatusBarHeight(){
    	Class<?> c = null;
    	Object obj = null;
    	Field field = null;
    	int x = 0, sbar = 0;
    	try {
    	    c = Class.forName("com.android.internal.R$dimen");
    	    obj = c.newInstance();
    	    field = c.getField("status_bar_height");
    	    x = Integer.parseInt(field.get(obj).toString());
    	    sbar = mContext.getResources().getDimensionPixelSize(x);
    	} catch (Exception e1) {
    	    Log.e(TAG, "get status bar height fail");
    	    e1.printStackTrace();
    	    sbar = 48;
    	} 
    	
    	return sbar;
    }
    
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    /**
     * Listener that is called when this popup window is dismissed.
     */
    public interface OnDismissListener {
        /**
         * Called when this popup window is dismissed.
         */
        public void onDismiss(rbWindow win);
    }
    

    
    class rbPopupContainer extends FrameLayout {
        static final String TAG = "rbPopupViewContainer";
        
        private boolean isFocused = true;
        private LinearLayout mMainView;
        private View mDimView;
        private Context mContext;

        public rbPopupContainer(Context context) {
            super(context);
            init(context);
        }
        
        public rbPopupContainer(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }
        
        private void init(Context context){
        	setMotionEventSplittingEnabled(false);
        	
        	mContext = context;
        	mMainView = new LinearLayout(mContext);
        	this.addView(mMainView, new FrameLayout.LayoutParams(
        		FrameLayout.LayoutParams.MATCH_PARENT, 
        		FrameLayout.LayoutParams.MATCH_PARENT));
        	mMainView.setOrientation(LinearLayout.VERTICAL);
        	
        	mDimView = new View(mContext);
    		mDimView.setBackgroundColor(0x44000000);
    		this.addView(mDimView, new FrameLayout.LayoutParams(
        		FrameLayout.LayoutParams.MATCH_PARENT, 
        		FrameLayout.LayoutParams.MATCH_PARENT));
    		mDimView.setVisibility(View.GONE);
    		
        	//setFocus();
        }
        
        public void killFocus(){
        	if(isFocused==true){
	        	if(mDimView==null){
	        		mDimView = new View(mContext);
	        		mDimView.setBackgroundColor(0x44000000);
	        		this.addView(mDimView, new FrameLayout.LayoutParams(
		        		FrameLayout.LayoutParams.MATCH_PARENT, 
		        		FrameLayout.LayoutParams.MATCH_PARENT));
	        	}
	        	mDimView.setVisibility(View.VISIBLE);
	        	isFocused = false;
	        	setWindowFocusable(false);
        	}
        }
        
        public void setFocus(){
        	if(isFocused==false){
	        	if(mDimView!=null){
	        		mDimView.setVisibility(View.GONE);
	        	}
	        	isFocused = true;
	        	setWindowFocusable(true);
        	}
        }
        
        public void addChild(View child, LayoutParams params){
        	mMainView.addView(child, params);
        }
        
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
        	if(!isShowing() || mPopupView==null){
        		return super.dispatchKeyEvent(event);
        	}
        	
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (getKeyDispatcherState() == null) {
                    return super.dispatchKeyEvent(event);
                }

                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null) {
                        state.startTracking(event, this);
                    }
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null && state.isTracking(event) && !event.isCanceled()) {
                        dismiss();
                        return true;
                    }
                }
                return super.dispatchKeyEvent(event);
            } else {
                return super.dispatchKeyEvent(event);
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
        	if(!isShowing() || mPopupView==null){
        		return super.dispatchTouchEvent(ev);
        	}
        	
            //Log.w(TAG, "dispatchTouchEvent: action="+ev.getAction());
            int action = ev.getAction();
            switch(action){
            case MotionEvent.ACTION_OUTSIDE:
            {
            	Log.i(TAG, mWinTag+" is outside");
            	try {
            		mPopupManager.setOutFlag(mWinTag);
            		//setWindowFocusable(false);
            		/*
                	if(mPopupManager.isBottomWindow(mWinTag)){
                		if(mPopupManager.checkIsLostAllFocus()){
                			Log.i(TAG, "windows lost all focus, will kill all focus");
                			mPopupManager.disableAllFocus();
                		}
                	}
                	*/
				} catch (RemoteException e) {
				}
            	killFocus();
            	break;
            }
            
            case MotionEvent.ACTION_DOWN:
            {
            	setFocus();
            	
            	try{
            		/*
	            	if(mPopupManager.checkIsLostAllFocus()){
	            		mPopupManager.enableAllFocus();
	            		Log.i(TAG,"windows get focus again");
	            	}
	            	*/
	            	mPopupManager.clearOutFlag(mWinTag);
	            	//setWindowFocusable(true);
	        		
	        		if(!mPopupManager.isTopWindow(mWinTag)){
	        			mPopupManager.bringWindowToFront(mWinTag);
	        			
	            		mWindowManager.removeView(this);
	            		mWindowManager.addView(this, this.getLayoutParams());
	            		
	            		return true;
	            	}
            	}catch (RemoteException e) {
				}
            	break;
            }
            
            case MotionEvent.ACTION_UP:
            {
            	
            	break;
            }
            
            }
            
            return super.dispatchTouchEvent(ev);
        }

        /*
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            //final int x = (int) event.getX();
            //final int y = (int) event.getY();

            Log.w(TAG, "onTouchEvent: action="+event.getAction());
            if ((event.getAction() == MotionEvent.ACTION_UP)){
            	mWindowManager.removeView(this);
    			mWindowManager.addView(this, this.getLayoutParams());
            }
            
            return false;
        }
        */
        
        @Override
		public void onConfigurationChanged(Configuration newConfig) {
			Log.i(TAG, "onConfigurationChanged: orientation="+newConfig.orientation);
			adjustWindowPos();
		}
    }
    
    public class rbWindowReceiver extends BroadcastReceiver{
    	static final String TAG = "rbWindowReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getStringExtra("target").equals(mWinTag)){
				String action = intent.getStringExtra("action");
				Log.i(TAG, "onReceive: target="+mWinTag+", action="+action);
				
				if(action.equals("setFocus")){
					if (isShowing() && mPopupView != null) {
						mPopupView.setFocus();
					}
				}else if(action.equals("killFocus")){
					if (isShowing() && mPopupView != null) {
						mPopupView.killFocus();
					}
				}else if(action.equals("enableFocus")){
					//setWindowFocusable(true);
				}else if(action.equals("disableFocus")){
					//setWindowFocusable(false);
				}else if(action.equals("dismiss")){
					dismiss();
				}
			}
		}
    	
    }
}