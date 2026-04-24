package com.vandapai.tests.exam;

import com.vandapai.base.BaseTest;
import com.vandapai.components.SidebarComponent;
import com.vandapai.pages.exam.ExamManagementPage;
import com.vandapai.pages.login.LoginPage;
import com.vandapai.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExamManagementTest extends BaseTest {

    private ExamManagementPage goToExamManagementPage() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        ExamManagementPage examManagementPage = new ExamManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openExamManagement();

        return examManagementPage;
    }

    @Test
    public void AT_QLDT_001_NavigateToCreateExamSuccessfully() {
        ExamManagementPage examManagementPage = goToExamManagementPage();

        examManagementPage.clickCreateExamButton();

        Assert.assertTrue(
                examManagementPage.isCreateExamPageDisplayed(),
                "Không điều hướng sang đúng màn hình Tạo đề thi"
        );

        Assert.assertTrue(
                examManagementPage.isCreateExamFormDisplayed(),
                "Màn hình Tạo đề thi không hiển thị đầy đủ các trường/khu vực cần thiết"
        );
    }

    @Test
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
        examManagementPage.enterNumberOfExamCodes("5");
        examManagementPage.tickAllowDuplicateQuestionsCheckbox();

        examManagementPage.clickProceedButton();

        Assert.assertTrue(
                examManagementPage.isExamSetNameRequiredMessageDisplayed(),
                "Không hiển thị validation yêu cầu nhập Mã bộ đề khi để trống"
        );

        examManagementPage.waitForUserToSeeToast();
    }
}