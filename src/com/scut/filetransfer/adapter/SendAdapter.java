package com.scut.filetransfer.adapter;
import java.util.List;

import com.scut.filetransfer.R;
import com.scut.filetransfer.bean.FileInfo;

import android.content.Context;
import android.widget.TextView;

public class SendAdapter extends CommonAdapter<FileInfo> {



	public SendAdapter(List<FileInfo> list, Context context, int layoutId) {
		super(context, list, layoutId);
	}
	
	@Override
	public void getView(CommonViewHolder holder, FileInfo fileInfo) {
		TextView tvFileName = holder.getView(R.id.fileName);
		tvFileName.setText(fileInfo.getFileName());
		//?fileInfo.id = position
		fileInfo.setId(holder.getPosition());
	}




}
