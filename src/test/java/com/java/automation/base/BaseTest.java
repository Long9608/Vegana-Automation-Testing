package com.java.automation.base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.java.automation.config.TestConfig;
import com.java.automation.utils.ExtentReportManager;
import com.java.automation.utils.LoggerUtil;
import com.java.automation.utils.ScreenshotUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

/**
 * Base test class that sets up and tears down WebDriver
 * Includes Extent Reports, Logging, and Screenshot capabilities
 */
public class BaseTest {
    protected WebDriver driver;
    protected Logger logger;
    protected ExtentTest extentTest;

    @BeforeSuite
    public void setUpSuite() {
        // Initialize Extent Reports
        ExtentReportManager.getInstance();
        logger = LoggerUtil.getLogger(this.getClass());
        logger.info("Test Suite started");
    }

    @BeforeMethod
    public void setUp(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String testDescription = result.getMethod().getDescription();
        if (testDescription == null || testDescription.isEmpty()) {
            testDescription = "Test: " + testName;
        }
        
        // Create test in Extent Report
        extentTest = ExtentReportManager.createTest(testName, testDescription);
        
        logger = LoggerUtil.getLogger(this.getClass());
        logger.info("Starting test: " + testName);
        String browser = TestConfig.getBrowser().toLowerCase();
        
        switch (browser) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                // Browser sẽ tự động mở và hiển thị (không headless)
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-infobars");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--no-sandbox");
                // Đảm bảo browser hiển thị (không headless)
                chromeOptions.setHeadless(false);
                // Selenium 4 tự động quản lý driver thông qua Selenium Manager
                driver = new ChromeDriver(chromeOptions);
                break;
                
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--start-maximized");
                // Đảm bảo browser hiển thị (không headless)
                firefoxOptions.setHeadless(false);
                // Selenium 4 tự động quản lý driver thông qua Selenium Manager
                driver = new FirefoxDriver(firefoxOptions);
                break;
                
            case "edge":
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-infobars");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--no-sandbox");
                // Đảm bảo browser hiển thị (không headless)
                options.setHeadless(false);
                // Selenium 4 tự động quản lý driver thông qua Selenium Manager
                driver = new ChromeDriver(options);
                break;
                
            default:
                ChromeOptions defaultOptions = new ChromeOptions();
                defaultOptions.addArguments("--start-maximized");
                defaultOptions.addArguments("--disable-notifications");
                defaultOptions.addArguments("--disable-infobars");
                // Đảm bảo browser hiển thị (không headless)
                defaultOptions.setHeadless(false);
                // Selenium 4 tự động quản lý driver thông qua Selenium Manager
                driver = new ChromeDriver(defaultOptions);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TestConfig.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.getPageLoadTimeout()));
        driver.get(TestConfig.getBaseUrl());
        
        logger.info("Navigated to: " + TestConfig.getBaseUrl());
        extentTest.log(Status.INFO, "Navigated to: " + TestConfig.getBaseUrl());
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        
        // Log test result
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test FAILED: " + testName);
            extentTest.log(Status.FAIL, "Test Failed: " + result.getThrowable().getMessage());
            
            // Take screenshot on failure
            String screenshotPath = ScreenshotUtil.takeScreenshot(driver, testName + "_FAILED");
            if (screenshotPath != null) {
                try {
                    extentTest.addScreenCaptureFromPath(screenshotPath);
                    logger.info("Screenshot saved: " + screenshotPath);
                } catch (Exception e) {
                    logger.error("Error adding screenshot to report: " + e.getMessage());
                }
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("Test PASSED: " + testName);
            extentTest.log(Status.PASS, "Test Passed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("Test SKIPPED: " + testName);
            extentTest.log(Status.SKIP, "Test Skipped");
        }
        
        // Close browser
        if (driver != null) {
            driver.quit();
            logger.info("Browser closed");
        }
        
        // Flush Extent Report
        ExtentReportManager.flush();
    }
}

