package com.orangehrm.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

	protected static Properties properties;
	
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();
	protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);
	
	
	
	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}

	public static WebDriver getDriver() {
		if (driver.get() == null) {
			logger.error("WebDriver is null");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get();
	}
	
	public static ActionDriver getActionDriver() {
		if (actionDriver.get() == null) {
			logger.error("ActionDriver is null");
			throw new IllegalStateException("ActionDriver is not initialized");
		}
		return actionDriver.get();
	}

	public static void setDriver(ThreadLocal<WebDriver> driver) {
		BaseClass.driver = driver;
	}
	
	

	public static Properties getProperties() {
		return properties;
	}

	public static void setProperties(Properties properties) {
		BaseClass.properties = properties;
	}



	protected FileInputStream fis;

	@BeforeMethod
	public synchronized void setUpGlobally() {
		System.out.println("Setting up webdriver for: " + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(2);
		logger.info("WebDriver initialized and browser maximized");
		logger.trace("Trace message");
		/*//Initialize the ActionDriver once
		if (actionDriver == null) {
			actionDriver = new ActionDriver(driver);
			logger.info("Action driver is created");
		}*/
		//Initialize the ActionDriver for the current thread
		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("ActionDriver initialized for thread: "+Thread.currentThread().getId());

	}

	@BeforeSuite
	public void loadConfig() {
		// Load the configuration file
		properties = new Properties();
		try {
			fis = new FileInputStream(new File("src/main/resources/config.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Property file loaded");
		//Start Extent Report
		//ExtentManager.getReporter(); //This is implemented in ITestListener
	}

	// Initialize the WebDriver based on the config.properties value
	private void launchBrowser() {

		String browser = properties.getProperty("browser").toLowerCase().trim();

		switch (browser) {
		case "chrome":
			ChromeOptions options = new ChromeOptions();

			// Run Chrome in headless mode
			options.addArguments("--headless");

			// Disable GPU acceleration
			options.addArguments("--disable-gpu");

			// Start Chrome maximized
			options.addArguments("--start-maximized");

			// Disable browser notifications
			options.addArguments("--disable-notifications");

			// Disable infobars
			options.addArguments("--disable-infobars");

			// Set a custom user agent
			options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

			// Disable extensions
			options.addArguments("--disable-extensions");
			
			// Ignore certificate errors
			options.addArguments("--ignore-certificate-errors");

			driver.set(new ChromeDriver(options));
			logger.info("ChromeDriver instance got created");
			ExtentManager.registerDriver(getDriver());
			break;

		case "firefox":
			FirefoxOptions optionsFireFox = new FirefoxOptions();
			// Run Firefox in headless mode
			optionsFireFox.addArguments("-headless");

			// Disable GPU acceleration
			optionsFireFox.addArguments("-disable-gpu");

			// Start Firefox maximized
			optionsFireFox.addArguments("--start-maximized");

			// Disable browser notifications
			optionsFireFox.addPreference("dom.webnotifications.enabled", false);

			// Disable infobars
			optionsFireFox.addPreference("browser.contentblocking.introCount", 99);

			// Set a custom user agent
			optionsFireFox.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0");

			// Disable extensions
			optionsFireFox.addArguments("-disable-extensions");

			// Ignore certificate errors
			optionsFireFox.setAcceptInsecureCerts(true);
			
			driver.set(new FirefoxDriver(optionsFireFox));
			logger.info("Firefox instance got created");
			ExtentManager.registerDriver(getDriver());
			break;

		case "edge":
			EdgeOptions edgeOptions = new EdgeOptions();

			// Run Edge in headless mode
			edgeOptions.addArguments("--headless");

			// Disable GPU acceleration
			edgeOptions.addArguments("--disable-gpu");

			// Start Edge maximized
			edgeOptions.addArguments("--start-maximized");

			// Disable browser notifications
			edgeOptions.addArguments("--disable-notifications");

			// Disable infobars
			edgeOptions.addArguments("--disable-infobars");

			// Set a custom user agent
			edgeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Edg/91.0.864.59");

			// Disable extensions
			edgeOptions.addArguments("--disable-extensions");

			// Ignore certificate errors
			edgeOptions.setCapability("acceptInsecureCerts", true);

			driver.set(new EdgeDriver(edgeOptions));
			logger.info("EdgeDriver instance got created");
			ExtentManager.registerDriver(getDriver());
			break;

		default:
			throw new IllegalArgumentException("browser not supported");
		}
	}

	// configure browser settings such as implicit wait, maximize browser and
	// navigate tot he url
	private void configureBrowser() {
		// Implement the implicit wait
		getDriver().manage().timeouts()
				.implicitlyWait(Duration.ofSeconds(Integer.parseInt(properties.getProperty("implicitWait"))));

		// maximize the browser
		getDriver().manage().window().maximize();

		try {
			// navigate to URL
			getDriver().get(properties.getProperty("url"));
		} catch (Exception e) {
			logger.error("failed to navigate to the url " + e.getMessage());
		}

	}

	@AfterMethod
	public synchronized void tearDown() {
		if (getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				logger.error("unable to quit the driver " + e.getMessage());
			}
		}
		driver.remove();
		actionDriver.remove();;
		logger.info("WebDriver instance is closed");
		//ExtentManager.endTest(); //Implemented in ITestListeners
	}
	
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

}
