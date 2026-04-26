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
        String baseUrl;

        if (currentUrl.contains("/lecturer/")) {
            baseUrl = currentUrl.substring(0, currentUrl.indexOf("/lecturer/"));
        } else {
            baseUrl = "http://127.0.0.1:8000";
        }

        driver.get(baseUrl + "/lecturer/exam-codes/");

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        shortWait.until(driver -> {
            try {
                String current = driver.getCurrentUrl();
                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("EXAM SET LIST URL = " + current);
                System.out.println("EXAM SET LIST BODY = " + bodyText);

                return current.contains("/lecturer/exam-codes")
                        && !current.matches(".*/lecturer/exam-codes/\\d+/?$")
                        && (
                        bodyText.contains("Danh sách bộ đề")
                                || bodyText.contains("Tạo đề thi")
                                || bodyText.contains("Mã bộ đề")
                                || bodyText.contains("Số mã đề")
                                || driver.findElements(By.id("examSetTableBody")).size() > 0
                );
            } catch (Exception e) {
                return false;
            }
        });

        sleep(1500);
    }

    public void openFirstExamSetCard() {
        openExamSetListPage();

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        By firstExamSetRow = By.xpath("//*[@id='examSetTableBody']/tr[1]");

        WebElement firstRow = shortWait.until(ExpectedConditions.presenceOfElementLocated(firstExamSetRow));

        scrollToElement(firstRow);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(firstRow));
            firstRow.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstRow);
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

    //   lấy số lượng mã đề ở mã đè vừa tạo
    private final By firstExamSetCodeCountCell = By.xpath("//*[@id='examSetTableBody']/tr[1]/td[4]");
    public String getFirstExamSetCodeCountText() {
        openExamSetListPage();

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement cell = shortWait.until(
                ExpectedConditions.visibilityOfElementLocated(firstExamSetCodeCountCell)
        );

        String text = cell.getText().trim();

        System.out.println("FIRST EXAM SET CODE COUNT TEXT = " + text);

        return text;
    }
    //    Kiểm tra số mã đề về 0
    public boolean isFirstExamSetCodeCountZero() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                WebElement cell = driver.findElement(firstExamSetCodeCountCell);
                String text = cell.getText().trim();

                System.out.println("FIRST EXAM SET CODE COUNT TEXT = " + text);

                return text.equals("0")
                        || text.contains("0 mã")
                        || text.matches(".*\\b0\\b.*");
            } catch (Exception e) {
                return false;
            }
        });
    }
}