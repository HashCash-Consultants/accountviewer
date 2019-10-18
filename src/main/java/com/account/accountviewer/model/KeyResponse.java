package com.account.accountviewer.model;

public class KeyResponse {

	private String privkey;
	private String pubkey;
	private String statuscode;
	
	public String getStatuscode() {
		return statuscode;
	}
	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}
	public String getPrivkey() {
		return privkey;
	}
	public void setPrivkey(String privkey) {
		this.privkey = privkey;
	}
	public String getPubkey() {
		return pubkey;
	}
	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}
	
}
