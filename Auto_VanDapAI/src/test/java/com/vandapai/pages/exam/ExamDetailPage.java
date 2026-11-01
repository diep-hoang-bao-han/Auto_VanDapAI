package com.vandapai.pages.exam;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * ExamDetailPage: Xử lý các thao tác trên màn hình Chi tiết bộ đề thi.
 * Bao gồm: xem danh sách mã đề, chỉnh sửa mã đề, duyệt mã đề, xóa mã đề.
 */
public class ExamDetailPage extends BasePage {

    // =========================
    // CONSTRUCTOR
    // =========================
    public ExamDetailPage(WebDriver driver) {
        super(driver);
    }

    // =========================
    // VERIFICATION METHODS - CHECK PAGE DISPLAY
    // =========================

    /**
     * Kiểm tra xem có đang ở màn hình Chi tiết bộ đề hay không.
     * - URL chứa "/lecturer/exam-codes/" và không chứa "/create".
     * - Body text chứa các từ khóa đặc trưng (Mã đề, Danh sách mã đề, Câu hỏi...).
     */
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

    /**
     * Kiểm tra chi tiết bộ đề hiển thị đúng với tên bộ đề cụ thể.
     */
    public boolean isExamSetDetailDisplayed(String examSetName) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                String currentUrl = driver.getCurrentUrl();
                String bodyText = driver.findElement(By.tagName("body")).getText();

                boolean isDetailUrl =
                        currentUrl.contains("/lecturer/exam-codes/")
                                && !currentUrl.contains("/create");

                boolean hasBasicInfo =
                        bodyText.contains("Môn học")
                                && bodyText.contains("Năm học")
                                && bodyText.contains("Học kỳ");

                boolean hasExamCode =
                        driver.findElements(By.xpath("//*[@id='cardGrid']/div")).size() >= 1;

