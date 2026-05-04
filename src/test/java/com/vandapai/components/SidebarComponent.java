package com.vandapai.components;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * SidebarComponent: Xử lý các thao tác trên thanh sidebar menu.
 * Kế thừa BasePage để dùng chung driver, wait, click, scroll,...
 */
public class SidebarComponent extends BasePage {

    public SidebarComponent(WebDriver driver) {
        super(driver);
    }

    /**
     * Mở trang Quản lý câu hỏi (Question Management) từ sidebar.
     * - Click vào menu "Câu hỏi & Đề thi" (nút mở dropdown).
     * - Đợi dropdown hiển thị.
     * - Click vào link "Quản lý câu hỏi" (nếu có href thì dùng driver.get, nếu không thì click).
     * - Đợi URL chứa "/lecturer/questions".
     */
    public void openQuestionManagement() {
        // Locator menu chính "Câu hỏi & Đề thi"
        By questionAndExamMenu = By.xpath("//*[@id='sidebar']/nav/div/button");
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(questionAndExamMenu));

        // Click menu (nếu click thường lỗi thì dùng JS click)
        try {
            menu.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
        }

        // Đợi dropdown hiển thị
        By dropdown = By.id("qna-dropdown");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdown));

        // Locator link "Quản lý câu hỏi"
        By questionManagementLink = By.xpath("//*[@id='qna-dropdown']//a[contains(.,'Quản lý câu hỏi')]");
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(questionManagementLink));

        // Lấy href, nếu có thì dùng driver.get, nếu không thì click thông thường
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

        // Đợi URL chuyển đúng trang Quản lý câu hỏi
        wait.until(ExpectedConditions.urlContains("/lecturer/questions"));
    }

    /**
     * Mở trang Quản lý đề thi (Exam Management) từ sidebar.
     * - Tương tự openQuestionManagement nhưng click vào link "Quản lý đề thi".
     * - Đợi URL chứa "/lecturer/exam-codes".
     */
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

    /**
     * Mở trang Quản lý ca thi (Exam Session Management) từ sidebar.
     * - Click trực tiếp vào menu "Quản lý ca thi" (a[2] trong sidebar).
     * - Không cần mở dropdown như hai menu trên.
     */
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