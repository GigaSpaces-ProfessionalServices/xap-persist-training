package com.c123.billbuddy.custom;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.openspaces.persistency.patterns.ManagedEntriesSpaceDataSource;

import com.c123.billbuddy.dao.MerchantDAO;
import com.c123.billbuddy.dao.PaymentDAO;
import com.c123.billbuddy.dao.ProcessingFeeDAO;
import com.c123.billbuddy.dao.UserDAO;
import com.gigaspaces.datasource.DataIterator;
import com.gigaspaces.metadata.SpaceTypeDescriptor;

public class BillBuddySpaceDataSource extends ManagedEntriesSpaceDataSource {
	
	@Resource
	private HashSet<String> mEntries;
	
	@Resource
	private BasicDataSource datasource;
	
	private UserDAO userDAO;
	private MerchantDAO merchantDAO;
	private PaymentDAO paymentDAO;
	private ProcessingFeeDAO processingFeeDAO;

	@Override
	public Iterable<String> getManagedEntries() {
		System.out.println("BillBuddySpaceDataSource:getManagedEntries");
		for (String types : mEntries) {
			System.out.println("BillBuddySpaceDataSource:getManagedEntries type=" + types);
		}
		return mEntries;
	}

	public HashSet<String> getmEntries() {
		return mEntries;
	}

	public void setmEntries(HashSet<String> mEntries) {
		this.mEntries = mEntries;
	}

	public BasicDataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(BasicDataSource datasource) {
		this.datasource = datasource;
	}
	
	@PreDestroy
	public void close(){
		System.out.println("BillBuddySpaceDataSource:close");
	}

	@PostConstruct
	public void init(){
		// Initiate all the data access object
		userDAO = new UserDAO();
		merchantDAO = new MerchantDAO();
		paymentDAO = new PaymentDAO();
		processingFeeDAO = new ProcessingFeeDAO();
		if (datasource != null){
			userDAO.setDatasource(this.datasource);
			userDAO.init();
			
			merchantDAO.setDatasource(this.datasource);
			merchantDAO.init();
			
			paymentDAO.setDatasource(this.datasource);
			paymentDAO.init();
			
			processingFeeDAO.setDatasource(this.datasource);
			processingFeeDAO.init();
			
		} else {
			System.out.println("BillBuddySpaceDataSource data source missing !!!!");
		}
	}
	
	public DataIterator<SpaceTypeDescriptor> initialMetadataLoad(){
    	return null;
	}
	
	public DataIterator<Object> initialDataLoad(){
		// Load all data thru initial load using the DAO objects
		//TODO replace null with the right implementation
		ArrayList<Object> res =  null;
		return new CustomDataIterator(res);
	}
}
