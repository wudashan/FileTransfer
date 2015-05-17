package com.scut.filetransfer.adapter;
import java.util.ArrayList;
import java.util.List;

import com.scut.filetransfer.R;
import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.service.DownloadService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveAdapter extends CommonAdapter<FileInfo> {
	
	private ListView lv_main;
	private List<Integer> listButton;	//表示已完成的button

	public ReceiveAdapter(Context context, List<FileInfo> list, int layoutId,ListView lv_main) {
		super(context,list,layoutId);
		this.lv_main = lv_main;
		listButton = new ArrayList<Integer>();
	}


	@Override
	public void getView(CommonViewHolder holder,final FileInfo fileInfo) {

		//获取list里的内容，并设置到组件里
		TextView  tvFileName = holder.getView(R.id.tvFileName);
		final Button btnButton = holder.getView(R.id.btnButton);
		ProgressBar pbProgress = holder.getView(R.id.pbProgress);
		
		tvFileName.setText(fileInfo.getFileName());
		pbProgress.setMax(100);
		pbProgress.setProgress(fileInfo.getFinished());
		btnButton.setText(fileInfo.getStatus());
		btnButton.setVisibility(Button.VISIBLE);
		btnButton.setBackgroundColor(Color.LTGRAY);
		
		//若文件传输完成，设置button不可见，并解决复用button的问题
		if (fileInfo.getFinished() == 100 && listButton.contains(holder.getPosition())) {
			//btnButton.setVisibility(Button.INVISIBLE);
			btnButton.setBackgroundColor(Color.TRANSPARENT);
			btnButton.setTextColor(Color.BLACK);
			btnButton.setText("已完成");
			btnButton.setOnClickListener(null);
		}
		if (fileInfo.getFinished() == 100 && "已完成".equals(btnButton.getText().toString())) {
			btnButton.setBackgroundColor(Color.TRANSPARENT);
			btnButton.setTextColor(Color.BLACK);
			btnButton.setText("已完成");
			btnButton.setOnClickListener(null);
		}
		
		//将文件id设为position
		fileInfo.setId(holder.getPosition());
		Log.i("ReceiveAdapter", "holder.getPosition() and fileInfo.getId():"+fileInfo.getId());
		btnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(context,DownloadService.class);
				
				if ("停止下载".equals(btnButton.getText().toString())) {
					//若点击“停止下载”按钮
					//告诉Service执行STOP动作，并将文件信息传过去
					intent.setAction(DownloadService.ACTION_STOP);
					intent.putExtra("fileInfo", fileInfo);
					context.startService(intent);
					btnButton.setText("继续下载");
					fileInfo.setStatus("继续下载");
				}else if ("继续下载".equals(btnButton.getText().toString())) {
					//若点击“继续下载”按钮
					//告诉Service执行START动作，并将文件信息传过去
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", fileInfo);
					context.startService(intent);
					btnButton.setText("停止下载");
					fileInfo.setStatus("停止下载");
				}else if ("开始下载".equals(btnButton.getText().toString())) {
					//若点击“开始下载”按钮
					//告诉Service执行START动作，并将文件ID和信息传过去
					intent.setAction(DownloadService.ACTION_START);
					intent.putExtra("fileInfo", fileInfo);
					context.startService(intent);
					btnButton.setText("停止下载");
					fileInfo.setStatus("停止下载");
				}
			}
		});
		
		
		
	}


	/**
	 * 自定义更新进度条和文件名的方法
	 * 因为使用系统的notifyDataSetChanged会调用getView()
	 * 会带来大量的耗时
	 * @param position
	 */
	public void myNotifyDataSetChanged(int position) {
		//System.out.println("MyAdapter myNotifyDataSetChanged()");
		//得到第1个可显示控件的位置,记住是第1个可显示控件噢。而不是第1个控件
		int visiblePosition = lv_main.getFirstVisiblePosition(); 
		//得到你需要更新item的View,若控件不在显示范围，则不进行更新
		if (position > lv_main.getLastVisiblePosition() || position < lv_main.getFirstVisiblePosition()) {
			return ;
		}	
		View item = lv_main.getChildAt(position - visiblePosition);
		FileInfo fileInfo = list.get(position);
		
		ProgressBar pbProgress = (ProgressBar) item.findViewById(R.id.pbProgress);
		TextView tvFileName =  (TextView) item.findViewById(R.id.tvFileName);
		Button btnButton =  (Button) item.findViewById(R.id.btnButton);
		
		pbProgress.setProgress(fileInfo.getFinished());
		tvFileName.setText(fileInfo.getFileName());
		
		if (fileInfo.getFinished() == 100) {
			btnButton.setBackgroundColor(Color.TRANSPARENT);
			btnButton.setTextColor(Color.BLACK);
			btnButton.setText("已完成");
			btnButton.setOnClickListener(null);
			listButton.add(position);
			Toast.makeText(context, "文件已保存在"+DownloadService.DOWNLOAD_PATH, Toast.LENGTH_SHORT).show();
		}
	}



}
