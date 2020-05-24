package com.c123.billbuddy.client;


import java.util.ArrayList;
import java.util.Calendar;

import org.openspaces.core.GigaSpace;
import org.springframework.transaction.annotation.Transactional;

import com.c123.billbuddy.model.AccountStatus;
import com.c123.billbuddy.model.CategoryType;
import com.c123.billbuddy.model.Merchant;
import com.gigaspaces.document.DocumentProperties;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;

/** 
 * Merchant Feeder class gets merchant name list which is stored in merchants data member. 
 * The class performing loop on the list and create user class and write it into space
 * 
 * The Class also enables creating one static user & write into the space
 * 
 * @author 123Completed
 */


public class MerchantFeeder {
	
	
	@SuppressWarnings("serial")
	private static final ArrayList<String> merchants=new ArrayList<String>(){{
	    add("Like Pace"); add("Konegsad"); add("SomeDisk"); add("Swakowsky"); 
	    add("Green Head band"); add("Shiruckou"); add("Eagle"); add("Lohitech"); 
	    add("The musicals"); add("SoccerMaster"); add("Fort"); add("2-Times"); 
	    add("Mazalaty"); add("jewelry 4 U"); add("Gems"); add("Hautika"); 

	}};
	
	public MerchantFeeder(){
		
	}
	
	// Method loads a list of merchants from the DataUtil class that serves as a user repository.
	// It then writes them into the space using a GigaSpace space proxy.
	// It reads users from the space & displays them into the console.
    
	public static void loadData(GigaSpace gigaSpace) throws Exception {
		System.out.println("Starting Merchant Feeder");
		System.out.println("Method: loadData - loads all merchants into the space");
		
		registerContractType(gigaSpace);
		
        // merchantAccountId will serve as the Unique Identifier value
        
		Integer merchantAccountId = 1;

        // for each merchant in the merchantList do:
        
        for (String merchantName : MerchantFeeder.merchants) {
        	
        	createMerchant(merchantAccountId, merchantName,gigaSpace);
            merchantAccountId++;               
        }
        	
        System.out.println("Stopping Merchant Feeder");
        
    }

	   @Transactional
	    private static void createMerchant(Integer merchantAccountId, String merchantName,GigaSpace gigaSpace) {
	    	Merchant templateMerchant = new Merchant();
	        templateMerchant.setMerchantAccountId(merchantAccountId);
	        
	        Merchant foundMerchant = gigaSpace.read(templateMerchant);

	        
	        if (foundMerchant == null) {
	         	
	        	Merchant merchant = new Merchant();
	        	
	        	merchant.setName(merchantName);
	            merchant.setReceipts(0d);
	            merchant.setFeeAmount(0d);
	            
	            // Select Random Category
	            
	            CategoryType[] categoryTypes = CategoryType.values();
	            merchant.setCategory(categoryTypes[(int) ((categoryTypes.length - 1) * Math.random())]);
	            merchant.setStatus(AccountStatus.ACTIVE);
	            merchant.setMerchantAccountId(merchantAccountId);
	            
	            // Merchant is not found, let's add it.
	            
	            // DO NOT WRITE MERCHANTS
	             gigaSpace.write(merchant);
	            
	             System.out.println(String.format("Added Merchant object with name '%s'", merchant.getName()));
	            
	            createMerchantContract(merchantAccountId,gigaSpace);
	        }
			
		}

	
	
	
    /** 
     * Creates SpaceDocument with the terms between Merchant and BillBuddy 
     */ 
    private static void createMerchantContract(Integer merchantId, GigaSpace gigaSpace) {
    	
    	Calendar calendar = Calendar.getInstance();

		DocumentProperties documentProperties = new DocumentProperties();
	
		// 1. Create the properties:
		documentProperties.setProperty("transactionPercentFee", 
				Double.valueOf(Math.random()/10)).
		setProperty("contractDate", calendar.getTime()).
		setProperty("merchantId", merchantId);
	
    	// 2. Create the document using the type name and properties: 
        
		SpaceDocument document = new SpaceDocument("ContractDocument", documentProperties);
        
        // 3. Write the document to the space:
        
		gigaSpace.write(document);
        
//        System.out.println(String.format("Added MerchantContract object with id '%s'", document.getProperty("id")));
		
	}
    
    /** 
     * Register ContractDocument SpaceDocument into Space 
     */ 
    private static void registerContractType(GigaSpace gigaSpace) {
        
    	// Create type descriptor and  Other type settings
        
    	SpaceTypeDescriptor typeDescriptor = 
            new SpaceTypeDescriptorBuilder("ContractDocument").idProperty("id", true).routingProperty("merchantId").create();
            
    	// Register type:
        
    	gigaSpace.getTypeManager().registerTypeDescriptor(typeDescriptor);
    }	
}
