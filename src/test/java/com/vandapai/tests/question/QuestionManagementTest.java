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

    /*
     * Dữ liệu dùng chung cho các testcase automation quản lý câu hỏi.
     * Dùng constant để khi đổi môn học hoặc file test thì chỉ cần sửa một chỗ.
     */
    private static final String SUBJECT_NAME = "Kho và Khai Phá Dữ Liệu";
    private static final String VALID_PDF_FILE_PATH = "C:\\Users\\DELL\\Downloads\\Test Data\\Data Mining and Data Warehousing.pdf";

    /*
     * Biến này vẫn giữ lại để lưu tên ngân hàng vừa tạo ở một số luồng cần dùng.
     * TC007 và TC008 hiện tại tự tạo dữ liệu mới trong chính testcase, không phụ thuộc TC005.
     */
    private static String createdQuestionBankName;

    /*
     * Helper dùng cho các testcase cần đi vào màn hình tạo ngân hàng câu hỏi.
     * Luồng:
     * Đăng nhập -> Quản lý câu hỏi -> Chọn môn học -> Bấm Tạo ngân hàng mới.
     */
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

    /*
     * Helper dùng cho các testcase thao tác trên màn hình danh sách ngân hàng câu hỏi.
     * Luồng:
     * Đăng nhập -> Quản lý câu hỏi -> Chọn môn học.
     */
    private QuestionManagementPage goToQuestionManagementPage() {
        LoginPage loginPage = new LoginPage(driver);
        SidebarComponent sidebar = new SidebarComponent(driver);
        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);

        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
        sidebar.openQuestionManagement();
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);

        return questionManagementPage;
    }

    /*
     * Helper tạo mới một ngân hàng câu hỏi hợp lệ bằng dữ liệu random.
     * Dùng cho TC007 và TC008 để mỗi testcase tự chuẩn bị dữ liệu thật, không phụ thuộc testcase khác.
     *
     * Luồng:
     * Vào màn hình tạo ngân hàng -> Upload tài liệu -> Chọn tài liệu -> Cấu hình AI
     * -> Tạo câu hỏi -> Lưu ngân hàng với tên random -> Quay lại danh sách -> Reload danh sách.
     */
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

        /*
         * Sau khi lưu xong, quay lại danh sách ngân hàng câu hỏi để kiểm tra dữ liệu thật đã được lưu.
         */
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

        String filePath = "C:\\Users\\DELL\\Downloads\\Test Data\\CHUONG1.csv";

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

        /*
         * Lưu lại tên ngân hàng vừa tạo để có thể dùng kiểm tra sau khi quay lại danh sách.
         */
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

    /*
     * TC007:
     * Tạo mới ngân hàng câu hỏi từ đầu, quay lại danh sách, reload và kiểm tra ngân hàng vừa tạo có hiển thị.
     * Mục tiêu chính: kiểm tra dữ liệu sau khi lưu có tồn tại trên danh sách hay không.
     */
    @Test(priority = 7)
    public void AT_QLCH_007_VerifyCreatedQuestionBankDisplayedInListAfterReload() {
        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);

        Assert.assertTrue(
                questionManagementPage.isQuestionBankDisplayedInList(bankName),
                "Không tìm thấy ngân hàng câu hỏi vừa tạo trong danh sách sau khi reload: " + bankName
        );
    }

    /*
     * TC008:
     * Tạo mới ngân hàng câu hỏi từ đầu, quay lại danh sách, reload, mở chi tiết và kiểm tra danh sách câu hỏi bên trong.
     * Mục tiêu chính: kiểm tra dữ liệu sau khi lưu có mở lại chi tiết và hiển thị câu hỏi đúng hay không.
     */
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
}