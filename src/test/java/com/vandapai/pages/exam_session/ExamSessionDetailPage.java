package com.vandapai.pages.exam_session;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class ExamSessionDetailPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By detailTitle = By.xpath("//*[contains(normalize-space(.),'Chi tiết ca thi')]");
    private final By detailStatusBadge = By.id("detailStatusBadge");
    private final By viewCommonListButton = By.xpath("//a[contains(normalize-space(.),'Xem danh sách chung')]");
    private final By roomTable = By.cssSelector("table.detail-table");

    public ExamSessionDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(detailTitle));
        wait.until(ExpectedConditions.visibilityOfElementLocated(detailStatusBadge));
    }

    public void assertDetailDisplayedCorrectly(String sessionName) {
        waitUntilLoaded();

        Assert.assertTrue(
                driver.getPageSource().contains(sessionName),
                "Màn hình chi tiết không hiển thị đúng tên ca thi vừa tạo: " + sessionName
        );

        Assert.assertTrue(
                driver.findElement(detailStatusBadge).isDisplayed(),
                "Không thấy badge trạng thái ca thi ở màn hình chi tiết."
        );

        Assert.assertTrue(
                driver.findElement(roomTable).isDisplayed(),
                "Không thấy bảng danh sách phòng thi ở màn hình chi tiết."
        );
    }

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

    private final By editButton = By.cssSelector("a.btn-edit");
    private final By deleteButton = By.id("openDeleteModalButton");
    private final By deleteModal = By.id("customDeleteModal");
    private final By confirmDeleteButton = By.id("confirmDeleteBtn");

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

    public void deleteSession() {
        WebElement delete = wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
        clickByJs(delete);

        wait.until(ExpectedConditions.visibilityOfElementLocated(deleteModal));

        WebElement confirm = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton));
        clickByJs(confirm);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/lecturer/exam-sessions"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'Danh sách ca thi')]")
                )
        ));
    }

    private void clickByJs(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}