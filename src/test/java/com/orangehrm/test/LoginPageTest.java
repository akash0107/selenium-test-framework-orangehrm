package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.Dataproviders;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass{
	
	private LoginPage loginPage;
	private HomePage homePage;
	
	@BeforeMethod
	public void setUp() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}
	
	@Test(dataProvider = "validLoginData", dataProviderClass = Dataproviders.class)
	public void verifyValidLoginTest(String userName,String password) {
		//ExtentManager.startTest("Valid login test");
		loginPage.login(userName, password);
		ExtentManager.logStep("Enterend valid username and password");
		ExtentManager.logStep("Verifying Admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(),"Admin tab should be visible after successful login");
		ExtentManager.logStep("Verifying Admin tab is visible successfully");
		homePage.logOutOperation();
		ExtentManager.logStep("logged out successfully");
		staticWait(2);	
	}
	
	@Test(dataProvider = "inValidLoginData", dataProviderClass = Dataproviders.class)
	public void verifyInvalidLoginTest(String userName,String password) {
		//ExtentManager.startTest("InValid login test");
		ExtentManager.logStep("Entering username and pssword");
		loginPage.login(userName,password);
		Assert.assertTrue(loginPage.verifyErrorMessage("Invalid credentials"),"Message should be displayed");
		ExtentManager.logStep("validation successful");
		Assert.assertTrue(loginPage.checkErrorMessageDisplay(), "Error message should be displayed");
		ExtentManager.logStep("Error message is displaying successfully");
	}

}
