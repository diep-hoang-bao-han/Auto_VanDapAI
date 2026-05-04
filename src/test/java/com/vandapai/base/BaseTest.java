package com.vandapai.base;

import com.vandapai.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Lớp BaseTest: Chứa các thiết lập chung cho tất cả các test case.
 * Cấu hình WebDriver, khởi tạo driver, zoom page, và dọn dẹp sau mỗi test.
 */
public class BaseTest {
    protected WebDriver driver;

    /**
     * Phương thức setUp: Chạy trước mỗi @Test.
     * - Setup ChromeDriver bằng WebDriverManager.
     * - Cấu hình ChromeOptions (zoom 75%, start maximized).
     * - Khởi tạo driver và implicit wait 10s.
     * - Điều hướng đến base.url từ config.properties.
     * - Zoom page về 75% bằng Javascript.
     */
    @BeforeMethod
    public void setUp() {
        // Tự động tải và cài đặt ChromeDriver phù hợp
        WebDriverManager.chromedriver().setup();

        // Cấu hình ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--force-device-scale-factor=0.75");
        options.addArguments("--high-dpi-support=0.75");
        options.addArguments("--start-maximized");

        // Khởi tạo driver
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Mở URL từ config
        driver.get(ConfigReader.get("base.url"));

        // Zoom page về 75% thêm lần nữa cho chắc
        ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='75%'");
    }

    /**
     * Phương thức tearDown: Chạy sau mỗi @Test.
     * - Đóng driver nếu driver đã được khởi tạo.
     */
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}