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

public class ExamSessionTest extends BaseTest {

    private ExamSessionManagementPage examSessionPage;

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

    @Test(priority = 1)
    public void AT_QLCT_001_GoToExamSessionManagementPageSuccessfully() {
        Assert.assertTrue(
                examSessionPage.isAtExamSessionManagementPage(),
                "Không điều hướng được vào màn hình Quản lý ca thi."
        );

        examSessionPage.assertMainElementsDisplayed();
    }

    @Test(priority = 2)
    public void AT_QLCT_002_SearchExamSessionByName() {
        String positiveKeyword = "Thi";
        String negativeKeyword = "XYZABC123";

        examSessionPage.searchSession(positiveKeyword);
        examSessionPage.assertSearchHasResult(positiveKeyword);

        examSessionPage.searchSession(negativeKeyword);
        examSessionPage.assertSearchNoResult();
    }

    @Test(priority = 3)
    public void AT_QLCT_003_FilterExamSessionByStatus() {
        examSessionPage.selectStatusByValue("SCHEDULED");
        examSessionPage.assertAllRowsContainStatusText("Sắp diễn ra");
    }

    @Test(priority = 4)
    public void AT_QLCT_004_OpenCreateExamSessionPageSuccessfully() {
        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();
        createPage.assertStep1Displayed();
    }

    @Test(priority = 5)
    public void AT_QLCT_005_ValidateStep1RequiredData() {
        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();

        createPage.fillStep1WithoutName();
        createPage.clickNext();
        createPage.assertToastContains("Vui lòng nhập tên kỳ thi");

        createPage.fillStep1WithInvalidTime();
        createPage.clickNext();
        createPage.assertToastContains("Thời gian kết thúc phải sau thời gian bắt đầu");
    }

    @Test(priority = 6)
    public void AT_QLCT_006_CannotGoToStep3WhenRoomHasNoExamCode() {
        String sessionName = "AUTO_CA_THI_NO_CODE_" + System.currentTimeMillis();

        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.goToStep2WithValidData(sessionName);

        createPage.addRoom();
        createPage.assertRoomCreated();
        createPage.fillFirstRoom("Phòng A101", 10, "CK101");

        createPage.assertCannotGoStep3WhenRoomHasNoExamCode();
    }

    @Test(priority = 7)
    public void AT_QLCT_007_AssignExamCodeForRoomSuccessfully() {
        String sessionName = "AUTO_CA_THI_ASSIGN_CODE_" + System.currentTimeMillis();

        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.goToStep2WithValidData(sessionName);

        createPage.addRoom();
        createPage.fillFirstRoom("Phòng A101", 10, "CK101");

        createPage.selectFirstExamCodeAndConfirm();
        createPage.assertExamCodeAssigned();
    }

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

        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.assertCreatedSessionDisplayed(sessionName);
    }

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

        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();

        examSessionPage.searchAndOpenSessionByName(sessionName);

        ExamSessionDetailPage detailPage = new ExamSessionDetailPage(driver);
        detailPage.assertDetailDisplayedCorrectly(sessionName);

        detailPage.clickViewCommonList();

        ExamSessionCommonListPage commonListPage = new ExamSessionCommonListPage(driver);
        commonListPage.assertCommonListDisplayedCorrectly(sessionName);
    }

    @Test(priority = 10)
    public void AT_QLCT_010_EditCreatedExamSessionSuccessfully() {
        String sessionName = "AUTO_CA_THI_EDIT_SRC_" + System.currentTimeMillis();

        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();
        createPage.createSessionEndToEnd(
                sessionName,
                "Phòng A101",
                40,
                "CK101"
        );

        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.searchAndOpenSessionByName(sessionName);

        ExamSessionDetailPage detailPage = new ExamSessionDetailPage(driver);
        detailPage.assertDetailDisplayedCorrectly(sessionName);
        detailPage.clickEditButton();

        ExamSessionCreatePage editPage = new ExamSessionCreatePage(driver);
        editPage.waitUntilCreatePageOpened();

        Assert.assertTrue(
                driver.getPageSource().contains("Chỉnh sửa ca thi")
                        || driver.getCurrentUrl().contains("mode=edit"),
                "Không mở được màn hình Chỉnh sửa ca thi."
        );
    }

    @Test(priority = 11)
    public void AT_QLCT_011_DeleteCreatedExamSessionSuccessfully() {
        String sessionName = "AUTO_CA_THI_DELETE_" + System.currentTimeMillis();

        examSessionPage.clickCreateSessionButton();

        ExamSessionCreatePage createPage = new ExamSessionCreatePage(driver);
        createPage.waitUntilCreatePageOpened();
        createPage.createSessionEndToEnd(
                sessionName,
                "Phòng A101",
                40,
                "CK101"
        );

        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.searchAndOpenSessionByName(sessionName);

        ExamSessionDetailPage detailPage = new ExamSessionDetailPage(driver);
        detailPage.assertDetailDisplayedCorrectly(sessionName);
        detailPage.deleteSession();

        examSessionPage = new ExamSessionManagementPage(driver);
        examSessionPage.waitUntilLoaded();
        examSessionPage.assertSessionNotDisplayedAfterSearch(sessionName);
    }
}