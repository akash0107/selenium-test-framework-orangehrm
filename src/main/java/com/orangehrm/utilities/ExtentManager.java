package com.orangehrm.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static Map<Long, WebDriver> driverMap = new HashMap<>();

	// Initialize the Extent Report
	public synchronized static ExtentReports getReporter() {
		if (extent == null) {
			String driverPath = System.getProperty("user.dir") + "/src/test/resources/ExtentReport/ExtentReport.html";
			ExtentSparkReporter spark = new ExtentSparkReporter(driverPath);
			spark.config().setReportName("Automation Test Report");
			spark.config().setDocumentTitle("OrangeHRM Test");
			spark.config().setTheme(Theme.DARK);

			extent = new ExtentReports();
			extent.attachReporter(spark);
			// Adding System information
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("JAVA Version", System.getProperty("java.version"));
			extent.setSystemInfo("User Name", System.getProperty("user.name"));
		}
		return extent;
	}

	// Start the test
	public synchronized static ExtentTest startTest(String testName) {
		ExtentTest extentTest = getReporter().createTest(testName);
		test.set(extentTest);
		return extentTest;
	}

	// End the test
	public synchronized static void endTest() {
		getReporter().flush();
	}

	// Get current Thread's test
	public synchronized static ExtentTest getTest() {
		return test.get();
	}

	// Method to get the name of the current Thread
	public static String getTestName() {
		ExtentTest currentTest = getTest();
		if (currentTest != null) {
			return currentTest.getModel().getName();
		}
		else {
			return "No test is currently Active for the Thread";
		}
	}
	
	//Log a step
	public static void logStep(String logMessage) {
		getTest().info(logMessage);
	}
	
	//Log step validation with Screenshot
	public static void logStepWithScreenShot(WebDriver driver, String logMessage,String ScreenShotMessage) {
		getTest().pass(logMessage);
		//screenShot 
		attachScreenshot(driver, ScreenShotMessage);
	}
	
	//Log a failure
	public static void logFailure(WebDriver driver, String logMessage,String ScreenShotMessage) {
		String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
		getTest().fail(colorMessage);
		attachScreenshot(driver, ScreenShotMessage);
	}
	
	//Log a skip
	public static void logSkip(String logMessage) {
		String colorMessage = String.format("<span style='color:orange;'>%s</span>", logMessage);
		getTest().skip(colorMessage);
	}
	
	//Take a ScreenShot with Date and Time
	public synchronized static String takeScreenShot(WebDriver driver, String screenShotName) {
		File src =  ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		//Format Date and time for File name
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		
		//Saving the screenshot to a file
		String destPath = System.getProperty("user.dir") + "/src/test/resources/ExtentReport/screenshots/"+screenShotName+"_"+timeStamp+".png";
		try {
			FileUtils.copyFile(src, new File(destPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Convert Screenshot to Base64 for embedding in Report
		String Base64Format = convertToBase64(src);
		return Base64Format;
	}
	
	//Convert Screenshot to Base64Format
	public static String convertToBase64(File screenshotFile) {
		String base64Format = "";
		//Read the file content into byte array
		byte[] fileContent ;
		try {
			fileContent = FileUtils.readFileToByteArray(screenshotFile);
			base64Format  = Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return base64Format;
	}

	
	//Attach screenshot to Repot using Base64
	public static void attachScreenshot(WebDriver driver, String message) {
		try {
			String screenShotBase64 = takeScreenShot(driver, getTestName());
			getTest().info(message,com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(screenShotBase64).build());
		} catch (Exception e) {
			getTest().fail("Failed to attach the screenshot: "+message);
			e.printStackTrace();
		}
	}
	// Register WebDriver for current thread
	public static void registerDriver(WebDriver driver) {
		driverMap.put(Thread.currentThread().getId(), driver);
	}
}
