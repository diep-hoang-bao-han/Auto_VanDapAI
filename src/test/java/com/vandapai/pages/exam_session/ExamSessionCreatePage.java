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

/**
 * ExamSessionCreatePage: Xử lý các thao tác trên màn hình Tạo ca thi (gồm 3 bước).
 * - Step 1: Thông tin chung (tên ca thi, năm học, học kỳ, ngày thi, giờ bắt đầu/kết thúc,...)
 * - Step 2: Phòng thi & Trộn (thêm phòng, chọn mã đề cho từng phòng)
 * - Step 3: Phân bổ sinh viên (chọn danh sách lớp, random phân bổ)
 */
public class ExamSessionCreatePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // =========================
    // STEP PANELS LOCATORS
    // =========================
    private final By step1Panel = By.cssSelector("[data-step-panel='1']");
    private final By step2Panel = By.cssSelector("[data-step-panel='2']");
    private final By step3Panel = By.cssSelector("[data-step-panel='3']");

    // =========================
    // STEP 1 LOCATORS - THÔNG TIN CHUNG
    // =========================
    private final By sessionGroupName = By.id("sessionGroupName");
    private final By sessionAcademicYear = By.id("sessionAcademicYear");
    private final By sessionSemester = By.id("sessionSemester");
    private final By sessionSubjectSelect = By.id("sessionSubjectSelect");
    private final By sessionExamDate = By.id("sessionExamDate");
    private final By sessionDuration = By.id("sessionDuration");
    private final By sessionStartTime = By.id("sessionStartTime");
    private final By sessionEndTime = By.id("sessionEndTime");
    private final By sessionDescription = By.id("sessionDescription");

    // =========================
    // STEP 2 LOCATORS - PHÒNG THI
    // =========================
    private final By addRoomButton = By.id("addRoomButton");
    private final By roomNameInputs = By.cssSelector("[data-room-field='room_name']");
    private final By roomStudentCountInputs = By.cssSelector("[data-room-field='student_count']");
    private final By roomPasswordInputs = By.cssSelector("[data-room-field='password']");

    // =========================
    // EXAM PICKER MODAL LOCATORS
    // =========================
    private final By examPickerBackdrop = By.id("examPickerBackdrop");

    // =========================
    // STEP 3 LOCATORS - PHÂN BỔ SINH VIÊN
    // =========================
    private final By rosterPicker = By.id("rosterPicker");
    private final By addRosterButton = By.id("addRosterButton");
    private final By randomAssignButton = By.id("randomAssignButton");

    // =========================
    // CONSTANTS
    // =========================
    private static final int MAX_CONFLICT_RETRY_DAYS = 7;

    // Biến lưu ngày thi hiện tại, dùng cho xử lý conflict
    private LocalDate examDate = LocalDate.now().plusDays(30);

    // =========================
    // CONSTRUCTOR
    // =========================
    public ExamSessionCreatePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Lấy element đầu tiên từ locator (đợi có ít nhất 1 element).
     */
    private WebElement getFirstElement(By locator) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, 0));
        return driver.findElements(locator).get(0);
    }

    /**
     * Kiểm tra panel có đang active hay không.
     */
    private boolean isPanelActive(By panelLocator) {
        try {
            WebElement panel = driver.findElement(panelLocator);
            String className = panel.getAttribute("class");
            return panel.isDisplayed() && (className == null || !className.contains("es-hidden"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra element có hiển thị hay không.
     */
    private boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Chọn giá trị trong dropdown select nếu có thể.
     */
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

    /**
     * Nhập giá trị vào input (có thể theo index nếu có nhiều element cùng locator).
     */
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

    /**
     * Tìm button hiển thị có chứa text (không phân biệt hoa thường).
     */
    private WebElement findVisibleButtonContains(String text) {
        List<WebElement> buttons = driver.findElements(By.xpath("//button[contains(normalize-space(.),'" + text + "')]"));

        for (WebElement button : buttons) {
            if (button.isDisplayed() && button.isEnabled()) {
                return button;
            }
        }

        throw new NoSuchElementException("Không tìm thấy button hiển thị chứa text: " + text);
    }

    /**
     * Tìm button hiển thị có text chính xác hoặc chứa text.
     */
    private WebElement findVisibleButtonExactOrContains(String text) {
        List<WebElement> buttons = driver.findElements(By.xpath("//button[normalize-space(.)='" + text + "' or contains(normalize-space(.),'" + text + "')]"));

        for (WebElement button : buttons) {
            if (button.isDisplayed() && button.isEnabled()) {
                return button;
            }
        }

        throw new NoSuchElementException("Không tìm thấy button hiển thị có text: " + text);
    }

    /**
     * Tìm button hiển thị bên trong modal chọn mã đề.
     */
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

    /**
     * Click vào element bằng JS (fallback khi click thông thường lỗi).
     */
    private void clickByJs(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Xử lý popup xác nhận conflict nếu xuất hiện.
     */
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

    /**
     * Sleep theo milliseconds.
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // =========================
    // WAIT & VERIFICATION METHODS
    // =========================

    /**
     * Đợi cho đến khi màn hình Tạo ca thi được mở.
     * - URL chứa "create-session".
     * - Các element cơ bản hiển thị.
     */
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

    /**
     * Kiểm tra Step 1 (Thông tin chung) đang được hiển thị.
     */
    public void assertStep1Displayed() {
        Assert.assertTrue(isPanelActive(step1Panel), "Step 1 - Thông tin chung chưa được active.");
        Assert.assertTrue(isDisplayed(sessionGroupName), "Không thấy ô Tên kỳ thi.");
        Assert.assertTrue(isDisplayed(sessionExamDate), "Không thấy ô Ngày diễn ra.");
        Assert.assertTrue(isDisplayed(sessionStartTime), "Không thấy ô Giờ bắt đầu.");
        Assert.assertTrue(isDisplayed(sessionEndTime), "Không thấy ô Giờ kết thúc.");
    }

    /**
     * Kiểm tra Step 2 (Phòng thi & Trộn) đang được hiển thị.
     */
    public void assertStep2Displayed() {
        Assert.assertTrue(isPanelActive(step2Panel), "Step 2 - Phòng thi & Trộn chưa được active.");
        Assert.assertTrue(isDisplayed(addRoomButton), "Không thấy nút Thêm phòng thi.");
    }

    /**
     * Kiểm tra Step 3 (Phân bổ sinh viên) đang được hiển thị.
     */
    public void assertStep3Displayed() {
        Assert.assertTrue(isPanelActive(step3Panel), "Step 3 - Phân bổ sinh viên chưa được active.");
        Assert.assertTrue(isDisplayed(rosterPicker), "Không thấy dropdown Chọn danh sách lớp.");
        Assert.assertTrue(isDisplayed(addRosterButton), "Không thấy nút Thêm danh sách.");
    }

    /**
     * Kiểm tra toast message có chứa text mong đợi.
     */
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

    // =========================
    // STEP 1 METHODS
    // =========================

    /**
     * Điền thông tin hợp lệ cho Step 1.
     * @param sessionName Tên ca thi
     */
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

    /**
     * Điền Step 1 nhưng bỏ trống tên ca thi (dùng cho test validation).
     */
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

    /**
     * Điền Step 1 với thời gian không hợp lệ (giờ kết thúc < giờ bắt đầu).
     */
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

    /**
     * Click nút "Tiếp theo".
     */
    public void clickNext() {
        WebElement button = findVisibleButtonContains("Tiếp theo");
        clickByJs(button);
        sleep(800);
    }

    /**
     * Click nút "Xác nhận" để hoàn tất tạo ca thi.
     */
    public void clickSubmitConfirm() {
        WebElement button = findVisibleButtonExactOrContains("Xác nhận");
        clickByJs(button);
        sleep(1500);
    }

    /**
     * Điền Step 1 với dữ liệu hợp lệ và chuyển sang Step 2.
     * @param sessionName Tên ca thi
     */
    public void goToStep2WithValidData(String sessionName) {
        fillValidStep1(sessionName);
        clickNext();
        assertStep2Displayed();
    }

    // =========================
    // STEP 2 METHODS - PHÒNG THI
    // =========================

    /**
     * Thêm một phòng thi mới.
     */
    public void addRoom() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addRoomButton));
        clickByJs(button);
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(roomNameInputs, 0));
    }

    /**
     * Điền thông tin cho phòng thi đầu tiên.
     * @param roomName Tên phòng
     * @param studentCount Số lượng sinh viên
     * @param password Mật khẩu phòng thi
     */
    public void fillFirstRoom(String roomName, int studentCount, String password) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(roomNameInputs, 0));

        setInputValue(roomNameInputs, 0, roomName);
        setInputValue(roomStudentCountInputs, 0, String.valueOf(studentCount));
        setInputValue(roomPasswordInputs, 0, password);
    }

    /**
     * Kiểm tra phòng thi đã được thêm thành công.
     */
    public void assertRoomCreated() {
        Assert.assertTrue(driver.findElements(roomNameInputs).size() > 0, "Chưa thêm được dòng phòng thi.");
        Assert.assertTrue(driver.findElements(roomStudentCountInputs).size() > 0, "Không thấy ô Số lượng SV.");
        Assert.assertTrue(driver.findElements(roomPasswordInputs).size() > 0, "Không thấy ô Mật khẩu.");
    }

    /**
     * Kiểm tra không thể chuyển sang Step 3 khi phòng chưa chọn mã đề.
     */
    public void assertCannotGoStep3WhenRoomHasNoExamCode() {
        clickNext();
        assertToastContains("chưa chọn bộ đề và mã đề");
        Assert.assertTrue(isPanelActive(step2Panel), "Hệ thống đã chuyển sang Step 3 dù phòng chưa chọn mã đề.");
    }

    // =========================
    // STEP 2 METHODS - CHỌN MÃ ĐỀ CHO PHÒNG
    // =========================

    /**
     * Mở modal chọn mã đề cho phòng thi đầu tiên.
     */
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

    /**
     * Chọn mã đề đầu tiên trong danh sách và xác nhận.
     */
    public void selectFirstExamCodeAndConfirm() {
        openExamCodeModalForFirstRoom();

        // Chuyển sang mode chỉnh sửa nếu cần
        switchExamPickerToEditModeIfNeeded();

        // Lấy tất cả checkbox không bị disabled
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

        // Chọn checkbox đầu tiên
        clickByJs(checkboxes.get(0));

        // Click nút xác nhận
        WebElement confirmButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("confirmExamPickerButton"))
        );
        clickByJs(confirmButton);

        // Đợi modal đóng
        wait.until(ExpectedConditions.or(
                ExpectedConditions.invisibilityOfElementLocated(examPickerBackdrop),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'Xem bộ đề') and contains(normalize-space(.),'mã đề')]")
                )
        ));
    }

    /**
     * Chuyển popup chọn mã đề sang mode chỉnh sửa (nếu đang ở mode view).
     */
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

    /**
     * Kiểm tra mã đề đã được gán cho phòng thi.
     */
    public void assertExamCodeAssigned() {
        By viewExamCodeLink = By.xpath("//*[contains(text(),'Xem bộ đề') and contains(text(),'mã đề')]");
        Assert.assertTrue(isDisplayed(viewExamCodeLink), "Sau khi chọn mã đề, chưa thấy link Xem bộ đề - mã đề.");
    }

    /**
     * Chuyển sang Step 3 sau khi đã gán mã đề cho phòng.
     */
    public void goToStep3AfterAssignExamCode() {
        clickNext();
        assertStep3Displayed();
    }

    // =========================
    // STEP 3 METHODS - PHÂN BỔ SINH VIÊN
    // =========================

    /**
     * Thêm danh sách lớp đầu tiên có sẵn (không phải option "Không còn").
     */
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

    /**
     * Random phân bổ sinh viên vào các phòng thi.
     */
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

    /**
     * Kiểm tra sinh viên đã được phân bổ.
     */
    public void assertStudentsAssigned() {
        By assignedStudent = By.xpath("//*[contains(text(),'SV2024') or contains(text(),'MSSV') or contains(text(),'Mã đề')]");
        Assert.assertTrue(isDisplayed(assignedStudent), "Không thấy sinh viên được phân bổ sau khi random.");
    }

    // =========================
    // END-TO-END FLOW METHODS
    // =========================

    /**
     * Tạo ca thi hoàn chỉnh từ đầu đến cuối (tự động xử lý conflict lịch thi).
     * @param sessionName Tên ca thi
     * @param studentCount Số lượng sinh viên
     */
    public void createSessionEndToEnd(String sessionName, int studentCount) {
        // Step 1
        fillValidStep1(sessionName);
        clickNext();
        assertStep2Displayed();

        // Step 2: Thêm phòng và chọn mã đề
        addRoom();
        fillFirstRoom("Phòng A101", studentCount, "CK101");

        selectFirstExamCodeAndConfirm();
        assertExamCodeAssigned();

        // Chuyển sang Step 3
        goToStep3AfterAssignExamCode();

        // Step 3: Phân bổ sinh viên
        addFirstRoster();
        randomAssignStudents();
        assertStudentsAssigned();

        // Submit
        clickSubmitConfirm();

        // Xử lý conflict nếu có
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

    /**
     * Tạo ca thi hoàn chỉnh với tham số đầy đủ (tên phòng, mật khẩu).
     */
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

    // =========================
    // CONFLICT HANDLING METHODS
    // =========================

    /**
     * Tăng ngày thi lên 1 ngày.
     */
    private void increaseExamDateByOneDay() {
        examDate = examDate.plusDays(1);
        setInputValue(sessionExamDate, examDate.toString());
        sleep(700);
    }

    /**
     * Xử lý conflict lịch thi: nếu popup trùng lịch xuất hiện thì click "Tôi sẽ đổi giờ thi".
     * @return true nếu có conflict và đã xử lý, false nếu không có conflict
     */
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

    /**
     * Tự động giải quyết conflict bằng cách tăng ngày thi tối đa MAX_CONFLICT_RETRY_DAYS lần.
     */
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

    /**
     * Quay lại Step 1 từ Step 3 (click "Quay lại" hai lần).
     */
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