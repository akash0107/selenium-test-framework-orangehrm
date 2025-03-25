package com.orangehrm.actiondriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static Logger logger = BaseClass.logger;

	public ActionDriver(WebDriver driver) {
		super();
		this.driver = driver;
		this.wait = new WebDriverWait(driver,
				Duration.ofSeconds(Integer.parseInt(BaseClass.getProperties().getProperty("explicitWait"))));
		logger.info("WebDriver instance got created" + Thread.currentThread().getId());
	}

	// Method to click an element
	public void click(By by) {
		try {
			applyColor(by, "green");
			waitForElementToBeClickable(by);
			logger.info("Clicked on element -->" + getElementDescription(by));
			ExtentManager.logStep("Clicked an element: " + getElementDescription(by));
			driver.findElement(by).click();

		} catch (Exception e) {
			applyColor(by, "red");
			logger.error("Unable to click the element " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to get the element ",
					getElementDescription(by) + "_element was not clicked");
		}
	}

	// Method to enter text in an input field
	public void enterText(By by, String value) {
		try {
			waitForElementToBevisible(by);
			driver.findElement(by).clear();
			applyColor(by, "green");
			driver.findElement(by).sendKeys(value);
			logger.info("Entered text on: " + getElementDescription(by) + "value as: " + value);
		} catch (Exception e) {
			logger.error("Unable to enter the input value " + e.getMessage());
		}
	}

	// Method to get text from an input field
	public String getText(By by) {
		try {
			waitForElementToBevisible(by);
			applyColor(by, "green");
			return driver.findElement(by).getText();
		} catch (Exception e) {
			logger.error("unable to get the text" + e.getMessage());
			return null;
		}
	}

	// Method to compare Two text
	public boolean compareText(By by, String expectedText) {
		try {
			waitForElementToBevisible(by);
			String actualText = driver.findElement(by).getText();
			if (expectedText.equals(actualText)) {
				applyColor(by, "green");
				logger.info("texts are matching: " + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenShot(BaseClass.getDriver(), "Text comparison failed ",
						"Text verified successfully! " + actualText + " equals " + expectedText);
				return true;
			} else {
				applyColor(by, "red");
				logger.error("texts are not matching: " + actualText + " equals " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Text comparison failed",
						"Text verified not successfully! " + actualText + " not equals " + expectedText);
				return false;
			}
		} catch (Exception e) {
			logger.error("unable to compare texts: " + e.getMessage());
		}
		return false;
	}

	// wait for element to be clickable
	private void waitForElementToBeClickable(By by) {
		try {
			applyColor(by, "green");
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			applyColor(by, "red");
			logger.error("Element is not clickable " + e.getMessage());
		}
	}

	// wait for element to be visible
	private void waitForElementToBevisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
		} catch (Exception e) {
			logger.error("Element is not visible " + e.getMessage());
		}
	}

	// Scroll Into an element
	public void scrollToElement(By by) {
		try {
			applyColor(by, "green");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0].scrollIntoView(true)", element);
		} catch (Exception e) {
			applyColor(by, "red");
			logger.error("unable to locate element " + e.getMessage());
		}
	}

	// wait for the page load state
	public void waitForPageLoad() {
		try {
			wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
					.equals("complete"));
		} catch (Exception e) {
			logger.error("Page load timeout: " + e.getMessage());
		}
	}

	// method to check if the element is displayed
	public boolean isDisplayed(By by) {
		try {
			waitForElementToBevisible(by);
			if (driver.findElement(by).isDisplayed()) {
				applyColor(by, "green");
				logger.info("Element: " + getElementDescription(by) + " is visible");
				ExtentManager.logStep("Element is displayed: " + getElementDescription(by));
				ExtentManager.logStepWithScreenShot(BaseClass.getDriver(), "Element is displayed ",
						"Element: " + getElementDescription(by) + " is visible");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			applyColor(by, "red");
			logger.error("element is not displayed " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Element is not displayed: ",
					"Element is not displayed: " + getElementDescription(by));
			return false;
		}
	}

	// Method to get a description of an element
	public String getElementDescription(By locator) {
		// Check for null in driver or locator
		if (driver == null || locator == null) {
			return "driver or locator is null";
		}

		try {
			// find the element using the locator
			WebElement element = driver.findElement(locator);

			// Get element attributes
			String name = element.getDomAttribute("name");
			String id = element.getDomAttribute("id");
			String text = element.getText();
			String className = element.getDomAttribute("class");
			String placeHolder = element.getDomAttribute("placeholder");
			String type = element.getDomAttribute("type");
			String alt = element.getDomAttribute("alt");

			// Return the description based on elements attribute
			if (isNotEmpty(name)) {
				return "Element with name: " + name;
			} else if (isNotEmpty(id)) {
				return "Element with id: " + id;
			} else if (isNotEmpty(text)) {
				return "Element with text: " + truncate(text, 50);
			} else if (isNotEmpty(className)) {
				return "Element with class name:" + className;
			} else if (isNotEmpty(placeHolder)) {
				return "Element with placeholder:" + placeHolder;
			} else if (isNotEmpty(type)) {
				return "Element with type:" + type;
			} else if (isNotEmpty(alt)) {
				return "Element with alt text:" + alt;
			}
		} catch (Exception e) {
			logger.error("Unable to describe the element: " + e.getMessage());
		}
		return "unable to describe an element";

	}

	// Utility method to check if the string is not null or empty
	private boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	// Utility method to truncate long string
	private String truncate(String value, int maxLength) {
		if (value == null || value.length() <= maxLength) {
			return value;
		}
		return value.substring(0, maxLength) + "...";
	}

	// Utility Method to border an element
	public void applyColor(By by, String color) {
		try {
			// Locate the element
			WebElement element = driver.findElement(by);
			// Apply the border
			String script = "arguments[0].style.border='3px solid " + color + "';";
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(script, element);
			logger.info("Applied the border with color " + color + " to element " + getElementDescription(by));
		} catch (Exception e) {
			logger.warn("Failed to apply the border to element " + getElementDescription(by) + e.getMessage());
		}
	}

	// Utility Method to border an element
	public void applyBorder(By by, String color) {
		try {
			// Locate the element
			WebElement element = driver.findElement(by);
			// Apply the border
			String script = "arguments[0].style.border='3px solid " + color + "';";
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(script, element);
			logger.info("Applied the border with color " + color + " to element " + getElementDescription(by));
		} catch (Exception e) {
			logger.warn("Failed to apply the border to element " + getElementDescription(by) + e.getMessage());
		}
	}

	// ===================== Select Methods =====================

	// Method to select a dropdown by visible text
	public void selectByVisibleText(By by, String value) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByVisibleText(value);
			applyBorder(by, "green");
			logger.info("Selected dropdown value: " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown value: " + value, e);
		}
	}

	// Method to select a dropdown by value
	public void selectByValue(By by, String value) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByValue(value);
			applyBorder(by, "green");
			logger.info("Selected dropdown value by actual value: " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown by value: " + value, e);
		}
	}

	// Method to select a dropdown by index
	public void selectByIndex(By by, int index) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByIndex(index);
			applyBorder(by, "green");
			logger.info("Selected dropdown value by index: " + index);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown by index: " + index, e);
		}
	}

	// Method to get all options from a dropdown
	public List<String> getDropdownOptions(By by) {
		List<String> optionsList = new ArrayList<>();
		try {
			WebElement dropdownElement = driver.findElement(by);
			Select select = new Select(dropdownElement);
			for (WebElement option : select.getOptions()) {
				optionsList.add(option.getText());
			}
			applyBorder(by, "green");
			logger.info("Retrieved dropdown options for " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to get dropdown options: " + e.getMessage());
		}
		return optionsList;
	}

	// ===================== JavaScript Utility Methods =====================

	// Method to click using JavaScript
	public void clickUsingJS(By by) {
		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			applyBorder(by, "green");
			logger.info("Clicked element using JavaScript: " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to click using JavaScript", e);
		}
	}

	// Method to scroll to the bottom of the page
	public void scrollToBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
		logger.info("Scrolled to the bottom of the page.");
	}

	// Method to highlight an element using JavaScript
	public void highlightElementJS(By by) {
		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid yellow'", element);
			logger.info("Highlighted element using JavaScript: " + getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to highlight element using JavaScript", e);
		}
	}

	// ===================== Window and Frame Handling =====================

	// Method to switch between browser windows
	public void switchToWindow(String windowTitle) {
		try {
			Set<String> windows = driver.getWindowHandles();
			for (String window : windows) {
				driver.switchTo().window(window);
				if (driver.getTitle().equals(windowTitle)) {
					logger.info("Switched to window: " + windowTitle);
					return;
				}
			}
			logger.warn("Window with title " + windowTitle + " not found.");
		} catch (Exception e) {
			logger.error("Unable to switch window", e);
		}
	}

	// Method to switch to an iframe
	public void switchToFrame(By by) {
		try {
			driver.switchTo().frame(driver.findElement(by));
			logger.info("Switched to iframe: " + getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to switch to iframe", e);
		}
	}

	// Method to switch back to the default content
	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
		logger.info("Switched back to default content.");
	}

	// ===================== Alert Handling =====================

	// Method to accept an alert popup
	public void acceptAlert() {
		try {
			driver.switchTo().alert().accept();
			logger.info("Alert accepted.");
		} catch (Exception e) {
			logger.error("No alert found to accept", e);
		}
	}

	// Method to dismiss an alert popup
	public void dismissAlert() {
		try {
			driver.switchTo().alert().dismiss();
			logger.info("Alert dismissed.");
		} catch (Exception e) {
			logger.error("No alert found to dismiss", e);
		}
	}

	// Method to get alert text
	public String getAlertText() {
		try {
			return driver.switchTo().alert().getText();
		} catch (Exception e) {
			logger.error("No alert text found", e);
			return "";
		}
	}

	// ===================== Browser Actions =====================

	public void refreshPage() {
		try {
			driver.navigate().refresh();
			ExtentManager.logStep("Page refreshed successfully.");
			logger.info("Page refreshed successfully.");
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to refresh page", "refresh_page_failed");
			logger.error("Unable to refresh page: " + e.getMessage());
		}
	}

	public String getCurrentURL() {
		try {
			String url = driver.getCurrentUrl();
			ExtentManager.logStep("Current URL fetched: " + url);
			logger.info("Current URL fetched: " + url);
			return url;
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to fetch current URL", "get_current_url_failed");
			logger.error("Unable to fetch current URL: " + e.getMessage());
			return null;
		}
	}

	public void maximizeWindow() {
		try {
			driver.manage().window().maximize();
			ExtentManager.logStep("Browser window maximized.");
			logger.info("Browser window maximized.");
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to maximize window", "maximize_window_failed");
			logger.error("Unable to maximize window: " + e.getMessage());
		}
	}

	// ===================== Advanced WebElement Actions =====================

	public void moveToElement(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(driver.findElement(by)).perform();
			ExtentManager.logStep("Moved to element: " + elementDescription);
			logger.info("Moved to element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to move to element",
					elementDescription + "_move_failed");
			logger.error("Unable to move to element: " + e.getMessage());
		}
	}

	public void dragAndDrop(By source, By target) {
		String sourceDescription = getElementDescription(source);
		String targetDescription = getElementDescription(target);
		try {
			Actions actions = new Actions(driver);
			actions.dragAndDrop(driver.findElement(source), driver.findElement(target)).perform();
			ExtentManager.logStep("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
			logger.info("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to drag and drop",
					sourceDescription + "_drag_failed");
			logger.error("Unable to drag and drop: " + e.getMessage());
		}
	}

	public void doubleClick(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.doubleClick(driver.findElement(by)).perform();
			ExtentManager.logStep("Double-clicked on element: " + elementDescription);
			logger.info("Double-clicked on element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to double-click element",
					elementDescription + "_doubleclick_failed");
			logger.error("Unable to double-click element: " + e.getMessage());
		}
	}

	public void rightClick(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.contextClick(driver.findElement(by)).perform();
			ExtentManager.logStep("Right-clicked on element: " + elementDescription);
			logger.info("Right-clicked on element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to right-click element",
					elementDescription + "_rightclick_failed");
			logger.error("Unable to right-click element: " + e.getMessage());
		}
	}

	public void sendKeysWithActions(By by, String value) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.sendKeys(driver.findElement(by), value).perform();
			ExtentManager.logStep("Sent keys to element: " + elementDescription + " | Value: " + value);
			logger.info("Sent keys to element --> " + elementDescription + " | Value: " + value);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to send keys",
					elementDescription + "_sendkeys_failed");
			logger.error("Unable to send keys to element: " + e.getMessage());
		}
	}

	public void clearText(By by) {
		String elementDescription = getElementDescription(by);
		try {
			driver.findElement(by).clear();
			ExtentManager.logStep("Cleared text in element: " + elementDescription);
			logger.info("Cleared text in element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to clear text",
					elementDescription + "_clear_failed");
			logger.error("Unable to clear text in element: " + e.getMessage());
		}
	}

	// Method to upload a file
	public void uploadFile(By by, String filePath) {
		try {
			driver.findElement(by).sendKeys(filePath);
			applyBorder(by, "green");
			logger.info("Uploaded file: " + filePath);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to upload file: " + e.getMessage());
		}
	}
}
