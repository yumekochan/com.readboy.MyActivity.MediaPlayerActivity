/*
 * ����:�����б�����
 */
package com.readboy.MyActivity;

import java.util.List;
import java.util.Map;

import com.readboy.MyMp3.MediaPlayerApplication;
import com.readboy.MyMp3.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SongAdapter extends SimpleAdapter { //�����б�Adapter
	private Context context = null;
	private List<? extends Map<String, ?>> data = null;
	
	private int playPosition = (-1); //���Ÿ���position
	private int selectPosition = (-1); //ѡ�����position
	
	//��ɫ��ʾ
	private final int COLOR_NORMAL_VALIDITY = Color.WHITE; //������Ч
	private final int COLOR_NORMAL_SELECT_VALIDITY = Color.CYAN; //����ѡ����Ч
	private final int COLOR_PLAY_VALIDITY = Color.GREEN; //������Ч
	private final int COLOR_PLAY_SELECT_VALIDITY = Color.YELLOW; //����ѡ����Ч
	
	private final int COLOR_NORMAL_INVALIDITY = Color.GRAY; //������Ч
	private final int COLOR_NORMAL_SELECT_INVALIDITY = Color.MAGENTA; //����ѡ����Ч
	private final int COLOR_PLAY_INVALIDITY = Color.BLACK; //������Ч
	private final int COLOR_PLAY_SELECT_INVALIDITY = Color.BLUE; //����ѡ����Ч
	
	public SongAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() { //��ȡ����Ŀ
		return data.size();
	}

	@Override
	public Object getItem(int position) { //��ȡ��Ӧλ����Ŀ����
		return data.get(position);
	}

	@Override
	public long getItemId(int position) { //��ȡ��ĿId
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) { //�б�ÿ��
		View row = convertView; //ǰ��洢��view
		ViewWrapper viewWrapper = null; //�Զ����ڲ���
		
		if(row == null) { //δ�洢View
			row = LayoutInflater.from(context).inflate(R.layout.activity_listviewitem, null); //��ȡView
			viewWrapper = new ViewWrapper(row); //new�ڲ������
			row.setTag(viewWrapper); //�洢
		} else {
			viewWrapper = (ViewWrapper)row.getTag(); //��ȡ�洢����
		}
		
		boolean isValidity = false; //������Ч״̬��־λ
		MediaPlayerApplication mpApp = ((MediaPlayerApplication)context.getApplicationContext()); //Application�����ʼ��
		String state = mpApp.getStateById(false, position); 
		if(state == null) {
			return row;
		}
		if(state.equals("validity")) {
			isValidity = true; //��Ч
		}
		int nameColor = 0, timerColor = 0; //��ɫ��־
		
		if(position == playPosition) { //��ǰposition�����ڲ��ŵ�position
			viewWrapper.getPlaySongIcon().setVisibility(ImageView.VISIBLE); //��ʾicon
			if(playPosition == selectPosition) { //��ǰ���ź�ѡ�е�position��ͬһ��
				//����ѡ����ɫ
				if(isValidity) { //��Ч 
					nameColor = COLOR_PLAY_SELECT_VALIDITY;
					timerColor = COLOR_PLAY_SELECT_VALIDITY;
				} else { //��Ч
					nameColor = COLOR_PLAY_SELECT_INVALIDITY; 
					timerColor = COLOR_PLAY_SELECT_INVALIDITY;
				}
			} else { //��ͬ��
				//���ò�����ɫ
				if(isValidity) { //��Ч 
					nameColor = COLOR_PLAY_VALIDITY;
					timerColor = COLOR_PLAY_VALIDITY;
				} else { //��Ч
					nameColor = COLOR_PLAY_INVALIDITY; 
					timerColor = COLOR_PLAY_INVALIDITY;
				}
			}
		} else if(position == selectPosition) { //��ǰposition��ѡ�еĸ���
			viewWrapper.getPlaySongIcon().setVisibility(ImageView.GONE); //����icon
			//����ѡ����ɫ
			if(isValidity) { //��Ч 
				nameColor = COLOR_NORMAL_SELECT_VALIDITY;
				timerColor = COLOR_NORMAL_SELECT_VALIDITY;
			} else { //��Ч
				nameColor = COLOR_NORMAL_SELECT_INVALIDITY; 
				timerColor = COLOR_NORMAL_SELECT_INVALIDITY;
			}
		} else { //������ʾ������Ϣ
			viewWrapper.getPlaySongIcon().setVisibility(ImageView.GONE); //����icon
			//����������ɫ
			if(isValidity) { //��Ч 
				nameColor = COLOR_NORMAL_VALIDITY;
				timerColor = COLOR_NORMAL_VALIDITY;
			} else { //��Ч
				nameColor = COLOR_NORMAL_INVALIDITY; 
				timerColor = COLOR_NORMAL_INVALIDITY;
			}
		}
		viewWrapper.getSongName().setTextColor(nameColor); //������ɫ
		viewWrapper.getSongTimer().setTextColor(timerColor);
		viewWrapper.getSongName().setText((String)(data.get(position).get("songName")));
		if(isValidity) { //��Ч��ʾʱ��
			viewWrapper.getSongTimer().setText((String)(data.get(position).get("songTimer")));
		} else { //��Ч
			viewWrapper.getSongTimer().setText("��Ч");
		}
		
		return row;
	}
	
	public boolean setPlayPosition(int position) { //���ò�����Ŀ��position
		if(position >= data.size()) {
			System.out.println("mymp3 Activity SongAdapter setPlayPosition() error position = "+position);
			return false;
		}
		if(position < 0) {
			position = (-1);
		}
		playPosition = position;
		return true;
	}
	
	public int getPlayPosition() { //��ȡ������Ŀ��position
		return playPosition;
	}
	
	public boolean setSelectPosition(int position) { //����ѡ�и�����position
		if(position >= data.size()) {
			System.out.println("mymp3 Activity SongAdapter setSelectPosition() error position = "+position);
			return false;
		}
		if(position < 0) {
			position = (-1);
		}
		selectPosition = position;
		return true;
	}

	public int getSelectPosition() { //��ȡѡ�и�����position
		return selectPosition;
	}

	class ViewWrapper { //�ڲ���,����ظ�findViewById������
		View base = null;
		ImageView playSongIcon = null;
		TextView songName = null;
		TextView songTimer = null;
		
		public ViewWrapper(View base) {
			this.base = base;
		}
		
		public ImageView getPlaySongIcon() { //��ȡicon
			if(playSongIcon == null) {
				playSongIcon = (ImageView)base.findViewById(R.id.activity_playSongIcon);
			}
			return playSongIcon;
		}
		
		public TextView getSongName() { //��ȡ��������textView
			if(songName == null) {
				songName = (TextView)base.findViewById(R.id.activity_songName);
			}
			return songName;
		}
		
		public TextView getSongTimer() { //��ȡ����ʱ���TextView
			if(songTimer == null) {
				songTimer = (TextView)base.findViewById(R.id.activity_songTimer);
			}
			return songTimer;
		}
	}
}
