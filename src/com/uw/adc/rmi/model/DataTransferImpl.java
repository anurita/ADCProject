package com.uw.adc.rmi.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DataTransferImpl extends UnicastRemoteObject implements DataTransfer{	

	public DataTransferImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String key;
	private String value;
	
	@Override
	public String getKey() throws RemoteException {
		return key;
	}
	@Override
	public void setKey(String key) throws RemoteException {
		this.key = key;
	}
	@Override
	public String getValue() throws RemoteException {
		return value;
	}
	@Override
	public void setValue(String value) throws RemoteException {
		this.value = value;
	}
	
	@Override
	public String toString(){
		return "Data ["
				+ (key != null ? "key=" + key + ", " : "") 
				+ (value != null ? "value=" + value : "") + "]";
	}
}
