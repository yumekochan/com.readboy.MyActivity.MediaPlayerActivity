package com.readboy.rbpopupservice;

interface IrbPopupManager
{
	void addWindow(String tag);
	void removeWindow(String tag);
	void bringWindowToFront(String tag);
	void setTopWindowFocus();
	void killTopWindowFocus();
	boolean isTopWindow(String tag);
	boolean isBottomWindow(String tag);
	void setOutFlag(String tag);
	void clearOutFlag(String tag);
	boolean checkIsLostAllFocus();
	void disableAllFocus();
	void enableAllFocus();
	
	void updateWindowPos(String tag, int x, int y);
	int getTopWindowPos(String tag);
}