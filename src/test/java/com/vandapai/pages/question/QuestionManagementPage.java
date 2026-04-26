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
        select.selectByVisibleText(subjectText);
        wait.until(ExpectedConditions.visibilityOfElementLocated(bankListContainer));
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
        WebElement bankElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(normalize-space(.),'" + bankName + "')]")
        ));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                bankElement
        );

        try {
            wait.until(ExpectedConditions.elementToBeClickable(bankElement));
            bankElement.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bankElement);
        }

        sleep(1500);
    }
}