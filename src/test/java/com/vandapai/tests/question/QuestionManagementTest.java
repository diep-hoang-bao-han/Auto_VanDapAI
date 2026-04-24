package com.vandapai.tests.question;

import com.vandapai.base.BaseTest;
import com.vandapai.components.SidebarComponent;
import com.vandapai.pages.login.LoginPage;
import com.vandapai.pages.question.QuestionManagementPage;
import com.vandapai.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QuestionManagementTest extends BaseTest {

    private QuestionManagementPage goToCreateQuestionBankPage() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openQuestionManagement();
        questionManagementPage.selectSubjectByVisibleText("Kho và Khai Phá Dữ Liệu");
        questionManagementPage.clickCreateNewBank();

        return questionManagementPage;
    }

    @Test
    public void AT_QLCH_001_NavigateToCreateBankSuccessfully() {
        QuestionManagementPage questionManagementPage = goToCreateQuestionBankPage();

        Assert.assertTrue(
                questionManagementPage.isQuestionBankSectionDisplayed(),
                "Khu vực Ngân hàng câu hỏi không hiển thị"
        );

        Assert.assertTrue(
                questionManagementPage.isAIConfigSectionDisplayed(),
                "Khu vực Cấu hình AI không hiển thị"
        );

        Assert.assertTrue(
                questionManagementPage.isSaveButtonDisplayed(),
                "Nút Lưu thành ngân hàng không hiển thị"
        );

        Assert.assertTrue(
                questionManagementPage.isAddQuestionButtonDisplayed(),
                "Nút Thêm câu hỏi không hiển thị"
        );
    }

    @Test
    public void AT_QLCH_002_UploadValidPdfSuccessfully() {
        QuestionManagementPage questionManagementPage = goToCreateQuestionBankPage();

        String filePath = "C:\\Users\\DELL\\Downloads\\Test Data\\Data Mining and Data Warehousing.pdf";

        questionManagementPage.clickAddDocumentButton();
        questionManagementPage.uploadFile(filePath);
        questionManagementPage.waitForUploadSuccessToast();
    }

    @Test
    public void AT_QLCH_003_DoNotAllowUploadInvalidFileType() {
        QuestionManagementPage questionManagementPage = goToCreateQuestionBankPage();

        String filePath = "C:\\Users\\DELL\\Downloads\\Test Data\\CHUONG1.csv";

        questionManagementPage.clickAddDocumentButton();
        questionManagementPage.uploadFile(filePath);

        Assert.assertTrue(
                questionManagementPage.isUploadErrorToastDisplayed("Định dạng file không hợp lệ"),
                "Không hiển thị thông báo lỗi định dạng file không hợp lệ"
        );

        Assert.assertTrue(
                questionManagementPage.isEmptyDocumentMessageDisplayed(),
                "Danh sách tài liệu không còn ở trạng thái 'Chưa có tài liệu nào'"
        );
    }

    @Test
    public void AT_QLCH_004_DoNotAllowSaveWithoutValidQuestionData() {
        QuestionManagementPage questionManagementPage = goToCreateQuestionBankPage();

        questionManagementPage.clickSaveBankButton();

        Assert.assertTrue(
                questionManagementPage.isWarningToastDisplayed("ít nhất 1 câu hỏi"),
                "Không hiển thị cảnh báo khi lưu trong trạng thái chưa có câu hỏi"
        );
    }

    @Test
    public void AT_QLCH_005_SaveQuestionBankSuccessfullyAfterHavingValidQuestions() {
        QuestionManagementPage questionManagementPage = goToCreateQuestionBankPage();

        String filePath = "C:\\Users\\DELL\\Downloads\\Test Data\\Data Mining and Data Warehousing.pdf";

        questionManagementPage.clickAddDocumentButton();
        questionManagementPage.uploadFile(filePath);
        questionManagementPage.waitForUploadSuccessToast();

        questionManagementPage.closeUploadPopup();
        questionManagementPage.selectUploadedDocumentCheckbox();
        questionManagementPage.setQuestionCount("20");
        questionManagementPage.clickAllLevelChip();

        Assert.assertTrue(
                questionManagementPage.isGenerateQuestionButtonEnabled(),
                "Nút Tạo câu hỏi vẫn bị disable sau khi chọn tài liệu, số lượng và mức độ"
        );

        questionManagementPage.clickGenerateQuestionButton();

        Assert.assertTrue(
                questionManagementPage.waitUntilCanSaveQuestionBank(),
                "Sau khi tạo câu hỏi, chưa thấy câu hỏi hoặc nút Lưu thành ngân hàng"
        );

        questionManagementPage.clickSaveBankButton();

        Assert.assertTrue(
                questionManagementPage.waitUntilSaveNamePopupAppears(),
                "Popup đặt tên ngân hàng câu hỏi không hiển thị sau khi bấm Lưu thành ngân hàng"
        );

        String bankName = "Ngan hang auto test " + System.currentTimeMillis();

        questionManagementPage.enterBankName(bankName);
        questionManagementPage.clickConfirmSaveBankButton();

        questionManagementPage.waitAfterConfirmSaveBank(bankName);

        SidebarComponent sidebar = new SidebarComponent(driver);
        sidebar.openQuestionManagement();

        questionManagementPage.selectSubjectByVisibleText("Kho và Khai Phá Dữ Liệu");

        Assert.assertTrue(
                questionManagementPage.isQuestionBankDisplayedInList(bankName),
                "Không tìm thấy ngân hàng câu hỏi vừa lưu trong danh sách: " + bankName
        );
    }
    @Test
    public void AT_QLCH_006_DoNotAllowSaveWhenQuestionListIsEmptyAfterDeletingAllQuestions() {
        QuestionManagementPage questionManagementPage = goToCreateQuestionBankPage();

        String filePath = "C:\\Users\\DELL\\Downloads\\Test Data\\Data Mining and Data Warehousing.pdf";

        questionManagementPage.clickAddDocumentButton();
        questionManagementPage.uploadFile(filePath);
        questionManagementPage.waitForUploadSuccessToast();

        questionManagementPage.closeUploadPopup();
        questionManagementPage.selectUploadedDocumentCheckbox();
        questionManagementPage.setQuestionCount("20");
        questionManagementPage.clickAllLevelChip();

        Assert.assertTrue(
                questionManagementPage.isGenerateQuestionButtonEnabled(),
                "Nút Tạo câu hỏi vẫn bị disable sau khi chọn tài liệu, số lượng và mức độ"
        );

        questionManagementPage.clickGenerateQuestionButton();

        Assert.assertTrue(
                questionManagementPage.waitForGenerateQuestionSuccessToast(),
                "Không hiển thị thông báo tạo câu hỏi thành công"
        );

        Assert.assertTrue(
                questionManagementPage.waitUntilQuestionsAreGenerated(),
                "Danh sách câu hỏi chưa được tạo/hiển thị sau khi tạo câu hỏi bằng AI"
        );

        questionManagementPage.clickBulkDeleteQuestionButton();
        questionManagementPage.confirmDeleteQuestions();

        Assert.assertTrue(
                questionManagementPage.waitForDeleteSuccessToast(),
                "Không hiển thị thông báo xóa thành công"
        );

        questionManagementPage.clickSaveBankButton();

        Assert.assertTrue(
                questionManagementPage.isWarningToastDisplayed("ít nhất 1 câu hỏi"),
                "Không hiển thị cảnh báo khi lưu trong trạng thái danh sách câu hỏi rỗng"
        );
    }
}