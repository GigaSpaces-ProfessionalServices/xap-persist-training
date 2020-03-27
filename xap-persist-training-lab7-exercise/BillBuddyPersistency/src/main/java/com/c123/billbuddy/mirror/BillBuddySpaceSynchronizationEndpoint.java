package com.c123.billbuddy.mirror;

import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.gigaspaces.sync.SpaceSynchronizationEndpoint;
import org.apache.commons.dbcp.BasicDataSource;
import org.openspaces.persistency.patterns.ManagedEntriesSpaceSynchronizationEndpoint;

import com.c123.billbuddy.dao.MerchantDAO;
import com.c123.billbuddy.dao.PaymentDAO;
import com.c123.billbuddy.dao.ProcessingFeeDAO;
import com.c123.billbuddy.dao.UserDAO;
import com.c123.billbuddy.model.Merchant;
import com.c123.billbuddy.model.Payment;
import com.c123.billbuddy.model.ProcessingFee;
import com.c123.billbuddy.model.User;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.OperationsBatchData;

public class BillBuddySpaceSynchronizationEndpoint extends SpaceSynchronizationEndpoint {

	@Resource
	private HashSet<String> mEntries;
	
	private UserDAO userDAO;
	private MerchantDAO merchantDAO;
	private PaymentDAO paymentDAO;
	private ProcessingFeeDAO processingFeeDAO;
	
	@Resource
	private BasicDataSource datasource;
	
	public BillBuddySpaceSynchronizationEndpoint() {
		super();
		
	}

	public HashSet<String> getmEntries() {
		return mEntries;
	}

	public void setmEntries(HashSet<String> mEntries) {
		this.mEntries = mEntries;
	}


	@PreDestroy
	public void close(){
		System.out.println("BillBuddySpaceSynchronizationEndpoint:close");
	}

	@PostConstruct
	public void init(){
		userDAO = new UserDAO();
		merchantDAO = new MerchantDAO();
		paymentDAO = new PaymentDAO();
		processingFeeDAO = new ProcessingFeeDAO();
		if (datasource != null){
			userDAO.setDatasource(this.datasource);
			userDAO.initWithCreateIfMissing();
			
			merchantDAO.setDatasource(this.datasource);
			merchantDAO.initWithCreateIfMissing();
			
			paymentDAO.setDatasource(this.datasource);
			paymentDAO.initWithCreateIfMissing();
			
			processingFeeDAO.setDatasource(this.datasource);
			processingFeeDAO.initWithCreateIfMissing();
			
		} else {
			System.out.println("BillBuddySpaceSynchronizationEndpoint:init data source missing !!!!");
		}
		
	}
	
	@Override
	public void onOperationsBatchSynchronization(OperationsBatchData batchData) {
		
		super.onOperationsBatchSynchronization(batchData);
		System.out.println("BillBuddySpaceSynchronizationEndpoint:onOperationsBatchSynchronization");
        DataSyncOperation[] operations = batchData.getBatchDataItems();
        for (DataSyncOperation operation : operations) {
        	if (operation.supportsDataAsObject()) {
				//TODO Finish the missing implementation
        	}
        }
	}

	public BasicDataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(BasicDataSource datasource) {
		this.datasource = datasource;
	}
	
	private void storeObject(Object  obj){
		if(obj instanceof User){
			userDAO.writeObjectToDB(obj);
			return;
		}
		
		if(obj instanceof Merchant){
			merchantDAO.writeObjectToDB(obj);
			return;
		}
		
		if(obj instanceof Payment){
			paymentDAO.writeObjectToDB(obj);
			return;
		}
		
		if(obj instanceof ProcessingFee){
			processingFeeDAO.writeObjectToDB(obj);
			return;
		}		
	}
}
