package com.c123.billbuddy.events;


import java.util.Calendar;

import javax.annotation.Resource;


import com.c123.billbuddy.model.Merchant;
import com.c123.billbuddy.model.Payment;
import com.c123.billbuddy.model.ProcessingFee;
import com.c123.billbuddy.model.TransactionStatus;
import com.gigaspaces.document.SpaceDocument;
import com.j_spaces.core.client.SQLQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openspaces.core.GigaSpace;
import org.openspaces.events.*;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.polling.Polling;


/** 
 * AuditPaymentEventContainer class. 
 *  
 * Pooling Event starts when new Payment with Transaction status AUDIT written into space.
 * SpaceDataEvent reads Merchant by Id, Contract Document and executing Processing Fee 
 * transaction between Merchant and BillBuddy 
 * 
 * @author 123Completed
 */

@EventDriven
@Polling(gigaSpace="gigaSpace")
@TransactionalEvent
public class ProcessingFeePollingEventContainer {
    private final Log log = LogFactory.getLog(ProcessingFeePollingEventContainer.class);
    @Resource
    private GigaSpace gigaSpace;
       	
	@EventTemplate
	SQLQuery<Payment> unprocessedData() {
		log.info("Starting ProcessingFeeTransaction EventTemplate for Payment with NEW status.");
		log.info("templete will be more efficient but we use SQLQuery for course training.");
		
        SQLQuery<Payment> template = new SQLQuery<Payment>(Payment.class, "status = ?" );
        template.setParameter(1, TransactionStatus.AUDITED);
        return template;
    }
 

    @SpaceDataEvent
    public Payment processPayments(Payment payment) {
    	
    	log.info("Starting ProcessingFeeTransaction SpaceDataEvent.");
    	
    	// Read Merchant Account
        log.info("Read Merchant Id: "  + payment.getReceivingMerchantId() + " account.");
        Merchant merchantTemplate = new Merchant();
        merchantTemplate.setMerchantAccountId(payment.getReceivingMerchantId());
               
        Merchant merchant = gigaSpace.read(merchantTemplate);
        
       
        // Read Contract Account
        log.info("Read Contract Id: "  + payment.getReceivingMerchantId() + " account.");

        SQLQuery<SpaceDocument> queryContract = new SQLQuery<SpaceDocument>("ContractDocument", "merchantId = ?").setProjections("transactionPercentFee");
        queryContract.setParameter(1, payment.getReceivingMerchantId());
        
        SpaceDocument contract = gigaSpace.read(queryContract);
      
        // Get transactionPercentFee 
        Double transactionFeeAmount = ((Double)contract.getProperty("transactionPercentFee")) * payment.getPaymentAmount();
    	
        // Withdraw payment amount from merchant account
        updateMerchantBalance(merchant, transactionFeeAmount);
    	
        ProcessingFee processingFee = new ProcessingFee();
        
        processingFee.setDescription(payment.getDescription());
        processingFee.setDependentPaymentId(payment.getPaymentId());
        processingFee.setPayingAccountId(merchant.getMerchantAccountId());
        
        processingFee.setAmount(transactionFeeAmount);
        
        Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
        processingFee.setCreatedDate(calendar.getTime());
        
        processingFee.setStatus(TransactionStatus.PROCESSED);
        
        // Write the ProcessingFee object
        gigaSpace.write(processingFee);
             
        // Set payment status
    	payment.setStatus(TransactionStatus.PROCESSED);
    	
        return payment;
    }
    
    private void updateMerchantBalance(Merchant merchant, Double transactionFeeAmount) {
    	log.info("ProcessingFeeTransaction add " + transactionFeeAmount + 
    			" from merchant: " + merchant.getName());
    	
    	merchant.setFeeAmount(merchant.getFeeAmount() + transactionFeeAmount);
  
    	gigaSpace.write(merchant);
    	
    	log.info("ProcessingFeeTransaction updates merchants transactionFeeAmount. Merchant: " + merchant.getName() +
    			" new transactionFeeAmount is " + merchant.getFeeAmount());
		
    }
    
 
}
