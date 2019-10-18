package com.account.accountviewer.serviceimpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.hashcash.sdk.AssetTypeNative;
import org.hashcash.sdk.CreateAccountOperation;
import org.hashcash.sdk.KeyPair;
import org.hashcash.sdk.Memo;
import org.hashcash.sdk.Network;
import org.hashcash.sdk.PaymentOperation;
import org.hashcash.sdk.Server;
import org.hashcash.sdk.Transaction;
import org.hashcash.sdk.responses.AccountResponse;
import org.hashcash.sdk.responses.SubmitTransactionResponse;

import com.account.accountviewer.model.BalanceRequest;
import com.account.accountviewer.model.BalanceResponse;
import com.account.accountviewer.model.FundTransferRequest;
import com.account.accountviewer.model.FundTransferResponse;
import com.account.accountviewer.model.KeyResponse;
import com.account.accountviewer.model.SendRequest;
import com.account.accountviewer.model.SendResponse;
import com.account.accountviewer.model.SigninRequest;
import com.account.accountviewer.model.SigninResponse;
import com.account.accountviewer.service.AccountServiceModel;

@Service
public class AccountService implements AccountServiceModel 
{
private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private Environment env;
	
	@Override
	public KeyResponse keyGeneration() {
		KeyResponse response=new KeyResponse();
		KeyPair pair = KeyPair.random();
		//System.out.println(new String(pair.getSecretSeed()));
		//System.out.println(new String(pair.getAccountId()));
		logger.info("===KeyGenegation===");
		logger.info(new String(pair.getSecretSeed()));
		logger.info(new String(pair.getAccountId()));
	    response.setPrivkey(new String(pair.getSecretSeed()));
	    response.setPubkey(new String(pair.getAccountId()));
	    response.setStatuscode("1");
	    
		return response;
	}

	@Override
	public SigninResponse sign(SigninRequest request) {
		SigninResponse response=new SigninResponse();
		try
		 {
			 KeyPair ksecret=KeyPair.fromSecretSeed(request.getSecretkey());
			 String  pubkey=ksecret.getAccountId();
			 //System.out.println("Your Addres.."+pubkey); 
			 logger.info("====Signin===");
			 logger.info("Your Addres.."+pubkey);
			 response.setPubkey(pubkey);
			 response.setMessage("Login Successful");
			 response.setStatuscode("1");
			 return response;
		 }
		 catch(org.hashcash.sdk.FormatException e)
		 {
			 //System.out.println("Invalid Address");
			 logger.info("Invalid Address");
			 response.setMessage("Please check your secret key");
			 response.setStatuscode("0");
			 return response;
		 }
	}

