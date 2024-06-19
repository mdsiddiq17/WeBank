package com.bank.model;

import com.bank.enums.CreditDebit;
import com.bank.enums.TransactionType;

public class Transaction {
    private String transactionId;
    private int userId;
    private long accountNo;
    private long payeeAccNo;
    private TransactionType transType;
    private long amount;
    private long closingBalance;
    private long transDateTime;
    private CreditDebit creditDebit;
    private String remarks;
    
    public String getRemarks() {
		return remarks;
	}
	public long getTransDateTime() {
		return transDateTime;
	}
	public void setTransDateTime(long transDateTime) {
		this.transDateTime = transDateTime;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public CreditDebit getCreditDebit() {
		return creditDebit;
	}
	public void setCreditDebit(CreditDebit creditDebit) {
		this.creditDebit = creditDebit;
	}
	private long createdTime;	
    
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public int getUserId() {
		return userId;
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
	public long getPayeeAccNo() {
		return payeeAccNo;
	}
	public void setPayeeAccNo(long payeeAccNo) {
		this.payeeAccNo = payeeAccNo;
	}
	public TransactionType getTransType() {
		return transType;
	}
	public void setTransType(TransactionType transType) {
		this.transType = transType;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public long getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(long closingBalance) {
		this.closingBalance = closingBalance;
	}
	
	 @Override
	    public String toString() {
	        return "Transaction{" +
	                "transactionId='" + transactionId + '\'' +
	                ", userId=" + userId +
	                ", accountNo=" + accountNo +
	                ", payeeAccNo=" + payeeAccNo +
	                ", transType=" + transType +
	                ", amount=" + amount +
	                ", closingBalance=" + closingBalance +
	                ", transDateTime=" + transDateTime +
	                ", creditDebit=" + creditDebit +
	                ", remarks='" + remarks + '\'' +
	                ", createdTime=" + createdTime +
	                '}';
	    }
}
