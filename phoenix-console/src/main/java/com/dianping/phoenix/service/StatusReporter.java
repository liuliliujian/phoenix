package com.dianping.phoenix.service;

public interface StatusReporter {
	public void updateState(String state);

	public void log(String message);

	public void log(String message, Throwable e);
	
	public void categoryLog(String category,String subCategory,String message);
	
	public void categoryLog(String category,String subCategory,String message, Throwable e);
	
	public String getMessage(String category,String subCategory,int index);
	
	public void clearMessage(String category,String subCategory);
}
