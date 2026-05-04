package com.vandapai.pages.exam_session;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

/**
 * ExamSessionDetailPage: Xử lý các thao tác trên màn hình Chi tiết ca thi.
 * Bao gồm: xem thông tin ca thi, trạng thái, danh sách phòng,
 * xem danh sách chung, chỉnh sửa ca thi, xóa ca thi.
 */
public class ExamSessionDetailPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // =========================
    // LOCATORS
    // =========================
    private final By detailTitle = By.xpath("//*[contains(normalize-space(.),'Chi tiết ca thi')]");
    private final By detailStatusBadge = By.id("detailStatusBadge");
    private final By viewCommonListButton = By.xpath("//a[contains(normalize-space(.),'Xem danh sách chung')]");
    private final By roomTable = By.cssSelector("table.detail-table");
    private final By editButton = By.cssSelector("a.btn-edit");
    private final By deleteButton = By.id("openDeleteModalButton");
    private final By deleteModal = By.id("customDeleteModal");
    private final By confirmDeleteButton = By.id("confirmDeleteBtn");

    // =========================
    // CONSTRUCTOR
    // =========================
    public ExamSessionDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Click vào element bằng JS (fallback khi click thường lỗi).
     */
    private void clickByJs(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // =========================
    // WAIT & VERIFICATION METHODS
    // =========================

    /**
     * Đợi cho đến khi màn hình chi tiết ca thi được tải hoàn tất.
     * - Đợi tiêu đề "Chi tiết ca thi" hiển thị.
     * - Đợi badge trạng thái hiển thị.
     */
    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(detailTitle));
        wait.until(ExpectedConditions.visibilityOfElementLocated(detailStatusBadge));
    }

    /**
     * Kiểm tra màn hình chi tiết hiển thị đúng với tên ca thi.
     * @param sessionName Tên ca thi cần kiểm tra
     */
    public void assertDetailDisplayedCorrectly(String sessionName) {
        waitUntilLoaded();

        // Kiểm tra tên ca thi hiển thị trong page source
        Assert.assertTrue(
                driver.getPageSource().contains(sessionName),
                "Màn hình chi tiết không hiển thị đúng tên ca thi vừa tạo: " + sessionName
        );

        // Kiểm tra badge trạng thái hiển thị
        Assert.assertTrue(
                driver.findElement(detailStatusBadge).isDisplayed(),
                "Không thấy badge trạng thái ca thi ở màn hình chi tiết."
        );

        // Kiểm tra bảng danh sách phòng thi hiển thị
        Assert.assertTrue(
                driver.findElement(roomTable).isDisplayed(),
                "Không thấy bảng danh sách phòng thi ở màn hình chi tiết."
        );
    }

    // =========================
    // ACTION METHODS
    // =========================

    /**
     * Click nút "Xem danh sách chung" để điều hướng sang màn hình danh sách chung.
     * - Đợi URL chứa "/common-list" hoặc tiêu đề danh sách chung hiển thị.
     */
    public void clickViewCommonList() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(viewCommonListButton));

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/common-list"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(.),'Xem danh sách chung')]"))
        ));
    }

    /**
     * Click nút "Chỉnh sửa" để điều hướng sang màn hình chỉnh sửa ca thi.
     * - Đợi URL chứa "mode=edit" hoặc tiêu đề "Chỉnh sửa ca thi" hiển thị.
     */
    public void clickEditButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(editButton));
        clickByJs(button);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("mode=edit"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'Chỉnh sửa ca thi')]")
                )
        ));
    }

    /**
     * Xóa ca thi hiện tại.
     * - Click nút Xóa, đợi modal xác nhận hiển thị, click xác nhận.
     * - Đợi quay lại màn hình Danh sách ca thi.
     */
    public void deleteSession() {
        // Click nút Xóa
        WebElement delete = wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
        clickByJs(delete);

        // Đợi modal xác nhận hiển thị
        wait.until(ExpectedConditions.visibilityOfElementLocated(deleteModal));

        // Click nút xác nhận xóa
        WebElement confirm = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton));
        clickByJs(confirm);

        // Đợi quay lại trang danh sách ca thi
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/lecturer/exam-sessions"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'Danh sách ca thi')]")
                )
        ));
    }
}