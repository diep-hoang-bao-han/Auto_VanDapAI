package com.vandapai.pages.question;

import com.vandapai.pages.common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class QuestionManagementPage extends BasePage {

    public QuestionManagementPage(WebDriver driver) {
        super(driver);
    }

    private final By subjectDropdown = By.id("subjectSelect");
    private final By createNewBankBtn = By.id("goDetailBtn");
    private final By bankListContainer = By.id("bankListContainer");

    public void selectSubjectByVisibleText(String subjectText) {
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(subjectDropdown));
        Select select = new Select(dropdown);

        String expected = normalizeText(subjectText);

        for (WebElement option : select.getOptions()) {
            String actualText = option.getText().trim();
            String actual = normalizeText(actualText);

            if (!actualText.isBlank()
                    && !actualText.contains("-- Chọn môn học --")
                    && (actual.equals(expected)
                    || actual.contains(expected)
                    || expected.contains(actual))) {

                select.selectByVisibleText(actualText);

                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(bankListContainer),
                        ExpectedConditions.urlContains("subject_id")
                ));

                sleep(800);
                return;
            }
        }

        System.out.println("Không tìm thấy môn học cần chọn: " + subjectText);
        System.out.println("Danh sách môn học hiện có:");
        for (WebElement option : select.getOptions()) {
            System.out.println("- [" + option.getText() + "]");
        }

        throw new RuntimeException("Không tìm thấy môn học: " + subjectText);
    }

    private String normalizeText(String text) {
        if (text == null) return "";

        return text
                .trim()
                .toLowerCase()
                .replace("đ", "d")
                .replace("Đ", "d")
                .replaceAll("\\s+", " ");
    }

    public void clickCreateNewBank() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement button = longWait.until(ExpectedConditions.presenceOfElementLocated(createNewBankBtn));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                button
        );

        longWait.until(ExpectedConditions.visibilityOf(button));

        String oldUrl = driver.getCurrentUrl();
        System.out.println("URL BEFORE CLICK CREATE BANK = " + oldUrl);

        try {
            longWait.until(ExpectedConditions.elementToBeClickable(button));
            button.click();
        } catch (Exception e) {
            System.out.println("NORMAL CLICK CREATE BANK FAILED = " + e.getMessage());
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(driver -> isOnCreateQuestionBankDetailPage());
        } catch (Exception ignored) {
        }

        if (!isOnCreateQuestionBankDetailPage()) {
            System.out.println("NORMAL CLICK HAS NO EFFECT -> TRY JS CLICK");

            try {
                ((JavascriptExecutor) driver).executeScript(
                        "document.getElementById('goDetailBtn').click();"
                );
            } catch (Exception e) {
                System.out.println("JS CLICK CREATE BANK FAILED = " + e.getMessage());
            }
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(driver -> isOnCreateQuestionBankDetailPage());
        } catch (Exception ignored) {
        }

        if (!isOnCreateQuestionBankDetailPage()) {
            String detailUrl;

            if (oldUrl.contains("?")) {
                detailUrl = oldUrl + "&mode=detail&view=generate";
            } else {
                detailUrl = oldUrl + "?mode=detail&view=generate";
            }

            System.out.println("CLICK STILL HAS NO EFFECT -> FORCE NAVIGATE TO = " + detailUrl);
            driver.get(detailUrl);
        }

        longWait.until(driver -> isOnCreateQuestionBankDetailPage());
    }

    private boolean isOnCreateQuestionBankDetailPage() {
        try {
            String currentUrl = driver.getCurrentUrl();
            String bodyText = driver.findElement(By.tagName("body")).getText();

            return currentUrl.contains("mode=detail")
                    || bodyText.contains("Chi tiết ngân hàng")
                    || bodyText.contains("Lưu thành ngân hàng");
        } catch (Exception e) {
            return false;
        }
    }

    public void reloadQuestionBankList() {
        driver.navigate().refresh();

        wait.until(ExpectedConditions.visibilityOfElementLocated(bankListContainer));

        sleep(1200);
    }

    public boolean isQuestionBankDisplayedInList(String bankName) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        return longWait.until(driver -> {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(bankListContainer));

                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("CHECK BANK LIST BODY = " + bodyText);
                System.out.println("EXPECTED BANK NAME = " + bankName);

                return bodyText.contains(bankName);
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void openQuestionBankDetailByName(String bankName) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement bankText = longWait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(normalize-space(.),'" + bankName + "')]")
        ));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                bankText
        );

        String oldUrl = driver.getCurrentUrl();
        System.out.println("URL BEFORE OPEN BANK DETAIL = " + oldUrl);
        System.out.println("OPEN BANK DETAIL BY NAME = " + bankName);

        try {
            /*
             * Click vào card cha gần nhất thay vì chỉ click text tên ngân hàng.
             * Vì text có thể không phải element được gắn onclick.
             */
            WebElement clickableCard = bankText.findElement(By.xpath(
                    "./ancestor::*[contains(@class,'card') " +
                            "or contains(@class,'bank') " +
                            "or contains(@class,'item') " +
                            "or contains(@class,'question')][1]"
            ));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                    clickableCard
            );

            try {
                longWait.until(ExpectedConditions.elementToBeClickable(clickableCard));
                clickableCard.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableCard);
            }

        } catch (Exception e) {
            /*
             * Fallback: nếu không tìm được card cha thì click trực tiếp text.
             */
            try {
                longWait.until(ExpectedConditions.elementToBeClickable(bankText));
                bankText.click();
            } catch (Exception ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bankText);
            }
        }

        longWait.until(driver -> {
            String currentUrl = driver.getCurrentUrl();
            String bodyText = driver.findElement(By.tagName("body")).getText();

            System.out.println("URL AFTER OPEN BANK DETAIL = " + currentUrl);

            return !currentUrl.equals(oldUrl)
                    || currentUrl.contains("detail")
                    || currentUrl.contains("bank")
                    || bodyText.contains("Chi tiết ngân hàng")
                    || bodyText.contains("Danh sách câu hỏi")
                    || bodyText.contains(bankName);
        });

        sleep(1500);
    }

    private final By firstQuestionBankCard = By.xpath("//*[@id='bankListContainer']/div/div[1]");
    public void openFirstQuestionBankCard() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement firstCard = shortWait.until(
                ExpectedConditions.presenceOfElementLocated(firstQuestionBankCard)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                firstCard
        );

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(firstCard));
            firstCard.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstCard);
        }

        sleep(1500);
    }

    public boolean isQuestionBankNotDisplayedInList(String bankName) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        return shortWait.until(driver -> {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(bankListContainer));

                String bodyText = driver.findElement(By.tagName("body")).getText();

                System.out.println("QUESTION BANK LIST AFTER DELETE = " + bodyText);
                System.out.println("DELETED BANK NAME = " + bankName);

                return !bodyText.contains(bankName);
            } catch (Exception e) {
                return false;
            }
        });
    }
    public void openQuestionBankListPage() {
        String currentUrl = driver.getCurrentUrl();
        String baseUrl;

        if (currentUrl.contains("/lecturer/")) {
            baseUrl = currentUrl.substring(0, currentUrl.indexOf("/lecturer/"));
        } else {
            baseUrl = "http://127.0.0.1:8000";
        }

        driver.get(baseUrl + "/lecturer/questions/");

        wait.until(driver -> {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();

                return bodyText.contains("Quản lý câu hỏi")
                        || bodyText.contains("Tạo ngân hàng mới")
                        || driver.findElements(By.id("subjectSelect")).size() > 0;
            } catch (Exception e) {
                return false;
            }
        });

        sleep(1000);
    }
}