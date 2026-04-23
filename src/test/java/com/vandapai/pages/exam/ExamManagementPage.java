package com.vandapai.pages.exam;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ExamManagementPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ExamManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private final By createExamBtn = By.xpath("//button[contains(.,'Tạo đề thi')]");

    public void clickCreateExam() {
        wait.until(ExpectedConditions.elementToBeClickable(createExamBtn)).click();
    }
}