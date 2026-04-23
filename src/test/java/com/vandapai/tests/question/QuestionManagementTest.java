package com.vandapai.tests.question;

import com.vandapai.base.BaseTest;
import com.vandapai.components.SidebarComponent;
import com.vandapai.pages.login.LoginPage;
import com.vandapai.pages.question.QuestionManagementPage;
import com.vandapai.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QuestionManagementTest extends BaseTest {

    @Test
    public void AT_QLCH_001_NavigateToCreateBankSuccessfully() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openQuestionManagement();

        questionManagementPage.selectSubjectByVisibleText("Kho và Khai Phá Dữ Liệu");
        questionManagementPage.clickCreateNewBank();

        Assert.assertTrue(questionManagementPage.isQuestionBankSectionDisplayed(),
                "Khu vực Ngân hàng câu hỏi không hiển thị");

        Assert.assertTrue(questionManagementPage.isAIConfigSectionDisplayed(),
                "Khu vực Cấu hình AI không hiển thị");

        Assert.assertTrue(questionManagementPage.isSaveButtonDisplayed(),
                "Nút Lưu thành ngân hàng không hiển thị");

        Assert.assertTrue(questionManagementPage.isAddQuestionButtonDisplayed(),
                "Nút Thêm câu hỏi không hiển thị");
    }

    @Test
    public void AT_QLCH_002_UploadValidPdfSuccessfully() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openQuestionManagement();

        questionManagementPage.selectSubjectByVisibleText("Kho và Khai Phá Dữ Liệu");
        questionManagementPage.clickCreateNewBank();

        String filePath = "C:\\Users\\DELL\\Downloads\\Test Data\\Data Mining and Data Warehousing.pdf";

        questionManagementPage.clickAddDocumentButton();
        questionManagementPage.uploadPdfFile(filePath);
        questionManagementPage.waitForUploadSuccessToast();

        Assert.assertTrue(
                questionManagementPage.isUploadSuccessToastDisplayed(),
                "Không hiển thị toast upload tài liệu thành công"
        );
    }
}