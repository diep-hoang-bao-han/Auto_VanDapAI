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

/**
 * QuestionManagementTest: Bộ test case cho chức năng Quản lý câu hỏi (Ngân hàng câu hỏi).
 * Bao gồm: tạo ngân hàng, upload tài liệu, sinh câu hỏi bằng AI,
 * chỉnh sửa câu hỏi, cập nhật mức độ (1 câu / bulk), xóa câu hỏi, xóa ngân hàng.
 */
public class QuestionManagementTest extends BaseTest {

    // =========================
    // CONSTANTS
    // =========================
    private static final String SUBJECT_NAME = "Kho và Khai Phá Dữ Liệu";
    private static final String VALID_PDF_FILE_PATH = "C:\\Auto_VanDapAI\\src\\test\\resources\\Data Test\\Data Mining and Data Warehousing.pdf";

    // Lưu tên ngân hàng vừa tạo để dùng cho các test case sau
    private static String createdQuestionBankName;

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Đăng nhập và điều hướng đến màn hình Tạo ngân hàng câu hỏi.
     * @return QuestionBankCreatePage object
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

    /**
     * Đăng nhập và điều hướng đến màn hình Quản lý câu hỏi (danh sách ngân hàng).
     * @return QuestionManagementPage object
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

    /**
     * Tạo ngân hàng câu hỏi thành công với dữ liệu hợp lệ.
     * - Upload PDF, chọn tài liệu, set số lượng câu hỏi = 20, chọn tất cả mức độ.
     * - Click Tạo câu hỏi, đợi sinh xong, lưu ngân hàng với tên unique.
     * @return Tên ngân hàng vừa tạo
     */
    private String createQuestionBankWithValidQuestions() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        // Upload tài liệu PDF
        questionBankCreatePage.clickAddDocumentButton();
        questionBankCreatePage.uploadFile(VALID_PDF_FILE_PATH);
        questionBankCreatePage.waitForUploadSuccessToast();

        // Đóng popup và chọn tài liệu vừa upload
        questionBankCreatePage.closeUploadPopup();
        questionBankCreatePage.selectUploadedDocumentCheckbox();

        // Cấu hình AI: 20 câu hỏi, tất cả mức độ
        questionBankCreatePage.setQuestionCount("20");
        questionBankCreatePage.clickAllLevelChip();

        // Kiểm tra nút Tạo câu hỏi được enable
        Assert.assertTrue(
                questionBankCreatePage.isGenerateQuestionButtonEnabled(),
                "Nút Tạo câu hỏi vẫn bị disable sau khi chọn tài liệu, số lượng và mức độ"
        );

        // Click nút Tạo câu hỏi
        questionBankCreatePage.clickGenerateQuestionButton();

        // Đợi có thể lưu ngân hàng
        Assert.assertTrue(
                questionBankCreatePage.waitUntilCanSaveQuestionBank(),
                "Sau khi tạo câu hỏi, chưa thấy câu hỏi hoặc nút Lưu thành ngân hàng"
        );

        // Click nút Lưu thành ngân hàng
        questionBankCreatePage.clickSaveBankButton();

        // Đợi popup đặt tên hiển thị
        Assert.assertTrue(
                questionBankCreatePage.waitUntilSaveNamePopupAppears(),
                "Popup đặt tên ngân hàng câu hỏi không hiển thị sau khi bấm Lưu thành ngân hàng"
        );

        // Đặt tên ngân hàng
        String bankName = "Ngan hang auto test " + System.currentTimeMillis();
        createdQuestionBankName = bankName;

        questionBankCreatePage.enterBankName(bankName);
        questionBankCreatePage.clickConfirmSaveBankButton();
        questionBankCreatePage.waitAfterConfirmSaveBank(bankName);

