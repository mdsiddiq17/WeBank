package com.bank.model;

public class Employee extends User{
    private int branchId;
    private int userId;
    
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@Override
    public String toString() {
        return "Employee{" +
                "branchId=" + branchId +
                ", userId=" + userId +
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
