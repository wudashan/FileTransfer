package com.wechat.filetransfer.bluetooth.adapter;

import java.io.File;
import java.util.List;

import com.wechat.filetransfer.R;
import com.wechat.filetransfer.bluetooth.adapter.util.MyFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter{
	private LayoutInflater mLayoutInflater;
	private List<File> mFileList;
	private int mLayoutId;
	
	public FileListAdapter(Context context, List<File> fileList, int layoutId){
		this.mLayoutInflater = LayoutInflater.from(context);
		this.mFileList = fileList;
		this.mLayoutId = layoutId;
	}

	@Override
	public int getCount() {
		return mFileList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mFileList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		File file = mFileList.get(position);
		MyFile myFile = null;
		if(null == convertView){
			myFile = new MyFile();
			convertView = mLayoutInflater.inflate(mLayoutId, null);
			myFile.mFileImageView = (ImageView) convertView.findViewById(R.id.fileImageView);
			myFile.mFileNameTV = (TextView) convertView.findViewById(R.id.fileNameTV);
			
			convertView.setTag(myFile);
		}else {
			myFile = (MyFile) convertView.getTag();
		}
		
		if(file.isFile()){
			myFile.mFileImageView.setImageResource(R.drawable.icon_file);
		}else {
			myFile.mFileImageView.setImageResource(R.drawable.icon_folder);
		}
		myFile.mFileNameTV.setText(file.getName());
		
		return convertView;
	}
	
}
