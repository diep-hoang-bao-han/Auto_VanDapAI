package com.vandapai.pages.exam;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ExamManagementPage extends BasePage {

    public ExamManagementPage(WebDriver driver) {
        super(driver);
    }

    private final By createExamButton = By.xpath(
            "//a[@href='/lecturer/exam-codes/create/' and contains(normalize-space(.),'Tạo đề thi')]"
    );

    private final By examSearchInput = By.xpath(
            "//input[contains(@placeholder,'Tìm kiếm') " +
                    "or contains(@placeholder,'Tìm') " +
                    "or contains(@placeholder,'mã bộ đề') " +
                    "or contains(@placeholder,'Mã bộ đề') " +
                    "or contains(@id,'search') " +
                    "or contains(@name,'search')]"
    );

    public String generateExamSetName(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }

    public void clickCreateExamButton() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(createExamButton));
        scrollToElement(button);

        String href = button.getAttribute("href");

        if (href != null && !href.trim().isEmpty()) {
            driver.get(href);
        } else {
            clickElement(button);
        }

        wait.until(ExpectedConditions.urlContains("/lecturer/exam-codes/create"));
    }

    public void openExamSetListPage() {
        String currentUrl = driver.getCurrentUrl();
        String baseUrl = currentUrl.substring(0, currentUrl.indexOf("/lecturer/"));

        driver.get(baseUrl + "/lecturer/exam-codes/");

        wait.until(driver -> {
            String bodyText = driver.findElement(By.tagName("body")).getText();

            return bodyText.contains("Quản lý đề thi")
                    || bodyText.contains("Danh sách bộ đề")
                    || bodyText.contains("Tạo đề thi");
        });

        sleep(1000);
    }

    public void openFirstExamSetCard() {
        openExamSetListPage();

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement firstCard = shortWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(
                        "(//a[contains(@href,'/lecturer/exam-codes/') and not(contains(@href,'create'))]" +
                                " | //div[contains(@class,'card') and .//text()[contains(.,'Mã đề') or contains(.,'Bộ đề')]]" +
                                " | //tr[.//td])[1]"
                )
        ));

        scrollToElement(firstCard);

        try {
            firstCard.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstCard);
        }

        sleep(1500);
    }

    public void searchExamSet(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(examSearchInput));
        scrollToElement(input);

        input.click();
        input.sendKeys(Keys.CONTROL, "a");
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(keyword);
        input.sendKeys(Keys.ENTER);

        sleep(1500);
    }

    public boolean isExamSetDisplayedInList(String keyword) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            String bodyText = driver.findElement(By.tagName("body")).getText();

            System.out.println("EXAM SET LIST TEXT = " + bodyText);

            return bodyText.contains(keyword);
        });
    }

    public void openExamSetDetailByName(String examSetName) {
        WebElement examSetElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(normalize-space(.),'" + examSetName + "')]")
        ));

        scrollToElement(examSetElement);
        clickElement(examSetElement);

        sleep(1500);
    }
}