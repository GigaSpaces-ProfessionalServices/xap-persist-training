package com.gs.billbuddy.mirror;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.dbcp.BasicDataSource;

import com.gigaspaces.document.SpaceDocument;

public class ContractSpaceDocumentDatabaseStorer {
	public static final String REGISTRED_DOCUMENT_TYPE="ContractDocument";
	public static final String TABLE_NAME="CONTACT";
	
	private Connection conn;
	private PreparedStatement ptmt = null;
	private ResultSet resultSet = null;

	private boolean tableExits=false;
	private BasicDataSource datasource;
	private boolean init=false;
	
	public ContractSpaceDocumentDatabaseStorer() {
	}
	
	public BasicDataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(BasicDataSource datasource) {
		this.datasource = datasource;
	}

	public void init(){
		System.out.println("writeContractToDB: init start");
		if (datasource != null) {
			try {
				if (!this.tableExits){
					conn = datasource.getConnection();
					ptmt = conn.prepareStatement(checkTableExistsSQL());
					resultSet = ptmt.executeQuery();
				}
			} catch (SQLException ex) {
				System.out.println("writeContractToDB: Table CONTRACTS does not exist");
				
				// create table in DB
				try{
					ptmt = conn.prepareStatement(getTableCreateSQL());
					int result = ptmt.executeUpdate();
					System.out.println("Table CONTRACTS was created in Database");
					this.tableExits = true;
				} catch (SQLException exx){
					System.out.println("ERROR: WAS not able to create table to Database!!! Contract is not saved to Database");
					exx.printStackTrace();
					return;
				} 
			}
		}	else {
			
			System.out.println("ERROR: No data source is populated!!!");
		}
		init=true;
		System.out.println("writeContractToDB: init end");
	}
	

	public void writeContractToDB(SpaceDocument doc, Integer merchantID){
		// Check if table exists
		if (datasource != null && this.tableExits) {
			
			// Store Contract in the DB
			System.out.println("Continue to store contract in database ... ");
			
			try {
				
				ptmt = conn.prepareStatement(getSelectContractStatement());
				ptmt.setInt( 1, merchantID );
				resultSet = ptmt.executeQuery();
				if (resultSet.first()) {
					System.out.println("writeContractToDB: record already exists in the database, contract id: " + merchantID);
				} else {
					ptmt = conn.prepareStatement(getInsertContractStatement());
					ptmt.setInt( 1, merchantID );
					Blob obj = new SerialBlob(serialize(doc) );
					ptmt.setBlob(2,obj);
					int res = ptmt.executeUpdate();
				}
			} catch (SQLException ex) {
				System.out.println("ERROR: Storing Contract to the Database has failed");
				ex.printStackTrace();
			} catch (IOException ioex) {
				System.out.println("ERROR: failed to convert document to byte array ");
				ioex.printStackTrace();
			} catch (Exception genex) {
				genex.printStackTrace();
			}
		} else {
			System.out.println("ERROR: Data Source is null or Table Already Exists.");
		}
	
	}
	
	private String getTableCreateSQL(){
		return " CREATE TABLE CONTRACTS ( Contract_ID int, Document BLOB );";
	}
	
	private String getTableDropSQL(){
		return " Drop TABLE CONTRACTS;";
	}
	
	private String checkTableExistsSQL(){
		return " SELECT 1 FROM jbillbuddy.CONTRACTS LIMIT 1;";
	}
	
	private String getInsertContractStatement(){
		return " insert into CONTRACTS (Contract_ID,Document) values (?,?)";
	}
	
	private String getSelectContractStatement(){
		return "select * from CONTRACTS where Contract_ID=?";
	}
	
	public static byte[] serialize( Object obj ) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
    
	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}
}
