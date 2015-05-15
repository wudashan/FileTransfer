package com.scut.filetransfer.util;

import java.io.IOException;
import java.net.ServerSocket;

import android.util.Log;

public class PortUtil {
	public static final int PORT =  8879;
	public static boolean isPortAvailable(int port) {
	    try {
	        ServerSocket server = new ServerSocket(port);
	        Log.i("PortUtil", "The port is available.");
	        server.close();
	        return true;
	    } catch (IOException e) {
	    	 Log.i("PortUtil", "The port is occupied.");
	    }
	    return false;
	}
}
