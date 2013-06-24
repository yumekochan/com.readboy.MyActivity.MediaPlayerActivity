package com.readboy.MyMp3;

import java.io.File;
import java.util.ArrayList;

public class Searcher {
	String searchPath; //搜索路径 
	String suffix; //后缀
	boolean isSubFolderSearch; //子文件夹搜索标志
	private ArrayList<String> listItem = new ArrayList<String>();

	//构造函数
	public Searcher(String searchPath, String suffix, boolean isSubFolderSearch) {
		this.searchPath = searchPath;
		this.suffix = suffix;
		this.isSubFolderSearch = isSubFolderSearch; 
	}
	
	//初始化, 返回值0表示正常,(-1)表示出错(路径不存在)
	public int init() {
		return searchFiles(searchPath, suffix, isSubFolderSearch);
	}
	
	//获取总数
	public int getTotalNum() {
		for (int i = 0; i < listItem.size(); i++) {
			System.out.println(listItem.get(i));
		}
		
		return listItem.size();
	}

	//根据Index获取路径
	public String getPathByIndex(int idx) {
		if (idx < 0 || idx >= listItem.size()) {
			return null;
		}

		return listItem.get(idx);
	}

	//搜索,返回值0表示正常,(-1)表示出错(路径不存在)
	private int searchFiles(String searchPath, String suffix, boolean isSubFolderSearch) {
		// 打开路径
		File file = new File(searchPath);
		if (false == file.exists()) { // 打开路径失败
			System.out.println("searchPath "+searchPath+" not exist!");
			return (-1);
		}
		// 获取路径下的所有文件
		File files[] = new File(searchPath).listFiles();

		// 后缀分解
		String suffixes[] = suffix.split(",");

		// 分析所有文件和文件夹
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) { // 是文件
				for (String str : suffixes) {
					String fileSuffix = files[i].getPath().substring((int) (files[i].getPath().length() - str.length()));
					String lower = fileSuffix.toLowerCase();
					String lowerStr = str.toLowerCase();
					if (lowerStr.equals(lower)) {
						listItem.add(files[i].getPath());
						break;
					}
				}
			} else if (files[i].isDirectory() && isSubFolderSearch == true
					&& !files[i].isHidden()) { // 支持子文件夹搜索且非隐藏文件夹
				searchFiles(files[i].getPath(), suffix, isSubFolderSearch); // 搜索子文件夹
			}
		}
		return 0;
	}
}
