package com.vandapai.pages.exam_session;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

/**
 * ExamSessionManagementPage: Xử lý các thao tác trên màn hình Quản lý ca thi (Danh sách ca thi).
 * Bao gồm: điều hướng vào trang, tìm kiếm, lọc theo trạng thái,
 * tạo ca thi mới, mở chi tiết ca thi, kiểm tra ca thi hiển thị sau khi tạo/xóa.
 */
public class ExamSessionManagementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // =========================
    // LOCATORS
    // =========================
    private final By menuExamSession = By.xpath("//*[@id='sidebar']/nav/a[2]");
    private final By pageTitle = By.xpath("//*[@id='page-scroll-container']/div[3]/div[1]");
    private final By createSessionButton = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[1]/a");
    private final By searchInput = By.xpath("//*[@id='sessionFilterForm']/div/input");
    private final By statusDropdown = By.xpath("//*[@id='sessionFilterForm']/select[4]");

    private final By sessionRows = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[2]/table/tbody/tr");
    private final By firstSessionName = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[2]/table/tbody/tr[1]/td[2]");
    private final By firstSessionStatus = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[2]/table/tbody/tr[1]/td[8]/div");

    // =========================
    // CONSTRUCTOR
    // =========================
    public ExamSessionManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Kiểm tra element có hiển thị hay không.
     */
    private boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

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

    /**
     * Sleep theo milliseconds.
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // =========================
    // NAVIGATION & WAIT METHODS
    // =========================

    /**
     * Điều hướng đến trang Quản lý ca thi thông qua sidebar menu.
     */
    public void goToExamSessionPage() {
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(menuExamSession));
        clickByJs(menu);
        waitUntilLoaded();
    }

    /**
     * Đợi cho đến khi màn hình Quản lý ca thi được tải hoàn tất.
     */
    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        wait.until(ExpectedConditions.visibilityOfElementLocated(createSessionButton));
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
    }

    /**
     * Kiểm tra xem có đang ở màn hình Quản lý ca thi hay không.
     */
    public boolean isAtExamSessionManagementPage() {
        try {
            String text = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText();
            return text.contains("Ca thi") || text.contains("Danh sách ca thi") || text.contains("Quản lý ca thi");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra các element chính trên màn hình có hiển thị hay không.
     */
    public void assertMainElementsDisplayed() {
        Assert.assertTrue(isDisplayed(pageTitle), "Không thấy tiêu đề Danh sách ca thi.");
        Assert.assertTrue(isDisplayed(createSessionButton), "Không thấy nút + Tạo ca thi mới.");
        Assert.assertTrue(isDisplayed(searchInput), "Không thấy ô tìm kiếm ca thi.");
        Assert.assertTrue(isDisplayed(statusDropdown), "Không thấy dropdown Trạng thái.");
    }

    // =========================
    // ACTION METHODS
    // =========================

    /**
     * Click nút "Tạo ca thi mới".
     */
    public void clickCreateSessionButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(createSessionButton));
        clickByJs(button);
    }

    /**
     * Tìm kiếm ca thi theo từ khóa (tên ca thi).
     * @param keyword Từ khóa cần tìm
     */
    public void searchSession(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(keyword);
        input.sendKeys(Keys.ENTER);
        sleep(1000);
    }

    /**
     * Kiểm tra xem danh sách ca thi có ít nhất một dòng hay không.
     */
    public boolean hasAnyRows() {
        return driver.findElements(sessionRows).size() > 0;
    }

    /**
     * Lấy số lượng dòng ca thi trong bảng.
     */
    public int getRowCount() {
        return driver.findElements(sessionRows).size();
    }

    /**
     * Lấy tên ca thi đầu tiên trong danh sách.
     */
    public String getFirstSessionName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstSessionName)).getText().trim();
    }

    /**
     * Lấy text trạng thái của ca thi đầu tiên.
     */
    public String getFirstSessionStatusText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstSessionStatus)).getText().trim();
    }

    /**
     * Kiểm tra kết quả tìm kiếm có chứa từ khóa mong đợi.
     * @param keyword Từ khóa đã tìm
     */
    public void assertSearchHasResult(String keyword) {
        Assert.assertTrue(hasAnyRows(), "Không có kết quả tìm kiếm với từ khóa: " + keyword);

        String firstName = getFirstSessionName().toLowerCase();
        Assert.assertTrue(
                firstName.contains(keyword.toLowerCase()),
                "Tên ca thi đầu tiên không chứa từ khóa. Actual: " + firstName
        );
    }

    /**
     * Kiểm tra không có kết quả tìm kiếm nào.
     */
    public void assertSearchNoResult() {
        Assert.assertEquals(
                getRowCount(),
                0,
                "Tìm kiếm keyword không tồn tại nhưng bảng vẫn còn dữ liệu."
        );
    }

    /**
     * Lọc ca thi theo trạng thái (dùng visible text).
     * @param visibleText Text hiển thị của option (ví dụ: "Sắp diễn ra")
     */
    public void selectStatusByVisibleText(String visibleText) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(visibleText);
        sleep(1200);
    }

    /**
     * Lọc ca thi theo trạng thái (dùng value).
     * @param value Giá trị của option (ví dụ: "SCHEDULED")
     */
    public void selectStatusByValue(String value) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown));
        Select select = new Select(dropdown);
        select.selectByValue(value);
        sleep(1200);
    }

    /**
     * Kiểm tra tất cả các dòng trong bảng đều chứa text trạng thái mong đợi.
     * @param expectedStatusText Text trạng thái cần kiểm tra (ví dụ: "Sắp diễn ra")
     */
    public void assertAllRowsContainStatusText(String expectedStatusText) {
        List<WebElement> rows = driver.findElements(sessionRows);
        Assert.assertTrue(rows.size() > 0, "Không có dòng ca thi nào để kiểm tra trạng thái.");

        for (WebElement row : rows) {
            String rowText = row.getText();
            Assert.assertTrue(
                    rowText.contains(expectedStatusText),
                    "Có dòng không chứa trạng thái " + expectedStatusText + ". Row text: " + rowText
            );
        }
    }

    /**
     * Kiểm tra ca thi vừa tạo có hiển thị trong danh sách sau khi tìm kiếm hay không.
     * @param sessionName Tên ca thi cần kiểm tra
     */
    public void assertCreatedSessionDisplayed(String sessionName) {
        searchSession(sessionName);

        Assert.assertTrue(
                hasAnyRows(),
                "Không tìm thấy ca thi vừa tạo trong danh sách: " + sessionName
        );

        Assert.assertTrue(
                getFirstSessionName().contains(sessionName),
                "Dòng đầu tiên không phải ca thi vừa tạo. Expected: " + sessionName + ", Actual: " + getFirstSessionName()
        );
    }

    /**
     * Click vào dòng ca thi đầu tiên trong danh sách để mở chi tiết.
     */
    public void clickFirstSessionRow() {
        By firstRow = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[2]/table/tbody/tr[1]");

        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(firstRow));
        clickByJs(row);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/lecturer/exam-sessions/"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(.),'Chi tiết ca thi')]"))
        ));
    }

    /**
     * Tìm kiếm ca thi theo tên và mở chi tiết ca thi đó.
     * @param sessionName Tên ca thi cần tìm và mở
     */
    public void searchAndOpenSessionByName(String sessionName) {
        searchSession(sessionName);

        Assert.assertTrue(
                hasAnyRows(),
                "Không tìm thấy ca thi vừa tạo sau khi search: " + sessionName
        );

        Assert.assertTrue(
                getFirstSessionName().contains(sessionName),
                "Kết quả đầu tiên không đúng ca thi vừa tạo. Expected: " + sessionName
        );

        clickFirstSessionRow();
    }

    /**
     * Kiểm tra ca thi không còn hiển thị trong danh sách sau khi đã xóa.
     * @param sessionName Tên ca thi đã xóa
     */
    public void assertSessionNotDisplayedAfterSearch(String sessionName) {
        searchSession(sessionName);

        Assert.assertEquals(
                getRowCount(),
                0,
                "Ca thi đã xóa nhưng vẫn còn hiển thị trong danh sách: " + sessionName
        );
    }
}