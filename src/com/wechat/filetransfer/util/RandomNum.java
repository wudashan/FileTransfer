package com.wechat.filetransfer.util;

import java.util.Random;

public class RandomNum {

	private String numStrTmp = "";
    private String numStr = "";
    private int[] numArray;
    
	public RandomNum(int length) {
		// TODO Auto-generated constructor stub
		numArray = new int[length];
	}
	private void initNum() {
        numStr = "";
        numStrTmp = "";
        for (int i = 0; i < numArray.length; i++) {
            int numIntTmp = new Random().nextInt(10);
            numStrTmp = String.valueOf(numIntTmp);
            numStr = numStr + numStrTmp;
            numArray[i] = numIntTmp;
        }
    }
	
	public String getCode() {
		initNum();
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < numArray.length; i++) {
			temp.append(numArray[i]);
		}
		return temp.toString();
	}
}
