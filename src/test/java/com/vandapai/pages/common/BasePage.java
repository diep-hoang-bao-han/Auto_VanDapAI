package com.vandapai.pages.common;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    protected WebDriverWait waitSeconds(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                element
        );
    }

    protected void clickElement(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            try {
                new Actions(driver).moveToElement(element).click().perform();
            } catch (Exception ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        }
    }

    protected void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollToElement(element);
        clickElement(element);
    }

    protected void typeText(WebElement input, String value) {
        scrollToElement(input);

        input.click();
        input.sendKeys(Keys.CONTROL, "a");
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);

        sleep(300);
    }

    protected String getPageAndToastText() {
        String bodyText = "";
        String toastText = "";

        try {
            bodyText = driver.findElement(By.tagName("body")).getText();
        } catch (Exception ignored) {
        }

        try {
            Object textObj = ((JavascriptExecutor) driver).executeScript(
                    "let items = document.querySelectorAll('.global-toast-message');" +
                            "return Array.from(items).map(e => e.innerText.trim()).join(' | ');"
            );

            toastText = textObj == null ? "" : textObj.toString();
        } catch (Exception ignored) {
        }

        String result = bodyText + " | " + toastText;
        System.out.println("PAGE + TOAST TEXT = " + result);

        return result;
    }

    public void waitForUserToSeeToast() {
        sleep(2000);
    }

    protected void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}