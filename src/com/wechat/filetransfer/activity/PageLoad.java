package com.wechat.filetransfer.activity;

import java.util.ArrayList;
import java.util.List;

import com.wechat.filetransfer.R;
import com.wechat.filetransfer.adapter.ReceiveAdapter;
import com.wechat.filetransfer.bean.FileInfo;
import com.wechat.filetransfer.db.FileInfoDAO;
import com.wechat.filetransfer.db.FileInfoDAOImpl;
import com.wechat.filetransfer.services.DownloadService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PageLoad extends Fragment {

	private static List<FileInfo> list = null;
	private static ReceiveAdapter adapter = null;
	private static FileInfoDAO fileInfoDAO = null;
	private ListView lv_main = null;
	private IntentFilter filter = null;

	private static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_load, container, false);
		// 先从数据库里获取文件信息集合，包括未下载完的和已下载完的
		fileInfoDAO = new FileInfoDAOImpl(getActivity());

		list = new ArrayList<FileInfo>();
		list.addAll(fileInfoDAO.getAllFileInfos());
		// 将文件信息集合发送给adapter
		lv_main = (ListView) view.findViewById(R.id.lv_main);
		adapter = new ReceiveAdapter(getActivity(), list, R.layout.item_load,
				lv_main);
		lv_main.setAdapter(adapter);
		// 注册广播接收器
		filter = new IntentFilter();
		filter.addAction(DownloadService.ACTION_UPDATE);
		getActivity().registerReceiver(receiver, filter);
		return view;
	}

	public void unregisterReceiver() {
		getActivity().unregisterReceiver(receiver);
	}

	public static void ReceiveFileFromSender(FileInfo fileInfo) {
		// 若文件存在，且未下完，则进行断点续传，而不添加load_item
		if (fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(),
				fileInfo.getFileName())) {
			fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(),
					fileInfo.getFileName(), "继续下载");
			handler.sendMessage(new Message());
		} else {
			// 若文件不存在，则新添一个load_item进行下载。
			list.add(fileInfo);
			handler.sendMessage(new Message());
		}
	}

	
	/**
	 * 根据传入的fileInfo，查看数据库是否已经有该记录
	 * 若该文件已经传输过且传输完成，则返回true
	 * 其他条件（断点续传和第一次传输）则返回false
	 * @param fileInfo
	 * @return
	 */
	public static boolean isExistAndDoneFile(FileInfo fileInfo) {

		// 若文件同名且已下完成，则返回true
		if (fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(),fileInfo.getFileName())) {
			FileInfo fileInfo2 = fileInfoDAO.getFileInfo(fileInfo.getIP(), fileInfo.getPort(), fileInfo.getFileName());
			if (fileInfo2.getFinished() == 100) {
				return true;
			}else {
				return false;
			}
		} else {
			//发送端打开端口继续传输
			return false;
		}


	}

	// 广播接收器类
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
				FileInfo fileInfo = new FileInfo();
				fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				int finished = fileInfo.getFinished();
				String fileName = fileInfo.getFileName();
				int position = fileInfo.getId();
				list.get(position).setFinished(finished);
				list.get(position).setFileName(fileName);
				adapter.myNotifyDataSetChanged(position);
				// adapter.notifyDataSetChanged();
			}
		}
	};
}
