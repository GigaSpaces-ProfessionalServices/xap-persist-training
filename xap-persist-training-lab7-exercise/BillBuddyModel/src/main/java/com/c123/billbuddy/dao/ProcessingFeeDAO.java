package com.c123.billbuddy.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.c123.billbuddy.model.Merchant;
import com.c123.billbuddy.model.Payment;
import com.c123.billbuddy.model.ProcessingFee;

public class ProcessingFeeDAO extends BaseDAO {

	public ProcessingFeeDAO() {
		tableName = "processingfee";
	}
	

	protected void insertItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " Start insert");
		ProcessingFee fee = (ProcessingFee) obj;
		String sql = "INSERT INTO `processingfee` VALUES ( ?, ?, ? , ? , ?, ?,? );";
		ptmt = conn.prepareStatement(sql);
		ptmt.setString(1,fee.getProcessingFeeId());
		ptmt.setInt(2,fee.getPayingAccountId());
		ptmt.setString(3,fee.getDependentPaymentId());
		ptmt.setString(4,fee.getDescription());
		ptmt.setDouble(5,fee.getAmount());
		ptmt.setString(6,fee.getStatus().name());
		ptmt.setDate(7, convertDate( fee.getCreatedDate() ));
		int res = ptmt.executeUpdate();
	}
	
	protected void deleteItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " Delete Item");
		ProcessingFee fee = (ProcessingFee) obj;
		String sql = "DELETE FROM processingfee where processingFeeId=?";
		ptmt = conn.prepareStatement(sql);
		ptmt.setString(1,fee.getProcessingFeeId());
		int res = ptmt.executeUpdate();
	}
	
	protected Object createObject(ResultSet resultSet) throws SQLException{
		// System.out.println(getClass().getName() + " createObject ");
		ProcessingFee fee = new ProcessingFee();
		fee.setPayingAccountId(resultSet.getInt("payingAccountId"));
		fee.setProcessingFeeId( resultSet.getString("processingFeeId") );
		fee.setDependentPaymentId(resultSet.getString("dependentPaymentId"));
		fee.setDescription( resultSet.getString("description"));
		fee.setStatus( convertToTransStatus( resultSet.getString("status") ) );
		fee.setAmount(resultSet.getDouble("amount") );
		
		java.util.Date theDate = resultSet.getTimestamp("createdDate");
		fee.setCreatedDate(theDate);
		return fee;
	}
	
	protected String getCreateTableSQL() throws SQLException{
		System.out.println(getClass().getName() + " getCreateTableSQL ");
		return "CREATE TABLE `processingfee` (  `processingFeeId` varchar(255) NOT NULL,  "
				+ "`payingAccountId` int(11) DEFAULT NULL,  "
				+ "`dependentPaymentId` varchar(255) DEFAULT NULL,  "
				+ "`description` varchar(255) DEFAULT NULL,  "
				+ "`amount` double DEFAULT NULL,  "
				+ "`status` varchar(255) DEFAULT NULL,  "
				+ "`createdDate` datetime DEFAULT NULL, "
				+ "PRIMARY KEY (`processingFeeId`));";		
	}
	
	protected boolean checkIfObjectExists(Object obj) throws SQLException {
		System.out.println(getClass().getName() + " checkIfObjectExists ");
		ProcessingFee fee = (ProcessingFee) obj;
		ptmt = conn.prepareStatement("select * from processingfee where processingFeeId=?");
		ptmt.setString(1,fee.getProcessingFeeId());
		resultSet = ptmt.executeQuery();
		if (resultSet.first()) {
			return true;
		} 		
		return false;
	}
	
	
}
