package com.vandapai.tests.exam_session;

import com.vandapai.base.BaseTest;
import com.vandapai.pages.exam_session.ExamSessionCreatePage;
import com.vandapai.pages.exam_session.ExamSessionManagementPage;
import com.vandapai.pages.exam_session.ExamSessionDetailPage;
import com.vandapai.pages.exam_session.ExamSessionCommonListPage;
import com.vandapai.pages.login.LoginPage;
import com.vandapai.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * ExamSessionTest: Bộ test case cho chức năng Quản lý ca thi.
 * Bao gồm: tạo ca thi, tìm kiếm, lọc theo trạng thái,
 * chỉnh sửa, xóa ca thi, xem danh sách chung.
 */
public class ExamSessionTest extends BaseTest {

    private ExamSessionManagementPage examSessionPage;

    // =========================
    // SETUP METHOD
    // =========================

    /**
     * Chạy trước mỗi test case: đăng nhập và điều hướng đến trang Quản lý ca thi.
     */
    @BeforeMethod
    public void loginAndGoToExamSessionPage() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.get("username"),
                ConfigReader.get("password")
        );

        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.goToExamSessionPage();
    }

    // =========================
    // TEST CASES
    // =========================

    /**
     * AT_QLCT_001: Điều hướng vào màn hình Quản lý ca thi thành công.
     */
    @Test(priority = 1)
    public void AT_QLCT_001_GoToExamSessionManagementPageSuccessfully() {
        Assert.assertTrue(
                examSessionPage.isAtExamSessionManagementPage(),
                "Không điều hướng được vào màn hình Quản lý ca thi."
        );

        examSessionPage.assertMainElementsDisplayed();
    }

    /**
     * AT_QLCT_002: Tìm kiếm ca thi theo tên.
     */
    @Test(priority = 2)
    public void AT_QLCT_002_SearchExamSessionByName() {
        String positiveKeyword = "Thi";
        String negativeKeyword = "XYZABC123";

        // Tìm kiếm với từ khóa có kết quả
        examSessionPage.searchSession(positiveKeyword);
        examSessionPage.assertSearchHasResult(positiveKeyword);

        // Tìm kiếm với từ khóa không có kết quả
        examSessionPage.searchSession(negativeKeyword);
        examSessionPage.assertSearchNoResult();
    }

    /**
     * AT_QLCT_003: Lọc ca thi theo trạng thái.
     */
    @Test(priority = 3)
    public void AT_QLCT_003_FilterExamSessionByStatus() {
        // Lọc theo trạng thái "Sắp diễn ra" (SCHEDULED)
        examSessionPage.selectStatusByValue("SCHEDULED");
        examSessionPage.assertAllRowsContainStatusText("Sắp diễn ra");
    }

    /**
     * AT_QLCT_004: Mở màn hình Tạo ca thi thành công.
     */
    @Test(priority = 4)
    public void AT_QLCT_004_OpenCreateExamSessionPageSuccessfully() {
        // Click nút Tạo ca thi
        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();
        createPage.assertStep1Displayed();
    }

    /**
     * AT_QLCT_005: Kiểm tra validation dữ liệu bắt buộc ở Step 1.
     * - Thiếu tên ca thi.
     * - Thời gian kết thúc trước thời gian bắt đầu.
     */
    @Test(priority = 5)
    public void AT_QLCT_005_ValidateStep1RequiredData() {
        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();

        // Test case 1: Để trống tên kỳ thi
        createPage.fillStep1WithoutName();
        createPage.clickNext();
        createPage.assertToastContains("Vui lòng nhập tên kỳ thi");

        // Test case 2: Nhập thời gian không hợp lệ (kết thúc < bắt đầu)
        createPage.fillStep1WithInvalidTime();
        createPage.clickNext();
        createPage.assertToastContains("Thời gian kết thúc phải sau thời gian bắt đầu");
    }

    /**
     * AT_QLCT_006: Không thể chuyển sang Step 3 khi phòng thi chưa chọn mã đề.
     */
    @Test(priority = 6)
    public void AT_QLCT_006_CannotGoToStep3WhenRoomHasNoExamCode() {
        String sessionName = "AUTO_CA_THI_NO_CODE_" + System.currentTimeMillis();

        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.goToStep2WithValidData(sessionName);

        // Thêm phòng nhưng chưa chọn mã đề
        createPage.addRoom();
        createPage.assertRoomCreated();
        createPage.fillFirstRoom("Phòng A101", 10, "CK101");

        // Kiểm tra không thể chuyển sang Step 3
        createPage.assertCannotGoStep3WhenRoomHasNoExamCode();
    }

    /**
     * AT_QLCT_007: Gán mã đề cho phòng thi thành công.
     */
    @Test(priority = 7)
    public void AT_QLCT_007_AssignExamCodeForRoomSuccessfully() {
        String sessionName = "AUTO_CA_THI_ASSIGN_CODE_" + System.currentTimeMillis();

        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.goToStep2WithValidData(sessionName);

        // Thêm phòng và chọn mã đề
        createPage.addRoom();
        createPage.fillFirstRoom("Phòng A101", 10, "CK101");

        createPage.selectFirstExamCodeAndConfirm();
        createPage.assertExamCodeAssigned();
    }

    /**
     * AT_QLCT_008: Tạo ca thi thành công end-to-end.
     */
    @Test(priority = 8)
    public void AT_QLCT_008_CreateExamSessionSuccessfullyEndToEnd() {
        String sessionName = "AUTO_CA_THI_" + System.currentTimeMillis();
        int studentCount = 30;

        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();
        createPage.createSessionEndToEnd(
                sessionName,
                "Phòng A101",
                studentCount,
                "CK101"
        );

        // Quay lại trang danh sách và kiểm tra ca thi vừa tạo hiển thị
        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.assertCreatedSessionDisplayed(sessionName);
    }

    /**
     * AT_QLCT_009: Tạo ca thi -> Tìm kiếm -> Mở chi tiết -> Xem danh sách chung thành công.
     */
    @Test(priority = 9)
    public void AT_QLCT_009_CreateSearchOpenDetailAndViewCommonListSuccessfully() {
        String sessionName = "AUTO_CA_THI_VIEW_" + System.currentTimeMillis();

        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();

        createPage.createSessionEndToEnd(
                sessionName,
                "Phòng A101",
                40,
                "CK101"
        );

        // Tìm kiếm và mở ca thi vừa tạo
        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.searchAndOpenSessionByName(sessionName);

        // Kiểm tra màn hình chi tiết
        ExamSessionDetailPage detailPage = new ExamSessionDetailPage(driver);
        detailPage.assertDetailDisplayedCorrectly(sessionName);

        // Click nút "Xem danh sách chung"
        detailPage.clickViewCommonList();

        // Kiểm tra màn hình danh sách chung
        ExamSessionCommonListPage commonListPage = new ExamSessionCommonListPage(driver);
        commonListPage.assertCommonListDisplayedCorrectly(sessionName);
    }

    /**
     * AT_QLCT_010: Chỉnh sửa ca thi đã tạo thành công.
     */
    @Test(priority = 10)
    public void AT_QLCT_010_EditCreatedExamSessionSuccessfully() {
        String sessionName = "AUTO_CA_THI_EDIT_SRC_" + System.currentTimeMillis();

        // Tạo ca thi mới
        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();
        createPage.createSessionEndToEnd(
                sessionName,
                "Phòng A101",
                40,
                "CK101"
        );

        // Tìm kiếm và mở ca thi vừa tạo
        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.searchAndOpenSessionByName(sessionName);

        // Kiểm tra màn hình chi tiết
        ExamSessionDetailPage detailPage = new ExamSessionDetailPage(driver);
        detailPage.assertDetailDisplayedCorrectly(sessionName);

        // Click nút Chỉnh sửa
        detailPage.clickEditButton();

        // Kiểm tra đã vào màn hình chỉnh sửa
        ExamSessionCreatePage editPage = new ExamSessionCreatePage(driver);
        editPage.waitUntilCreatePageOpened();

        Assert.assertTrue(
                driver.getPageSource().contains("Chỉnh sửa ca thi")
                        || driver.getCurrentUrl().contains("mode=edit"),
                "Không mở được màn hình Chỉnh sửa ca thi."
        );
    }

    /**
     * AT_QLCT_011: Xóa ca thi đã tạo thành công.
     */
    @Test(priority = 11)
    public void AT_QLCT_011_DeleteCreatedExamSessionSuccessfully() {
        String sessionName = "AUTO_CA_THI_DELETE_" + System.currentTimeMillis();

        // Tạo ca thi mới
        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();
        createPage.createSessionEndToEnd(
                sessionName,
                "Phòng A101",
                40,
                "CK101"
        );

        // Tìm kiếm và mở ca thi vừa tạo
        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.searchAndOpenSessionByName(sessionName);

        // Kiểm tra màn hình chi tiết
        ExamSessionDetailPage detailPage = new ExamSessionDetailPage(driver);
        detailPage.assertDetailDisplayedCorrectly(sessionName);

        // Xóa ca thi
        detailPage.deleteSession();

        // Kiểm tra ca thi không còn trong danh sách sau khi xóa
        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.assertSessionNotDisplayedAfterSearch(sessionName);
    }
}