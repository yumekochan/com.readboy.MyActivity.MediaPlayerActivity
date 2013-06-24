/*
 * 功能:解析歌词
 */
package com.readboy.MyMp3;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricParser {
	private LyricInformation lrcInfo = null; //歌词信息对象
	private Map<Long, String> maps = null; //用来保存时间点和歌词的对应关系
	
	public LyricParser() {
		lrcInfo = new LyricInformation(); //歌词信息对象
		maps = new TreeMap<Long, String>(); //用来保存时间点和歌词的对应关系
	}
	
	//根据路径解析歌词,返回歌词信息
	public LyricInformation parseLrcInfoByPath(String path) throws Exception {
		lrcInfo = parseLrcInfo(path);
		return lrcInfo;
	}
	
	//根据输入流解析歌词,返回歌词信息
	private LyricInformation parseLrcInfo(String path) throws IOException {
		//判断格式获取BufferedReader
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = null;				 
		BufferedInputStream bis = new BufferedInputStream(fis);
		bis.mark(4);
		byte[] encode = new byte[3];
		bis.read(encode);
		bis.reset();
		if(encode[0] == (-17) && encode[1] == (-69) && encode[2] == (-65)) {
			isr = new InputStreamReader(bis, "utf-8");
		} else if(encode[0] == (-1) && encode[1] == (-2)) {
			isr = new InputStreamReader(bis, "unicode");
		} else if(encode[0] == (-2) && encode[1] == (-1)) {
			isr = new InputStreamReader(bis, "utf-16be");
		} else if(encode[0] == (-1) && encode[1] == (-1)) {
			isr = new InputStreamReader(bis, "utf-16le");
		} else {
	        byte[] encodeTmp = new byte[(int)file.length()];
	        bis.read(encodeTmp);
	        String str = new String(encodeTmp, "utf-8");
	        if(str.contains("�")) {
	        	isr = new InputStreamReader(bis, "gbk");
	        } else {
	        	isr = new InputStreamReader(bis, "utf-8");
	        }
		}
		BufferedReader br = new BufferedReader(isr);
		
		//一行行读,读一行解析一行
		String line = null;
		while((line = br.readLine()) != null) {
			parseLine(line); 
		}
		//全部解析完后,设置info		
		lrcInfo.setInfos(maps);
		//打印信息
//		Iterator<Entry<Long, String>> iterator = maps.entrySet().iterator();
//		while(iterator.hasNext()) {
//			Entry<Long, String> entry = (Entry<Long, String>)iterator.next();
//			Long key = entry.getKey();
//			String value = entry.getValue();
//			System.out.println("mymp3 LyricParser lyric key = "+key+", value = "+value);
//		}
		return lrcInfo;
	}
	
	//利用正则表达式解析每行歌词,并将解析完信息保存在lrcInfo对象中
	private void parseLine(String line) {
		System.out.println("mymp3 LyricParser "+line);
		if(line.startsWith("[ti:")) { //获取歌曲名信息
			String title = line.substring(4, line.length()-1);
			lrcInfo.setTitle(title);
			System.out.println("mymp3 LyricParser title "+title);
		} else if(line.startsWith("[ar:")) { //获取歌手信息
			String artist = line.substring(4, line.length()-1);
			lrcInfo.setArtist(artist);
			System.out.println("mymp3 LyricParser artist "+artist);
		} else if(line.startsWith("[al:")) { //获取专辑信息
			String album = line.substring(4, line.length()-1);
			lrcInfo.setAlbum(album);
			System.out.println("mymp3 LyricParser album "+album);
		} else if(line.startsWith("[by:")) { //获取歌词作者信息
			String bySomebody = line.substring(4, line.length()-1);
			lrcInfo.setBySomebody(bySomebody);
			System.out.println("mymp3 LyricParser bySomebody "+bySomebody);
		} else { //通过正则表达式取得每句歌词信息
			//设置正则表达式
			String regular = "\\[(\\d{1,2}:\\d{1,2}\\.\\d{1,2})\\]|\\[(\\d{1,2}:\\d{1,2})\\]";
			Pattern pattern = Pattern.compile(regular);
			Matcher matcher = pattern.matcher(line);
			//如果存在匹配项则执行如下操作
			long currentTime = 0; //存放临时时间
			String currentContent =null; //存放临时歌词
			while(matcher.find()) {
				//得到匹配内容
				//String msg = matcher.group();
				//得到这个匹配项开始的索引
				//int start = matcher.start();
				//得到这个匹配项结束的索引
				//int end = matcher.end();
				//得到这个匹配项中的数组
				int groupCount = matcher.groupCount();
				for(int index = 0; index < groupCount; index ++) {
					String timeTmp = matcher.group(index);
					if(index == 0) { //将第二组中的内容设置为当前的一个时间点
						currentTime = str2Long(timeTmp.substring(1, timeTmp.length()-1));
					}
				}
				//得到时间点后的内容并设置为当前内容
				String content[] = pattern.split(line);
				if(content.length <= 0) {
					currentContent = "";
				} else {
					currentContent = content[content.length-1];
				}
				//设置时间点和内容映射
				maps.put(currentTime, currentContent);
			}
		}
	}
	
	//将时间格式为XX:XX.XX转换成long型,以毫秒为单位
	private long str2Long(String time) {
		String str[] = time.split("\\:");
		int minute = Integer.parseInt(str[0]);
		int second = 0, milliSecond = 0;
		
		if(str[1].contains(".")) {
			String strTmp[] = str[1].split("\\.");
			second = Integer.parseInt(strTmp[0]);
			milliSecond = Integer.parseInt(strTmp[1]);
		} else {
			second = Integer.parseInt(str[1]);
		}
		return minute*60*1000+second*1000+milliSecond*10;
	}
}
