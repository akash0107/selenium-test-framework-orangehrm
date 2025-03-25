package com.orangehrm.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class TestListeners implements ITestListener,IAnnotationTransformer{

	//Triggers when a test starts
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.startTest(testName);
		ExtentManager.logStep("Test started: "+testName);
	}

	//Triggers when the test passes
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logStepWithScreenShot(BaseClass.getDriver(), "Test passed successfully", "Test end: "+testName+"-Test passed");
	}

	//Triggered when a test failed
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String failureMsg = result.getThrowable().getMessage();
		ExtentManager.logStep(failureMsg);
		ExtentManager.logFailure(BaseClass.getDriver(), "Test failed: "+testName, "-Test Failed");
	}
	
	
	//Triggeres when test skipps
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkip("Test skipped: "+testName);
	}

	//Triggers on Test start
	@Override
	public void onStart(ITestContext context) {
		ExtentManager.getReporter();
	}

	//Triggers when the suite finishes
	@Override
	public void onFinish(ITestContext context) {
		ExtentManager.endTest();
	}

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}
	
	
	

}
