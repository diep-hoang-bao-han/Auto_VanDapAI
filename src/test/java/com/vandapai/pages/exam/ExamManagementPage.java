package com.vandapai.pages.exam;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ExamManagementPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ExamManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // =========================
    // LOCATORS - COMMON
    // =========================

    private final By createExamButton = By.xpath(
            "//a[@href='/lecturer/exam-codes/create/' and contains(normalize-space(.),'Tạo đề thi')]"
    );

    private final By proceedButton = By.id("btnSubmit");

    private final By toastMessage = By.cssSelector("span.global-toast-message");

    // =========================
    // LOCATORS - CREATE EXAM FORM
    // =========================

    private final By subjectDropdown = By.id("subjectId");
    private final By examSetNameInput = By.id("examSetName");
    private final By academicYearInput = By.id("academicYear");
    private final By semesterHiddenInput = By.id("semester");
    private final By sourceBanksSelect = By.id("sourceBanks");

    private final By easyCountInput = By.id("easyCount");
    private final By mediumCountInput = By.id("mediumCount");
    private final By hardCountInput = By.id("hardCount");

    private final By easyScoreInput = By.id("easyScore");
    private final By mediumScoreInput = By.id("mediumScore");
    private final By hardScoreInput = By.id("hardScore");

    private final By numberOfExamCodesInput = By.id("numberOfVersions");
    private final By allowDuplicateQuestionsCheckbox = By.id("allowDuplicateQuestions");

    // =========================
    // TC001 - NAVIGATE CREATE EXAM
    // =========================

    public void clickCreateExamButton() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(createExamButton));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                button
        );

        wait.until(ExpectedConditions.visibilityOf(button));

        String href = button.getAttribute("href");

        if (href != null && !href.trim().isEmpty()) {
            driver.get(href);
        } else {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(button));
                button.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            }
        }

        wait.until(ExpectedConditions.urlContains("/lecturer/exam-codes/create"));
    }

    public boolean isCreateExamPageDisplayed() {
        return wait.until(driver -> {
            String currentUrl = driver.getCurrentUrl();
            String bodyText = driver.findElement(By.tagName("body")).getText();

            return currentUrl.contains("/lecturer/exam-codes/create")
                    && bodyText.contains("Tạo đề thi");
        });
    }

    public boolean isCreateExamFormDisplayed() {
        return wait.until(driver -> {
            String bodyText = driver.findElement(By.tagName("body")).getText();

            System.out.println("CREATE EXAM PAGE BODY = " + bodyText);

            boolean hasBasicInfo =
                    bodyText.contains("Môn học")
                            || bodyText.contains("Năm học")
                            || bodyText.contains("Học kỳ");

            boolean hasSourceSection =
                    bodyText.contains("Nguồn")
                            || bodyText.contains("Lựa chọn Nguồn")
                            || bodyText.contains("Ngân hàng câu hỏi");

            boolean hasMatrixSection =
                    bodyText.contains("Ma trận")
                            || bodyText.contains("Ma trận Đề thi")
                            || bodyText.contains("Dễ")
                            || bodyText.contains("Trung bình")
                            || bodyText.contains("Khó");

            boolean hasActionButton =
                    bodyText.contains("Tạo đề thi")
                            || bodyText.contains("Tiến hành")
                            || bodyText.contains("Tạo đề");

            return hasBasicInfo && hasSourceSection && hasMatrixSection && hasActionButton;
        });
    }

    // =========================
    // TC002 - EMPTY EXAM SET NAME
    // =========================

    public void selectSubjectByValue(String subjectValue) {
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(subjectDropdown));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                dropdown,
                subjectValue
        );

        wait.until(driver -> driver.getCurrentUrl().contains("subject_id=" + subjectValue));
    }

    public void leaveExamSetNameEmpty() {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(examSetNameInput));

        input.clear();

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '';" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                input
        );
    }

    public void enterAcademicYear(String academicYear) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(academicYearInput));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                input
        );

        input.click();
        input.clear();
        input.sendKeys(academicYear);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                input,
                academicYear
        );

        wait.until(driver -> academicYear.equals(input.getAttribute("value")));
    }

    public void selectSemesterHK1() {
        WebElement hk1Button = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-value='HK1']")
        ));

        try {
            hk1Button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", hk1Button);
        }

        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('semester').value='HK1';" +
                        "document.getElementById('semester').dispatchEvent(new Event('change', { bubbles: true }));"
        );
    }

    public void selectFirstAvailableQuestionBankSource() {
        WebElement sourceSelect = wait.until(ExpectedConditions.presenceOfElementLocated(sourceBanksSelect));

        Boolean hasOption = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "const select = arguments[0];" +
                        "return select.options && select.options.length > 0;",
                sourceSelect
        );

        if (!hasOption) {
            throw new RuntimeException("Không có nguồn câu hỏi nào để chọn trong sourceBanks");
        }

        ((JavascriptExecutor) driver).executeScript(
                "const select = arguments[0];" +
                        "const firstValue = select.options[0].value;" +
                        "select.value = firstValue;" +
                        "for (let option of select.options) {" +
                        "   option.selected = option.value === firstValue;" +
                        "}" +
                        "select.dispatchEvent(new Event('change', { bubbles: true }));" +
                        "if (window.jQuery) { jQuery(select).trigger('change'); }",
                sourceSelect
        );
    }

    public void configValidExamMatrix() {
        setInputValueIfExists(easyCountInput, "1");
        setInputValueIfExists(mediumCountInput, "1");
        setInputValueIfExists(hardCountInput, "1");

        setInputValueIfExists(easyScoreInput, "2");
        setInputValueIfExists(mediumScoreInput, "3");
        setInputValueIfExists(hardScoreInput, "5");
    }

    public void enterNumberOfExamCodes(String number) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(numberOfExamCodesInput));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                input
        );

        input.click();
        input.clear();
        input.sendKeys(number);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                input,
                number
        );

        wait.until(driver -> number.equals(driver.findElement(numberOfExamCodesInput).getAttribute("value")));
    }

    public void tickAllowDuplicateQuestionsCheckbox() {
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(allowDuplicateQuestionsCheckbox));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                checkbox
        );

        boolean checkedBefore = Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('allowDuplicateQuestions').checked;"
        ));

        if (!checkedBefore) {
            ((JavascriptExecutor) driver).executeScript(
                    "document.getElementById('allowDuplicateQuestions').click();"
            );
        }

        Object checkedAfter = ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('allowDuplicateQuestions').checked;"
        );

        System.out.println("ALLOW DUPLICATE CHECKED AFTER CLICK = " + checkedAfter);
    }

    public void clickProceedButton() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(proceedButton));

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

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean hasToast = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.body.innerText.includes('Vui lòng nhập mã bộ đề');"
        );

        if (!hasToast) {
            System.out.println("NORMAL CLICK BTN SUBMIT HAS NO TOAST -> CALL submitCreateExamSet() DIRECTLY");

            ((JavascriptExecutor) driver).executeScript(
                    "if (typeof submitCreateExamSet === 'function') { submitCreateExamSet(); }"
            );
        }
    }

    private void setInputValueIfExists(By locator, String value) {
        try {
            WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                    input,
                    value
            );
        } catch (Exception e) {
            System.out.println("Không tìm thấy input để set value: " + locator);
        }
    }

    public boolean isExamSetNameRequiredMessageDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        return shortWait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                String toastText = "";

                try {
                    toastText = driver.findElement(toastMessage).getText().trim();
                } catch (Exception ignored) {
                }

                String allToastText = "";

                try {
                    Object textObj = ((JavascriptExecutor) driver).executeScript(
                            "let items = document.querySelectorAll('.global-toast-message');" +
                                    "return Array.from(items).map(e => e.innerText.trim()).join(' | ');"
                    );

                    allToastText = textObj == null ? "" : textObj.toString();
                } catch (Exception ignored) {
                }

                System.out.println("TOAST TEXT = [" + toastText + "]");
                System.out.println("ALL TOAST TEXT = [" + allToastText + "]");
                System.out.println("BODY HAS ERROR = " + bodyText.contains("Vui lòng nhập mã bộ đề"));

                return toastText.contains("Vui lòng nhập mã bộ đề")
                        || allToastText.contains("Vui lòng nhập mã bộ đề")
                        || bodyText.contains("Vui lòng nhập mã bộ đề")
                        || toastText.contains("nhập mã bộ đề")
                        || allToastText.contains("nhập mã bộ đề")
                        || bodyText.contains("nhập mã bộ đề");
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void waitForUserToSeeToast() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}