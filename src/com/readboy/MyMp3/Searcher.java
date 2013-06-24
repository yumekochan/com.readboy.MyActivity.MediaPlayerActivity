package com.readboy.MyMp3;

import java.io.File;
import java.util.ArrayList;

public class Searcher {
	String searchPath; //����·�� 
	String suffix; //��׺
	boolean isSubFolderSearch; //���ļ���������־
	private ArrayList<String> listItem = new ArrayList<String>();

	//���캯��
	public Searcher(String searchPath, String suffix, boolean isSubFolderSearch) {
		this.searchPath = searchPath;
		this.suffix = suffix;
		this.isSubFolderSearch = isSubFolderSearch; 
	}
	
	//��ʼ��, ����ֵ0��ʾ����,(-1)��ʾ����(·��������)
	public int init() {
		return searchFiles(searchPath, suffix, isSubFolderSearch);
	}
	
	//��ȡ����
	public int getTotalNum() {
		for (int i = 0; i < listItem.size(); i++) {
			System.out.println(listItem.get(i));
		}
		
		return listItem.size();
	}

	//����Index��ȡ·��
	public String getPathByIndex(int idx) {
		if (idx < 0 || idx >= listItem.size()) {
			return null;
		}

		return listItem.get(idx);
	}

	//����,����ֵ0��ʾ����,(-1)��ʾ����(·��������)
	private int searchFiles(String searchPath, String suffix, boolean isSubFolderSearch) {
		// ��·��
		File file = new File(searchPath);
		if (false == file.exists()) { // ��·��ʧ��
			System.out.println("searchPath "+searchPath+" not exist!");
			return (-1);
		}
		// ��ȡ·���µ������ļ�
		File files[] = new File(searchPath).listFiles();

		// ��׺�ֽ�
		String suffixes[] = suffix.split(",");

		// ���������ļ����ļ���
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) { // ���ļ�
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
					&& !files[i].isHidden()) { // ֧�����ļ��������ҷ������ļ���
				searchFiles(files[i].getPath(), suffix, isSubFolderSearch); // �������ļ���
			}
		}
		return 0;
	}
}
