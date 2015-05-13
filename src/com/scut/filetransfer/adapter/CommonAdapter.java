package com.scut.filetransfer.adapter;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CommonAdapter<T> extends BaseAdapter {
	
	protected Context context;
	protected List<T> list;
	protected int layoutId;
	
	public CommonAdapter(Context context, List<T> list, int layoutId) {
		super();
		this.context = context;
		this.list = list;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public T getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public abstract void getView(CommonViewHolder holder,T t);

	@Override
	public  View getView(int position, View convertView, ViewGroup parent){
		CommonViewHolder holder = CommonViewHolder.get(position, context, convertView, parent, layoutId);
		getView(holder, list.get(position));
		return holder.getConvertView();
		
	}

}
