/*
 * ����:TextView�������Ч��,��дisFocused����,��֤�����һֱ��ͣ
 */
package com.readboy.MyMp3;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeForeverTextView extends TextView{
	public MarqueeForeverTextView(Context context) {
		super(context);
	}
	public MarqueeForeverTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MarqueeForeverTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	public boolean isFocused() {
		return true;
	}
}
