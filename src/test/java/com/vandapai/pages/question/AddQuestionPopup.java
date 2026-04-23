package com.vandapai.pages.question;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AddQuestionPopup {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public AddQuestionPopup(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private final By popupTitle = By.xpath("//*[contains(normalize-space(),'Thêm câu hỏi')]");
    private final By easyLevelBtn = By.xpath("//button[normalize-space()='Dễ']");
    private final By mediumLevelBtn = By.xpath("//button[normalize-space()='Trung bình']");
    private final By hardLevelBtn = By.xpath("//button[normalize-space()='Khó']");
    private final By questionContentTextarea = By.xpath("//textarea | //*[@placeholder='Nhập câu hỏi vào đây...']");
    private final By saveBtn = By.xpath("//button[normalize-space()='Lưu']");
    private final By cancelBtn = By.xpath("//button[contains(.,'Hủy')]");

    public boolean isDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(popupTitle)).isDisplayed();
    }

    public void selectDifficulty(String level) {
        switch (level.toLowerCase()) {
            case "dễ":
                wait.until(ExpectedConditions.elementToBeClickable(easyLevelBtn)).click();
                break;
            case "trung bình":
                wait.until(ExpectedConditions.elementToBeClickable(mediumLevelBtn)).click();
                break;
            case "khó":
                wait.until(ExpectedConditions.elementToBeClickable(hardLevelBtn)).click();
                break;
            default:
                throw new IllegalArgumentException("Mức độ không hợp lệ: " + level);
        }
    }

    public void enterQuestionContent(String content) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(questionContentTextarea)).sendKeys(content);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
    }
}