package com.c123.billbuddy.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.c123.billbuddy.model.Address;
import com.c123.billbuddy.model.User;

public class UserDAO extends BaseDAO {

	public UserDAO() {
		tableName = "user";
	}
	

	
	protected void insertItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " Start insert");
		User user = (User) obj;
		String sql = "INSERT INTO `user` VALUES ( ?, ?, ? , ? , ?, ?, ?, ?, ?, ?  );";
		ptmt = conn.prepareStatement(sql);
		ptmt.setInt(1,user.getUserAccountId());
		ptmt.setString(2,user.getName());
		ptmt.setString(3,user.getAddress().getStreet());
		ptmt.setString(4,user.getAddress().getCity());
		ptmt.setString(5,user.getAddress().getState());
		ptmt.setString(6,user.getAddress().getCountry().name());
		ptmt.setInt(7,user.getAddress().getZipCode());
		ptmt.setDouble(8,user.getBalance());
		ptmt.setDouble(9,user.getCreditLimit());
		ptmt.setString(10,user.getStatus().name());
		int res = ptmt.executeUpdate();
	}
	
	protected void deleteItem(Object obj) throws SQLException{
		System.out.println(getClass().getName() + " Delete Item");
		User user = (User) obj;
		String sql = "DELETE FROM user where userAccountId=?";
		ptmt = conn.prepareStatement(sql);
		ptmt.setInt(1,user.getUserAccountId());
		int res = ptmt.executeUpdate();
	}

	protected boolean checkIfObjectExists(Object obj) throws SQLException {
		System.out.println(getClass().getName() + " checkIfObjectExists ");
		User user = (User) obj;
		ptmt = conn.prepareStatement("select * from user where userAccountId=?");
		ptmt.setInt(1,user.getUserAccountId());
		resultSet = ptmt.executeQuery();
		if (resultSet.first()) {
			return true;
		} 		
		return false;
	}

	protected Object createObject(ResultSet resultSet) throws SQLException{
		// System.out.println(getClass().getName() + " createObject ");
		User user = new User();
		user.setUserAccountId(resultSet.getInt("userAccountId") );
		user.setName(resultSet.getString("name"));
		user.setBalance(resultSet.getDouble("balance"));
		user.setStatus( convertToStatus( resultSet.getString("status") ) );
		user.setCreditLimit(resultSet.getDouble("creditLimit"));
		
		Address address = new Address();
		address.setCity(resultSet.getString("city"));
		address.setStreet(resultSet.getString("street"));
		address.setState(resultSet.getString("state"));
		address.setZipCode(resultSet.getInt("zipCode"));
		address.setCountry( convertToCountry( resultSet.getString("country") ) );
		user.setAddress(address);
		return user;
	}
	
	
	protected String getCreateTableSQL() throws SQLException{
		System.out.println(getClass().getName() + " getCreateTableSQL ");
		return "CREATE TABLE `user` (`userAccountId` int(11) NOT NULL, "
				+ "`name` varchar(255) DEFAULT NULL, "
				+ "`street` varchar(255) DEFAULT NULL, "
				+ "`city` varchar(255) DEFAULT NULL, "
				+ "`state` varchar(255) DEFAULT NULL, "
				+ "`country` varchar(255) DEFAULT NULL, "
				+ "`zipCode` int(11) DEFAULT NULL, "
				+ "`balance` double DEFAULT NULL, "
				+ "`creditLimit` double DEFAULT NULL, "
				+ "`status` varchar(255) DEFAULT NULL, "
				+ "PRIMARY KEY (`userAccountId`));";		
	}
}
