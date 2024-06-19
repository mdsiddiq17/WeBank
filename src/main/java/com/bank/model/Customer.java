package com.bank.model;

public class Customer extends User{
    
    private int userId;
    private long aadhaar;
    private String pan;
    // Getters and Setters
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getAadhaar() {
		return aadhaar;
	}
	public void setAadhaar(long aadhaar) {
		this.aadhaar = aadhaar;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	
	public String toString() {
        return "Customer{" +
                "userId=" + userId +
                ", aadhaar=" + aadhaar +
                ", pan='" + pan + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", mobile=" + getMobile() +
                ", address='" + getAddress() + '\'' +
                ", dob=" + getDob() +
                ", status=" + getStatus() +
                ", gender=" + getGender() +
                ", role=" + getRole() +
                '}';
    }
  
}
