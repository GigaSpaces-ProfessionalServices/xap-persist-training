package com.c123.billbuddy.mirror;

import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.openspaces.persistency.patterns.ManagedEntriesSpaceSynchronizationEndpoint;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.OperationsBatchData;

public class BillBuddySpaceSynchronizationEndpoint extends
		ManagedEntriesSpaceSynchronizationEndpoint {

	@Resource
	private HashSet<String> mEntries;
	
	private ContractSpaceDocumentDatabaseStorer spaceDocumentDatabaseStorer;
	
	@Resource
	private BasicDataSource datasource;
	
	public BillBuddySpaceSynchronizationEndpoint() {
		super();
		
	}

	@Override
	public Iterable<String> getManagedEntries() {
		System.out.println("BillBuddySpaceSynchronizationEndpoint:getManagedEntries");
		for (String types : mEntries) {
			System.out.println("BillBuddySpaceSynchronizationEndpoint:getManagedEntries type=" + types);
		}
		return mEntries;
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
		spaceDocumentDatabaseStorer = new ContractSpaceDocumentDatabaseStorer();
		if (datasource != null){
			spaceDocumentDatabaseStorer.setDatasource(this.datasource);
		} else {
			System.out.println("BillBuddySpaceSynchronizationEndpoint:init still NULL !!!!");
		}
		spaceDocumentDatabaseStorer.init();
	}
	
	@Override
	public void onOperationsBatchSynchronization(OperationsBatchData batchData) {
		// TODO Auto-generated method stub
		super.onOperationsBatchSynchronization(batchData);
		System.out.println("BillBuddySpaceSynchronizationEndpoint:onOperationsBatchSynchronization");
        DataSyncOperation[] operations = batchData.getBatchDataItems();
        for (DataSyncOperation operation : operations) {
        	if (operation.supportsDataAsDocument()) {
        		SpaceDocument contract = operation.getDataAsDocument();
        		System.out.println("BillBuddySpaceSynchronizationEndpoint:onOperationsBatchSynchronization write document type: " + contract.getTypeName());
        		Integer merchID =   ((Integer)contract.getProperty("merchantId"));
        		System.out.println("BillBuddySpaceSynchronizationEndpoint:onOperationsBatchSynchronization merchant ID: " + merchID);
//        		if (!spaceDocumentDatabaseStorer.isInit()){
//        			spaceDocumentDatabaseStorer.init();
//        		}
        		spaceDocumentDatabaseStorer.writeContractToDB(contract, merchID);;
        	}
        }
	}

	public BasicDataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(BasicDataSource datasource) {
		this.datasource = datasource;
	}
}
