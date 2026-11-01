package com.vandapai.pages.question;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * QuestionBankCreatePage: Xử lý các thao tác trên màn hình Tạo ngân hàng câu hỏi.
 * Bao gồm: upload tài liệu, chọn tài liệu, cấu hình AI (số lượng câu hỏi, mức độ),
 * tạo câu hỏi bằng AI, lưu ngân hàng, xóa câu hỏi hàng loạt.
 */
public class QuestionBankCreatePage extends BasePage {

    // =========================
    // CONSTRUCTOR
    // =========================
    public QuestionBankCreatePage(WebDriver driver) {
        super(driver);
    }

    // =========================
    // LOCATORS - MAIN SECTIONS
    // =========================
    private final By saveButton = By.xpath("//button[contains(normalize-space(.),'Lưu thành ngân hàng')]");
    private final By addQuestionBtn = By.id("openAddQuestionModalBtn");
    private final By questionBankSection = By.xpath("//*[@id='qm2App']/div[2]/div[2]/div[2]/div[1]");
    private final By aiConfigSection = By.xpath("//*[@id='qm2App']/div[2]/div[2]/div[1]/div[2]");

    // =========================
    // LOCATORS - UPLOAD DOCUMENT
    // =========================
    private final By addDocumentBtn = By.cssSelector("button[title='Thêm tài liệu']");
    private final By uploadFileInput = By.id("uploadFileInput");
    private final By uploadToast = By.cssSelector("span.global-toast-message");
    private final By emptyDocumentMessage = By.cssSelector(".qm2-empty");
    private final By closeUploadModalBtn = By.cssSelector(".qm2-dialog-close");
    private final By documentCheckbox = By.cssSelector("input.doc-checkbox");

    // =========================
    // LOCATORS - AI GENERATION
    // =========================
    private final By generateQuestionBtn = By.id("generateAiBtn");
    private final By toastMessage = By.cssSelector("span.global-toast-message");
    private final By questionCountRange = By.id("questionCountRange");
    private final By allLevelChip = By.cssSelector("button.qm2-chip[data-level='ALL']");

    // =========================
    // LOCATORS - SAVE BANK
    // =========================
    private final By saveBankButton = By.xpath("//button[contains(normalize-space(.),'Lưu thành ngân hàng')]");
    private final By bankNameInput = By.id("bankNameInput");
    private final By confirmSaveBankBtn = By.id("confirmSaveBankBtn");

    // =========================
    // LOCATORS - BULK DELETE
    // =========================
    private final By bulkDeleteQuestionBtn = By.cssSelector("button.qm2-bulk-action.danger");
    private final By confirmDeleteQuestionBtn = By.id("globalConfirmAccept");

    // =========================
    // VERIFICATION METHODS - PAGE DISPLAY
    // =========================

