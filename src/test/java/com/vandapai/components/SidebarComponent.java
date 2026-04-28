package com.vandapai.components;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SidebarComponent extends BasePage {

    public SidebarComponent(WebDriver driver) {
        super(driver);
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

        String href = link.getAttribute("href");

        if (href != null && !href.trim().isEmpty()) {
            driver.get(href);
        } else {
            try {
                link.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
            }
        }

        wait.until(ExpectedConditions.urlContains("/lecturer/questions"));
    }

    public void openExamManagement() {
        By questionAndExamMenu = By.xpath("//*[@id='sidebar']/nav/div/button");
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(questionAndExamMenu));

        try {
            menu.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
        }

        By dropdown = By.id("qna-dropdown");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdown));

        By examManagementLink = By.xpath("//*[@id='qna-dropdown']//a[contains(.,'Quản lý đề thi')]");
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(examManagementLink));

        String href = link.getAttribute("href");

        if (href != null && !href.trim().isEmpty()) {
            driver.get(href);
        } else {
            try {
                link.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
            }
        }

        wait.until(ExpectedConditions.urlContains("/lecturer/exam-codes"));
    }

    public void openExamSessionManagement() {
        WebElement menuExamSession = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='sidebar']/nav/a[2]"))
        );

        try {
            menuExamSession.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuExamSession);
        }
    }
}