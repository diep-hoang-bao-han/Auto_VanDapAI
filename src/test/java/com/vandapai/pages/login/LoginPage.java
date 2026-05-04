package com.vandapai.pages.login;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * LoginPage: Xử lý các thao tác đăng nhập vào hệ thống.
 * Kế thừa BasePage để dùng chung driver, wait,...
 */
public class LoginPage extends BasePage {

    // =========================
    // CONSTRUCTOR
    // =========================
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // =========================
    // LOCATORS
    // =========================
    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.xpath("//*[@id=\"page-scroll-container\"]/div[3]/div/div[2]/form/div[3]/button");

    // =========================
    // ACTION METHOD
    // =========================

    /**
     * Thực hiện đăng nhập với tên đăng nhập và mật khẩu được cung cấp.
     * - Nhập username.
     * - Nhập password.
     * - Click nút đăng nhập.
     *
     * @param username Tên đăng nhập (thường lấy từ config.properties)
     * @param password Mật khẩu tương ứng
     */
    public void login(String username, String password) {
        // Đợi ô username hiển thị và nhập username
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).sendKeys(username);

        // Nhập password
        driver.findElement(passwordInput).sendKeys(password);

        // Click nút đăng nhập
        driver.findElement(loginButton).click();
    }
}