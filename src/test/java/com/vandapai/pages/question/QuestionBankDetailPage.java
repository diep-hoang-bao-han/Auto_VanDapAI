package com.vandapai.pages.question;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.support.ui.Select;
public class QuestionBankDetailPage extends BasePage {

    public QuestionBankDetailPage(WebDriver driver) {
        super(driver);
    }

    public boolean isQuestionBankDetailDisplayed(String bankName) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return longWait.until(driver -> {
            try {
                String currentUrl = driver.getCurrentUrl();
                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("DETAIL CURRENT URL = " + currentUrl);
                System.out.println("DETAIL BODY TEXT = " + bodyText);

                boolean hasBankName = bodyText.contains(bankName);
                boolean hasDetailText =
                        bodyText.contains("Chi tiết ngân hàng")
                                || bodyText.contains("Danh sách câu hỏi")
                                || bodyText.contains("Ngân hàng câu hỏi")
                                || bodyText.contains("Lưu thành ngân hàng");

                boolean hasQuestionInfo =
                        bodyText.contains("câu hỏi")
                                || bodyText.contains("Dễ")
                                || bodyText.contains("Trung bình")
                                || bodyText.contains("Khó");

                return hasBankName || (hasDetailText && hasQuestionInfo);
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
    // =========================
    // LOCATORS - EDIT QUESTION
    // =========================

    private final By firstQuestionEditButton = By.xpath("//*[@id='questionList']/div[1]/div[3]/div[1]/button[1]");
    private final By firstQuestionContentTextarea = By.xpath("//*[@id='questionList']/div[1]/div[1]/div[2]/textarea");
    private final By firstQuestionSaveButton = By.xpath("//*[@id='questionList']/div[1]/div[3]/div[2]/button[1]");
    private final By toastMessage = By.cssSelector("div.global-toast.success");

    public void clickEditFirstQuestionButton() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement editButton = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(firstQuestionEditButton)
        );

        scrollToElement(editButton);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(editButton));
            editButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editButton);
        }

        shortWait.until(ExpectedConditions.visibilityOfElementLocated(firstQuestionContentTextarea));

        sleep(800);
    }

    public void updateFirstQuestionContent(String newQuestionContent) {
        WebElement textarea = wait.until(
                ExpectedConditions.visibilityOfElementLocated(firstQuestionContentTextarea)
        );

        scrollToElement(textarea);

        try {
            textarea.click();
            textarea.clear();
            textarea.sendKeys(newQuestionContent);
        } catch (Exception e) {
            System.out.println("SENDKEYS NỘI DUNG CÂU HỎI KHÔNG ĐƯỢC -> SET BẰNG JS");
        }

        sleep(500);

        String actualValue = textarea.getAttribute("value");

        if (actualValue == null || !actualValue.trim().equals(newQuestionContent)) {
            ((JavascriptExecutor) driver).executeScript(
                    "const textarea = arguments[0];" +
                            "textarea.focus();" +
                            "textarea.value = arguments[1];" +
                            "textarea.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "textarea.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "textarea.dispatchEvent(new Event('blur', { bubbles: true }));",
                    textarea,
                    newQuestionContent
            );

            sleep(500);
        }

        actualValue = textarea.getAttribute("value");

        System.out.println("EXPECTED QUESTION CONTENT = " + newQuestionContent);
        System.out.println("ACTUAL QUESTION CONTENT = " + actualValue);

        if (actualValue == null || !actualValue.trim().equals(newQuestionContent)) {
            throw new RuntimeException(
                    "Không nhập được nội dung câu hỏi mới. Expected = "
                            + newQuestionContent + ", Actual = " + actualValue
            );
        }
    }

    public void clickSaveFirstQuestionButton() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement saveButton = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(firstQuestionSaveButton)
        );

        scrollToElement(saveButton);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(saveButton));
            saveButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);
        }

        sleep(1500);
    }

    public boolean isUpdateQuestionSuccessToastDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                String toastText = "";

                try {
                    WebElement toast = driver.findElement(toastMessage);
                    toastText = toast.getText().trim();
                } catch (Exception ignored) {
                }

                String pageText = bodyText + " | " + toastText;

                System.out.println("UPDATE QUESTION TOAST TEXT = " + pageText);

                return pageText.contains("Đã lưu thành công")
                        || pageText.contains("Lưu thành công")
                        || pageText.contains("Cập nhật thành công")
                        || pageText.contains("thành công");
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean isFirstQuestionContentDisplayed(String expectedQuestionContent) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                String textareaValue = "";
                try {
                    WebElement textarea = driver.findElement(firstQuestionContentTextarea);
                    textareaValue = textarea.getAttribute("value");
                } catch (Exception ignored) {
                }

                System.out.println("EXPECTED UPDATED QUESTION = " + expectedQuestionContent);
                System.out.println("BODY TEXT AFTER REFRESH = " + bodyText);
                System.out.println("TEXTAREA VALUE AFTER REFRESH = " + textareaValue);

                return bodyText.contains(expectedQuestionContent)
                        || (
                        textareaValue != null
                                && textareaValue.trim().equals(expectedQuestionContent)
                );
            } catch (Exception e) {
                return false;
            }
        });
    }
    // =========================
    // LOCATORS - UPDATE QUESTION LEVEL
    // =========================

    private final By firstQuestionLevelDropdown = By.cssSelector(
            "#questionList > div:nth-child(1) select.question-difficulty-select"
    );
    public void selectFirstQuestionLevelByValue(String levelValue) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement dropdown = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(firstQuestionLevelDropdown)
        );

        scrollToElement(dropdown);

        try {
            Select select = new Select(dropdown);
            select.selectByValue(levelValue);
        } catch (Exception e) {
            System.out.println("SELECT MỨC ĐỘ BẰNG SELECT KHÔNG ĐƯỢC -> SET BẰNG JS");

            ((JavascriptExecutor) driver).executeScript(
                    "const select = arguments[0];" +
                            "select.value = arguments[1];" +
                            "select.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "select.dispatchEvent(new Event('change', { bubbles: true }));",
                    dropdown,
                    levelValue
            );
        }

        sleep(1000);
    }

    public boolean isUpdateQuestionLevelSuccessToastDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                String toastText = "";

                try {
                    WebElement toast = driver.findElement(By.cssSelector("span.global-toast-message"));
                    toastText = toast.getText().trim();
                } catch (Exception ignored) {
                }

                String pageText = bodyText + " | " + toastText;

                System.out.println("UPDATE QUESTION LEVEL TOAST TEXT = " + pageText);

                return pageText.contains("Đã cập nhật mức độ câu hỏi")
                        || pageText.contains("cập nhật mức độ")
                        || pageText.contains("Đã cập nhật")
                        || pageText.contains("thành công");
            } catch (Exception e) {
                return false;
            }
        });
    }
    public boolean isFirstQuestionLevelValueDisplayed(String expectedLevelValue) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return shortWait.until(driver -> {
            try {
                WebElement dropdown = driver.findElement(firstQuestionLevelDropdown);
                Select select = new Select(dropdown);

                String actualValue = select.getFirstSelectedOption().getAttribute("value");
                String actualText = select.getFirstSelectedOption().getText().trim();

                System.out.println("EXPECTED LEVEL VALUE = " + expectedLevelValue);
                System.out.println("ACTUAL LEVEL VALUE = " + actualValue);
                System.out.println("ACTUAL LEVEL TEXT = " + actualText);

                return expectedLevelValue.equals(actualValue);
            } catch (Exception e) {
                return false;
            }
        });
    }

    // =========================
