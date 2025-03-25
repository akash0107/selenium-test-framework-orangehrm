package com.orangehrm.test;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DBConnection;
import com.orangehrm.utilities.Dataproviders;
import com.orangehrm.utilities.ExtentManager;

public class DBVerificationTest extends BaseClass{
	
	private LoginPage loginPage;
	private HomePage homePage;
	
	@BeforeMethod
	public void setUp() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}
	
	@Test(dataProvider = "employeeVerification", dataProviderClass = Dataproviders.class)
	public void VerifyEmployeeNameFromDB(String empId,String empName) {
		ExtentManager.startTest("DB verification for employees");
		SoftAssert softAssert = getSoftAssert();
		ExtentManager.logStep("Logging with admin credentials");
		loginPage.login(properties.getProperty("username"), properties.getProperty("password"));
		ExtentManager.logStep("click on PIM tab");
		homePage.clickOnPimTab();
		ExtentManager.logStep("Search for employee");
		
		homePage.employeeSearch(empName);
		
		ExtentManager.logStep("Get the employee details from DB");
		String employeeId = empId;
		Map<String, String> employeeDetails = DBConnection.getEmployeeDetails(employeeId);
		
		String firstName = employeeDetails.get("firstName");
		String middleName = employeeDetails.get("middleName");
		String lastName = employeeDetails.get("lastName");
		
		String empFirstNamePlusMiddleName = (firstName+" "+middleName).trim();
		
		ExtentManager.logStep("Verify the employee first and middle name");
		softAssert.assertTrue(homePage.verifyEmployeeFirstNameAndMiddleName(empFirstNamePlusMiddleName),"first and middle name are not matching");
		
		ExtentManager.logStep("Verify the employee last name");
		softAssert.assertTrue(homePage.verifyEmployeeLasteName(lastName),"last name not matching");
		softAssert.assertAll();
		ExtentManager.endTest();
	}

}
