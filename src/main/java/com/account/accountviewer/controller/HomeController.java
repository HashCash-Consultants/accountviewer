package com.account.accountviewer.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.account.accountviewer.model.BalanceRequest;
import com.account.accountviewer.model.BalanceResponse;
import com.account.accountviewer.model.FundTransferRequest;
import com.account.accountviewer.model.FundTransferResponse;
import com.account.accountviewer.model.KeyResponse;
import com.account.accountviewer.model.SendRequest;
import com.account.accountviewer.model.SendResponse;
import com.account.accountviewer.model.SigninRequest;
import com.account.accountviewer.model.SigninResponse;
import com.account.accountviewer.serviceimpl.AccountService;




@RestController
public class HomeController {
	@Autowired
	AccountService accountService;
	
	@RequestMapping(value="/keygeneration",method=RequestMethod.POST)
	public KeyResponse keyGeneration()
	{
		return accountService.keyGeneration();
	}
	
	@RequestMapping(value="/signin",method=RequestMethod.POST)
	public SigninResponse checkAddress(@RequestBody SigninRequest request)
	{
		return accountService.sign(request);
	}
	
	@RequestMapping(value="/balance",method=RequestMethod.POST)
	public BalanceResponse balance(@RequestBody BalanceRequest balanceRequest)
	{
		return accountService.checkBalance(balanceRequest);
	}
	
	@RequestMapping(value="/send",method=RequestMethod.POST)
	public SendResponse sendHCX(@RequestBody SendRequest request)
	{
		return accountService.sendhcx(request);
	}
	@RequestMapping(value="/fund",method=RequestMethod.POST)
	public FundTransferResponse fundTransfer(@RequestBody FundTransferRequest request)
	{
		return accountService.fundTransfer(request);
	} 
	
	


}
