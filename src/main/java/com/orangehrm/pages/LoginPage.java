package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class LoginPage {
	
	private ActionDriver actionDriver;
	
	//Define locators using By class
	private By userNameField = By.name("username");
	private By passwordField = By.name("password");
	private By loginBtn = By.xpath("//button[@type='submit']");
	private By errorMessage = By.xpath("//p[text()='Invalid credentials']");
	
	
	
	public LoginPage(WebDriver driver) {
		super();
		//this.actionDriver = new ActionDriver(driver);
		this.actionDriver = BaseClass.getActionDriver();
	}

	//Method to perform Login
	public void login(String userName, String password) {
		actionDriver.enterText(userNameField, userName);
		actionDriver.enterText(passwordField, password);
		actionDriver.click(loginBtn);
	}
	
	//Method to check if error message is displayed
	public boolean checkErrorMessageDisplay() {
		return actionDriver.isDisplayed(errorMessage);
	}

	//Method to get the text from error message
	public String getErrorMessageText() {
		return actionDriver.getText(errorMessage);
	}
	
	//verify if error is correct or not
	public boolean verifyErrorMessage(String expectError) {
		return actionDriver.compareText(errorMessage, expectError);
	}
	
}
