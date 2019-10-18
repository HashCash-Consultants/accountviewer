package com.account.accountviewer.model;

public class SigninResponse {
	
   private String message;
   private String statuscode;
   private String pubkey;
   public String getPubkey() {
	return pubkey;
}
public void setPubkey(String pubkey) {
	this.pubkey = pubkey;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getStatuscode() {
	return statuscode;
}
public void setStatuscode(String statuscode) {
	this.statuscode = statuscode;
}
   
	

}
