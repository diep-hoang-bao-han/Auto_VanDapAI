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

public class ExamSessionManagementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By menuExamSession = By.xpath("//*[@id='sidebar']/nav/a[2]");
    private final By pageTitle = By.xpath("//*[@id='page-scroll-container']/div[3]/div[1]");
    private final By createSessionButton = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[1]/a");
    private final By searchInput = By.xpath("//*[@id='sessionFilterForm']/div/input");
    private final By statusDropdown = By.xpath("//*[@id='sessionFilterForm']/select[4]");

    private final By sessionRows = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[2]/table/tbody/tr");
    private final By firstSessionName = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[2]/table/tbody/tr[1]/td[2]");
    private final By firstSessionStatus = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[2]/table/tbody/tr[1]/td[8]/div");

    public ExamSessionManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    public void goToExamSessionPage() {
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(menuExamSession));
        clickByJs(menu);
        waitUntilLoaded();
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        wait.until(ExpectedConditions.visibilityOfElementLocated(createSessionButton));
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
    }

    public boolean isAtExamSessionManagementPage() {
        try {
            String text = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText();
            return text.contains("Ca thi") || text.contains("Danh sách ca thi") || text.contains("Quản lý ca thi");
        } catch (Exception e) {
            return false;
        }
    }

    public void assertMainElementsDisplayed() {
        Assert.assertTrue(isDisplayed(pageTitle), "Không thấy tiêu đề Danh sách ca thi.");
        Assert.assertTrue(isDisplayed(createSessionButton), "Không thấy nút + Tạo ca thi mới.");
        Assert.assertTrue(isDisplayed(searchInput), "Không thấy ô tìm kiếm ca thi.");
        Assert.assertTrue(isDisplayed(statusDropdown), "Không thấy dropdown Trạng thái.");
    }

    public void clickCreateSessionButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(createSessionButton));
        clickByJs(button);
    }

    public void searchSession(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(keyword);
        input.sendKeys(Keys.ENTER);
        sleep(1000);
    }

    public boolean hasAnyRows() {
        return driver.findElements(sessionRows).size() > 0;
    }

    public int getRowCount() {
        return driver.findElements(sessionRows).size();
    }

    public String getFirstSessionName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstSessionName)).getText().trim();
    }

    public String getFirstSessionStatusText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstSessionStatus)).getText().trim();
    }

    public void assertSearchHasResult(String keyword) {
        Assert.assertTrue(hasAnyRows(), "Không có kết quả tìm kiếm với từ khóa: " + keyword);

        String firstName = getFirstSessionName().toLowerCase();
        Assert.assertTrue(
                firstName.contains(keyword.toLowerCase()),
                "Tên ca thi đầu tiên không chứa từ khóa. Actual: " + firstName
        );
    }

    public void assertSearchNoResult() {
        Assert.assertEquals(
                getRowCount(),
                0,
                "Tìm kiếm keyword không tồn tại nhưng bảng vẫn còn dữ liệu."
        );
    }

    public void selectStatusByVisibleText(String visibleText) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(visibleText);
        sleep(1200);
    }

    public void selectStatusByValue(String value) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown));
        Select select = new Select(dropdown);
        select.selectByValue(value);
        sleep(1200);
    }

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

    private boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void clickByJs(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickFirstSessionRow() {
        By firstRow = By.xpath("//*[@id='page-scroll-container']/div[3]/div[2]/div[2]/div[2]/table/tbody/tr[1]");

        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(firstRow));
        clickByJs(row);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/lecturer/exam-sessions/"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(.),'Chi tiết ca thi')]"))
        ));
    }

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

    public void assertSessionNotDisplayedAfterSearch(String sessionName) {
        searchSession(sessionName);

        Assert.assertEquals(
                getRowCount(),
                0,
                "Ca thi đã xóa nhưng vẫn còn hiển thị trong danh sách: " + sessionName
        );
    }
}