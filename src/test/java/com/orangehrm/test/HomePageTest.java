package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.Dataproviders;
import com.orangehrm.utilities.ExtentManager;

public class HomePageTest extends BaseClass{
	
	private LoginPage loginPage;
	private HomePage homePage;
	
	@BeforeMethod
	public void setUp() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}
	
	
	@Test(dataProvider = "validLoginData", dataProviderClass = Dataproviders.class)
	public void verifyOrangeHRMLogo(String userName,String password) {
		ExtentManager.startTest("Valid login test");
		ExtentManager.logStep("Entering username and pssword");
		loginPage.login(userName,password);
		ExtentManager.logStep("Verifying logi is visible or not!!");
		Assert.assertTrue(homePage.verifyOrangeHRMLogo());
		ExtentManager.logStep("Logo verification successfully");
		homePage.logOutOperation();
		ExtentManager.logStep("Logged Out successfully successfully");
		ExtentManager.endTest();
	}

}
