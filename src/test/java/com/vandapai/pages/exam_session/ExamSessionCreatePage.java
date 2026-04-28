package com.vandapai.pages.exam_session;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class ExamSessionCreatePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Step panels
    private final By step1Panel = By.cssSelector("[data-step-panel='1']");
    private final By step2Panel = By.cssSelector("[data-step-panel='2']");
    private final By step3Panel = By.cssSelector("[data-step-panel='3']");

    // Step 1
    private final By sessionGroupName = By.id("sessionGroupName");
    private final By sessionAcademicYear = By.id("sessionAcademicYear");
    private final By sessionSemester = By.id("sessionSemester");
    private final By sessionSubjectSelect = By.id("sessionSubjectSelect");
    private final By sessionExamDate = By.id("sessionExamDate");
    private final By sessionDuration = By.id("sessionDuration");
    private final By sessionStartTime = By.id("sessionStartTime");
    private final By sessionEndTime = By.id("sessionEndTime");
    private final By sessionDescription = By.id("sessionDescription");

    // Step 2
    private final By addRoomButton = By.id("addRoomButton");
    private final By roomNameInputs = By.cssSelector("[data-room-field='room_name']");
    private final By roomStudentCountInputs = By.cssSelector("[data-room-field='student_count']");
    private final By roomPasswordInputs = By.cssSelector("[data-room-field='password']");

    // Exam picker modal
    private final By examPickerBackdrop = By.id("examPickerBackdrop");

    // Step 3
    private final By rosterPicker = By.id("rosterPicker");
    private final By addRosterButton = By.id("addRosterButton");
    private final By randomAssignButton = By.id("randomAssignButton");
    private static final int MAX_CONFLICT_RETRY_DAYS = 7;

    private WebElement getFirstElement(By locator) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, 0));
        return driver.findElements(locator).get(0);
    }

    public ExamSessionCreatePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void waitUntilCreatePageOpened() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/lecturer/create-session"),
                ExpectedConditions.urlContains("create-session")
        ));

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(sessionGroupName),
                ExpectedConditions.visibilityOfElementLocated(By.id("sessionGroupName")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(.),'Thông tin chung')]"))
        ));
    }

    public void assertStep1Displayed() {
        Assert.assertTrue(isPanelActive(step1Panel), "Step 1 - Thông tin chung chưa được active.");
        Assert.assertTrue(isDisplayed(sessionGroupName), "Không thấy ô Tên kỳ thi.");
        Assert.assertTrue(isDisplayed(sessionExamDate), "Không thấy ô Ngày diễn ra.");
        Assert.assertTrue(isDisplayed(sessionStartTime), "Không thấy ô Giờ bắt đầu.");
        Assert.assertTrue(isDisplayed(sessionEndTime), "Không thấy ô Giờ kết thúc.");
    }

    public void assertStep2Displayed() {
        Assert.assertTrue(isPanelActive(step2Panel), "Step 2 - Phòng thi & Trộn chưa được active.");
        Assert.assertTrue(isDisplayed(addRoomButton), "Không thấy nút Thêm phòng thi.");
    }

    public void assertStep3Displayed() {
        Assert.assertTrue(isPanelActive(step3Panel), "Step 3 - Phân bổ sinh viên chưa được active.");
        Assert.assertTrue(isDisplayed(rosterPicker), "Không thấy dropdown Chọn danh sách lớp.");
        Assert.assertTrue(isDisplayed(addRosterButton), "Không thấy nút Thêm danh sách.");
    }

    public void fillValidStep1(String sessionName) {
        waitUntilCreatePageOpened();

        setInputValue(sessionGroupName, sessionName);
        setSelectIfPossible(sessionAcademicYear, "2025-2026");
        setSelectIfPossible(sessionSemester, "HK1");

        setInputValue(sessionExamDate, examDate.toString());

        setInputValue(sessionStartTime, "08:00");
        setInputValue(sessionEndTime, "09:30");

        if (isDisplayed(sessionDuration)) {
            setInputValue(sessionDuration, "90");
        }

        setInputValue(sessionDescription, "Automation tạo ca thi.");
    }

    public void fillStep1WithoutName() {
        waitUntilCreatePageOpened();

        setInputValue(sessionGroupName, "");
        setInputValue(sessionExamDate, LocalDate.now().plusDays(30).toString());
        setInputValue(sessionStartTime, "08:00");
        setInputValue(sessionEndTime, "09:30");

        if (isDisplayed(sessionDuration)) {
            setInputValue(sessionDuration, "90");
        }
    }

    public void fillStep1WithInvalidTime() {
        waitUntilCreatePageOpened();

        setInputValue(sessionGroupName, "AUTO_CA_THI_INVALID_TIME");
        setInputValue(sessionExamDate, LocalDate.now().plusDays(31).toString());
        setInputValue(sessionStartTime, "10:00");
        setInputValue(sessionEndTime, "08:00");

        if (isDisplayed(sessionDuration)) {
            setInputValue(sessionDuration, "90");
        }
    }

    public void clickNext() {
        WebElement button = findVisibleButtonContains("Tiếp theo");
        clickByJs(button);
        sleep(800);
    }

    public void clickSubmitConfirm() {
        WebElement button = findVisibleButtonExactOrContains("Xác nhận");
        clickByJs(button);
        sleep(1500);
    }

    public void assertToastContains(String expectedText) {
        By toast = By.xpath(
                "//*[contains(@class,'toast') or contains(@class,'Toast') or contains(@id,'toast') or contains(@id,'Toast')]" +
                        "[contains(normalize-space(.),\"" + expectedText + "\")]"
        );

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(toast));
        } catch (TimeoutException e) {
            By fallback = By.xpath("//*[contains(normalize-space(.),\"" + expectedText + "\")]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(fallback));
        }
    }

    public void goToStep2WithValidData(String sessionName) {
        fillValidStep1(sessionName);
        clickNext();
        assertStep2Displayed();
    }

    public void addRoom() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addRoomButton));
        clickByJs(button);
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(roomNameInputs, 0));
    }

    public void fillFirstRoom(String roomName, int studentCount, String password) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(roomNameInputs, 0));

        setInputValue(roomNameInputs, 0, roomName);
        setInputValue(roomStudentCountInputs, 0, String.valueOf(studentCount));
        setInputValue(roomPasswordInputs, 0, password);
    }

    public void assertRoomCreated() {
        Assert.assertTrue(driver.findElements(roomNameInputs).size() > 0, "Chưa thêm được dòng phòng thi.");
        Assert.assertTrue(driver.findElements(roomStudentCountInputs).size() > 0, "Không thấy ô Số lượng SV.");
        Assert.assertTrue(driver.findElements(roomPasswordInputs).size() > 0, "Không thấy ô Mật khẩu.");
    }

    public void assertCannotGoStep3WhenRoomHasNoExamCode() {
        clickNext();
        assertToastContains("chưa chọn bộ đề và mã đề");
        Assert.assertTrue(isPanelActive(step2Panel), "Hệ thống đã chuyển sang Step 3 dù phòng chưa chọn mã đề.");
    }

    public void openExamCodeModalForFirstRoom() {
        By roomExamAction = By.xpath(
                "(//*[self::button or self::a]" +
                        "[contains(normalize-space(.),'Xem bộ đề') " +
                        "or contains(normalize-space(.),'Thêm đề')])[1]"
        );

        WebElement action = wait.until(ExpectedConditions.elementToBeClickable(roomExamAction));
        clickByJs(action);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(examPickerBackdrop),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'Chọn mã đề cho phòng thi')]")
                ),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'Xem bộ đề - mã đề')]")
                )
        ));
    }

    public void selectFirstExamCodeAndConfirm() {
        openExamCodeModalForFirstRoom();

        switchExamPickerToEditModeIfNeeded();

        List<WebElement> checkboxes = driver.findElements(
                By.cssSelector("#examPickerBackdrop input[type='checkbox']:not(:disabled)")
        );

        if (checkboxes.isEmpty()) {
            checkboxes = driver.findElements(
                    By.cssSelector("#examPickerBackdrop input[type='checkbox']")
            );
        }

        Assert.assertTrue(
                checkboxes.size() > 0,
                "Không tìm thấy checkbox mã đề trong popup."
        );

        clickByJs(checkboxes.get(0));

        WebElement confirmButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("confirmExamPickerButton"))
        );
        clickByJs(confirmButton);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.invisibilityOfElementLocated(examPickerBackdrop),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'Xem bộ đề') and contains(normalize-space(.),'mã đề')]")
                )
        ));
    }

    private void switchExamPickerToEditModeIfNeeded() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));

            WebElement editButton = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(normalize-space(.),'Chỉnh sửa')]")
            ));

            clickByJs(editButton);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("#examPickerBackdrop input[type='checkbox']:not(:disabled)")
                    ),
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(normalize-space(.),'Chỉnh sửa bộ đề')]")
                    )
            ));
        } catch (Exception ignored) {
            // Popup đã ở mode chọn mã đề thì bỏ qua
        }
    }

    public void assertExamCodeAssigned() {
        By viewExamCodeLink = By.xpath("//*[contains(text(),'Xem bộ đề') and contains(text(),'mã đề')]");
        Assert.assertTrue(isDisplayed(viewExamCodeLink), "Sau khi chọn mã đề, chưa thấy link Xem bộ đề - mã đề.");
    }

    public void goToStep3AfterAssignExamCode() {
        clickNext();
        assertStep3Displayed();
    }

    public void addFirstRoster() {
        assertStep3Displayed();

        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(rosterPicker));
        Select select = new Select(selectElement);

        List<WebElement> options = select.getOptions();
        Assert.assertTrue(options.size() > 1, "Không có danh sách lớp nào để chọn.");

        boolean selected = false;
        for (WebElement option : options) {
            String value = option.getAttribute("value");
            String text = option.getText();

            if (value != null && !value.isBlank() && !text.contains("Không còn")) {
                select.selectByValue(value);
                selected = true;
                break;
            }
        }

        Assert.assertTrue(selected, "Không chọn được danh sách lớp hợp lệ.");

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addRosterButton));
        clickByJs(addButton);

        sleep(1000);
    }

    public void randomAssignStudents() {
        WebElement randomButton;

        if (isDisplayed(randomAssignButton)) {
            randomButton = wait.until(ExpectedConditions.elementToBeClickable(randomAssignButton));
        } else {
            randomButton = findVisibleButtonContains("Random");
        }

        clickByJs(randomButton);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'SV2024') or contains(text(),'MSSV') or contains(text(),'Mã đề')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Chưa phân bổ sinh viên cho phòng này')]"))
        ));

        sleep(1500);
    }

    public void assertStudentsAssigned() {
        By assignedStudent = By.xpath("//*[contains(text(),'SV2024') or contains(text(),'MSSV') or contains(text(),'Mã đề')]");
        Assert.assertTrue(isDisplayed(assignedStudent), "Không thấy sinh viên được phân bổ sau khi random.");
    }

    public void createSessionEndToEnd(String sessionName, int studentCount) {
        fillValidStep1(sessionName);
        clickNext();
        assertStep2Displayed();

        addRoom();
        fillFirstRoom("Phòng A101", studentCount, "CK101");

        selectFirstExamCodeAndConfirm();
        assertExamCodeAssigned();

        goToStep3AfterAssignExamCode();

        addFirstRoster();
        randomAssignStudents();
        assertStudentsAssigned();

        clickSubmitConfirm();

        if (handleScheduleConflictIfAppears()) {
            clickBackToStep1FromStep3();
            increaseExamDateByOneDay();

            clickNext();
            assertStep2Displayed();

            clickNext();
            assertStep3Displayed();

            clickSubmitConfirm();
        }
    }

    private boolean isPanelActive(By panelLocator) {
        try {
            WebElement panel = driver.findElement(panelLocator);
            String className = panel.getAttribute("class");
            return panel.isDisplayed() && (className == null || !className.contains("es-hidden"));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void setSelectIfPossible(By locator, String value) {
        try {
            WebElement element = driver.findElement(locator);
            if (!element.isDisplayed() || !element.isEnabled()) return;

            Select select = new Select(element);
            for (WebElement option : select.getOptions()) {
                if (value.equals(option.getAttribute("value")) || value.equals(option.getText().trim())) {
                    select.selectByVisibleText(option.getText().trim());
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void setInputValue(By locator, String value) {
        setInputValue(locator, 0, value);
    }

    private void setInputValue(By locator, int index, String value) {
        for (int i = 0; i < 3; i++) {
            try {
                wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, index));
                WebElement element = driver.findElements(locator).get(index);

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});" +
                                "arguments[0].value = arguments[1];" +
                                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                        element,
                        value
                );

                sleep(300);
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                sleep(500);
            }
        }

        throw new RuntimeException("Không nhập được giá trị cho locator: " + locator);
    }

    private void setInputValue(WebElement element, String value) {
        for (int i = 0; i < 3; i++) {
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});" +
                                "arguments[0].value = arguments[1];" +
                                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                        element,
                        value
                );
                sleep(300);
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                sleep(500);
            }
        }

        throw new RuntimeException("Không nhập được giá trị do element bị stale: " + value);
    }

    private WebElement findVisibleButtonContains(String text) {
        List<WebElement> buttons = driver.findElements(By.xpath("//button[contains(normalize-space(.),'" + text + "')]"));

        for (WebElement button : buttons) {
            if (button.isDisplayed() && button.isEnabled()) {
                return button;
            }
        }

        throw new NoSuchElementException("Không tìm thấy button hiển thị chứa text: " + text);
    }

    private WebElement findVisibleButtonExactOrContains(String text) {
        List<WebElement> buttons = driver.findElements(By.xpath("//button[normalize-space(.)='" + text + "' or contains(normalize-space(.),'" + text + "')]"));

        for (WebElement button : buttons) {
            if (button.isDisplayed() && button.isEnabled()) {
                return button;
            }
        }

        throw new NoSuchElementException("Không tìm thấy button hiển thị có text: " + text);
    }

    private WebElement findVisibleButtonInsideModal(String text) {
        List<WebElement> buttons = driver.findElements(By.xpath(
                "//*[@id='examPickerBackdrop']//button[contains(normalize-space(.),'" + text + "')]"
        ));

        for (WebElement button : buttons) {
            if (button.isDisplayed() && button.isEnabled()) {
                return button;
            }
        }

        return findVisibleButtonContains(text);
    }

    private void clickByJs(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void handleConflictConfirmIfAppears() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement confirm = shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[contains(normalize-space(.),'Xác nhận')]")
            ));

            if (confirm.isDisplayed() && confirm.isEnabled()) {
                clickByJs(confirm);
                sleep(1000);
            }
        } catch (Exception ignored) {
        }
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void createSessionEndToEnd(
            String sessionName,
            String roomName,
            int studentCount,
            String roomPassword
    ) {
        fillValidStep1(sessionName);
        clickNext();
        assertStep2Displayed();

        addRoom();
        fillFirstRoom(roomName, studentCount, roomPassword);

        selectFirstExamCodeAndConfirm();
        assertExamCodeAssigned();

        goToStep3AfterAssignExamCode();

        addFirstRoster();
        randomAssignStudents();
        assertStudentsAssigned();

        clickSubmitConfirm();
        sleep(1500);

        resolveScheduleConflictByIncreasingDateUntilSuccess();

        handleConflictConfirmIfAppears();
    }

    private LocalDate examDate = LocalDate.now().plusDays(30);
    private void increaseExamDateByOneDay() {
        examDate = examDate.plusDays(1);
        setInputValue(sessionExamDate, examDate.toString());
        sleep(700);
    }
    private void resolveScheduleConflictByIncreasingDateUntilSuccess() {
        for (int attempt = 1; attempt <= MAX_CONFLICT_RETRY_DAYS; attempt++) {
            if (!handleScheduleConflictIfAppears()) {
                return;
            }

            clickBackToStep1FromStep3();
            increaseExamDateByOneDay();

            clickNext();
            assertStep2Displayed();

            clickNext();
            assertStep3Displayed();

            clickSubmitConfirm();
            sleep(1500);
        }

        Assert.fail("Đã đổi ngày thi " + MAX_CONFLICT_RETRY_DAYS + " lần nhưng vẫn bị trùng lịch thi.");
    }
    private boolean handleScheduleConflictIfAppears() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

            WebElement conflictModal = shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(normalize-space(.),'Trùng lịch thi')]")
            ));

            if (!conflictModal.isDisplayed()) {
                return false;
            }

            WebElement changeTimeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(normalize-space(.),'Tôi sẽ đổi giờ thi')]")
            ));

            clickByJs(changeTimeButton);
            sleep(1000);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void clickBackToStep1FromStep3() {
        WebElement backButtonStep3 = findVisibleButtonContains("Quay lại");
        clickByJs(backButtonStep3);
        sleep(800);
        assertStep2Displayed();

        WebElement backButtonStep2 = findVisibleButtonContains("Quay lại");
        clickByJs(backButtonStep2);
        sleep(800);
        assertStep1Displayed();
    }
}