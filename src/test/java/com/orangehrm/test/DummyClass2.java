package com.orangehrm.test;

import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;

public class DummyClass2 extends BaseClass{
	
	@Test
	public void dummyTest() {
		String title = getDriver().getTitle();
		System.out.println(title);
		assert title.equals("OrangeHRM"):"Test failed - Title is not matching";
		System.out.println("Test passed, title is matching");
	}

}
