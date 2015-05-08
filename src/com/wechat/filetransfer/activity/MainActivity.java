package com.wechat.filetransfer.activity;

import java.util.ArrayList;
import java.util.List;

import com.wechat.filetransfer.R;
import com.wechat.filetransfer.bean.FileInfo;
import com.wechat.filetransfer.db.FileInfoDAO;
import com.wechat.filetransfer.db.FileInfoDAOImpl;
import com.wechat.filetransfer.services.SocketServer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		OnPageChangeListener, OnClickListener {

	public static SocketServer server = null;
	private ViewPager viewPager;
	private PageSend pageSend;
	private static PageLoad pageLoad;
	private PageBlueTooth pageBlueTooth;
	private FragmentPagerAdapter adapter;
	private FileInfoDAO fileInfoDAO;
	private List<ChangeColorIconWithText> listChangeColorIconWithTexts = new ArrayList<ChangeColorIconWithText>();
	private ChangeColorIconWithText one;
	private ChangeColorIconWithText two;
	private ChangeColorIconWithText three;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		initDatas();
		initEvent();
		server = new SocketServer();
		new Thread() {
			public void run() {
				server.beginListen();
			};
		}.start();
	}

	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		one = (ChangeColorIconWithText) findViewById(R.id.send);
		two = (ChangeColorIconWithText) findViewById(R.id.load);
		three = (ChangeColorIconWithText) findViewById(R.id.blueTooth);
		
		listChangeColorIconWithTexts.add(one);
		listChangeColorIconWithTexts.add(two);
		listChangeColorIconWithTexts.add(three);
		pageSend = new PageSend();
		pageLoad = new PageLoad();
		pageBlueTooth = new PageBlueTooth();
	}

	private void initDatas() {
		
		fileInfoDAO  = new FileInfoDAOImpl(this);
		
		adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return 3;
			}

			@Override
			public Fragment getItem(int arg0) {
				switch (arg0) {
				case 0:
					return pageSend;
				case 1:
					return pageLoad;
				case 2:
					return pageBlueTooth;
				default:
					return null;
				}
			}
		};
		viewPager.setAdapter(adapter);
		one.setIconAlpha(1.0f);
	}

	private void initEvent() {
		viewPager.setOnPageChangeListener(this);
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
	}

	
	@Override
	public void onBackPressed() {
		//退出程序前需要提醒用户暂停所有传输文件
		boolean isAllPause = true;
		for (FileInfo fileInfo : fileInfoDAO.getAllFileInfos()) {
			if (fileInfo.getStatus().equals("已完成") || fileInfo.getStatus().equals("停止下载")) {
				continue;
			}else {
				Toast.makeText(this, "请暂停完所有任务再退出！", Toast.LENGTH_LONG).show();
				isAllPause = false;
				break;
			}
		}
		
		if (isAllPause) {
			super.onBackPressed();
		}
		else{
			return;
		}
	}
	
	
	@Override
	protected void onDestroy() {
		pageLoad.unregisterReceiver();
		server.stopListen();
		super.onDestroy();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (positionOffset > 0) {
			ChangeColorIconWithText left = listChangeColorIconWithTexts
					.get(position);
			ChangeColorIconWithText right = listChangeColorIconWithTexts
					.get(position + 1);
			left.setIconAlpha(1 - positionOffset);
			right.setIconAlpha(positionOffset);
		}
	}

	
	
	@Override
	public void onPageSelected(int arg0) {

	}

	@Override
	public void onClick(View view) {

		resetOtherTabs();

		switch (view.getId()) {
		case R.id.send:
			listChangeColorIconWithTexts.get(0).setIconAlpha(1.0f);
			viewPager.setCurrentItem(0, false);
			break;

		case R.id.load:
			listChangeColorIconWithTexts.get(1).setIconAlpha(1.0f);
			viewPager.setCurrentItem(1, false);
			break;
			
		case R.id.blueTooth:
			listChangeColorIconWithTexts.get(2).setIconAlpha(1.0f);
			viewPager.setCurrentItem(2, false);
			break;
		default:
			break;
		}
	}

	private void resetOtherTabs() {
		for (int i = 0; i < listChangeColorIconWithTexts.size(); i++) {
			listChangeColorIconWithTexts.get(i).setIconAlpha(0);
		}
	}

	@SuppressWarnings("static-access")
	public static void addLoadItem(FileInfo fileInfo) {
		pageLoad.ReceiveFileFromSender(fileInfo);
	}
}
