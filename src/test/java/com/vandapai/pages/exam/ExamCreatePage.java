package com.vandapai.pages.exam;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ExamCreatePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ExamCreatePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private final By examCodeInput =
            By.xpath("//input[@placeholder='Nhập mã bộ đề']");

    private final By schoolYearInput =
            By.xpath("//input[contains(@value,'2024') or @placeholder='Nhập năm học']");

    private final By createExamBtn =
            By.xpath("//button[contains(.,'Tiến hành tạo đề thi') or contains(.,'Tạo đề thi')]");

    public void enterExamCode(String examCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(examCodeInput)).sendKeys(examCode);
    }

    public void clickCreateExamButton() {
        wait.until(ExpectedConditions.elementToBeClickable(createExamBtn)).click();
    }

    public boolean isDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(createExamBtn)).isDisplayed();
    }
}