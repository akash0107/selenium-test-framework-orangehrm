package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;

public class HomePage {

	private ActionDriver actionDriver;

	// Define locators using By class
	private By adminTab = By.xpath("//span[text()='Admin']");
	private By orangeHRMLogo = By.xpath("//img[@alt='client brand banner']");
	private By userIDDropDown = By.className("oxd-userdropdown");
	private By logOutBtn = By.linkText("Logout");

	private By pimTab = By.xpath("//a[contains(@href,'PimModule')]");
	private By employeeName = By.xpath("//label[text()='Employee Name']/../following-sibling::div//input");
	private By searchBtn = By.xpath("//button[@type='submit']");
	private By firstAndMiddleName = By.xpath("//div[@class='oxd-table-body']//div[@role='row']/div[3]/div");
	private By lastName = By.xpath("//div[@class='oxd-table-body']//div[@role='row']/div[4]/div");

	// to initialize the ActionDriver Object by passing the WebDriver instance
	public HomePage(WebDriver driver) {
		super();
		actionDriver = new ActionDriver(driver);
	}

	public boolean isAdminTabVisible() {
		return actionDriver.isDisplayed(adminTab);
	}

	public boolean verifyOrangeHRMLogo() {
		return actionDriver.isDisplayed(orangeHRMLogo);
	}

	public void logOutOperation() {
		actionDriver.click(userIDDropDown);
		actionDriver.click(logOutBtn);
	}

	// Navigate to pim tab
	public void clickOnPimTab() {
		actionDriver.click(pimTab);
	}

	public void employeeSearch(String empName) {
		actionDriver.enterText(employeeName, empName);
		actionDriver.click(searchBtn);
		actionDriver.scrollToElement(firstAndMiddleName);
	}

	// Verify employee first name and middle name
	public boolean verifyEmployeeFirstNameAndMiddleName(String employeeFirstNameAndMiddleNameFromDB) {
		return actionDriver.compareText(firstAndMiddleName, employeeFirstNameAndMiddleNameFromDB);
	}

	// Verify employee last name
	public boolean verifyEmployeeLasteName(String employeeLastNameFromDB) {
		return actionDriver.compareText(lastName, employeeLastNameFromDB);
	}

}
