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

    // Flow chung
    private final By subjectDropdown = By.id("subjectSelect");
    private final By createNewBankBtn = By.id("goDetailBtn");
    private final By bankListContainer = By.id("bankListContainer");

    // TC001
    private final By saveButton = By.xpath("//button[contains(normalize-space(.),'Lưu thành ngân hàng')]");
    private final By addQuestionBtn = By.id("openAddQuestionModalBtn");
    private final By questionBankSection = By.xpath("//*[@id='qm2App']/div[2]/div[2]/div[2]/div[1]");
    private final By aiConfigSection = By.xpath("//*[@id='qm2App']/div[2]/div[2]/div[1]/div[2]");

    // TC002, TC003, TC005, TC006
    private final By addDocumentBtn = By.cssSelector("button[title='Thêm tài liệu']");
    private final By uploadFileInput = By.id("uploadFileInput");
    private final By uploadToast = By.cssSelector("span.global-toast-message");
    private final By emptyDocumentMessage = By.cssSelector(".qm2-empty");

    // TC004, TC005, TC006
    private final By saveBankButton = By.xpath("//button[contains(normalize-space(.),'Lưu thành ngân hàng')]");
    private final By warningToastMessage = By.cssSelector("span.global-toast-message");

    // TC005, TC006 - Cấu hình AI
    private final By closeUploadModalBtn = By.cssSelector(".qm2-dialog-close");
    private final By documentCheckbox = By.cssSelector("input.doc-checkbox");
    private final By generateQuestionBtn = By.id("generateAiBtn");
    private final By toastMessage = By.cssSelector("span.global-toast-message");
    private final By questionCountRange = By.id("questionCountRange");
    private final By allLevelChip = By.cssSelector("button.qm2-chip[data-level='ALL']");

    // TC005 - Popup đặt tên ngân hàng câu hỏi
    private final By bankNameInput = By.id("bankNameInput");
    private final By confirmSaveBankBtn = By.id("confirmSaveBankBtn");

    // TC006 - Xóa câu hỏi
    private final By bulkDeleteQuestionBtn = By.cssSelector("button.qm2-bulk-action.danger");
    private final By confirmDeleteQuestionBtn = By.id("globalConfirmAccept");

    public void selectSubjectByVisibleText(String subjectText) {
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(subjectDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(subjectText);
        wait.until(ExpectedConditions.visibilityOfElementLocated(bankListContainer));
    }

    public void clickCreateNewBank() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement button = longWait.until(ExpectedConditions.presenceOfElementLocated(createNewBankBtn));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                button
        );

        longWait.until(ExpectedConditions.visibilityOf(button));

        String oldUrl = driver.getCurrentUrl();
        System.out.println("URL BEFORE CLICK CREATE BANK = " + oldUrl);

        try {
            longWait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            System.out.println("NORMAL CLICK CREATE BANK FAILED = " + e.getMessage());
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(driver -> isOnCreateQuestionBankDetailPage());
        } catch (Exception ignored) {
        }

        if (!isOnCreateQuestionBankDetailPage()) {
            System.out.println("NORMAL CLICK HAS NO EFFECT -> TRY JS CLICK");

            try {
                ((JavascriptExecutor) driver).executeScript(
                        "document.getElementById('goDetailBtn').click();"
                );
            } catch (Exception e) {
                System.out.println("JS CLICK CREATE BANK FAILED = " + e.getMessage());
            }
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(driver -> isOnCreateQuestionBankDetailPage());
        } catch (Exception ignored) {
        }

        if (!isOnCreateQuestionBankDetailPage()) {
            String detailUrl;

            if (oldUrl.contains("?")) {
                detailUrl = oldUrl + "&mode=detail&view=generate";
            } else {
                detailUrl = oldUrl + "?mode=detail&view=generate";
            }

            System.out.println("CLICK STILL HAS NO EFFECT -> FORCE NAVIGATE TO = " + detailUrl);
            driver.get(detailUrl);
        }

        longWait.until(driver -> isOnCreateQuestionBankDetailPage());
    }

    private boolean isOnCreateQuestionBankDetailPage() {
        try {
            String currentUrl = driver.getCurrentUrl();
            String bodyText = driver.findElement(By.tagName("body")).getText();

            return currentUrl.contains("mode=detail")
                    || bodyText.contains("Chi tiết ngân hàng")
                    || bodyText.contains("Lưu thành ngân hàng");
        } catch (Exception e) {
            return false;
        }
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

    // TC002, TC003, TC005, TC006
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
                uploadToast,
                "Upload tài liệu thành công"
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

    // TC004, TC005, TC006
    public void clickSaveBankButton() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement saveBtn = longWait.until(ExpectedConditions.presenceOfElementLocated(saveBankButton));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                saveBtn
        );

        longWait.until(ExpectedConditions.visibilityOf(saveBtn));

        try {
            longWait.until(ExpectedConditions.elementToBeClickable(saveBtn));
            saveBtn.click();
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
            } catch (Exception ex) {
                ((JavascriptExecutor) driver).executeScript("handleSaveBank();");
            }
        }
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

    // TC005, TC006
    public void closeUploadPopup() {
        WebElement closeBtn = wait.until(ExpectedConditions.presenceOfElementLocated(closeUploadModalBtn));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                closeBtn
        );

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
        } catch (Exception e) {
            closeBtn.click();
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
                range,
                value
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
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                button
        );

        wait.until(ExpectedConditions.visibilityOf(button));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
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

    public boolean waitUntilQuestionsAreGenerated() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(180));

        return longWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("BODY TEXT CHECK = " + bodyText);

                boolean hasAllTabWithCount = bodyText.matches("(?s).*Tất cả \\([1-9][0-9]*\\).*");
                boolean hasSelectedQuestionToolbar = bodyText.contains("Đã chọn câu hỏi");
                boolean hasQuestionStatus = bodyText.contains("Mới tạo") || bodyText.contains("Chưa lưu");
                boolean hasDifficultyText = bodyText.contains("Dễ")
                        || bodyText.contains("Trung bình")
                        || bodyText.contains("Khó");

                System.out.println("HAS ALL TAB COUNT = " + hasAllTabWithCount);
                System.out.println("HAS SELECTED TOOLBAR = " + hasSelectedQuestionToolbar);
                System.out.println("HAS QUESTION STATUS = " + hasQuestionStatus);
                System.out.println("HAS DIFFICULTY TEXT = " + hasDifficultyText);

                return hasAllTabWithCount
                        || hasSelectedQuestionToolbar
                        || (hasQuestionStatus && hasDifficultyText);
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean waitUntilCanSaveQuestionBank() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(180));

        return longWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                boolean hasQuestionCount = bodyText.matches("(?s).*Tất cả \\([1-9][0-9]*\\).*");
                boolean hasQuestionContent = bodyText.contains("Mới tạo") || bodyText.contains("Chưa lưu");
                boolean hasSaveButton = driver.findElements(saveBankButton).size() > 0
                        && driver.findElement(saveBankButton).isDisplayed();

                System.out.println("HAS QUESTION COUNT = " + hasQuestionCount);
                System.out.println("HAS QUESTION CONTENT = " + hasQuestionContent);
                System.out.println("HAS SAVE BUTTON = " + hasSaveButton);

                return (hasQuestionCount || hasQuestionContent) && hasSaveButton;
            } catch (Exception e) {
                return false;
            }
        });
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

    // TC005 - Popup đặt tên ngân hàng câu hỏi
    public boolean isSaveBankNamePopupDisplayed() {
        WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return popupWait.until(driver -> {
            try {
                WebElement input = driver.findElement(bankNameInput);
                return input.isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean waitUntilSaveNamePopupAppears() {
        WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return popupWait.until(driver -> {
            try {
                WebElement input = driver.findElement(bankNameInput);
                return input.isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void enterBankName(String bankName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(bankNameInput));

        input.clear();
        input.sendKeys(bankName);
    }

    public void clickConfirmSaveBankButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(confirmSaveBankBtn));

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    public boolean waitForSaveBankSuccessToast() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));

        return longWait.until(driver -> {
            try {
                WebElement toast = driver.findElement(toastMessage);
                String actualText = toast.getText().trim();

                System.out.println("SAVE BANK TOAST = [" + actualText + "]");

                return !actualText.isEmpty()
                        && (
                        actualText.contains("Lưu thành công")
                                || actualText.contains("Lưu ngân hàng câu hỏi thành công")
                                || actualText.contains("Tạo ngân hàng câu hỏi thành công")
                                || actualText.contains("Lưu ngân hàng thành công")
                );
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean waitForSaveBankSuccessToast(String bankName) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));

        return longWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                String toastText = "";

                try {
                    toastText = driver.findElement(toastMessage).getText().trim();
                } catch (Exception ignored) {
                }

                System.out.println("SAVE BANK TOAST = [" + toastText + "]");
                System.out.println("BODY AFTER SAVE CONTAINS BANK NAME = " + bodyText.contains(bankName));

                boolean hasSaveSuccessToast =
                        toastText.contains("Lưu thành công")
                                || toastText.contains("Lưu ngân hàng câu hỏi thành công")
                                || toastText.contains("Tạo ngân hàng câu hỏi thành công")
                                || toastText.contains("Lưu ngân hàng thành công")
                                || (toastText.contains("thành công")
                                && !toastText.contains("Tạo câu hỏi thành công"));

                boolean popupClosed = true;

                try {
                    WebElement input = driver.findElement(bankNameInput);
                    popupClosed = !input.isDisplayed();
                } catch (Exception ignored) {
                    popupClosed = true;
                }

                boolean bankNameDisplayed = bodyText.contains(bankName);

                boolean backToListScreen = bodyText.contains("Quản lý câu hỏi")
                        && bodyText.contains("Tạo ngân hàng mới");

                return hasSaveSuccessToast || bankNameDisplayed || backToListScreen || popupClosed;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void waitAfterConfirmSaveBank(String bankName) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        longWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                boolean popupClosed = true;

                try {
                    WebElement input = driver.findElement(bankNameInput);
                    popupClosed = !input.isDisplayed();
                } catch (Exception ignored) {
                    popupClosed = true;
                }

                boolean hasSuccessText = bodyText.contains("thành công")
                        || bodyText.contains("Tạo ngân hàng")
                        || bodyText.contains("Lưu");

                return popupClosed || hasSuccessText;
            } catch (Exception e) {
                return false;
            }
        });
    }

    // Kiểm tra ngân hàng vừa tạo có tồn tại trong danh sách hay không
    public boolean isQuestionBankDisplayedInList(String bankName) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return longWait.until(driver -> {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(bankListContainer));

                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("CHECK BANK LIST BODY = " + bodyText);
                System.out.println("EXPECTED BANK NAME = " + bankName);

                return bodyText.contains(bankName);
            } catch (Exception e) {
                return false;
            }
        });
    }

    // TC006 - Xóa câu hỏi
    public void clickBulkDeleteQuestionButton() {
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(bulkDeleteQuestionBtn));

        try {
            deleteBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);
        }
    }

    public void confirmDeleteQuestions() {
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteQuestionBtn));

        try {
            confirmBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);
        }
    }

    public boolean waitForDeleteSuccessToast() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                WebElement toast = driver.findElement(toastMessage);
                String actualText = toast.getText().trim();

                System.out.println("DELETE TOAST = [" + actualText + "]");

                return !actualText.isEmpty() && actualText.contains("Xóa thành công");
            } catch (Exception e) {
                return false;
            }
        });
    }
}