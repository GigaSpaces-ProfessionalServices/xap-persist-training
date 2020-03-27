package com.c123.billbuddy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.dbcp.BasicDataSource;

import com.c123.billbuddy.model.AccountStatus;
import com.c123.billbuddy.model.CategoryType;
import com.c123.billbuddy.model.CountryNames;
import com.c123.billbuddy.model.TransactionStatus;

public class BaseDAO {
	protected String dbName="custbillbuddy";
	protected Connection conn;
	protected PreparedStatement ptmt = null;
	protected ResultSet resultSet = null;

	protected boolean tableExits=false;
	protected BasicDataSource datasource;
	protected boolean init=false;
	
	protected String tableName=null;
	protected String pkColumnName=null;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public BasicDataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(BasicDataSource datasource) {
		this.datasource = datasource;
	}
	
	public String getPkColumnName() {
		return pkColumnName;
	}

	public void setPkColumnName(String pkColumnName) {
		this.pkColumnName = pkColumnName;
	}
	
	public void initWithCreateIfMissing(){
		System.out.println( getClass().getName() + ": initWithCreateIfMissing start");
		if (datasource != null) {
			try {
				conn = datasource.getConnection();
				ptmt = conn.prepareStatement(checkTableExistsSQL());
				resultSet = ptmt.executeQuery();
				this.tableExits=true;
				
			} catch (SQLException ex) {
				System.out.println(getClass().getName() + ": Table " + dbName + "." + tableName + " does not exist");
				System.out.println(getClass().getName() + ": END");
				System.out.println("init: Table " + tableName + " does not exist");
				
				// create table in DB
				try{
					ptmt = conn.prepareStatement(getCreateTableSQL());
					int result = ptmt.executeUpdate();
					System.out.println("Table " + getTableName() + " was created in Database");
					this.tableExits = true;
				} catch (SQLException exx){
					System.out.println("ERROR: WAS not able to create table to Database!!! ");
					exx.printStackTrace();
					return;
				} 
				return;
			}
		}	else {
			
			System.out.println(getClass().getName() + " ERROR: No data source is populated!!!");
		}
		init=true;
		System.out.println(getClass().getName() + ": initWithCreateIfMissing end");
	}

	public void init(){
		System.out.println( getClass().getName() + ": init start");
		if (datasource != null) {
			try {
				conn = datasource.getConnection();
				ptmt = conn.prepareStatement(checkTableExistsSQL());
				resultSet = ptmt.executeQuery();
				this.tableExits=true;
				
			} catch (SQLException ex) {
				System.out.println(getClass().getName() + ": Table " + dbName + "." + tableName + " does not exist");
				System.out.println(getClass().getName() + ": END");
				System.out.println("init: Table " + tableName + " does not exist");
			}
			init=true;
			System.out.println(getClass().getName() + ": init end");
		}
	}

	public ArrayList<Object> readFromDB(){
		// Check if table exists
		ArrayList<Object> res = new ArrayList<Object>();
		if (datasource != null && this.tableExits) {
			res = new ArrayList<Object>();
			// Read object from the database
			System.out.println( getClass().getName() + " Continue to load object from database ... ");
			try {
				ptmt = conn.prepareStatement(getAllItemsSQL());
				resultSet = ptmt.executeQuery();
				while (resultSet.next()) {
					res.add(createObject(resultSet));
				}
			} catch (SQLException ex) {
				System.out.println("ERROR: readFromDB from the Database has failed");
				ex.printStackTrace();
			} catch (Exception genex) {
				genex.printStackTrace();
			}
		} else {
			System.out.println("ERROR: Data Source is null or Table Already Exists.");
		}
		return res;
	}
	
	public void writeObjectToDB(Object obj){
		// Check if table exists
		if (datasource != null && this.tableExits) {
			
			// Store Space object in the database
			System.out.println("Continue to write object in the database ... ");
			System.out.println(obj.toString());
			try {
				
				if (checkIfObjectExists(obj) ) {
					deleteItem(obj);
				} 
				insertItem(obj);
			} catch (SQLException ex) {
				System.out.println("ERROR: Storing process to the Database has failed");
				ex.printStackTrace();
			} catch (Exception genex) {
				genex.printStackTrace();
			}
		} else {
			System.out.println("ERROR: Data Source is null or Table does not exists.");
		}
	
	}
	
	public void deleteFromObjectToDB(Object obj){
		// Check if table exists
		if (datasource != null && this.tableExits) {
			
			// Delete space object from the database
			System.out.println("Delete Object from database ... ");
			System.out.println(obj.toString());
			try {
				
				deleteItem(obj);
				
			} catch (SQLException ex) {
				System.out.println("ERROR: Deleting space object from the Database has failed");
				ex.printStackTrace();
			} catch (Exception genex) {
				genex.printStackTrace();
			}
		} else {
			System.out.println("ERROR: Data Source is null or Table Already Exists.");
		}
	
	}

	protected String checkTableExistsSQL(){
		if (getTableName() != null){
			return " SELECT 1 FROM " + getTableName() + " LIMIT 1;";
		}
		System.out.println(getClass().getName() + " ERROR: checkTableExistsSQL not implemented !!!");
		return null;
	}
	
	protected String getAllItemsSQL(){
		if (getTableName() != null){
			return " select * from " + getTableName() + ";";
		}
		System.out.println(getClass().getName() + " ERROR: getAllItemsSQL not implemented !!!");
		return null;
	}
	
	protected String getItemByIDSQL(){
		if (getTableName() != null){
			return " select * from " + getTableName() + " where " + getPkColumnName() + "=? ;";
		}
		System.out.println(getClass().getName() + " ERROR: getAllItemsSQL not implemented !!!");
		return null;
	}
	
	protected Object createObject(ResultSet resultSet) throws SQLException{
		System.out.println(getClass().getName() + " createObject ERROR: not implemented !!!");
		return null;
	}
	
	protected void insertItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " insertItem ERROR: not implemented !!!");
	}
	
	protected String getCreateTableSQL() throws SQLException{
		System.out.println(getClass().getName() + " insertItem ERROR: not implemented !!!");
		return null;
	}
	
	protected java.sql.Date convertDate(java.util.Date inp) {
	    java.sql.Date sqlDate = new java.sql.Date(inp.getTime());
	    return sqlDate;
	}

	protected void deleteItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " deleteItem ERROR: not implemented !!!");
	}
	
	protected boolean checkIfObjectExists(Object obj) throws SQLException {
		System.out.println(getClass().getName() + " deleteItem ERROR: not implemented !!!");
		return false;
	}
	
	protected AccountStatus convertToStatus(String inp) {
		   if (inp != null) {
		      for (AccountStatus item : AccountStatus.values()) {
		        if (inp.equalsIgnoreCase(item.name())) {
		          return item;
		        }
		      }
		    }
		   return null;
	}

	protected CountryNames convertToCountry(String inp) {
		   if (inp != null) {
		      for (CountryNames item : CountryNames.values()) {
		        if (inp.equalsIgnoreCase(item.name())) {
		          return item;
		        }
		      }
		    }
		   return null;
	}
	
	protected CategoryType convertToCategory(String inp) {
		   if (inp != null) {
		      for (CategoryType item : CategoryType.values()) {
		        if (inp.equalsIgnoreCase(item.name())) {
		          return item;
		        }
		      }
		    }
		   return null;
	}

	protected TransactionStatus convertToTransStatus(String inp) {
		   if (inp != null) {
		      for (TransactionStatus item : TransactionStatus.values()) {
		        if (inp.equalsIgnoreCase(item.name())) {
		          return item;
		        }
		      }
		    }
		   return null;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	
}
