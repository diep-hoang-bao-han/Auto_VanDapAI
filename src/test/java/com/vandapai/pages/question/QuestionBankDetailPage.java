package com.vandapai.pages.question;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class QuestionBankDetailPage extends BasePage {

    public QuestionBankDetailPage(WebDriver driver) {
        super(driver);
    }

    public boolean isQuestionBankDetailDisplayed(String bankName) {
        WebDriverWait detailWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return detailWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                boolean hasBankName = bodyText.contains(bankName);

                boolean hasDetailContent =
                        bodyText.contains("Câu hỏi")
                                || bodyText.contains("Danh sách câu hỏi")
                                || bodyText.contains("Nội dung câu hỏi")
                                || bodyText.contains("Mức độ")
                                || bodyText.contains("Điểm");

                return hasBankName && hasDetailContent;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean hasAtLeastOneQuestionInQuestionBankDetail() {
        WebDriverWait detailWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return detailWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                boolean hasQuestionContent =
                        bodyText.contains("Câu hỏi")
                                || bodyText.contains("Nội dung câu hỏi")
                                || bodyText.contains("Dễ")
                                || bodyText.contains("Trung bình")
                                || bodyText.contains("Khó");

                boolean notEmpty =
                        !bodyText.contains("Chưa có câu hỏi")
                                && !bodyText.contains("Không có câu hỏi");

                return hasQuestionContent && notEmpty;
            } catch (Exception e) {
                return false;
            }
        });
    }
}