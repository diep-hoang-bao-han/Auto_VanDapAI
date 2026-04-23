package com.vandapai.components;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(questionAndExamMenu));

        try {
            menu.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
        }

        By dropdown = By.id("qna-dropdown");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdown));

        By questionManagementLink = By.xpath("//*[@id='qna-dropdown']//a[contains(.,'Quản lý câu hỏi')]");
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(questionManagementLink));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", link
        );

        try {
            link.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }

        wait.until(ExpectedConditions.urlContains("/lecturer/questions"));
    }
    public void openExamManagement() {
        By questionAndExamMenu = By.xpath("//*[@id='sidebar']/nav/div/button");
        wait.until(ExpectedConditions.elementToBeClickable(questionAndExamMenu)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("qna-dropdown")));

        By examManagementLink = By.xpath("//*[@id='qna-dropdown']/a[2]");
        wait.until(ExpectedConditions.elementToBeClickable(examManagementLink)).click();
    }
}