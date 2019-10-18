package com.account.accountviewer.service;

import org.springframework.web.bind.annotation.RequestBody;

import com.account.accountviewer.model.BalanceRequest;
import com.account.accountviewer.model.BalanceResponse;
import com.account.accountviewer.model.FundTransferRequest;
import com.account.accountviewer.model.FundTransferResponse;
import com.account.accountviewer.model.KeyResponse;
import com.account.accountviewer.model.SendRequest;
import com.account.accountviewer.model.SendResponse;
import com.account.accountviewer.model.SigninRequest;
import com.account.accountviewer.model.SigninResponse;




public interface AccountServiceModel {

        KeyResponse keyGeneration();
	    SigninResponse sign(SigninRequest request);
        BalanceResponse checkBalance(BalanceRequest balanceRequest);
        SendResponse sendhcx(SendRequest request);
        FundTransferResponse fundTransfer(FundTransferRequest request);
	     
	
	
}
