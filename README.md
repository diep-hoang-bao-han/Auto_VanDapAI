
# 🎓 DUE-EXAM – AI Oral Examination System | Test Automation

![Java](https://img.shields.io/badge/Java-Automation-orange)
![Selenium](https://img.shields.io/badge/Selenium-WebDriver-43B02A?logo=selenium&logoColor=white)
![TestNG](https://img.shields.io/badge/TestNG-Testing_Framework-red)
![Maven](https://img.shields.io/badge/Maven-Build_Tool-blue)
![POM](https://img.shields.io/badge/Design-Page_Object_Model-purple)

## 📌 Project Overview

**DUE-EXAM** is an AI-powered oral examination management system designed to support educational assessment processes, including question bank management, exam creation, and exam session management.

This repository focuses on **Web Test Automation** using Java, Selenium WebDriver, and TestNG.

The automation framework follows the **Page Object Model (POM)** design pattern to improve code reusability, maintainability, and test organization.

**Role:** Software Tester – Test Automation  
**Project Type:** Web Application Testing  
**Testing Approach:** Functional Testing & Regression Testing

## 🛠️ Tech Stack

| Category | Technologies / Tools |
|---|---|
| Programming Language | Java |
| Automation Tool | Selenium WebDriver |
| Test Framework | TestNG |
| Design Pattern | Page Object Model (POM) |
| Build Tool | Maven |
| IDE | IntelliJ IDEA |
| Test Data | CSV, PDF |
| Documentation | Microsoft Excel |

## 🔍 Testing Scope

The automation testing focuses on three main modules:

- **Question Bank Management:** Create, update, and manage question banks and questions.
- **Exam Management:** Create, manage, and view examination details.
- **Exam Session Management:** Create, manage, and view examination sessions.

## ⚙️ Framework Highlights

- Implemented the **Page Object Model (POM)** design pattern.
- Organized automated tests by functional modules.
- Developed reusable Page Objects and UI components.
- Centralized test setup and configuration management.
- Used TestNG for test execution and assertions.
- Managed dependencies and builds using Maven.
- Organized external test data using CSV and PDF files.

## 📂 Project Resources

| Resource | Description |
|---|---|
| `src/test/java/com/vandapai/tests` | Automated test scripts |
| `src/test/java/com/vandapai/pages` | Page Object classes |
| `src/test/java/com/vandapai/base` | Base test setup |
| `src/test/resources` | Configuration and test data |
| `testng.xml` | TestNG suite configuration |
| `pom.xml` | Maven project configuration |
| `Testcases&Bugs` | Test case documentation and defect reports |

## ▶️ Test Execution

**Prerequisites:** Java JDK, Maven, and a compatible web browser.

1. Clone the repository.
2. Configure the application URL and test environment in `config.properties`.
3. Open the project in IntelliJ IDEA.
4. Execute the configured TestNG suite.

```bash
mvn clean test
```

> Test execution requires the target application and configured test environment to be available.

## 📊 Testing Documentation

The project includes supporting QA documentation:

- **Manual Test Cases:** Functional test scenarios and execution records.
- **Automation Test Cases:** Automated test scenarios and coverage documentation.
- **Bug Reports:** Defects identified during the testing process.

These documents are maintained in the `Testcases&Bugs` directory.

## 👩‍💻 Author

**Diệp Hoàng Bảo Hân**  
Software Tester | Manual & Automation Testing

---

⭐ This project demonstrates hands-on experience in Selenium test automation, Page Object Model implementation, test case development, and defect tracking.
