package com.vandapai.pages.exam;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ExamCreatePage extends BasePage {

    public ExamCreatePage(WebDriver driver) {
        super(driver);
    }

    private final By proceedButton = By.id("btnSubmit");

    private final By subjectDropdown = By.id("subjectId");
    private final By examSetNameInput = By.id("examSetName");
    private final By academicYearInput = By.id("academicYear");
    private final By sourceBanksSelect = By.id("sourceBanks");

    private final By easyCountInput = By.id("easyCount");
    private final By mediumCountInput = By.id("mediumCount");
    private final By hardCountInput = By.id("hardCount");

    private final By easyScoreInput = By.id("easyScore");
    private final By mediumScoreInput = By.id("mediumScore");
    private final By hardScoreInput = By.id("hardScore");

    private final By numberOfExamCodesInput = By.id("numberOfVersions");
    private final By allowDuplicateQuestionsCheckbox = By.id("allowDuplicateQuestions");

    private final By globalConfirmBackdrop = By.cssSelector("#globalConfirmBackdrop.open[aria-hidden='false']");
    private final By globalConfirmAcceptButton = By.cssSelector("#globalConfirmBackdrop.open[aria-hidden='false'] #globalConfirmAccept");
    private final By globalConfirmCancelButton = By.cssSelector("#globalConfirmBackdrop.open[aria-hidden='false'] #globalConfirmCancel");

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

    public void selectSubjectByValue(String subjectValue) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement dropdown = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(subjectDropdown)
        );

        scrollToElement(dropdown);

        try {
            Select select = new Select(dropdown);
            select.selectByValue(subjectValue);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    dropdown,
                    subjectValue
            );
        }

        shortWait.until(driver -> {
            try {
                String actualValue = driver.findElement(subjectDropdown).getAttribute("value");
                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("SUBJECT SELECTED VALUE = " + actualValue);
                System.out.println("AFTER SELECT SUBJECT URL = " + driver.getCurrentUrl());

                boolean selectedCorrectSubject = subjectValue.equals(actualValue);

                boolean formReady =
                        driver.findElements(examSetNameInput).size() > 0
                                && driver.findElements(academicYearInput).size() > 0
                                && (
                                bodyText.contains("Nguồn câu hỏi")
                                        || bodyText.contains("Ngân hàng câu hỏi")
                                        || driver.findElements(sourceBanksSelect).size() > 0
                        );

                return selectedCorrectSubject && formReady;
            } catch (Exception e) {
                return false;
            }
        });

        sleep(1000);
    }

    public void leaveExamSetNameEmpty() {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(examSetNameInput));
        scrollToElement(input);

        input.click();
        input.sendKeys(Keys.CONTROL, "a");
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(Keys.TAB);

        sleep(500);
    }

    public void enterExamSetName(String examSetName) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement input = shortWait.until(ExpectedConditions.presenceOfElementLocated(examSetNameInput));

        scrollToElement(input);

        try {
            input.click();
            input.sendKeys(Keys.CONTROL, "a");
            input.sendKeys(Keys.BACK_SPACE);
            input.sendKeys(examSetName);
            input.sendKeys(Keys.TAB);
        } catch (Exception e) {
            System.out.println("SENDKEYS MÃ BỘ ĐỀ KHÔNG ĐƯỢC -> SET BẰNG JS");
        }

        sleep(500);

        String actualValue = driver.findElement(examSetNameInput).getAttribute("value");

        if (actualValue == null || !actualValue.trim().equals(examSetName)) {
            System.out.println("MÃ BỘ ĐỀ CHƯA NHẬP ĐÚNG -> SET LẠI BẰNG JS");

            ((JavascriptExecutor) driver).executeScript(
                    "const input = document.getElementById('examSetName');" +
                            "input.focus();" +
                            "input.value = arguments[0];" +
                            "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('blur', { bubbles: true }));",
                    examSetName
            );

            sleep(500);
        }

        actualValue = driver.findElement(examSetNameInput).getAttribute("value");

        System.out.println("EXAM SET NAME | EXPECTED = " + examSetName + " | ACTUAL = " + actualValue);

        if (actualValue == null || !actualValue.trim().equals(examSetName)) {
            throw new RuntimeException(
                    "Không nhập được Mã bộ đề. Expected = " + examSetName + ", Actual = " + actualValue
            );
        }
    }

    public void enterAcademicYear(String academicYear) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement input = shortWait.until(ExpectedConditions.presenceOfElementLocated(academicYearInput));

        scrollToElement(input);

        try {
            input.click();
            input.sendKeys(Keys.CONTROL, "a");
            input.sendKeys(Keys.BACK_SPACE);
            input.sendKeys(academicYear);
            input.sendKeys(Keys.TAB);
        } catch (Exception e) {
            System.out.println("SENDKEYS NĂM HỌC KHÔNG ĐƯỢC -> SET BẰNG JS");
        }

        sleep(500);

        String actualValue = driver.findElement(academicYearInput).getAttribute("value");

        if (actualValue == null || !actualValue.trim().equals(academicYear)) {
            System.out.println("NĂM HỌC CHƯA NHẬP ĐÚNG -> SET LẠI BẰNG JS");

            ((JavascriptExecutor) driver).executeScript(
                    "const input = document.getElementById('academicYear');" +
                            "input.focus();" +
                            "input.value = arguments[0];" +
                            "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('blur', { bubbles: true }));",
                    academicYear
            );

            sleep(500);
        }

        actualValue = driver.findElement(academicYearInput).getAttribute("value");

        System.out.println("ACADEMIC YEAR | EXPECTED = " + academicYear + " | ACTUAL = " + actualValue);

        if (actualValue == null || !actualValue.trim().equals(academicYear)) {
            throw new RuntimeException(
                    "Không nhập được Năm học. Expected = " + academicYear + ", Actual = " + actualValue
            );
        }
    }

    public void selectSemesterHK1() {
        WebElement hk1Button = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-value='HK1']")
        ));

        scrollToElement(hk1Button);
        clickElement(hk1Button);

        wait.until(driver -> "HK1".equals(
                ((JavascriptExecutor) driver).executeScript("return document.getElementById('semester').value;")
        ));
    }

    public void selectFirstAvailableQuestionBankSource() {
        WebElement sourceSelect = wait.until(ExpectedConditions.presenceOfElementLocated(sourceBanksSelect));
        scrollToElement(sourceSelect);

        Boolean hasOption = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "const select = arguments[0];" +
                        "return select.options && select.options.length > 0;",
                sourceSelect
        );

        if (!Boolean.TRUE.equals(hasOption)) {
            throw new RuntimeException("Không có nguồn câu hỏi nào để chọn trong sourceBanks");
        }

        try {
            Select select = new Select(sourceSelect);
            select.selectByIndex(0);
        } catch (Exception e) {
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

        sleep(1000);
    }

    public void configValidExamMatrix() {
        configExamMatrix("1", "1", "1", "2", "3", "5");
    }

    public void configExamMatrix(String easyCount, String mediumCount, String hardCount,
                                 String easyScore, String mediumScore, String hardScore) {
        setNumberByPlusMinusButtons(easyCountInput, easyCount);
        setNumberByPlusMinusButtons(mediumCountInput, mediumCount);
        setNumberByPlusMinusButtons(hardCountInput, hardCount);

        setNumberByPlusMinusButtons(easyScoreInput, easyScore);
        setNumberByPlusMinusButtons(mediumScoreInput, mediumScore);
        setNumberByPlusMinusButtons(hardScoreInput, hardScore);

        sleep(1000);
    }

    public void enterNumberOfExamCodes(String number) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement input = shortWait.until(ExpectedConditions.presenceOfElementLocated(numberOfExamCodesInput));
        scrollToElement(input);

        try {
            input.click();
            input.sendKeys(Keys.CONTROL, "a");
            input.sendKeys(Keys.BACK_SPACE);
            input.sendKeys(number);
            input.sendKeys(Keys.TAB);
        } catch (Exception e) {
            System.out.println("SENDKEYS SỐ LƯỢNG MÃ ĐỀ KHÔNG ĐƯỢC -> SET BẰNG JS");
        }

        sleep(500);

        String actualValue = driver.findElement(numberOfExamCodesInput).getAttribute("value");

        if (actualValue == null || !actualValue.trim().equals(number)) {
            System.out.println("SỐ LƯỢNG MÃ ĐỀ CHƯA NHẬP ĐÚNG -> SET LẠI BẰNG JS");

            ((JavascriptExecutor) driver).executeScript(
                    "const input = document.getElementById('numberOfVersions');" +
                            "input.focus();" +
                            "input.value = arguments[0];" +
                            "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('blur', { bubbles: true }));",
                    number
            );

            sleep(500);
        }

        actualValue = driver.findElement(numberOfExamCodesInput).getAttribute("value");

        System.out.println("NUMBER OF EXAM CODES | EXPECTED = " + number + " | ACTUAL = " + actualValue);

        if (actualValue == null || !actualValue.trim().equals(number)) {
            throw new RuntimeException(
                    "Không nhập được Số lượng mã đề. Expected = " + number + ", Actual = " + actualValue
            );
        }
    }

    public void refreshCreateExamPage() {
        driver.navigate().refresh();

        wait.until(ExpectedConditions.urlContains("/lecturer/exam-codes/create"));

        wait.until(ExpectedConditions.presenceOfElementLocated(examSetNameInput));
        wait.until(ExpectedConditions.presenceOfElementLocated(academicYearInput));
        wait.until(ExpectedConditions.presenceOfElementLocated(numberOfExamCodesInput));

        sleep(1000);
    }

    public void tickAllowDuplicateQuestionsCheckbox() {
        wait.until(ExpectedConditions.presenceOfElementLocated(allowDuplicateQuestionsCheckbox));

        ((JavascriptExecutor) driver).executeScript(
                "const cb = document.getElementById('allowDuplicateQuestions');" +
                        "if (!cb) throw new Error('Không tìm thấy checkbox allowDuplicateQuestions');" +
                        "cb.scrollIntoView({block:'center'});" +
                        "cb.checked = true;" +
                        "cb.setAttribute('checked', 'checked');" +
                        "cb.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "cb.dispatchEvent(new Event('change', { bubbles: true }));"
        );

        sleep(500);

        Boolean checkedAfter = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('allowDuplicateQuestions').checked;"
        );

        System.out.println("ALLOW DUPLICATE QUESTIONS CHECKED = " + checkedAfter);

        if (!Boolean.TRUE.equals(checkedAfter)) {
            throw new RuntimeException("Không tick được checkbox Cho phép câu hỏi trùng lặp giữa các mã đề");
        }
    }

    public void clickProceedButton() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(proceedButton));
        scrollToElement(button);

        ((JavascriptExecutor) driver).executeScript(
                "const btn = document.getElementById('btnSubmit');" +
                        "if (!btn) throw new Error('Không tìm thấy nút Tiến hành tạo đề thi');" +
                        "btn.scrollIntoView({block:'center'});" +
                        "btn.dispatchEvent(new MouseEvent('mousedown', {bubbles:true, cancelable:true, view:window}));" +
                        "btn.dispatchEvent(new MouseEvent('mouseup', {bubbles:true, cancelable:true, view:window}));" +
                        "btn.dispatchEvent(new MouseEvent('click', {bubbles:true, cancelable:true, view:window}));"
        );

        sleep(1500);
    }

    public void printCreateExamFormDebug() {
        Object debug = ((JavascriptExecutor) driver).executeScript(
                "const ids=['examSetName','academicYear','semester','sourceBanks','easyCount','mediumCount','hardCount','easyScore','mediumScore','hardScore','numberOfVersions','allowDuplicateQuestions','btnSubmit'];" +
                        "let o={}; ids.forEach(id=>{" +
                        "let e=document.getElementById(id);" +
                        "o[id]=e ? (e.type==='checkbox' ? e.checked : (id==='btnSubmit' ? {disabled:e.disabled,text:e.innerText,cls:e.className} : e.value)) : 'NOT_FOUND';" +
                        "}); return o;"
        );
        System.out.println("FORM DEBUG = " + debug);
    }

    public void clickAcceptConfirmPopup() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("globalConfirmAccept")));

        ((JavascriptExecutor) driver).executeScript(
                "const btn = document.getElementById('globalConfirmAccept');" +
                        "if (!btn) throw new Error('Không tìm thấy nút Xác nhận');" +
                        "btn.click();" +
                        "setTimeout(() => btn.click(), 300);" +
                        "setTimeout(() => btn.click(), 700);" +
                        "setTimeout(() => btn.dispatchEvent(new MouseEvent('click', {bubbles:true, cancelable:true, view:window})), 1000);"
        );

        sleep(5000);
    }

    public void clickCancelConfirmPopup() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        shortWait.until(ExpectedConditions.visibilityOfElementLocated(globalConfirmBackdrop));

        WebElement cancelButton = shortWait.until(
                ExpectedConditions.elementToBeClickable(globalConfirmCancelButton)
        );

        scrollToElement(cancelButton);
        clickElement(cancelButton);

        shortWait.until(driver -> {
            try {
                Object isOpen = ((JavascriptExecutor) driver).executeScript(
                        "const popup = document.querySelector('#globalConfirmBackdrop');" +
                                "return popup && popup.classList.contains('open') && popup.getAttribute('aria-hidden') === 'false';"
                );

                return !Boolean.TRUE.equals(isOpen);
            } catch (Exception e) {
                return true;
            }
        });

        sleep(800);
    }

    public boolean isExamSetNameRequiredMessageDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        return shortWait.until(driver -> {
            String pageText = getPageAndToastText();

            return pageText.contains("Vui lòng nhập mã bộ đề")
                    || pageText.contains("nhập mã bộ đề");
        });
    }

    public boolean isAcademicYearInvalidMessageDisplayed() {
        String expectedMessage = "Vui lòng nhập đúng định dạng năm học";

        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            return shortWait.until(d -> {
                String pageText = d.findElement(By.tagName("body")).getText();

                return pageText.contains(expectedMessage)
                        || pageText.contains("định dạng năm học")
                        || pageText.contains("2025-2026");
            });
        } catch (Exception e) {
            System.out.println("Không bắt được toast lỗi năm học. BODY TEXT = " + driver.findElement(By.tagName("body")).getText());
            return false;
        }
    }

    public boolean isQuestionBankSourceDisplayed() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

            return shortWait.until(d -> {
                String bodyText = d.findElement(By.tagName("body")).getText();

                boolean hasQuestionBankText =
                        bodyText.contains("Ngân hàng")
                                || bodyText.contains("NGUỒN CÂU HỎI")
                                || bodyText.contains("Nguồn câu hỏi")
                                || bodyText.contains("câu");

                boolean hasSourceBankElement =
                        d.findElements(By.xpath(
                                "//*[contains(normalize-space(.),'Ngân hàng') " +
                                        "or contains(normalize-space(.),'NGUỒN CÂU HỎI') " +
                                        "or contains(normalize-space(.),'Nguồn câu hỏi')]"
                        )).size() > 0;

                return hasQuestionBankText || hasSourceBankElement;
            });

        } catch (Exception e) {
            System.out.println("Không bắt được nguồn ngân hàng câu hỏi.");
            System.out.println("CURRENT URL = " + driver.getCurrentUrl());
            System.out.println("BODY TEXT = " + driver.findElement(By.tagName("body")).getText());
            return false;
        }
    }

    public boolean isTotalQuestionAndScoreUpdated(String expectedTotalQuestions, String expectedTotalScore) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        return shortWait.until(driver -> {
            String bodyText = driver.findElement(By.tagName("body")).getText();

            boolean hasTotalQuestion =
                    bodyText.contains("Tổng câu")
                            && bodyText.contains(expectedTotalQuestions);

            boolean hasTotalScore =
                    bodyText.contains("Tổng điểm")
                            && (
                            bodyText.contains(expectedTotalScore)
                                    || bodyText.contains(expectedTotalScore + ".00")
                    );

            System.out.println("TOTAL TEXT = " + bodyText);

            return hasTotalQuestion && hasTotalScore;
        });
    }

    public boolean isCreateExamConfirmPopupDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        return shortWait.until(driver -> {
            try {
                WebElement backdrop = driver.findElement(globalConfirmBackdrop);
                WebElement acceptButton = driver.findElement(globalConfirmAcceptButton);
                WebElement cancelButton = driver.findElement(globalConfirmCancelButton);

                String popupText = getOpenConfirmPopupText();

                System.out.println("CONFIRM POPUP TEXT = " + popupText);

                boolean hasConfirmTitle =
                        popupText.contains("Xác nhận tạo bộ đề")
                                || popupText.contains("XÁC NHẬN");

                boolean hasConfirmQuestion =
                        popupText.contains("Bạn có chắc chắn muốn tạo bộ đề không")
                                || popupText.contains("Bạn có chắc chắn muốn tiếp tục tạo bộ đề không")
                                || popupText.contains("có chắc chắn muốn tạo bộ đề");

                boolean hasActionButtons =
                        popupText.contains("Hủy")
                                && popupText.contains("Xác nhận");

                return backdrop.isDisplayed()
                        && hasConfirmTitle
                        && hasConfirmQuestion
                        && hasActionButtons
                        && acceptButton.isDisplayed()
                        && cancelButton.isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean isTotalScoreGreaterThan10ConfirmPopupDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        return shortWait.until(driver -> {
            try {
                WebElement backdrop = driver.findElement(globalConfirmBackdrop);
                String popupText = getOpenConfirmPopupText();

                System.out.println("CONFIRM POPUP TEXT = " + popupText);

                return backdrop.isDisplayed()
                        && popupText.contains("Xác nhận tạo bộ đề")
                        && (
                        popupText.contains("lớn hơn 10")
                                || popupText.contains("vượt quá 10")
                                || popupText.contains("quá 10")
                                || popupText.contains("hơn 10")
                );
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean isTotalScoreLessThan10ConfirmPopupDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        return shortWait.until(driver -> {
            try {
                WebElement backdrop = driver.findElement(globalConfirmBackdrop);
                String popupText = getOpenConfirmPopupText();

                System.out.println("CONFIRM POPUP TEXT = " + popupText);

                return backdrop.isDisplayed()
                        && popupText.contains("Xác nhận tạo bộ đề")
                        && (
                        popupText.contains("chưa đủ 10 điểm")
                                || popupText.contains("nhỏ hơn 10")
                                || popupText.contains("chưa đủ")
                                || popupText.contains("dưới 10")
                );
            } catch (Exception e) {
                return false;
            }
        });
    }

    public boolean isCreateExamSuccessfullyDisplayed() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));

        return longWait.until(driver -> {
            String currentUrl = driver.getCurrentUrl();
            String pageText = getPageAndToastText();

            System.out.println("CREATE SUCCESS URL = " + currentUrl);
            System.out.println("CREATE SUCCESS TEXT = " + pageText);

            boolean popupGone =
                    !pageText.contains("Xác nhận tạo bộ đề")
                            && !pageText.contains("Bạn có chắc chắn muốn tiếp tục tạo bộ đề không?");

            boolean hasSuccessMessage =
                    pageText.contains("Tạo bộ đề thành công")
                            || pageText.contains("Tạo đề thành công")
                            || pageText.contains("Sinh đề thành công")
                            || pageText.contains("tạo thành công");

            boolean leftCreatePage =
                    currentUrl.contains("/lecturer/exam-codes/")
                            && !currentUrl.contains("/create");

            return popupGone && (hasSuccessMessage || leftCreatePage);
        });
    }

    private void setNumberByPlusMinusButtons(By inputLocator, String expectedValue) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(inputLocator));
        scrollToElement(input);

        String inputId = input.getAttribute("id");
        int targetValue = Integer.parseInt(expectedValue);
        int currentValue = getIntegerValue(inputLocator);

        System.out.println("SET NUMBER | INPUT = " + inputId
                + " | CURRENT = " + currentValue
                + " | TARGET = " + targetValue);

        int safety = 0;

        while (currentValue < targetValue && safety < 20) {
            try {
                WebElement plusButton = driver.findElement(getPlusButtonByInputId(inputId));
                clickMatrixButton(plusButton);
                sleep(300);
            } catch (Exception e) {
                System.out.println("KHÔNG CLICK ĐƯỢC NÚT + CỦA " + inputId);
                break;
            }

            int newValue = getIntegerValue(inputLocator);

            if (newValue == currentValue) {
                System.out.println("CLICK + KHÔNG LÀM ĐỔI GIÁ TRỊ " + inputId);
                break;
            }

            currentValue = newValue;
            safety++;
        }

        while (currentValue > targetValue && safety < 40) {
            try {
                WebElement minusButton = driver.findElement(getMinusButtonByInputId(inputId));
                clickMatrixButton(minusButton);
                sleep(300);
            } catch (Exception e) {
                System.out.println("KHÔNG CLICK ĐƯỢC NÚT - CỦA " + inputId);
                break;
            }

            int newValue = getIntegerValue(inputLocator);

            if (newValue == currentValue) {
                System.out.println("CLICK - KHÔNG LÀM ĐỔI GIÁ TRỊ " + inputId);
                break;
            }

            currentValue = newValue;
            safety++;
        }

        int finalValue = getIntegerValue(inputLocator);

        if (finalValue != targetValue) {
            System.out.println("NÚT +/- KHÔNG SET ĐƯỢC " + inputId + " -> SET TRỰC TIẾP VÀO INPUT");

            ((JavascriptExecutor) driver).executeScript(
                    "const input = document.getElementById(arguments[0]);" +
                            "input.focus();" +
                            "input.value = arguments[1];" +
                            "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                            "input.dispatchEvent(new Event('blur', { bubbles: true }));",
                    inputId,
                    expectedValue
            );

            sleep(500);
        }

        finalValue = getIntegerValue(inputLocator);

        System.out.println("AFTER SET NUMBER | INPUT = " + inputId
                + " | EXPECTED = " + targetValue
                + " | ACTUAL = " + finalValue);

        if (finalValue != targetValue) {
            throw new RuntimeException("Không set được giá trị cho input "
                    + inputId + ". Expected = " + targetValue + ", Actual = " + finalValue);
        }
    }

    private int getIntegerValue(By inputLocator) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(inputLocator));
        String rawValue = input.getAttribute("value");

        if (rawValue == null || rawValue.trim().isEmpty()) {
            return 0;
        }

        rawValue = rawValue.trim();

        if (rawValue.contains(".")) {
            rawValue = rawValue.substring(0, rawValue.indexOf("."));
        }

        return Integer.parseInt(rawValue);
    }

    private By getPlusButtonByInputId(String inputId) {
        return By.xpath("//*[@id='" + inputId + "']/ancestor::*[contains(@class,'matrix') or contains(@class,'row') or contains(@class,'item') or contains(@class,'control')][1]//button[normalize-space()='+']");
    }

    private By getMinusButtonByInputId(String inputId) {
        return By.xpath("//*[@id='" + inputId + "']/ancestor::*[contains(@class,'matrix') or contains(@class,'row') or contains(@class,'item') or contains(@class,'control')][1]//button[normalize-space()='-']");
    }

    private void clickMatrixButton(WebElement button) {
        scrollToElement(button);

        try {
            button.click();
        } catch (Exception e1) {
            try {
                new Actions(driver).moveToElement(button).click().perform();
            } catch (Exception e2) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            }
        }
    }

    private String getOpenConfirmPopupText() {
        Object textObj = ((JavascriptExecutor) driver).executeScript(
                "const popup = document.querySelector('#globalConfirmBackdrop.open[aria-hidden=\"false\"]');" +
                        "return popup ? popup.innerText : '';"
        );

        return textObj == null ? "" : textObj.toString();
    }
}