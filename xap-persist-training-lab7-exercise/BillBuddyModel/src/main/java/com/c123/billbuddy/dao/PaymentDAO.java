package com.c123.billbuddy.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.c123.billbuddy.model.Payment;
import com.c123.billbuddy.model.ProcessingFee;

public class PaymentDAO extends BaseDAO {

	public PaymentDAO() {
		tableName = "payment";
	}
	

	protected void insertItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " Start insert");
		Payment payment = (Payment) obj;
		String sql = "INSERT INTO `payment` VALUES ( ?, ?, ? , ? , ?, ?,? );";
		ptmt = conn.prepareStatement(sql);
		ptmt.setString(1,payment.getPaymentId());
		ptmt.setInt(2,payment.getPayingAccountId());
		ptmt.setInt(3,payment.getReceivingMerchantId());
		ptmt.setString(4,payment.getDescription());
		ptmt.setDouble(5,payment.getPaymentAmount());
		ptmt.setString(6,payment.getStatus().name());
		ptmt.setDate(7, convertDate( payment.getCreatedDate() ));
		int res = ptmt.executeUpdate();
	}
	
	protected void deleteItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " Delete Item");
		Payment payment = (Payment) obj;
		String sql = "DELETE FROM payment where paymentId=?";
		ptmt = conn.prepareStatement(sql);
		ptmt.setString(1,payment.getPaymentId());
		int res = ptmt.executeUpdate();
	}

	protected Object createObject(ResultSet resultSet) throws SQLException{
		// System.out.println(getClass().getName() + " createObject ");
		Payment payment = new Payment();
		payment.setPaymentId( resultSet.getString("paymentId") );
		payment.setPayingAccountId( resultSet.getInt("payingAccountId") );
		payment.setReceivingMerchantId( resultSet.getInt("receivingMerchantId") );
		payment.setDescription( resultSet.getString("description") );
		payment.setStatus( convertToTransStatus( resultSet.getString("status") ) );
		payment.setPaymentAmount( resultSet.getDouble("paymentAmount") );
		java.util.Date theDate = resultSet.getTimestamp("createdDate");
		payment.setCreatedDate(theDate);
		return payment;
	}
	
	protected String getCreateTableSQL() throws SQLException{
		System.out.println(getClass().getName() + " insertItem ERROR: not implemented !!!");
		return "CREATE TABLE `payment` (   `paymentId` varchar(255) NOT NULL,   "
				+ "`payingAccountId` int(11) DEFAULT NULL,  "
				+ "`receivingMerchantId` int(11) DEFAULT NULL,  "
				+ "`description` varchar(255) DEFAULT NULL,  "
				+ "`paymentAmount` double DEFAULT NULL,  "
				+ "`status` varchar(255) DEFAULT NULL,  "
				+ "`createdDate` datetime DEFAULT NULL, "
				+ "PRIMARY KEY (`paymentId`) );";		
	}

	protected boolean checkIfObjectExists(Object obj) throws SQLException {
		System.out.println(getClass().getName() + " checkIfObjectExists ");
		Payment payment = (Payment) obj;
		ptmt = conn.prepareStatement("select * from payment where paymentId=?");
		ptmt.setString(1,payment.getPaymentId());
		resultSet = ptmt.executeQuery();
		if (resultSet.first()) {
			return true;
		} 		
		return false;
	}

}
