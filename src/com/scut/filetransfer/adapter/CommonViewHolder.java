package com.scut.filetransfer.adapter;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommonViewHolder {
	private SparseArray<View> sparseArray;
	private int position;
	private Context context;
	private View convertView;
	private ViewGroup parent;
	private int layoutId;
	
	
	

	public CommonViewHolder(int position, Context context,ViewGroup parent, int layoutId) {
		super();
		sparseArray = new SparseArray<View>();
		this.position = position;
		this.context = context;
		this.parent = parent;
		this.layoutId = layoutId;
		this.convertView = LayoutInflater.from(context).inflate(layoutId, parent,false);
		this.convertView.setTag(this);
	}



	public static CommonViewHolder get(int position, Context context, View convertView,ViewGroup parent, int layoutId) {
		if (convertView == null) {
			return new CommonViewHolder(position, context, parent, layoutId);
		}else {
			CommonViewHolder commonViewHolder = (CommonViewHolder) convertView.getTag();
			//防止复用的联动问题
			commonViewHolder.position = position;
			return commonViewHolder;
		}
	}
	
	public <T extends View> T getView(int viewId){
		View view = sparseArray.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			sparseArray.put(viewId, view);
		}
		return (T) view;
	}

	public View getConvertView() {
		return convertView;
	}



	public int getPosition() {
		return position;
	}
	
	
	
	
	
	

}
