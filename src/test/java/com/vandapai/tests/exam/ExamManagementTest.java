package com.vandapai.tests.exam;

import com.vandapai.base.BaseTest;
import com.vandapai.components.SidebarComponent;
import com.vandapai.pages.exam.ExamManagementPage;
import com.vandapai.pages.login.LoginPage;
import com.vandapai.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ExamManagementTest extends BaseTest {

    // Lưu bộ đề tạo thật để các case sau có thể dùng lại khi chạy chung suite.
    private static String createdExamSetName;

    // Dùng chung: đăng nhập và vào màn hình Quản lý đề thi.
    private ExamManagementPage goToExamManagementPage() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        ExamManagementPage examManagementPage = new ExamManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openExamManagement();

        return examManagementPage;
    }

    // Dữ liệu sai định dạng năm học cho TC003.
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

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        Assert.assertTrue(
                examManagementPage.isCreateExamFormDisplayed(),
                "Màn hình Tạo đề thi không hiển thị đầy đủ các khu vực cần thiết"
        );
    }

    @Test(priority = 2)
    public void AT_QLDT_002_DoNotAllowCreateExamWhenExamSetNameIsEmpty() {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examManagementPage.selectSubjectByValue("1");
        examManagementPage.leaveExamSetNameEmpty();
        examManagementPage.enterAcademicYear("2024-2025");
        examManagementPage.selectSemesterHK1();
        examManagementPage.selectFirstAvailableQuestionBankSource();
        examManagementPage.configValidExamMatrix();
        examManagementPage.enterNumberOfExamCodes("2");
        examManagementPage.tickAllowDuplicateQuestionsCheckbox();

        examManagementPage.clickProceedButton();

        Assert.assertTrue(
                examManagementPage.isExamSetNameRequiredMessageDisplayed(),
                "Không hiển thị validation yêu cầu nhập Mã bộ đề khi để trống"
        );

        examManagementPage.waitForUserToSeeToast();
    }

    @Test(priority = 3, dataProvider = "invalidAcademicYearData")
    public void AT_QLDT_003_DoNotAllowCreateExamWhenAcademicYearFormatIsInvalid(String invalidAcademicYear) {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        // 1. Vào màn hình Tạo đề thi
        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        // 2. Làm sạch form cho từng data case của TC3
        // Vì TC3 chạy 3 lần theo DataProvider: 2026, 2026-2027a, abc
        examManagementPage.refreshCreateExamPage();

        // 3. Nhập/chọn dữ liệu trên form
        examManagementPage.selectSubjectByValue("1");

        examManagementPage.enterExamSetName(
                "AUTO_NAM_HOC_SAI_" + invalidAcademicYear.replaceAll("[^a-zA-Z0-9]", "")
        );

        examManagementPage.enterAcademicYear(invalidAcademicYear);

        examManagementPage.selectSemesterHK1();

        examManagementPage.selectFirstAvailableQuestionBankSource();

        // 4. Cấu hình ma trận hợp lệ để hệ thống đi tới validation năm học
        examManagementPage.configValidExamMatrix();

        // 5. Nhập số lượng mã đề
        examManagementPage.enterNumberOfExamCodes("2");

        // 6. Tick cho phép trùng câu hỏi giữa các mã đề nếu cần
        examManagementPage.tickAllowDuplicateQuestionsCheckbox();

        // 7. Nhấn Tiến hành tạo đề thi
        examManagementPage.printCreateExamFormDebug();
        examManagementPage.clickProceedButton();

        // 8. Kiểm tra validation năm học sai định dạng
        Assert.assertTrue(
                examManagementPage.isAcademicYearInvalidMessageDisplayed(),
                "Không hiển thị validation năm học không hợp lệ với dữ liệu: " + invalidAcademicYear
        );

        examManagementPage.waitForUserToSeeToast();
    }

    @Test(priority = 4)
    public void AT_QLDT_004_AddQuestionBankSourceSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examManagementPage.selectSubjectByValue("1");
        examManagementPage.selectFirstAvailableQuestionBankSource();

        Assert.assertTrue(
                examManagementPage.isQuestionBankSourceDisplayed(),
                "Nguồn câu hỏi không được thêm hoặc không hiển thị đúng"
        );

        examManagementPage.waitForUserToSeeToast();
    }

    @Test(priority = 5)
    public void AT_QLDT_005_TotalQuestionAndScoreUpdatedCorrectlyWhenMatrixChanged() {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examManagementPage.selectSubjectByValue("1");
        examManagementPage.selectFirstAvailableQuestionBankSource();

        // Tổng câu = 2 + 2 + 1 = 5
        // Tổng điểm = 2*2 + 2*3 + 1*0 = 10
        examManagementPage.configExamMatrix("2", "2", "1", "2", "3", "0");

        Assert.assertTrue(
                examManagementPage.isTotalQuestionAndScoreUpdated("5", "10"),
                "Tổng câu hoặc Tổng điểm không cập nhật đúng theo ma trận đề thi"
        );

        examManagementPage.waitForUserToSeeToast();
    }

    @Test(priority = 6)
    public void AT_QLDT_006_ShowConfirmPopupWhenTotalScoreGreaterThan10() {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        String examSetName = examManagementPage.generateExamSetName("AUTO_SCORE_GT10");

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examManagementPage.selectSubjectByValue("1");
        examManagementPage.enterExamSetName(examSetName);
        examManagementPage.enterAcademicYear("2024-2025");
        examManagementPage.selectSemesterHK1();
        examManagementPage.selectFirstAvailableQuestionBankSource();

        // Tổng câu = 5, Tổng điểm = 12
        examManagementPage.configExamMatrix("2", "2", "1", "2", "3", "2");
        examManagementPage.enterNumberOfExamCodes("2");
        examManagementPage.tickAllowDuplicateQuestionsCheckbox();

        Assert.assertTrue(
                examManagementPage.isTotalQuestionAndScoreUpdated("5", "12"),
                "Tổng điểm chưa hiển thị lớn hơn 10 trước khi tạo đề"
        );

        examManagementPage.clickProceedButton();

        Assert.assertTrue(
                examManagementPage.isTotalScoreGreaterThan10ConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận khi tổng điểm lớn hơn 10"
        );

        // TC006 chỉ kiểm tra popup cảnh báo thật, không tạo dữ liệu rác.
        examManagementPage.clickCancelConfirmPopup();
    }

    @Test(priority = 7)
    public void AT_QLDT_007_ShowConfirmPopupWhenTotalScoreLessThan10() {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        String examSetName = examManagementPage.generateExamSetName("AUTO_SCORE_LT10");

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examManagementPage.selectSubjectByValue("1");
        examManagementPage.enterExamSetName(examSetName);
        examManagementPage.enterAcademicYear("2024-2025");
        examManagementPage.selectSemesterHK1();
        examManagementPage.selectFirstAvailableQuestionBankSource();

        // Tổng câu = 4, Tổng điểm = 8
        examManagementPage.configExamMatrix("2", "1", "1", "2", "2", "2");
        examManagementPage.enterNumberOfExamCodes("2");
        examManagementPage.tickAllowDuplicateQuestionsCheckbox();

        Assert.assertTrue(
                examManagementPage.isTotalQuestionAndScoreUpdated("4", "8"),
                "Tổng điểm chưa hiển thị nhỏ hơn 10 trước khi tạo đề"
        );

        examManagementPage.clickProceedButton();

        Assert.assertTrue(
                examManagementPage.isTotalScoreLessThan10ConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận khi tổng điểm nhỏ hơn 10"
        );

        // TC007 chỉ kiểm tra popup cảnh báo thật, không tạo dữ liệu rác.
        examManagementPage.clickCancelConfirmPopup();
    }

    @Test(priority = 8)
    public void AT_QLDT_008_CreateExamSuccessfullyWithValidData() {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        createdExamSetName = examManagementPage.generateExamSetName("AUTO_CREATE_EXAM");

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examManagementPage.selectSubjectByValue("1");
        examManagementPage.enterExamSetName(createdExamSetName);
        examManagementPage.enterAcademicYear("2024-2025");
        examManagementPage.selectSemesterHK1();
        examManagementPage.selectFirstAvailableQuestionBankSource();
        examManagementPage.configValidExamMatrix();
        examManagementPage.enterNumberOfExamCodes("2");
        examManagementPage.tickAllowDuplicateQuestionsCheckbox();

        examManagementPage.clickProceedButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận tạo bộ đề"
        );

        examManagementPage.clickAcceptConfirmPopup();

        Assert.assertTrue(
                examManagementPage.isCreateExamSuccessfullyDisplayed(),
                "Hệ thống chưa tạo bộ đề thành công thật sự"
        );

        // Sau khi tạo thành công, quay ra danh sách và mở card đầu tiên để kiểm tra chi tiết thật
        examManagementPage.openFirstExamSetCard();

        Assert.assertTrue(
                examManagementPage.isExamSetDetailPageDisplayed(),
                "Không mở được màn hình chi tiết bộ đề sau khi tạo thành công"
        );
    }

    @Test(priority = 9)
    public void AT_QLDT_009_SearchExamSetByNameOrCodeSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        String examSetName = examManagementPage.generateExamSetName("AUTO_SEARCH_EXAM");

        // TC009 tự tạo dữ liệu thật để tìm kiếm, không phụ thuộc dữ liệu cũ.
        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examManagementPage.selectSubjectByValue("1");
        examManagementPage.enterExamSetName(examSetName);
        examManagementPage.enterAcademicYear("2024-2025");
        examManagementPage.selectSemesterHK1();
        examManagementPage.selectFirstAvailableQuestionBankSource();
        examManagementPage.configValidExamMatrix();
        examManagementPage.enterNumberOfExamCodes("1");
        examManagementPage.tickAllowDuplicateQuestionsCheckbox();

        examManagementPage.clickProceedButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận tạo bộ đề"
        );

        examManagementPage.clickAcceptConfirmPopup();

        Assert.assertTrue(
                examManagementPage.isCreateExamSuccessfullyDisplayed(),
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

        String examSetName = examManagementPage.generateExamSetName("AUTO_DETAIL_EXAM");

        // TC010 cũng tự tạo dữ liệu thật để có thể chạy riêng.
        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        examManagementPage.selectSubjectByValue("1");
        examManagementPage.enterExamSetName(examSetName);
        examManagementPage.enterAcademicYear("2024-2025");
        examManagementPage.selectSemesterHK1();
        examManagementPage.selectFirstAvailableQuestionBankSource();
        examManagementPage.configValidExamMatrix();
        examManagementPage.enterNumberOfExamCodes("1");
        examManagementPage.tickAllowDuplicateQuestionsCheckbox();

        examManagementPage.clickProceedButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamConfirmPopupDisplayed(),
                "Không hiển thị popup xác nhận tạo bộ đề"
        );

        examManagementPage.clickAcceptConfirmPopup();

        Assert.assertTrue(
                examManagementPage.isCreateExamSuccessfullyDisplayed(),
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
                examManagementPage.isExamSetDetailDisplayed(examSetName),
                "Màn hình chi tiết bộ đề thi không hiển thị đúng thông tin"
        );
    }
}