package com.wechat.filetransfer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.wechat.filetransfer.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityShowFile extends Activity {

	private TextView tvPath;
	private ListView fileView;
	private SimpleAdapter adapter;
	private int[] to;
	private String[] from;
	private ArrayList<HashMap<String, Object>> data;
	private String path = "";
	private String parent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);

		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(false);
		setTitle("选择文件");

		tvPath = (TextView) findViewById(R.id.tvPath);

		to = new int[] { R.id.imageView, R.id.fileName, R.id.fileSize,
				R.id.isDirectory };
		from = new String[] { "image", "name", "size", "isDirectory" };
		data = new ArrayList<HashMap<String, Object>>();
		adapter = new SimpleAdapter(this, data, R.layout.item_file, from, to);
		parent = filesListView(Environment.getExternalStorageDirectory()
				.getPath());

		fileView = (ListView) findViewById(R.id.fileView);
		fileView.setAdapter(adapter);
		fileView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// 如果是文件返回发送界面，如果是文件夹进入文件夹
				TextView isDirectory = (TextView) view
						.findViewById(R.id.isDirectory);
				TextView fileName = (TextView) view.findViewById(R.id.fileName);

				if (isDirectory.getText().toString().equals("true")) {
					String temp = path;
					if (!temp.equals("/")) {
						temp += "/";
					}
					temp += fileName.getText().toString();
					parent = filesListView(temp);
					adapter.notifyDataSetChanged();
				} else {
					Intent intent = new Intent();
					intent.putExtra("filePath", path);
					intent.putExtra("fileName", fileName.getText().toString());
					setResult(1, intent);
					finish();

				}

			}

		});

	}

	private String filesListView(String selectPath) {
		File file = new File(selectPath);
		if (file.canRead()) {

			tvPath.setText(selectPath);
			path = selectPath;

			File[] files = file.listFiles();
			data.clear();
			for (int i = 0; i < files.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				if (files[i].isDirectory()) {
					map.put("image", R.drawable.folder);
					map.put("size", "<");
					map.put("isDirectory", "true");

				} else {
					map.put("image", R.drawable.file);
					map.put("size", getSize(files[i]));
					map.put("isDirectory", "false");

				}
				map.put("name", files[i].getName());
				data.add(map);
			}
			String parent;
			if ((parent = file.getParent()) != null) {
				System.out.println(parent);
				return parent;
			}
		} else {
			Toast.makeText(getApplicationContext(), "该目录不能读取",
					Toast.LENGTH_SHORT).show();
		}
		return null;
	}

	private String getSize(File file) {
		long size = file.length();
		if (size / 1024 == 0) {
			return size + "B";
		}
		size /= 1024;
		if (size / 1024 == 0) {
			return size + "KB";
		}
		size /= 1024;
		if (size / 1024 == 0) {
			return size + "MB";
		}
		size /= 1024;
		return size + "GB";
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (parent != null) {
				path = parent;
				tvPath.setText(path);
				parent = filesListView(path);
				adapter.notifyDataSetChanged();
			} else {
				new AlertDialog.Builder(ActivityShowFile.this).setTitle("警告")
						.setMessage("确定退出？").setNegativeButton("取消", null)
						.setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								ActivityShowFile.this.finish();
							}
						}).show();
			}

		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_close) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
