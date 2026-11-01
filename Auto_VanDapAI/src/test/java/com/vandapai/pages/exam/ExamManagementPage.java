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

/**
 * ExamManagementPage: Xử lý các thao tác trên màn hình Quản lý đề thi (Danh sách bộ đề).
 * Bao gồm: tạo bộ đề mới, tìm kiếm, mở chi tiết bộ đề, lấy số lượng mã đề.
 */
public class ExamManagementPage extends BasePage {

    // =========================
    // CONSTRUCTOR
    // =========================
    public ExamManagementPage(WebDriver driver) {
        super(driver);
    }

    // =========================
    // LOCATORS
    // =========================
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

    private final By firstExamSetCodeCountCell = By.xpath("//*[@id='examSetTableBody']/tr[1]/td[4]");
    private final By firstExamSetSearchResultRow = By.xpath("//*[@id='examSetTableBody']/tr[1]");

    // =========================
    // UTILITY METHODS
    // =========================

    /**
     * Tạo tên bộ đề duy nhất bằng cách thêm timestamp.
     * @param prefix Tiền tố (ví dụ "AUTO_CREATE_EXAM")
     * @return Tên bộ đề hoàn chỉnh (prefix_timestamp)
     */
    public String generateExamSetName(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }

    // =========================
    // NAVIGATION METHODS
    // =========================

    /**
     * Click nút "Tạo đề thi" để điều hướng sang màn hình Tạo đề thi.
     * - Nếu link có href thì dùng driver.get, nếu không thì click thông thường.
     * - Đợi URL chứa "/lecturer/exam-codes/create".
     */
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

    /**
     * Mở trang danh sách bộ đề (không phải chi tiết).
     * - Xây dựng base URL từ currentUrl.
     * - Điều hướng đến "/lecturer/exam-codes/".
     * - Đợi trang load đúng (chứa "Danh sách bộ đề" hoặc "Tạo đề thi").
     */
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

    /**
     * Mở bộ đề đầu tiên trong danh sách (click vào hàng đầu tiên của bảng).
     */
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

    // =========================
    // SEARCH METHODS
    // =========================

    /**
     * Tìm kiếm bộ đề theo từ khóa.
     * - Nhập keyword vào ô tìm kiếm, sau đó nhấn Enter.
     */
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

    /**
     * Kiểm tra xem bộ đề có hiển thị trong danh sách kết quả tìm kiếm hay không.
     * @param keyword Từ khóa cần kiểm tra (thường là tên bộ đề)
     */
    public boolean isExamSetDisplayedInList(String keyword) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            String bodyText = driver.findElement(By.tagName("body")).getText();

            System.out.println("EXAM SET LIST TEXT = " + bodyText);

            return bodyText.contains(keyword);
        });
    }

    /**
     * Mở chi tiết bộ đề bằng cách click vào element chứa tên bộ đề.
     */
    public void openExamSetDetailByName(String examSetName) {
        WebElement examSetElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(normalize-space(.),'" + examSetName + "')]")
        ));

        scrollToElement(examSetElement);
        clickElement(examSetElement);

        sleep(1500);
    }

    /**
     * Mở kết quả tìm kiếm đầu tiên (sau khi đã search).
     */
    public void openFirstExamSetSearchResult() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement firstRow = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(firstExamSetSearchResultRow)
        );

        scrollToElement(firstRow);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(firstRow));
            firstRow.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstRow);
        }

        sleep(1500);
    }

    // =========================
    // VERIFICATION METHODS - CODE COUNT
    // =========================

    /**
     * Lấy số lượng mã đề của bộ đề đầu tiên trong danh sách (dạng text).
     * - Mở lại trang danh sách để đảm bảo dữ liệu mới nhất.
     */
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

    /**
     * Kiểm tra số lượng mã đề của bộ đề đầu tiên có bằng 0 hay không.
     * - Dùng cho test case xóa tất cả mã đề.
     */
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