package com.bank.model;

import com.bank.enums.AccountType;
import com.bank.enums.Status;

public class Account {
    
	private int userId;
    private long accountNo;
    private AccountType accountType;
    private long balance;
    private int branchId;
    private Status accountStatus = Status.ACTIVE;
    
    
	public int getUserId() {
		return userId;
	}
	public Status getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(Status accountStatus) {
		this.accountStatus = accountStatus;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}
	public AccountType getAccountType() {
		return accountType;
	}
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
	public long getBalance() {
		return balance;
	}
	public void setBalance(long balance) {
		this.balance = balance;
	}
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

    //Getters and Setters
	@Override
    public String toString() {
        return "Account{" +
                "userId=" + userId +
                ", accountNo=" + accountNo +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", branchId=" + branchId +
                ", accountStatus=" + accountStatus +
                '}';
    }
}