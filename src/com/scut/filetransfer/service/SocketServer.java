package com.scut.filetransfer.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.scut.filetransfer.R;
import com.scut.filetransfer.activity.PageLoad;
import com.scut.filetransfer.activity.ScanActivity;
import com.scut.filetransfer.bean.FileInfo;
import com.scut.filetransfer.bean.User;
import com.scut.filetransfer.util.AESUtil;
import com.scut.filetransfer.util.Base64Utils;
import com.scut.filetransfer.util.MD5Util;
import com.scut.filetransfer.util.RandomNum;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 扫描的服务器端
 * 
 * @author ccz
 * 
 */
public class SocketServer {

	private static ServerSocket server;
	private Context context;
	private static Set<User> clientSet = new HashSet<User>();
	private List<User> clientList = new ArrayList<User>();
	private int clientNumber = 0;
	private static boolean flag = true;
	private String publicKey;
	private String code = "";
	private AESUtil aesUtil;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				showInputDialog(msg.getData().getString("ip"));
				break;
			case 2:
				if (clientList != null && !clientList.isEmpty()
						&& clientList.size() != clientNumber) {
					for (int i = clientNumber; i < clientList.size(); i++) {
						Point point = ((ScanActivity) context).randomPoint();
						((ScanActivity) context).addImage(R.drawable.head,
								point.x, point.y, clientList.get(i));
					}
					clientNumber = clientList.size();
				}
				break;
			default:
				break;
			}
		};
	};

	private static SocketServer socketServer;

	public static SocketServer getInstance() {
		if (socketServer == null)
			socketServer = new SocketServer();
		return socketServer;
	}

	private SocketServer() {
		try {
			server = new ServerSocket(ConnectionManager.SERVERPORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		flag = true;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private String handleMessage(String message) {
		if (message == null)
			return null;
		String[] result = message.split(",");
		if (result.length == 5) {
			final String ip = result[0];
			final String phone = result[1];
			final double latitude = Double.parseDouble(result[2]);
			final double lontitude = Double.parseDouble(result[3]);
			User temp = new User(ip, phone, latitude, lontitude);
			if (!clientSet.contains(temp)) {
				String str = ConnectionManager.getIpAddress() + ","
						+ android.os.Build.MODEL + ","
						+ ConnectionManager.locationManager.getLatitude() + ","
						+ ConnectionManager.locationManager.getLontitude()
						+ ",scan";
				ConnectionManager.sendMsg(ip, str);
				clientSet.add(temp);
				clientList.add(temp);
				Message msg = new Message();
				msg.what = 2;
				handler.sendMessage(msg);
			}
		} else {
			String tag = result[0];
			String value = result[1];
			/**
			 * 接收到验证请求，私钥解密，比对验证码
			 */
			if (tag.equals("check")) {
				String receivedCode = null;
				try {
					receivedCode = ((ScanActivity) context).getRSAUtil()
							.decodeByPrivate(Base64Utils.decode(value));
				} catch (Exception e) {
					e.printStackTrace();
				}
				String correctCode = null;
				try {
					correctCode = MD5Util.getMD5(code
							+ context.getString(R.string.app_name));
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				}
				if (receivedCode != null && correctCode != null
						&& correctCode.equals(receivedCode)) {
					String ip = result[2];
					String aesCode = new RandomNum(5).getCode();
					aesUtil = AESUtil.getInstance(aesCode + code);
					try {
						sendMsg = Base64Utils.encode(((ScanActivity) context)
								.getRSAUtil().encodeByPrivate(aesCode));
					} catch (Exception e) {
						e.printStackTrace();
					}
					((ScanActivity) context).finish();
					ConnectionManager.sendMsg(ip, "send," + sendMsg);
				}
			}
			/**
			 * 接收到验证成功的通知，公钥解密取出对称加密密钥
			 */
			else if (tag.equals("send")) {
				String receivedCode = null;
				try {
					receivedCode = ((ScanActivity) context).getRSAUtil()
							.decodeByPublic(Base64Utils.decode(value),
									publicKey);
				} catch (Exception e) {
					e.printStackTrace();
				}
				aesUtil = AESUtil.getInstance(receivedCode + myCode);
				((ScanActivity) context).finish();
			}
			/**
			 * 开始发文件
			 */
			else if (tag.equals("start")) {
				String fileName = result[2];
				FileInfo fileInfo = new FileInfo(value, 8879);
				fileInfo.setFileName(fileName);
				PageLoad.ReceiveFileFromSender(fileInfo);
			}
			/**
			 * 接收到请求传送信息，弹出对话框让用户输入验证码
			 */
			else {
				publicKey = value;
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("ip", tag);
				msg.setData(bundle);
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}
		return message;
	}

	public void clear() {
		clientSet.clear();
		clientList.clear();
		clientNumber = 0;
	}

	private String sendMsg = null;
	private String myCode;

	private void showInputDialog(final String ip) {
		final EditText et = new EditText(context);
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		new AlertDialog.Builder(context)
				.setTitle(context.getString(R.string.input_check_code))
				.setView(et)
				.setPositiveButton(context.getString(R.string.comfirm),
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String input = et.getText().toString().trim();

								if (input.length() != 4) {
									Toast.makeText(
											context,
											context.getString(R.string.wrong_check_code),
											Toast.LENGTH_SHORT).show();
								} else {
									try {
										myCode = input;
										input = MD5Util.getMD5(input
												+ context
														.getString(R.string.app_name));
										sendMsg = Base64Utils
												.encode(((ScanActivity) context)
														.getRSAUtil()
														.encodeByPublic(input,
																publicKey));
									} catch (Exception e) {
										e.printStackTrace();
									}
									new Thread() {
										public void run() {
											ConnectionManager.sendMsg(
													ip,
													"check,"
															+ sendMsg
															+ ","
															+ ConnectionManager
																	.getIpAddress());
										};
									}.start();
								}
							}
						})
				.setNegativeButton(context.getString(R.string.cancel), null)
				.show();
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getDistance(User user) {
		double distance = 0;
		distance = ConnectionManager.locationManager.calculateDistance(
				ConnectionManager.locationManager.getLatitude(),
				ConnectionManager.locationManager.getLontitude(),
				user.getLatitude(), user.getLontitude());
		return distance;
	}

	public void stopListen() {
		flag = false;
		clear();
	}

	public void beginListen() {
		flag = true;
		while (flag) {
			Socket socket;
			try {
				socket = server.accept();
				new Thread(new ServerRunable(socket)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ServerRunable implements Runnable {

		Socket socket = null;

		public ServerRunable(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			String line = null;
			InputStream input;

			try {
				input = socket.getInputStream();
				BufferedReader bff = new BufferedReader(new InputStreamReader(
						input));
				// 半关闭socket
				socket.shutdownOutput();
				// 获取客户端的信息
				while ((line = bff.readLine()) != null) {
					handleMessage(line);
				}
				// 关闭输入输出流
				bff.close();
				input.close();
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}