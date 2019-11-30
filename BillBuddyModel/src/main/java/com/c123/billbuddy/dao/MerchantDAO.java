package com.c123.billbuddy.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.c123.billbuddy.model.Address;
import com.c123.billbuddy.model.Merchant;
import com.c123.billbuddy.model.User;

public class MerchantDAO extends BaseDAO {

	public MerchantDAO() {
		tableName = "merchant";
	}
	

	
	protected void insertItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " Start insert");
		Merchant merch = (Merchant) obj;
		String sql = "INSERT INTO `merchant` VALUES ( ?, ?, ? , ? , ?, ? );";
		ptmt = conn.prepareStatement(sql);
		ptmt.setInt(1,merch.getMerchantAccountId());
		ptmt.setString(2,merch.getName());
		ptmt.setDouble(3,merch.getReceipts());
		ptmt.setDouble(4,merch.getFeeAmount());
		ptmt.setString(5,merch.getCategory().name());
		ptmt.setString(6,merch.getStatus().name());
		int res = ptmt.executeUpdate();
	}

	protected void deleteItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " Delete Item");
		Merchant merch = (Merchant) obj;
		String sql = "DELETE FROM merchant where merchantAccountId=?";
		ptmt = conn.prepareStatement(sql);
		ptmt.setInt(1,merch.getMerchantAccountId());
		int res = ptmt.executeUpdate();
	}
	
	protected Object createObject(ResultSet resultSet) throws SQLException{
		// System.out.println(getClass().getName() + " createObject ");
		Merchant merch = new Merchant();
		merch.setMerchantAccountId(resultSet.getInt("merchantAccountId") );
		merch.setName(resultSet.getString("name"));
		merch.setStatus( convertToStatus( resultSet.getString("status") ) );
		merch.setReceipts( resultSet.getDouble("receipts") );
		merch.setCategory( convertToCategory( resultSet.getString("category")  ) );
		return merch;
	}

	
	protected String getCreateTableSQL() throws SQLException{
		System.out.println(getClass().getName() + " getCreateTableSQL ");
		return "CREATE TABLE `merchant` ( `merchantAccountId` int(11) NOT NULL, "
				+ "`name` varchar(255) DEFAULT NULL,   "
				+ "`receipts` double DEFAULT NULL,  "
				+ "`feeAmount` double DEFAULT NULL,  "
				+ "`category` varchar(255) DEFAULT NULL,  "
				+ "`status` varchar(255) DEFAULT NULL,  PRIMARY KEY (`merchantAccountId`));";		
	}

	protected boolean checkIfObjectExists(Object obj) throws SQLException {
		System.out.println(getClass().getName() + " checkIfObjectExists ");
		Merchant merch = (Merchant) obj;
		ptmt = conn.prepareStatement("select * from merchant where merchantAccountId=?");
		ptmt.setInt(1,merch.getMerchantAccountId());
		resultSet = ptmt.executeQuery();
		if (resultSet.first()) {
			return true;
		} 		
		return false;
	}
	
}
