# 🎓 AI Oral Exam System (DUE-EXAM) - Automation Testing Framework

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Selenium](https://img.shields.io/badge/Selenium%20WebDriver-4.18.1-green.svg)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-red.svg)](https://testng.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-blue.svg)](https://maven.apache.org/)

Automated UI Testing Framework được xây dựng theo mô hình **Page Object Model (POM)** để kiểm thử tự động các luồng chức năng trọng yếu của hệ thống **Chấm thi Vấn đáp Trực tuyến Ứng dụng Trí tuệ Nhân tạo (DUE-EXAM)**.

---

## 📌 Giới thiệu dự án kiểm thử

Dự án tự động hóa quá trình xác thực và kiểm thử hồi quy (Regression Testing) trên nền tảng Web của hệ thống DUE-EXAM, tập trung vào các nghiệp vụ cốt lõi dành cho Giảng viên và Quản trị viên:

* **Xác thực & Phân quyền (Authentication)**: Đăng nhập, kiểm tra phiên làm việc và bảo mật tài khoản.
* **Quản lý Ngân hàng câu hỏi (Question Bank Management)**: Kiểm thử tạo, cập nhật, tải lên tài liệu học liệu và đồng bộ câu hỏi.
* **Quản lý Đề thi & Ca thi (Exam & Session Management)**: Tạo bộ đề, sinh mã đề tự động/thủ công, cấu hình phòng thi và phân bổ thí sinh.

Framework giúp rút ngắn thời gian thực thi các bộ kịch bản lặp lại từ **180 phút (thao tác thủ công) xuống còn ~38 phút (nhanh gấp ~4.7 lần)** với độ chính xác tuyệt đối.

---

## 🛠 Công nghệ & Thư viện sử dụng

* **Ngôn ngữ lập trình**: Java 17
* **Framework kiểm thử**: TestNG (v7.10.2)
* **Thư viện tự động hóa Web**: Selenium WebDriver (v4.18.1)
* **Quản lý Driver**: WebDriverManager (v5.8.0)
* **Build Tool & Dependency Management**: Apache Maven (Maven Surefire Plugin v3.2.5)
* **Thiết kế kiến trúc**: Page Object Model (POM)

---

## 📂 Cấu trúc thư mục (Project Structure)

```text
Auto_VanDapAI/
├── .gitignore
├── pom.xml                   # Cấu hình dependency và Maven Surefire Plugin
├── testng.xml                # Test Suite Runner điều phối các test cases
└── src/
    ├── main/java/            # Page Objects, BasePage, Helpers, Common Utilities
    └── test/java/
        └── tests/            # Test Classes triển khai kiểm thử
            ├── LoginTest.java       # Bộ test kiểm tra xác thực người dùng
            ├── QuestionTest.java    # Bộ test quản lý câu hỏi & học liệu
            └── ExamTest.java        # Bộ test tạo đề thi và cấu hình ca thi
