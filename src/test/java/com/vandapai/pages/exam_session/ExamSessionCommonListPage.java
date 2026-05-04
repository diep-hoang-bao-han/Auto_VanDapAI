package com.vandapai.pages.exam_session;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

/**
 * ExamSessionCommonListPage: Xử lý các thao tác và kiểm tra trên màn hình "Xem danh sách chung"
 * của một ca thi. Màn hình này hiển thị danh sách sinh viên được phân bổ vào ca thi,
 * kèm theo phòng thi và mã đề tương ứng.
 */
public class ExamSessionCommonListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // =========================
    // LOCATORS
    // =========================
    private final By commonTitle = By.xpath("//*[contains(normalize-space(.),'Xem danh sách chung')]");
    private final By summaryList = By.cssSelector(".common-summary-list");
    private final By studentRows = By.cssSelector("table.common-table tbody tr");
    private final By firstStudentCode = By.cssSelector(".common-student-code");
    private final By roomChip = By.cssSelector(".common-room-chip");
    private final By examCodeChip = By.cssSelector(".common-code-chip");

    // =========================
    // CONSTRUCTOR
    // =========================
    public ExamSessionCommonListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    // =========================
    // WAIT METHODS
    // =========================

    /**
     * Đợi cho đến khi màn hình danh sách chung được tải hoàn tất.
     * - Đợi tiêu đề "Xem danh sách chung" hiển thị.
     * - Đợi phần summary list (tổng quan ca thi) hiển thị.
     */
    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(commonTitle));
        wait.until(ExpectedConditions.visibilityOfElementLocated(summaryList));
    }

    // =========================
    // ASSERTION / VERIFICATION METHODS
    // =========================

    /**
     * Kiểm tra toàn bộ nội dung màn hình danh sách chung hiển thị đúng.
     * - Tên ca thi xuất hiện trong summary list.
     * - Có ít nhất một dòng sinh viên.
     * - Hiển thị mã sinh viên.
     * - Hiển thị phòng thi.
     * - Hiển thị mã đề.
     *
     * @param sessionName Tên ca thi cần kiểm tra
     */
    public void assertCommonListDisplayedCorrectly(String sessionName) {
        // Đợi trang tải xong
        waitUntilLoaded();

        // Kiểm tra tên ca thi hiển thị trong phần tóm tắt
        Assert.assertTrue(
                driver.findElement(summaryList).getText().contains(sessionName),
                "Danh sách chung không hiển thị đúng tên kỳ thi vừa tạo: " + sessionName
        );

        // Kiểm tra có ít nhất một sinh viên trong danh sách
        Assert.assertTrue(
                driver.findElements(studentRows).size() > 0,
                "Danh sách chung không có dòng sinh viên nào."
        );

        // Kiểm tra mã sinh viên hiển thị
        Assert.assertTrue(
                driver.findElement(firstStudentCode).isDisplayed(),
                "Không thấy mã sinh viên trong danh sách chung."
        );

        // Kiểm tra phòng thi hiển thị
        Assert.assertTrue(
                driver.findElement(roomChip).isDisplayed(),
                "Không thấy phòng thi trong danh sách chung."
        );

        // Kiểm tra mã đề hiển thị
        Assert.assertTrue(
                driver.findElement(examCodeChip).isDisplayed(),
                "Không thấy mã đề trong danh sách chung."
        );
    }
}