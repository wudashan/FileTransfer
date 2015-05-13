package com.scut.filetransfer.activity;
import java.util.ArrayList;
import java.util.List;

import com.scut.filetransfer.R;
import com.scut.filetransfer.adapter.SendAdapter;
import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.service.ConnectionManager;
import com.scut.filetransfer.service.UploadService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PageSend extends Fragment implements OnClickListener {

	private LinearLayout searchFriend, showFile, startSend;
	private TextView userToSend, fileToSend;
	private String fileName, filePath;
	private List<FileInfo> listSend = null;
	private ListView lvSendMission = null;
	private SendAdapter adapter = null;
	private String sendip;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.page_send, container, false);

		fileToSend = (TextView) view.findViewById(R.id.fileToSend);
		userToSend = (TextView) view.findViewById(R.id.userToSend);
		startSend =  (LinearLayout) view.findViewById(R.id.startSend);
		showFile = (LinearLayout) view.findViewById(R.id.showFile);
		searchFriend = (LinearLayout) view.findViewById(R.id.searchFriend);
		searchFriend.setOnClickListener(this);
		showFile.setOnClickListener(this);
		startSend.setOnClickListener(this);
		lvSendMission = (ListView) view.findViewById(R.id.lvSendMission);
		listSend = new ArrayList<FileInfo>();
		adapter = new SendAdapter(listSend, getActivity(), R.layout.item_send);
		lvSendMission.setAdapter(adapter);
		return view;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.showFile:
			Intent intent = new Intent(getActivity(), ActivityShowFile.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.startSend:
			if (sendip == null || sendip.equals("")) {
				Toast.makeText(getActivity(), "请先选好要发送的对象！", Toast.LENGTH_LONG)
						.show();
				break;
			}
			if (fileName == null) {
				Toast.makeText(getActivity(), "请先选好要发送的文件！", Toast.LENGTH_LONG)
						.show();
				break;
			}

			if (sendip != null && !sendip.equals("") && fileName != null) {
				new Thread() {
					public void run() {
						ConnectionManager.sendMsg(sendip, "start,"
								+ ConnectionManager.getIpAddress() + "," + fileName);
					};
				}.start();

				// 这里设置发送端的IP和端口号
				FileInfo fileInfo = new FileInfo();
				fileInfo.setIP(sendip);
				System.out.println(sendip);
				fileInfo.setPort(8879);
				filePath = filePath + "/" + fileName;
				fileInfo.setFileName(filePath);
				listSend.add(fileInfo);
				// 将fileInfo.id = position
				adapter.notifyDataSetChanged();
				// ///////////////////////////////
				Intent i = new Intent(getActivity(), UploadService.class);
				i.setAction(UploadService.ACTION_START);
				i.putExtra("filePath", filePath);
				i.putExtra("fileInfo", fileInfo);
				getActivity().startService(i);
				fileToSend.setText(fileName);

			}
			break;
		case R.id.searchFriend:
			MainActivity.server.clear();
			Intent intent2 = new Intent(getActivity(), ScanActivity.class);
			startActivityForResult(intent2, 2);
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 1:
			fileName = data.getStringExtra("fileName");
			filePath = data.getStringExtra("filePath");
			fileToSend.setText(fileName);
			break;
		case 2:
			Bundle bunde = data.getExtras();
			sendip = bunde.getString("ip");
			userToSend.setText(sendip);
			break;
		default:
			break;
		}
	}
}