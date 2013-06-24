/*
 * 功能:歌词信息类,为解析歌词服务
 */
package com.readboy.MyMp3;

import java.util.Map;

public class LyricInformation { //歌词封装类
	private String title = null; //music title
	private String artist = null; //artist name
	private String album = null; //album name
	private String bySomebody = null; //the lrc maker
	private String offset = null; //the time delay or bring forward
	private Map<Long, String> infos = null; //保存歌词信息和时间点一一对应的Map
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public String getBySomebody() {
		return bySomebody;
	}
	public void setBySomebody(String bySomebody) {
		this.bySomebody = bySomebody;
	}
	
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	
	public Map<Long, String> getInfos() {
		return infos;
	}
	public void setInfos(Map<Long, String> infos) {
		this.infos = infos;
	}
}