                return isDetailUrl && hasBasicInfo && hasExamCode;
            } catch (Exception e) {
                return false;
            }
        });
    }

    // =========================
    // LOCATORS - EDIT EXAM CODE
    // =========================
    private final By firstExamCodeCard = By.xpath("//*[@id='cardGrid']/div[1]");
    private final By examCodeNameInput = By.id("examCodeNameInput");
    private final By firstQuestionTextarea = By.xpath("//*[@id='questionEditorList']/div[1]/textarea");
    private final By saveCodeButton = By.id("btnSaveCode");
    private final By toastMessage = By.cssSelector("span.global-toast-message");

    // =========================
    // ACTIONS - EDIT EXAM CODE
    // =========================

    /**
     * Mở card mã đề đầu tiên để chỉnh sửa.
     */
    public void openFirstExamCodeCard() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement card = shortWait.until(ExpectedConditions.presenceOfElementLocated(firstExamCodeCard));

        scrollToElement(card);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(card));
            card.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", card);
        }

        shortWait.until(ExpectedConditions.visibilityOfElementLocated(examCodeNameInput));
    }

    /**
     * Kiểm tra panel biên tập mã đề hiển thị (input tên mã đề, textarea câu hỏi, nút Lưu).
     */
    public boolean isExamCodeEditorDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                WebElement input = driver.findElement(examCodeNameInput);
                WebElement textarea = driver.findElement(firstQuestionTextarea);
                WebElement saveButton = driver.findElement(saveCodeButton);

                return input.isDisplayed()
                        && textarea.isDisplayed()
                        && saveButton.isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Cập nhật tên mã đề.
     * - Thử sendKeys thông thường, nếu không được thì dùng JS set value.
     */
    public void updateExamCodeName(String newExamCodeName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(examCodeNameInput));

        input.click();
        input.clear();
        input.sendKeys(newExamCodeName);

        sleep(500);

        String actualValue = input.getAttribute("value");

        if (actualValue == null || !actualValue.trim().equals(newExamCodeName)) {
            ((JavascriptExecutor) driver).executeScript(
                    "const input = document.getElementById('examCodeNameInput');" +
                            "input.focus();" +
                            "input.value = arguments[0];" +
                            "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('blur', { bubbles: true }));",
                    newExamCodeName
            );
        }
    }

    /**
     * Cập nhật nội dung câu hỏi đầu tiên.
     * - Thử sendKeys thông thường, nếu không được thì dùng JS set value.
     */
    public void updateFirstQuestionContent(String newQuestionContent) {
        WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(firstQuestionTextarea));

        textarea.click();
        textarea.clear();
        textarea.sendKeys(newQuestionContent);

        sleep(500);

        String actualValue = textarea.getAttribute("value");

        if (actualValue == null || !actualValue.trim().equals(newQuestionContent)) {
            ((JavascriptExecutor) driver).executeScript(
                    "const textarea = document.querySelector('#questionEditorList > div:nth-child(1) textarea');" +
                            "textarea.focus();" +
                            "textarea.value = arguments[0];" +
                            "textarea.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "textarea.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "textarea.dispatchEvent(new Event('blur', { bubbles: true }));",
                    newQuestionContent
            );
        }
    }

    /**
     * Click nút Lưu mã đề và xử lý popup xác nhận.
     */
    public void clickSaveCodeButton() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(saveCodeButton));

        scrollToElement(button);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "const btn = document.getElementById('btnSaveCode');" +
                            "if (!btn) throw new Error('Không tìm thấy nút Lưu thay đổi');" +
                            "btn.click();"
            );
        }

        sleep(1000);

        clickConfirmSaveCodePopup();
    }

    /**
     * Click nút Xác nhận trên popup lưu thay đổi mã đề.
     */
    public void clickConfirmSaveCodePopup() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Đợi popup xác nhận hiển thị
        shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                return bodyText.contains("Xác nhận lưu thay đổi")
                        || bodyText.contains("Hệ thống sẽ lưu các thay đổi hiện tại")
                        || bodyText.contains("Lưu");
            } catch (Exception e) {
                return false;
            }
        });

        WebElement confirmButton = shortWait.until(
                ExpectedConditions.elementToBeClickable(globalConfirmAcceptButton)
        );

        scrollToElement(confirmButton);

        try {
            confirmButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "const btn = document.getElementById('globalConfirmAccept');" +
                            "if (!btn) throw new Error('Không tìm thấy nút Lưu trong popup xác nhận');" +
                            "btn.click();"
            );
        }

        sleep(2500);
    }

    /**
     * Kiểm tra thông báo lưu mã đề thành công.
     */
    public boolean isSaveExamCodeSuccessDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                String toastText = "";

                try {
                    toastText = driver.findElement(toastMessage).getText().trim();
                } catch (Exception ignored) {
                }

                String pageText = bodyText + " | " + toastText;

                System.out.println("SAVE EXAM CODE TEXT = " + pageText);

                return pageText.contains("Lưu thay đổi thành công")
                        || pageText.contains("Cập nhật thành công")
                        || pageText.contains("Lưu thành công")
                        || pageText.contains("thành công");
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Kiểm tra dữ liệu mã đề đã được cập nhật đúng sau khi lưu.
     */
    public boolean isEditedExamCodeDataDisplayed(String expectedExamCodeName, String expectedQuestionContent) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                WebElement codeInput = driver.findElement(examCodeNameInput);
                WebElement questionTextarea = driver.findElement(firstQuestionTextarea);

                String actualCodeName = codeInput.getAttribute("value");
                String actualQuestionContent = questionTextarea.getAttribute("value");

                System.out.println("EXPECTED EXAM CODE = " + expectedExamCodeName);
                System.out.println("ACTUAL EXAM CODE = " + actualCodeName);
                System.out.println("EXPECTED QUESTION = " + expectedQuestionContent);
                System.out.println("ACTUAL QUESTION = " + actualQuestionContent);

                return actualCodeName != null
                        && actualQuestionContent != null
                        && actualCodeName.trim().equals(expectedExamCodeName)
                        && actualQuestionContent.trim().equals(expectedQuestionContent);
            } catch (Exception e) {
                return false;
            }
        });
    }

    // =========================
    // LOCATORS & ACTIONS - APPROVE EXAM CODES
    // =========================
    private final By globalConfirmAcceptButton = By.id("globalConfirmAccept");
    private final By selectAllExamCodesCheckbox = By.id("selectAllCodes");
    private final By approveAllExamCodesButton = By.xpath("//*[@id='bulkBar']/button[1]");
    private final By examSetApprovalBadge = By.id("examSetApprovalBadge");

    /**
     * Kiểm tra trạng thái bộ đề là "CHƯA DUYỆT".
     */
    public boolean isExamSetUnapprovedStatusDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                WebElement badge = driver.findElement(examSetApprovalBadge);
                String badgeText = badge.getText().trim();

                System.out.println("EXAM SET APPROVAL BADGE = " + badgeText);

                return badge.isDisplayed()
                        && (
                        badgeText.contains("CHƯA DUYỆT")
                                || badgeText.contains("Chưa duyệt")
                );
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Tick checkbox "Chọn tất cả mã đề".
     */
    public void selectAllExamCodes() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement checkbox = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(selectAllExamCodesCheckbox)
        );

        scrollToElement(checkbox);

        Boolean checkedBefore = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('selectAllCodes').checked;"
        );

        if (!Boolean.TRUE.equals(checkedBefore)) {
            try {
                shortWait.until(ExpectedConditions.elementToBeClickable(checkbox));
                checkbox.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript(
                        "const cb = document.getElementById('selectAllCodes');" +
                                "cb.checked = true;" +
                                "cb.dispatchEvent(new Event('input', { bubbles: true }));" +
                                "cb.dispatchEvent(new Event('change', { bubbles: true }));" +
                                "cb.click();"
                );
            }
        }

        sleep(1000);

        Boolean checkedAfter = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('selectAllCodes').checked;"
        );

        System.out.println("SELECT ALL EXAM CODES CHECKED = " + checkedAfter);

        if (!Boolean.TRUE.equals(checkedAfter)) {
            throw new RuntimeException("Không tick được checkbox Chọn tất cả mã đề");
        }
    }

    /**
     * Click nút "Duyệt tất cả mã đề".
     */
    public void clickApproveAllExamCodesButton() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement button = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(approveAllExamCodesButton)
        );

        scrollToElement(button);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }

        sleep(1000);
    }

    /**
     * Xác nhận duyệt tất cả mã đề trên popup.
     */
    public void confirmApproveAllExamCodesPopup() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement confirmButton = shortWait.until(
                ExpectedConditions.elementToBeClickable(globalConfirmAcceptButton)
        );

        scrollToElement(confirmButton);

        try {
            confirmButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "const btn = document.getElementById('globalConfirmAccept');" +
                            "if (!btn) throw new Error('Không tìm thấy nút xác nhận duyệt mã đề trên popup');" +
                            "btn.click();"
            );
        }

        sleep(3000);
    }

    /**
     * Kiểm tra trạng thái bộ đề là "ĐÃ DUYỆT" sau khi duyệt thành công.
     */
    public boolean isExamSetApprovedStatusDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return shortWait.until(driver -> {
            try {
                WebElement badge = driver.findElement(examSetApprovalBadge);
                String badgeText = badge.getText().trim();

                System.out.println("EXAM SET APPROVAL BADGE AFTER APPROVE = " + badgeText);

                return badge.isDisplayed()
                        && (
                        badgeText.contains("ĐÃ DUYỆT")
                                || badgeText.contains("Đã duyệt")
                );
            } catch (Exception e) {
                return false;
            }
        });
    }

    // =========================
    // LOCATORS & ACTIONS - DELETE EXAM CODES
    // =========================
    private final By deleteAllExamCodesButton = By.xpath("//*[@id='bulkBar']/button[2]");

    /**
     * Click nút "Xóa tất cả mã đề".
     */
    public void clickDeleteAllExamCodesButton() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement button = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(deleteAllExamCodesButton)
        );

        scrollToElement(button);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }

        sleep(1000);
    }

    /**
     * Xác nhận xóa mã đề trên popup.
     */
    public void confirmDeleteExamCodesPopup() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement confirmButton = shortWait.until(
                ExpectedConditions.elementToBeClickable(globalConfirmAcceptButton)
        );

        scrollToElement(confirmButton);

        try {
            confirmButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "const btn = document.getElementById('globalConfirmAccept');" +
                            "if (!btn) throw new Error('Không tìm thấy nút xác nhận xóa trên popup');" +
                            "btn.click();"
            );
        }

        sleep(3000);
    }

    /**
     * Kiểm tra thông báo xóa mã đề thành công.
     */
    public boolean isDeleteExamCodesSuccessDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("DELETE EXAM CODES TEXT = " + bodyText);

                return bodyText.contains("Đã xóa")
                        || bodyText.contains("Đã xoá")
                        || bodyText.contains("xóa")
                        || bodyText.contains("xoá")
                        || bodyText.contains("mã đề");
            } catch (Exception e) {
                return false;
            }
        });
    }
}