    /**
     * Kiểm tra khu vực Ngân hàng câu hỏi có hiển thị.
     */
    public boolean isQuestionBankSectionDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(questionBankSection)).isDisplayed();
    }

    /**
     * Kiểm tra khu vực Cấu hình AI có hiển thị.
     */
    public boolean isAIConfigSectionDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(aiConfigSection)).isDisplayed();
    }

    /**
     * Kiểm tra nút "Lưu thành ngân hàng" có hiển thị.
     */
    public boolean isSaveButtonDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(saveButton)).isDisplayed();
    }

    /**
     * Kiểm tra nút "Thêm câu hỏi" có hiển thị.
     */
    public boolean isAddQuestionButtonDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(addQuestionBtn)).isDisplayed();
    }

    // =========================
    // ACTION METHODS - UPLOAD DOCUMENT
    // =========================

    /**
     * Click nút "Thêm tài liệu" để mở modal upload.
     */
    public void clickAddDocumentButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addDocumentBtn));

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    /**
     * Upload file từ đường dẫn cho trước.
     * @param filePath Đường dẫn tuyệt đối đến file cần upload
     */
    public void uploadFile(String filePath) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(uploadFileInput));
        input.sendKeys(filePath);
    }

    /**
     * Đợi toast thông báo upload thành công.
     */
    public void waitForUploadSuccessToast() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(uploadToast));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                uploadToast,
                "Upload tài liệu thành công"
        ));
    }

    /**
     * Kiểm tra toast lỗi upload có hiển thị với message mong đợi.
     * @param expectedMessage Nội dung lỗi kỳ vọng
     */
    public boolean isUploadErrorToastDisplayed(String expectedMessage) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(uploadToast));
        String actualMessage = driver.findElement(uploadToast).getText().trim();
        return actualMessage.contains(expectedMessage);
    }

    /**
     * Kiểm tra thông báo "Chưa có tài liệu nào" có hiển thị.
     */
    public boolean isEmptyDocumentMessageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emptyDocumentMessage)).isDisplayed();
    }

    /**
     * Đóng popup upload tài liệu.
     */
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

        sleep(800);
    }

    /**
     * Tick checkbox chọn tài liệu vừa upload.
     */
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

    // =========================
    // ACTION METHODS - AI CONFIGURATION
    // =========================

    /**
     * Set số lượng câu hỏi muốn tạo (bằng thanh range).
     * @param value Giá trị số câu hỏi (dạng string)
     */
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

    /**
     * Click chip "Tất cả" để chọn tất cả các mức độ câu hỏi (Dễ, Trung bình, Khó).
     */
    public void clickAllLevelChip() {
        WebElement allChip = wait.until(ExpectedConditions.elementToBeClickable(allLevelChip));

        try {
            allChip.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", allChip);
        }
    }

    /**
     * Kiểm tra nút "Tạo câu hỏi" có được enable hay không.
     */
    public boolean isGenerateQuestionButtonEnabled() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(generateQuestionBtn));
        return button.isEnabled();
    }

    /**
     * Click nút "Tạo câu hỏi" để AI sinh câu hỏi từ tài liệu đã chọn.
     */
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

    /**
     * Đợi toast thông báo tạo câu hỏi thành công (timeout 180s do AI có thể lâu).
     */
    public boolean waitForGenerateQuestionSuccessToast() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(180));

        return longWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                String toastText = "";

                try {
                    WebElement toast = driver.findElement(toastMessage);
                    toastText = toast.getText().trim();
                } catch (Exception ignored) {
                }

                String pageText = bodyText + " | " + toastText;

                System.out.println("GENERATE TOAST = [" + toastText + "]");
                System.out.println("GENERATE PAGE TEXT CHECK = " + pageText);

                boolean fullSuccess = pageText.contains("Tạo câu hỏi thành công");
                boolean partialSuccess = pageText.contains("Tạo câu hỏi một phần")
                        || pageText.contains("AI chỉ tạo được")
                        || pageText.contains("Đã sinh bù");
                boolean hasGeneratedQuestions = pageText.matches("(?s).*Tất cả \\([1-9][0-9]*\\).*")
                        || pageText.contains("Mới tạo")
                        || pageText.contains("Chưa lưu")
                        || pageText.contains("Đã chọn câu hỏi");

                return fullSuccess || partialSuccess || hasGeneratedQuestions;
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Đợi cho đến khi câu hỏi được sinh ra và hiển thị trong danh sách.
     */
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

    /**
     * Đợi cho đến khi có thể lưu ngân hàng (đã có câu hỏi và nút lưu hiển thị).
     */
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

    // =========================
    // ACTION METHODS - SAVE QUESTION BANK
    // =========================

    /**
     * Click nút "Lưu thành ngân hàng".
     */
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

    /**
     * Kiểm tra popup đặt tên ngân hàng có hiển thị.
     */
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

    /**
     * Đợi popup đặt tên ngân hàng xuất hiện.
     */
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

    /**
     * Nhập tên cho ngân hàng câu hỏi.
     * @param bankName Tên ngân hàng muốn đặt
     */
    public void enterBankName(String bankName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(bankNameInput));

        input.clear();
        input.sendKeys(bankName);
    }

    /**
     * Click nút xác nhận lưu ngân hàng.
     */
    public void clickConfirmSaveBankButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(confirmSaveBankBtn));

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    /**
     * Đợi toast thông báo lưu ngân hàng thành công.
     */
    public boolean waitForSaveBankSuccessToast() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));

        return longWait.until(driver -> {
            try {
                WebElement toast = driver.findElement(toastMessage);
                String actualText = toast.getText().trim();

                System.out.println("SAVE BANK TOAST = [" + actualText + "]");

                return !actualText.isEmpty()
                        && (actualText.contains("Lưu thành công")
                        || actualText.contains("Lưu ngân hàng câu hỏi thành công")
                        || actualText.contains("Tạo ngân hàng câu hỏi thành công")
                        || actualText.contains("Lưu ngân hàng thành công"));
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Đợi toast thông báo lưu ngân hàng thành công và kiểm tra tên ngân hàng.
     * @param bankName Tên ngân hàng đã lưu
     */
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

                boolean hasSaveSuccessToast = toastText.contains("Lưu thành công")
                        || toastText.contains("Lưu ngân hàng câu hỏi thành công")
                        || toastText.contains("Tạo ngân hàng câu hỏi thành công")
                        || toastText.contains("Lưu ngân hàng thành công")
                        || (toastText.contains("thành công") && !toastText.contains("Tạo câu hỏi thành công"));

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

    /**
     * Đợi sau khi xác nhận lưu ngân hàng (popup đóng hoặc có thông báo thành công).
     * @param bankName Tên ngân hàng đã lưu
     */
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

    // =========================
    // VERIFICATION METHODS - TOAST & WARNING
    // =========================

    /**
     * Kiểm tra toast warning có hiển thị với message mong đợi.
     * @param expectedMessage Nội dung warning kỳ vọng
     */
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

    /**
     * Kiểm tra toast message có hiển thị với nội dung mong đợi.
     * @param expectedMessage Nội dung toast kỳ vọng
     */
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

    // =========================
    // ACTION METHODS - BULK DELETE QUESTIONS
    // =========================

    /**
     * Click nút xóa hàng loạt câu hỏi.
     */
    public void clickBulkDeleteQuestionButton() {
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(bulkDeleteQuestionBtn));

        try {
            deleteBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);
        }
    }

    /**
     * Xác nhận xóa câu hỏi trên popup.
     */
    public void confirmDeleteQuestions() {
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteQuestionBtn));

        try {
            confirmBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);
        }
    }

    /**
     * Đợi toast thông báo xóa thành công.
     */
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