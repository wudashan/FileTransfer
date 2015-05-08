package com.wechat.filetransfer.db;

import java.util.List;

import com.wechat.filetransfer.bean.ThreadInfo;

/**
 * 数据访问接口
 * @author Wise
 *
 */
public interface ThreadDAO {
		public boolean isExists(String IP,int Port,int thread_id);
		public void insertThread(ThreadInfo threadInfo);
		public void deleteThread(String IP,int Port,int thread_id);
		public void updateThread(String IP,int Port,int thread_id,int finished);
		public List<ThreadInfo> getThreads(String IP,int Port);
}
