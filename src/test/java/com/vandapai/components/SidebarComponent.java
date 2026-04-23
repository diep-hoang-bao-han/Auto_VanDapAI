package com.vandapai.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SidebarComponent {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public SidebarComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void openQuestionManagement() {
        By questionAndExamMenu = By.xpath("//*[@id='sidebar']/nav/div/button");
        wait.until(ExpectedConditions.elementToBeClickable(questionAndExamMenu)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("qna-dropdown")));

        By questionManagementLink = By.xpath("//*[@id='qna-dropdown']/a[1]");
        wait.until(ExpectedConditions.elementToBeClickable(questionManagementLink)).click();

        wait.until(ExpectedConditions.urlContains("/lecturer/questions/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("goDetailBtn")));
    }

    public void openExamManagement() {
        By questionAndExamMenu = By.xpath("//*[@id='sidebar']/nav/div/button");
        wait.until(ExpectedConditions.elementToBeClickable(questionAndExamMenu)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("qna-dropdown")));

        By examManagementLink = By.xpath("//*[@id='qna-dropdown']/a[2]");
        wait.until(ExpectedConditions.elementToBeClickable(examManagementLink)).click();
    }
}

