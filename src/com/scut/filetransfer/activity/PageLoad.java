package com.scut.filetransfer.activity;
import java.util.ArrayList;
import java.util.List;

import com.scut.filetransfer.R;
import com.scut.filetransfer.adapter.ReceiveAdapter;
import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.database.FileInfoDAO;
import com.scut.filetransfer.database.FileInfoDAOImpl;
import com.scut.filetransfer.service.DownloadService;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PageLoad extends Fragment {

	private static List<FileInfo> list = null;
	private static ReceiveAdapter adapter = null;
	private static FileInfoDAO fileInfoDAO = null;
	private ListView lv_main = null;
	private static   ImageView bg_imageView;
	private static   TextView bg_textView;
	private static int flag = 0;
	private IntentFilter filter = null;

	private static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				adapter.notifyDataSetChanged();
				break;
			case 1:
				bg_imageView.setVisibility(View.GONE);
				bg_textView.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		};
	};
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_load, container, false);
		// 先从数据库里获取文件信息集合，包括未下载完的和已下载完的
		fileInfoDAO = new FileInfoDAOImpl(getActivity());
		bg_imageView = (ImageView) view.findViewById(R.id.bg_imageView);
		bg_textView = (TextView) view.findViewById(R.id.bg_textView);
		list = new ArrayList<FileInfo>();
		list.addAll(fileInfoDAO.getAllFileInfos());
		//若有文件记录，则设置图片不可见
		if (!list.isEmpty()) {
			bg_imageView.setVisibility(View.GONE);
			bg_textView.setVisibility(View.GONE);
		}
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
		
		Message message1 = new Message();
		message1.what = 1;
		handler.sendMessage(message1);
		
		
		// 若文件存在，且未下完，则进行断点续传，而不添加load_item
		if (fileInfoDAO.isExists(fileInfo.getIP(), fileInfo.getPort(),
				fileInfo.getFileName())) {
			fileInfoDAO.updateFileInfo(fileInfo.getIP(), fileInfo.getPort(),
					fileInfo.getFileName(), "继续下载");
			Message message = new Message();
			message.what = 0;
			handler.sendMessage(message);
		} else {
			// 若文件不存在，则新添一个load_item进行下载。
			list.add(fileInfo);
			Message message = new Message();
			message.what = 0;
			handler.sendMessage(message);
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
