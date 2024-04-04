package qkart_qa;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Register {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/register";
    public String lastGeneratedUsername = "";

    public Register(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToRegisterPage() {
        if (!driver.getCurrentUrl().equals(this.url)) {
            driver.get(this.url);
        }
    }

    public Boolean registerUser(String Username, String Password, Boolean makeUsernameDynamic)
            throws InterruptedException, TimeoutException {
        WebElement username_txt_box = this.driver.findElement(By.id("username"));

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String test_data_username;
        if (makeUsernameDynamic)
            
            test_data_username = Username + "_" + String.valueOf(timestamp.getTime());
        else
            test_data_username = Username;

        // Type the generated username in the username field
        username_txt_box.sendKeys(test_data_username);

        WebElement password_txt_box = this.driver.findElement(By.id("password"));
        String test_data_password = Password;

        password_txt_box.sendKeys(test_data_password);

        WebElement confirm_password_txt_box = this.driver.findElement(By.id("confirmPassword"));

        confirm_password_txt_box.sendKeys(test_data_password);
        Thread.sleep(1000);

        WebElement register_now_button = this.driver.findElement(By.className("button"));

        register_now_button.click();
        Thread.sleep(3000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/login")));

        this.lastGeneratedUsername = test_data_username;

        return this.driver.getCurrentUrl().endsWith("/login");
    }
}
