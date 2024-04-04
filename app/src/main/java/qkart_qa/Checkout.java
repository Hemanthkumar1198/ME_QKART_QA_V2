package qkart_qa;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    public Checkout(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    public Boolean addNewAddress(String addresString) {
        try {
            WebElement addNewAddressButton = driver.findElement(By.id("add-new-btn"));
            addNewAddressButton.click();

            WebElement AddressBox = driver.findElement(By.className("MuiOutlinedInput-input"));
            AddressBox.clear();
            AddressBox.sendKeys(addresString);

            List<WebElement> buttons = driver.findElements(By.className("css-177pwqq"));
            for (WebElement button : buttons) {
                if (button.getText().equals("ADD")) {
                    button.click();
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(String.format(
                            "//*[@class='MuiTypography-root MuiTypography-body1 css-yg30e6' and text()='%s']",
                            addresString))));
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;

        }
    }

    public Boolean selectAddress(String addressToSelect) {
        try {
            WebElement parentBox = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[1]/div/div[1]"));
            List<WebElement> allBoxes = parentBox.findElements(By.className("not-selected"));

            for (WebElement box : allBoxes) {
                if (box.findElement(By.className("css-yg30e6")).getText().replaceAll(" ", "")
                        .equals(addressToSelect.replaceAll(" ", ""))) {
                    box.findElement(By.tagName("input")).click();
                    return true;
                }
            }
            System.out.println("Unable to find the given address");
            return false;
        } catch (Exception e) {
            System.out.println("Exception Occurred while selecting the given address: " + e.getMessage());
            return false;
        }

    }

    public Boolean placeOrder() {
        try {
            List<WebElement> elements = driver.findElements(By.className("css-177pwqq"));
            for (WebElement element : elements) {
                if (element.getText().equals("PLACE ORDER")) {
                    element.click();
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    public Boolean verifyInsufficientBalanceMessage() {
        try {
            WebElement alertMessage = driver.findElement(By.id("notistack-snackbar"));
            if (alertMessage.isDisplayed()) {
                if (alertMessage.getText().equals("You do not have enough balance in your wallet for this purchase")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    }
}


