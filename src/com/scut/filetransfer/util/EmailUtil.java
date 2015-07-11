package com.scut.filetransfer.util;

import com.scut.filetransfer.util.MultiMailsender.MultiMailSenderInfo;

public class EmailUtil {
	
	private static final String serverHost = "smtp.qq.com";
	private static final String serverPort = "25";
	private static final boolean validate = true;
	private static final String userName = "2307216524@qq.com";
	private static final String password = "hLeIIcF9";
	private static final String subject = "四位验证码";
	

	public static void sendEmail(String sender, String code){
		MultiMailSenderInfo mailInfo = new MultiMailSenderInfo();
		mailInfo.setMailServerHost(serverHost); 
	    mailInfo.setMailServerPort(serverPort); 
	    mailInfo.setValidate(validate); 
	    mailInfo.setUserName(userName); 
	    mailInfo.setPassword(password);//您的邮箱密码 
	    mailInfo.setFromAddress(userName); 
	    mailInfo.setToAddress(sender); 
	    mailInfo.setSubject(subject); 
	    mailInfo.setContent(code);
	    MultiMailsender sms = new MultiMailsender(); 
	    sms.sendTextMail(mailInfo);
	    
	}

}
