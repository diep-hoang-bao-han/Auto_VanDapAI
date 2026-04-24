package com.vandapai.pages.question;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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

    // Flow chung
    private final By subjectDropdown = By.id("subjectSelect");
    private final By createNewBankBtn = By.id("goDetailBtn");
    private final By bankListContainer = By.id("bankListContainer");

    // TC001
    private final By saveButton = By.id("saveBankBtnWrapper");
    private final By addQuestionBtn = By.id("openAddQuestionModalBtn");
    private final By questionBankSection = By.xpath("//*[@id='qm2App']/div[2]/div[2]/div[2]/div[1]");
    private final By aiConfigSection = By.xpath("//*[@id='qm2App']/div[2]/div[2]/div[1]/div[2]");

    // TC002, TC003, TC005
    private final By addDocumentBtn = By.cssSelector("button[title='Thêm tài liệu']");
    private final By uploadFileInput = By.id("uploadFileInput");
    private final By uploadToast = By.cssSelector("span.global-toast-message");
    private final By emptyDocumentMessage = By.cssSelector(".qm2-empty");

    // TC004, TC005
    private final By saveBankButton = By.id("saveBankBtnWrapper");
    private final By warningToastMessage = By.cssSelector("span.global-toast-message");

    // TC005
    private final By closeUploadModalBtn = By.cssSelector(".qm2-dialog-close");
    private final By documentCheckbox = By.cssSelector("input.doc-checkbox");
    private final By generateQuestionBtn = By.id("generateAiBtn");
    private final By toastMessage = By.cssSelector("span.global-toast-message");
    private final By questionCountRange = By.id("questionCountRange");
    private final By allLevelChip = By.cssSelector("button.qm2-chip[data-level='ALL']");

    public void selectSubjectByVisibleText(String subjectText) {
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(subjectDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(subjectText);
        wait.until(ExpectedConditions.visibilityOfElementLocated(bankListContainer));
    }

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

    public boolean isAIConfigSectionDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(aiConfigSection)).isDisplayed();
    }

    public boolean isSaveButtonDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(saveButton)).isDisplayed();
    }

    public boolean isAddQuestionButtonDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(addQuestionBtn)).isDisplayed();
    }

    // TC002, TC003, TC005
    public void clickAddDocumentButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addDocumentBtn));
        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    public void uploadFile(String filePath) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(uploadFileInput));
        input.sendKeys(filePath);
    }

    public void waitForUploadSuccessToast() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(uploadToast));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                uploadToast, "Upload tài liệu thành công"
        ));
    }

    public boolean isUploadErrorToastDisplayed(String expectedMessage) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(uploadToast));
        String actualMessage = driver.findElement(uploadToast).getText().trim();
        return actualMessage.contains(expectedMessage);
    }

    public boolean isEmptyDocumentMessageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emptyDocumentMessage)).isDisplayed();
    }

    // TC004, TC005
    public void clickSaveBankButton() {
        ((JavascriptExecutor) driver).executeScript("handleSaveBank();");
    }

    public boolean isWarningToastDisplayed(String expectedMessage) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        return shortWait.until(driver -> {
            try {
                Object textObj = ((JavascriptExecutor) driver).executeScript(
                        "const el = document.querySelector('span.global-toast-message');" +
                                "return el ? el.innerText.trim() : '';"
                );

                String actualText = textObj == null ? "" : textObj.toString().trim();
                System.out.println("ACTUAL TOAST = [" + actualText + "]");

                return !actualText.isEmpty() && actualText.contains(expectedMessage);
            } catch (Exception e) {
                return false;
            }
        });
    }

    // TC005
    public void closeUploadPopup() {
        WebElement closeBtn = wait.until(ExpectedConditions.presenceOfElementLocated(closeUploadModalBtn));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});", closeBtn
        );

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
        } catch (Exception e) {
            try {
                closeBtn.click();
            } catch (Exception ex) {
                throw ex;
            }
        }

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void selectUploadedDocumentCheckbox() {
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(documentCheckbox));
        if (!checkbox.isSelected()) {
            try {
                checkbox.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
            }
        }
    }

    public void setQuestionCount(String value) {
        WebElement range = wait.until(ExpectedConditions.presenceOfElementLocated(questionCountRange));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                range, value
        );
    }

    public void clickAllLevelChip() {
        WebElement allChip = wait.until(ExpectedConditions.elementToBeClickable(allLevelChip));
        try {
            allChip.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", allChip);
        }
    }

    public boolean isGenerateQuestionButtonEnabled() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(generateQuestionBtn));
        return button.isEnabled();
    }

    public void clickGenerateQuestionButton() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(generateQuestionBtn));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});", button
        );

        wait.until(ExpectedConditions.visibilityOf(button));

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    public boolean isToastMessageDisplayed(String expectedMessage) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(60));

        return shortWait.until(driver -> {
            try {
                WebElement toast = driver.findElement(toastMessage);
                String actualText = toast.getText().trim();
                System.out.println("ACTUAL TOAST = [" + actualText + "]");
                return !actualText.isEmpty() && actualText.contains(expectedMessage);
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean waitForGenerateQuestionSuccessToast() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(180));

        return longWait.until(driver -> {
            try {
                WebElement toast = driver.findElement(toastMessage);
                String actualText = toast.getText().trim();
                System.out.println("GENERATE TOAST = [" + actualText + "]");

                return !actualText.isEmpty()
                        && actualText.contains("Tạo câu hỏi thành công");
            } catch (Exception e) {
                return false;
            }
        });
    }
}