        // Quay lại màn hình Quản lý câu hỏi để kiểm tra
        SidebarComponent sidebar = new SidebarComponent(driver);
        sidebar.openQuestionManagement();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);
        questionManagementPage.reloadQuestionBankList();

        return bankName;
    }

    // =========================
    // TEST CASES
    // =========================

    /**
     * AT_QLCH_001: Điều hướng sang màn hình Tạo ngân hàng thành công.
     */
    @Test
    public void AT_QLCH_001_NavigateToCreateBankSuccessfully() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        // Kiểm tra các khu vực chính trên màn hình
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

    /**
     * AT_QLCH_002: Upload file PDF hợp lệ thành công.
     */
    @Test
    public void AT_QLCH_002_UploadValidPdfSuccessfully() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        questionBankCreatePage.clickAddDocumentButton();
        questionBankCreatePage.uploadFile(VALID_PDF_FILE_PATH);
        questionBankCreatePage.waitForUploadSuccessToast();
    }

    /**
     * AT_QLCH_003: Không cho phép upload file không đúng định dạng.
     */
    @Test
    public void AT_QLCH_003_DoNotAllowUploadInvalidFileType() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        String filePath = "C:\\Auto_VanDapAI\\src\\test\\resources\\Data Test\\CHUONG1.csv";

        questionBankCreatePage.clickAddDocumentButton();
        questionBankCreatePage.uploadFile(filePath);

        // Kiểm tra toast lỗi định dạng file
        Assert.assertTrue(
                questionBankCreatePage.isUploadErrorToastDisplayed("Định dạng file không hợp lệ"),
                "Không hiển thị thông báo lỗi định dạng file không hợp lệ"
        );

        // Kiểm tra danh sách tài liệu trống
        Assert.assertTrue(
                questionBankCreatePage.isEmptyDocumentMessageDisplayed(),
                "Danh sách tài liệu không còn ở trạng thái 'Chưa có tài liệu nào'"
        );
    }

    /**
     * AT_QLCH_004: Không cho phép lưu khi chưa có câu hỏi hợp lệ.
     */
    @Test
    public void AT_QLCH_004_DoNotAllowSaveWithoutValidQuestionData() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        // Click lưu ngay khi chưa có câu hỏi
        questionBankCreatePage.clickSaveBankButton();

        // Kiểm tra cảnh báo
        Assert.assertTrue(
                questionBankCreatePage.isWarningToastDisplayed("ít nhất 1 câu hỏi"),
                "Không hiển thị cảnh báo khi lưu trong trạng thái chưa có câu hỏi"
        );
    }

    /**
     * AT_QLCH_005: Lưu ngân hàng thành công sau khi có câu hỏi hợp lệ.
     */
    @Test
    public void AT_QLCH_005_SaveQuestionBankSuccessfullyAfterHavingValidQuestions() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        // Upload và tạo câu hỏi
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

        // Quay lại danh sách và kiểm tra
        SidebarComponent sidebar = new SidebarComponent(driver);
        sidebar.openQuestionManagement();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);

        Assert.assertTrue(
                questionManagementPage.isQuestionBankDisplayedInList(bankName),
                "Không tìm thấy ngân hàng câu hỏi vừa lưu trong danh sách: " + bankName
        );
    }

    /**
     * AT_QLCH_006: Không cho phép lưu khi danh sách câu hỏi rỗng sau khi xóa hết.
     */
    @Test
    public void AT_QLCH_006_DoNotAllowSaveWhenQuestionListIsEmptyAfterDeletingAllQuestions() {
        QuestionBankCreatePage questionBankCreatePage = goToCreateQuestionBankPage();

        // Upload và tạo câu hỏi
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

        // Xóa tất cả câu hỏi
        questionBankCreatePage.clickBulkDeleteQuestionButton();
        questionBankCreatePage.confirmDeleteQuestions();

        Assert.assertTrue(
                questionBankCreatePage.waitForDeleteSuccessToast(),
                "Không hiển thị thông báo xóa thành công"
        );

        // Thử lưu khi không còn câu hỏi
        questionBankCreatePage.clickSaveBankButton();

        Assert.assertTrue(
                questionBankCreatePage.isWarningToastDisplayed("ít nhất 1 câu hỏi"),
                "Không hiển thị cảnh báo khi lưu trong trạng thái danh sách câu hỏi rỗng"
        );
    }

    /**
     * AT_QLCH_007: Kiểm tra ngân hàng vừa tạo hiển thị trong danh sách sau khi reload.
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

    /**
     * AT_QLCH_008: Mở chi tiết ngân hàng và kiểm tra danh sách câu hỏi.
     */
    @Test(priority = 8)
    public void AT_QLCH_008_OpenCreatedQuestionBankDetailAndVerifyQuestionList() {
        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        // Kiểm tra ngân hàng hiển thị trong danh sách
        Assert.assertTrue(
                questionManagementPage.isQuestionBankDisplayedInList(bankName),
                "Không tìm thấy ngân hàng câu hỏi trước khi mở chi tiết: " + bankName
        );

        // Mở chi tiết ngân hàng
        questionManagementPage.openQuestionBankDetailByName(bankName);

        // Kiểm tra màn hình chi tiết
        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Màn hình chi tiết ngân hàng câu hỏi không hiển thị đúng thông tin: " + bankName
        );

        // Kiểm tra có ít nhất một câu hỏi
        Assert.assertTrue(
                questionBankDetailPage.hasAtLeastOneQuestionInQuestionBankDetail(),
                "Chi tiết ngân hàng câu hỏi không hiển thị danh sách câu hỏi hoặc không có câu hỏi nào"
        );
    }

    /**
     * AT_QLCH_009: Cập nhật nội dung câu hỏi thành công.
     */
    @Test(priority = 9)
    public void AT_QLCH_009_UpdateQuestionContentSuccessfully() {
        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        // Mở ngân hàng đầu tiên
        questionManagementPage.openFirstQuestionBankCard();

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Không mở được màn hình chi tiết ngân hàng câu hỏi vừa tạo: " + bankName
        );

        // Cập nhật nội dung câu hỏi đầu tiên
        String newQuestionContent = "Sự khác biệt cốt lõi giữa hệ thống OLTP (Online Transaction Processing) và OLAP (Online Analytical Processing) là gì?";

        questionBankDetailPage.clickEditFirstQuestionButton();
        questionBankDetailPage.updateFirstQuestionContent(newQuestionContent);
        questionBankDetailPage.clickSaveFirstQuestionButton();

        // Kiểm tra nội dung đã được cập nhật
        Assert.assertEquals(
                questionBankDetailPage.getFirstQuestionContent(),
                newQuestionContent,
                "Nội dung câu hỏi không được cập nhật đúng"
        );

        // Click nút Cập nhật vào ngân hàng
        questionBankDetailPage.clickUpdateQuestionBankButton();

        Assert.assertTrue(
                questionBankDetailPage.isUpdateQuestionBankSuccessToastDisplayed(),
                "Không hiển thị thông báo cập nhật ngân hàng thành công"
        );

        driver.navigate().refresh();
    }

    /**
     * AT_QLCH_010: Cập nhật mức độ hàng loạt cho nhiều câu hỏi thành công.
     */
    @Test(priority = 10)
    public void AT_QLCH_010_BulkUpdateQuestionLevelSuccessfully() {
        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        // Mở ngân hàng đầu tiên
        questionManagementPage.openFirstQuestionBankCard();

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Không mở được màn hình chi tiết ngân hàng: " + bankName
        );

        Assert.assertTrue(
                questionBankDetailPage.waitForQuestionListLoaded(),
                "Danh sách câu hỏi không load"
        );

        // Lấy số lượng câu hỏi Khó trước khi update
        int beforeHard = questionBankDetailPage.getHardQuestionCount();

        // Chọn 3 câu hỏi đầu, bulk change level thành Khó
        questionBankDetailPage.selectFirstThreeQuestions();
        questionBankDetailPage.clickBulkChangeLevelButton();
        questionBankDetailPage.selectHardLevelInBulkModal();
        questionBankDetailPage.clickConfirmBulkLevelButton();

        // Kiểm tra có câu hỏi được đánh dấu "Chưa lưu"
        Assert.assertTrue(
                questionBankDetailPage.isAnyQuestionMarkedAsUnsaved(),
                "Không có câu hỏi nào được đánh dấu 'Chưa lưu'"
        );

        // Cập nhật vào ngân hàng
        questionBankDetailPage.clickUpdateQuestionBankButton();

        Assert.assertTrue(
                questionBankDetailPage.isUpdateQuestionBankSuccessToastDisplayed(),
                "Không hiển thị toast cập nhật thành công"
        );

        // Kiểm tra số lượng câu hỏi Khó đã tăng
        int afterHard = questionBankDetailPage.getHardQuestionCount();

        Assert.assertTrue(
                afterHard >= beforeHard + 1,
                "Số lượng câu hỏi Khó không tăng sau bulk update. Trước: " + beforeHard + ", Sau: " + afterHard
        );
    }

    /**
     * AT_QLCH_011: Xóa hàng loạt câu hỏi thành công.
     */
    @Test(priority = 11)
    public void AT_QLCH_011_BulkDeleteQuestionsSuccessfully() {
        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        // Mở ngân hàng đầu tiên
        questionManagementPage.openFirstQuestionBankCard();

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Không mở được màn hình chi tiết ngân hàng câu hỏi vừa tạo: " + bankName
        );

        // Lấy số lượng câu hỏi trước khi xóa
        int beforeCount = questionBankDetailPage.getTotalQuestionCountFromAllTab();

        Assert.assertTrue(
                beforeCount >= 5,
                "Ngân hàng câu hỏi cần có ít nhất 5 câu hỏi để kiểm tra xóa hàng loạt. Số câu hiện tại: " + beforeCount
        );

        int deletedCount = 3;

        // Chọn 3 câu hỏi đầu và xóa
        questionBankDetailPage.selectFirstThreeQuestions();
        questionBankDetailPage.clickBulkDeleteQuestionButton();
        questionBankDetailPage.confirmBulkDeleteQuestionPopup();

        // Cập nhật vào ngân hàng
        questionBankDetailPage.clickUpdateQuestionBankButton();

        Assert.assertTrue(
                questionBankDetailPage.isUpdateQuestionBankSuccessToastDisplayed(),
                "Không hiển thị thông báo cập nhật ngân hàng thành công sau khi xóa hàng loạt câu hỏi"
        );

        // Kiểm tra số lượng câu hỏi đã giảm đúng số lượng
        Assert.assertTrue(
                questionBankDetailPage.isQuestionCountDecreasedBy(beforeCount, deletedCount),
                "Tổng số câu hỏi không giảm đúng " + deletedCount + " câu sau khi xóa hàng loạt"
        );
    }

    /**
     * AT_QLCH_012: Xóa ngân hàng câu hỏi thành công.
     */
    @Test(priority = 12)
    public void AT_QLCH_012_DeleteQuestionBankSuccessfully() {
        String bankName = createQuestionBankWithValidQuestions();

        QuestionManagementPage questionManagementPage = new QuestionManagementPage(driver);
        QuestionBankDetailPage questionBankDetailPage = new QuestionBankDetailPage(driver);

        // Mở ngân hàng đầu tiên
        questionManagementPage.openFirstQuestionBankCard();

        Assert.assertTrue(
                questionBankDetailPage.isQuestionBankDetailDisplayed(bankName),
                "Không mở được màn hình chi tiết ngân hàng câu hỏi vừa tạo: " + bankName
        );

        // Xóa ngân hàng
        questionBankDetailPage.clickDeleteQuestionBankButton();
        questionBankDetailPage.confirmDeleteQuestionBankPopup(bankName);

        // Quay lại danh sách và kiểm tra ngân hàng không còn hiển thị
        questionManagementPage.openQuestionBankListPage();
        questionManagementPage.selectSubjectByVisibleText(SUBJECT_NAME);
        questionManagementPage.reloadQuestionBankList();

        Assert.assertTrue(
                questionManagementPage.isQuestionBankNotDisplayedInList(bankName),
                "Ngân hàng câu hỏi vừa xóa vẫn còn hiển thị trong danh sách: " + bankName
        );
    }
}