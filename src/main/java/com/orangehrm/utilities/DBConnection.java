package com.orangehrm.utilities;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.orangehrm.base.BaseClass;



public class DBConnection {
	
	private final static String DB_URL = "jdbc:mysql://localhost:3306/orangehrm";
	private final static String DB_USERNAME = "root";
	private final static String DB_PASSWORD = "";
	public static Logger logger = BaseClass.logger;
	
	public static Connection getDBConnection() {
		try {
			Connection con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			logger.info("DB connection successful");
			return con;
		} catch (SQLException e) {
			logger.error("Error while establishing connection");
			e.printStackTrace();
			return null;
		}
	}
	
	//Get the employee details from the DB and store in a Map
	public static Map<String, String> getEmployeeDetails(String employee_id){
		String query = "SELECT emp_firstname,emp_middle_name,emp_lastname FROM `hs_hr_employee` WHERE employee_id="+employee_id+";";
		Map<String,String> employeeDetails = new HashMap<>();
		try(Connection conn = getDBConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)){
			logger.info("Executed query "+query);
			if (rs.next()) {
				String firstName = rs.getString("emp_firstname");
				String middleName = rs.getString("emp_middle_name");
				String lastName = rs.getString("emp_lastname");
				
				//Store in Map
				employeeDetails.put("firstName", firstName);
				employeeDetails.put("middleName", middleName!=null?middleName:"");
				employeeDetails.put("lastName", lastName);
				
				logger.info("Query execited successfully");
				logger.info("Employee data fetched");
				
			}
			else {
				logger.error("Employees not found");
			}
		}catch (Exception e) {
			logger.error("Error while executing query"+e.getMessage());
			e.printStackTrace();
		}
		return employeeDetails;
	}
	
}
