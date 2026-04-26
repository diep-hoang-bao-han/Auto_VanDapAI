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

public class ExamManagementTest extends BaseTest {

    private static String createdExamSetName;

    private ExamManagementPage goToExamManagementPage() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        ExamManagementPage examManagementPage = new ExamManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openExamManagement();

        return examManagementPage;
    }

    @DataProvider(name = "invalidAcademicYearData")
    public Object[][] invalidAcademicYearData() {
        return new Object[][]{
                {"2026"},
                {"2026-2027a"},
                {"abc"}
        };
    }

    @Test(priority = 1)
    public void AT_QLDT_001_NavigateToCreateExamSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        Assert.assertTrue(
                examCreatePage.isCreateExamFormDisplayed(),
                "Màn hình Tạo đề thi không hiển thị đầy đủ các khu vực cần thiết"
        );
    }

    @Test(priority = 2)
    public void AT_QLDT_002_DoNotAllowCreateExamWhenExamSetNameIsEmpty() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examCreatePage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.leaveExamSetNameEmpty();
        examCreatePage.enterAcademicYear("2024-2025");
        examCreatePage.selectSemesterHK1();
        examCreatePage.selectFirstAvailableQuestionBankSource();
        examCreatePage.configValidExamMatrix();
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        examCreatePage.clickProceedButton();

        Assert.assertTrue(
                examCreatePage.isExamSetNameRequiredMessageDisplayed(),
                "Không hiển thị validation yêu cầu nhập Mã bộ đề khi để trống"
        );

        examCreatePage.waitForUserToSeeToast();
    }

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

        Assert.assertTrue(
                examCreatePage.isAcademicYearInvalidMessageDisplayed(),
                "Không hiển thị validation năm học không hợp lệ với dữ liệu: " + invalidAcademicYear
        );

        examCreatePage.waitForUserToSeeToast();
    }

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

        Assert.assertTrue(
                examCreatePage.isQuestionBankSourceDisplayed(),
                "Nguồn câu hỏi không được thêm hoặc không hiển thị đúng"
        );

        examCreatePage.waitForUserToSeeToast();
    }

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

        examCreatePage.configExamMatrix("2", "2", "1", "2", "3", "0");

        Assert.assertTrue(
                examCreatePage.isTotalQuestionAndScoreUpdated("5", "10"),
                "Tổng câu hoặc Tổng điểm không cập nhật đúng theo ma trận đề thi"
        );

        examCreatePage.waitForUserToSeeToast();
    }

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

        examCreatePage.configExamMatrix("2", "2", "1", "2", "3", "2");
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        Assert.assertTrue(
                examCreatePage.isTotalQuestionAndScoreUpdated("5", "12"),
                "Tổng điểm chưa hiển thị lớn hơn 10 trước khi tạo đề"
        );

        examCreatePage.clickProceedButton();

        Assert.assertTrue(
                examCreatePage.isTotalScoreGreaterThan10ConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận khi tổng điểm lớn hơn 10"
        );

        examCreatePage.clickCancelConfirmPopup();
    }

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

        examCreatePage.configExamMatrix("2", "1", "1", "2", "2", "2");
        examCreatePage.enterNumberOfExamCodes("2");
        examCreatePage.tickAllowDuplicateQuestionsCheckbox();

        Assert.assertTrue(
                examCreatePage.isTotalQuestionAndScoreUpdated("4", "8"),
                "Tổng điểm chưa hiển thị nhỏ hơn 10 trước khi tạo đề"
        );

        examCreatePage.clickProceedButton();

        Assert.assertTrue(
                examCreatePage.isTotalScoreLessThan10ConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận khi tổng điểm nhỏ hơn 10"
        );

        examCreatePage.clickCancelConfirmPopup();
    }

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

        examCreatePage.selectSubjectByValue("1");
        examCreatePage.enterExamSetName(createdExamSetName);
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

        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề sau khi tạo thành công"
        );
    }

    @Test(priority = 9)
    public void AT_QLDT_009_SearchExamSetByNameOrCodeSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_SEARCH_EXAM");

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
        examCreatePage.enterNumberOfExamCodes("1");
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

        examManagementPage.openExamSetListPage();
        examManagementPage.searchExamSet(examSetName);

        Assert.assertTrue(
                examManagementPage.isExamSetDisplayedInList(examSetName),
                "Không tìm thấy bộ đề vừa tạo trong danh sách: " + examSetName
        );
    }

    @Test(priority = 10)
    public void AT_QLDT_010_OpenExamSetDetailAndVerifyExamCodes() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_DETAIL_EXAM");

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
        examCreatePage.enterNumberOfExamCodes("1");
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

        examManagementPage.openExamSetListPage();
        examManagementPage.searchExamSet(examSetName);

        Assert.assertTrue(
                examManagementPage.isExamSetDisplayedInList(examSetName),
                "Không tìm thấy bộ đề thi trước khi mở chi tiết: " + examSetName
        );

        examManagementPage.openExamSetDetailByName(examSetName);

        Assert.assertTrue(
                examDetailPage.isExamSetDetailDisplayed(examSetName),
                "Màn hình chi tiết bộ đề thi không hiển thị đúng thông tin"
        );
    }

    @Test(priority = 11)
    public void AT_QLDT_011_UpdateFirstExamCodeAndFirstQuestionSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_UPDATE_EXAM");

        /*
         * Tạo bộ đề mới để đảm bảo bộ đề chưa được sử dụng trong ca thi.
         */
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

        /*
         * Do search đang lỗi, mở bộ đề đầu tiên trong danh sách.
         * Theo luồng hiện tại, bộ đề vừa tạo sẽ nằm đầu danh sách.
         */
        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề vừa tạo"
        );

        /*
         * Mở mã đề đầu tiên trong bộ đề.
         */
        examDetailPage.openFirstExamCodeCard();

        Assert.assertTrue(
                examDetailPage.isExamCodeEditorDisplayed(),
                "Không hiển thị panel Biên tập Mã đề"
        );

        /*
         * Cập nhật mã đề và nội dung câu hỏi đầu tiên.
         * Mã đề dùng số random để hạn chế trùng với 101, 102.
         */
        String newExamCodeName = "9" + (System.currentTimeMillis() % 10000);
        String newFirstQuestionContent = "Data warehouse là gì?";

        examDetailPage.updateExamCodeName(newExamCodeName);
        examDetailPage.updateFirstQuestionContent(newFirstQuestionContent);
        examDetailPage.clickSaveCodeButton();

        Assert.assertTrue(
                examDetailPage.isSaveExamCodeSuccessDisplayed(),
                "Không hiển thị thông báo lưu thay đổi mã đề thành công"
        );

        /*
         * Refresh lại trang chi tiết bộ đề, mở lại mã đề đầu tiên để kiểm tra dữ liệu đã lưu.
         */
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

    @Test(priority = 12)
    public void AT_QLDT_012_ApproveAllExamCodesAndVerifyExamSetApproved() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_APPROVE_EXAM");

        /*
         * Tạo bộ đề mới có ít nhất 2 mã đề để duyệt hàng loạt.
         */
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

        /*
         * Do search đang lỗi, mở bộ đề đầu tiên trong danh sách.
         * Bộ đề vừa tạo thường nằm ở đầu bảng.
         */
        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề vừa tạo"
        );

        Assert.assertTrue(
                examDetailPage.isExamSetUnapprovedStatusDisplayed(),
                "Trạng thái ban đầu của bộ đề không phải CHƯA DUYỆT"
        );

        /*
         * Chọn tất cả mã đề và duyệt hàng loạt.
         */
        examDetailPage.selectAllExamCodes();
        examDetailPage.clickApproveAllExamCodesButton();
        examDetailPage.confirmApproveAllExamCodesPopup();

        /*
         * Refresh để kiểm tra trạng thái cuối cùng được lưu thật.
         */
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

    @Test(priority = 13)
    public void AT_QLDT_013_DeleteAllExamCodesAndVerifyCodeCountIsZero() {
        ExamManagementPage examManagementPage = goToExamManagementPage();
        ExamCreatePage examCreatePage = new ExamCreatePage(driver);
        ExamDetailPage examDetailPage = new ExamDetailPage(driver);

        String examSetName = examManagementPage.generateExamSetName("AUTO_DELETE_CODES_EXAM");

        /*
         * Tạo bộ đề mới có ít nhất 2 mã đề để kiểm tra xóa hàng loạt mã đề.
         */
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

        /*
         * Do search đang lỗi, mở bộ đề đầu tiên trong danh sách.
         * Bộ đề vừa tạo thường nằm đầu bảng.
         */
        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examDetailPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề vừa tạo"
        );

        /*
         * Chọn tất cả mã đề và xóa hàng loạt.
         */
        examDetailPage.selectAllExamCodes();
        examDetailPage.clickDeleteAllExamCodesButton();
        examDetailPage.confirmDeleteExamCodesPopup();

        Assert.assertTrue(
                examDetailPage.isDeleteExamCodesSuccessDisplayed(),
                "Không hiển thị thông báo xóa mã đề thành công"
        );

        /*
         * Quay lại danh sách bộ đề và kiểm tra cột số mã đề của dòng đầu tiên đã về 0.
         */
        examManagementPage.openExamSetListPage();

        Assert.assertTrue(
                examManagementPage.isFirstExamSetCodeCountZero(),
                "Số lượng mã đề của bộ đề vừa xóa chưa về 0"
        );
    }
}