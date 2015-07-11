package com.scut.filetransfer.activity;


import com.scut.filetransfer.R;
import com.scut.filetransfer.util.EmailUtil;
import com.scut.filetransfer.util.LogUtil;
import com.scut.filetransfer.util.MultiMailsender;
import com.scut.filetransfer.util.MultiMailsender.MultiMailSenderInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PageUser extends Fragment implements OnClickListener{
	
	private LinearLayout llUserInfo;
	private LinearLayout llUserEmail;
	private TextView tvUserName;
	private TextView tvUserEmail;
	private SharedPreferences sharedPreferences;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//初始化控件
		View view = inflater.inflate(R.layout.page_user, container, false);
		llUserInfo = (LinearLayout) view.findViewById(R.id.userInfo);
		llUserEmail = (LinearLayout) view.findViewById(R.id.userEmail);
		tvUserName = (TextView) view.findViewById(R.id.tvUserName);
		tvUserEmail = (TextView) view.findViewById(R.id.tvUserEmail);
		//读取数据
		sharedPreferences = getActivity().getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
		String userName = sharedPreferences.getString("userName","null");
		String userEmail = sharedPreferences.getString("userEmail","null");
		if (!userName.equals("null") ) {
			tvUserName.setText(userName);
		}
		if (!userEmail.equals("null")) {
			tvUserEmail.setText(userEmail);
		}
		//设置监听
		llUserInfo.setOnClickListener(this);
		llUserEmail.setOnClickListener(this);
		
		//发送邮件
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String senderEmail = sharedPreferences.getString("userEmail", "null");
				if (!senderEmail.equals("null")) {
					EmailUtil.sendEmail(senderEmail, "8888"); 
				}
			}
		}).start();
		
		
		return view;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userInfo:
			final EditText edUserName = new EditText(getActivity());
			new AlertDialog.Builder(getActivity())
			.setTitle(R.string.write_user_name)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(edUserName)
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.comfirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String userName = edUserName.getText().toString();
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("userName", userName);
					tvUserName.setText(userName);
					editor.commit();
				}
			}).show();
			break;
		case R.id.userEmail:
			final EditText edUserEmail = new EditText(getActivity());
			new AlertDialog.Builder(getActivity())
			.setTitle(R.string.write_user_email)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(edUserEmail)
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.comfirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String userEmail = edUserEmail.getText().toString();
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("userEmail", userEmail);
					tvUserEmail.setText(userEmail);
					editor.commit();
				}
			}).show();
			break;
		default:
			break;
		}
		
	}
}
