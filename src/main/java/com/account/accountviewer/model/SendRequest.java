package com.account.accountviewer.model;

public class SendRequest {
	
	private String secretseed;
	private String destinationkey;
	private String amount;
	private String option;
	private String memo;
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSecretseed() {
		return secretseed;
	}
	public void setSecretseed(String secretseed) {
		this.secretseed = secretseed;
	}
	public String getDestinationkey() {
		return destinationkey;
	}
	public void setDestinationkey(String destinationkey) {
		this.destinationkey = destinationkey;
	}
	

}
