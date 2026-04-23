package com.vandapai.pages.question;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class QuestionManagementPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public QuestionManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // TC001, TC002
    private final By subjectDropdown = By.id("subjectSelect");
    private final By createNewBankBtn = By.id("goDetailBtn");
    private final By saveButton = By.id("saveBankBtnWrapper");
    private final By addQuestionBtn = By.id("openAddQuestionModalBtn");
    private final By bankListContainer = By.id("bankListContainer");

    // TC001
    private final By questionBankSection = By.xpath("//*[@id='qm2App']/div[2]/div[2]/div[2]/div[1]");
    private final By aiConfigSection = By.xpath("//*[@id='qm2App']/div[2]/div[2]/div[1]/div[2]");

    // TC002
    private final By addDocumentBtn = By.cssSelector("button[title='Thêm tài liệu']");
    private final By uploadFileInput = By.id("uploadFileInput");
    private final By uploadSuccessToast = By.cssSelector("span.global-toast-message");
    private final By closeUploadModalBtn = By.cssSelector("#uploadModal .qm2-dialog-close");
    private final By firstDocumentCheckbox = By.cssSelector("input.doc-checkbox");

    // TC001, TC002
    public void selectSubjectByVisibleText(String subjectText) {
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(subjectDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(subjectText);

        wait.until(ExpectedConditions.visibilityOfElementLocated(bankListContainer));
    }

    // TC001, TC002
    public void clickCreateNewBank() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(createNewBankBtn));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});", button
        );

        wait.until(ExpectedConditions.visibilityOf(button));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
    }

    // TC001
    public boolean isQuestionBankSectionDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(questionBankSection)).isDisplayed();
    }

    // TC001
    public boolean isAIConfigSectionDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(aiConfigSection)).isDisplayed();
    }

    // TC001
    public boolean isSaveButtonDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(saveButton)).isDisplayed();
    }

    // TC001
    public boolean isAddQuestionButtonDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(addQuestionBtn)).isDisplayed();
    }

    // TC002
    public void clickAddDocumentButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addDocumentBtn));
        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    // TC002
    public void uploadPdfFile(String filePath) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(uploadFileInput));
        input.sendKeys(filePath);
    }

    // TC002
    public void waitForUploadSuccessToast() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(uploadSuccessToast));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                uploadSuccessToast, "Upload tài liệu thành công"
        ));
    }

    // TC002
    public boolean isUploadSuccessToastDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(uploadSuccessToast)).isDisplayed();
    }

    // TC002
    public void closeUploadModal() {
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(closeUploadModalBtn));
        try {
            closeBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
        }
    }

    // TC002
    public void selectUploadedDocumentCheckbox() {
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(firstDocumentCheckbox));
        if (!checkbox.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
        }
    }

    // TC002
    public boolean isDocumentCheckboxSelected() {
        WebElement checkbox = wait.until(ExpectedConditions.visibilityOfElementLocated(firstDocumentCheckbox));
        return checkbox.isSelected();
    }
}