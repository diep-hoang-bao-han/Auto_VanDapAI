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

/**
 * BasePage: Lớp cha cho tất cả các Page Object.
 * Cung cấp các phương thức tiện ích: wait, scroll, click, type, get toast text, sleep.
 */
public class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // WebDriverWait mặc định 20 giây
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Tạo WebDriverWait với số giây tùy chỉnh.
     */
    protected WebDriverWait waitSeconds(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    /**
     * Cuộn đến element (center màn hình).
     */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                element
        );
    }

    /**
     * Click vào element với nhiều fallback:
     * - Đợi element clickable rồi click thường.
     * - Nếu lỗi, thử Actions.
     * - Nếu vẫn lỗi, dùng JS click.
     */
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

    /**
     * Click vào element thông qua locator.
     * - Tìm element, scroll đến, sau đó click bằng clickElement(WebElement).
     */
    protected void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollToElement(element);
        clickElement(element);
    }

    /**
     * Nhập text vào input (có clear nội dung cũ bằng Ctrl+A + Backspace).
     */
    protected void typeText(WebElement input, String value) {
        scrollToElement(input);

        input.click();
        input.sendKeys(Keys.CONTROL, "a");
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);

        sleep(300);
    }

    /**
     * Lấy text của toàn bộ body + toast message (nếu có).
     * Dùng JS để lấy .global-toast-message.
     */
    protected String getPageAndToastText() {
        String bodyText = "";
        String toastText = "";

        // Lấy text của body
        try {
            bodyText = driver.findElement(By.tagName("body")).getText();
        } catch (Exception ignored) {
        }

        // Lấy text của toast message (nếu có)
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

    /**
     * Chờ user nhìn thấy toast (sleep 2 giây).
     */
    public void waitForUserToSeeToast() {
        sleep(2000);
    }

    /**
     * Sleep theo milliseconds, bắt InterruptedException.
     */
    protected void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}