	@Override
	public BalanceResponse checkBalance(BalanceRequest balanceRequest) {
		
		BalanceResponse balanceResponse=new BalanceResponse();
		KeyPair source= KeyPair.fromAccountId(balanceRequest.getPubkey());
		String bal=null;
		String url=env.getProperty("url");
		
		try {
				Server server=new Server(url);
				AccountResponse accountResponse=server.accounts().account(source);
				if(accountResponse==null)
				{
					System.out.println("Not Found");
				}
				else
				{
					
					logger.info("Balances for account " + balanceRequest.getPubkey());
					
					for (AccountResponse.Balance balance : accountResponse.getBalances()) {
						 if(balance.getAssetType().equalsIgnoreCase("native"))
			    	    {
			    		  bal=balance.getBalance();
			    		  balanceResponse.setBalance(bal);
			    		  balanceResponse.setMessage("Balance Found");
			    		  balanceResponse.setStatuscode("1");
			    		  break;
			    	    }
					}
				}
				
				
			} catch (org.hashcash.sdk.requests.ErrorResponse  e) {
				// TODO Auto-generated catch block
				balanceResponse.setBalance("0");
				balanceResponse.setStatuscode("0");
				balanceResponse.setMessage("This account is currently inactive. To activate it, send at least 20 HCX to the public key displayed above");
				logger.info("This account is currently inactive. To activate it, send at least 20 HCX to the public key displayed above");
				System.out.println(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return balanceResponse;
		
		}

	@Override
	public SendResponse sendhcx(SendRequest request) {
		SendResponse res=new SendResponse();
		double base_reserve=10;
		BigDecimal min_balance=BigDecimal.ZERO;
		BigDecimal netwrok_fee=new BigDecimal("0.00001");
		Transaction transaction=null;
		SubmitTransactionResponse submitTransactionResponse=null;
		String url=env.getProperty("url");
		network();
		
		Server server = new Server(url);
      KeyPair  source = KeyPair.fromSecretSeed(request.getSecretseed());
        String nativebalance=balancenetwork(new String(source.getAccountId()));
       KeyPair destination = KeyPair.fromAccountId(request.getDestinationkey());
		AccountResponse sourceAccount;
		try {
			sourceAccount = server.accounts().account(source);
			if(nativebalance!=null)
	        {
	           int counter_entry=sourceAccount.getSubentryCount();	
	           min_balance=new BigDecimal((2+counter_entry)*10);
	        }
			BigDecimal maxsend=new BigDecimal(nativebalance).subtract(new BigDecimal(String.valueOf(min_balance.add(netwrok_fee))));
			if(new BigDecimal(request.getAmount()).compareTo(maxsend)==1)
			{
				res.setMessage("This transaction would make balance go below minimum balance");
				logger.info("This transaction would make balance go below minimum balance");
				res.setStatuscode("0");
				
			}
			else
			{
				
				int opt=Integer.parseInt(request.getOption());
				Memo memo=Memo.none();
				if(opt==1)
				{
				   memo=Memo.text(request.getMemo()) ;
				}
				else if(opt==2)
				{
					memo=Memo.id(Long.parseLong(request.getMemo()));
				}
				else if(opt==3)
				{
					memo=Memo.hash(request.getMemo());
				}
				else if(opt==4)
				{
					memo=Memo.returnHash(request.getMemo());
				}
				
				 transaction = new Transaction.Builder(sourceAccount)
				        .addOperation(new PaymentOperation.Builder(destination,new AssetTypeNative(),request.getAmount()).build())
				        .addMemo(memo)
				        .setTimeout(5000)
				       .build();
				transaction.sign(source);
				SubmitTransactionResponse response = server.submitTransaction(transaction);
				  if(response.isSuccess()){
					  logger.info("Inside Send");
				   logger.info("Success!");
				   logger.info(response.toString());
				  logger.info("Hash of transaction: "+ response.getHash());
				 res.setMessage("HCX send successful");
				 res.setStatuscode("1");
				}
				  else
				  {
					  
					  System.out.println(response.getExtras().getResultCodes().getTransactionResultCode());
					  System.out.println(response.getExtras().getResultCodes().getOperationsResultCodes());
					  ArrayList<String>list=response.getExtras().getResultCodes().getOperationsResultCodes();
					  String message=list.get(0);
					  if(message.equals("op_no_destination"))
					  {
						  if(request.getAmount().equals("20"))
							{
								network();
								server = new Server(url);
						       source = KeyPair.fromSecretSeed(request.getSecretseed());
							   destination = KeyPair.fromAccountId(request.getDestinationkey());
							   try {
								sourceAccount = server.accounts().account(source);
								CreateAccountOperation op = new CreateAccountOperation.Builder(destination,"20").build();
								 transaction = new Transaction.Builder(sourceAccount).addOperation(op).addMemo(Memo.text("CreateAcc"))
									.setTimeout(5000)
									.build();
								transaction.sign(source);
								System.out.println("seq no: "+ transaction.getSequenceNumber());
								 response = server.submitTransaction(transaction);
								  if(response.isSuccess()){
								  logger.info("Account create inside send");
								  logger.info("Success!");
								  logger.info("Response is",response);
								  res.setStatuscode("1");
								  res.setMessage("Your Account have been activated");
								  }
								  else
								  {
									  logger.info("response is: "+ response.getExtras().getResultCodes().getTransactionResultCode());
									  logger.info("response is: "+ response.getExtras().getResultCodes().getOperationsResultCodes());
									  res.setMessage("Error");
									  res.setStatuscode("0");
								  }
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								res.setMessage("Error");
								res.setStatuscode("0");
								}
							  }
							else
							{
								res.setMessage("Please activate your account by sending 20 HCX");
								logger.info("Please activate your account by sending 20 HCX");
								res.setStatuscode("0");
								
							} 
					  }
					  
				  }
				
			}
			
			
			
			
			  
		} catch (IOException e) {
		   
			
			 
			
			System.out.println(e.getMessage());
			 e.printStackTrace();
		}
		
		return res;
		
		
	}
		
	
public void network()
{
	String passphrase=env.getProperty("passphrase");
	Network network = new Network(passphrase);
	Network.use(network);
	
}

public String  balancenetwork(String address)
{
	KeyPair source= KeyPair.fromAccountId(address);
	String bal=null;
	String url=env.getProperty("url");
	Transaction transaction=null;
	SubmitTransactionResponse submitTransactionResponse=null;
	try {
			Server server=new Server(url);
			AccountResponse accountResponse=server.accounts().account(source);
			
			logger.info("Balances for account " + address);
			
			for (AccountResponse.Balance balance : accountResponse.getBalances()) {
				 if(balance.getAssetType().equalsIgnoreCase("native"))
	    	    {
	    		  bal=balance.getBalance();
	    		  break;
	    	     }
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	
	return bal;
}

@Override
public FundTransferResponse fundTransfer(FundTransferRequest request) {

	String url=env.getProperty("url");
	String fromsecretseesd=env.getProperty("root");
	Transaction transaction=null;
	SubmitTransactionResponse submitTransactionResponse=null;
	FundTransferResponse res=new FundTransferResponse();
	network();
	Server server = new Server(url);
    KeyPair source = KeyPair.fromSecretSeed(fromsecretseesd);
	KeyPair destination = KeyPair.fromAccountId(request.getDestkey());
	
	AccountResponse sourceAccount;
	
		try {
			sourceAccount = server.accounts().account(source);
			transaction = new Transaction.Builder(sourceAccount)
			        .addOperation(new PaymentOperation.Builder(destination,new AssetTypeNative(),request.getAmount()).build())
			        
			        .setTimeout(5000)
			       .build();
			transaction.sign(source);
			SubmitTransactionResponse response = server.submitTransaction(transaction);
			  if(response.isSuccess()){
			 logger.info("==Fund Transfer==");
			  logger.info("Success");
			  logger.info(response.toString());
			  logger.info("Hash of transaction: ",response.getHash());
			 res.setMessage("HCX send successful");
			 res.setStatuscode("1");
			}
			  else
			  {
				  
				  System.out.println(response.getExtras().getResultCodes().getTransactionResultCode());
				  System.out.println(response.getExtras().getResultCodes().getOperationsResultCodes());
				  ArrayList<String>list=response.getExtras().getResultCodes().getOperationsResultCodes();
				  String message=list.get(0);
				  if(message.equals("op_no_destination"))
				  {
					  if(request.getAmount().equals("20"))
						{
							network();
							server = new Server(url);
					       source = KeyPair.fromSecretSeed(fromsecretseesd);
						   destination = KeyPair.fromAccountId(request.getDestkey());
						   try {
							sourceAccount = server.accounts().account(source);
							CreateAccountOperation op = new CreateAccountOperation.Builder(destination,"20").build();
							 transaction = new Transaction.Builder(sourceAccount).addOperation(op).addMemo(Memo.text("CreateAcc"))
								.setTimeout(5000)
								.build();
							transaction.sign(source);
							System.out.println("seq no: "+ transaction.getSequenceNumber());
							 response = server.submitTransaction(transaction);
							  if(response.isSuccess()){
								  logger.info("==Fund Transfer Create Account==");
								  logger.info("Success");
								  logger.info("Hash of transaction: ",response.getHash());
							  res.setStatuscode("1");
							  res.setMessage("Your Account have been activated");
							  }
							  else
							  {
								  System.out.println("response is: "+ response.getExtras().getResultCodes().getTransactionResultCode());
								  res.setMessage("Error");
								  res.setStatuscode("0");
							  }
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							res.setMessage("Error");
							res.setStatuscode("0");
							}
						  }
						else
						{
							res.setMessage("Please activate your account by sending 20 HCX");
							logger.info("Please activate your account by sending 20 HCX");
							res.setStatuscode("0");
							
						} 
				  }
		} 
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
}
}





	


