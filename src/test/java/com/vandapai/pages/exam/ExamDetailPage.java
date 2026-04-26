package com.vandapai.pages.exam;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ExamDetailPage extends BasePage {

    public ExamDetailPage(WebDriver driver) {
        super(driver);
    }

    public boolean isExamSetDetailPageDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            String currentUrl = driver.getCurrentUrl();
            String bodyText = driver.findElement(By.tagName("body")).getText();

            System.out.println("DETAIL PAGE URL = " + currentUrl);
            System.out.println("DETAIL PAGE TEXT = " + bodyText);

            boolean isDetailUrl =
                    currentUrl.contains("/lecturer/exam-codes/")
                            && !currentUrl.contains("/create");

            boolean hasDetailContent =
                    bodyText.contains("Mã đề")
                            || bodyText.contains("Danh sách mã đề")
                            || bodyText.contains("Câu hỏi")
                            || bodyText.contains("Chi tiết")
                            || bodyText.contains("Tổng điểm")
                            || bodyText.contains("Trạng thái");

            return isDetailUrl && hasDetailContent;
        });
    }

    public boolean isExamSetDetailDisplayed(String examSetName) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            String currentUrl = driver.getCurrentUrl();
            String bodyText = driver.findElement(By.tagName("body")).getText();

            System.out.println("DETAIL URL = " + currentUrl);
            System.out.println("DETAIL TEXT = " + bodyText);

            boolean hasExamSetName = bodyText.contains(examSetName);

            boolean hasDetailInfo =
                    bodyText.contains("Mã đề")
                            || bodyText.contains("Danh sách mã đề")
                            || bodyText.contains("Câu hỏi")
                            || bodyText.contains("Chi tiết");

            return hasExamSetName && hasDetailInfo;
        });
    }
}