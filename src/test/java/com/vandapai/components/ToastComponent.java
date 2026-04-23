package com.vandapai.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ToastComponent {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ToastComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    public boolean isToastDisplayed(String message) {
        By toast = By.xpath("//*[contains(normalize-space(),\"" + message + "\")]");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(toast)).isDisplayed();
    }
}