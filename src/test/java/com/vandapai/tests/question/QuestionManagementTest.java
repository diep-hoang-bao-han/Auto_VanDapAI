package com.vandapai.tests.question;

import com.vandapai.base.BaseTest;
import com.vandapai.components.SidebarComponent;
import com.vandapai.pages.login.LoginPage;
import com.vandapai.pages.question.QuestionBankCreatePage;
import com.vandapai.pages.question.QuestionBankDetailPage;
import com.vandapai.pages.question.QuestionManagementPage;
import com.vandapai.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QuestionManagementTest extends BaseTest {

    private static final String SUBJECT_NAME = "Kho và Khai Phá Dữ Liệu";
    private static final String VALID_PDF_FILE_PATH = "C:\\Auto_VanDapAI\\src\\test\\resources\\Data Test\\Data Mining and Data Warehousing.pdf";

    private static String createdQuestionBankName;

    private QuestionBankCreatePage goToCreateQuestionBankPage() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openQuestionManagement();
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);
        questionManagementPage.clickCreateNewBank();

        return new QuestionBankCreatePage(driver);
    }

    private QuestionManagementPage goToQuestionManagementPage() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openQuestionManagement();
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);

        return questionManagementPage;
    }

    private String createQuestionBankWithValidQuestions() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        questionBankCreatePage.clickAddDocumentButton();
        questionBankCreatePage.uploadFile(VALID_PDF_FILE_PATH);
        questionBankCreatePage.waitForUploadSuccessToast();

        questionBankCreatePage.closeUploadPopup();
        questionBankCreatePage.selectUploadedDocumentCheckbox();
        questionBankCreatePage.setQuestionCount("20");
        questionBankCreatePage.clickAllLevelChip();

        Assert.assertTrue(
                questionBankCreatePage.isGenerateQuestionButtonEnabled(),
                "Nút Tạo câu hỏi vẫn bị disable sau khi chọn tài liệu, số lượng và mức độ"
        );

        questionBankCreatePage.clickGenerateQuestionButton();

        Assert.assertTrue(
                questionBankCreatePage.waitUntilCanSaveQuestionBank(),
                "Sau khi tạo câu hỏi, chưa thấy câu hỏi hoặc nút Lưu thành ngân hàng"
        );

        questionBankCreatePage.clickSaveBankButton();

        Assert.assertTrue(
                questionBankCreatePage.waitUntilSaveNamePopupAppears(),
                "Popup đặt tên ngân hàng câu hỏi không hiển thị sau khi bấm Lưu thành ngân hàng"
        );

        String bankName = "Ngan hang auto test " + System.currentTimeMillis();
        createdQuestionBankName = bankName;

        questionBankCreatePage.enterBankName(bankName);
        questionBankCreatePage.clickConfirmSaveBankButton();
        questionBankCreatePage.waitAfterConfirmSaveBank(bankName);

        SidebarComponent sidebar = new SidebarComponent(driver);
        sidebar.openQuestionManagement();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);
        questionManagementPage.reloadQuestionBankList();

        return bankName;
    }

    @Test
    public void AT_QLCH_001_NavigateToCreateBankSuccessfully() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        Assert.assertTrue(
                questionBankCreatePage.isQuestionBankSectionDisplayed(),
                "Khu vực Ngân hàng câu hỏi không hiển thị"
        );

        Assert.assertTrue(
                questionBankCreatePage.isAIConfigSectionDisplayed(),
                "Khu vực Cấu hình AI không hiển thị"
        );

        Assert.assertTrue(
                questionBankCreatePage.isSaveButtonDisplayed(),
                "Nút Lưu thành ngân hàng không hiển thị"
        );

        Assert.assertTrue(
                questionBankCreatePage.isAddQuestionButtonDisplayed(),
                "Nút Thêm câu hỏi không hiển thị"
        );
    }

    @Test
    public void AT_QLCH_002_UploadValidPdfSuccessfully() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        questionBankCreatePage.clickAddDocumentButton();
        questionBankCreatePage.uploadFile(VALID_PDF_FILE_PATH);
        questionBankCreatePage.waitForUploadSuccessToast();
    }

    @Test
    public void AT_QLCH_003_DoNotAllowUploadInvalidFileType() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        String filePath = "C:\\Auto_VanDapAI\\src\\test\\resources\\Data Test\\CHUONG1.csv";

        questionBankCreatePage.clickAddDocumentButton();
        questionBankCreatePage.uploadFile(filePath);

        Assert.assertTrue(
                questionBankCreatePage.isUploadErrorToastDisplayed("Định dạng file không hợp lệ"),
                "Không hiển thị thông báo lỗi định dạng file không hợp lệ"
        );

        Assert.assertTrue(
                questionBankCreatePage.isEmptyDocumentMessageDisplayed(),
                "Danh sách tài liệu không còn ở trạng thái 'Chưa có tài liệu nào'"
        );
    }

    @Test
    public void AT_QLCH_004_DoNotAllowSaveWithoutValidQuestionData() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        questionBankCreatePage.clickSaveBankButton();

        Assert.assertTrue(
                questionBankCreatePage.isWarningToastDisplayed("ít nhất 1 câu hỏi"),
                "Không hiển thị cảnh báo khi lưu trong trạng thái chưa có câu hỏi"
        );
    }

    @Test
    public void AT_QLCH_005_SaveQuestionBankSuccessfullyAfterHavingValidQuestions() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        questionBankCreatePage.clickAddDocumentButton();
        questionBankCreatePage.uploadFile(VALID_PDF_FILE_PATH);
        questionBankCreatePage.waitForUploadSuccessToast();

        questionBankCreatePage.closeUploadPopup();
        questionBankCreatePage.selectUploadedDocumentCheckbox();
        questionBankCreatePage.setQuestionCount("20");
        questionBankCreatePage.clickAllLevelChip();

        Assert.assertTrue(
                questionBankCreatePage.isGenerateQuestionButtonEnabled(),
                "Nút Tạo câu hỏi vẫn bị disable sau khi chọn tài liệu, số lượng và mức độ"
        );

        questionBankCreatePage.clickGenerateQuestionButton();

        Assert.assertTrue(
                questionBankCreatePage.waitUntilCanSaveQuestionBank(),
                "Sau khi tạo câu hỏi, chưa thấy câu hỏi hoặc nút Lưu thành ngân hàng"
        );

        questionBankCreatePage.clickSaveBankButton();

        Assert.assertTrue(
                questionBankCreatePage.waitUntilSaveNamePopupAppears(),
                "Popup đặt tên ngân hàng câu hỏi không hiển thị sau khi bấm Lưu thành ngân hàng"
        );

        String bankName = "Ngan hang auto test " + System.currentTimeMillis();

        //Lưu lại tên ngân hàng vừa tạo để có thể dùng kiểm tra sau khi quay lại danh sách.
        createdQuestionBankName = bankName;

        questionBankCreatePage.enterBankName(bankName);
        questionBankCreatePage.clickConfirmSaveBankButton();

        questionBankCreatePage.waitAfterConfirmSaveBank(bankName);

        SidebarComponent sidebar = new SidebarComponent(driver);
        sidebar.openQuestionManagement();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);

        Assert.assertTrue(
                questionManagementPage.isQuestionBankDisplayedInList(bankName),
                "Không tìm thấy ngân hàng câu hỏi vừa lưu trong danh sách: " + bankName
        );
    }

    @Test
    public void AT_QLCH_006_DoNotAllowSaveWhenQuestionListIsEmptyAfterDeletingAllQuestions() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        questionBankCreatePage.clickAddDocumentButton();
        questionBankCreatePage.uploadFile(VALID_PDF_FILE_PATH);
        questionBankCreatePage.waitForUploadSuccessToast();

        questionBankCreatePage.closeUploadPopup();
        questionBankCreatePage.selectUploadedDocumentCheckbox();
        questionBankCreatePage.setQuestionCount("20");
        questionBankCreatePage.clickAllLevelChip();

        Assert.assertTrue(
                questionBankCreatePage.isGenerateQuestionButtonEnabled(),
                "Nút Tạo câu hỏi vẫn bị disable sau khi chọn tài liệu, số lượng và mức độ"
        );

        questionBankCreatePage.clickGenerateQuestionButton();

        Assert.assertTrue(
                questionBankCreatePage.waitForGenerateQuestionSuccessToast(),
                "Không hiển thị thông báo tạo câu hỏi thành công"
        );

        Assert.assertTrue(
                questionBankCreatePage.waitUntilQuestionsAreGenerated(),
                "Danh sách câu hỏi chưa được tạo/hiển thị sau khi tạo câu hỏi bằng AI"
        );

        questionBankCreatePage.clickBulkDeleteQuestionButton();
        questionBankCreatePage.confirmDeleteQuestions();

        Assert.assertTrue(
                questionBankCreatePage.waitForDeleteSuccessToast(),
                "Không hiển thị thông báo xóa thành công"
        );

        questionBankCreatePage.clickSaveBankButton();

        Assert.assertTrue(
                questionBankCreatePage.isWarningToastDisplayed("ít nhất 1 câu hỏi"),
                "Không hiển thị cảnh báo khi lưu trong trạng thái danh sách câu hỏi rỗng"
        );
    }

    @Test(priority = 7)
    public void AT_QLCH_007_VerifyCreatedQuestionBankDisplayedInListAfterReload() {
        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);

        Assert.assertTrue(
                questionManagementPage.isQuestionBankDisplayedInList(bankName),
                "Không tìm thấy ngân hàng câu hỏi vừa tạo trong danh sách sau khi reload: " + bankName
        );
    }

    @Test(priority = 8)
    public void AT_QLCH_008_OpenCreatedQuestionBankDetailAndVerifyQuestionList() {
        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        Assert.assertTrue(
                questionManagementPage.isQuestionBankDisplayedInList(bankName),
                "Không tìm thấy ngân hàng câu hỏi trước khi mở chi tiết: " + bankName
        );

        questionManagementPage.openQuestionBankDetailByName(bankName);

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Màn hình chi tiết ngân hàng câu hỏi không hiển thị đúng thông tin: " + bankName
        );

        Assert.assertTrue(
                questionBankDetailPage.hasAtLeastOneQuestionInQuestionBankDetail(),
                "Chi tiết ngân hàng câu hỏi không hiển thị danh sách câu hỏi hoặc không có câu hỏi nào"
        );
    }

    @Test(priority = 9)
    public void AT_QLCH_009_UpdateQuestionContentSuccessfully() {

        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        questionManagementPage.openFirstQuestionBankCard();

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Không mở được màn hình chi tiết ngân hàng câu hỏi vừa tạo: " + bankName
        );

        String newQuestionContent = "Sự khác biệt cốt lõi giữa hệ thống OLTP (Online Transaction Processing) và OLAP (Online Analytical Processing) là gì?";

        questionBankDetailPage.clickEditFirstQuestionButton();
        questionBankDetailPage.updateFirstQuestionContent(newQuestionContent);
        questionBankDetailPage.clickSaveFirstQuestionButton();

        Assert.assertEquals(
                questionBankDetailPage.getFirstQuestionContent(),
                newQuestionContent,
                "Nội dung câu hỏi không được cập nhật đúng"
        );

        questionBankDetailPage.clickUpdateQuestionBankButton();

        Assert.assertTrue(
                questionBankDetailPage.isUpdateQuestionBankSuccessToastDisplayed(),
                "Không hiển thị thông báo cập nhật ngân hàng thành công"
        );

        driver.navigate().refresh();
    }

    @Test(priority = 10)
    public void AT_QLCH_010_BulkUpdateQuestionLevelSuccessfully() {

        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        questionManagementPage.openFirstQuestionBankCard();

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Không mở được màn hình chi tiết ngân hàng: " + bankName
        );

        Assert.assertTrue(
                questionBankDetailPage.waitForQuestionListLoaded(),
                "Danh sách câu hỏi không load"
        );

        int beforeHard = questionBankDetailPage.getHardQuestionCount();

        questionBankDetailPage.selectFirstThreeQuestions();
        questionBankDetailPage.clickBulkChangeLevelButton();
        questionBankDetailPage.selectHardLevelInBulkModal();
        questionBankDetailPage.clickConfirmBulkLevelButton();

        Assert.assertTrue(
                questionBankDetailPage.isAnyQuestionMarkedAsUnsaved(),
                "Không có câu hỏi nào được đánh dấu 'Chưa lưu'"
        );

        questionBankDetailPage.clickUpdateQuestionBankButton();

        Assert.assertTrue(
                questionBankDetailPage.isUpdateQuestionBankSuccessToastDisplayed(),
                "Không hiển thị toast cập nhật thành công"
        );

        int afterHard = questionBankDetailPage.getHardQuestionCount();

        Assert.assertTrue(
                afterHard >= beforeHard + 1,
                "Số lượng câu hỏi Khó không tăng sau bulk update. Trước: " + beforeHard + ", Sau: " + afterHard
        );
    }



    @Test(priority = 11)
    public void AT_QLCH_011_BulkDeleteQuestionsSuccessfully() {

        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        questionManagementPage.openFirstQuestionBankCard();

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Không mở được màn hình chi tiết ngân hàng câu hỏi vừa tạo: " + bankName
        );

        int beforeCount = questionBankDetailPage.getTotalQuestionCountFromAllTab();

        Assert.assertTrue(
                beforeCount >= 5,
                "Ngân hàng câu hỏi cần có ít nhất 5 câu hỏi để kiểm tra xóa hàng loạt. Số câu hiện tại: " + beforeCount
        );


        int deletedCount = 3;

        questionBankDetailPage.selectFirstThreeQuestions();
        questionBankDetailPage.clickBulkDeleteQuestionButton();
        questionBankDetailPage.confirmBulkDeleteQuestionPopup();

        questionBankDetailPage.clickUpdateQuestionBankButton();

        Assert.assertTrue(
                questionBankDetailPage.isUpdateQuestionBankSuccessToastDisplayed(),
                "Không hiển thị thông báo cập nhật ngân hàng thành công sau khi xóa hàng loạt câu hỏi"
        );

        Assert.assertTrue(
                questionBankDetailPage.isQuestionCountDecreasedBy(beforeCount, deletedCount),
                "Tổng số câu hỏi không giảm đúng " + deletedCount + " câu sau khi xóa hàng loạt"
        );
    }

    @Test(priority = 12)
    public void AT_QLCH_012_DeleteQuestionBankSuccessfully() {

        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        questionManagementPage.openFirstQuestionBankCard();

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Không mở được màn hình chi tiết ngân hàng câu hỏi vừa tạo: " + bankName
        );

        questionBankDetailPage.clickDeleteQuestionBankButton();
        questionBankDetailPage.confirmDeleteQuestionBankPopup(bankName);

        questionManagementPage.openQuestionBankListPage();
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);
        questionManagementPage.reloadQuestionBankList();

        Assert.assertTrue(
                questionManagementPage.isQuestionBankNotDisplayedInList(bankName),
                "Ngân hàng câu hỏi vừa xóa vẫn còn hiển thị trong danh sách: " + bankName
        );
    }
}