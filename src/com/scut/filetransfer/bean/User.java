package com.scut.filetransfer.bean;
/**
 * User¿‡
 * 
 * @author ccz
 * 
 */
public class User {

	private String ipAddress;
	private String phoneModel;
	private double latitude;
	private double lontitude;

	public User() {
	}

	public User(String ipAddress, String phoneModel, double latitude,
			double lontitude) {
		super();
		this.ipAddress = ipAddress;
		this.phoneModel = phoneModel;
		this.latitude = latitude;
		this.lontitude = lontitude;
	}

	@Override
	public int hashCode() {
		return this.ipAddress.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null != obj && obj instanceof User) {
			User p = (User) obj;
			if (ipAddress.equals(p.getIpAddress())) {
				return true;
			}
		}
		return false;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPhoneModel() {
		return phoneModel;
	}

	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLontitude() {
		return lontitude;
	}

	public void setLontitude(double lontitude) {
		this.lontitude = lontitude;
	}
}