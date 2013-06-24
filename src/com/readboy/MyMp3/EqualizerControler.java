/*
 * 频谱控制音效
 */

package com.readboy.MyMp3;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;

public class EqualizerControler {
	
	public static final int MODE_STANDARD = 0; //标准模式
	public static final int MODE_CLASSICAL = 1; //经典模式
	public static final int MODE_JAZZ = 2; //爵士模式
	public static final int MODE_POP = 3; //流行模式
	public static final int MODE_LIVE = 4; //现场模式
	public static final int MODE_ROCK = 5; //摇滚模式
	
	//均衡器
	private Equalizer equalizer = null;
	//频谱引擎个数
	private short bands = 0;
	//频谱最低和最高等级
	private short minEqualizer = 0;
	private short maxEqualizer = 0;
	
	public EqualizerControler(Context context, MediaPlayer mediaPlayer) {
		//创建优先级为0均衡器对象
		equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
		// 启用均衡器
		equalizer.setEnabled(true);
		//通过均衡器得到其支持的频谱引擎
		bands = equalizer.getNumberOfBands();
		//获取频谱等级
		minEqualizer = equalizer.getBandLevelRange()[0];
		maxEqualizer = equalizer.getBandLevelRange()[1];
	}
	
	public void setEqualizer(int modeTmp) {
		
		int mode[] = new int[5];
		mode[0] = 0;
		mode[1] = 0;
		mode[2] = 0;
		mode[3] = 0;
		mode[4] = 0;
		
		switch(modeTmp) {
		case MODE_STANDARD:
			break;
		case MODE_CLASSICAL:
			mode[0] = -2;
			mode[2] = 12;
			mode[3] = 7;
			mode[4] = -12;
			break;
		case MODE_JAZZ:
			mode[0] = -2;
			mode[2] = 12;
			break;
		case MODE_POP:
			mode[0] = -2;
			mode[2] = 2;
			mode[3] = 4;
			mode[4] = 6;
			break;
		case MODE_LIVE:
			mode[0] = -2;
			mode[1] = 2;
			mode[2] = 12;
			mode[3] = 9;
			mode[4] = 6;
			break;
		case MODE_ROCK:
			mode[0] = -2;
			mode[2] = 12;
			mode[3] = 7;
			mode[4] = 12;
			break;
		}
		int N = (int)bands;
		for(int i = 0; i < N; i ++) {
			short band = (short)i;
			short level = (short)(minEqualizer+100*(mode[i]+(maxEqualizer-minEqualizer)/200));
			try {
				equalizer.setBandLevel(band, level);
			} catch(Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeEqualizer() {
		if(equalizer != null) {
			equalizer.release();
			equalizer = null;
		}
	}
}
