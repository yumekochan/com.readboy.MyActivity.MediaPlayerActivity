/*
 * 功能:歌曲列表容器
 */
package com.readboy.MyWindow;

import java.util.List;
import java.util.Map;

import com.readboy.MyMp3.MediaPlayerApplication;
import com.readboy.MyMp3.R;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SongAdapter extends SimpleAdapter { //歌曲列表Adapter
	private Context context = null;
	private List<? extends Map<String, ?>> data = null;
	
	private int playPosition = (-1); //播放歌曲position
	private int selectPosition = (-1); //选择歌曲position
	
	//颜色显示
//	private final int COLOR_NORMAL_VALIDITY = Color.WHITE; //正常有效
//	private final int COLOR_NORMAL_SELECT_VALIDITY = Color.CYAN; //正常选中有效
//	private final int COLOR_PLAY_VALIDITY = Color.GREEN; //播放有效
//	private final int COLOR_PLAY_SELECT_VALIDITY = Color.YELLOW; //播放选中有效
//	
//	private final int COLOR_NORMAL_INVALIDITY = Color.GRAY; //正常无效
//	private final int COLOR_NORMAL_SELECT_INVALIDITY = Color.MAGENTA; //正常选中无效
//	private final int COLOR_PLAY_INVALIDITY = Color.BLACK; //播放无效
//	private final int COLOR_PLAY_SELECT_INVALIDITY = Color.BLUE; //播放选中无效
	
	private final int COLOR_NORMAL_VALIDITY = Color.BLACK; //正常有效
	private final int COLOR_NORMAL_SELECT_VALIDITY = Color.CYAN; //正常选中有效
	private final int COLOR_PLAY_VALIDITY = Color.BLUE; //播放有效
	private final int COLOR_PLAY_SELECT_VALIDITY = Color.YELLOW; //播放选中有效
	
	private final int COLOR_NORMAL_INVALIDITY = Color.GRAY; //正常无效
	private final int COLOR_NORMAL_SELECT_INVALIDITY = Color.MAGENTA; //正常选中无效
	private final int COLOR_PLAY_INVALIDITY = Color.WHITE; //播放无效
	private final int COLOR_PLAY_SELECT_INVALIDITY = Color.RED; //播放选中无效
	
	public SongAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() { //获取总条目
		return data.size();
	}

	@Override
	public Object getItem(int position) { //获取对应位置条目内容
		return data.get(position);
	}

	@Override
	public long getItemId(int position) { //获取条目Id
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) { //列表每项
		View row = convertView; //前面存储的view
		ViewWrapper viewWrapper = null; //自定义内部类
		if(row == null) { //未存储View
			row = LayoutInflater.from(context).inflate(R.layout.window_listviewitem, null); //获取View
			viewWrapper = new ViewWrapper(row); //new内部类对象
			row.setTag(viewWrapper); //存储
		} else {
			viewWrapper = (ViewWrapper)row.getTag(); //获取存储内容
		}
		boolean isValidity = false; //歌曲有效状态标志位
		MediaPlayerApplication mpApp = ((MediaPlayerApplication)context.getApplicationContext()); //Application对象初始化
		String state = mpApp.getStateById(false, position); 
		if(state == null) {
			return row;
		}
		if(state.equals("validity")) {
			isValidity = true; //有效
		}
		int nameColor = 0, timerColor = 0; //颜色标志
		if(position == playPosition) { //当前position是正在播放的position
			viewWrapper.getPlaySongIcon().setVisibility(ImageView.VISIBLE); //显示icon
			if(playPosition == selectPosition) { //当前播放和选中的position是同一个
				//设置选中颜色
				if(isValidity) { //有效 
					nameColor = COLOR_PLAY_SELECT_VALIDITY;
					timerColor = COLOR_PLAY_SELECT_VALIDITY;
				} else { //无效
					nameColor = COLOR_PLAY_SELECT_INVALIDITY; 
					timerColor = COLOR_PLAY_SELECT_INVALIDITY;
				}
			} else { //不同个
				//设置播放颜色
				if(isValidity) { //有效 
					nameColor = COLOR_PLAY_VALIDITY;
					timerColor = COLOR_PLAY_VALIDITY;
				} else { //无效
					nameColor = COLOR_PLAY_INVALIDITY; 
					timerColor = COLOR_PLAY_INVALIDITY;
				}
			}
		} else if(position == selectPosition) { //当前position是选中的歌曲
			viewWrapper.getPlaySongIcon().setVisibility(ImageView.GONE); //隐藏icon
			//设置选中颜色
			if(isValidity) { //有效 
				nameColor = COLOR_NORMAL_SELECT_VALIDITY;
				timerColor = COLOR_NORMAL_SELECT_VALIDITY;
			} else { //无效
				nameColor = COLOR_NORMAL_SELECT_INVALIDITY; 
				timerColor = COLOR_NORMAL_SELECT_INVALIDITY;
			}
		} else { //正常显示歌曲信息
			viewWrapper.getPlaySongIcon().setVisibility(ImageView.GONE); //隐藏icon
			//设置正常颜色
			if(isValidity) { //有效 
				nameColor = COLOR_NORMAL_VALIDITY;
				timerColor = COLOR_NORMAL_VALIDITY;
			} else { //无效
				nameColor = COLOR_NORMAL_INVALIDITY; 
				timerColor = COLOR_NORMAL_INVALIDITY;
			}
		}
		if(nameColor == COLOR_NORMAL_SELECT_INVALIDITY || nameColor == COLOR_NORMAL_SELECT_VALIDITY || nameColor == COLOR_PLAY_SELECT_INVALIDITY || nameColor == COLOR_PLAY_SELECT_VALIDITY) {
			viewWrapper.getSongName().setEllipsize(TruncateAt.MARQUEE);
		} else {
			viewWrapper.getSongName().setEllipsize(TruncateAt.END);
		}
		viewWrapper.getSongName().setTextColor(nameColor); //设置颜色
		viewWrapper.getSongArtist().setTextColor(nameColor);
		viewWrapper.getSongTimer().setTextColor(timerColor);
		viewWrapper.getSongName().setText((String)(data.get(position).get("songName")));
		viewWrapper.getSongArtist().setText((String)(data.get(position).get("songArtist")));
		if(isValidity) { //有效显示时间
			viewWrapper.getSongTimer().setText((String)(data.get(position).get("songTimer")));
		} else { //无效
			viewWrapper.getSongTimer().setText("无效");
		}
		return row;
	}
	
	public boolean setPlayPosition(int position) { //设置播放曲目的position
		if(position >= data.size()) {
			System.out.println("mymp3 window SongAdapter setPlayPosition() error position = "+position);
			return false;
		}
		if(position < 0) {
			position = (-1);
		}
		playPosition = position;
		return true;
	}
	
	public int getPlayPosition() { //获取播放曲目的position
		return playPosition;
	}
	
	public boolean setSelectPosition(int position) { //设置选中歌曲的position
		if(position >= data.size()) {
			System.out.println("mymp3 window SongAdapter setSelectPosition() error position = "+position);
			return false;
		}
		if(position < 0) {
			position = (-1);
		}
		selectPosition = position;
		return true;
	}

	public int getSelectPosition() { //获取选中歌曲的position
		return selectPosition;
	}

	class ViewWrapper { //内部类,解决重复findViewById的问题
		View base = null;
		ImageView playSongIcon = null;
		TextView songName = null;
		TextView songArtist = null;
		TextView songTimer = null;
		
		public ViewWrapper(View base) {
			this.base = base;
		}
		
		public ImageView getPlaySongIcon() { //获取icon
			if(playSongIcon == null) {
				playSongIcon = (ImageView)base.findViewById(R.id.window_playSongIcon);
			}
			return playSongIcon;
		}
		
		public TextView getSongName() { //获取歌曲名的textView
			if(songName == null) {
				songName = (TextView)base.findViewById(R.id.window_songName);
			}
			return songName;
		}
		
		public TextView getSongArtist() { // 获取歌手的TextView
			if(songArtist == null) {
				songArtist = (TextView)base.findViewById(R.id.window_songArtist);
			}
			return songArtist;
		}
		
		public TextView getSongTimer() { //获取歌曲时间的TextView
			if(songTimer == null) {
				songTimer = (TextView)base.findViewById(R.id.window_songTimer);
			}
			return songTimer;
		}
	}
}
