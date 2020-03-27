package com.c123.billbuddy.custom;

import java.util.HashSet;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class BillBuddyCustomFactoryBean implements FactoryBean<BillBuddySpaceDataSource> , InitializingBean {
	
	@Resource
	private HashSet<String> managedEntries;
	
	@Resource
	private BasicDataSource datasource;
	
	private BillBuddySpaceDataSource billBuddySpaceDataSource;

	
	@Override
	public BillBuddySpaceDataSource getObject() throws Exception {
		// Get space data sources if one is not available create one
		if (billBuddySpaceDataSource == null ) {
			billBuddySpaceDataSource = new BillBuddySpaceDataSource();
			//TODO set the needed properties
			billBuddySpaceDataSource.init();
		}
		
		return billBuddySpaceDataSource;
	}

	@Override
	public Class<?> getObjectType() {
		return null;//TODO replace null with relevant class type to return
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	// Get managed entities by the space data sources
	public HashSet<String> getManagedEntries() {
		return managedEntries;
	}

	// Set managed entities by the space data sources
	public void setManagedEntries(HashSet<String> managedEntries) {
		this.managedEntries = managedEntries;
	}

	// Get the data source to use the database
	public BasicDataSource getDatasource() {
		return datasource;
	}

	// Get the data source to use the database
	public void setDatasource(BasicDataSource datasource) {
		this.datasource = datasource;
	}
	
}
