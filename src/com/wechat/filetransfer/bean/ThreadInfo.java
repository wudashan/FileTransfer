package com.wechat.filetransfer.bean;
/**
 * 线程实体集
 * @author Wise
 *
 */
public class ThreadInfo {
		private int id;
		private String IP;
		private int Port;
		private int start;
		private int end;
		private int finished;
		
		
		
		
		
		public ThreadInfo(String iP, int port,int id) {
			super();
			this.id = id;
			start = 0;
			end = 0;
			finished = 0;
			IP = iP;
			Port = port;
		}
		public ThreadInfo() {
			super();
		}
		public ThreadInfo(int id, String iP, int port, int start, int end,
				int finished) {
			super();
			this.id = id;
			IP = iP;
			Port = port;
			this.start = start;
			this.end = end;
			this.finished = finished;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getIP() {
			return IP;
		}
		public void setIP(String iP) {
			IP = iP;
		}
		public int getPort() {
			return Port;
		}
		public void setPort(int port) {
			Port = port;
		}
		public int getStart() {
			return start;
		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		public int getFinished() {
			return finished;
		}
		public void setFinished(int finished) {
			this.finished = finished;
		}
		@Override
		public String toString() {
			return "ThreadInfo [id=" + id + ", IP=" + IP + ", Port=" + Port
					+ ", start=" + start + ", end=" + end + ", finished="
					+ finished + "]";
		}
		
}