// LOCATORS - BULK UPDATE QUESTION LEVEL
// =========================

    private final By firstQuestionCheckbox = By.xpath("//*[@id='questionList']/div[1]/input");
    private final By secondQuestionCheckbox = By.xpath("//*[@id='questionList']/div[2]/input");
    private final By thirdQuestionCheckbox = By.xpath("//*[@id='questionList']/div[3]/input");

    private final By bulkChangeLevelButton = By.xpath("//*[@id='bulkBar']/button[1]");
    private final By changeLevelModal = By.id("changeLevelModal");
    private final By hardLevelButtonInModal = By.xpath("//*[@id='bulkLevelSegment']/button[3]");

    private final By secondQuestionLevelDropdown = By.cssSelector("#questionList > div:nth-child(2) select.question-difficulty-select");
    private final By thirdQuestionLevelDropdown = By.cssSelector("#questionList > div:nth-child(3) select.question-difficulty-select");


    public void selectFirstThreeQuestions() {
        clickQuestionCheckboxIfNotSelected(firstQuestionCheckbox);
        clickQuestionCheckboxIfNotSelected(secondQuestionCheckbox);
        clickQuestionCheckboxIfNotSelected(thirdQuestionCheckbox);

        sleep(1000);
    }

    private void clickQuestionCheckboxIfNotSelected(By checkboxLocator) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement checkbox = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(checkboxLocator)
        );

        scrollToElement(checkbox);

        Boolean checkedBefore = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].checked;",
                checkbox
        );

        if (!Boolean.TRUE.equals(checkedBefore)) {
            try {
                shortWait.until(ExpectedConditions.elementToBeClickable(checkbox));
                checkbox.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].checked = true;" +
                                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                        checkbox
                );
            }
        }

        sleep(300);
    }

    public void selectHardLevelInBulkModal() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement hardButton = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(hardLevelButtonInModal)
        );

        scrollToElement(hardButton);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(hardButton));
            hardButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", hardButton);
        }

        sleep(1500);
    }

    public boolean isBulkUpdateLevelSuccessToastDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                String toastText = "";

                try {
                    WebElement toast = driver.findElement(toastMessage);
                    toastText = toast.getText().trim();
                } catch (Exception ignored) {
                }

                String pageText = bodyText + " | " + toastText;

                System.out.println("BULK UPDATE LEVEL TOAST TEXT = " + pageText);

                return pageText.contains("Thành công")
                        || pageText.contains("thành công")
                        || pageText.contains("Đã cập nhật")
                        || pageText.contains("cập nhật mức độ");
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean areFirstThreeQuestionsHardLevel() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return shortWait.until(driver -> {
            try {
                String firstValue = getSelectedQuestionLevelValue(firstQuestionLevelDropdown);
                String secondValue = getSelectedQuestionLevelValue(secondQuestionLevelDropdown);
                String thirdValue = getSelectedQuestionLevelValue(thirdQuestionLevelDropdown);

                System.out.println("FIRST QUESTION LEVEL = " + firstValue);
                System.out.println("SECOND QUESTION LEVEL = " + secondValue);
                System.out.println("THIRD QUESTION LEVEL = " + thirdValue);

                return "HARD".equals(firstValue)
                        && "HARD".equals(secondValue)
                        && "HARD".equals(thirdValue);
            } catch (Exception e) {
                return false;
            }
        });
    }

    private String getSelectedQuestionLevelValue(By dropdownLocator) {
        WebElement dropdown = wait.until(
                ExpectedConditions.presenceOfElementLocated(dropdownLocator)
        );

        Select select = new Select(dropdown);

        return select.getFirstSelectedOption().getAttribute("value");
    }

    public void clickBulkChangeLevelButton() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement button = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(bulkChangeLevelButton)
        );

        scrollToElement(button);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }

        shortWait.until(ExpectedConditions.visibilityOfElementLocated(changeLevelModal));

        sleep(800);
    }

    private final By confirmBulkLevelButton = By.id("confirmBulkLevelBtn");
    public void clickConfirmBulkLevelButton() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement button = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(confirmBulkLevelButton)
        );

        scrollToElement(button);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }

        sleep(1500);
    }

    private final By allQuestionTab = By.xpath("//button[contains(normalize-space(.),'Tất cả')]");
    private final By bulkDeleteQuestionButton = By.xpath("//*[@id='bulkBar']/button[2]");
    private final By globalConfirmBackdrop = By.id("globalConfirmBackdrop");
    private final By globalConfirmAcceptButton = By.id("globalConfirmAccept");


    public void clickBulkDeleteQuestionButton() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement button = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(bulkDeleteQuestionButton)
        );

        scrollToElement(button);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }

        shortWait.until(ExpectedConditions.visibilityOfElementLocated(globalConfirmBackdrop));

        sleep(800);
    }

    public void confirmBulkDeleteQuestionPopup() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        shortWait.until(ExpectedConditions.visibilityOfElementLocated(globalConfirmBackdrop));

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

        sleep(2000);
    }

    public boolean isBulkDeleteQuestionSuccessToastDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                String toastText = "";

                try {
                    WebElement toast = driver.findElement(By.cssSelector("span.global-toast-message"));
                    toastText = toast.getText().trim();
                } catch (Exception ignored) {
                }

                String pageText = bodyText + " | " + toastText;

                System.out.println("BULK DELETE QUESTION TEXT = " + pageText);

                return pageText.contains("Xóa thành công")
                        || pageText.contains("Xoá thành công")
                        || pageText.contains("Xóa thàn công")
                        || pageText.contains("Đã xóa")
                        || pageText.contains("Đã xoá")
                        || pageText.contains("Thành công")
                        || pageText.contains("thành công");
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean isQuestionCountDecreasedBy(int beforeCount, int deletedCount) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                int afterCount = driver.findElements(questionItems).size();

                System.out.println("QUESTION COUNT BEFORE DELETE = " + beforeCount);
                System.out.println("QUESTION COUNT AFTER DELETE = " + afterCount);
                System.out.println("EXPECTED DELETED COUNT = " + deletedCount);

                return afterCount == beforeCount - deletedCount;
            } catch (Exception e) {
                return false;
            }
        });
    }

    private final By questionItems = By.cssSelector("#questionList > div");

    public int getTotalQuestionCountFromAllTab() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        shortWait.until(ExpectedConditions.presenceOfElementLocated(By.id("questionList")));

        int count = shortWait.until(driver -> {
            try {
                int size = driver.findElements(questionItems).size();

                System.out.println("QUESTION ITEM COUNT = " + size);

                return size > 0 ? size : -1;
            } catch (Exception e) {
                return -1;
            }
        });

        if (count <= 0) {
            throw new RuntimeException("Không đếm được số lượng câu hỏi trong #questionList");
        }

        return count;
    }

    // =========================
    // LOCATORS - DELETE QUESTION BANK
    // =========================

    private final By deleteQuestionBankButton = By.id("deleteBankBtnTop");

    public void clickDeleteQuestionBankButton() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement button = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(deleteQuestionBankButton)
        );

        scrollToElement(button);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "const btn = document.getElementById('deleteBankBtnTop');" +
                            "if (!btn) throw new Error('Không tìm thấy nút Xóa ngân hàng');" +
                            "btn.scrollIntoView({block:'center'});" +
                            "btn.click();"
            );
        }

        shortWait.until(driver -> {
            try {
                WebElement popup = driver.findElement(globalConfirmBackdrop);
                String text = popup.getText();

                System.out.println("DELETE BANK POPUP TEXT = " + text);

                return popup.isDisplayed()
                        && (
                        text.contains("Xóa")
                                || text.contains("xoá")
                                || text.contains("ngân hàng")
                                || text.contains("xác nhận")
                );
            } catch (Exception e) {
                return false;
            }
        });

        sleep(800);
    }

    public void confirmDeleteQuestionBankPopup(String bankName) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        shortWait.until(ExpectedConditions.visibilityOfElementLocated(globalConfirmBackdrop));

        WebElement confirmButton = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(globalConfirmAcceptButton)
        );

        scrollToElement(confirmButton);

        String oldUrl = driver.getCurrentUrl();

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(confirmButton));
            confirmButton.click();
        } catch (Exception e) {
            System.out.println("NORMAL CLICK CONFIRM DELETE BANK FAILED -> USE JS CLICK");
        }

        sleep(500);

        if (driver.getCurrentUrl().equals(oldUrl)) {
            ((JavascriptExecutor) driver).executeScript(
                    "const btn = document.getElementById('globalConfirmAccept');" +
                            "if (!btn) throw new Error('Không tìm thấy nút xác nhận xóa ngân hàng');" +
                            "btn.click();" +
                            "setTimeout(() => btn.click(), 300);" +
                            "setTimeout(() => btn.dispatchEvent(new MouseEvent('click', {bubbles:true, cancelable:true, view:window})), 700);"
            );
        }

        shortWait.until(driver -> {
            try {
                String currentUrl = driver.getCurrentUrl();
                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("AFTER CONFIRM DELETE BANK URL = " + currentUrl);
                System.out.println("AFTER CONFIRM DELETE BANK TEXT = " + bodyText);

                boolean leftDetailPage =
                        currentUrl.contains("/lecturer/questions")
                                && !currentUrl.contains("mode=detail")
                                && !currentUrl.contains("view=bank");

                boolean backToList =
                        bodyText.contains("Danh sách ngân hàng")
                                && bodyText.contains("Tạo ngân hàng mới")
                                && !bodyText.contains(bankName);

                boolean successToast =
                        bodyText.contains("Xóa ngân hàng thành công")
                                || bodyText.contains("Xóa thành công")
                                || bodyText.contains("Đã xóa")
                                || bodyText.contains("thành công");

                return leftDetailPage || backToList || successToast;
            } catch (Exception e) {
                return false;
            }
        });

        sleep(1500);
    }

    public boolean isDeleteQuestionBankCompleted(String deletedBankName) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                String currentUrl = driver.getCurrentUrl();

                String toastText = "";
                try {
                    WebElement toast = driver.findElement(By.cssSelector("span.global-toast-message"));
                    toastText = toast.getText().trim();
                } catch (Exception ignored) {
                }

                String pageText = bodyText + " | " + toastText;

                System.out.println("DELETE QUESTION BANK CHECK URL = " + currentUrl);
                System.out.println("DELETE QUESTION BANK CHECK TEXT = " + pageText);
                System.out.println("DELETED BANK NAME = " + deletedBankName);

                boolean hasSuccessToast =
                        pageText.contains("Xóa ngân hàng thành công")
                                || pageText.contains("Xoá ngân hàng thành công")
                                || pageText.contains("Xóa thành công")
                                || pageText.contains("Đã xóa")
                                || pageText.contains("thành công");

                boolean backToBankList =
                        pageText.contains("Danh sách ngân hàng")
                                || pageText.contains("Tạo ngân hàng mới")
                                || driver.findElements(By.id("bankListContainer")).size() > 0;

                boolean deletedBankNotDisplayed =
                        !pageText.contains(deletedBankName);

                return hasSuccessToast || (backToBankList && deletedBankNotDisplayed);
            } catch (Exception e) {
                return false;
            }
        });
    }
    private final By updateQuestionBankButton = By.xpath(
            "//*[self::button or self::a][contains(normalize-space(.),'Cập nhật vào ngân hàng')]"
    );

    public boolean isBulkUpdateLevelMarkedAsChangedToastDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("BULK UPDATE LEVEL MARKED TOAST TEXT = " + bodyText);

                return bodyText.contains("Đã đánh dấu/cập nhật thay đổi mức độ")
                        || bodyText.contains("Bấm Cập nhật vào ngân hàng để lưu chính thức")
                        || bodyText.contains("Đã chỉnh sửa")
                        || bodyText.contains("Chưa cập nhật");
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean isQuestionMarkedAsChangedToastDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));

        return shortWait.until(driver -> {
            String bodyText = driver.findElement(By.tagName("body")).getText();

            return bodyText.contains("Đã đánh dấu thay đổi")
                    || bodyText.contains("Bấm Cập nhật vào ngân hàng để lưu")
                    || bodyText.contains("Chưa cập nhật");
        });
    }

    public boolean isUpdateQuestionBankSuccessToastDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(12));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("UPDATE QUESTION BANK TOAST TEXT = " + bodyText);

                return bodyText.contains("Cập nhật")
                        && bodyText.contains("thành công")
                        || bodyText.contains("Đã lưu")
                        || bodyText.contains("cập nhật ngân hàng thành công")
                        || bodyText.contains("Cập nhật ngân hàng câu hỏi thành công");
            } catch (Exception e) {
                return false;
            }
        });
    }


    public void clickUpdateQuestionBankButton() {
        WebElement button = wait.until(
                ExpectedConditions.presenceOfElementLocated(updateQuestionBankButton)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                button
        );

        try {
            wait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }

        sleep(1500);
    }
    public String getFirstQuestionContent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement questionContent = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//div[contains(@class,'question-content')])[1]")
        ));

        return questionContent.getText().trim();
    }

    public boolean isAnyQuestionMarkedAsUnsaved() {
        try {
            List<WebElement> unsavedLabels = driver.findElements(
                    By.xpath("//*[contains(text(),'Chưa lưu')]")
            );
            return unsavedLabels.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isLevelCountUpdated() {
        try {
            WebElement hardCount = driver.findElement(
                    By.xpath("//button[contains(.,'Khó')]")
            );

            String text = hardCount.getText(); // ví dụ: "Khó (10)"
            int count = Integer.parseInt(text.replaceAll("\\D+", ""));

            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }



    // ===== WAIT LOAD =====
    public boolean waitForQuestionListLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'question')]")
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    // ===== COUNT =====
    public int getHardQuestionCount() {
        try {
            // Cách 1: Dùng id="countHard" bạn vừa cung cấp
            WebElement hardCountElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("countHard"))
            );
            String text = hardCountElement.getText().trim();

            // Nếu text là "10" hoặc "(10)" hoặc "Khó (10)"
            int count = Integer.parseInt(text.replaceAll("\\D+", ""));

            System.out.println("HARD QUESTION COUNT = " + count);
            return count;

        } catch (Exception e) {
            System.out.println("Không lấy được số lượng câu hỏi Khó: " + e.getMessage());

            // Fallback: thử cách cũ
            try {
                WebElement hard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//button[contains(.,'Khó')]")
                ));
                String text = hard.getText();
                return Integer.parseInt(text.replaceAll("\\D+", ""));
            } catch (Exception ex) {
                return 0;
            }
        }
    }


}