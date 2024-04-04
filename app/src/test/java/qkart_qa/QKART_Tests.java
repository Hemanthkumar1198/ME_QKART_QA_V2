package qkart_qa;

import java.io.File;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(ListenerClass.class)
public class QKART_Tests {
    public static WebDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        driver = new ChromeDriver();
        System.out.println("created driver");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
    @Test(groups = "Sanity_Test", description = "sample", priority = 1)
    public void TestCase01() throws InterruptedException, TimeoutException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase1");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        Assert.assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        // logStatus("End TestCase", "Test Case 1: Verify user Registration : ", status
        // ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase1");
    }

    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    // public static void logStatus(String type, String message, String status) {

    // System.out.println(String.format("%s | %s | %s | %s",
    // String.valueOf(java.time.LocalDateTime.now()), type,
    // message, status));
    // }

    public static void takeScreenshot(String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(groups = "Sanity_Test", description = "sample", priority = 2)
    public void TestCase02() throws InterruptedException, TimeoutException {
        Boolean status;
        // logStatus("Start Testcase", "Test Case 2: Verify User Registration with an
        // existing username ", "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Verify User registration failed");
        // logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
        // if (!status) {
        // logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status
        // ? "PASS" : "FAIL");
        // return false;

        // }

        lastGeneratedUserName = registration.lastGeneratedUsername;

        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);
        Assert.assertFalse(status, "Verify User registration using lastgenrateduser failed");
        // logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status
        // ? "FAIL" : "PASS");
        // return !status;
    }

    /*
     * Verify the functinality of the search text box
     */
    @Test(groups = "Sanity_Test", description = "sample", priority = 3)
    public void TestCase03() throws InterruptedException {
        // logStatus("TestCase 3", "Start test case : Verify functionality of search box
        // ", "DONE");
        boolean status;
        SoftAssert softassert = new SoftAssert();
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        Thread.sleep(5000);

        status = homePage.searchForProduct("YONEX");
        softassert.assertTrue(status, "search for given product failed");
        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Unable to search for given
        // product", "FAIL");
        // return false;
        // }

        List<WebElement> searchResults = homePage.getSearchResults();

        // if (searchResults.size() == 0) {
        // logStatus("TestCase 3", "Test Case Failure. There were no results for the
        // given search string", "FAIL");
        // return false;
        // }

        for (WebElement webElement : searchResults) {
            SearchResult resultelement = new SearchResult(webElement);

            String elementText = resultelement.getTitleofResult();
            Assert.assertEquals(elementText, "YONEX Smash Badminton Racquet",
                    "Test Results contains un-expected values");
            // if (!elementText.toUpperCase().contains("YONEX")) {
            // logStatus("TestCase 3", "Test Case Failure. Test Results contains un-expected
            // values: " + elementText,
            // "FAIL");
            // return false;
            // }
        }

        // logStatus("Step Success", "Successfully validated the search results ",
        // "PASS");
        Thread.sleep(2000);

        status = homePage.searchForProduct("Gesundheit");

        // Assert.assertEquals(status, "Gesundheit", "verify No product found failed");
        // if (!elementText.toUpperCase().contains("YONEX")) {
        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Unable to search for given
        // product", "FAIL");
        // return false;
        // }

        searchResults = homePage.getSearchResults();
        Assert.assertEquals(searchResults.size(), 0, "result is not equal to 0");
        Assert.assertTrue(homePage.isNoResultFound());
        // if (searchResults.size() == 0) {
        // if (homePage.isNoResultFound()) {
        // logStatus("Step Success", "Successfully validated that no products found
        // message is displayed", "PASS");
        // }
        // logStatus("TestCase 3", "Test Case PASS. Verified that no search results were
        // found for the given text",
        // "PASS");
        // } else {
        // logStatus("TestCase 3", "Test Case Fail. Expected: no results , actual:
        // Results were available", "FAIL");
        // return false;
        // }

        // return true;
    }

    /*
     * Verify the presence of size chart and check if the size chart content is as
     * expected
     */
    @Test(groups = "Regression_Test", description = "sample", priority = 4)
    public void TestCase04() throws InterruptedException {
        // logStatus("TestCase 4", "Start test case : Verify the presence of size
        // Chart", "DONE");
        boolean status = false;

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        Thread.sleep(5000);

        status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            if (result.verifySizeChartExists()) {
                // logStatus("Step Success", "Successfully validated presence of Size Chart
                // Link", "PASS");

                status = result.verifyExistenceofSizeDropdown(driver);
                // logStatus("Step Success", "Validated presence of drop down", status ? "PASS"
                // : "FAIL");

                // if (result.openSizechart()) {
                // if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,
                // driver)) {
                // logStatus("Step Success", "Successfully validated contents of Size Chart
                // Link", "PASS");
                // } else {
                // logStatus("Step Failure", "Failure while validating contents of Size Chart
                // Link", "FAIL");
                // }

                // status = result.closeSizeChart(driver);

                // } else {
                // logStatus("TestCase 4", "Test Case Fail. Failure to open Size Chart",
                // "FAIL");
                // return false;
                // }

                // } else {
                // logStatus("TestCase 4", "Test Case Fail. Size Chart Link does not exist",
                // "FAIL");
                // return false;
                // }
                // }
                // logStatus("TestCase 4", "Test Case PASS. Validated Size Chart Details",
                // "PASS");
                // return status;
            }
        }
    }

    /*
     * Verify the complete flow of checking out and placing order for products is
     * working correctly
     */
    @Test(groups = "Sanity_Test", description = "sample", priority = 5)
    public void TestCase05() throws InterruptedException, TimeoutException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying
        // products", "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("TestCase 5", "Test Case Failure. Happy Flow Test Failed", "FAIL");
        // }

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();

        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" :
        // "FAIL");
        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Failed : ", status ?
        // "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        status = homePage.searchForProduct("Tan");
        homePage.addProductToCart("Tan Leatherette Weekender Duffle");

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = driver.getCurrentUrl().endsWith("/thanks");

        homePage.navigateToHome();
        Thread.sleep(3000);

        homePage.PerformLogout();

        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status
        // ? "PASS" : "FAIL");
        // return status;
    }

    /*
     * Verify the quantity of items in cart can be updated
     */
    @Test(groups = "Regression_Test", description = "sample", priority = 6)
    @Parameters({ "Xtends", "Yarine" })
    public void TestCase06(String Xtends, String Yarine) throws InterruptedException, TimeoutException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited",
        // "DONE");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        SoftAssert sa = new SoftAssert();
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        sa.assertTrue(status, "User regestration Failed");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Register Failed", status ? "PASS" :
        // "FAIL");
        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        sa.assertTrue(status, "User Login Failed");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" :
        // "FAIL");
        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }

        homePage.navigateToHome();
        status = homePage.searchForProduct(Xtends);
        Assert.assertTrue(status, "Search for the Given Product Failed");
        homePage.addProductToCart(Xtends);

        status = homePage.searchForProduct(Yarine);
        Assert.assertTrue(status, "Search for the Given Product Failed");
        homePage.addProductToCart(Yarine);

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(Xtends, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(Yarine, 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(Xtends, 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "URL does not ends with thanks");

        homePage.navigateToHome();
        homePage.PerformLogout();

        // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ",
        // status ? "PASS" : "FAIL");
        // return status;
    }

    @Test(groups = "Sanity_Test", description = "sample", priority = 7)
    @Parameters({ "Stylecon" })
    public void TestCase07(String Stylecon) throws InterruptedException, TimeoutException {
        Boolean status;
        // logStatus("Start TestCase",
        // "Test Case 7: Verify that insufficient balance error is thrown when the
        // wallet balance is not enough",
        // "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User Perform Registration Failed");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Registration Failed", status ? "PASS"
        // : "FAIL");
        // logStatus("End TestCase",
        // "Test Case 7: Verify that insufficient balance error is thrown when the
        // wallet balance is not enough: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" :
        // "FAIL");
        // logStatus("End TestCase",
        // "Test Case 7: Verify that insufficient balance error is thrown when the
        // wallet balance is not enough: ",
        // status ? "PASS" : "FAIL");
        // return false;
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(Stylecon);
        Assert.assertTrue(status, "Search for given productn Failed");
        homePage.addProductToCart(Stylecon);

        homePage.changeProductQuantityinCart(Stylecon, 10);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();

        // logStatus("End TestCase",
        // "Test Case 7: Verify that insufficient balance error is thrown when the
        // wallet balance is not enough: ",
        // status ? "PASS" : "FAIL");

        // return status;
    }

    @Test(groups = "Regression_Test", description = "sample", priority = 8)
    public void TestCase08() throws InterruptedException, TimeoutException {
        Boolean status = false;

        // logStatus("Start TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab
        // is opened",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User regestration is failed");
        // if (!status) {
        // logStatus("TestCase 8",
        // "Test Case Failure. Verify that product added to cart is available when a new
        // tab is opened",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login is failed");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" :
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab
        // is opened",
        // status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        Assert.assertTrue(status, "Product search failed");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab
        // is opened",
        // status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase08");

        /// return status;
    }

    @Test(groups = "Regression_Test", description = "sample", priority = 9)
    public void TestCase09() throws InterruptedException, TimeoutException {
        Boolean status = false;

        // logStatus("Start TestCase",
        // "Test Case 09: Verify that the Privacy Policy, About Us are displayed
        // correctly ",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");
        SoftAssert Softassert = new SoftAssert();
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Regestration Failed");
        // if (!status) {
        // logStatus("TestCase 09",
        // "Test Case Failure. Verify that the Privacy Policy, About Us are displayed
        // correctly ",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login Failed");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" :
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed
        // correctly ",
        // status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        Softassert.assertTrue(status, "Verifying parent page url Failed");
        // if (!status) {
        // logStatus("Step Failure", "Verifying parent page url didn't change on privacy
        // policy link click failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed
        // correctly ",
        // status ? "PASS" : "FAIL");
        // }

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        Thread.sleep(2000);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        Thread.sleep(2000);
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        Softassert.assertTrue(status, "Verifying new tab opened has Privacy Policy page Failed");
        // if (!status) {
        // logStatus("Step Failure", "Verifying new tab opened has Privacy Policy page
        // heading failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed
        // correctly ",
        // status ? "PASS" : "FAIL");
        // }

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        Thread.sleep(2000);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        Thread.sleep(2000);
        status = TOSHeading.getText().equals("Terms of Service");
        Softassert.assertTrue(status, "Verifying new tab opened has Terms Of Service page Failed");
        // if (!status) {
        // logStatus("Step Failure", "Verifying new tab opened has Terms Of Service page
        // heading failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed
        // correctly ",
        // status ? "PASS" : "FAIL");
        // }

        Softassert.assertAll();

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed
        // correctly ",
        // "PASS");
        // takeScreenshot(driver, "EndTestCase", "TestCase9");

        // return status;
    }

    @Test(description = "sample", priority = 10)
    public void TestCase10() throws InterruptedException {
        // logStatus("Start TestCase",
        // "Test Case 10: Verify that contact us option is working correctly ",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase10");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys("crio user");
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys("criouser@gmail.com");
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys("Testing the contact us page");

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        // logStatus("End TestCase",
        // "Test Case 10: Verify that contact us option is working correctly ",
        // "PASS");

        // takeScreenshot(driver, "EndTestCase", "TestCase10");

        // return true;
        // Assert.assertTrue(true);
    }

    @Test(groups = { "Sanity_Test", "Regression_Test" }, description = "sample", priority = 11)
    public void TestCase11() throws InterruptedException, TimeoutException {
        Boolean status;
        // logStatus("Start TestCase",
        // "Test Case 11: Ensure that the links on the QKART advertisement are
        // clickable",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase11");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "links on the QKART advertisement are clickable failed");
        // if (!status) {
        // logStatus("TestCase 11",
        // "Test Case Failure. Ensure that the links on the QKART advertisement are
        // clickable",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase11");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed failed");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" :
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase 11");
        // logStatus("End TestCase",
        // "Test Case 11: Ensure that the links on the QKART advertisement are
        // clickable",
        // status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
        Assert.assertTrue(status, "Search for yonex Failed");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        Assert.assertTrue(status, "Advertisement size Did not match");
        // logStatus("Step ", "Verify that 3 Advertisements are available", status ?
        // "PASS" : "FAIL");

        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        // logStatus("Step ", "Verify that Advertisement 1 is clickable ", status ?
        // "PASS" : "FAIL");

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        // Assert.assertTrue(status, "Search for yonex Failed");
        // logStatus("Step ", "Verify that Advertisement 2 is clickable ", status ?
        // "PASS" : "FAIL");

        // logStatus("End TestCase",
        // "Test Case 11: Ensure that the links on the QKART advertisement are
        // clickable",
        // status ? "PASS" : "FAIL");
        // return status;
    }
}
