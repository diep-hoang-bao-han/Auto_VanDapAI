package com.vandapai.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ConfirmDialogComponent {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ConfirmDialogComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void confirmDelete() {
        By confirmDeleteBtn = By.xpath("//div[@role='dialog']//button[normalize-space()='Xóa'] | //button[normalize-space()='Xóa']");
        wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn)).click();
    }

    public void clickCancel() {
        By cancelBtn = By.xpath("//div[@role='dialog']//button[contains(.,'Hủy bỏ')] | //button[contains(.,'Hủy bỏ')]");
        wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
    }

    public boolean isDialogDisplayed(String titleText) {
        By dialogTitle = By.xpath("//*[contains(normalize-space(),\"" + titleText + "\")]");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(dialogTitle)).isDisplayed();
    }
}