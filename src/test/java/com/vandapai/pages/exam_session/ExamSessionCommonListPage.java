package com.vandapai.pages.exam_session;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class ExamSessionCommonListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By commonTitle = By.xpath("//*[contains(normalize-space(.),'Xem danh sách chung')]");
    private final By summaryList = By.cssSelector(".common-summary-list");
    private final By studentRows = By.cssSelector("table.common-table tbody tr");
    private final By firstStudentCode = By.cssSelector(".common-student-code");
    private final By roomChip = By.cssSelector(".common-room-chip");
    private final By examCodeChip = By.cssSelector(".common-code-chip");

    public ExamSessionCommonListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(commonTitle));
        wait.until(ExpectedConditions.visibilityOfElementLocated(summaryList));
    }

    public void assertCommonListDisplayedCorrectly(String sessionName) {
        waitUntilLoaded();

        Assert.assertTrue(
                driver.findElement(summaryList).getText().contains(sessionName),
                "Danh sách chung không hiển thị đúng tên kỳ thi vừa tạo: " + sessionName
        );

        Assert.assertTrue(
                driver.findElements(studentRows).size() > 0,
                "Danh sách chung không có dòng sinh viên nào."
        );

        Assert.assertTrue(
                driver.findElement(firstStudentCode).isDisplayed(),
                "Không thấy mã sinh viên trong danh sách chung."
        );

        Assert.assertTrue(
                driver.findElement(roomChip).isDisplayed(),
                "Không thấy phòng thi trong danh sách chung."
        );

        Assert.assertTrue(
                driver.findElement(examCodeChip).isDisplayed(),
                "Không thấy mã đề trong danh sách chung."
        );
    }
}