package com.vandapai.tests.exam;

import com.vandapai.base.BaseTest;
import com.vandapai.components.SidebarComponent;
import com.vandapai.pages.exam.ExamCreatePage;
import com.vandapai.pages.exam.ExamDetailPage;
import com.vandapai.pages.exam.ExamManagementPage;
import com.vandapai.pages.login.LoginPage;
import com.vandapai.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * ExamManagementTest: Bộ test case cho chức năng Quản lý đề thi.
 * Bao gồm: tạo đề thi, validation, chỉnh sửa mã đề, duyệt đề, xóa mã đề.
 */
public class ExamManagementTest extends BaseTest {

    // Lưu tên bộ đề vừa tạo để dùng cho các test case sau
    private static String createdExamSetName;

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Đăng nhập và điều hướng đến trang Quản lý đề thi.
     * @return ExamManagementPage object
     */
    private ExamManagementPage goToExamManagementPage() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        ExamManagementPage examManagementPage = new ExamManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openExamManagement();

        return examManagementPage;
    }

    // =========================
    // DATA PROVIDERS
    // =========================

    /**
     * DataProvider cung cấp các giá trị năm học không hợp lệ.
     */
    @DataProvider(name = "invalidAcademicYearData")
    public Object[][] invalidAcademicYearData() {
        return new Object[][]{
                {"2026"},
                {"2026-2027a"},
                {"abc"}
        };
    }

    // =========================
    // TEST CASES
    // =========================

    /**
     * AT_QLDT_001: Điều hướng sang màn hình Tạo đề thi thành công.
     */
    @Test(priority = 1)
    public void AT_QLDT_001_NavigateToCreateExamSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        // Click nút Tạo đề thi
        examManagementPage.clickCreateExamButton();

        // Kiểm tra đã vào đúng màn hình Tạo đề thi
        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        // Kiểm tra form tạo đề thi hiển thị đầy đủ
        Assert.assertTrue(
                examCreatePage.isCreateExamFormDisplayed(),
                "Màn hình Tạo đề thi không hiển thị đầy đủ các khu vực cần thiết"
        );
    }

    /**
     * AT_QLDT_002: Không cho phép tạo đề thi khi để trống Mã bộ đề.
     */
    @Test(priority = 2)
    public void AT_QLDT_002_DoNotAllowCreateExamWhenExamSetNameIsEmpty() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        // Điền các thông tin cần thiết, để trống Mã bộ đề
        examCreatePage.selectSubjectByValue("1");
        examCreatePage.leaveExamSetNameEmpty();
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();
        examCreatePage.configValidExamMatrix();
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        // Click nút Tiến hành
        examCreatePage.clickProceedButton();

        // Kiểm tra validation yêu cầu nhập Mã bộ đề
        Assert.assertTrue(
                examCreatePage.isExamSetNameRequiredMessageDisplayed(),
                "Không hiển thị validation yêu cầu nhập Mã bộ đề khi để trống"
        );

        examCreatePage.waitForUserToSeeToast();
    }

    /**
     * AT_QLDT_003: Không cho phép tạo đề thi khi định dạng Năm học không hợp lệ.
     * Sử dụng DataProvider với nhiều giá trị năm học sai.
     */
    @Test(priority = 3, dataProvider = "invalidAcademicYearData")
    public void AT_QLDT_003_DoNotAllowCreateExamWhenAcademicYearFormatIsInvalid(String invalidAcademicYear) {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.refreshCreateExamPage();

        examCreatePage.selectSubjectByValue("1");

        examCreatePage.enterExamSetName(
                "AUTO_NAM_HOC_SAI_" + invalidAcademicYear.replaceAll("[^a-zA-Z0-9]", "")
        );

        examCreatePage.enterAcademicYear(invalidAcademicYear);
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();
        examCreatePage.configValidExamMatrix();
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        examCreatePage.printCreateExamFormDebug();
        examCreatePage.clickProceedButton();

        // Kiểm tra validation năm học không hợp lệ
        Assert.assertTrue(
                examCreatePage.isAcademicYearInvalidMessageDisplayed(),
                "Không hiển thị validation năm học không hợp lệ với dữ liệu: " + invalidAcademicYear
        );

        examCreatePage.waitForUserToSeeToast();
    }

    /**
     * AT_QLDT_004: Thêm nguồn ngân hàng câu hỏi thành công.
     */
    @Test(priority = 4)
    public void AT_QLDT_004_AddQuestionBankSourceSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.selectFirstAvailableQuestionBankSource();

        // Kiểm tra nguồn câu hỏi đã được thêm
        Assert.assertTrue(
                examCreatePage.isQuestionBankSourceDisplayed(),
                "Nguồn câu hỏi không được thêm hoặc không hiển thị đúng"
        );

        examCreatePage.waitForUserToSeeToast();
    }

    /**
     * AT_QLDT_005: Tổng câu và Tổng điểm cập nhật đúng khi thay đổi ma trận.
     */
    @Test(priority = 5)
    public void AT_QLDT_005_TotalQuestionAndScoreUpdatedCorrectlyWhenMatrixChanged() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.selectFirstAvailableQuestionBankSource();

        // Cấu hình ma trận: 2 Dễ, 2 TB, 1 Khó; điểm: 2, 3, 0 => tổng 5 câu, 10 điểm
        examCreatePage.configExamMatrix("2", "2", "1", "2", "3", "0");

        // Kiểm tra tổng câu = 5, tổng điểm = 10
        Assert.assertTrue(
                examCreatePage.isTotalQuestionAndScoreUpdated("5", "10"),
                "Tổng câu hoặc Tổng điểm không cập nhật đúng theo ma trận đề thi"
        );

        examCreatePage.waitForUserToSeeToast();
    }

    /**
     * AT_QLDT_006: Hiển thị popup xác nhận khi tổng điểm lớn hơn 10.
     */
    @Test(priority = 6)
    public void AT_QLDT_006_ShowConfirmPopupWhenTotalScoreGreaterThan10() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_SCORE_GT10");

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.enterExamSetName(examSetName);
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();

        // Cấu hình ma trận: tổng điểm = 12 (>10)
        examCreatePage.configExamMatrix("2", "2", "1", "2", "3", "2");
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        Assert.assertTrue(
                examCreatePage.isTotalQuestionAndScoreUpdated("5", "12"),
                "Tổng điểm chưa hiển thị lớn hơn 10 trước khi tạo đề"
        );

        examCreatePage.clickProceedButton();

        // Kiểm tra popup xác nhận tổng điểm > 10
        Assert.assertTrue(
                examCreatePage.isTotalScoreGreaterThan10ConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận khi tổng điểm lớn hơn 10"
        );

        examCreatePage.clickCancelConfirmPopup();
    }

    /**
     * AT_QLDT_007: Hiển thị popup xác nhận khi tổng điểm nhỏ hơn 10.
     */
    @Test(priority = 7)
    public void AT_QLDT_007_ShowConfirmPopupWhenTotalScoreLessThan10() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_SCORE_LT10");

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.enterExamSetName(examSetName);
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();

        // Cấu hình ma trận: tổng điểm = 8 (<10)
        examCreatePage.configExamMatrix("2", "1", "1", "2", "2", "2");
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        Assert.assertTrue(
                examCreatePage.isTotalQuestionAndScoreUpdated("4", "8"),
                "Tổng điểm chưa hiển thị nhỏ hơn 10 trước khi tạo đề"
        );

        examCreatePage.clickProceedButton();

        // Kiểm tra popup xác nhận tổng điểm < 10
        Assert.assertTrue(
                examCreatePage.isTotalScoreLessThan10ConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận khi tổng điểm nhỏ hơn 10"
        );

        examCreatePage.clickCancelConfirmPopup();
    }

    /**
     * AT_QLDT_008: Tạo đề thi thành công với dữ liệu hợp lệ.
     */
    @Test(priority = 8)
    public void AT_QLDT_008_CreateExamSuccessfullyWithValidData() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        createdExamSetName = examManagementPage.generateExamSetName("AUTO_CREATE_EXAM");

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        // Điền đầy đủ thông tin hợp lệ
        examCreatePage.selectSubjectByValue("1");
        examCreatePage.enterExamSetName(createdExamSetName);
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();
        examCreatePage.configValidExamMatrix();
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        examCreatePage.clickProceedButton();

        // Kiểm tra popup xác nhận hiển thị
        Assert.assertTrue(
                examCreatePage.isCreateExamConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận tạo bộ đề"
        );

        examCreatePage.clickAcceptConfirmPopup();

        // Kiểm tra tạo đề thành công
        Assert.assertTrue(
                examCreatePage.isCreateExamSuccessfullyDisplayed(),
                "Hệ thống chưa tạo bộ đề thành công thật sự"
        );

        // Mở bộ đề vừa tạo và kiểm tra
        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề sau khi tạo thành công"
        );
    }

    /**
     * AT_QLDT_009: Tìm kiếm bộ đề và mở chi tiết thành công.
     */
    @Test(priority = 9)
    public void AT_QLDT_009_SearchExamSetAndOpenDetailSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_SEARCH_DETAIL_EXAM");

        // Tạo bộ đề mới
        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.enterExamSetName(examSetName);
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();
        examCreatePage.configValidExamMatrix();
        examCreatePage.enterNumberOfExamCodes("4");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        examCreatePage.clickProceedButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận tạo bộ đề"
        );

        examCreatePage.clickAcceptConfirmPopup();

        Assert.assertTrue(
                examCreatePage.isCreateExamSuccessfullyDisplayed(),
                "Hệ thống chưa tạo bộ đề thành công thật sự"
        );

        // Tìm kiếm bộ đề vừa tạo
        examManagementPage.openExamSetListPage();
        examManagementPage.searchExamSet(examSetName);

        Assert.assertTrue(
                examManagementPage.isExamSetDisplayedInList(examSetName),
                "Không tìm thấy bộ đề vừa tạo trong danh sách sau khi tìm kiếm: " + examSetName
        );

        // Mở kết quả tìm kiếm đầu tiên
        examManagementPage.openFirstExamSetSearchResult();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailDisplayed(examSetName),
                "Màn hình chi tiết bộ đề thi không hiển thị đúng thông tin: " + examSetName
        );
    }

    /**
     * AT_QLDT_010: Cập nhật mã đề đầu tiên và câu hỏi đầu tiên thành công.
     */
    @Test(priority = 10)
    public void AT_QLDT_010_UpdateFirstExamCodeAndFirstQuestionSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_UPDATE_EXAM");

        // Tạo bộ đề mới (đảm bảo bộ đề chưa được sử dụng)
        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.enterExamSetName(examSetName);
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();
        examCreatePage.configValidExamMatrix();
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        examCreatePage.clickProceedButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận tạo bộ đề"
        );

        examCreatePage.clickAcceptConfirmPopup();

        Assert.assertTrue(
                examCreatePage.isCreateExamSuccessfullyDisplayed(),
                "Hệ thống chưa tạo bộ đề thành công thật sự"
        );

        // Mở bộ đề vừa tạo
        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề vừa tạo"
        );

        // Mở mã đề đầu tiên để chỉnh sửa
        examDetailPage.openFirstExamCodeCard();

        Assert.assertTrue(
                examDetailPage.isExamCodeEditorDisplayed(),
                "Không hiển thị panel Biên tập Mã đề"
        );

        // Cập nhật tên mã đề và nội dung câu hỏi
        String newExamCodeName = "9" + (System.currentTimeMillis() % 10000);
        String newFirstQuestionContent = "Data warehouse là gì?";

        examDetailPage.updateExamCodeName(newExamCodeName);
        examDetailPage.updateFirstQuestionContent(newFirstQuestionContent);
        examDetailPage.clickSaveCodeButton();

        Assert.assertTrue(
                examDetailPage.isSaveExamCodeSuccessDisplayed(),
                "Không hiển thị thông báo lưu thay đổi mã đề thành công"
        );

        // Refresh và kiểm tra lại
        driver.navigate().refresh();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Sau khi refresh, màn hình chi tiết bộ đề không hiển thị"
        );

        examDetailPage.openFirstExamCodeCard();

        Assert.assertTrue(
                examDetailPage.isEditedExamCodeDataDisplayed(newExamCodeName, newFirstQuestionContent),
                "Thông tin mã đề hoặc nội dung câu hỏi đầu tiên chưa được lưu đúng sau khi mở lại"
        );
    }

    /**
     * AT_QLDT_011: Duyệt tất cả mã đề và kiểm tra bộ đề đã được duyệt.
     */
    @Test(priority = 11)
    public void AT_QLDT_011_ApproveAllExamCodesAndVerifyExamSetApproved() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_APPROVE_EXAM");

        // Tạo bộ đề mới
        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.enterExamSetName(examSetName);
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();
        examCreatePage.configValidExamMatrix();
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        examCreatePage.clickProceedButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận tạo bộ đề"
        );

        examCreatePage.clickAcceptConfirmPopup();

        Assert.assertTrue(
                examCreatePage.isCreateExamSuccessfullyDisplayed(),
                "Hệ thống chưa tạo bộ đề thành công thật sự"
        );

        // Mở bộ đề vừa tạo
        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề vừa tạo"
        );

        // Kiểm tra trạng thái ban đầu là CHƯA DUYỆT
        Assert.assertTrue(
                examDetailPage.isExamSetUnapprovedStatusDisplayed(),
                "Trạng thái ban đầu của bộ đề không phải CHƯA DUYỆT"
        );

        // Duyệt tất cả mã đề
        examDetailPage.selectAllExamCodes();
        examDetailPage.clickApproveAllExamCodesButton();
        examDetailPage.confirmApproveAllExamCodesPopup();

        // Refresh và kiểm tra trạng thái đã duyệt
        driver.navigate().refresh();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Sau khi refresh, màn hình chi tiết bộ đề không hiển thị"
        );

        Assert.assertTrue(
                examDetailPage.isExamSetApprovedStatusDisplayed(),
                "Sau khi duyệt tất cả mã đề, trạng thái bộ đề chưa chuyển sang ĐÃ DUYỆT"
        );
    }

    /**
     * AT_QLDT_012: Xóa tất cả mã đề và kiểm tra số lượng mã đề về 0.
     */
    @Test(priority = 12)
    public void AT_QLDT_012_DeleteAllExamCodesAndVerifyCodeCountIsZero() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_DELETE_CODES_EXAM");

        // Tạo bộ đề mới
        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.enterExamSetName(examSetName);
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();
        examCreatePage.configValidExamMatrix();
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        examCreatePage.clickProceedButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận tạo bộ đề"
        );

        examCreatePage.clickAcceptConfirmPopup();

        Assert.assertTrue(
                examCreatePage.isCreateExamSuccessfullyDisplayed(),
                "Hệ thống chưa tạo bộ đề thành công thật sự"
        );

        // Mở bộ đề vừa tạo
        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề vừa tạo"
        );

        // Chọn tất cả và xóa mã đề
        examDetailPage.selectAllExamCodes();
        examDetailPage.clickDeleteAllExamCodesButton();
        examDetailPage.confirmDeleteExamCodesPopup();

        Assert.assertTrue(
                examDetailPage.isDeleteExamCodesSuccessDisplayed(),
                "Không hiển thị thông báo xóa mã đề thành công"
        );

        // Mở lại trang danh sách và kiểm tra số lượng mã đề = 0
        examManagementPage.openExamSetListPage();

        Assert.assertTrue(
                examManagementPage.isFirstExamSetCodeCountZero(),
                "Số lượng mã đề của bộ đề vừa xóa chưa về 0"
        );
    }
}